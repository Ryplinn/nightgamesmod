package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.BehindFootjob;
import nightgames.stance.Stance;
import nightgames.status.BodyFetish;

public class FootPump extends Skill {
    FootPump() {
        super("Foot Pump");
        addTag(SkillTag.usesFeet);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.dominant);
    }

    @Override public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.seduction) >= 22;
    }

    @Override public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().behind(user) && target.crotchAvailable() && user.canAct() && !c.getStance()
                        .inserted() && target.hasDick() && user.outfit.hasNoShoes();
    }

    @Override public float priorityMod(Combat c, Character user) {
        BodyPart feet = user.body.getRandom("feet");
        Character other = c.p1 == user ? c.p2 : c.p1;
        BodyPart otherpart = other.hasDick() ? other.body.getRandomCock() : other.body.getRandomPussy();
        if (feet != null) {
            return (float) Math.max(0, feet.getPleasure(user, otherpart) - 1);
        }
        return 0;
    }

    @Override public int getMojoBuilt(Combat c, Character user) {
        return 5;
    }

    @Override public boolean resolve(Combat c, Character user, Character target) {
        int m = 12 + Random.random(6);
        int m2 = m / 2;
        writeOutput(c, Result.normal, user, target);
        target.body.pleasure(user, user.body.getRandom("feet"), target.body.getRandom("cock"), m, c, new SkillUsage<>(this, user, target));
        target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandom("breasts"), m2, c, new SkillUsage<>(this, user, target));
        if (c.getStance().en != Stance.behindfootjob) {
            c.setStance(new BehindFootjob(user.getType(), target.getType()), user, true);
        }
        if (Random.random(100) < 15 + 2 * user.get(Attribute.fetishism)) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), "feet", .25));
        }
        return true;
    }

    @Override public Skill copy(Character user) {
        return new FootPump();
    }

    @Override public int speed(Character user) {
        return 4;
    }

    @Override public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "You wrap your legs around {other:name-possessive} waist and grip {other:possessive} {other:body-part:cock} between your toes. Massaging {other:name-possessive} {other:body-part:cock} between your toes, you start to stroke {other:possessive} {other:body-part:cock} up and down between your toes. Reaching around from behind {other:possessive} back, you start to tease and caress {other:possessive} breasts with your hands. Alternating between pumping and massaging the head of {other:possessive} {other:body-part:cock} with your toes, {other:pronoun} begins to let out a low moan with each additional touch.",
                        user, target);
    }

    @Override public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "{self:SUBJECT} wraps {self:possessive} legs around {other:name-possessive} waist and settles {self:possessive} "
                        + "feet on both sides of {other:possessive} {other:body-part:cock}. Cupping {other:name-possessive} "
                        + "dick with {self:possessive} arches, {self:subject} starts making long and steady strokes up and "
                        + "down {other:possessive} {other:body-part:cock} as it remains"
                        + " trapped in between {self:possessive} arches. Reaching around {other:direct-object}, {self:subject} begins to rub "
                        + "and gently flick {other:possessive} nipples with {self:possessive} fingers. Alternating between pumping and "
                        + "massaging the head of {other:possessive} {other:body-part:cock} with {self:possessive} toes {other:subject} can't help "
                        + "but groan in pleasure.",
                        user, target);
    }

    @Override public String describe(Combat c, Character user) {
        return "Pleasure your opponent with your feet";
    }

    @Override public boolean makesContact() {
        return true;
    }

}
