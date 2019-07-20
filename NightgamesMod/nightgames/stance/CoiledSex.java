package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

import java.util.Optional;

public class CoiledSex extends Position {

    public CoiledSex(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.coiled);
        this.domType = DomType.FEMDOM;
    }

    @Override
    public int pinDifficulty(Combat c, Character self) {
        return 8;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "Your limbs are coiled around " + getBottom().nameOrPossessivePronoun() + " body and "
                            + getBottom().possessiveAdjective() + " cock is inside you.";
        } else {
            return String.format("%s on top of %s with %s cock trapped in %s pussy and %s face smothered in %s cleavage.",
                            getBottom().subjectAction("are", "is"), getTop().nameDirectObject(),
                            getBottom().possessiveAdjective(), getTop().possessiveAdjective(),
                            getBottom().possessiveAdjective(), getTop().possessiveAdjective());
        }
    }

    @Override
    public String image() {
        return "coiledsex.jpg";
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
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
        if (writeMessage) {
            c.write(getBottom(), Formatter.format(
                            "In a desperate gamble for dominance, {self:subject} piston wildly into {other:name-do}, making {other:direct-object} yelp and breaking {other:possessive} concentration. Shaking off {other:possessive} limbs coiled around {self:direct-object}, {self:subject} grab ahold of {other:possessive} legs and swing into a missionary position.",
                            getBottom(), getTop()));
        }
        return new Missionary(bottom, top);
    }

    @Override
    public double pheromoneMod(Character self) {
        return 6;
    }
    
    @Override
    public int dominance() {
        return 4;
    }
}
