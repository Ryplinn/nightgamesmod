package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.stance.Mount;
import nightgames.stance.ReverseMount;
import nightgames.stance.Stance;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

public class Stumble extends Skill {

    public Stumble() {
        super("Stumble");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.submission) >= 9;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().en == Stance.neutral;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "An accidental pervert classic";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (Random.random(2) == 0) {
            c.setStance(new Mount(target.getType(), user.getType()), target, false);
        } else {
            c.setStance(new ReverseMount(target.getType(), user.getType()), target, false);
        }
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }
        if (user.checkAddiction(AddictionType.MIND_CONTROL, target)) {
            user.unaddictCombat(AddictionType.MIND_CONTROL,
                            target, Addiction.LOW_INCREASE, c);
            c.write(user, "Acting submissively voluntarily reduces Mara's control over " + user.nameDirectObject());
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Stumble();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You slip and fall to the ground, pulling " + target.getName() + " awkwardly on top of you.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format(
                        "%s stumbles and falls, grabbing %s to catch %s. Unfortunately, "
                                        + "%s can't keep %s balance and %s %s on top of %s. Maybe that's not so unfortunate.",
                        user.getName(), target.nameDirectObject(), user.reflectivePronoun(),
                        target.subject(), target.possessiveAdjective(), target.pronoun(),
                        target.action("fall"), user.directObject());
    }

}
