package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.Charmed;
import nightgames.status.Stsflag;

public class Suggestion extends Skill {

    public Suggestion() {
        super("Suggestion");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.hypnotism) >= 1;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().behind(user)
                        && !c.getStance().behind(target) && !c.getStance().sub(user) && !target.is(Stsflag.charmed);
    }
    
    @Override
    public int getMojoCost(Combat c, Character user) {
        return 5;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Hypnotize your opponent so she can't defend herself";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (!target.is(Stsflag.cynical)) {
            if (user.human()) {
                c.write(user, deal(c, 0, Result.normal, user, target));
            } else {
                c.write(user, receive(c, 0, Result.normal, user, target));
            }
            target.add(c, new Charmed(target.getType()));
            return true;
        } else if (user.human()) {
            c.write(user, deal(c, 0, Result.miss, user, target));
        } else {
            c.write(user, receive(c, 0, Result.miss, user, target));
        }
        return false;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format(
                            "You attempt to put %s under hypnotic suggestion, but %s doesn't appear to be affected.",
                            target.getName(), target.pronoun());
        }
        return String.format(
                        "You speak in a calm, rhythmic tone, lulling %s into a hypnotic trance. Her eyes seem to glaze over slightly, momentarily slipping under your influence.",
                        target.getName());
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s attempts to put %s under hypnotic suggestion, but"
                            + " %s %s to regain control of %s consciousness.",
                            user.subject(), target.nameDirectObject(),
                            target.pronoun(), target.action("manage"), target.possessiveAdjective());
        }
        return String.format("%s speaks in a firm, but relaxing tone, attempting to put %s"
                        + " into a trance. Obviously %s wouldn't let %s be "
                        + "hypnotized in the middle of a match, right? ...Right? ..."
                        + "Why %s %s fighting %s again?", user.subject(),
                        target.nameDirectObject(), target.subject(),
                        target.reflectivePronoun(), target.action("was", "were"),
                        target.pronoun(), user.subject());
    }

}
