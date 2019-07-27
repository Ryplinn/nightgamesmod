package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Pin;

public class Restrain extends Skill {

    public Restrain() {
        super("Pin");
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && c.getStance().mobile(user) && c.getStance().prone(target)
                        && c.getStance().reachTop(user) && user.canAct() && c.getStance().reachTop(target)
                        && !c.getStance().connected(c);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (rollSucceeded) {
            writeOutput(c, Result.normal, user, target);
            c.setStance(new Pin(user.getType(), target.getType()), user, true);
            target.emote(Emotion.nervous, 10);
            target.emote(Emotion.desperate, 10);
            user.emote(Emotion.dominant, 20);
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.power) >= 8;
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return 75;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You try to catch " + target.getName() + "'s hands, but she squirms too much to keep your grip on her.";
        } else {
            return "You manage to restrain " + target.getName() + ", leaving her helpless and vulnerable beneath you.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to pin %s down, but %s %s %s arms free.",
                            user.subject(), target.nameDirectObject(),
                            target.pronoun(), target.action("keep"), target.possessiveAdjective());
        } else {
            return String.format("%s pounces on %s and pins %s arms in place, leaving %s at %s mercy.",
                            user.subject(), target.nameDirectObject(), target.possessiveAdjective(),
                            target.directObject(), user.directObject());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Restrain opponent until she struggles free";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
