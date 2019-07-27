package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;

public class Surrender extends Skill {
    public Surrender() {
        super("Surrender");
        addTag(SkillTag.suicidal);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return -100000000;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct();
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        writeOutput(c, Result.normal, user, target);
        user.temptNoSkillNoTempter(c, user.getArousal().max());
        user.loseWillpower(c, user.getWillpower().max());
        return true;
    }

    @Override
    public int speed(Character user) {
        return 20;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format(
                        "After giving up on the fight, %s start fantasizing about %s body. %s quickly find %s at the edge.",
                        user.subject(), target.possessiveAdjective(),
                        Formatter.capitalizeFirstLetter(user.pronoun()), user.reflectivePronoun());
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format(
                        "After giving up on the fight, %s %s fantasizing about %s body. %s quickly find %s at the edge.",
                        user.subject(), user.action("start"), target.possessiveAdjective(),
                        Formatter.capitalizeFirstLetter(user.pronoun()), user.reflectivePronoun());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Give up";
    }
}
