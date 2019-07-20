package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

import java.util.Optional;

public class AnalProne extends AnalSexStance {

    public AnalProne(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.anal);
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return String.format("You're holding %s legs over your shoulder while your cock is buried in %s's ass.",
                            getBottom().nameOrPossessivePronoun(), getBottom().possessiveAdjective());
        } else if (getTop().has(Trait.strapped)) {
            return String.format("%s flat on %s back with %s feet over %s head while %s pegs %s with %s strapon dildo.",
                            getBottom().subjectAction("are", "is"), getBottom().possessiveAdjective(),
                            getBottom().possessiveAdjective(), getBottom().possessiveAdjective(),
                            getTop().subject(), getBottom().directObject(), getTop().possessiveAdjective());
        } else {
            return String.format("%s flat on %s back with %s feet over %s head while %s pegs %s with %s %s.",
                            getBottom().subjectAction("are", "is"), getBottom().possessiveAdjective(),
                            getBottom().possessiveAdjective(), getBottom().possessiveAdjective(),
                            getTop().subject(), getBottom().directObject(),
                            getTop().possessiveAdjective(), getTop().body.getRandomInsertable().describe(getTop()));
        }
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
    public boolean feet(Character c, Character target) {
        return false;
    }

    @Override
    public boolean oral(Character c, Character target) {
        return false;
    }

    @Override
    public boolean behind(Character c) {
        return false;
    }

    @Override
    public boolean inserted(Character c) {
        return c.getType() == top;
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new Mount(top, bottom));
    }

    @Override
    public Optional<Position> checkOngoing(Combat c) {
        Character inserter = inserted(getTop()) ? getTop() : getBottom();
        Character inserted = inserted(getTop()) ? getBottom() : getTop();

        if (!inserter.hasInsertable()) {
            if (inserted.human()) {
                c.write("With " + inserter.getName() + "'s pole gone, your ass gets a respite.");
            } else {
                c.write(inserted.getName() + " sighs with relief with "+inserter.nameOrPossessivePronoun()
                            +" dick gone.");
            }
            return insertRandom(c);
        }
        if (inserted.body.getRandom("ass") == null) {
            if (inserted.human()) {
                c.write("With your asshole suddenly disappearing, " + inserter.getName()
                                + "'s dick pops out of what was once your sphincter.");
            } else {
                c.write("Your dick pops out of " + inserted.getName() + " as her asshole shrinks and disappears.");
            }
            return insertRandom(c);
        }
        return Optional.empty();
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (getTop().has(Trait.strapped)) {
            if (writeMessage) {
                c.write(getBottom(), Formatter.format(
                                "As {other:subject-action:are|is} thrusting into {self:name-do} with {other:possessive} strapon, {self:subject-action:suddenly pull|suddenly pulls} {self:possessive} face up towards {other:direct-object}, and kisses {other:direct-object} deeply. "
                                                + "Taking advantage of {other:possessive} surprise, {self:SUBJECT-ACTION:quickly pushes|quickly pushes} {other:direct-object} down and {self:action:pull|pulls} {other:possessive} fake cock out of {self:reflective}.",
                                getBottom(), getTop()));
            }
            return new Mount(bottom, top);
        } else {
            if (writeMessage) {
                c.write(getBottom(), Formatter.format(
                                "As {other:subject-action:are|is} thrusting into {self:name-do}, {self:subject-action:suddenly pull|suddenly pulls} {self:possessive} face up towards {other:direct-object}, and {self:action:kiss|kisses} {other:direct-object} deeply. "
                                                + "Taking advantage of {other:possessive} surprise, {self:SUBJECT-ACTION:quickly push|quickly pushes} {other:direct-object} down and {self:action:start|starts} fucking {other:direct-object} back on top of {other:direct-object}.",
                                                getBottom(), getTop()));
            }
            return new AnalCowgirl(bottom, top);
        }
    }

    @Override
    public String image() {
        if (getBottom().hasPussy()) {
            return "analf.jpg";
        } else {
            return "pegging.jpg";
        }
    }
    
    @Override
    public int dominance() {
        return 4;
    }
}
