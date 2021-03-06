package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.skills.damage.DamageType;

public class CommandHurt extends PlayerCommand {

    CommandHurt() {
        super("Force Pain");
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Convince your thrall that running into the nearest wall is a good idea.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        target.pain(c, user, (int) DamageType.physical.modifyDamage(target, target, Random.random(30, 50)));
        c.write(user, deal(c, 0, Result.normal, user, target));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return "Grinning, you point towards the nearest wall. " + target.getName()
                        + " seems confused for a moment, but soon she understands your"
                        + " meaning and runs headfirst into it.";
    }

    @Override
    public String receive(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return "<<This should not be displayed, please inform The" + " Silver Bard: CommandHurt-receive>>";
    }

}
