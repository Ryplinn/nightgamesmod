package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

import java.util.Optional;

public class Anal extends AnalSexStance {

    public Anal(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.anal);
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return String.format("%s behind %s and %s cock in buried in %s ass.",
                            getTop().subjectAction("are", "is"),
                            getBottom().nameDirectObject(), getTop().possessiveAdjective(),
                            getBottom().possessiveAdjective());
        } else if (getTop().has(Trait.strapped)) {
            return String.format("%s pegging %s with %s strapon dildo from behind.",
                           getTop().subjectAction("are", "is"), getBottom().nameDirectObject(),
                           getTop().possessiveAdjective());
        } else {
            return String.format("%s fucking %s in the ass from behind",
                            getTop().subjectAction("are", "is"), getBottom().nameDirectObject());
        }
    }

    @Override
    public String image() {
        if (getTop().hasDick() && getTop().useFemalePronouns() && getBottom().hasDick() && getBottom().useFemalePronouns()) {
            return "futa_futa_doggy.jpg";
        } else if (!getBottom().hasDick() && getTop().useFemalePronouns()) {
            return "futa_doggy.jpg";
        } else if (!getTop().useFemalePronouns()) {
            return "analf.jpg";
        } else {
            return "pegging.jpg";
        }
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
        return c.getType() != bottom;
    }

    @Override
    public boolean reachBottom(Character c) {
        return c.getType() != bottom;
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
    public boolean inserted(Character c) {
        return c.getType() == top;
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return new Behind(top, bottom);
    }

    @Override
    public Optional<Position> checkOngoing(Combat c) {
        Character inserter = inserted(getTop()) ? getTop() : getBottom();
        Character inserted = inserted(getTop()) ? getBottom() : getTop();
        Optional<Position> newStance = Optional.empty();

        if (!inserter.hasInsertable()) {
            if (inserted.human()) {
                c.write("With " + inserter.getName() + "'s pole gone, your ass gets a respite.");
            } else {
                c.write(inserted.getName() + " sighs with relief with "
                                + inserter.nameOrPossessivePronoun() + " phallus gone.");
            }
            newStance = Optional.ofNullable(insertRandom(c));
        }
        if (inserted.body.getRandom("ass") == null) {
            if (inserted.human()) {
                c.write("With your asshole suddenly disappearing, " + inserter.getName()
                                + "'s dick pops out of what was once your sphincter.");
            } else {
                c.write(Formatter.capitalizeFirstLetter(inserter.nameOrPossessivePronoun()) +
                                " dick pops out of " + inserted.getName() 
                                + " as "+inserted.possessiveAdjective()+" asshole shrinks and disappears.");
            }
            newStance = Optional.ofNullable(insertRandom(c));
        }
        return newStance;
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (getTop().has(Trait.strapped)) {
            if (writeMessage) {
                c.write(getBottom(), Formatter.format(
                                "As {other:subject-action:are|is} thrusting into {self:name-do} with {other:possessive} strapon, {self:subject-action:force|forces} {self:possessive} hips back and knock {other:direct-object} off balance. {self:SUBJECT-ACTION:quickly pull|quickly pulls} {other:possessive} fake cock out of {self:possessive} bottom while sitting on top of {other:direct-object}.",
                                getBottom(), getTop()));
            }
            return new ReverseMount(bottom, top);
        } else {
            if (writeMessage) {
                c.write(getBottom(), Formatter.format(
                                "As {other:subject-action:are|is} thrusting into {self:name-do} with {other:possessive} {other:body-part:cock}, {self:subject-action:force|forces} {self:possessive} hips back and knock {other:direct-object} off balance. {self:SUBJECT-ACTION:quickly maneuver|quickly maneuvers} {self:reflective} on top of {other:direct-object}, now fucking {other:direct-object} back in an anal cowgirl position.",
                                getBottom(), getTop()));
            }
            return new AnalCowgirl(bottom, top);
        }
    }

    @Override
    public int dominance() {
        return 4;
    }
}
