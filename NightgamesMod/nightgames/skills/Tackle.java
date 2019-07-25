package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.stance.Mount;
import nightgames.status.Winded;

public class Tackle extends Skill {

    public Tackle() {
        super("Tackle");

        addTag(SkillTag.positioning);
        addTag(SkillTag.hurt);
        addTag(SkillTag.staminaDamage);
        addTag(SkillTag.knockdown);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && c.getStance().mobile(user) && c.getStance().mobile(target)
                        && !c.getStance().prone(user) && user.canAct();
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.has(Trait.takedown) && target.getStamina().percent() <= 25) {
            c.write(user, Formatter.format("While {other:subject-action:take|takes} a breath,"
                            + " {self:subject-action:take|takes} careful aim at {other:possessive}"
                            + " waist and {self:action:charge|charges} in at full speed. It's a perfect"
                            + " hit, knocking the wind out of {other:subject} and allowing {self:subject}"
                            + " to take {self:subject} place on top of {other:possessive} heaving chest."
                            , user, target));
            c.setStance(new Mount(user.getType(), target.getType()));
            target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, Random.random(15, 30)));
            target.add(c, new Winded(target.getType(), 2));
        }
        if (target.roll(user, accuracy(c, user, target))
                        && user.checkVsDc(Attribute.power, target.knockdownDC() - user.getAttribute(Attribute.animism))) {
            if (user.getAttribute(Attribute.animism) >= 1) {
                writeOutput(c, Result.special, user, target);
                target.pain(c, user, (int) DamageType.physical
                                .modifyDamage(user, target, Random.random(15, 30)));
            } else {
                writeOutput(c, Result.normal, user, target);
                target.pain(c, user, (int) DamageType.physical
                                .modifyDamage(user, target, Random.random(10, 25)));
            }
            c.setStance(new Mount(user.getType(), target.getType()), user, true);
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 15;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.power) >= 26 && !user.has(Trait.petite) || user.getAttribute(Attribute.animism) >= 1;
    }

    @Override
    public int speed(Character user) {
        if (user.getAttribute(Attribute.animism) >= 1) {
            return 3;
        } else {
            return 1;
        }
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        if (user.has(Trait.takedown) && target.getStamina().percent() <= 25) {
            return 200;
        }
        
        int base = 80;
        if (user.getAttribute(Attribute.animism) >= 1) {
            base = 120 + (user.getArousal().getReal() / 10);
        }
        return Math.round(Math.max(Math.min(150,
                        2.5f * (user.getAttribute(Attribute.power) - c.getOpponent(user).getAttribute(Attribute.power)) + base),
                        40));
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (user.getAttribute(Attribute.animism) >= 1) {
            return "Pounce";
        } else {
            return getName(c, user);
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return "You let your instincts take over and you pounce on " + target.getName()
                            + " like a predator catching your prey.";
        } else if (modifier == Result.normal) {
            return "You tackle " + target.getName() + " to the ground and straddle her.";
        } else {
            return "You lunge at " + target.getName() + ", but she dodges out of the way.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return String.format("%s wiggles her butt cutely before leaping at %s and pinning %s to the floor.",
                            user.subject(), target.nameDirectObject(), target.directObject());
        }
        if (modifier == Result.miss) {
            return String.format("%s tries to tackle %s, but %s %s out of the way.",
                            user.subject(), target.nameDirectObject(),
                            target.pronoun(), target.action("sidestep"));
        } else {
            return String.format("%s bowls %s over and sits triumphantly on %s chest.",
                            user.subject(), target.nameDirectObject(), target.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Knock opponent to ground and get on top of her";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
