package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.Stsflag;

public class OrgasmSeal extends Skill {

    OrgasmSeal() {
        super("Orgasm Seal", 4);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.spellcasting) >= 15 || user.get(Attribute.darkness) >= 5;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && !target.is(Stsflag.orgasmseal);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 20;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Prevents your opponent from cumming with a mystical seal";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        target.add(c, new nightgames.status.OrgasmSeal(target.getType(), 15));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new OrgasmSeal();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You focus your energy onto " + target.nameOrPossessivePronoun()
                        + " abdomen, coalescing it into a blood red mark that prevents her from cumming.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s makes a complicated gesture and envelopes %s finger tips in a blood red glow. "
                        + "With a nasty grin, %s jams %s finger into %s %s. Strangely it doesn't hurt at all, but"
                        + " when %s withdraws %s finger, %s leaves a glowing pentagram on %s.",
                        user.subject(), user.possessiveAdjective(), user.pronoun(),
                        user.possessiveAdjective(), target.nameOrPossessivePronoun(),
                        (target.hasBalls() ? "balls" : "lower abdomen"),
                        user.pronoun(), target.possessiveAdjective(), user.subject(),
                        target.nameDirectObject());
    }
}
