package nightgames.combat;

import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.characters.body.Body;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.arms.ArmManager;
import nightgames.characters.body.mods.*;
import nightgames.characters.trait.Trait;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.global.*;
import nightgames.gui.GUI;
import nightgames.gui.GUIColor;
import nightgames.items.Item;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.items.clothing.ClothingTrait;
import nightgames.nskills.tags.SkillTag;
import nightgames.pet.Pet;
import nightgames.pet.PetCharacter;
import nightgames.skills.*;
import nightgames.skills.damage.DamageType;
import nightgames.stance.*;
import nightgames.status.*;
import nightgames.status.Stunned;
import nightgames.status.Compulsive.Situation;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

import java.util.*;
import java.util.stream.Collectors;

public class Combat extends Observable implements Cloneable {
    static final int NPC_TURN_LIMIT = 75;
    private static final double NPC_DRAW_ERROR_MARGIN = .15;

    private enum CombatPhase {
        START,
        PRETURN,
        SKILL_SELECTION,
        PET_ACTIONS,
        DETERMINE_SKILL_ORDER,
        P1_ACT_FIRST,
        P2_ACT_FIRST,
        P1_ACT_SECOND,
        P2_ACT_SECOND,
        UPKEEP,
        LEVEL_DRAIN,
        RESULTS_SCENE,
        FINISHED_SCENE,
        ENDED,
    }
    private transient GUI gui;
    public Character p1;
    public Character p2;
    private List<PetCharacter> otherCombatants;
    private transient List<PetCharacter> defeatedPets;
    private Map<String, CombatantData> combatantData;
    public Optional<Character> winner;
    private CombatPhase phase;
    private Skill.SkillUsage p1act;
    private Skill.SkillUsage p2act;
    public Area location;
    private String message;
    private Position stance;
    int timer;
    public Result state;
    private Map<String, String> images;
    boolean lastFailed = false;
    private CombatLog log;
    private boolean beingObserved;
    private int postCombatScenesSeen;
    private boolean wroteMessageSinceLastClear;
    private boolean cloned;
    private boolean finished;
    private static List<Skill> WORSHIP_SKILLS = Arrays.asList(new BreastWorship(), new CockWorship(), new FootWorship(),
                    new PussyWorship(), new Anilingus());
    public static final String TEMPT_WORSHIP_BONUS = "TEMPT_WORSHIP_BONUS";
    public volatile boolean combatMessageChanged = false;   // Signals to the GUI that it should update its view of the combat message.
    public volatile boolean clearText = false;  // Signals to the GUI that it should clear the main text window before updating its view of the combat message.
    private int delayCounter = 2;   // In unobserved combats, delays combat a few rounds to give a nearby player a chance to intervene.


    private String imagePath = "";

    public Combat(Character p1, Character p2, Area loc) {
        this(p1, p2, loc, new Neutral(p1.getType(), p2.getType()));
    }

    public Combat(Character p1, Character p2, Area loc, Position starting) {
        stance = starting;
        this.p1 = p1;
        combatantData = new HashMap<>();
        this.p2 = p2;
        p1.startBattle();
        p2.startBattle();
        // If we're reaching this deep into another object to get some data, it's probably in the wrong place.
        // TODO: Move ArmManager access to somewhere else
        getCombatantData(p1).setManager(Match.getMatch().getMatchData().getDataFor(p1).getArmManager());
        getCombatantData(p2).setManager(Match.getMatch().getMatchData().getDataFor(p2).getArmManager());
        location = loc;
        message = "";
        timer = 0;
        images = new HashMap<>();
        p1.state = State.combat;
        p2.state = State.combat;
        postCombatScenesSeen = 0;
        otherCombatants = new ArrayList<>();
        defeatedPets = new ArrayList<>();
        wroteMessageSinceLastClear = false;
        winner = Optional.empty();
        phase = CombatPhase.START;
        cloned = false;
        if (doExtendedLog()) {
            log = new CombatLog(this);
        }
        finished = false;
        gui = GUI.gui;
    }

    // FIXME: Something funny is going on with stances being silently reversed.

    public Combat(Character p1, Character p2, Area loc, Encounter.Initiation init) {
        this(p1, p2, loc);
        if (init == Encounter.Initiation.ambushStrip) {
            p2.undress(this);
            p1.emote(Emotion.dominant, 50);
            p2.emote(Emotion.nervous, 50);
        }
    }

    private void applyCombatStatuses(Character self, Character other) {
        if (other.human()) {
            write(self.challenge(other));
        }
        self.getAddictions().forEach(a -> a.startCombat(this, other));
        if (self.has(Trait.zealinspiring) && other.getAddiction(AddictionType.ZEAL, self).map(Addiction::isInWithdrawal).orElse(false)) {
            self.add(this, new DivineCharge(self.getType(), .3));
        }
        if (self.has(Trait.suave) && !other.hasDick()) {
            self.add(this, new SapphicSeduction(self.getType()));
        }

        if (self.has(Trait.footfetishist)) {
            applyFetish(self, other, "feet");
        } 
        if(self.has(Trait.breastobsessed) && other.hasBreasts()) {
            applyFetish(self, other, "breasts");
        }
        if(self.has(Trait.assaddict)) {
            applyFetish(self, other, "ass");
        }
        if(self.has(Trait.pussywhipped ) && other.hasPussy()) {
            applyFetish(self, other, "pussy");
        }
        if (self.has(Trait.cockcraver)&& other.hasDick()) {
            applyFetish(self, other, "cock");
        }
        if (self.has(Trait.Pseudopod) && self.has(Trait.slime)) {
            ArmManager manager = new ArmManager();
            manager.selectArms(self);
            getCombatantData(self).setManager(manager);
        }
    }

    private void applyFetish(Character self, Character other, String FetishType) {
        if ( !other.body.get(FetishType).isEmpty() && !self.body.getFetish(FetishType).isPresent()) {
            if (self.human()) {
                write(self, "As your first battle of the night begins, you can't help but think about " + other.nameOrPossessivePronoun() + " " + FetishType + " and how " + Body.partPronoun(FetishType) + " would feel on your skin.");
            } 
            self.add(this, new BodyFetish(self.getType(), null, FetishType, .25));
        }
    }

    private void startScene() throws InterruptedException {
        if (p1.mostlyNude() && !p2.mostlyNude()) {
            p1.emote(Emotion.nervous, 20);
        }
        if (p2.mostlyNude() && !p1.mostlyNude()) {
            p2.emote(Emotion.nervous, 20);
        }
        applyCombatStatuses(p1, p2);
        applyCombatStatuses(p2, p1);

        if (isBeingObserved()) {
            updateGUI();
            if (doExtendedLog()) {
                log.logHeader("\n");
            }
            this.promptNext(gui);
        }
    }

    public CombatantData getCombatantData(Character character) {
        if (!combatantData.containsKey(character.getTrueName())) {
            combatantData.put(character.getTrueName(), new CombatantData());
        }
        return combatantData.get(character.getTrueName());
    }

    private boolean checkBottleCollection(Character victor, Character loser, PartMod mod) {
        return victor.has(Item.EmptyBottle, 1) && loser.body.get("pussy")
                                                            .stream()
                                                            .anyMatch(part -> part.moddedPartCountsAs(loser, mod));
    }

