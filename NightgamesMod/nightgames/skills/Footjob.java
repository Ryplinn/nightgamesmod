package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.BodyFetish;

public class Footjob extends Skill {

    public Footjob() {
        super("Footjob");
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
        return (target.hasDick() || target.hasPussy()) && c.getStance().feet(user, target) && target.crotchAvailable()
                        && c.getStance().prone(user) != c.getStance().prone(target) && user.canAct()
                        && !c.getStance().inserted() && user.outfit.hasNoShoes();
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
    public int accuracy(Combat c, Character user, Character target) {
        return target.body.getFetish("feet").isPresent() ? 200 : 80;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            int m = Random.random(12, 20);
            if (user.human()) {
                c.write(user, Formatter.format(deal(c, m, Result.normal, user, target), user, target));
            } else if (c.shouldPrintReceive(target, c)) {
                c.write(user, Formatter.format(receive(c, m, Result.normal, user, target), user, target));
            }
            if (target.hasDick()) {
                target.body.pleasure(user, user.body.getRandom("feet"), target.body.getRandom("cock"), m, c, new SkillUsage<>(this, user, target));
            } else {
                target.body.pleasure(user, user.body.getRandom("feet"), target.body.getRandom("pussy"), m, c, new SkillUsage<>(this, user, target));
            }
            if (Random.random(100) < 15 + 2 * user.getAttribute(Attribute.fetishism)) {
                target.add(c, new BodyFetish(target.getType(), user.getType(), "feet", .25));
            }
        } else {
            if (user.human()) {
                c.write(user, Formatter.format(deal(c, 0, Result.miss, user, target), user, target));
            } else if (c.shouldPrintReceive(target, c)) {
                c.write(user, Formatter.format(receive(c, 0, Result.miss, user, target), user, target));
            }
            return false;
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
        if (modifier == Result.miss) {
            return "You attempt to place your foot between " + target.nameOrPossessivePronoun() + " legs, but "
                            + target.pronoun() + " moves away at the last second.";
        } else {
            String message = "";
            if (target.hasDick()) {
                message = "You press your foot against {other:name-possessive} girl-cock and stimulate it by rubbing it up and down with the sole of your foot, occasionally teasing the head with your toes. {other:POSSESSIVE} {other:body-part:cock}";
                if (target.getArousal().percent() < 30) {
                    message += "starts to get hard.";
                } else if (target.getArousal().percent() < 60) {
                    message += "throbs between your soles.";
                } else {
                    message += "is practically leaking pre-cum all over your soles.";
                }
            } else if (target.hasPussy()) {
                message = "You rub your foot against " + target.getName()
                                + "'s pussy lips while rubbing {other:possessive} clit with your big toe. ";
                if (target.getArousal().percent() < 30) {
                    message += "The wetness from {other:possessive} excitement starts to coat the underside of your foot.";
                } else if (target.getArousal().percent() < 60) {
                    message += "{other:POSSESSIVE} {other:body-part:pussy} is so wet, your foot easily glides along {other:possessive} parted lips.";
                } else {
                    message += "{other:PRONOUN} is so wet that your toes briefly slip inside of {other:direct-object} before pulling them out to tease {other:direct-object} further.";
                }
            }
            return message;
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s swings %s foot at %s groin, but misses.",
                            user.subject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun());
        } else {
            if (target.hasDick()) {
                return String.format("%s rubs %s dick with the sole of %s soft foot. From time to time,"
                                + " %s teases %s by pinching the glans between %s toes and jostling %s balls.",
                                user.subject(), target.nameOrPossessivePronoun(), user.possessiveAdjective(),
                                user.subject(), target.directObject(), user.possessiveAdjective(),
                                target.possessiveAdjective());
            } else if (target.hasPussy()) {
                return String.format("%s teases the lips of %s slit with %s foot. From time "
                                + "to time, %s teases %s by slipping %s big toe inside and wiggling it around.",
                                user.subject(), target.nameOrPossessivePronoun(), user.possessiveAdjective(),
                                user.subject(), target.directObject(), user.possessiveAdjective());
            }
            return String.format("%s teases %s asshole with %s foot. From time to time, %s "
                            + "teases %s by pressing %s big toe at %s sphincter and nudging it.",
                            user.subject(), target.nameOrPossessivePronoun(), user.possessiveAdjective(),
                            user.subject(), target.directObject(), user.possessiveAdjective(),
                            target.possessiveAdjective());
        }
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
