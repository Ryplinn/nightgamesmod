package nightgames.stance;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;

import java.util.Optional;

public class Cowgirl extends Position {

    public Cowgirl(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.cowgirl);
        this.domType = DomType.FEMDOM;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You're on top of " + getBottom().getName() + ".";
        } else {
            return String.format("%s is riding %s in Cowgirl position. %s breasts bounce in front of %s"
                            + " face each time %s moves %s hips.", getTop().subject(), getBottom().nameDirectObject(),
                            Formatter.capitalizeFirstLetter(getTop().possessiveAdjective()), getBottom().possessiveAdjective(),
                            getTop().pronoun(), getTop().possessiveAdjective());
        }
    }

    @Override
    public String image() {
        if (getBottom().useFemalePronouns()) {
            return "cowgirl_futa.jpg";
        }
        return "cowgirl.jpg";
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return c.getType() != bottom;
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
        return true;
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
                            "{self:SUBJECT-ACTION:pinch|pinches} {other:possessive} clitoris with {self:possessive} hands as {other:subject-action:try|tries} to ride {self:direct-object}. "
                                            + "While {other:subject-action:yelp|yelps} with surprise, {self:subject-action:take|takes} the chance to swing around into a dominant missionary position.",
                            getBottom(), getTop()));
        }
        return new Missionary(bottom, top);
    }

    public static Position similarInstance(Character top, Character bottom) {
        if (top.get(Attribute.power) > 25 && Random.random(2) == 0) {
            return new UpsideDownFemdom(top, bottom);
        }
        return new Cowgirl(top.getType(), bottom.getType());
    }
    
    @Override
    public int dominance() {
        return 3;
    }
}
