package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.BodyFetish;

public class FootSmother extends Skill {
    FootSmother() {
        super("Foot Smother");
        addTag(SkillTag.usesFeet);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.dominant);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.fetishism) >= 20;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.footAvailable() && user.body.has("feet") && c.getStance().mobile(user)
                        && c.getStance().dom(user) && user.canAct() && c.getStance().prone(target)
                        && !c.getStance().behind(user) && user.outfit.hasNoShoes();
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 150;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int m;
        m = 8 + Random.random(6);
        if (user.human()) {
            c.write(user, Formatter.format(deal(c, 0, Result.normal, user, target), user, target));
        } else {
            c.write(user, Formatter.format(receive(c, 0, Result.normal, user, target), user, target));
        }
        target.temptWithSkill(c, user, user.body.getRandom("feet"), m, this);
        if (Random.random(100) < 30 + 2 * user.get(Attribute.fetishism)) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), "feet", .25));
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new FootSmother();
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You place the soles of your feet over top of {other:name-possessive} face and press down, keeping {other:direct-object} in place and giving {other:direct-object} no choice but to worship your feet.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String message = "{self:SUBJECT} presses {self:possessive} soles in to {other:name-possessive} "
                        + "face causing {other:direct-object} to "
                        + "inhale {self:possessive} scent deeply. As {other:subject-action:start|starts} to worship {self:possessive}"
                        + " {self:body-part:feet}, ";
        String parts = "";
        if (target.hasDick()) {
            if (target.getArousal().percent() < 30) {
                parts += "{other:possessive} {other:body-part:cock} starts to twitch";
            } else if (target.getArousal().percent() < 60) {
                parts += "{other:possessive} {other:body-part:cock} starts to throb";
            } else {
                parts += "{other:possessive} {other:body-part:cock} start to leak " + target.body.getRandomCock().getFluids(target);
            }
        }
        if (target.hasPussy()) {
            if (parts.length() > 0) {
                parts += " and ";
            }
            if (target.getArousal().percent() < 30) {
                parts += "{other:pronoun-action:feel|feels} {other:reflective} start to get wet";
            } else if (target.getArousal().percent() < 60) {
                parts += "{other:pronoun-action:feel|feels} {other:possessive}"
                                + " wetness start to run down {other:possessive} leg";
            } else {
                parts += "{other:possessive} {other:body-part:pussy} starts to spasm as {other:possessive} "
                                + target.body.getRandomPussy().getFluids(target) + " puddles underneath {other:direct-object}";
            }
        }
        return Formatter.format(message + parts + ".", user, target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Smothers your opponent's face with your foot. Low damage but high chance of inducing fetishes.";
    }
}
