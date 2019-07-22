package nightgames.trap;

import nightgames.areas.Deployable;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Encounter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public abstract class Trap implements Deployable {

    public static Set<Supplier<Trap>> trapPool;
    protected CharacterType owner;
    private final String name;
    private int strength;
    protected Trap(String name, CharacterType owner) {
        this.name = name;
        this.owner = owner;
        this.setStrength(0);
    }

    public static void buildTrapPool() {
        trapPool = new HashSet<>();
        trapPool.add(Alarm::new);
        trapPool.add(Tripline::new);
        trapPool.add(Snare::new);
        trapPool.add(SpringTrap::new);
        trapPool.add(AphrodisiacTrap::new);
        trapPool.add(DissolvingTrap::new);
        trapPool.add(Decoy::new);
        trapPool.add(Spiderweb::new);
        trapPool.add(EnthrallingTrap::new);
        trapPool.add(IllusionTrap::new);
        trapPool.add(StripMine::new);
        trapPool.add(TentacleTrap::new);
        trapPool.add(RoboWeb::new);
    }

    protected abstract void trigger(Character target);

    public boolean decoy() {
        return false;
    }

    public abstract boolean recipe(Character owner);

    public abstract boolean requirements(Character owner);

    public abstract String setup(Character owner);

    public boolean resolve(Character active) {
        if (active.getType() != owner) {
            trigger(active);
            return true;
        }
        return false;
    }

    public void setStrength(Character user) {
        this.strength = user.getLevel();
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }

    @Override
    public final Character getOwner() {
        return owner.fromPoolGuaranteed();
    }

    @Override
    public final String toString() {
        return getName();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj instanceof Trap && getName().equals(obj.toString());
    }
    
    public void capitalize(Character attacker, Character victim, Encounter enc) {
        // NOP
    }

    public String getName() {
        return name;
    }

}
