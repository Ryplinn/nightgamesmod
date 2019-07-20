package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;

public class StandingOver extends Position {

    public StandingOver(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.standingover);
        facingType = FacingType.FACING;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You are standing over " + getBottom().getName() + ", who is helpless on the ground.";
        } else {
            return String.format("%s flat on %s back, while %s stands over %s.",
                            getBottom().subjectAction("are", "is"), getBottom().possessiveAdjective(),
                            getTop().subject(), getBottom().directObject());
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
        if (getBottom().hasPussy()) {
            return "standing_m.jpg";
        } else {
            return "standing_f.jpg";
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
        return c.getType() != top && c.getType() != bottom;
    }

    @Override
    public boolean reachBottom(Character c) {
        return c.getType() != top && c.getType() != bottom;
    }

    @Override
    public boolean prone(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public boolean feet(Character c, Character target) {
        return target.getType() == bottom;
    }

    @Override
    public boolean oral(Character c, Character target) {
        return target.getType() == bottom && c.getType() != top;
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
        return 1;
    }
    
    @Override
    public int distance() {
        return 2;
    }
}
