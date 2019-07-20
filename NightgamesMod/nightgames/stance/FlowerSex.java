package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;

import java.util.Optional;

public class FlowerSex extends Position {

    public FlowerSex(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.flowertrap);
        this.domType = DomType.FEMDOM;
    }

    @Override
    public int pinDifficulty(Combat c, Character self) {
        return 12;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You're coiled around " + getBottom().nameOrPossessivePronoun()
                            + " body with his cock inside you and the petals of your flower wrapped around both of you like a cocoon.";
        } else {
            return String.format("%s trapped in a giant flower bulb surrounding %s and %s. "
                            + "Inside, %s on top of %s with %s cock trapped in %s pussy "
                            + "and %s face smothered in %s cleavage.", getBottom().subjectAction("are", "is"),
                            getBottom().human() ? "you" : getBottom().reflectivePronoun(), getTop().subject(),
                            getBottom().subjectAction("are", "is"), getTop().nameDirectObject(),
                            getBottom().possessiveAdjective(), getTop().possessiveAdjective(),
                            getBottom().possessiveAdjective(), getTop().possessiveAdjective());
        }
    }

    @Override
    public String image() {
        return "flower.png";
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return true;
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
        return c.getType() != bottom;
    }

    @Override
    public boolean reachBottom(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public boolean prone(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public boolean behind(Character c) {
        return false;
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new Mount(top, bottom));
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        return this;
    }

    @Override
    public double pheromoneMod(Character self) {
        return 5;
    }
    
    @Override
    public int dominance() {
        return 5;
    }
}
