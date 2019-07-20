package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.skills.damage.DamageType;

import java.util.Optional;

public class Jumped extends Position {
    public Jumped(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.standing);
        this.domType = DomType.FEMDOM;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You are clinging to " + getBottom().nameOrPossessivePronoun()
                            + " arms while her dick is buried deep in your pussy";
        } else {
            return String.format("%s clinging to %s shoulders and gripping %s waist "
                            + "with %s thighs while %s uses the leverage to ride %s.",
                            getTop().subjectAction("are", "is"), getBottom().nameOrPossessivePronoun(),
                            getBottom().possessiveAdjective(), getTop().possessiveAdjective(),
                            getTop().pronoun(), getBottom().directObject());
        }
    }

    @Override
    public String image() {
        return "standing.jpg";
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom && c.getType() != top;
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
        return c.getType() != bottom && c.getType() != top;
    }

    @Override
    public boolean reachBottom(Character c) {
        return c.getType() != bottom && c.getType() != top;
    }

    @Override
    public boolean prone(Character c) {
        return false;
    }

    @Override
    public boolean behind(Character c) {
        return false;
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new Neutral(top, bottom));
    }

    @Override
    public void decay(Combat c) {
        time++;
        getTop().weaken(c, (int) DamageType.stance.modifyDamage(getBottom(), getTop(), 2));
    }

    @Override
    public Optional<Position> checkOngoing(Combat c) {
        if (getBottom().getStamina().get() < 2 && !getTop().has(Trait.petite)) {
            if (getBottom().human()) {
                c.write("Your legs give out and you fall on the floor. " + getTop().getName() + " lands heavily on your lap.");
                return Optional.of(new Cowgirl(top, bottom));
            } else {
                c.write(Formatter.format("{other:SUBJECT-ACTION:lose} {other:possessive} balance and {other:action:fall},"
                                + " pulling {self:name-do} down on top of {other:direct-object}.", getTop(), getBottom()));
                return Optional.of(new Cowgirl(top, bottom));
            }
        } else {
            return super.checkOngoing(c);
        }
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(getBottom(), Formatter.format(
                            "{self:SUBJECT-ACTION:pinch|pinches} {other:possessive} clitoris with {self:possessive} hands as {other:subject-action:try|tries} to ride {self:direct-object}. "
                                            + "While {other:subject-action:yelp|yelps} with surprise, {self:subject-action:take|takes} the chance to push {other:direct-object} against a wall and fuck {other:direct-object} in a standing position.",
                                            getBottom(), getTop()));
        }
        return new Standing(bottom, top);
    }
    
    @Override
    public int dominance() {
        return 3;
    }
}
