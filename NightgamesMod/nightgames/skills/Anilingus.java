package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.AssPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.status.BodyFetish;
import nightgames.status.Stsflag;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Anilingus extends Skill {
    private static final String worshipString = "Ass Worship";

    public Anilingus() {
        super("Lick Ass");
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.oral);
    }

    @Override
    public Set<SkillTag> getTags(Combat c, Character user, Character target) {
        if (isWorship(c, user, target)) {
            Set<SkillTag> tags = new HashSet<>(super.getTags(c, user, target));
            tags.add(SkillTag.worship);
            return tags;
        }
        return super.getTags(c, user, target);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.shameless) || user.getAttribute(Attribute.seduction) >= 30 || c.getStance().en == Stance.facesitting;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        boolean canUse = c.getStance().isBeingFaceSatBy(user, target) && user.canRespond()
                        || user.canAct();
        boolean titsBlocking = c.getStance().enumerate() == Stance.paizuripin
                        || c.getStance().enumerate() == Stance.titfucking;
        return target.crotchAvailable() && target.body.has("ass") && c.getStance().oral(user, target) && canUse
                        && !c.getStance().anallyPenetrated(c, target) && !titsBlocking;
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return user.has(Trait.silvertongue) ? 1 : 0;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        AssPart targetAss = (AssPart) target.body.getRandom("ass");
        Result result = Result.normal;
        int m = 10;
        int n = 0;
        int selfm = 0;
        if (isWorship(c, user, target)) {
            result = Result.sub;
            m += 4 + Random.random(6);
            n = 20;
            selfm = 20;
        } else if (c.getStance().isBeingFaceSatBy(user, target)) {
            result = Result.reverse;
            m += Random.random(6);
            n = 10;
        } else if (!c.getStance().mobile(target) || rollSucceeded) {
            m += Random.random(6);
            if (user.has(Trait.silvertongue)) {
                m += 4;
                result = Result.special;
            }
        } else {
            m = 0;
            n = 0;
            result = Result.miss;
        }
        writeOutput(c, m, result, user, target);
        if (m > 0) {
            target.body.pleasure(user, user.body.getRandom("mouth"), targetAss, m, c, new SkillUsage<>(this, user, target));
        }
        if (n > 0) {
            target.buildMojo(c, n);
        }
        if (selfm > 0) {
            user.temptWithSkill(c, target, target.body.getRandom("ass"), selfm, this);
        }
        if (target.has(Trait.temptingass) && !user.bound()) {
            c.write(target, Formatter.format("Servicing {other:possessive} perfect behind makes {self:direct-object} almost unconsciously touch {self:reflective}.", user, target));
            (new Masturbate()).resolve(c, user, target, true);
        }
        return result != Result.miss;
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return !c.getStance().isBeingFaceSatBy(user, target) && c.getStance().reachTop(target)? 75 : 200;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You try to lick " + target.getName() + "'s rosebud, but "+target.pronoun()+" pushes your head away.";
        } else if (modifier == Result.special) {
            return "You gently rim " + target.getName() + "'s asshole with your tongue, sending shivers through "+target.possessiveAdjective()+" body.";
        } else if (modifier == Result.reverse) {
            return "With " + target.nameOrPossessivePronoun()
                            + " ass pressing into your face, you helplessly give in and take an experimental lick at "+target.possessiveAdjective()+" pucker.";
        } else if (modifier == Result.sub) {
            return "With a terrible need coursing through you, you lower your face between "
                            + target.nameOrPossessivePronoun()
                            + " rear cheeks and plunge your tongue repeatedly in and out of "+target.possessiveAdjective()+" "
                            + target.body.getRandom("ass").describe(target) + ". "
                            + "You dimly realize that this is probably arousing you as much as " + target.getName()
                            + ", but worshipping "+target.possessiveAdjective()+" sublime derriere seems much higher on your priorities than winning.";
        }
        return "You thrust your tongue into " + target.getName() + "'s ass and lick it, making "+target.directObject()+" yelp in surprise.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s closes in on %s behind, but %s %s to push %s head away.", user.getName(),
                            target.nameOrPossessivePronoun(), user.pronoun(),
                            target.action("manage"), target.possessiveAdjective());
        } else if (modifier == Result.special) {
            return String.format("%s gently rims %s asshole with %s tongue, sending shivers through %s body.",
                            user.getName(), target.nameOrPossessivePronoun(), user.possessiveAdjective(),
                            target.possessiveAdjective());
        } else if (modifier == Result.reverse) {
            return String.format("With %s ass pressing into %s face, %s helplessly gives in and starts licking %s ass.",
                            target.nameOrPossessivePronoun(), user.nameOrPossessivePronoun(), user.pronoun(),
                            target.possessiveAdjective());
        } else if (modifier == Result.sub) {
            return String.format("As if entranced, %s buries %s face inside %s asscheeks, licking %s crack and worshipping %s anus.",
                            user.subject(), user.possessiveAdjective(), target.nameOrPossessivePronoun(), target.possessiveAdjective(), target.possessiveAdjective());
        }
        return String.format("%s licks %s tight asshole, both surprising and arousing %s.",
                        user.getName(), target.nameOrPossessivePronoun(), target.pronoun());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Perform anilingus on opponent";
    }

    private boolean isWorship(Combat c, Character user, Character target) {
        Optional<BodyFetish> fetish = user.body.getFetish("ass");
        boolean worship = c.getOpponent(user).has(Trait.objectOfWorship);
        boolean enthralled = user.is(Stsflag.enthralled);
        boolean isPet = target == null ? user.isPet() : user.isPetOf(target);
        return fetish.isPresent() || worship || enthralled || isPet;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        return isWorship(c, user, null) ? worshipString : "Lick Ass";
    }
}
