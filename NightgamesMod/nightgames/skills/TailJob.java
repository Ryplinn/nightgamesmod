package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.status.BodyFetish;

public class TailJob extends Skill {

    TailJob() {
        super("Tailjob");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        boolean enough = user.getAttribute(Attribute.seduction) >= 20 || user.getAttribute(Attribute.animism) >= 1;
        return enough && user.body.get("tail").size() > 0;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && target.crotchAvailable() && c.getStance().mobile(user)
                        && !c.getStance().mobile(target) && !c.getStance().inserted(target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Use your tail to tease your opponent";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        writeOutput(c, Result.normal, user, target);
        int m = (5 + Random.random(10))
                        + Math.min(user.getArousal().getReal() / 20, user.getAttribute(Attribute.animism));
        String receiver;
        if (target.hasDick()) {
            receiver = "cock";
        } else {
            receiver = "pussy";
        }
        if (Random.random(100) < 5 + 2 * user.getAttribute(Attribute.fetishism)) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), "tail", .25));
        }
        target.body.pleasure(user, user.body.getRandom("tail"), target.body.getRandom(receiver), m, c, new SkillUsage<>(this, user, target));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (target.hasDick()) {
            return "You skillfully use your flexible " + user.body.getRandom("tail").describe(user)
                            + " to stroke and tease " + target.getName() + "'s sensitive girl-cock.";
        } else {
            return "You skillfully use your flexible " + user.body.getRandom("tail").describe(user)
                            + " to stroke and tease " + target.getName() + "'s sensitive girl parts.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (target.hasDick()) {
            return String.format("%s teases %s sensitive dick and balls with %s %s. "
                            + "It wraps completely around %s shaft and strokes firmly.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            user.possessiveAdjective(),
                            user.body.getRandom("tail").describe(user),
                            target.possessiveAdjective());
        } else {
            return String.format("%s teases %s sensitive pussy with %s %s. "
                            + "It runs along %s nether lips and leaves %s gasping.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            user.possessiveAdjective(),
                            user.body.getRandom("tail").describe(user),
                            target.possessiveAdjective(), target.directObject());
        }
    }

    @Override
    public boolean makesContact() {
        return true;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FOREPLAY;
    }
}
