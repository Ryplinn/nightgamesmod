package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.status.AttributeBuff;

public class ArmBar extends Skill {

    ArmBar() {
        super("Armbar");
        addTag(SkillTag.hurt);
        addTag(SkillTag.staminaDamage);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().dom(user) && c.getStance().reachTop(target) && user.canAct()
                        && !user.has(Trait.undisciplined) && !c.getStance().inserted();
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            int m = (int) DamageType.physical.modifyDamage(user, target, Random.random(6, 10));
            writeOutput(c, m, Result.normal, user, target);
            target.pain(c, user, m);
            target.add(c, new AttributeBuff(target.getType(), Attribute.power, -4, 5));
            target.emote(Emotion.angry, 15);
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.power) >= 20 && !user.has(Trait.undisciplined);
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You grab at " + target.getName() + "'s arm, but "+target.pronoun()+" pulls it free.";
        } else {
            return "You grab " + target.getName()
                            + "'s arm at the wrist and pull it to your chest in the traditional judo submission technique.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s %s wrist, but %s %s it out of %s grasp.",
                            user.subjectAction("grab"), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("pry", "pries"), user.possessiveAdjective());
        } else {
            return String.format("%s %s arm between %s legs, forcibly overextending %s elbow. "
                            + "The pain almost makes %s tap out, but %s %s to yank %s arm out of %s grip",
                            user.subjectAction("pull"), target.nameOrPossessivePronoun(),
                            user.possessiveAdjective(), target.possessiveAdjective(), target.pronoun(),
                            target.pronoun(), target.action("manage"), target.possessiveAdjective(),
                            user.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "A judo submission hold that hyperextends the arm.";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
