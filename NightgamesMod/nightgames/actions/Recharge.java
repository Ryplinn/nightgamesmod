package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.gui.GUI;
import nightgames.items.Item;

public class Recharge extends Action {

    /**
     * 
     */
    private static final long serialVersionUID = 2089054062272510717L;

    Recharge() {
        super("Recharge");
    }

    @Override
    public boolean usable(Character user) {
        return user.location().recharge() && user.getAttribute(Attribute.science) > 0 && user.count(Item.Battery) < 20 && !user.bound();
    }

    @Override
    public Movement execute(Character user) {
        if (user.human()) {
            GUI.gui.message("You find a power supply and restore your batteries to full.");
        }
        user.chargeBattery();
        return Movement.recharge;
    }

    @Override
    public Movement consider() {
        return Movement.recharge;
    }

}
