package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.status.Aggressive;
import nightgames.status.Stsflag;

public class Spores extends Skill {

    public Spores() {
        super("Spores");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.bio) >= 13;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && !target.is(Stsflag.aggressive);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 20;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Release some spores to force your opponent into a frenzied attack.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (target.wary()) {
            c.write(user,
                            Formatter.format("{self:SUBJECT-ACTION:release|releases} a mass of tiny particles, but "
                                            + "{other:subject-action:avoid|avoids} breathing any of them in.",
                            user, target));
            return false;
        } else {
            c.write(user,
                            Formatter.format("{self:SUBJECT-ACTION:release|releases} a mass of tiny particles, and "
                                            + "{other:subject-action:are|is} forced to breathe them in. The scent"
                                            + " drives {other:direct-object} into a frenzy.", user, target));
            target.add(c, new Aggressive(target.getType(), user.nameOrPossessivePronoun() + " spores", 5));
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

}
