package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;

public class Suckle extends Skill {
    public Suckle() {
        super("Suckle");
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return target.breastsAvailable() && c.getStance().reachTop(user) && c.getStance().front(user)
                        && (user.canAct() || c.getStance().enumerate() == Stance.nursing && user.canRespond())
                        && c.getStance().facing(user, target) && c.getStance().en != Stance.neutral;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        return resolve(c, user, target, false);
    }

    public boolean resolve(Combat c, Character user, Character target, boolean silent) {
        Result results = target.has(Trait.lactating) ? Result.special : Result.normal;
        int m = (user.getAttribute(Attribute.seduction) > 10 ? 8 : 4) + Random.random(6);
        if (!silent) writeOutput(c, Result.normal, user, target);
        if (user.has(Trait.silvertongue)) {
            m += 4;
        }

        target.body.pleasure(user, user.body.getRandom("mouth"), target.body.getRandom("breasts"), m, c, new SkillUsage<>(this, user, target));
        if (results == Result.special) {
            target.buildMojo(c, 10);
        } else {
            target.buildMojo(c, 5);
        }
        return true;
    }
    
    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 6;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.normal) {
            return "You slowly circle your tongue around each of " + target.getName()
                            + "'s nipples, and start sucking like a newborn.";
        } else {
            return "You slowly circle your tongue around each of " + target.getName()
                            + "'s nipples, and start sucking like a newborn. "
                            + "Her milk slides smoothly down your throat, and you're left with a warm comfortable feeling.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.normal) {
            return String.format("%s licks and sucks %s nipples, sending a "
                            + "surge of excitement straight to %s groin.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            target.possessiveAdjective());
        } else {
            return String.format("%s licks and sucks %s nipples, drawing forth "
                            + "a gush of breast milk from %s teats. "
                            + "%s drinks deeply of %s milk, gurgling happily as more of the"
                            + " smooth liquid flows down %s throat.", user.subject(),
                            target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            user.subject(), target.nameOrPossessivePronoun(),
                            user.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Suck your opponent's nipples. Builds mojo for the opponent.";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
