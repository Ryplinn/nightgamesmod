package nightgames.actions;

import nightgames.characters.Character;
import nightgames.items.Item;
import nightgames.trap.Trap;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class Action implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4981682001213276175L;
    private static Set<Action> actionPool;
    static {
        buildActionPool();
    }
    protected String name;

    public Action(String name) {
        this.name = name;
    }

    private static void buildActionPool() {
        actionPool = new HashSet<>();
        actionPool.add(new Resupply());
        actionPool.add(new Wait());
        actionPool.add(new Hide());
        actionPool.add(new Bathe());
        actionPool.add(new Scavenge());
        actionPool.add(new Craft());
        actionPool.add(new Use(Item.Lubricant));
        actionPool.add(new Use(Item.EnergyDrink));
        actionPool.add(new Use(Item.Beer));
        actionPool.add(new Recharge());
        actionPool.add(new Locate());
        actionPool.add(new MasturbateAction());
        actionPool.add(new Energize());
        actionPool.add(new Disguise());
        actionPool.add(new BushAmbush());
        actionPool.add(new PassAmbush());
        actionPool.add(new TreeAmbush());
        actionPool.add(new Struggle());
        Trap.buildTrapPool();
        for (Trap t : Trap.trapPool) {
            actionPool.add(new SetTrap(t));
        }
    }

    public static Set<Action> getActions() {
        return actionPool;
    }

    public abstract boolean usable(Character user);

    public abstract Movement execute(Character user);

    @Override
    public String toString() {
        return name;
    }

    public abstract Movement consider();

    public boolean freeAction() {
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Action other = (Action) obj;
        if (name == null) {
            return other.name == null;
        } else
            return name.equals(other.name);
    }

}
