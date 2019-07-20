package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

import java.util.Optional;

public class Doggy extends Position {

    public Doggy(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.doggy);
        this.domType = DomType.MALEDOM;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return getBottom().getName() + " is on her hands and knees in front of you, while you fuck her Doggy style.";
        } else {
            return String.format("Things aren't going well for %s. %s %s down on %s hands and knees, while %s"
                            + " is fucking %s from behind.", getBottom().subject(),
                            Formatter.capitalizeFirstLetter(getBottom().pronoun()), getBottom().action("are", "is"),
                            getBottom().possessiveAdjective(), getTop().subject(), getBottom().directObject());
        }
    }

    @Override
    public String image() {
        if (getTop().has(Trait.strapped)) {
            return "doggy_ff_strapped.jpg";
        }
        if (getTop().useFemalePronouns()) {
            if (getBottom().hasDick()) {
                return "futa_futa_doggy.jpg";
            }
            return "futa_doggy.jpg";
        }
        return "doggy.jpg";
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return c.getType() != top && c.getType() != bottom;
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
        return c.getType() == top;
    }

    @Override
    public boolean reachBottom(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean prone(Character c) {
        return false;
    }

    @Override
    public boolean behind(Character c) {
        return c.getType() == top;
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new Behind(top, bottom));
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(getBottom(), Formatter.format(
                            "{self:SUBJECT-ACTION:manage|manages} to reach between {self:possessive} legs and grab hold of {other:possessive} "
                                            + (getTop().hasBalls() ? "ballsack" : "cock")
                                            + ", stopping {other:direct-object} in mid thrust. {self:SUBJECT-ACTION:smirk|smirks} at {other:direct-object} over {self:possessive} shoulder "
                                            + "and pushes {self:possessive} butt against {other:direct-object}, using the leverage of "
                                            + "{other:possessive} " + (getTop().hasBalls() ? "testicles" : "cock")
                                            + " to keep {other:direct-object} from backing away to maintain {self:possessive} balance. {self:SUBJECT-ACTION:force|forces} {other:direct-object} onto {other:possessive} back, while never breaking {other:possessive} connection. After "
                                            + "some complex maneuvering, {other:subject-action:end|ends} up on the floor while {self:subject-action:straddle|straddles} {other:possessive} hips in a reverse cowgirl position.",
                            getBottom(), getTop()));
        }
        return new ReverseCowgirl(bottom, top);
    }
    
    @Override
    public int dominance() {
        return 3;
    }
}
