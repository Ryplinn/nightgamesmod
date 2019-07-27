package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.Staleness;
import nightgames.stance.Stance;
import nightgames.status.BodyFetish;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionSymptom;
import nightgames.status.addiction.AddictionType;

public class Thrust extends Skill {
    public Thrust(String name) {
        // thrust skills become stale very slowly and recovers pretty fast
        this(name, Staleness.build().withDecay(.05).withDefault(1.0).withRecovery(.10).withFloor(.5));
    }
    public Thrust(String name, Staleness staleness) {
        super(name, 0 , staleness);
        addTag(SkillTag.fucking);
        addTag(SkillTag.thrusting);
        addTag(SkillTag.pleasureSelf);
    }

    public Thrust() {
        this("Thrust");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return !user.has(Trait.temptress) || user.getAttribute(Attribute.technique) < 11;
    }

    protected boolean havingSex(Combat c, Character user, Character target) {
        return getSelfOrgan(c, user, target) != null && getTargetOrgan(c, user, target) != null && user.canRespond()
                        && (c.getStance().havingSexOtherNoStrapped(c, user)
                                        || c.getStance().partsForStanceOnly(c, user, target).stream().anyMatch(part -> part.isType("cock")));
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return havingSex(c, user, target) && c.getStance().canthrust(c, user);
    }

    public BodyPart getSelfOrgan(Combat c, Character user, Character target) {
        if (c.getStance().penetratedBy(c, target, user)) {
            return user.body.getRandomInsertable();
        } else if (c.getStance().anallyPenetratedBy(c, user, target)) {
            return user.body.getRandom("ass");
        } else if (c.getStance().vaginallyPenetratedBy(c, user, target)) {
            return user.body.getRandomPussy();
        } else {
            return null;
        }
    }

    public BodyPart getTargetOrgan(Combat c, Character user, Character target) {
        if (c.getStance().penetratedBy(c, user, target)) {
            return target.body.getRandomInsertable();
        } else if (c.getStance().anallyPenetratedBy(c, target, user)) {
            return target.body.getRandom("ass");
        } else if (c.getStance().vaginallyPenetratedBy(c, target, user)) {
            return target.body.getRandomPussy();
        }
        return null;
    }

    public int[] getDamage(Combat c, Character user, Character target) {
        int[] results = new int[2];

        int m = 8 + Random.random(11);
        if (c.getStance().anallyPenetrated(c, target) && user.has(Trait.assmaster)) {
            m *= 1.5;
        }

        float mt = Math.max(1, m / 3.f);

        if (user.has(Trait.experienced)) {
            mt = Math.max(1, mt * .66f);
        }
        mt = target.modRecoilPleasure(c, mt);

        if (user.checkAddiction(AddictionType.BREEDER, target)) {
            float bonus = .3f * user.getAddiction(AddictionType.BREEDER, target).flatMap(Addiction::activeTracker)
                            .map(AddictionSymptom::getCombatSeverity).map(Enum::ordinal).orElse(0);
            mt += mt * bonus;
        }
        if (target.checkAddiction(AddictionType.BREEDER, user)) {
            float bonus = .3f * target.getAddiction(AddictionType.BREEDER, user).flatMap(Addiction::activeTracker)
                            .map(AddictionSymptom::getCombatSeverity).map(Enum::ordinal).orElse(0);
            m += m * bonus;
        }
        results[0] = m;
        results[1] = (int) mt;

        return results;
    }

