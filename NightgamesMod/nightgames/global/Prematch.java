package nightgames.global;

import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.characters.trait.Trait;
import nightgames.daytime.Daytime;
import nightgames.ftc.FTCMatch;
import nightgames.gui.GUI;
import nightgames.items.Item;
import nightgames.modifier.Modifier;
import nightgames.modifier.standard.FTCModifier;
import nightgames.modifier.standard.NoModifier;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Base class for match setup.
 */
public abstract class Prematch {
    public CompletableFuture<Match> preparedMatch;  // Completes when match is ready to play

    protected Prematch(CompletableFuture<Match> preparedMatch) {
        this.preparedMatch = preparedMatch;
        Flag.unflag(Flag.victory);
    }

    /**
     * Chooses and creates an instance of the Prematch of one of the available Match types.
     * @return A new Prematch of the chosen Match type.
     */
    public static Prematch decideMatchType(CompletableFuture<Match> match) {
        return new PreMatchSchool(match);
        // When the player first reaches level 15, make an FTCMatch.
        // After the first FTC match, there's a one in ten chance that a match will be an FTC match.
        /*
         * TODO Lots of FTC bugs right now, will disable it for the time being.
         * Enable again once some of the bugs are sorted out.
        MatchType type;
        if (Flag.checkFlag(Flag.NoFTC) || GameState.gameState.characterPool.human.getLevel() < 15) {
            type = MatchType.NORMAL;
        } else if (!Flag.checkFlag(Flag.didFTC) || DebugFlags.isDebugOn(DebugFlags.DEBUG_FTC)) {
            type = MatchType.FTC;
        } else {
            type = DebugFlags.isDebugOn(DebugFlags.DEBUG_FTC) || Random.random(10) == 0 ?
                            MatchType.FTC :
                            MatchType.NORMAL;
        }
        return type.buildPrematch();
        */
    }

    private static Set<Character> pickCharacters(Collection<Character> avail, Collection<Character> added, int size) {
        List<Character> randomizer = avail.stream()
                        .filter(c -> !c.human())
                        .filter(c -> !c.has(Trait.event))
                        .filter(c -> !added.contains(c))
                        .collect(Collectors.toList());
        Collections.shuffle(randomizer);
        Set<Character> results = new HashSet<>(added);
        results.addAll(randomizer.subList(0, Math.min(Math.max(0, size - results.size())+1, randomizer.size())));
        return results;
    }

    private void buildMatch(Collection<Character> combatants, Modifier mod) {
        Match match;
        if (mod.name().equals("ftc")) {
            if (combatants.size() < 5) {
                match = new Match(combatants, new NoModifier());
            } else {
                Flag.flag(Flag.FTC);
                match = new FTCMatch(combatants, ((FTCModifier) mod).getPrey());
            }
        } else {
            match = new Match(combatants, mod);
        }
        preparedMatch.complete(match);
    }

    public void setUpMatch(Modifier matchmod) {
        assert Daytime.day == null;
        CharacterPool characterPool = GameState.gameState.characterPool;
        Set<Character> lineup = new HashSet<>(characterPool.debugChars);
        Character lover = null;
        int maxaffection = 0;
        Flag.unflag(Flag.FTC);
        for (Character player : characterPool.availableNpcs()) {
            player.getStamina().fill();
            player.getArousal().empty();
            player.getMojo().empty();
            player.getWillpower().fill();
            if (player.getPure(Attribute.science) > 0) {
                player.chargeBattery();
            }
            if (characterPool.human.getAffection(player) > maxaffection && !player.has(Trait.event) && !Flag
                            .checkCharacterDisabledFlag(player)) {
                maxaffection = characterPool.human.getAffection(player);
                lover = player;
            }
        }
        List<Character> participants = new ArrayList<>();
        participants.add(characterPool.human);
        // Disable characters flagged as disabled
        for (NPC npc : characterPool.availableNpcs()) {
            if (!Flag.checkCharacterDisabledFlag(npc)) {
                participants.add(npc);
            }
        }
        if (lover != null) {
            lineup.add(lover);
        }
        lineup.add(characterPool.human);
        if (matchmod.name().equals("maya")) {
            if (!Flag.checkFlag(Flag.Maya)) {
                characterPool.newChallenger(new Maya(characterPool.human.getLevel()).getCharacter());
                Flag.flag(Flag.Maya);
            }
            NPC maya = Optional.ofNullable(characterPool.getNPC("Maya")).orElseThrow(() -> new IllegalStateException(
                            "Maya data unavailable when attempting to add her to lineup."));
            lineup.add(maya);
            lineup = pickCharacters(participants, lineup, 5);
            Match.resting = new HashSet<>(characterPool.availableNpcs());
            Match.resting.removeAll(lineup);
            maya.gain(Item.Aphrodisiac, 10);
            maya.gain(Item.DisSol, 10);
            maya.gain(Item.Sedative, 10);
            maya.gain(Item.Lubricant, 10);
            maya.gain(Item.BewitchingDraught, 5);
            maya.gain(Item.FeralMusk, 10);
            maya.gain(Item.ExtremeAphrodisiac, 5);
            maya.gain(Item.ZipTie, 10);
            maya.gain(Item.SuccubusDraft, 10);
            maya.gain(Item.Lactaid, 5);
            maya.gain(Item.Handcuffs, 5);
            maya.gain(Item.Onahole2);
            maya.gain(Item.Dildo2);
            maya.gain(Item.Strapon2);
            buildMatch(lineup, matchmod);
        } else if (matchmod.name().equals("ftc")) {
            Character prey = ((FTCModifier) matchmod).getPrey();
            if (!prey.human()) {
                lineup.add(prey);
            }
            lineup = pickCharacters(participants, lineup, 5);
            Match.resting = new HashSet<>(characterPool.availableNpcs());
            Match.resting.removeAll(lineup);
            buildMatch(lineup, matchmod);
        } else if (participants.size() > 5) {
            lineup = pickCharacters(participants, lineup, 5);
            Match.resting = new HashSet<>(characterPool.availableNpcs());
            Match.resting.removeAll(lineup);
            buildMatch(lineup, matchmod);
        } else {
            buildMatch(participants, matchmod);
        }
        GUI.gui.clearText();
    }

    public abstract void prompt(Player player) throws InterruptedException, ExecutionException;

}
