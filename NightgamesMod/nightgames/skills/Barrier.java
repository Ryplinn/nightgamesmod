package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.Shield;

public class Barrier extends Skill {

    public Barrier(CharacterType self) {
        super("Barrier", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.spellcasting) >= 18;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return !c.getStance().sub(getSelf()) && !c.getStance().prone(getSelf()) && !c.getStance().prone(target)
                        && getSelf().canAct();
    }

    @Override
    public int getMojoCost(Combat c) {
        return 10;
    }

    @Override
    public String describe(Combat c) {
        return "Creates a magical barrier to protect you from physical damage: 3 Mojo";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        writeOutput(c, Result.normal, target);
        getSelf().add(c, new Shield(self, .5));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Barrier(user.getType());
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.recovery;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return "You conjure a simple magic barrier around yourself, reducing physical damage. Unfortunately, it will do nothing against a gentle caress.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return getSelf().getName()
                        + " holds a hand in front of her and "+target.subjectAction("see")+" a magical barrier appear briefly, before it becomes invisible.";
    }

}
