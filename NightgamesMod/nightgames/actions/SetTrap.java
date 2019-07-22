package nightgames.actions;

import nightgames.characters.Character;
import nightgames.gui.GUI;
import nightgames.trap.Trap;

import java.util.function.Supplier;

public class SetTrap extends Action {
    /**
     * 
     */
    private static final long serialVersionUID = 9194305067966782124L;
    private Supplier<Trap> trap;

    SetTrap(Supplier<Trap> trap) {
        super("Set(" + trap.get().toString() + ")");
        this.trap = trap;
    }

    @Override
    public boolean usable(Character user) {
        Trap testTrap = trap.get();
        return testTrap.recipe(user) && !user.location().open() && testTrap.requirements(user)
                        && user.location().env.size() < 5 && !user.bound() && !user.location().isTrapped();
    }

    @Override
    public Movement execute(Character user) {
        Trap newTrap = trap.get();
        newTrap.setStrength(user);
        user.location().place(newTrap);
        String message = newTrap.setup(user);
        if (user.human()) {
            GUI.gui.message(message);
        }
        return Movement.trap;
    }

    @Override
    public Movement consider() {
        return Movement.trap;
    }

}
