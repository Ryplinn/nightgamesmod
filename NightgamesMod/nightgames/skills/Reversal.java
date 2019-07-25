package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Pin;
import nightgames.status.Compulsive;
import nightgames.status.Compulsive.Situation;

import java.util.Optional;

public class Reversal extends Skill {

    public Reversal() {
        super("Reversal");
        addTag(SkillTag.escaping);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && !c.getStance().mobile(user) && c.getStance().sub(user) && user.canAct();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 20;
    }
    
    @Override
    public float priorityMod(Combat c, Character user) {
        return 5.f - (float) user.getAttribute(Attribute.submission) / 3.f;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Optional<String> compulsion = Compulsive.describe(c, user, Situation.PREVENT_REVERSAL);
        if (compulsion.isPresent()) {
            c.write(user, compulsion.get());
            user.pain(c, null, Random.random(20, 50));
            Compulsive.doPostCompulsion(c, user, Situation.PREVENT_REVERSAL);
            return false;
        }
        if (target.roll(user, accuracy(c, user, target))) {
            writeOutput(c, Result.normal, user, target);

            c.setStance(new Pin(user.getType(), target.getType()), user, true);
            target.emote(Emotion.nervous, 10);
            user.emote(Emotion.dominant, 10);
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.cunning) >= 24;
    }

    @Override
    public int speed(Character user) {
        return 4;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return Math.round(Math.max(Math.min(150,
                        2.5f * (user.getAttribute(Attribute.cunning) - target.getAttribute(Attribute.cunning)) + 75),
                        40));
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You try to get on top of " + target.getName()
                            + ", but she's apparently more ready for it than you realized.";
        } else {
            return "You take advantage of " + target.getName() + "'s distraction and put her in a pin.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to reverse %s hold, but %s %s %s.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("stop"),
                            user.directObject());
        } else {
            return String.format("%s rolls %s over and ends up on top.",
                            user.subject(), target.nameDirectObject());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Take dominant position: 10 Mojo";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
