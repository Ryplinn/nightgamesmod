package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.status.FiredUp;

public class TemptressRide extends Thrust {

    TemptressRide() {
        super("Improved Ride");
    }

    @Override
    public BodyPart getSelfOrgan(Combat c, Character user, Character target) {
        BodyPart part = super.getSelfOrgan(c, user, target);
        if (part != null && part.isType("pussy")) {
            return part;
        }
        return null;
    }

    @Override
    public BodyPart getTargetOrgan(Combat c, Character user, Character target) {
        BodyPart part = super.getTargetOrgan(c, user, target);
        if (part != null && part.isType("cock")) {
            return part;
        }
        return null;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.temptress) && user.get(Attribute.technique) >= 11;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        return "Skillful Ride";
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 5;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (c.getStance().anallyPenetrated(c, user)) {
            return super.resolve(c, user, target);
        }
        int targetDmg = 10 + Random.random(Math.max(10, user.get(Attribute.technique)));
        int selfDmg = (int) Math.max(1f, targetDmg / 3f);
        if (user.has(Trait.experienced)) {
            selfDmg *= 0.67;
        }
        FiredUp status = (FiredUp) user.status.stream().filter(s -> s instanceof FiredUp).findAny().orElse(null);
        int stack = status == null || !status.getPart().equals("pussy") ? 0 : status.getStack();

        if (user.human()) {
            c.write(user, deal(c, stack, Result.normal, user, target));
        } else {
            c.write(user, receive(c, stack, Result.normal, user, target));
        }

        target.body.pleasure(user, user.body.getRandomPussy(), target.body.getRandomCock(),
                        targetDmg + targetDmg * stack / 2, c, new SkillUsage<>(this, user, target));

        user.body.pleasure(target, target.body.getRandomCock(), user.body.getRandomPussy(), selfDmg, c, new SkillUsage<>(this, user, target));

        user.add(c, new FiredUp(user.getType(), target.getType(), "pussy"));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new TemptressRide();
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        switch (damage) {
            case 0:
                return String.format(
                                "%s holding just the tip of %s %s inside of %s %s,"
                                                + " rubbing it against the soft, tight entrance. Slowly	moving"
                                                + " %s hips, %s %s driving %s crazy.",
                                user.subjectAction("are", "is"), target.nameOrPossessivePronoun(),
                                target.body.getRandomCock().describe(target), user.possessiveAdjective(),
                                user.body.getRandomPussy().describe(user), user.possessiveAdjective(),
                                user.pronoun(), user.action("are", "is"), target.directObject());
            case 1:
                return String.format(
                                "%s down fully onto %s, squeezing %s tightly"
                                                + " with %s %s. The muscles are wound so tight that it's nearly"
                                                + " impossible to move at all, but %s %s down hard and eventually"
                                                + " all of %s %s is lodged firmly inside of %s.",
                                user.subjectAction("slide"), target.subject(), target.directObject(),
                                user.possessiveAdjective(), user.body.getRandomPussy().describe(user),
                                user.pronoun(), user.action("push", "pushes"), target.possessiveAdjective(),
                                target.body.getRandomCock().describe(target), user.directObject());
            default:
                return String.format(
                                "%s up and down %s rock-hard %s while the velvet vise"
                                                + " of %s %s is undulating on %s shaft, sending ripples along it"
                                                + " as if milking it. Overcome with pleasure, %s entire body tenses up and"
                                                + " %s %s %s head back, trying hard not to cum instantly.",
                                user.subjectAction("move"), target.nameOrPossessivePronoun(),
                                target.body.getRandomCock().describe(target), user.possessiveAdjective(),
                                user.body.getRandomPussy().describe(user), target.possessiveAdjective(),
                                target.possessiveAdjective(), target.pronoun(), target.action("throw"),
                                target.possessiveAdjective());
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return deal(c, damage, modifier, user, target);
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }

}
