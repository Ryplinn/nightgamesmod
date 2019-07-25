package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.status.AttributeBuff;
import nightgames.status.BodyFetish;
import nightgames.status.Status;

public class SuccubusNurse extends Skill {

    public SuccubusNurse() {
        super("Succubus Nurse");
        addTag(SkillTag.breastfeed);
        addTag(SkillTag.usesBreasts);
        addTag(SkillTag.perfectAccuracy);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.SuccubusWarmth);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && user.body.getLargestBreasts() != BreastsPart.flat
                        && c.getStance().en == Stance.succubusembrace;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return user.has(Trait.Pacification) ? 15 : 0;
    }
    
    @Override
    public float priorityMod(Combat c, Character user) {
        if (!user.has(Trait.lactating)) {
            return -3.f;
        } else if (user.has(Trait.Pacification)) {
            return 2.f;
        }
        return 1.f;
    }

    @Override
    public String describe(Combat c, Character user) {
        return user.has(Trait.lactating) ? "Put your opponent's mouth to use"
                        : "Let your opponent drink a bit of milk";
    }
    
    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        c.write(user, Formatter.format(
                        "{self:SUBJECT-ACTION:shift|shifts}, pulling {other:name-possessive} head down "
                                        + "towards one of {self:possessive} puffy nipples. %s. {self:POSSESSIVE} milk"
                                        + " slides smoothly down {other:possessive} throat, %s.",
                        user, target,
                        alreadyInfluenced(target)
                                        ? "{other:PRONOUN-ACTION:don't|doesn't} even try to"
                                                        + " resist as {self:pronoun-action:place|places} {other:possessive}"
                                                        + " mouth around it"
                                        : "{other:PRONOUN-ACTION:clamp|clamps}"
                                                        + " {other:possessive} lips shut, but it's no use as"
                                                        + " {self:subject-action:pry|pries} open {other:possessive} mouth"
                                                        + " and {self:action:insert|inserts} {self:possessive} nipple.",
                        user.has(Trait.Pacification)
                                        ? "making {other:direct-object} feel strangely" + " calm and passive inside"
                                        : "feeling strangely erotic"));
        if (user.has(Trait.Pacification)) {
            target.add(c, new AttributeBuff(target.getType(), Attribute.power, -2, 5));
        }
        new Suckle().resolve(c, user, user, true);
        if (Random.random(100) < 5 + 2 * user.getAttribute(Attribute.fetishism)) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), BreastsPart.a.getType(), .25));
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    private boolean alreadyInfluenced(Character target) {
        return target.status.stream()
                            .anyMatch(Status::mindgames);
    }

}
