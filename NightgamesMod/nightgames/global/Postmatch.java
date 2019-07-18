package nightgames.global;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.gui.ContinueButton;
import nightgames.gui.GUI;
import nightgames.gui.SaveButton;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

class Postmatch {
    final CountDownLatch readyForBed;

    private Character player;
    private List<Character> combatants;
    protected boolean normal;

    Postmatch(Character player, List<Character> combatants) throws InterruptedException {
        readyForBed = new CountDownLatch(1);
        this.player = player;
        this.combatants = combatants;
        normal = true;
        for (Character self : combatants) {
            for (Character other : combatants) {
                if (self != other && self.getAffection(other) >= 1 && self.getAttraction(other) >= 20) {
                    self.gainAttraction(other, -20);
                    self.gainAffection(other, 2);
                }
            }
        }

        events();
        if (normal) {
            normal();
        }
    }

    void endMatchGui() throws InterruptedException {
        GUI.gui.combat = null;
        GUI.gui.clearCommand();
        GUI.gui.showNone();
        GUI.gui.mntmQuitMatch.setEnabled(false);
        ContinueButton sleep = GUI.gui.next("Go to sleep");
        GUI.gui.commandPanel.add(new SaveButton());
        GUI.gui.commandPanel.refresh();
        sleep.await();
        readyForBed.countDown(); // unblock main loop
    }

    void endMatch() {
        double level = 0;
        int maxLevelTracker = 0;

        Set<Character> everyone = GameState.getGameState().characterPool.everyone();
        for (Character character : everyone) {
            character.getStamina().fill();
            character.getArousal().empty();
            character.getMojo().empty();
            character.change();
            level += character.getLevel();
            if (!character.has(Trait.unnaturalgrowth) && !character.has(Trait.naturalgrowth)) {
                maxLevelTracker = Math.max(character.getLevel(), maxLevelTracker);
            }
        }
        final int maxLevel = maxLevelTracker / everyone.size();
        everyone.stream().filter(c -> c.has(Trait.naturalgrowth)).filter(c -> c.getLevel() < maxLevel + 2)
                        .forEach(c -> {
                            while (c.getLevel() < maxLevel + 2) {
                                c.addLevels(1);
                            }
                            c.spendLevels(null);
                        });
        everyone.stream().filter(c -> c.has(Trait.unnaturalgrowth)).filter(c -> c.getLevel() < maxLevel + 5)
                        .forEach(c -> {
                            while (c.getLevel() < maxLevel + 5) {
                                c.addLevels(1);
                            }
                            c.spendLevels(null);
                        });

        level /= everyone.size();

        for (Character rested : Match.resting) {
            rested.gainXP(100 + Math.max(0, (int) Math.round(10 * (level - rested.getLevel()))));
        }
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Match end");
        }
    }

    private void events() throws InterruptedException {
        String message = "";
        if (Flag.checkFlag(Flag.metLilly) && !Flag.checkFlag(Flag.challengeAccepted) && Random.random(10) >= 7) {
            message = message
                            + "When you gather after the match to collect your reward money, you notice Jewel is holding a crumpled up piece of paper and ask about it. <i>\"This? I found it lying on the ground during the match. It seems to be a worthless piece of trash, but I didn't want to litter.\"</i> Jewel's face is expressionless, but there's a bitter edge to her words that makes you curious. You uncrumple the note and read it.<br/><br/>'Jewel always acts like the dominant, always-on-top tomboy, but I bet she loves to be held down and fucked hard.'<br/><br/><i>\"I was considering finding whoever wrote the note and tying his penis in a knot,\"</i> Jewel says, still impassive. <i>\"But I decided to just throw it out instead.\"</i> It's nice that she's learning to control her temper, but you're a little more concerned with the note. It mentions Jewel by name and seems to be alluding to the Games. You doubt one of the other girls wrote it. You should probably show it to Lilly.<br/><br/><i>\"Oh for fuck's sake..\"</i> Lilly sighs, exasperated. <i>\"I thought we'd seen the last of these. I don't know who writes them, but they showed up last year too. I'll have to do a second sweep of the grounds each night to make sure they're all picked up by morning. They have competitors' names on them, so we absolutely cannot let a normal student find one.\"</i> She toys with a pigtail idly while looking annoyed. <i>\"For what it's worth, they do seem to pay well if you do what the note says that night. Do with them what you will.\"</i><br/>";

            Flag.flag(Flag.challengeAccepted);
        }
        if (!message.equals("")) {
            GUI.gui.clearText();
            GUI.gui.message(message);
            GUI.gui.next("Next").await();
        }
    }

    private void normal() {
        Character closest = null;
        int maxaffection = 0;
        for (Character rival : combatants) {
            if (rival.getAffection(player) > maxaffection) {
                closest = rival;
                maxaffection = rival.getAffection(player);
            }
        }

        if (maxaffection >= 15) {
            closest.afterParty();
        } else {
            GUI.gui.message("You walk back to your dorm and get yourself cleaned up.");
        }
    }
}
