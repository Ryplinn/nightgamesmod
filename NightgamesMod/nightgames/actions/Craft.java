package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.State;

public class Craft extends Action {

    /**
     * 
     */
    private static final long serialVersionUID = 3199968029862277675L;

    Craft() {
        super("Craft Potion");
    }

    @Override
    public boolean usable(Character user) {
        return user.location().potions() && user.getAttribute(Attribute.cunning) > 15 && !user.bound();
    }

    @Override
    public Movement execute(Character user) {
        user.state = State.crafting;
        return Movement.craft;
    }

    @Override
    public Movement consider() {
        return Movement.craft;
    }

}
