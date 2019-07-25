package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.Shield;

public class Barrier extends Skill {

    public Barrier() {
        super("Barrier");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.spellcasting) >= 18;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !c.getStance().sub(user) && !c.getStance().prone(user) && !c.getStance().prone(target)
                        && user.canAct();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Creates a magical barrier to protect you from physical damage: 3 Mojo";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        user.add(c, new Shield(user.getType(), .5));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.recovery;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You conjure a simple magic barrier around yourself, reducing physical damage. Unfortunately, it will do nothing against a gentle caress.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.getName()
                        + " holds a hand in front of her and "+target.subjectAction("see")+" a magical barrier appear briefly, before it becomes invisible.";
    }

}
