package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;

public class EngulfedFuck extends Skill {

    EngulfedFuck() {
        super("Multi Fuck");
        addTag(SkillTag.fucking);
        addTag(SkillTag.pleasureSelf);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().en == Stance.engulfed && c.getStance().dom(user)
                        && Pairing.findPairing(user, target) != Pairing.NONE;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 25;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Use all available genitals to give your opponent a proper workout.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        Pairing pair = Pairing.findPairing(user, target);
        double base = 10.0 + Math.min(20, Random
                        .random(user.getAttribute(Attribute.slime) / 3 + user.getAttribute(Attribute.seduction) / 5));
        int selfDmg = (int) ((base * pair.modPleasure(true)) / (user.has(Trait.experienced) ? 2.0 : 3.0));
        int targetDmg = (int) (base * pair.modPleasure(false));
        switch (pair) {
            case ASEX_MALE:
                c.write(user,
                                Formatter.format("{self:SUBJECT-ACTION:wrap|wraps} {other:name-possessive}"
                                                + " {other:body-part:cock} in a clump of slime molded after {self:possessive} ass"
                                                + " and {self:action:pump|pumps} it furiously.", user, target));
                target.body.pleasure(user, user.body.getRandomAss(), target.body.getRandomCock(), targetDmg,
                                c, new SkillUsage<>(this, user, target));
                user.body.pleasure(target, target.body.getRandomCock(), user.body.getRandomAss(), selfDmg, c, new SkillUsage<>(this, user, target));
                break;
            case FEMALE_HERM:
                c.write(user,
                                Formatter.format("{self:SUBJECT-ACTION:impale|impales} {self:reflective} on"
                                                + " {other:name-possessive} {other:body-part:cock} and {self:action:bounce|bounces} wildly,"
                                                + " filling {other:direct-object} with pleasure. At the same time, {self:pronoun} "
                                                + "{self:action:twiddle|twiddles} {other:possessive} clit with {self:possessive} fingers.",
                                user, target));
                target.body.pleasure(user, user.body.getRandomPussy(), target.body.getRandomCock(),
                                targetDmg / 2, c, new SkillUsage<>(this, user, target));
                target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandomPussy(),
                                targetDmg / 2, c, new SkillUsage<>(this, user, target));
                user.body.pleasure(target, target.body.getRandomCock(), user.body.getRandomPussy(), selfDmg,
                                c, new SkillUsage<>(this, user, target));
                break;
            case FEMALE_MALE:
                c.write(user,
                                Formatter.format("{self:SUBJECT-ACTION:impale|impales} {self:reflective} on"
                                                + " {other:name-possessive} {other:body-part:cock} and {self:action:bounce|bounces} wildly,"
                                                + " filling {other:direct-object} with pleasure.", user, target));
                target.body.pleasure(user, user.body.getRandomPussy(), target.body.getRandomCock(), targetDmg,
                                c, new SkillUsage<>(this, user, target));
                user.body.pleasure(target, target.body.getRandomCock(), user.body.getRandomPussy(), selfDmg,
                                c, new SkillUsage<>(this, user, target));
                break;
            case HERM_ASEX:
            case MALE_ASEX:
                c.write(user,
                                Formatter.format("Despite not having much to work with, {self:subject} still"
                                                + " {self:action:manage|manages} to make {other:subject} squeal by pounding {other:name-possessive}"
                                                + " {other:body-part:ass} with {self:possessive} {self:body-part:cock}.",
                                user, target));
                target.body.pleasure(user, user.body.getRandomCock(), target.body.getRandomAss(), targetDmg,
                                c, new SkillUsage<>(this, user, target));
                user.body.pleasure(target, target.body.getRandomAss(), user.body.getRandomCock(), selfDmg, c, new SkillUsage<>(this, user, target));
                break;
            case HERM_FEMALE:
                c.write(user,
                                Formatter.format("{self:SUBJECT-ACTION:pound|pounds} {other:name-possessive} "
                                                + "{other:body-part:pussy} with vigor, at the same time fingering {other:possessive}"
                                                + " ass.", user, target));
                target.body.pleasure(user, user.body.getRandomCock(), target.body.getRandomPussy(),
                                targetDmg / 2, c, new SkillUsage<>(this, user, target));
                target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandomAss(),
                                targetDmg / 2, c, new SkillUsage<>(this, user, target));
                user.body.pleasure(target, target.body.getRandomPussy(), user.body.getRandomCock(), selfDmg,
                                c, new SkillUsage<>(this, user, target));
                break;
            case HERM_HERM:
                c.write(user,
                                Formatter.format("It takes some clever maneuvering, but {self:SUBJECT-ACTION:manage|manages}"
                                                + " to line {other:name-do} and {self:reflective} up perfectly. When"
                                                + " {self:pronoun} {self:action:strike|strikes}, {other:possessive} {other:body-part:cock} ends up"
                                                + " in {self:possessive} {self:body-part:pussy}, and {self:body-part:cock} in {other:possessive}"
                                                + " {other:body-part:pussy}. With every twitch, both of you are filled with unimaginable pleasure,"
                                                + " so when {self:pronoun} {self:action:start|starts} fucking in earnest the sensations are"
                                                + " almost enough to cause you both to pass out.", user, target));
                target.body.pleasure(user, user.body.getRandomCock(), target.body.getRandomPussy(),
                                targetDmg / 2, c, new SkillUsage<>(this, user, target));
                target.body.pleasure(user, user.body.getRandomPussy(), target.body.getRandomCock(),
                                targetDmg / 2, c, new SkillUsage<>(this, user, target));
                user.body.pleasure(target, target.body.getRandomPussy(), user.body.getRandomCock(),
                                selfDmg / 2, c, new SkillUsage<>(this, user, target));
                user.body.pleasure(target, target.body.getRandomCock(), user.body.getRandomPussy(),
                                selfDmg / 2, c, new SkillUsage<>(this, user, target));
                break;
            case HERM_MALE:
                c.write(user,
                                Formatter.format("{self:SUBJECT-ACTION:lower|lowers} {self:possessive}"
                                                + " {self:body-part:pussy} down on {other:name-possessive} {other:body-part:cock}."
                                                + " While slowly fucking {other:direct-object}, {self:pronoun} {self:action:move|moves}"
                                                + " {self:possessive} {self:body-part:cock} to the entrance of {other:possessive} ass."
                                                + " Before {other:pronoun} {other:action:have|has} a chance to react, {self:pronoun}"
                                                + " {self:action:shove|shoves} it up there in one thrust, fucking {other:direct-object}"
                                                + " from both sides.", user, target));
                target.body.pleasure(user, user.body.getRandomCock(), target.body.getRandomAss(),
                                targetDmg / 2, c, new SkillUsage<>(this, user, target));
                target.body.pleasure(user, user.body.getRandomPussy(), target.body.getRandomCock(),
                                targetDmg / 2, c, new SkillUsage<>(this, user, target));
                user.body.pleasure(target, target.body.getRandomPussy(), user.body.getRandomCock(),
                                selfDmg / 2, c, new SkillUsage<>(this, user, target));
                user.body.pleasure(target, target.body.getRandomCock(), user.body.getRandomAss(), selfDmg / 2,
                                c, new SkillUsage<>(this, user, target));
                break;
            case MALE_FEMALE:
            case MALE_MALE:
            case MALE_HERM:
                BodyPart bpart = pair == Pairing.MALE_MALE ? target.body.getRandomAss() : target.body.getRandomPussy();
                int realTargetDmg = targetDmg;
                String msg = "{self:SUBJECT-ACTION:place|places} {self:possessive}"
                                + " {self:body-part:cock} at {other:name-possessive} " + bpart.describe(target)
                                + " and thrust in all the way"
                                + " in a single movement of {self:possessive} hips. {self:PRONOUN} "
                                + "{self:action:proceed|proceeds} to piston in and out at a furious pace.";
                if (pair == Pairing.MALE_HERM) {
                    msg += " At the same time, {self:pronoun} {self:action:use:uses} both of {self:possessive}"
                                    + " hands to pump {other:possessive} {other:body-part:cock}.";
                }
                c.write(user, Formatter.format(msg, user, target));
                if (pair == Pairing.MALE_HERM) {
                    target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandomCock(),
                                    targetDmg / 2, c, new SkillUsage<>(this, user, target));
                    realTargetDmg /= 2;
                }
                target.body.pleasure(user, user.body.getRandomCock(), bpart, realTargetDmg, c, new SkillUsage<>(this, user, target));
                user.body.pleasure(target, bpart, user.body.getRandomCock(), selfDmg, c, new SkillUsage<>(this, user, target));
                break;
            default:
                throw new IllegalArgumentException("EngulfedFuck had pairing NONE or default");
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    private enum Pairing {
        FEMALE_MALE(1, 1),
        FEMALE_HERM(1, 1.5),
        MALE_MALE(1, 1),
        MALE_FEMALE(1, 1),
        MALE_HERM(1, 1.5),
        MALE_ASEX(1, .75),
        HERM_MALE(1.25, 1),
        HERM_FEMALE(1.25, 1),
        HERM_HERM(1.25, 1.5),
        HERM_ASEX(1.25, .75),
        ASEX_MALE(.6, 1),
        NONE(0, 0);

        private final double selfMod, targetMod;

        Pairing(double selfMod, double targetMod) {
            this.selfMod = selfMod;
            this.targetMod = targetMod;
        }

        double modPleasure(boolean slime) {
            return slime ? selfMod : targetMod;
        }

        static Pairing findPairing(Character self, Character target) {
            if (herm(self)) {
                if (herm(target)) {
                    return HERM_HERM;
                } else if (target.hasPussy()) {
                    return HERM_FEMALE;
                } else if (target.hasDick()) {
                    return HERM_MALE;
                }
                return HERM_ASEX;
            } else if (self.hasPussy()) {
                if (herm(target)) {
                    return FEMALE_HERM;
                } else if (target.hasPussy()) {
                    return NONE;
                } else if (target.hasDick()) {
                    return FEMALE_MALE;
                }
            } else if (self.hasDick()) {
                if (herm(target)) {
                    return MALE_HERM;
                } else if (target.hasPussy()) {
                    return MALE_FEMALE;
                } else if (target.hasDick()) {
                    return MALE_MALE;
                }
                return MALE_ASEX;
            } else if (target.hasDick()) {
                return ASEX_MALE;
            }
            return NONE;
        }

        private static boolean herm(Character ch) {
            return ch.hasDick() && ch.hasPussy();
        }
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
