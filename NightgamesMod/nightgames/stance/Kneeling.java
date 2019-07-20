package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;

public class Kneeling extends Position {
    public Kneeling(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.kneeling);
        facingType = FacingType.FACING;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You are standing over " + getBottom().getName() + ", who is kneeling before you.";
        } else {
            return String.format("%s kneeling on the ground, while %s stands over %s.",
                            getBottom().subjectAction("are", "is"), getTop().subject(), getBottom().directObject());
        }
    }

    @Override
    public boolean mobile(Character c) {
        return true;
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return c.getType() != top && c.getType() != bottom;
    }

    @Override
    public String image() {
        if (getBottom().hasPussy() || !getBottom().hasDick()) {
            return "kneeling_f.jpg";
        } else {
            return "kneeling_m.jpg";
        }
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
        return c.getType() == bottom;
    }

    @Override
    public boolean feet(Character c, Character target) {
        return c.getType() != bottom && target.getType() == bottom;
    }

    @Override
    public boolean oral(Character c, Character target) {
        return c.getType() != top;
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
        return getSubDomBonus(self, 2.0f);
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
        return 2;
    }
}
