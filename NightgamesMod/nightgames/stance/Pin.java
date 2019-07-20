package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

import java.util.Optional;

public class Pin extends Position {

    public Pin(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.pin);
        facingType = FacingType.FACING;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You're sitting on " + getBottom().getName() + ", holding her arms in place.";
        } else {
            return String.format("%s is pinning %s down, leaving %s helpless.",
                            getTop().subject(), getBottom().nameDirectObject(), getBottom().directObject());
        }
    }

    @Override
    public Optional<Position> checkOngoing(Combat c) {
        if (!getTop().canAct() && getBottom().canAct()) {
            c.write(getBottom(), Formatter.format("With {self:subject} unable to resist, "
                            + "{bottom:subject-action:roll} over on top of {self:direct-object}."
                            , getTop(), getBottom()));
            return Optional.of(new Mount(bottom, top));
        }
        return Optional.empty();
    }
    
    @Override
    public int pinDifficulty(Combat c, Character self) {
        return 10;
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public String image() {
        return new Behind(top, bottom).image();
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
        return c.getType() != bottom;
    }

    @Override
    public boolean reachBottom(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean prone(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public boolean inserted(Character c) {
        return false;
    }

    @Override
    public boolean feet(Character c, Character target) {
        return c.getType() != bottom && target.getType() == top;
    }

    @Override
    public boolean oral(Character c, Character target) {
        return c.getType() != bottom && target.getType() == top;
    }

    @Override
    public boolean behind(Character c) {
        return c.getType() == top;
    }

    @Override
    public float priorityMod(Character self) {
        return getSubDomBonus(self, 2.0f);
    }

    @Override
    public double pheromoneMod(Character self) {
        return 1.5;
    }
    
    @Override
    public int dominance() {
        return 4;
    }
    
    @Override
    public int distance() {
        return 1;
    }

    @Override
    public void struggle(Combat c, Character struggler) {
        c.write(struggler, String.format("%s to gain a more dominant position, but with"
                        + " %s behind %s holding %s wrists behind %s waist firmly, there is little %s can do.",
                        struggler.subjectAction("struggle"), getTop().subject(), struggler.directObject(),
                        struggler.possessiveAdjective(), struggler.possessiveAdjective(), struggler.pronoun()));
    }

    @Override
    public void escape(Combat c, Character escapee) {
        c.write(escapee, Formatter.format("{self:SUBJECT-ACTION:try} to escape {other:name-possessive} pin, but with"
                        + " {other:direct-object} sitting on {self:possessive} back, holding {self:possessive} wrists firmly, there is nothing {self:pronoun} can do.",
                        escapee, getTop()));
    }
}