    // FIXME: During sims characters with no insertables keep trying to thrust
    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        BodyPart selfO = getSelfOrgan(c, user, target);
        BodyPart targetO = getTargetOrgan(c, user, target);
        if (selfO == null || targetO == null) {
        	System.err.println("Something very odd happened during " + getClass().getSimpleName() + ", stance is " + c.getStance());
            System.err.println("Usable: " + usable(c, user, target));

        	System.err.println(user.save().toString());
            user.body.getCurrentParts().forEach(System.err::println);
            System.err.println(user.outfit);

        	System.err.println(target.save().toString());
            target.body.getCurrentParts().forEach(System.err::println);
            System.err.println(target.outfit);

        	System.err.print(CharacterType.lastUsedPool.dump());
        	c.write("Something very weird happened, please make a bug report with the logs.");
        	if (selfO == null && targetO == null) {
                throw new NullPointerException("null self and target organs");
            } else if (selfO == null) {
                throw new NullPointerException("null self organ");
            } else {
                throw new NullPointerException("null target organ");
            }
        }
        Result result;
        if (c.getStance().penetratedBy(c, user, c.getStance().getPartner(c, user))) {
            result = Result.reverse;
        } else if (c.getStance().en == Stance.anal) {
            result = Result.anal;
        } else {
            result = Result.normal;
        }

        writeOutput(c, result, user, target);

        int[] m = getDamage(c, user, target);
        assert m.length >= 2;

        if (m[0] != 0) {
            target.body.pleasure(user, selfO, targetO, m[0], c, new SkillUsage<>(this, user, target));
        }
        if (m[1] != 0) {
            user.body.pleasure(target, targetO, selfO, m[1], c, new SkillUsage<>(this, user, target));
        }
        if (selfO.isType("ass") && Random.random(100) < 2 + user.getAttribute(Attribute.fetishism)) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), "ass", .25));
        }
        return true;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 0;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.anal) {
            return "You thrust steadily into " + target.getName() + "'s ass, eliciting soft groans of pleasure.";
        } else if (modifier == Result.reverse) {
            return Formatter.format(
                            "You rock your hips against {other:direct-object}, riding {other:direct-object} smoothly. "
                                            + "Despite the slow pace, {other:subject} soon starts gasping and mewing with pleasure.",
                            user, target);
        } else {
            return "You thrust into " + target.getName()
                            + " in a slow, steady rhythm. She lets out soft breathy moans in time with your lovemaking. You can't deny you're feeling "
                            + "it too, but by controlling the pace, you can hopefully last longer than she can.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.anal) {
            String res;
            if (user.has(Trait.strapped)) {
                res = String.format("%s thrusts her hips, pumping her artificial cock in and out"
                                + " of %s ass and pushing on %s %s.", user.subject(),
                                target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                                target.hasBalls() ? "prostate" : "innermost parts");
                
            } else {
                res = String.format("%s cock slowly pumps the inside of %s rectum.",
                                user.nameOrPossessivePronoun(), target.nameOrPossessivePronoun());
            }
            if (user.has(Trait.assmaster)) {
                res += String.format(" %s penchant for fucking people in the ass makes "
                                + "%s thrusting that much more powerful, and that much more "
                                + "intense for the both of %s.", user.nameOrPossessivePronoun(),
                                user.possessiveAdjective(),
                                c.bothDirectObject(target));
            }
            return res;
        } else if (modifier == Result.reverse) {
            return String.format("%s rocks %s hips against %s, riding %s smoothly and deliberately. "
                            + "Despite the slow pace, the sensation of %s hot %s surrounding "
                            + "%s dick is gradually driving %s to %s limit.", user.subject(),
                            user.possessiveAdjective(), target.nameDirectObject(),
                            target.directObject(), user.nameOrPossessivePronoun(),
                            getSelfOrgan(c, user, target).describe(user),
                            target.nameOrPossessivePronoun(), target.directObject(),
                            target.possessiveAdjective());
        } else {
            return Formatter.format(
                            "{self:subject} thrusts into {other:name-possessive} {other:body-part:pussy} in a slow steady rhythm, leaving {other:direct-object} gasping.",
                            user, target);
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Slow fuck, minimizes own pleasure";
    }

    @Override
    public String getName(Combat c, Character user) {
        if (c.getStance().penetratedBy(c, c.getStance().getPartner(c, user), user)) {
            return "Thrust";
        } else {
            return "Ride";
        }
    }

    @Override
    public Character getDefaultTarget(Combat c, Character user) {
        return c.getStance().getPartner(c, user);
    }

    @Override
    public boolean makesContact() {
        return true;
    }

    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
