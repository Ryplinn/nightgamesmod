package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.items.Item;
import nightgames.pet.Slime;

public class SpawnSlime extends Skill {

    public SpawnSlime(Character self) {
        super("Create Slime", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Science) >= 3;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && c.getStance().mobile(getSelf()) && !c.getStance().prone(getSelf())
                        && getSelf().pet == null && getSelf().has(Item.Battery, 5);
    }

    @Override
    public int getMojoCost(Combat c) {
        return 5;
    }

    @Override
    public String describe(Combat c) {
        return "Creates a mindless, but living slime to attack your opponent: 5 mojo, 5 Battery";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        getSelf().consume(Item.Battery, 5);
        int power = 8 + getSelf().get(Attribute.Science) / 10;
        int ac = 3 + getSelf().get(Attribute.Science) / 10;
        if (getSelf().has(Trait.leadership)) {
            power += 5;
        }
        if (getSelf().has(Trait.tactician)) {
            ac += 3;
        }
        if (getSelf().human()) {
            c.write(getSelf(), deal(c, 0, Result.normal, target));
        } else if (target.human()) {
            c.write(getSelf(), receive(c, 0, Result.normal, target));
        }
        getSelf().pet = new Slime(getSelf(), power, ac);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new SpawnSlime(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.summoning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return "You dispense blue slime on the floor and send a charge through it to animate it. The slime itself is not technically alive, but an extension of a larger "
                        + "creature kept in Jett's lab.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return getSelf().name()
                        + " points a device at the floor and releases a blob of blue slime. The blob starts to move like a living thing and briefly takes on a vaguely humanoid shape "
                        + "and smiles at you.";
    }

}
