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

public class HeelGrind extends Skill {
    HeelGrind() {
        super("Heel Grind");
        addTag(SkillTag.usesFeet);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.dominant);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 22;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().behind(user) && target.crotchAvailable() && user.canAct()
                        && !c.getStance().vaginallyPenetrated(c, target) && target.hasPussy()
                        && user.outfit.hasNoShoes();
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        BodyPart feet = user.body.getRandom("feet");
        Character other = c.p1 == user ? c.p2 : c.p1;
        BodyPart otherpart = other.hasDick() ? other.body.getRandomCock() : other.body.getRandomPussy();
        if (feet != null) {
            return (float) Math.max(0, feet.getPleasure(user, otherpart) - 1);
        }
        return 0;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 15;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int m = 12 + Random.random(6);
        int m2 = m / 2;
        writeOutput(c, Result.normal, user, target);
        target.body.pleasure(user, user.body.getRandom("feet"), target.body.getRandom("pussy"), m, c, new SkillUsage<>(this, user, target));
        target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandom("breasts"), m2, c, new SkillUsage<>(this, user, target));
        if (c.getStance().en != Stance.behindfootjob) {
            c.setStance(new BehindFootjob(user.getType(), target.getType()), user, true);
        }
        if (Random.random(100) < 15 + 2 * user.getAttribute(Attribute.fetishism)) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), "feet", .25));
        }
        return true;
    }

    @Override
    public int speed(Character user) {
        return 4;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "You wrap your legs around {other:name-possessive} waist and press your heel gently into {other:possessive} cunt. Locking your ankles to keep {other:possessive} held in place, you start to gently gyrate your heel against {other:possessive} wet lips. Cupping each of {other:possessive} {other:body-part:breasts} with your hands, you start to pull and play with {other:name-possessive} nipples between your fingers. Your heel now coated in {other:possessive} wetness, you apply even more pressure and speed as you feel {other:subject} starting to hump it on {other:possessive} own.",
                        user, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "{self:subject} wraps {self:possessive} legs around {other:name-possessive} waist and "
                        + "presses {self:possessive} soft heel against {other:possessive} pussy, eliciting a gasp. "
                        + "{self:SUBJECT} grins at {other:name-possessive} reaction while locking {self:possessive} feet "
                        + "on top of each other to keep {other:direct-object} from escaping {self:possessive} assault. "
                        + "At the same time, {other:subject-action:feel|feels} {self:name-possessive} start to gently "
                        + "tweak and pinch {other:possessive} nipples. Flushed and dripping with arousal, "
                        + "{other:subject-action:feel|feels} {other:possessive} body helplessly "
                        + "grinding {self:possessive} soaked heel, which starts to sink into {other:possessive} cunt bit by bit.",
                        user, target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Pleasure your opponent with your feet";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
