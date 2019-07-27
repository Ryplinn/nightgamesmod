package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.stance.Stance;
import nightgames.stance.StandingOver;
import nightgames.status.BodyFetish;
import nightgames.status.CockBound;
import nightgames.status.Stsflag;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionSymptom;
import nightgames.status.addiction.AddictionType;

import java.util.Optional;

public class PullOut extends Skill {

    PullOut() {
        super("Pull Out");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.hasStatus(Stsflag.knotted) && user.canAct() && (c.getStance().isFaceSitting(user)
                        || c.getStance().havingSex(c, user) && c.getStance().dom(user)) && permittedByAddiction(
                        user);
    }

    static boolean permittedByAddiction(Character user) {
        if (!user.human()) {
            return true;
        }
        Optional<Addiction> addiction = user.getAnyAddiction(AddictionType.BREEDER);
        if (!addiction.isPresent()) {
            return true;
        }
        Addiction add = addiction.get();
        return !add.atLeast(Addiction.Severity.HIGH) && !add.activeTracker().map(AddictionSymptom::getCombatSeverity)
                        .map(severity -> severity.atLeast(Addiction.Severity.HIGH)).orElse(false);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        Result result = Result.normal;
        if (c.getStance().inserted(user)) {
            if (c.getStance().en == Stance.anal) {
                result = Result.anal;
            }
        } else if (c.getStance().inserted(target)) {
            result = Result.reverse;
        } else if (c.getStance().isFacesatOn(target)) {
            result = Result.special;
        }
        boolean isLocked = user.hasStatus(Stsflag.leglocked) || user.hasStatus(Stsflag.armlocked);
        int baseDifficulty = isLocked ? 17 : 10;
        if (target.has(Trait.stronghold)) {
            baseDifficulty += 5;
        }
        int powerMod = Math.min(20, Math.max(5, target.getAttribute(Attribute.power) - user.getAttribute(Attribute.power)));
        if (c.getStance().en == Stance.anal) {
            if (target.has(Trait.bewitchingbottom)) {
                Optional<BodyFetish> fetish = user.body.getFetish("ass");
                if(fetish.isPresent()) {
                    baseDifficulty += 7 * fetish.get().magnitude;
                }
            }
            if (!target.has(Trait.powerfulcheeks)) {
                writeOutput(c, result, user, target);
                c.getStance().insertRandom(c).ifPresent(c::setStance);
                return true;
            } else if (user.checkVsDc(Attribute.power,
                            baseDifficulty - user.getEscape(c, target) + powerMod)) {
                if (isLocked) {
                    c.write(user, Formatter.format("Despite {other:name-possessive} inhumanly tight"
                                    + " ass and {other:possessive} strong grip on {self:direct-object},"
                                    + " {self:pronoun-action:manage|manages} to pull {self:body-part:cock}"
                                    + " ever so slowly out of {other:direct-object}.", user, target));
                } else {
                    c.write(user, Formatter.format("{other:NAME-POSSESSIVE} ass clenches powerfully"
                                    + " around {self:name-possessive} {self:body-part:cock} as"
                                    + " {self:pronoun-action:try|tries} to pull out of"
                                    + " it, but it proves insufficient as the hard shaft escapes its"
                                    + " former prison.", user, target));
                }
                c.getStance().insertRandom(c).ifPresent(c::setStance);
            } else if (!isLocked) {
                c.write(user, Formatter.format("{self:SUBJECT-ACTION:try|tries} to pull out of"
                                + " {other:name-possessive} lustrous ass, but {other:pronoun-action:squeeze|squeezes}"
                                + " {other:possessive} asscheeks tightly around your {self:body-part:cock},"
                                + " preventing your extraction.", user, target));
                user.body.pleasure(target, target.body.getRandomAss(), user.body.getRandomCock(), 6, c, new SkillUsage<>(this, user, target));
            } else {
                String lockDesc = user.hasStatus(Stsflag.leglocked) ? "legs" : "arms";
                c.write(user, Formatter.format("{self:SUBJECT-ACTION:try|tries} to pull out of"
                                + " {other:name-possessive} lustrous ass, but the combination"
                                + " of {other:possessive} tightly squeezing ass and"
                                + " powerful %s locks {self:pronoun} firmly inside of {other:direct-object}."
                                , user, target, lockDesc));
                user.body.pleasure(target, target.body.getRandomAss(), user.body.getRandomCock(), 10, c, new SkillUsage<>(this, user, target));
            }
        } else if (result == Result.special) {
            writeOutput(c, Result.special, user, target);
            c.setStance(new StandingOver(user.getType(), target.getType()), user, true);
        } else {
            if (isLocked || target.has(Trait.tight) && c.getStance().inserted(user)) {
                boolean escaped = user.checkVsDc(Attribute.power,
                                10 - user.getEscape(c, target) + target.getAttribute(Attribute.power));
                if (escaped) {
                    writeOutput(c, result, user, target);
                } else {
                    if (user.hasStatus(Stsflag.leglocked)) {
                        BodyPart part = c.getStance().anallyPenetrated(c, user) ? target.body.getRandom("ass")
                                        : target.body.getRandomPussy();
                        String partString = part.describe(target);
                        if (user.human()) {
                            c.write(user, "You try to pull out of " + target.getName() + "'s " + partString
                                            + ", but her legs immediately tighten against your waist, holding you inside her. "
                                            + "The mere friction from her action sends a shiver down your spine.");
                        } else {
                            c.write(user, String.format("%s tries to pull out of %s %s, but %s legs immediately pull"
                                            + " %s back in, holding %s inside %s.", user.subject(), target.nameOrPossessivePronoun(),
                                            partString, target.possessiveAdjective(), user.directObject(), user.nameDirectObject(),
                                            target.directObject()));
                        }
                    } else if (user.hasStatus(Stsflag.armlocked)) {
                        if (user.human()) {
                            c.write(user, "You try to pull yourself off of " + target.getName()
                                            + ", but she merely pulls you back on top of her, surrounding you in her embrace.");
                        } else {
                            c.write(user, String.format("%s tries to pull %s off of %s, but with "
                                            + "a gentle pull of %s hands, %s collapses back on top of %s.",
                                            user.subject(), user.reflectivePronoun(),
                                            target.nameDirectObject(), target.possessiveAdjective(),
                                            user.pronoun(), target.directObject()));
                        }
                    } else if (target.has(Trait.tight) && c.getStance().inserted(user)) {
                        BodyPart part = c.getStance().anallyPenetrated(c, target) ? target.body.getRandom("ass")
                                        : target.body.getRandomPussy();
                        String partString = part.describe(target);
                        if (user.human()) {
                            c.write(user, "You try to pull yourself out of " + target.getName() + "'s " + partString
                                            + ", but she clamps down hard on your cock while smiling at you. You almost cum from the sensation, and quickly abandon ideas about your escape.");
                        } else {
                            c.write(user, String.format("%s tries to pull %s out of %s %s, but %s down "
                                            + "hard on %s cock, and prevent %s from pulling out.", user.subject(),
                                            user.reflectivePronoun(), target.possessiveAdjective(), partString,
                                            target.subjectAction("pull"), target.possessiveAdjective(),
                                            user.directObject()));
                        }
                    }
                    int m = 8;
                    if (c.getStance().inserted(user)) {
                        BodyPart part = c.getStance().anallyPenetrated(c, target) ? target.body.getRandom("ass")
                                        : target.body.getRandomPussy();
                        user.body.pleasure(target, part, user.body.getRandomInsertable(), m, c, new SkillUsage<>(this, user, target));
                    }
                    user.struggle();
                    return false;
                }
            } else if (user.hasStatus(Stsflag.cockbound)) {
                CockBound s = (CockBound) user.getStatus(Stsflag.cockbound);
                c.write(user, String.format("%s to pull out of %s %s, but %s instantly reacts "
                                + "and pulls %s dick back in.", user.subjectAction("try", "tries"),
                                target.nameOrPossessivePronoun(), 
                                target.body.getRandomPussy().describe(target),
                                s.binding, user.possessiveAdjective()));
                int m = 8;
                user.body.pleasure(target, target.body.getRandom("pussy"), user.body.getRandom("cock"), m, c, new SkillUsage<>(this, user, target));
                return false;
            } else 
                writeOutput(c, result, user, target);
            c.getStance().insertRandom(c).ifPresent(c::setStance);
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.reverse) {
            return "You rise up and let " + target.nameOrPossessivePronoun() + " girl-cock slip out of your "
                            + (c.getStance().en == Stance.anal ? "ass." : "pussy");
        } else if (modifier == Result.anal) {
            return "You pull your dick completely out of " + target.getName() + "'s ass.";
        } else if (modifier == Result.normal) {
            return "You pull completely out of " + target.getName()
                            + "'s pussy, causing her to let out a disappointed little whimper.";
        } else {
            return "You pull yourself off " + target.getName()
                            + "'s face, causing her to gasp lungfuls of the new fresh air offer to her.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.anal) {
            return String.format("%s the pressure in %s anus recede as %s pulls out.",
                            target.subjectAction("feel"), target.possessiveAdjective(),
                            user.subject());
        } else if (modifier == Result.reverse) {
            return String.format("%s lifts %s hips more than normal, letting %s dick slip completely out of %s.",
                            user.subject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), user.directObject());
        } else if (modifier == Result.normal) {
            return String.format("%s pulls %s dick completely out of %s pussy, leaving %s feeling empty.",
                            user.subject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), target.directObject());
        } else {
            return String.format("%s lifts herself off %s face, giving %s a brief respite.",
                            user.subject(), target.nameOrPossessivePronoun(), target.directObject());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Aborts penetration";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
