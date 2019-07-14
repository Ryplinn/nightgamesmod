package nightgames.global;

import nightgames.actions.Action;
import nightgames.actions.Movement;
import nightgames.areas.Area;
import nightgames.areas.Cache;
import nightgames.areas.MapSchool;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Encounter;
import nightgames.gui.GUI;
import nightgames.modifier.Modifier;
import nightgames.skills.SkillPool;
import nightgames.status.Status;
import nightgames.status.Stsflag;
import nightgames.status.addiction.AddictionSymptom;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class Match {
    public final CountDownLatch matchComplete; // Counts down when match is over
    public static Set<Character> resting = new HashSet<>();
    public static Match match;
    protected int time;
    protected int dropOffTime;
    protected Map<String, Area> map;
    public List<Character> combatants;
    protected Map<Character, Integer> score;
    protected boolean pause;
    public Modifier condition;
    public MatchData matchData;

    public Match(Collection<Character> combatants, Modifier condition) {
        matchComplete = new CountDownLatch(1);
        this.combatants = new ArrayList<>();
        this.combatants.addAll(combatants);
        matchData = new MatchData();
        score = new HashMap<>();
        this.condition = condition;
        map = MapSchool.buildMap();
        for (Character combatant : combatants) {
            score.put(combatant, 0);
            combatant.adjustTraits();
            SkillPool.learnSkills(combatant);
            combatant.matchPrep(this);
        }
        time = 0;
        dropOffTime = 0;
        pause = false;
        Deque<Area> areaList = new ArrayDeque<>();
        areaList.add(map.get("Dorm"));
        areaList.add(map.get("Engineering"));
        areaList.add(map.get("Liberal Arts"));
        areaList.add(map.get("Dining"));
        areaList.add(map.get("Union"));
        areaList.add(map.get("Bridge"));
        areaList.add(map.get("Library"));
        areaList.add(map.get("Tunnel"));
        areaList.add(map.get("Workshop"));
        areaList.add(map.get("Pool"));
        combatants.forEach(combatant -> {
            if (combatant.has(Trait.immobile)) {
                combatant.place(map.get("Courtyard"));
            } else {
                combatant.place(areaList.pop());
            }
            combatant.getStamina().fill();
            combatant.getArousal().empty();
            combatant.getMojo().empty();
            combatant.getWillpower().fill();
            if (combatant.getPure(Attribute.science) > 0) {
                combatant.chargeBattery();
            }
            manageConditions(combatant);
        });

        match = this;
    }

    public static void startMatchGui(GUI gui) {
        gui.mntmQuitMatch.setEnabled(true);
        gui.showMap();
    }

    public void startMatch() {
        GameState.gameState.characterPool.getPlayer().getAddictions().forEach(a -> {
            Optional<Status> withEffect = a.startNight();
            withEffect.ifPresent(s -> GameState.gameState.characterPool.getPlayer().addNonCombat(s));
        });
        startMatchGui(GUI.gui);
    }

    public static HashSet<Character> getParticipants() {
        return new HashSet<>(GameState.gameState.characterPool.availableNpcs());
    }

    public static List<Character> getMatchParticipantsInAffectionOrder() {
        if (match == null) {
            return Collections.emptyList();
        }
        return GameState.gameState.characterPool.getInAffectionOrder(match.combatants.stream().filter(c -> !c.human()).collect(Collectors.toList()));
    }

    public static Match getMatch() {
        return match;
    }

    /**
     * Runs a match, cycling until the end time is reached.
     *
     * Every match cycle, each combatant gets a turn. Combatants act in initiative order, calculated at the start of each cycle.
     *
     * A turn consists of:
     * 1) Checking for an encounter
     * 2) Resolving the encounter if necessary
     * 3) Combat if necessary/select and perform the next move otherwise
     * @param endTime The number of match cycles.
     * @throws InterruptedException If an interrupt was received during a player prompt.
     */
    public void matchLoop(int endTime) throws InterruptedException {
        assert (combatants.size() > 0);
        while (time < endTime) {
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
                System.out.println("Starting round " + time);
            }
            getAreas().forEach(area -> area.setPinged(false));
            GUI.gui.refresh();

            // Sorting by initiative, descending. Raw speed stat breaks ties.
            combatants.forEach(Character::rollInitiative);
            combatants.sort(Comparator.comparingInt((Character c) -> c.lastInitRoll)
                            .thenComparingInt(c -> c.get(Attribute.speed)).reversed());
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_INITIATIVE)) {
                System.out.println("Initiative rolls:");
                combatants.forEach(c -> System.out.println(String.format("%s rolls %d" , c.getName(), c.lastInitRoll)));
            }

            for (Character combatant : combatants) {
                if (combatant.state == State.quit) {
                    break;
                }
                combatant.upkeep();
                manageConditions(combatant);

                // FIXME: supporting a character in an existing combat uses text from the character who's losing, not the player perspective.
                // Check for encounter before moving
                Optional<Encounter> maybeEncounter = combatant.location().encounter();

                // Resolve encounter
                Optional<Combat> maybeCombat = maybeEncounter.flatMap(Encounter::resolve);

                // Run combat if necessary, select and perform next move otherwise
                if (maybeCombat.isPresent()) {
                    Combat combat = maybeCombat.get();
                    if (!combat.shouldAutoresolve()) {
                        combat.loadCombatGUI(GUI.gui);
                    }
                    combat.runCombat();
                    if (!combat.shouldAutoresolve()) {
                        combat.removeCombatGUI(GUI.gui);
                    }
                } else {
                    Optional<Action> move;
                    do {
                        move = combatant.move();
                        move.ifPresent(combatant::doAction);
                    } while (move.map(Action::freeAction).orElse(false));
                    if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
                        System.out.println(
                                        combatant.getTrueName() + (combatant.is(Stsflag.disguised) ? "(Disguised)" : "")
                                                        + " is in " + combatant.location().name);
                    }
                }
            }

            if (meanLvl() > 3 && Random.random(10) + dropOffTime >= 12) {
                dropPackage();
                dropOffTime = 0;
            }
            if (Flag.checkFlag(Flag.challengeAccepted) && (time == 6 || time == 12 || time == 18 || time == 24)) {
                dropChallenge();
            }
            time++;
            dropOffTime++;
        }
    }

    public void end() {
        for (Character next : combatants) {
            next.finishMatch();
        }
        GUI.gui.clearText();
        GUI.gui.message("Tonight's match is over.");
        int cloth = 0;
        int creward = 0;
        Character player = null;
        Character winner = null;
        for (Character combatant : score.keySet()) {
            GUI.gui.message(combatant.getTrueName() + " scored " + score.get(combatant) + " victories.");
            combatant.modMoney(score.get(combatant) * combatant.prize());
            if (winner == null || score.get(combatant) >= score.get(winner)) {
                winner = combatant;
            }
            if (combatant.human()) {
                player = combatant;
            }
            for (Character other : combatants) {
                while (combatant.has(other.getTrophy())) {
                    combatant.consume(other.getTrophy(), 1, false);
                    combatant.modMoney(other.prize());
                    if (combatant.human()) {
                        cloth++;
                    }
                }
            }
            for (Challenge c : combatant.challenges) {
            	if(c.done){
                    combatant.money+=c.reward()+(c.reward()*3*combatant.getRank());
                    if(combatant.human()){
                        creward += c.reward()+(c.reward()*3*combatant.getRank());
                    }
                }
            }
            combatant.challenges.clear();
            combatant.state = State.ready;
            condition.undoItems(combatant);
            combatant.change();
        }
        GUI.gui.message("You made $" + score.get(player) * player.prize() + " for defeating opponents.");
        int bonus = score.get(player) * condition.bonus();
        winner.modMoney(bonus);
        if (bonus > 0) {
            GUI.gui.message("You earned an additional $" + bonus + " for accepting the handicap.");
        }
        condition.extraWinnings(player, score.get(player));
        if (winner == player) {
            GUI.gui.message("You also earned a bonus of $" + 5 * player.prize() + " for placing first.");
            Flag.flag(Flag.victory);
        }
        winner.modMoney(5 * winner.prize());
        GUI.gui.message("You traded in " + cloth + " sets of clothes for a total of $" + cloth * player.prize()
                        + ".<br/>");
        if (creward > 0) {
            GUI.gui.message("You also discover an envelope with $" + creward
                            + " slipped under the door to your room. Presumably it's payment for completed challenges.<br/>");
        }
        int maxaffection = 0;
        for (Character rival : combatants) {
            if (rival.getAffection(player) > maxaffection) {
                maxaffection = rival.getAffection(player);
            }
        }
        if (Flag.checkFlag(Flag.metLilly) && !Flag.checkFlag(Flag.challengeAccepted) && Random.random(10) >= 7) {
            GUI.gui.message(
                            "\nWhen you gather after the match to collect your reward money, you notice Jewel is holding a crumpled up piece of paper and ask about it. "
                                            + "<i>\"This? I found it lying on the ground during the match. It seems to be a worthless piece of trash, but I didn't want to litter.\"</i> Jewel's face is expressionless, "
                                            + "but there's a bitter edge to her words that makes you curious. You uncrumple the note and read it.<br/><br/>'Jewel always acts like the dominant, always-on-top tomboy, "
                                            + "but I bet she loves to be held down and fucked hard.'<br/><br/><i>\"I was considering finding whoever wrote the note and tying his penis in a knot,\"</i> Jewel says, still "
                                            + "impassive. <i>\"But I decided to just throw it out instead.\"</i> It's nice that she's learning to control her temper, but you're a little more concerned with the note. "
                                            + "It mentions Jewel by name and seems to be alluding to the games. You doubt one of the other girls wrote it. You should probably show it to Lilly.<br/><br/><i>\"Oh for fuck's "
                                            + "sake..\"</i> Lilly sighs, exasperated. <i>\"I thought we'd seen the last of these. I don't know who writes them, but they showed up last year too. I'll have to do a second "
                                            + "sweep of the grounds each night to make sure they're all picked up by morning. They have competitors' names on them, so we absolutely cannot let a normal student find "
                                            + "one.\"</i> She toys with a pigtail idly while looking annoyed. <i>\"For what it's worth, they do seem to pay well if you do what the note says that night. Do with them what "
                                            + "you will.\"</i><br/>");
            Flag.flag(Flag.challengeAccepted);
        }
        /*
         * if (maxaffection >= 15 && closest != null) { closest.afterParty(); } else { GameState.gui().message("You walk back to your dorm and get yourself cleaned up."); }
         */
        for (Character character : combatants) {
            if (character.getFlag("heelsTraining") >= 50 && !character.hasPure(Trait.proheels)) {
                if (character.human()) {
                    GUI.gui.message(
                                    "<br/>You've gotten comfortable at fighting in heels.<br/><b>Gained Trait: Heels Pro</b>");
                }
                character.add(Trait.proheels);
            }
            if (character.getFlag("heelsTraining") >= 100 && !character.hasPure(Trait.masterheels)) {
                if (character.human()) {
                    GUI.gui.message("<br/>You've mastered fighting in heels.<br/><b>Gained Trait: Heels Master</b>");
                }
                character.add(Trait.masterheels);
            }
        }
        GameState.gameState.characterPool.getPlayer().getAddictions().forEach(AddictionSymptom::endNight);
        matchComplete.countDown();
    }

    public int getHour() {
        return 10 + time / 12;
    }

    public String getTime() {
        int hour = getHour();
        if (hour > 12) {
            hour = hour % 12;
        }
        if (time % 12 < 2) {
            return hour + ":0" + time % 12 * 5;
        } else {
            return hour + ":" + time % 12 * 5;
        }
    }

    public Area gps(String name) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        return null;
    }

    public void score(Character character, int points) {
        score.put(character, score.get(character) + points);
    }

    public void manageConditions(Character player) {
        condition.handleOutfit(player);
        condition.handleItems(player);
        condition.handleStatus(player);
        condition.handleTurn(player, this);
        player.getAddictions().forEach(AddictionSymptom::refreshWithdrawal);
    }

    private int meanLvl() {
        return (int) combatants.stream().map(Character::getLevel).mapToInt(Integer::new).average().orElse(0);
    }

    public void dropPackage() {
        ArrayList<Area> areas = new ArrayList<Area>();
        areas.addAll(map.values());
        for (int i = 0; i < 10; i++) {
            Area target = areas.get(Random.random(areas.size()));
            if (!target.corridor() && !target.open() && target.env.size() < 5) {
                target.place(new Cache(meanLvl() + Random.random(11) - 4));
                GUI.gui.message("<br/><b>A new cache has been dropped off at " + target.name + "!</b>");
                break;
            }
        }
    }

    public void dropChallenge() {
        ArrayList<Area> areas = new ArrayList<Area>();
        areas.addAll(map.values());
        Area target = areas.get(Random.random(areas.size()));
        if (!target.open() && target.env.size() < 5) {
            target.place(new Challenge());
        }
    }

    public void quit() {
        Character human = GameState.gameState.characterPool.getPlayer();
        if (human.state == State.combat) {
            human.location().activeEncounter.getCombat().ifPresent(Combat::forfeit);
            human.location().endEncounter();
        }
        human.travel(new Area("Retirement", "", Movement.retire));
        human.state = State.quit;
    }

    public Collection<Movement> getResupplyAreas(Character ch) {
        return Arrays.asList(Movement.union, Movement.dorm);
    }

    public Collection<Area> getAreas() {
        return map.values();
    }
    
    public String genericRoomDescription() {
        return "room";
    }
    
    public MatchData getMatchData() {
        return matchData;
    }

    public Encounter buildEncounter(Area location) {
        return new Encounter(location);
    }
}
