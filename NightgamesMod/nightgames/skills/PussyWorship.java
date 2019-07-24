package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.BodyFetish;

import java.util.Optional;

public class PussyWorship extends Skill {

    public PussyWorship() {
        super("Pussy Worship");
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.worship);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return target.crotchAvailable() && target.hasDick() && c.getStance().oral(user, target)
                        && c.getStance().front(user) && user.canAct()
                        && !c.getStance().vaginallyPenetrated(c, target);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 0;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 0;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int m = 10 + Random.random(8);
        if (user.has(Trait.silvertongue)) {
            m += 4;
        }
        if (target.human()) {
            c.write(user, receive(c, m, Result.normal, user, target));
        } else if (user.human()) {
            c.write(user, deal(c, m, Result.normal, user, target));
        }
        target.body.pleasure(user, user.body.getRandom("mouth"), target.body.getRandom("pussy"), m, c, new SkillUsage<>(this, user, target));
        if (user.hasDick() && (!user.hasPussy() || Random.random(2) == 0)) {
            user.body.pleasure(user, user.body.getRandom("hands"), user.body.getRandomCock(), m, c, new SkillUsage<>(this, user, target));
        } else if (user.hasPussy()) {
            user.body.pleasure(user, user.body.getRandom("hands"), user.body.getRandomPussy(), m,
                            c, new SkillUsage<>(this, user, target));
        } else {
            user.body.pleasure(user, user.body.getRandom("hands"), user.body.getRandomHole(), m, c, new SkillUsage<>(this, user, target));
        }

        target.buildMojo(c, 20);
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        Optional<BodyFetish> fetish = user.body.getFetish("pussy");
        return user.isPetOf(target) || (fetish.isPresent() && fetish.get().magnitude >= .5);
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 150;
    }

    @Override
    public Skill copy(Character user) {
        return new PussyWorship();
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
        return Formatter.format(
                        "You ecstatically crawl towards {other:name-do} and bring your face up to {other:possessive} {other:body-part:pussy}. "
                                        + "You carefully form a seal with your mouth and {other:possessive} netherlips, and stick your tongue into {other:possessive} moist slit. "
                                        + "Minutes pass and you lose yourself alternating between tonguing {other:name-possessive} divine cunt while idly playing with yourself and "
                                        + "sucking on {other:possessive} fleshy nib. Finally, {other:subject} "
                                        + "pushes your head away from {other:possessive} drenched hole and you finally regain your senses.",
                        user, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "{self:subject} ecstatically crawls to {other:name-do} on {self:possessive} knees and attaches {self:possessive} {self:body-part:mouth} to "
                                        + "{other:possessive} {other:body-part:pussy} while holding onto {other:possessive} legs. {self:SUBJECT} carefully takes a few licks of {other:possessive} slit before "
                                        + "diving right in with {self:possessive} tongue to eat {other:name-do} out. Minutes pass and {self:subject} continues {self:possessive} attack on {other:name-possessive} cunt while idly playing with "
                                        + "{self:reflective}. Feeling a bit too good, {other:pronoun-action:manage|manages} to push {self:name-do} away from {other:possessive} sensitive womanhood lest {self:pronoun} makes {other:direct-object} cum accidentally.",
                        user, target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Worship your opponent's pussy";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
