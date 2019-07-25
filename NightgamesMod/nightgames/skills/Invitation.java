package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.status.ArmLocked;
import nightgames.status.LegLocked;
import nightgames.status.addiction.AddictionType;

public class Invitation extends Skill {
    private static final String divineStringFemale = "Goddess's Invitation";
    private static final String divineStringMale = "God's Invitation";

    public Invitation() {
        super("Invitation", 6);
        addTag(SkillTag.fucking);
        addTag(SkillTag.positioning);
        addTag(SkillTag.petDisallowed);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return user.has(Trait.submissive) ? 2 : 0;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) > 25 || user.has(Trait.submissive);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        boolean insertable = c.getStance().insert(c, user, user).isPresent()
                        || c.getStance().insert(c, target, user).isPresent();
        boolean stanceInsertable = c.getStance().insertRandomDom(c, target).isPresent();
        return stanceInsertable && c.getStance().distance() < 2 && insertable && user.canRespond() && user.crotchAvailable() && target.crotchAvailable()
                        && (user.hasDick() && target.hasPussy() || user.hasPussy() && target.hasDick()) && !target.isPet() && target.canRespond();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        //Free if user is Kat and player has Breeder
        Character opp = c.getOpponent(user);
        if (user.has(Trait.breeder) && opp.checkAddiction(AddictionType.BREEDER, user))
            return 0;
        return 50;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Invites opponent into your embrace";
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            if (hasDivinity(user)) {
                return Formatter.format(
                                "You command {other:name} to embrace you. {other:SUBJECT} moves to walk towards you for a second before snapping out of it.",
                                user, target);
            }
            return Formatter.format("You try to hug {other:name} and pull her down, but she twists out of your grasp.\n",
                            user, target);
        } else if (!c.getStance().inserted(user)) {
            if (hasDivinity(user)) {
                return Formatter.format(
                                "You command {other:name} to embrace you. {other:SUBJECT} obeys and hugs you close to {other:direct-object}. You follow up on your earlier command and tell her to fuck you, which she promptly lovingly complies.",
                                user, target);
            }
            return Formatter.format(
                            "You embrace {other:name} and smoothly slide her cock into your folds while she's distracted. You then pull her to the ground on top of you and softly wrap your legs around her waist",
                            user, target);
        } else {
            if (hasDivinity(user)) {
                return Formatter.format(
                                "You command {other:name} to embrace you. {other:SUBJECT} obeys and hugs you close to {other:direct-object}. You follow up on your earlier command and tell her to fuck you, which she promptly lovingly complies.",
                                user, target);
            }
            return Formatter.format(
                            "You embrace {other:name} and pull her on top of you. Taking advantage of her distraction, you push her on top of you while you are fucking her from beneath.",
                            user, target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            if (hasDivinity(user)) {
                return Formatter.format(
                                "{self:SUBJECT} commands {other:direct-object} to embrace {self:direct-object}. {other:SUBJECT} move to walk towards {self:direct-object} for a brief second before snapping out of it.",
                                user, target);
            }
            return Formatter.format(
                            "{self:NAME} hugs {other:name-do} softly and tries to pull {other:direct-object} into {self:direct-object}, but {other:pronoun-action:come|comes} to {other:possessive} senses in the nick of time and manage to twist out of {self:possessive} grasp, causing {self:NAME} to pout at {other:direct-object} cutely.\n",
                            user, target);
        } else if (!c.getStance().inserted(user)) {
            if (hasDivinity(user)) {
                return Formatter.format(
                                "{self:SUBJECT} commands {other:name-do} to embrace {self:direct-object}. {other:SUBJECT-ACTION:obey|obeys} and {other:action:hug|hugs} {self:direct-object} close to {other:reflective}. {self:NAME} follows up on {self:possessive} earlier command and tells {other:name-do} to fuck {self:direct-object}, to which {other:pronoun} promptly, lovingly {other:action:comply|complies}.",
                                user, target);
            }
            return Formatter.format(
                            "{self:NAME} embraces {other:name-do} and smoothly slides {other:possessive} cock into {self:possessive} folds while {other:pronoun-action:are|is} distracted. {self:PRONOUN} then pulls {other:direct-object} to the ground on top of {self:direct-object} and softly wraps {self:possessive} legs around {other:possessive} waist preventing {other:possessive} escape.",
                            user, target);
        } else {
            if (hasDivinity(user)) {
                return Formatter.format(
                                "{self:SUBJECT} commands {other:direct-object} to embrace {self:direct-object}. {other:SUBJECT-ACTION:obey|obeys} and {other:action:hug|hugs} {self:direct-object} close to {other:reflective}. {self:NAME} follows up on {self:possessive} earlier command and tells {other:name-do} to fuck {self:direct-object}, to which {other:pronoun} promptly, lovingly {other:action:comply|complies}.",
                                user, target);
            }
            return Formatter.format(
                            "{self:NAME} embraces {other:name-do} and pulls {other:direct-object} on top of {self:direct-object}. Taking advantage of {other:possessive} distraction, {self:subject} pushes {other:name-do} above {self:direct-object} with {self:direct-object} fucking {other:direct-object} from underneath.",
                            user, target);
        }
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int difficulty = target.getLevel() - target.getArousal().get() * 10 / target.getArousal().max()
                        + target.getAttribute(Attribute.seduction);
        int strength = user.getLevel() + user.getAttribute(Attribute.seduction)
                        * (user.has(Trait.submissive) ? 2 : 1) * (hasDivinity(user) ? 2 : 1);

        boolean success = Random.random(Math.min(Math.max(difficulty - strength, 1), 10)) == 0;
        Result result = Result.normal;
        if (!success) {
            result = Result.miss;
        } else if (hasDivinity(user)) {
            result = Result.divine;
        }

        if (success) {
            c.setStance(c.getStance().insertRandomDom(c, target).orElse(c.getStance()), user, user.canMakeOwnDecision());
        }

        if (user.human()) {
            c.write(user, deal(c, 0, result, user, target));
        } else {
            c.write(user, receive(c, 0, result, user, target));
        }
        if (success) {
            if (c.getStance().en == Stance.missionary) {
                target.add(c, new LegLocked(target.getType(), 4 * user.getAttribute(Attribute.power)));
            } else {
                target.add(c, new ArmLocked(target.getType(), 4 * user.getAttribute(Attribute.power)));
            }
            new Thrust().resolve(c, target, user);
            if (hasDivinity(user)) {
                user.usedAttribute(Attribute.divinity, c, .5);
            }
        }
        return success;
    }

    private boolean hasDivinity(Character user) {
        return user.getAttribute(Attribute.divinity) >= 25;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (hasDivinity(user)) {
            return user.hasPussy() ? divineStringFemale : divineStringMale;
        } else {
            return "Invitation";
        }
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
