package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

import java.util.Optional;

public class UpsideDownMaledom extends Position {
    public UpsideDownMaledom(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.upsidedownmaledom);
        this.domType = DomType.MALEDOM;
    }

    @Override
    public int pinDifficulty(Combat c, Character self) {
        return 8;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You are holding " + getBottom().getName() + " upside-down by her legs while fucking her pussy.";
        } else {
            return String.format("%s is holding %s upside-down by %s legs while fucking %s pussy.",
                            getTop().subject(), getBottom().nameDirectObject(), getBottom().possessiveAdjective(),
                            getTop().possessiveAdjective());
        }
    }

    @Override
    public String image() {
        return "upsidedownmaledom.jpg";
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
        return false;
    }

    @Override
    public boolean inserted(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean facing(Character c, Character target) {
        return (c.getType() != bottom && c.getType() != top) || (target.getType() != bottom && target.getType() != top);
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new StandingOver(top, bottom));
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            if (getBottom().human()) {
                c.write(getBottom(), Formatter.format(
                                "Summoning your remaining strength, you hold your arms up against the floor and use your hips to tip {other:name-do} off-balance with {other:possessive} dick still held inside of you. "
                                                + "{other:SUBJECT} lands on the floor with you on top of {other:direct-object} in a reverse cow-girl.",
                                getBottom(), getTop()));
            } else {
                c.write(getBottom(), Formatter.format(
                                "{self:SUBJECT} suddenly pushes against the floor and knocks {other:name-do} to the ground with {self:possessive} hips. "
                                                + "{other:PRONOUN-ACTION:land} on the floor with {self:name-do} on"
                                                + " top of {other:direct-object} with {self:direct-object} in a reverse cow-girl position.",
                                getBottom(), getTop()));
            }
        }
        return new ReverseCowgirl(bottom, top);
    }

    @Override
    public double pheromoneMod(Character self) {
        if (self.getType() == bottom) {
            return 10;
        }
        return 2;
    }

    @Override
    public int dominance() {
        return 4;
    }
}
