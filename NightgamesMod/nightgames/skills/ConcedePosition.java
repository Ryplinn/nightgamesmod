package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;

public class ConcedePosition extends Skill {

    public ConcedePosition() {
        super("Concede Position");
        addTag(SkillTag.worship);
        addTag(SkillTag.petDisallowed);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return false;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().dom(user) && c.getStance().reverse(c, false) != c.getStance() && c.getStance().havingSex(c, user);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        c.setStance(c.getStance().reverse(c, false));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
            return Formatter.format("{other:NAME-POSSESSIVE} divine majesty is too much for {self:name-do}. "
                            + "With a docile smile, {self:pronoun-action:concede|concedes} {self:possessive} dominant position to {other:direct-object}.", user, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.anal) {
            return String.format("%s the pressure in %s anus recede as %s pulls out.",
                            target.subjectAction("feel"), target.possessiveAdjective(),
                            user.subject());
        } else if (modifier == Result.reverse) {
            return String.format("%s lifts %s hips more than normal, letting %s dick slip completely out of %s.",
                            user.subject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), user.directObject());
        } else if (modifier == Result.normal) {
            return String.format("%s pulls %s dick completely out of %s pussy, leaving %s feeling empty.",
                            user.subject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), target.directObject());
        } else {
            return String.format("%s lifts herself off %s face, giving %s a brief respite.",
                            user.subject(), target.nameOrPossessivePronoun(), target.directObject());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Concede your dominant position to your opponent";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
