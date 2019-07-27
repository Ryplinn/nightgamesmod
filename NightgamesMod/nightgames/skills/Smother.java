package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.stance.Smothering;
import nightgames.stance.Stance;
import nightgames.status.BodyFetish;
import nightgames.status.Shamed;

public class Smother extends Skill {

    public Smother() {
        super("Smother");
        addTag(SkillTag.pleasureSelf);
        addTag(SkillTag.dominant);
        addTag(SkillTag.facesit);
        addTag(SkillTag.weaken);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.fetishism) >= 5;
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 6;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.crotchAvailable() && user.canAct() && c.getStance().dom(user)
                        && (c.getStance().isBeingFaceSatBy(target, user))
                        && !user.has(Trait.shy);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Shove your ass into your opponent's face to demonstrate your superiority";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        writeOutput(c, Result.normal, user, target);

        int m = 10;
        if (target.has(Trait.silvertongue)) {
            m = m * 3 / 2;
        }
        user.body.pleasure(target, target.body.getRandom("mouth"), user.body.getRandom("ass"), m, c, new SkillUsage<>(this, user, target));
        double n = 14 + Random.random(4);
        if (c.getStance().front(user)) {
            // opponent can see self
            n += user.body.getHotness(target);
        }
        if (target.has(Trait.imagination)) {
            n *= 1.5;
        }

        target.temptWithSkill(c, user, user.body.getRandom("ass"), (int) Math.round(n / 2), this);
        target.weaken(c, (int) DamageType.physical.modifyDamage(user, target, Random.random(10, 25)));

        target.loseWillpower(c, Math.max(10, target.getWillpower().max() * 10 / 100 ));
        target.add(c, new Shamed(target.getType()));
        if (c.getStance().enumerate() != Stance.smothering) {
            c.setStance(new Smothering(user.getType(), target.getType()), user, true);
        }
        if (Random.random(100) < 25 + 2 * user.getAttribute(Attribute.fetishism)) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), "ass", .35));
        }
        return true;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 25;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        if (c.getStance().enumerate() != Stance.smothering) {
            return Tactics.positioning;
        } else {
            return Tactics.pleasure;
        }
    }

    @Override
    public String getLabel(Combat c, Character user) {
        return "Smother";
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format("Enjoying your dominance over {other:name-do}, you experimentally scoot your legs forward so that your ass completely eclipses {other:possessive} face. {other:SUBJECT-ACTION:panic|panics} as {other:pronoun} {other:action:realize|realizes} that {other:pronoun} cannot breathe!", user, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format("Enjoying {self:possessive} dominance over {other:name-do}, {self:subject} experimentally scoots {self:possessive} legs forward so that {self:possessive} ass completely eclipses {other:possessive} face. {other:SUBJECT-ACTION:panic|panics} as {other:pronoun} {other:action:realize|realizes} that {other:pronoun} cannot breathe!", user, target);
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
