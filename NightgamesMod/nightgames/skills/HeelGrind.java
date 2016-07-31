package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.stance.BehindFootjob;
import nightgames.stance.Stance;
import nightgames.status.BodyFetish;

public class HeelGrind extends Skill {
    public HeelGrind(Character self) {
        super("Heel Grind", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Seduction) >= 22;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return c.getStance().behind(getSelf()) && target.crotchAvailable() && getSelf().canAct()
                        && !c.getStance().vaginallyPenetrated(target) && target.hasPussy()
                        && getSelf().outfit.hasNoShoes();
    }

    @Override
    public float priorityMod(Combat c) {
        BodyPart feet = getSelf().body.getRandom("feet");
        Character other = c.p1 == getSelf() ? c.p2 : c.p1;
        BodyPart otherpart = other.hasDick() ? other.body.getRandomCock() : other.body.getRandomPussy();
        if (feet != null) {
            return (float) Math.max(0, feet.getPleasure(getSelf(), otherpart) - 1);
        }
        return 0;
    }

    @Override
    public int getMojoBuilt(Combat c) {
        return 20;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        int m = 12 + Global.random(6);
        int m2 = m / 2;
        if (getSelf().human()) {
            c.write(getSelf(), deal(c, m, Result.normal, target));
        } else if (target.human()) {
            c.write(getSelf(), receive(c, m, Result.normal, target));
        }
        target.body.pleasure(getSelf(), getSelf().body.getRandom("feet"), target.body.getRandom("pussy"), m, c, this);
        target.body.pleasure(getSelf(), getSelf().body.getRandom("hands"), target.body.getRandom("breasts"), m2, c, this);
        if (c.getStance().en != Stance.behindfootjob) {
            c.setStance(new BehindFootjob(getSelf(), target));
        }
        if (Global.random(100) < 15 + 2 * getSelf().get(Attribute.Fetish)) {
            target.add(c, new BodyFetish(target, getSelf(), "feet", .25));
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new HeelGrind(user);
    }

    @Override
    public int speed() {
        return 4;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return Global.format(
                        "You wrap your legs around {other:name-possessive} waist and press your heel gently into {other:possessive} cunt. Locking your ankles to keep {other:possessive} held in place, you start to gently gyrate your heel against {other:possessive} wet lips. Cupping each of {other:possessive} {other:body-part:breasts} with your hands, you start to pull and play with {other:name-possessive} nipples between your fingers. Your heel now coated in {other:possessive} wetness, you apply even more pressure and speed as you feel {other:subject} starting to hump it on {other:possessive} own.",
                        getSelf(), target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return Global.format(
                        "{self:subject} wraps {self:possessive} legs around your waist and presses {self:possessive} soft heel against your pussy eliciting a gasp. {self:PRONOUN} grins at your reaction while locking {self:possessive} feet on top of each other to keep you from escaping {self:possessive} assault. At the same time, you feel {self:possessive} start to gently tweak and pinch your nipples. Flushed and dripping with arousal, you feel your body helplessly grinding {self:possessive} soaked heel, which starts to sink into your cunt bit by bit.",
                        getSelf(), target);
    }

    @Override
    public String describe(Combat c) {
        return "Pleasure your opponent with your feet";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
