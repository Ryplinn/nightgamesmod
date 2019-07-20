package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.gui.GUI;
import nightgames.items.Item;

public class Alarm extends Trap {
    
    public Alarm() {
        this(null);
    }
    
    public Alarm(CharacterType owner) {
        super("Alarm", owner);
    }

    @Override
    public void trigger(Character target) {
        if (target.human()) {
            GUI.gui.message(
                            "You're walking through the eerily quiet campus, when a loud beeping almost makes you jump out of your skin. You realize the beeping is "
                                            + "coming from a cell phone on the floor. You shut it off as quickly as you can, but it's likely everyone nearby heard it already.");
        } else if (target.location().humanPresent()) {
            GUI.gui.message(target.getName() + " Sets off your alarm, giving away her presence.");
        }
        target.location().alarm = true;
        target.location().remove(this);
    }

    @Override
    public boolean decoy() {
        return true;
    }

    @Override
    public boolean recipe(Character user) {
        return user.has(Item.Tripwire) && user.has(Item.Phone);
    }

    @Override
    public String setup(Character user) {
        owner = user.getType();
        getOwner().consume(Item.Tripwire, 1);
        getOwner().consume(Item.Phone, 1);
        if (user.human()) {
            return "You rig up a disposable phone to a tripwire. When someone trips the wire, it should set of the phone's alarm.";
        } else {
            return "";
        }
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.get(Attribute.cunning) >= 6;
    }

}