    private void doVictory(Character victor, Character loser) {
        if (loser.hasDick() && victor.has(Trait.succubus)) {
            victor.gain(Item.semen, 3);
            if (loser.human()) {
                write(victor, "<br/><b>As she leaves, you see all your scattered semen ooze out and gather into a orb in "
                                + victor.nameOrPossessivePronoun() + " hands. "
                                + "She casually drops your seed in some empty vials that appeared out of nowhere</b>");
            } else if (victor.human()) {
                write(victor, "<br/><b>" + loser.nameOrPossessivePronoun()
                                + " scattered semen lazily oozes into a few magically conjured flasks. "
                                + "To speed up the process, you milk " + loser.possessiveAdjective()
                                + " out of the last drops " + loser.subject()
                                + " had to offer. Yum, you just got some leftovers.</b>");
            }
        } else if (loser.hasDick() && (victor.human() || victor.has(Trait.madscientist))
                        && victor.has(Item.EmptyBottle, 1)) {
            // for now only the player and mara collects semen
            write(victor, Formatter.format(
                            "<br/><b>{self:SUBJECT-ACTION:manage|manages} to collect some of {other:name-possessive} scattered semen in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.semen, 1);
        }
        if (checkBottleCollection(victor, loser, DivineMod.INSTANCE)) {
            write(victor, Formatter.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:name-possessive} divine pussy juices in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.HolyWater, 1);
        }
        if (checkBottleCollection(victor, loser, DemonicMod.INSTANCE)) {
            write(victor, Formatter.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:name-possessive} demonic pussy juices in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.ExtremeAphrodisiac, 1);
        }
        if (checkBottleCollection(victor, loser, PlantMod.INSTANCE)) {
            write(victor, Formatter.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:possessive} nectar in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.nectar, 3);
        }
        if (checkBottleCollection(victor, loser, CyberneticMod.INSTANCE)) {
            write(victor, Formatter.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:possessive} artificial lubricant in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.LubricatingOils, 1);
        }
        if (checkBottleCollection(victor, loser, ArcaneMod.INSTANCE)) {
            write(victor, Formatter.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of the floating mana wisps ejected from {other:possessive} orgasm in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.RawAether, 1);
        }
        if (checkBottleCollection(victor, loser, FeralMod.INSTANCE)) {
            write(victor, Formatter.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:possessive} musky juices in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.FeralMusk, 1);
        }
        if (checkBottleCollection(victor, loser, GooeyMod.INSTANCE)) {
            write(victor, Formatter.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:possessive} goo in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.BioGel, 1);
        }
        if (checkBottleCollection(victor, loser, FieryMod.INSTANCE)) {
            write(victor, Formatter.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:possessive} excitement in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.MoltenDrippings, 1);
        }
        if (loser.human() && loser.getWillpower().max() < loser.getMaxWillpowerPossible()) {
            write("<br/>Ashamed at your loss, you resolve to win next time.");
            write("<br/><b>Gained 1 Willpower</b>.");
            loser.getWillpower()
                 .gain(1);
        }
        victor.getWillpower()
              .fill();
        loser.getWillpower()
             .fill();

        if (Flag.checkFlag(Flag.FTC) && loser.has(Item.Flag)) {
            write(victor, Formatter.format(
                            "<br/><b>{self:SUBJECT-ACTION:take|takes} the " + "Flag from {other:subject}!</b>", victor,
                            loser));
            loser.remove(Item.Flag);
            victor.gain(Item.Flag);
        }
    }

    private void draw() {
        state = eval();
        p1.evalChallenges(this, null);
        p2.evalChallenges(this, null);
        p2.draw(this, state);
    }

    private void victory(Character won) {
        state = eval();
        p1.evalChallenges(this, won);
        p2.evalChallenges(this, won);
        won.victory(this, state);
        doVictory(won, getOpponent(won));
    }

    private Optional<Character> checkLosses() {
        if (!isBeingObserved()) {
            // Check for NPC vs NPC win
            double fitness1 = p1.getFitness(this);
            double fitness2 = p2.getFitness(this);
            double diff = Math.abs(fitness1 / fitness2 - 1.0);
            if (diff > NPC_DRAW_ERROR_MARGIN) {
                return Optional.of(fitness1 > fitness2 ? p1 : p2);
            } else if (timer > NPC_TURN_LIMIT) {
                return Optional.of(NPC.noneCharacter());
            }
        }

        boolean p1Lost = p1.checkLoss(this);
        boolean p2Lost = p2.checkLoss(this);

        if (p1Lost && p2Lost) {
            return Optional.of(NPC.noneCharacter());
        } else if (p1Lost) {
            return Optional.of(p2);
        } else if (p2Lost) {
            return Optional.of(p1);
        }
        return Optional.empty();
    }

    private void checkForCombatComment() {
        Character other;
        if (p1.human()) {
            other = p2;
        } else if (p2.human()) {
            other = p1;
        } else {
            other = Random.random(2) == 0 ? p1 : p2;
        }
        if (other instanceof NPC) {
            NPC commenter = (NPC) other;
            Optional<String> comment = commenter.getComment(this);
            comment.ifPresent(s -> write(commenter,
                            "<i>\"" + Formatter.format(s, commenter, GameState.getGameState().characterPool.getPlayer())
                                            + "\"</i>"));
        }
    }

    private void doPreturnUpkeep() {
        timer += 1;
        write(describe());
        if (!shouldAutoresolve() && !Flag.checkFlag(Flag.noimage)) {
            gui.clearImage();
            if (!imagePath.isEmpty()) {
                gui.displayImage(imagePath, images.get(imagePath));
            }
        }
        p1.preturnUpkeep();
        p2.preturnUpkeep();
        p1act = null;
        p2act = null;
        if (Random.random(3) == 0 && !shouldAutoresolve()) {
            checkForCombatComment();
        }
    }

    private void doEndOfTurnUpkeep() {
        p1.eot(this, p2);
        p2.eot(this, p1);
        otherCombatants.removeAll(defeatedPets);
        defeatedPets.clear();
        getStance().decay(this);
        getStance().checkOngoing(this);
        // iterate through all the pets here so we don't get concurrent modification issues
        List<PetCharacter> pets = new ArrayList<>(otherCombatants);
        pets.forEach(other -> {
            if (otherCombatants.contains(other)) {
                other.eot(this, getOpponent(other));
            }
        });  
        checkStamina(p1);
        checkStamina(p2);
        pets.forEach(other -> {
            if (otherCombatants.contains(other)) {
                checkStamina(other);
            }
        });
        doStanceTick(p1);
        doStanceTick(p2);

        List<Character> team1 = new ArrayList<>(getPetsFor(p1));
        team1.add(p1);
        List<Character> team2 = new ArrayList<>(getPetsFor(p2));
        team2.add(p2);
        team1.forEach(self -> doAuraTick(self, team2));
        team2.forEach(self -> doAuraTick(self, team1));

        combatantData.values().forEach(data -> data.tick(this));

        p1.regen(this);
        p2.regen(this);
    }

    private void doAuraTick(Character character, List<Character> opponents) {
        if (character.has(Trait.overwhelmingPresence)) {
            write(character, Formatter.format("{self:NAME-POSSESSIVE} overwhelming presence mentally exhausts {self:possessive} opponents.", character, character));
            opponents.forEach(opponent -> opponent.weaken(this, opponent.getStamina().max() / 10));
        }
        String beguilingbreastCompletedFlag = Trait.beguilingbreasts.name() + "Completed";
        if (character.has(Trait.beguilingbreasts) && character.body.getLargestBreasts().getSize() >= BreastsPart.dd.getSize() && !getCombatantData(character).getBooleanFlag(beguilingbreastCompletedFlag)
                        && character.outfit.slotOpen(ClothingSlot.top)) {
            Character mainOpponent = getOpponent(character);
            write(character, Formatter.format("The instant {self:subject-action:lay|lays} {self:possessive} eyes on {other:name-possessive} bare breasts, {self:possessive} consciousness flies out of {self:possessive} mind. " +
                            (character.canAct() ? "{other:SUBJECT-ACTION:giggle|giggles} a bit and cups her stupendous tits and gives them a little squeeze to which {self:subject} can only moan." : ""), 
                            character, mainOpponent));
            opponents.forEach(opponent -> opponent.add(this, new Trance(opponent.getType(), 50)));
            getCombatantData(character).setBooleanFlag(beguilingbreastCompletedFlag, true);
        }

        Character mainOpponent = getOpponent(character);
        String buttslutCompletedFlag = Trait.buttslut.name() + "Completed";
        if (character.has(Trait.buttslut) && ((mainOpponent.hasDick() && mainOpponent.crotchAvailable() && mainOpponent.getArousal().percent() > 20) || mainOpponent.has(Trait.strapped)) && !getCombatantData(character).getBooleanFlag(buttslutCompletedFlag)) {
            write(character, Formatter.format("<b>Seeing the thick phallus in front of {self:reflective}, {self:subject} can't "
                            + "but help offer up {self:possessive} ass in hopes that {other:subject} will fill {self:possessive} rear door.</b>", character, mainOpponent));
            for (int i = 0; i < 5; i++) {
                Clothing article = character.strip(ClothingSlot.bottom, this);
                if (article == null) {
                    break;
                } else {
                    write(character, Formatter
                                    .format("{self:SUBJECT-ACTION:strip|strips} off {self:possessive} %s", character, mainOpponent, article.getName()));
                }
            }
            getCombatantData(character).setBooleanFlag(buttslutCompletedFlag, true);
            setStance(new Behind(mainOpponent.getType(), character.getType()));
        }

        if (character.has(Trait.footfetishist)) {
            fetishDisadvantageAura(character, opponents, "feet", ClothingSlot.feet);
        }
        if (character.has(Trait.breastobsessed)) {
            fetishDisadvantageAura(character, opponents, "breasts", ClothingSlot.top);
        }
        if(character.has(Trait.assaddict)) {
            fetishDisadvantageAura(character, opponents, "ass", ClothingSlot.bottom);
        }
        if(character.has(Trait.pussywhipped ) )  {
            fetishDisadvantageAura(character, opponents, "pussy", ClothingSlot.bottom);
        }
        if(character.has(Trait.cockcraver)) {
            fetishDisadvantageAura(character, opponents, "cock", ClothingSlot.bottom);
        }
        
        opponents.forEach(opponent -> checkIndividualAuraEffects(character, opponent));
    }
    
    
    private void fetishDisadvantageAura(Character character, List<Character> opponents, String fetishType, ClothingSlot clothingType) {
       
        float ifPartNotNull;
       
        
        if(fetishType.equals("breasts") && opponents.get(0).hasBreasts()){
            ifPartNotNull = 1;
        } else if(fetishType.equals("pussy") && opponents.get(0).hasPussy()){
            ifPartNotNull = 1;
        } else if(fetishType.equals("cock") && opponents.get(0).hasDick()){
            ifPartNotNull = 1;
        } else if(fetishType.equals("ass")){
            ifPartNotNull = 1;
        } else if(fetishType.equals("feet")){
            ifPartNotNull = 1;
        } else{
            ifPartNotNull = 0;
        }      
        
        if(ifPartNotNull == 1)
        {
            Optional<Character> otherWithAura = opponents.stream().filter(other -> !other.body.get(fetishType).isEmpty()).findFirst();
            Clothing clothes = otherWithAura.get().getOutfit().getTopOfSlot(clothingType);
            boolean seeFetish = clothes == null || clothes.getLayer() <= 1 || otherWithAura.get().getOutfit().getExposure() >= .5;
            String partDescrip;

            switch (fetishType) {
                case "breasts":
                    partDescrip = otherWithAura.get().body.getRandomBreasts().describe(otherWithAura.get());
                    break;
                case "ass":
                    partDescrip = otherWithAura.get().body.getRandomAss().describe(otherWithAura.get());
                    break;
                case "pussy":
                    partDescrip = otherWithAura.get().body.getRandomPussy().describe(otherWithAura.get());
                    break;
                case "cock":
                    partDescrip = otherWithAura.get().body.getRandomCock().describe(otherWithAura.get());
                    break;
                default:
                    partDescrip = fetishType;
                    break;
            }
        
            if (seeFetish && Random.random(5) == 0) {
                if (character.human()) {
                    write(character, "You can't help thinking about " + otherWithAura.get().nameOrPossessivePronoun() + " " + partDescrip + ".");
                }
                character.add(this, new BodyFetish(character.getType(), null, fetishType, .05));
            }
        }
        
       
    
    }
    
    private void checkIndividualAuraEffects(Character self, Character other) {
        if (self.has(Trait.magicEyeEnthrall) && other.getArousal().percent() >= 50 && getStance().facing(other, self)
                        && Random.random(20) == 0) {
            write(self,
                            Formatter.format("<br/>{other:NAME-POSSESSIVE} eyes start glowing and captures both {self:name-possessive} gaze and consciousness.",
                                            other, self));
            other.add(this, new Enthralled(other.getType(), self.getType(), 2));
        }
        if (self.has(Trait.magicEyeTrance) && other.getArousal().percent() >= 50 && getStance().facing(other, self)
                        && Random.random(10) == 0) {
            write(self,
                            Formatter.format("<br/>{other:NAME-POSSESSIVE} eyes start glowing and send {self:subject} straight into a trance.",
                                            other, self));
            other.add(this, new Trance(other.getType()));
        }

        if (self.has(Trait.magicEyeFrenzy) && other.getArousal().percent() >= 50 && getStance().facing(other, self)
                        && Random.random(10) == 0) {
            write(self,
                            Formatter.format("<br/>{other:NAME-POSSESSIVE} eyes start glowing and send {self:subject} into a frenzy.",
                                            other, self));
            other.add(this, new Frenzied(other.getType(), 3));
        }

        if (self.has(Trait.magicEyeArousal) && other.getArousal().percent() >= 50 && getStance().facing(other, self)
                        && Random.random(5) == 0) {
            write(self,
                            Formatter.format("<br/>{other:NAME-POSSESSIVE} eyes start glowing and {self:subject-action:feel|feels} a strong pleasure wherever {other:possessive} gaze lands. {self:SUBJECT-ACTION:are|is} literally being raped by {other:name-possessive} eyes!",
                                            other, self));
            other.temptNoSkillNoSource(this, self, self.get(Attribute.seduction) / 2);
        }

        if (getStance().facing(self, other) && other.breastsAvailable() && !self.has(Trait.temptingtits) && other.has(Trait.temptingtits)) {
            write(self, Formatter.format("{self:SUBJECT-ACTION:can't avert|can't avert} {self:possessive} eyes from {other:NAME-POSSESSIVE} perfectly shaped tits sitting in front of {self:possessive} eyes.",
                                            self, other));
            self.temptNoSkill(this, other, other.body.getRandomBreasts(), 10 + Math.max(0, other.get(Attribute.seduction) / 3 - 7));
        } else if (getOpponent(self).has(Trait.temptingtits) && getStance().behind(other)) {
            write(self, Formatter.format("{self:SUBJECT-ACTION:feel|feels} a heat in {self:possessive} groin as {other:name-possessive} enticing tits pressing against {self:possessive} back.",
                            self, other));
            double selfTopExposure = self.outfit.getExposure(ClothingSlot.top);
            double otherTopExposure = other.outfit.getExposure(ClothingSlot.top);
            double temptDamage = 20 + Math.max(0, other.get(Attribute.seduction) / 2 - 12);
            temptDamage = temptDamage * Math.min(1, selfTopExposure + .5) * Math.min(1, otherTopExposure + .5);
            self.temptNoSkill(this, other, other.body.getRandomBreasts(), (int) temptDamage);
        }

        if (self.has(Trait.enchantingVoice)) {
            int voiceCount = getCombatantData(self).getIntegerFlag("enchantingvoice-count");
            if (voiceCount >= 1) {
                if (!self.human()) {
                    write(self,
                                    Formatter.format("{other:SUBJECT} winks at you and verbalizes a few choice words that pass straight through your mental barriers.",
                                                    other, self));
                } else {
                    write(self,
                                    Formatter.format("Sensing a moment of distraction, you use the power in your voice to force {self:subject} to your will.",
                                                    other, self));
                }
                (new Command()).resolve(this, self, other);
                int cooldown = Math.max(1, 6 - (self.getLevel() - other.getLevel() / 5));
                getCombatantData(self).setIntegerFlag("enchantingvoice-count", -cooldown);
            } else {
                getCombatantData(self).setIntegerFlag("enchantingvoice-count", voiceCount + 1);
            }
        }

        getCombatantData(self).getManager().act(this, self, other);

        if (self.has(Trait.mindcontroller)) {
            Collection<Clothing> infra = self.outfit.getArticlesWithTrait(ClothingTrait.infrasound);
            float magnitude = infra.size() * (Addiction.LOW_INCREASE / 6);
            if (magnitude > 0) {
                other.addict(this, AddictionType.MIND_CONTROL, self, magnitude);
                if (Random.random(3) == 0) {
                    Addiction addiction = other.getAddiction(AddictionType.MIND_CONTROL, self).orElse(null);
                    Clothing source = (Clothing) infra.toArray()[0];
                    boolean knows = (addiction != null && addiction.atLeast(Addiction.Severity.MED)) || other.get(Attribute.cunning) >= 30
                                    || other.get(Attribute.science) >= 10;
                    String msg;
                    if (other.human()) {
                        msg = "<i>You hear a soft buzzing, just at the edge of your hearing. ";
                        if (knows) {
                            msg += Formatter.format("Although you can't understand it, the way it draws your"
                                            + " attention to {self:name-possessive} %s must mean it's"
                                            + " influencing you somehow!</i>", self, other, source.getName());
                        } else {
                            msg += "It's probably nothing, though.</i>";
                        }
                    } else {
                        msg = Formatter.format("You see that {other:subject-action:is} distracted from {self:possessive} %s", self, other, source.getName());
                    }
                    write(other, msg);
                }
            }
        }
    }

    private static final List<CombatPhase> SKIPPABLE_PHASES = 
                    Arrays.asList(
                    CombatPhase.PET_ACTIONS,
                    CombatPhase.P1_ACT_FIRST,
                    CombatPhase.P1_ACT_SECOND,
                    CombatPhase.P2_ACT_FIRST,
                    CombatPhase.P2_ACT_SECOND);

    private static final List<CombatPhase> FAST_COMBAT_SKIPPABLE_PHASES = 
                    Arrays.asList(
                    CombatPhase.PET_ACTIONS,
                    CombatPhase.P1_ACT_FIRST,
                    CombatPhase.P1_ACT_SECOND,
                    CombatPhase.P2_ACT_FIRST,
                    CombatPhase.P2_ACT_SECOND,
                    CombatPhase.UPKEEP,
                    CombatPhase.LEVEL_DRAIN);

    private CombatPhase levelDrainPostCombat() {
        CombatPhase nextPhase = CombatPhase.RESULTS_SCENE;
        if (p1.has(Trait.leveldrainer) ^ p2.has(Trait.leveldrainer) && !p1.has(Trait.strapped) && !p2.has(Trait.strapped)) {
            Character drainer = p1.has(Trait.leveldrainer) ? p1 : p2;
            Character drained = p1.has(Trait.leveldrainer) ? p2 : p1;
            if (drainer.equals(winner.orElse(null)) && !getCombatantData(drainer).getBooleanFlag("has_drained")) {
                // Drainer needs to get on top after winning.
                if (!getStance().havingSex(this, drainer) || !getStance().dom(drainer)) {
                    Position mountStance = new Mount(drainer.getType(), drained.getType());
                    Optional<Position> drainedDick = mountStance.insert(this, drained, drainer);
                    Optional<Position> drainerDick = mountStance.insert(this, drainer, drainer);
                    if (drainedDick.isPresent()) {
                        write(drainer, Formatter.format("With {other:name-do} defeated and unable to fight back, {self:subject-action:climb|climbs} "
                                        + "on top of {other:direct-object} and {self:action:insert} {other:possessive} cock into {self:reflective}.", drainer, drained));
                        setStance(drainedDick.get());
                    } else if (drainerDick.isPresent()) {
                        write(drainer, Formatter.format("With {other:name-do} defeated and unable to fight back, {self:subject-action:climb|climbs} "
                                        + "on top of {other:direct-object} and {self:action:insert} {self:reflective} into {other:possessive} soaking vagina.", drainer, drained));
                        setStance(drainerDick.get());
                    } else if (drainer.hasPussy() && drained.hasPussy()) {
                        write(drainer, Formatter.format("With {other:name-do} defeated and unable to fight back, {self:subject-action:climb|climbs} "
                                        + "on top of {other:direct-object} and {self:action:press} {self:possessive} wet snatch on top of {other:poss-pronoun}.", drainer, drained));
                        setStance(new TribadismStance(drainer.getType(), drained.getType()));
                    } else {
                        write(drainer, Formatter.format("With {other:name-do} defeated and unable to fight back, {self:subject-action:climb|climbs} "
                                        + "on top of {other:direct-object}. However, {self:pronoun} could not figure a "
                                        + "way to drain {other:possessive} levels.", drainer, drained));
                        return CombatPhase.RESULTS_SCENE;
                    }
                } else if (phase == CombatPhase.LEVEL_DRAIN) {
                    if (getCombatantData(drainer).getIntegerFlag("level_drain_thrusts") < 10) {
                        Skill thrustSkill = getStance().en == Stance.trib ? new PussyGrind() :
                                        Random.pickRandomGuaranteed(Arrays.asList(new Thrust(), new Grind(), new Piston()));
                        thrustSkill.resolve(this, drainer, drained);
                        write("<br/>");
                        getCombatantData(drainer).increaseIntegerFlag("level_drain_thrusts", 1);
                    } else {
                        drained.doOrgasm(this, drainer,
                                        Random.pickRandom(getStance().getPartsFor(this, drained, drainer)).orElse(drained.body.getRandomGenital()),
                                        Random.pickRandom(getStance().getPartsFor(this, drainer, drained)).orElse(drainer.body.getRandomGenital()));
                        getCombatantData(drainer).setBooleanFlag("has_drained", true);
                    }
                } else {
                    write(drainer, Formatter.format("With {other:name-do} defeated, {self:subject-action:don't stop|doesn't stop} {self:possessive} greedy hips, "
                                        + "{self:pronoun-action:are|is} determined to extract all the power {self:pronoun} can get!", drainer, drained));
                }
                if (!getCombatantData(drainer).getBooleanFlag("has_drained")) {
                    nextPhase = CombatPhase.LEVEL_DRAIN;
                }
            }
        }
        return nextPhase;
    }

    private void clearMessage() {
        wroteMessageSinceLastClear = false;
        message = "";
        clearText = true;
    }

    /**
     * Invokes character skill choice.
     * @return true (should pause) if the character picking skills is human-controlled.
     */
    private boolean pickSkills() throws InterruptedException {
        if (p1act == null) {
            return p1.chooseSkill(this);
        } else if (p2act == null) {
            return p2.chooseSkill(this);
        } else {
            phase = CombatPhase.PET_ACTIONS;
            return false;
        }
    }

    private String describe() {
        if (!p1.human() && !p2.human() && beingObserved) {
            return String.format("<font color=%s>", GUIColor.COMBAT_OBSERVE_P1_DESC.rgbHTML())
                            + p1.describe(GameState.getGameState().characterPool.getPlayer().get(Attribute.perception), this)
                            + String.format("</font><br/><br/><font color=%s>", GUIColor.COMBAT_OBSERVE_P2_DESC.rgbHTML())
                            + p2.describe(GameState.getGameState().characterPool.getPlayer().get(Attribute.perception), this)
                            + String.format("</font><br/><br/><font color=%s><b>", GUIColor.COMBAT_OBSERVE_STANCE_DESC.rgbHTML())
                            + Formatter.capitalizeFirstLetter(getStance().describe(this)) + "</b></font>";
        } else if (beingObserved) {
            Player player;
            NPC other;
            if (p1.human()) {
                player = (Player) p1;
                other = (NPC) p2;
            } else {
                player = (Player) p2;
                other = (NPC) p1;
            }
            if (player.is(Stsflag.blinded)) {
                return "<b>You are blinded, and cannot see what " + other.getTrueName() + " is doing!</b><br/><br/>" + Formatter.capitalizeFirstLetter(getStance().describe(this)) + "<br/><br/>"
                                + player.describe(other.get(Attribute.perception), this) + "<br/><br/>";
            } else {
                return other.describe(player.get(Attribute.perception), this) + "<br/><br/>" + Formatter.capitalizeFirstLetter(getStance().describe(this)) + "<br/><br/>"
                                + player.describe(other.get(Attribute.perception), this) + "<br/><br/>";
            }
        } else {
            return "";
        }
    }

    private Result eval() {
        if (getStance().getBottom().human() && getStance().inserted(getStance().getTop()) && getStance().en == Stance.anal) {
            return Result.anal;
        } else if (getStance().inserted()) {
            return Result.intercourse;
        } else {
            return Result.normal;
        }
    }

    public Optional<Skill> getRandomWorshipSkill(Character self, Character other) {
        List<Skill> avail = new ArrayList<>(WORSHIP_SKILLS);
        if (other.has(Trait.piety)) {
            avail.add(new ConcedePosition());
        }
        Collections.shuffle(avail);
        while (!avail.isEmpty()) {
            Skill skill = avail.remove(avail.size() - 1);
            if (Skill.skillIsUsable(this, skill, self, other)) {
                write(other, Formatter.format(
                                "<b>{other:NAME-POSSESSIVE} divine aura forces {self:subject} to forget what {self:pronoun} {self:action:were|was} doing and crawl to {other:direct-object} on {self:possessive} knees.</b>",
                                self, other));
                return Optional.of(skill);
            }
        }
        return Optional.empty();
    }

    private boolean rollWorship(Character self, Character other) {
        if ((other.has(Trait.objectOfWorship) || self.is(Stsflag.lovestruck)) && (other.breastsAvailable() || other.crotchAvailable())) {
            double chance = Math.min(20, Math.max(5, other.get(Attribute.divinity) + 10 - self.getLevel()));
            if (other.has(Trait.revered)) {
                chance += 10;
            }
            chance += getCombatantData(self).getDoubleFlag(TEMPT_WORSHIP_BONUS);
            if (Random.random(100) < chance) {
                getCombatantData(self).setDoubleFlag(TEMPT_WORSHIP_BONUS, 0);
                return true;
            }            
        }
        return false;
    }

    private boolean rollAssWorship(Character self, Character opponent) {
        int chance = 0;
        if (opponent.has(Trait.temptingass)) {
            chance += Math.max(0, Math.min(15, opponent.get(Attribute.seduction) - self.get(Attribute.seduction)));
            if (self.is(Stsflag.feral))
                chance += 10;
            if (self.is(Stsflag.charmed) || opponent.is(Stsflag.alluring))
                chance += 5;
            if (self.has(Trait.assmaster) || self.has(Trait.analFanatic))
                chance += 5;
            Optional<BodyFetish> fetish = self.body.getFetish("ass");
            if (fetish.isPresent() && opponent.has(Trait.bewitchingbottom)) {
                chance += 20 * fetish.get().magnitude;
            }
        }
        return Random.random(100) < chance;
    }

    private Optional<Skill> checkWorship(Character self, Character other, Skill def) {
        if (rollWorship(self, other)) {
            return getRandomWorshipSkill(self, other);
        }
        if (rollAssWorship(self, other)) {
            AssFuck fuck = new AssFuck();
            if (fuck.requirements(this, self, other) && fuck.usable(this, self, other) && !self.is(Stsflag.frenzied)) {
                write(other, Formatter.format("<b>The look of {other:name-possessive} ass,"
                                        + " so easily within {self:possessive} reach, causes"
                                        + " {self:subject} to involuntarily switch to autopilot."
                                        + " {self:SUBJECT} simply {self:action:NEED|NEEDS} that ass.</b>",
                                self, other));
                self.add(this, new Frenzied(self.getType(), 1));
                return Optional.of(fuck);
            }
            Anilingus anilingus = new Anilingus();
            if (anilingus.requirements(this, self, other) && anilingus.usable(this, self, other)) {
                write(other, Formatter.format("<b>The look of {other:name-possessive} ass,"
                                        + " so easily within {self:possessive} reach, causes"
                                        + " {self:subject} to involuntarily switch to autopilot."
                                        + " {self:SUBJECT} simply {self:action:NEED|NEEDS} that ass.</b>",
                                self, other));
                return Optional.of(anilingus);
            }
        }
        return Optional.empty();
    }

    /**
     * @param usage
     * @return
     */
    private boolean doAction(Skill.SkillUsage usage) {
        Character user = usage.user;
        Character target = usage.target;
        Optional<Skill> worshipSkill = checkWorship(user, target, usage.skill);
        if (worshipSkill.isPresent()) {
            usage = new Skill.SkillUsage<>(worshipSkill.get(), user, target);
        }
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.println(user.getTrueName() + " uses " + usage.skill.getLabel(this, user));
        }
        boolean results = resolveSkill(usage);
        this.write("<br/>");
        return results;
    }

    public void chooseSkill(Character c, Skill skill) {
        Skill.SkillUsage usage = new Skill.SkillUsage<>(skill, c, skill.getDefaultTarget(this, c));
        if (c == p1) {
            p1act = usage;
        }
        if (c == p2) {
            p2act = usage;
        }
    }

    /**
     * Performs pet actions.
     * @return Whether a pet action was performed.
     */
    private boolean doPetActions() {
        Set<PetCharacter> alreadyBattled = new HashSet<>();
        if (otherCombatants.size() > 0) {
            ArrayList<PetCharacter> pets = new ArrayList<>(otherCombatants);
            for (PetCharacter pet : pets) {
                if (!otherCombatants.contains(pet) || alreadyBattled.contains(pet)) { continue; }
                for (PetCharacter otherPet : pets) {
                    if (!otherCombatants.contains(pet) || alreadyBattled.contains(otherPet)) { continue; }
                    if (!pet.getSelf().owner().equals(otherPet.getSelf().owner()) && Random.random(2) == 0) {
                        petbattle(pet.getSelf(), otherPet.getSelf());
                        alreadyBattled.add(pet);
                        alreadyBattled.add(otherPet);
                    }
                }
            }

            List<PetCharacter> actingPets = new ArrayList<>(otherCombatants);
            actingPets.stream().filter(pet -> !alreadyBattled.contains(pet)).forEach(pet -> {
                pet.chooseSkill(this);
                if (pet.getSelf().owner().has(Trait.devoteeFervor) && Random.random(2) == 0) {
                    write(pet, Formatter.format("{self:SUBJECT} seems to have gained a second wind from {self:possessive} religious fervor!", pet, pet.getSelf().owner()));
                    pet.chooseSkill(this);
                }
            });
            write("<br/>");
        }
        return !alreadyBattled.isEmpty();
    }

    private void doStanceTick(Character self) {
        int stanceDominance = getStance().getDominanceOfStance(self);
        if (!(stanceDominance > 0)) {
            return;
        }

        Character other = getStance().getPartner(this, self);
        Optional<Addiction> addiction = other.getAddiction(AddictionType.DOMINANCE, self);
        if (!addiction.isPresent()) {
            addiction = other.getAnyAddiction(AddictionType.DOMINANCE);
            addiction.ifPresent(add -> {
                if (add.atLeast(Addiction.Severity.MED)) {
                    write(self, Formatter.format(
                                    "{self:name} does {self:possessive} best to be dominant, but with the way " + add
                                                    .getCause().getName()
                                                    + " has been working {self:direct-object} over {self:pronoun-action:are} completely desensitized.",
                                    self, other));
                }
            });
            return;
        }

        if (self.has(Trait.smqueen)) {
                write(self,
                            Formatter.format("{self:NAME-POSSESSIVE} cold gaze in {self:possessive} dominant position"
                                            + " makes {other:direct-object} shiver.",
                                            self, other));
            other.loseWillpower(this, stanceDominance, 0, false, " (SM Queen)");
        } else if (getStance().time % 2 == 0 && getStance().time > 0) {
            if (other.has(Trait.indomitable)) {
                write(self, Formatter.format("{other:SUBJECT}, typically being the dominant one,"
                                + "{other:action:are|is} simply refusing to acknowledge {self:name-possessive}"
                                + " current dominance.", self, other));
                stanceDominance = Math.max(1, stanceDominance - 3);
            } else {
                write(self, Formatter.format("{other:NAME-POSSESSIVE} compromising position takes a toll on {other:possessive} willpower.",
                                            self, other));
            }
            other.loseWillpower(this, stanceDominance, 0, false, " (Dominance)");
        }
        
        if (self.has(Trait.confidentdom) && Random.random(2) == 0) {
            Attribute attr;
            String desc;
            if (self.get(Attribute.ki) > 0 && Random.random(2) == 0) {
                attr = Attribute.ki;
                desc = "strengthening {self:possessive} focus on martial discipline";
            } else if (Random.random(2) == 0) {
                attr = Attribute.power;
                desc = "further empowering {self:possessive} muscles";
            } else {
                attr = Attribute.cunning;
                desc = "granting {self:direct-object} increased mental clarity";
            }
            write(self, Formatter.format("{self:SUBJECT-ACTION:feel|feels} right at home atop"
                            + " {other:name-do}, %s.", self, other, desc));
            self.add(this, new AttributeBuff(self.getType(), attr, Random.random(3) + 1, 10));
        }

        if (self.has(Trait.unquestionable) && Random.random(4) == 0) {
            write(self, Formatter.format("<b><i>\"Stay still, worm!\"</i> {self:subject-action:speak|speaks}"
                            + " with such force that it casues {other:name-do} to temporarily"
                            + " cease resisting.</b>", self, other));
            other.add(this, new Flatfooted(other.getType(), 1, false));
        }

        Optional<String> compulsion = Compulsive.describe(this, self, Compulsive.Situation.STANCE_FLIP);
        if (compulsion.isPresent() && Random.random(10) < 3 && new Reversal().usable(this, other, self)) {
            self.pain(this, null, Random.random(20, 50));
            Position nw = stance.reverse(this, false);
            if (!stance.equals(nw)) {
                stance = nw;
            } else {
                stance = new Pin(other.getType(), self.getType());
            }
            write(self, compulsion.get());
            Compulsive.doPostCompulsion(this, self, Situation.STANCE_FLIP);
        }
    }

    private boolean checkCounter(Character attacker, Character target) {
        return !target.has(Trait.submissive) && getStance().mobile(target)
                        && target.counterChance(attacker) > Random.random(100);
    }

    private boolean resolveCrossCounter(Skill.SkillUsage usage, int chance) {
        if (usage.target.has(Trait.CrossCounter) && Random.random(100) < chance) {
            if (!usage.target.human()) {
                write(usage.target, Formatter.format("As {other:SUBJECT-ACTION:move|moves} to counter, {self:subject-action:seem|seems} to disappear from {other:possessive} line of sight. "
                                + "A split second later, {other:pronoun-action:are|is} lying on the ground with a grinning {self:name-do} standing over {other:direct-object}. "
                                + "How did {self:pronoun} do that!?", usage.user, usage.target));
            } else {
                write(usage.target, Formatter.format(
                                "As {other:subject} moves to counter your assault, you press {other:possessive} arms down with your weight and leverage {other:possessive} "
                                                + "forward motion to trip {other:direct-object}, sending the poor {other:girl} crashing onto the floor.",
                                usage.user, usage.target));
            }
            usage.user.add(this, new Falling(usage.user.getType()));
            return true;
        }
        return false;
    }

    /**
     * @param usage The instance of skill usage to resolve.
     * @return true if either combatant orgasmed
     */
    boolean resolveSkill(Skill.SkillUsage usage) {
        Skill skill = usage.skill;
        Character user = usage.user;
        Character target = usage.target;
        boolean orgasmed = false;
        boolean madeContact = false;
        if (Skill.skillIsUsable(this, skill, user, target)) {
            boolean success;
            if (!target.human() || !target.is(Stsflag.blinded)) {
                write(user
                           .subjectAction("use ", "uses ") + skill.getLabel(this, user) + ".");
            }
            if (skill.makesContact() && !getStance().dom(target) && target.canAct()
                            && checkCounter(user, target)) {
                write("Countered!");
                if (!resolveCrossCounter(usage, 25)) {
                    target.counterattack(user, skill.type(this, user), this);
                }
                madeContact = true;
                success = false;
            } else if (target.is(Stsflag.counter) && skill.makesContact()) {
                write("Countered!");
                if (!resolveCrossCounter(usage, 50)) {
                    CounterStatus s = (CounterStatus) target.getStatus(Stsflag.counter);
                    if (user
                             .is(Stsflag.wary)) {
                        write(target, s.getCounterSkill()
                                       .getBlockedString(this, user, user));
                    } else {
                        s.resolveSkill(this, user);
                    }
                }
                madeContact = true;
                success = false;
            } else {
                success = Skill.resolve(skill, this, user, target);
                madeContact |= success && skill.makesContact();
            }
            if (success) {
                if (skill.getTags(this, user).contains(SkillTag.thrusting) && user.has(Trait.Jackhammer) && Random.random(2) == 0) {
                    write(user, Formatter.format("{self:NAME-POSSESSIVE} hips don't stop as {self:pronoun-action:continue|continues} to fuck {other:direct-object}.", user, target));
                    Skill.resolve(new WildThrust(), this, user, target);
                }
                if (skill.getTags(this, user).contains(SkillTag.thrusting) && user.has(Trait.Piledriver) && Random.random(3) == 0) {
                    write(user, Formatter.format("{self:SUBJECT-ACTION:fuck|fucks} {other:name-do} <b>hard</b>, so much so that {other:pronoun-action:are|is} momentarily floored by the stimulation.", user, target));
                    target.add(this, new Stunned(target.getType(), 1, false));
                }
                if (skill.type(this, user) == Tactics.damage) {
                    checkAndDoPainCompulsion(user);
                }
            }
            if (skill.type(this, user) == Tactics.damage) {
                checkAndDoPainCompulsion(user);
            }
            if (madeContact) {
            	resolveContactBonuses(user, target);
            	resolveContactBonuses(target, user);
            }
            checkStamina(target);
            checkStamina(user);
            orgasmed = target.orgasmed || user.orgasmed;
            lastFailed = false;
        } else {
            write(user
                       .possessiveAdjective() + " " + skill.getLabel(this, user) + " failed.");
            lastFailed = true;
        }
        return orgasmed;
    }

    private void checkAndDoPainCompulsion(Character self) {
        Optional<String> compulsion = Compulsive.describe(this, self, Situation.PUNISH_PAIN);
        if (compulsion.isPresent()) {
            self.pain(this, null, Random.random(10, 40));
            write(compulsion.get());
            Compulsive.doPostCompulsion(this, self, Situation.PUNISH_PAIN);
        }
    }
    
    private void resolveContactBonuses(Character contacted, Character contacter) {
		if (contacted.has(Trait.VolatileSubstrate) && contacted.has(Trait.slime)) {
			contacter.add(this, new Slimed(contacter.getType(), contacted.getType(), 1));
		}
	}

    private CombatPhase determineSkillOrder() {
        if (p1.init() + p1act.skill.speed(p1) >= p2.init() + p2act.skill.speed(p2)) {
            return CombatPhase.P1_ACT_FIRST;
        } else {
            return CombatPhase.P2_ACT_FIRST;
        }
    }

    public void updateGUI() {
        setChanged();
        notifyObservers();
    }

    public void resetMessageAfterGUIUpdate() {
        message = "";
        combatMessageChanged = false;
        clearText = false;
    }

    public void write(String text) {
        if (this.cloned) {
            return;
        }
        // TODO: Does this work if the message starts with an HTML tag?
        text = Formatter.capitalizeFirstLetter(text);
        if (text.isEmpty()) {
            return;
        }
        // Unless this is the first message since the last clear, prepend a newline.
        if (wroteMessageSinceLastClear) {
            text = "<br/>" + text;
        }
        setMessage(text);
        wroteMessageSinceLastClear = true;
        combatMessageChanged = true;
    }

    public void write(Character user, String text) {
        write(GUIColor.characterColor(user), text);
    }

    public void write(GUIColor color, String text) {
        write(colorMessage(color, text));
    }

    private String colorMessage(GUIColor color, String text) {
        return String.format("<font color=%s>", color.rgbHTML()) + text + "<font color='white'>";
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String text) {
        if (combatMessageChanged) {
            // The GUI hasn't written the latest changes, append
            message = message + text;
        } else {
            // send a new message
            message = text;
        }
        combatMessageChanged = true;
    }

    public String debugMessage() {
        return "Stance: " + getStance().getClass()
                                       .getName()
                        + "\np1: " + p1.debugMessage(this) + "\np2: " + p2.debugMessage(this);
    }

    private void checkStamina(Character p) {
        if (p.getStamina()
             .isEmpty() && !p.is(Stsflag.stunned)) {
            p.add(this, new Winded(p.getType(), 3));
            if (p.isPet()){
                // pets don't get stance changes
                return;
            }
            Character other;
            if (p == p1) {
                other = p2;
            } else {
                other = p1;
            }
            if (!getStance().prone(p)) {
                if (!getStance().mobile(p) && getStance().dom(other)) {
                    if (p.human()) {
                        write(p, "Your legs give out, but " + other.getName() + " holds you up.");
                    } else {
                        write(p, String.format("%s slumps in %s arms, but %s %s %s to keep %s from collapsing.",
                                        p.subject(), other.nameOrPossessivePronoun(),
                                        other.pronoun(), other.action("support"), p.directObject(),
                                        p.directObject()));
                    }
                } else if (getStance().havingSex(this, p) && getStance().dom(p) && getStance().reversable(this)) {
                    write(getOpponent(p), Formatter.format("{other:SUBJECT-ACTION:take|takes} the chance to shift into a more dominant position.", p, getOpponent(p)));
                    setStance(getStance().reverse(this, false));
                } else {
                    if (stance.havingSex(this)) {
                        setStance(stance.reverse(this, true));
                    } else {
                        if (p.human()) {
                            write(p, "You don't have the strength to stay on your feet. You slump to the floor.");
                        } else {
                            write(p, p.getName() + " drops to the floor, exhausted.");
                        } 
                        setStance(new StandingOver(other.getType(), p.getType()), null, false);
                    }
                }
                p.loseWillpower(this, Math.min(p.getWillpower()
                                                .max()
                                / 8, 15), true);
            }
            if (other.has(Trait.dominatrix)) {
                if (p.hasAddiction(AddictionType.DOMINANCE)) {
                    write(other, String.format("Being dominated by %s again reinforces %s"
                                    + " submissiveness towards %s.", other.getName(), p.nameOrPossessivePronoun(),
                                    other.directObject()));
                } else {
                    write(other, Formatter.format("There's something about the way {other:subject-action:know} just"
                                    + " how and where to hurt {self:name-do} which some part of {self:possessive}"
                                    + " psyche finds strangely appealing. {self:SUBJECT-ACTION:find} {self:reflective}"
                                    + " wanting more.", p, other));
                }
                p.addict(this, AddictionType.DOMINANCE, other, Addiction.HIGH_INCREASE);
            }
        }
    }

    private void next() throws InterruptedException {
        boolean prompt = true;
        // NPCs only
        if (shouldAutoresolve()) {
            prompt = false;
        }
        // Fast combat display
        if (Flag.checkFlag(Flag.AutoNext) && FAST_COMBAT_SKIPPABLE_PHASES.contains(phase)) {
            prompt = false;
        }
        // Don't pause on blank screens
        if (!wroteMessageSinceLastClear || phase == CombatPhase.START) {
            prompt = false;
        }
        if (prompt) {
            updateGUI();
            this.promptNext(gui);
        }
    }

    void intervene(Character intruder, Character assist) throws InterruptedException {
        // Start the fight on next combat phase
        delayCounter = 0;
        Character target;
        if (p1 == assist) {
            target = p2;
        } else {
            target = p1;
        }
        phase = CombatPhase.RESULTS_SCENE;
        if (p1.human() || p2.human() || intruder.human()) {
            loadCombatGUI(gui);
        }
        if (target.resist3p(this, intruder, assist)) {
            target.gainXP(20 + target.lvlBonus(intruder));
            intruder.gainXP(10 + intruder.lvlBonus(target));
            // TODO: This represents an orgasm and should be handled appropriately by a generic method
            intruder.getArousal()
                    .empty();
            if (intruder.has(Trait.insatiable)) {
                intruder.getArousal()
                        .restore((int) (intruder.getArousal()
                                                .max()
                                        * 0.2D));
            }
            target.undress(this);
            intruder.defeated(target);
            intruder.defeated(assist);
        } else {
            intruder.intervene3p(this, target, assist);
            next();
            assist.victory3p(this, target, intruder);
        }
    }

    // TODO: There's some questionable logic going on here. Go through and document it.
    public void end() throws InterruptedException {
        p1.state = State.ready;
        p2.state = State.ready;
        if (p1.human() || p2.human()) {
            Optional<CombatScene> scene;
            do {
                NPC npc;
                if (!p1.human()) {
                    npc = (NPC) p1;
                } else {
                    npc = (NPC) p2;
                }
                scene = getPostCombatScene(npc);
                if (scene.isPresent()) {
                    gui.clearText();
                    gui.clearCommand();
                    scene.get().visit(this, npc);
                    this.promptNext(gui);
                }
            } while (scene.isPresent());
        }

        p1.endofbattle(this);
        p2.endofbattle(this);

        getCombatantData(p1).getRemovedItems().forEach(p1::gain);
        getCombatantData(p2).getRemovedItems().forEach(p2::gain);

        GameState.getGameState().characterPool.otherCombatants = null;

        location.endEncounter();
        p1.spendXP();
        p1.spendLevels(this);
        p2.spendXP();
        p2.spendLevels(this);

        if (doExtendedLog()) {
            log.logEnd(winner);
        }
        if (!p1.has(Trait.Pseudopod)) {
            Match.getMatch().getMatchData().getDataFor(p1).setArmManager(getCombatantData(p1).getManager());
        }
        if (!p2.has(Trait.Pseudopod)) {
            Match.getMatch().getMatchData().getDataFor(p2).setArmManager(getCombatantData(p2).getManager());
        }
        gui.clearPortrait();
    }

    private Optional<CombatScene> getPostCombatScene(NPC npc) {
        List<CombatScene> availableScenes = npc.getPostCombatScenes()
                        .stream()
                        .filter(scene -> scene.meetsRequirements(this, npc))
                        .collect(Collectors.toList());
        return Random.pickRandom(availableScenes);
    }

    private void petbattle(Pet one, Pet two) {
        int roll1 = Random.random(20) + one.power();
        int roll2 = Random.random(20) + two.power();
        // pussies have an advantage?
        if (one.hasPussy() && two.hasDick()) {
            roll1 += 3;
        } else if (one.hasDick() && two.hasPussy()) {
            roll2 += 3;
        }
        if (roll1 > roll2) {
            one.vanquish(this, two);
        } else if (roll2 > roll1) {
            two.vanquish(this, one);
        } else {
            write(one.getName() + " and " + two.getName()
                            + " engage each other for awhile, but neither can gain the upper hand.");
        }
    }

    @Override
    public Combat clone() throws CloneNotSupportedException {
        Combat c = (Combat) super.clone();
        c.p1 = p1.clone();
        c.p2 = p2.clone();
        c.p1.finishClone(c.p2);
        c.p2.finishClone(c.p1);
        c.combatantData = new HashMap<>();
        combatantData.forEach((name, data) -> c.combatantData.put(name, (CombatantData) data.clone()));
        c.stance = getStance().clone();
        c.state = state;
        if (c.getStance().top == p1.getType()) {
            c.getStance().top = c.p1.getType();
        }
        if (c.getStance().top == p2.getType()) {
            c.getStance().top = c.p2.getType();
        }
        if (c.getStance().bottom == p1.getType()) {
            c.getStance().bottom = c.p1.getType();
        }
        if (c.getStance().bottom == p2.getType()) {
            c.getStance().bottom = c.p2.getType();
        }
        c.otherCombatants = new ArrayList<>();
        for (PetCharacter pet : otherCombatants) {
            if (pet.isPetOf(p1)) {
                c.otherCombatants.add(pet.cloneWithOwner(c.p1));
            } else if (pet.isPetOf(p2)) {
                c.otherCombatants.add(pet.cloneWithOwner(c.p2));
            }
        }
        if (getStance() instanceof  Threesome) {
            ((Threesome) c.getStance()).setOtherCombatants(c.otherCombatants);
        }
        c.postCombatScenesSeen = this.postCombatScenesSeen;
        c.cloned = true;
        return c;
    }

    public Skill lastact(Character user) {
        if (user == p1) {
            return p1act.skill;
        } else if (user == p2) {
            return p2act.skill;
        } else {
            return null;
        }
    }

    private void offerImage(String path, String artist) {
        imagePath = path;
        // TODO: Band-aid for NPE in tests. Refactor so this check is unnecessary.
        if (imagePath == null) {
            return;
        }
        if (!imagePath.isEmpty() && !cloned && isBeingObserved()) {
            gui.displayImage(imagePath, images.get(imagePath));
        }
    }

    public void forfeit() {
        try {
            end();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Position getStance() {
        return stance;
    }

    private void checkStanceStatus(Character c, Position oldStance, Position newStance) {
        if (oldStance.sub(c) && !newStance.sub(c)) {
            if ((oldStance.prone(c) || !oldStance.mobile(c)) && !newStance.prone(c) && newStance.mobile(c)) {
                c.add(this, new Braced(c.getType()));
                c.add(this, new Wary(c.getType(), 3));
            } else if (!oldStance.mobile(c) && newStance.mobile(c)) {
                c.add(this, new Wary(c.getType(), 3));
            }
        }
    }

    public void setStance(Position newStance) {
        setStance(newStance, null, true);
    }

    private void doEndPenetration(Character self, Character partner) {
        List<BodyPart> parts1 = stance.getPartsFor(this, self, partner);
        List<BodyPart> parts2 = stance.getPartsFor(this, partner, self);
        parts1.forEach(part -> parts2.forEach(other -> part.onEndPenetration(this, self, partner, other)));
        parts2.forEach(part -> parts1.forEach(other -> part.onEndPenetration(this, partner, self, other)));
    }

    private void doStartPenetration(Position stance, Character self, Character partner) {
        List<BodyPart> parts1 = stance.getPartsFor(this, self, partner);
        List<BodyPart> parts2 = stance.getPartsFor(this, partner, self);
        parts1.forEach(part -> parts2.forEach(other -> part.onStartPenetration(this, self, partner, other)));
        parts2.forEach(part -> parts1.forEach(other -> part.onStartPenetration(this, partner, self, other)));
    }

    public void struggle(Character struggler, Position position) {
        position.struggle(this, struggler);
        position.time += 2;
        Character partner = position.getPartner(this, struggler);
        partner.weaken(this, (int) DamageType.stance.modifyDamage(struggler, partner, Random.random(6, 11)));
    }

    public void escape(Character escapee, Position position) {
        position.escape(this, escapee);
        position.time += 2;
    }

    public void setStance(Position newStance, Character initiator, boolean voluntary) {
        if ((newStance.top != getStance().bottom && newStance.top != getStance().top) || (newStance.bottom != getStance().bottom && newStance.bottom != getStance().top)) {
            if (initiator != null && initiator.isPet() && newStance.top == initiator.getType()) {
                PetInitiatedThreesome threesomeSkill = new PetInitiatedThreesome();
                if (newStance.havingSex(this)) {
                    threesomeSkill.resolve(this, initiator, newStance.getBottom());
                } else if (!getStance().sub(newStance.getBottom())) {
                    write(initiator, Formatter.format("{self:SUBJECT-ACTION:take|takes} the chance to send {other:name-do} sprawling to the ground", initiator, newStance.getBottom()));
                    newStance.getBottom().add(this, new Falling(newStance.bottom));
                }
            } else {
                if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
                    System.out.printf("Tried to change stance without both players, stopping: %s -> %s\n",
                                    stance.getClass().getName(),
                                    newStance.getClass().getName());
                    Thread.dumpStack();
                }
            }
            return;
        }
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.printf("Stance Change: %s -> %s\n", stance.getClass()
                                                                 .getName(),
                            newStance.getClass()
                                     .getName());
        }
        if (initiator != null) {
            Character otherCharacter = getOpponent(initiator);
            if (voluntary && newStance.en == Stance.neutral && getStance().en != Stance.kneeling && otherCharacter.has(Trait.genuflection) && rollWorship(initiator, otherCharacter)) {
                write(initiator, Formatter.format("While trying to get back up, {self:name-possessive} eyes accidentally met {other:name-possessive} gaze. "
                                + "Like a deer in headlights, {self:possessive} body involuntarily stops moving and kneels down before {other:direct-object}.", initiator, otherCharacter));
                newStance = new Kneeling(otherCharacter.getType(), initiator.getType());
            }
        }
        checkStanceStatus(p1, stance, newStance);
        checkStanceStatus(p2, stance, newStance);

        if (stance.inserted() && !newStance.inserted()) {
            doEndPenetration(p1, p2);
            Character threePCharacter = stance.getDomSexCharacter();
            if (threePCharacter != p1 && threePCharacter != p2) {
                doEndPenetration(p1, threePCharacter);
                doEndPenetration(p2, threePCharacter);
                getCombatantData(threePCharacter).setIntegerFlag("ChoseToFuck", 0);
            }
            getCombatantData(p1).setIntegerFlag("ChoseToFuck", 0);
            getCombatantData(p2).setIntegerFlag("ChoseToFuck", 0);
        } else if (!stance.inserted() && newStance.inserted() && (newStance.penetrated(this, p1) || newStance.penetrated(this, p2)) ) {
            doStartPenetration(newStance, p1, p2);
        } else if (!stance.havingSex(this) && newStance.havingSex(this)) {
            Character threePCharacter = stance.getDomSexCharacter();
            if (threePCharacter != p1 && threePCharacter != p2) {
                doStartPenetration(newStance, p1, threePCharacter);
                doStartPenetration(newStance, p2, threePCharacter);
            }

            if (voluntary) {
                if (initiator != null) {
                    getCombatantData(initiator).setIntegerFlag("ChoseToFuck", 1);
                    getCombatantData(getOpponent(initiator)).setIntegerFlag("ChoseToFuck", -1);
                }
                if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
                    System.out.println(initiator + " initiated penetration, voluntary=" + voluntary);
                }
            }
            checkBreeder(p1, voluntary);
            checkBreeder(p2, voluntary);
        }

        if (stance != newStance && initiator != null && initiator.has(Trait.Catwalk)) {
            write(initiator, Formatter.format("The way {self:subject-action:move|moves} exudes such feline grace that it demands {other:name-possessive} attention.",
                            initiator, getOpponent(initiator)));
            initiator.add(this, new Alluring(initiator.getType(), 1));
        }

        stance = newStance;
        offerImage(stance.image(), "");
    }

    private void checkBreeder(Character checked, boolean voluntary) {
        Character opp = getStance().getPartner(this, checked);
        if (checked.checkAddiction(AddictionType.BREEDER, opp) && getStance().inserted(checked)) {
            if (voluntary) {
                write(checked, "As you enter Kat, instinct immediately kicks in. It just"
                                + " feels so right, like this is what you're supposed"
                                + " to be doing all the time.");
                checked.addict(this, AddictionType.BREEDER, opp, Addiction.MED_INCREASE);
            } else {
                write(checked, "Something shifts inside of you as Kat fills herself with"
                                + " you. A haze descends over your mind, clouding all but a desire"
                                + " to fuck her as hard as you can.");
                checked.addict(this, AddictionType.BREEDER, opp, Addiction.LOW_INCREASE);
            }
        }
    }

    public Character getOpponent(Character self) {
        if (self.equals(p1) || self.isPetOf(p1)) {
            return p2;
        }
        if (self.equals(p2) || self.isPetOf(p2)) {
            return p1;
        }
        System.err.println("Tried to get an opponent for " + self.getTrueName() + " which does not exist in combat.");
        Thread.dumpStack();
        return NPC.noneCharacter();
    }

    public void writeSystemMessage(String battleString) {
        if (Flag.checkFlag(Flag.systemMessages)) {
            write(battleString);
        }
    }

    private void writeSystemMessage(Character character, String string) {
        if (Flag.checkFlag(Flag.systemMessages)) {
            write(character, string);
        }
    }

    public int getTimer() {
        return timer;
    }

    private boolean doExtendedLog() {
        return (p1.human() || p2.human()) && Flag.checkFlag(Flag.extendedLogs);
    }

    public boolean isBeingObserved() {
        return beingObserved;
    }

    private void setBeingObserved() {
        this.beingObserved = true;
        if (log == null && DebugFlags.isDebugOn(DebugFlags.DEBUG_SPECTATE)) {
            log = new CombatLog(this);
        }
    }
    
    public boolean shouldPrintReceive(Character ch, Combat c) {
        return beingObserved || (c.p1.human() || c.p2.human());
    }
    
    public boolean shouldAutoresolve() {
        return !beingObserved && !p1.human() && !p2.human();
    }

    public String bothDirectObject(Character target) {
        return target.human() ? "you" : "them";
    }
    
    public String bothPossessive(Character target) {
        return target.human() ? "your" :  "their";
    }
    
    public String bothSubject(Character target) {
        return target.human() ? "you" : "they";
    }

    public List<PetCharacter> getPetsFor(Character target) {
        return otherCombatants.stream().filter(c -> c.isPetOf(target)).collect(Collectors.toList());
    }

    public void removePet(PetCharacter self) {
        if (self.has(Trait.resurrection) && !getCombatantData(self).getBooleanFlag("resurrected")) {
            write(self, "Just as " + self.subject() + " was about to disappear, a dazzling light covers " 
            + self.possessiveAdjective() + " body. When the light fades, " + self.pronoun() + " looks completely refreshed!");
            getCombatantData(self).setBooleanFlag("resurrected", true);
            self.getArousal().empty();
            self.getMojo().empty();
            self.getWillpower().fill();
            self.getStamina().fill();
            return;
        }
        getCombatantData(self).setBooleanFlag("resurrected", false);
        // Pets will be removed at end of round
        defeatedPets.add(self);
    }

    public void addPet(Character master, PetCharacter self) {
        if (self == null) {
            System.err.println("Tried to add null pet!");
            Thread.dumpStack();
            return;
        }
        if (master.has(Trait.leadership)) {
            int levelups = Math.max(5, master.getLevel() / 4);
            self.getSelf().setPower(self.getSelf().getPower() + levelups);
            self.addLevelsImmediate(this, levelups);
        }
        if (master.has(Trait.tactician)) {
            self.getSelf().setAc(self.getSelf().getAc() + 3);
            self.getArousal().setMax(self.getArousal().trueMax() * 1.5f);
            self.getStamina().setMax(self.getStamina().trueMax() * 1.5f);
        }
        self.getArousal().empty();
        self.getStamina().fill();
        writeSystemMessage(self, Formatter.format("{self:SUBJECT-ACTION:have|has} summoned {other:name-do} (Level %s)",
                                        master, self, self.getLevel()));
        otherCombatants.add(self);
        this.write(self, self.challenge(getOpponent(self)));
    }

    public List<PetCharacter> getOtherCombatants() {
        return otherCombatants;
    }

    public boolean otherCombatantsContains(Character character) {
        if (character instanceof PetCharacter) {
            PetCharacter pet = (PetCharacter) character;
            return otherCombatants.contains(pet);
        }
        return false;
    }

    public List<CharacterType> getOtherCombatantTypes() {
        return otherCombatants.stream().map(PetCharacter::getType).collect(Collectors.toList());
    }

    public boolean isEnded() {
        return phase == CombatPhase.FINISHED_SCENE || phase == CombatPhase.ENDED;
    }

    private void promptNext(GUI gui) throws InterruptedException {
        gui.clearCommand();
        gui.next("Next").await();
        gui.clearCommand();
    }

    public void removeCombatGUI(GUI gui) {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("End Combat");
        }
        gui.combat = null;
        gui.clearText();
        gui.clearImage();
        gui.showMap();
    }

    public void loadCombatGUI(GUI gui) {
        addObserver(gui);
        setBeingObserved();
        delayCounter = 0;
        gui.combat = this;
        gui.loadPortrait(this, this.p1, this.p2);
        gui.showPortrait();
    }

    void runCombatNoDelay() throws InterruptedException {
        delayCounter = 0;
        runCombat();
    }

    public void runCombat() throws InterruptedException {
        // delayCounter only applies to combats not involving a human
        if (!isBeingObserved() && delayCounter > 0) {
            delayCounter--;
            return;
        }
        if (phase == CombatPhase.START) {
            startScene();
        }
        GameState.getGameState().characterPool.setOtherCombatants(this.otherCombatants);
        while (!finished) {
            boolean pause = false;
            if (!cloned && isBeingObserved()) {
                gui.loadPortrait(this, p1, p2);
            }
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
                System.out.println("Current phase = " + phase);
            }
            if (winner.isPresent()) {
                // Combat should end; do post-combat scenes
                if (phase != CombatPhase.FINISHED_SCENE && phase != CombatPhase.RESULTS_SCENE) {
                    phase = levelDrainPostCombat(); // Either RESULTS_SCENE or LEVEL_DRAIN
                    pause = true;
                }
            } else {
                winner = checkLosses();
            }
            if ((p1.orgasmed || p2.orgasmed) && SKIPPABLE_PHASES.contains(phase)) {
                phase = CombatPhase.UPKEEP;
            }
            switch (phase) {
                case START:
                    phase = CombatPhase.PRETURN;
                    pause = false;
                    break;
                case PRETURN:
                    clearMessage();
                    doPreturnUpkeep();
                    phase = CombatPhase.SKILL_SELECTION;
                    pause = false;
                    break;
                case SKILL_SELECTION:
                    pause = pickSkills();
                    break;
                case PET_ACTIONS:
                    phase = CombatPhase.DETERMINE_SKILL_ORDER;
                    // Pause to let the player read, if any pets took actions.
                    pause = doPetActions();
                    break;
                case DETERMINE_SKILL_ORDER:
                    phase = determineSkillOrder();
                    pause = false;
                    break;
                case P1_ACT_FIRST:
                    if (doAction(p1act)) {
                        phase = CombatPhase.UPKEEP;
                    } else {
                        phase = CombatPhase.P2_ACT_SECOND;
                    }
                    pause = true;
                    break;
                case P1_ACT_SECOND:
                    doAction(p1act);
                    phase = CombatPhase.UPKEEP;
                    pause = true;
                    break;
                case P2_ACT_FIRST:
                    if (doAction(p2act)) {
                        phase = CombatPhase.UPKEEP;
                    } else {
                        phase = CombatPhase.P1_ACT_SECOND;
                    }
                    pause = true;
                    break;
                case P2_ACT_SECOND:
                    doAction(p2act);
                    phase = CombatPhase.UPKEEP;
                    pause = true;
                    break;
                case UPKEEP:
                    doEndOfTurnUpkeep();
                    phase = CombatPhase.PRETURN;
                    pause = true;
                    break;
                case RESULTS_SCENE:
                    winner.ifPresent(victor -> {
                        if (victor == NPC.noneCharacter()) {
                            draw();
                        } else {
                            victory(victor);
                        }
                    });
                    phase = CombatPhase.FINISHED_SCENE;
                    pause = true;
                    break;
                case FINISHED_SCENE:
                    phase = CombatPhase.ENDED;
                    break;
                default:
                    pause = true;
            }
            if (isBeingObserved()) {
                updateGUI();
            }
            if (phase == CombatPhase.ENDED) {
                end();
                finished = true;
            } else if (pause) {
                next();
            }
        }
    }
}
