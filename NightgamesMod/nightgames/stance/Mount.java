package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class Mount extends Position {

    public Mount(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.mount);
        facingType = FacingType.FACING;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You're on top of " + getBottom().getName() + ".";
        } else {
            return String.format("%s straddling %s, with %s enticing breasts right in front of %s.",
                            getTop().subjectAction("are", "is"), getBottom().nameDirectObject(),
                            getTop().possessiveAdjective(), getBottom().directObject());
        }
    }

    @Override
    public String image() {
        if (getTop().hasPussy() && getBottom().hasPussy()) {
            return "mount_ff.jpg";
        } else if (getBottom().hasPussy()) {
            return "mount_m.jpg";
        } else {
            return "mount_f.jpg";
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
        return target.getType() == bottom && c.getType() != top && c.getType() != bottom;
    }

    @Override
    public boolean oral(Character c, Character target) {
        return target.getType() == bottom && c.getType() != top && c.getType() != bottom;
    }

    @Override
    public boolean behind(Character c) {
        return false;
    }

    @Override
    public boolean inserted(Character c) {
        return false;
    }

    @Override
    public float priorityMod(Character self) {
        return getSubDomBonus(self, 4.0f);
    }

    @Override
    public double pheromoneMod(Character self) {
        return 3;
    }
    
    @Override
    public int dominance() {
        return 2;
    }

    @Override
    public int distance() {
        return 1;
    }

    @Override
    public void struggle(Combat c, Character struggler) {
        c.write(struggler, Formatter.format("{self:SUBJECT-ACTION:try} to struggle out of {other:name-possessive} hold, but with"
                        + " {other:direct-object} sitting firmly on {self:possessive} chest, there is nothing {self:pronoun} can do.",
                        struggler, getTop()));
    }

    @Override
    public void escape(Combat c, Character escapee) {
        c.write(escapee, Formatter.format("{self:SUBJECT-ACTION:try} to escape {other:name-possessive} hold, but with"
                        + " {other:direct-object} sitting firmly on {self:possessive} chest, there is nothing {self:pronoun} can do.",
                        escapee, getTop()));
    }
}
