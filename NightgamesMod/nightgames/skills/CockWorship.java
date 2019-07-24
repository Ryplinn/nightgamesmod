package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.BodyFetish;

import java.util.Optional;

public class CockWorship extends Skill {

    public CockWorship() {
        super("Cock Worship");
        addTag(SkillTag.pleasureSelf);
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
        writeOutput(c, Result.normal, user, target);
        BodyPart mouth = user.body.getRandom("mouth");
        BodyPart cock = target.body.getRandom("cock");
        target.body.pleasure(user, mouth, cock, m, c, new SkillUsage<>(this, user, target));
        if (user.hasDick() && (!user.hasPussy() || Random.random(2) == 0)) {
            user.body.pleasure(user, user.body.getRandom("hands"), user.body.getRandomCock(), m, c, new SkillUsage<>(this, user, target));
        } else if (user.hasPussy()) {
            user.body.pleasure(user, user.body.getRandom("hands"), user.body.getRandomPussy(), m,
                            c, new SkillUsage<>(this, user, target));
        } else {
            user.body.pleasure(user, user.body.getRandom("hands"), user.body.getRandomHole(), m, c, new SkillUsage<>(this, user, target));
        }
        if (mouth.isErogenous()) {
            user.body.pleasure(user, cock, mouth, m, c, new SkillUsage<>(this, user, target));
        }

        target.buildMojo(c, 20);
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        Optional<BodyFetish> fetish = user.body.getFetish("cock");
        return user.isPetOf(target) || (fetish.isPresent() && fetish.get().magnitude >= .5);
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 150;
    }

    @Override
    public Skill copy(Character user) {
        return new CockWorship();
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
                        "You ecstatically crawl towards {other:name-do} and reverently hold {other:possessive} {other:body-part:cock} with your hands. "
                                        + "You carefully take {other:possessive} member into your {self:body-part:mouth} and start blowing {other:direct-object} for all you are worth. "
                                        + "Minutes pass and you lose yourself in sucking {other:name-possessive} divine shaft while idly playing with yourself. Finally, {other:subject} "
                                        + "pushes your head away from {other:possessive} cock and you finally regain your senses.",
                        user, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "{self:subject} ecstatically crawls to {other:subject} on {self:possessive} knees and reverently cups {other:possessive} {other:body-part:cock}"
                                        + "with {self:possessive} hands. {self:PRONOUN} carefully takes {other:possessive} member into {self:possessive} {self:body-part:mouth} and starts sucking on it "
                                        + "like it was the most delicious popsicle made. Minutes pass and {self:subject} continues blowing {other:possessive} shaft while idly playing with "
                                        + "{self:reflective}. Feeling a bit too good, {other:subject-action:manage|manages} to push {self:name-do} away from {other:possessive} cock lest {self:pronoun} makes {other:direct-object} cum accidentally.",
                        user, target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Worship your opponent's dick";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
