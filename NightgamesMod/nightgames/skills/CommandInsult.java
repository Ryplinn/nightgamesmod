package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;

public class CommandInsult extends PlayerCommand {

    CommandInsult() {
        super("Insult");
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Temporarily destroy your thrall's self-image, draining their mojo.";
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 10;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        target.loseMojo(c, 15);
        c.write(user, deal(c, 0, Result.normal, user, target));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return "Your words nearly drive " + target.getName() + " to tears with their ferocity and psychic backup. Luckily,"
                        + " she won't remember any of it later.";
    }

    @Override
    public String receive(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return "<<This should not be displayed, please inform The" + " Silver Bard: CommandInsult-receive>>";
    }

}
