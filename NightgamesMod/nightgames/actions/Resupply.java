package nightgames.actions;

import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.characters.trait.Trait;
import nightgames.ftc.FTCMatch;
import nightgames.global.Flag;
import nightgames.global.Match;
import nightgames.gui.GUI;
import nightgames.items.Item;

public class Resupply extends Action {

    /**
     *
     */
    private static final long serialVersionUID = -3349606637987124335L;

    public Resupply() {
        super("Resupply");
    }

    @Override
    public boolean usable(Character user) {
        return  !user.bound() && user.location().resupply() || user.has(Trait.immobile)
                        || (Flag.checkFlag(Flag.FTC) && ((FTCMatch) Match.getMatch()).isBase(user, user.location()));
    }

    @Override
    public Movement execute(Character user) {
        if (Flag.checkFlag(Flag.FTC)) {
            FTCMatch match = (FTCMatch) Match.getMatch();
            if (user.human()) {
                GUI.gui.message("You get a change of clothes from the chest placed here.");
            }
            if (user.has(Item.Flag) && !match.isPrey(user)) {
                match.turnInFlag(user);
            } else if (match.canCollectFlag(user)) {
                match.grabFlag();
            }
        } else {
            if (user.human()) {
                if (Match.getMatch().condition.name().equals("nudist")) {
                    GUI.gui.message(
                                    "You check in so that you're eligible to fight again, but you still don't get any clothes.");
                } else {
                    GUI.gui.message("You pick up a change of clothes and prepare to get back in the fray.");
                }
            }
        }
        user.state = State.resupplying;
        return Movement.resupply;
    }

    @Override
    public Movement consider() {
        return Movement.resupply;
    }

}
