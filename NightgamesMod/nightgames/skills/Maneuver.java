package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Behind;

public class Maneuver extends Skill {
    public Maneuver() {
        super("Maneuver", 2);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && c.getStance().mobile(user) && !c.getStance().prone(user)
                        && !c.getStance().prone(target) && !c.getStance().behind(user) && user.canAct()
                        && !user.has(Trait.undisciplined) && !c.getStance().connected(c);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 15;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (isFlashStep(user)) {
            if (target.roll(user, accuracy(c, user, target))) {
                writeOutput(c, Result.special, user, target);
                c.setStance(new Behind(user.getType(), target.getType()), user, true);
                user.weaken(c, user.getStamina().get() / 10);
                user.emote(Emotion.confident, 15);
                user.emote(Emotion.dominant, 15);
                target.emote(Emotion.nervous, 10);
                return true;
            } else {
                writeOutput(c, Result.miss, user, target);
                return false;
            }
        } else {
            if (target.roll(user, accuracy(c, user, target))) {
                writeOutput(c, Result.normal, user, target);
                c.setStance(new Behind(user.getType(), target.getType()), user, true);
                user.emote(Emotion.confident, 15);
                user.emote(Emotion.dominant, 15);
                target.emote(Emotion.nervous, 10);
                return true;
            } else {
                writeOutput(c, Result.miss, user, target);
                return false;
            }
        }
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.cunning) >= 20 || isFlashStep(user);
    }

    @Override
    public int speed(Character user) {
        return 8;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return isFlashStep(user) ? 200 : 75;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return "You channel your ki into your feet and dash behind " + target.getName()
            + " faster than her eyes can follow.";
        } else if (modifier == Result.miss) {
            return "You try to get behind " + target.getName() + " but are unable to.";
        } else {
            return "You dodge past " + target.getName() + "'s guard and grab her from behind.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to slip behind %s, but %s %s able to keep %s in sight.",
                            user.subject(), target.nameDirectObject(), target.pronoun(),
                            target.action("are", "is"), user.directObject());
        } else if (modifier == Result.special) {
            return String.format("%s starts to move and suddenly vanishes. %s for a"
                            + " second and feel %s grab %s from behind.",
                            user.subject(), target.subjectAction("hesitate"),
                            user.subject(), target.directObject());
        } else {
            return String.format("%s lunges at %s, but when %s %s to grab %s, %s ducks out of sight. "
                            + "Suddenly %s arms are wrapped around %ss. How did %s get behind %s?",
                            user.subject(), target.nameDirectObject(), target.pronoun(),
                            target.action("try", "tries"), target.directObject(), user.pronoun(),
                            user.nameOrPossessivePronoun(), target.nameOrPossessivePronoun(),
                            user.pronoun(), target.directObject());
        }
    }

    private boolean isFlashStep(Character user) {
        return user.getStamina().percent() > 15 && user.get(Attribute.ki) >= 6;
    }

    @Override
    public String describe(Combat c, Character user) {
        if (isFlashStep(user)) {
            return "Use lightning speed to get behind your opponent before she can react: 10% stamina";
        } else {
            return "Get behind opponent";
        }
    }

    @Override
    public boolean makesContact() {
        return true;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (isFlashStep(user)) {
            return "Flash Step";
        }
        return getName(c, user);
    }
}
