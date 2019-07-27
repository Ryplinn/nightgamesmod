package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;

public class CommandMasturbate extends PlayerCommand {

    CommandMasturbate() {
        super("Force Masturbation");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target) && target.crotchAvailable();
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Convince your opponents to pleasure themselves for your viewing pleasure";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        boolean lowStart = target.getArousal().get() < 15;
        int m = 5 + Random.random(10);
        target.body.pleasure(target, target.body.getRandom("hands"), target.body.getRandomGenital(), m, c, new SkillUsage<>(this, user, target));

        boolean lowEnd = target.getArousal().get() < 15;
        if (user.human()) {
            if (lowStart) {
                if (lowEnd) {
                    c.write(user, deal(c, 0, Result.weak, user, target));
                } else {
                    c.write(user, deal(c, 0, Result.strong, user, target));
                }
            } else {
                c.write(user, deal(c, 0, Result.normal, user, target));
            }
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int magnitude, Result modifier, Character user, Character target) {
        switch (modifier) {
            case normal:
                return target.getName() + " seems more than happy to do as you tell her, "
                                + "as she starts fingering herself in abandon.";
            case special:
                return "Looking at you lustily, " + target.getName() + " rubs her clit as she gets wetter and wetter.";
            case weak:
                return target.getName() + " follows your command to the letter, but"
                                + " it doesn't seem to have that much of an effect on her.";
            default:
                return "<<This should not be displayed, please inform The" + " Silver Bard: CommandMasturbate-deal>>";
        }
    }

    @Override
    public String receive(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return "<<This should not be displayed, please inform The" + " Silver Bard: CommandMasturbate-receive>>";
    }

}
