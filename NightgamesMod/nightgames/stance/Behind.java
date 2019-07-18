package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class Behind extends Position {
    public Behind(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.behind);
        this.facingType = FacingType.BEHIND;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You are holding " + getBottom().getName() + " from behind.";
        } else {
            return String.format("%s is holding %s from behind.", getTop().subject(), getBottom().nameDirectObject());
        }
    }

    @Override
    public String image() {
        if (getTop().hasPussy() && getBottom().hasPussy()) {
            return "behind_ff.jpg";
        } else if (getBottom().hasPussy()) {
            return "behind_m.jpg";
        } else {
            return "behind_f.jpg";
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
        return true;
    }

    @Override
    public boolean prone(Character c) {
        return false;
    }

    @Override
    public boolean feet(Character c, Character target) {
        return c.getType() != top && c.getType() != bottom && target.getType() == top;
    }

    @Override
    public boolean oral(Character c, Character target) {
        return c.getType() != top && c.getType() != bottom && target.getType() == top;
    }

    @Override
    public boolean behind(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean inserted(Character c) {
        return false;
    }

    @Override
    public float priorityMod(Character self) {
        return (self.hasInsertable() ? 2 : 1) * getSubDomBonus(self, 1.0f);
    }

    @Override
    public double pheromoneMod(Character self) {
        return 1.5;
    }

    @Override
    public int dominance() {
        return 3;
    }

    @Override
    public int distance() {
        return 1;
    }

    @Override
    public void struggle(Combat c, Character struggler) {
        c.write(struggler, String.format("%s to gain a more dominant position, but with"
                        + " %s behind %s holding %s waist firmly, there is nothing %s can do.",
                        struggler.subjectAction("struggle"), getTop().subject(), struggler.directObject(),
                        struggler.possessiveAdjective(), struggler.pronoun()));
    }

    @Override
    public void escape(Combat c, Character escapee) {
        c.write(escapee, Formatter.format("{self:SUBJECT-ACTION:try} to escape {other:name-possessive} hold, but with"
                        + " {other:direct-object} behind {self:direct-object} holding {self:possessive} waist firmly, there is nothing {self:pronoun} can do.",
                        escapee, getTop()));
    }
}
