package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.BodyFetish;

public class FootSmother extends Skill {
    FootSmother(CharacterType self) {
        super("Foot Smother", self);
        addTag(SkillTag.usesFeet);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.dominant);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.fetishism) >= 20;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().footAvailable() && getSelf().body.has("feet") && c.getStance().mobile(getSelf())
                        && c.getStance().dom(getSelf()) && getSelf().canAct() && c.getStance().prone(target)
                        && !c.getStance().behind(getSelf()) && getSelf().outfit.hasNoShoes();
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return 150;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        int m;
        m = 8 + Random.random(6);
        if (getSelf().human()) {
            c.write(getSelf(), Formatter.format(deal(c, 0, Result.normal, target), getSelf(), target));
        } else {
            c.write(getSelf(), Formatter.format(receive(c, 0, Result.normal, target), getSelf(), target));
        }
        target.temptWithSkill(c, getSelf(), getSelf().body.getRandom("feet"), m, this);
        if (Random.random(100) < 30 + 2 * getSelf().get(Attribute.fetishism)) {
            target.add(c, new BodyFetish(target.getType(), self, "feet", .25));
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new FootSmother(user.getType());
    }

    @Override
    public int speed() {
        return 2;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return "You place the soles of your feet over top of {other:name-possessive} face and press down, keeping {other:direct-object} in place and giving {other:direct-object} no choice but to worship your feet.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
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
        return Formatter.format(message + parts + ".", getSelf(), target);
    }

    @Override
    public String describe(Combat c) {
        return "Smothers your opponent's face with your foot. Low damage but high chance of inducing fetishes.";
    }
}
