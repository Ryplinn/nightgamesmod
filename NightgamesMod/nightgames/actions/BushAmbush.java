package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.gui.GUI;

public class BushAmbush extends Action {

    private static final long serialVersionUID = 2384434976695344978L;

    BushAmbush() {
        super("Hide in Bushes");
    }

    @Override
    public boolean usable(Character user) {
        return user.location().id() == Movement.ftcPath
                        && (user.getAttribute(Attribute.cunning) >= 20 || user.getAttribute(Attribute.animism) >= 10)
                        && user.state != State.inBushes && !user.bound();
    }

    @Override
    public Movement execute(Character user) {
        if (user.human()) {
            if (user.getAttribute(Attribute.animism) >= 10) {
                GUI.gui.message("You crouch down in some dense bushes, ready" + " to pounce on passing prey.");
            } else {
                GUI.gui.message("You spot some particularly dense bushes, and figure"
                                + " they'll make for a decent hiding place. You lie down in them,"
                                + " and wait for someone to walk past.");
            }
        }
        user.state = State.inBushes;
        return Movement.ftcBushAmbush;
    }

    @Override
    public Movement consider() {
        return Movement.ftcBushAmbush;
    }

}
