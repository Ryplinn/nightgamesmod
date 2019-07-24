package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.Stsflag;

public class Obey extends Skill {

    Obey() {
        super("Obey");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.is(Stsflag.enthralled) && !user.is(Stsflag.stunned);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Obey your master's every command";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            String controller = target.useFemalePronouns() ? "mistress'" : "master's";
            c.write(user, "You patiently await your "+controller+" command.");
        } else if (c.shouldPrintReceive(target, c)) {
            c.write(user, user.getName() + " stares ahead blankly, waiting for "+user.possessiveAdjective()+" orders.");
        }
        if (user.human()) {
            (new Command()).resolve(c, user, user);
        } else {
            (new Masturbate()).resolve(c, user, target);
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Obey();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int paramInt, Result paramResult, Character user, Character paramCharacter) {
        return "";
    }

    @Override
    public String receive(Combat c, int paramInt, Result paramResult, Character user, Character paramCharacter) {
        return "";
    }

}
