package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.items.Item;
import nightgames.pet.Slime;

public class SpawnSlime extends Skill {

    SpawnSlime() {
        super("Create Slime");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.science) >= 3;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().prone(user)
                        && c.getPetsFor(user).size() < user.getPetLimit()
                        && user.has(Item.Battery, 5);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 5;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Creates a mindless, but living slime to attack your opponent: 5 mojo, 5 Battery";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        user.consume(Item.Battery, 5);
        int power = 5 + user.getAttribute(Attribute.science);
        int ac = 3 + user.getAttribute(Attribute.science) / 10;
        writeOutput(c, Result.normal, user, target);
        c.addPet(user, new Slime(user, power, ac).getSelf());
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.summoning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You dispense blue slime on the floor and send a charge through it to animate it. The slime itself is not technically alive, but an extension of a larger "
                        + "creature kept in Jett's lab.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s points a device at the floor and releases a blob of blue slime. The blob "
                        + "starts to move like a living thing and briefly takes on a vaguely humanoid shape "
                        + "and smiles at %s.", user.subject(), target.nameDirectObject());
    }

}
