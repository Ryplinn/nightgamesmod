package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;

public class GoodnightKiss extends Skill {

    GoodnightKiss() {
        super("Goodnight Kiss");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.ninjutsu) >= 18;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance()
                .kiss(user, target) && user.canAct();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 30;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Deliver a powerful knockout drug via a kiss: 30 Mojo";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        c.write(user, String.format(
                        "%s surreptitiously %s %s lips with a powerful sedative, careful not "
                                        + "to accidentally ingest any. As soon as %s %s an opening, "
                                        + "%s %s in and kiss %s softly. Only a small amount of the drug is actually "
                                        + "transferred by the kiss, but it's enough. %s immediately staggers "
                                        + "as the strength leaves %s body.",
                        user.subject(), user.action("coat"), user.possessiveAdjective(),
                        user.pronoun(), user.action("see"), user.pronoun(),
                        user.action("dart"), target.subject(), target.subject(), target.possessiveAdjective()));
        target.tempt(Random.random(4));
        target.getStamina()
              .empty();
        return true;
    }

    @Override
    public int speed(Character user) {
        return 7;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
