package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

import java.util.Optional;

public class ReverseCowgirl extends Position {

    public ReverseCowgirl(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.reversecowgirl);
        this.facingType = FacingType.BEHIND;
        this.domType = DomType.FEMDOM;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "";
        } else {
            return String.format("%s is riding %s in Reverse Cowgirl position, facing %s feet.",
                            getTop().subject(), getBottom().nameDirectObject(), getBottom().directObject());
        }
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public String image() {
        return "reverse_cowgirl.jpg";
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return false;
    }

    @Override
    public boolean dom(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean sub(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public boolean reachTop(Character c) {
        return c.getType() != top && c.getType() != bottom;
    }

    @Override
    public boolean reachBottom(Character c) {
        return true;
    }

    @Override
    public boolean prone(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public boolean behind(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new ReverseMount(top, bottom));
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(getBottom(), Formatter
                            .format("{self:SUBJECT-ACTION:manage|manages} to unbalance {other:name-do} and push {other:direct-object} forward onto {other:possessive} hands and knees. {self:SUBJECT-ACTION:follow|follows} {other:direct-object}, still inside {other:possessive} tight wetness, and continue "
                                            + "to fuck {other:direct-object} from behind.", getBottom(), getTop()));
        }
        return new Doggy(bottom, top);
    }
    
    @Override
    public int dominance() {
        return 3;
    }
}
