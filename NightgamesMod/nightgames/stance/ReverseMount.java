package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class ReverseMount extends Position {
    public ReverseMount(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.reversemount);
        facingType = FacingType.BEHIND;

    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You are straddling " + getBottom().getName() + ", with your back to her.";
        } else {
            return String.format("%s is sitting on %s chest, facing %s groin.",
                            getTop().subject(), getBottom().nameOrPossessivePronoun(), getBottom().possessiveAdjective());
        }
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public String image() {
        if (!getBottom().useFemalePronouns() && getTop().useFemalePronouns()) {
            return "rmount_f.jpg";
        } else if (getBottom().useFemalePronouns() && getTop().useFemalePronouns()) {
            return "rmount_ff.jpg";
        } else {
            return "rmount_m.jpg";
        }
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
    public boolean feet(Character c, Character target) {
        return target.getType() == bottom;
    }

    @Override
    public boolean oral(Character c, Character target) {
        return target.getType() == bottom;
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
        return 2;
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
        c.write(struggler, String.format("%s to gain a more dominant position, but with"
                        + " %s sitting on %s chest, there is nothing %s can do.",
                        struggler.subjectAction("struggle"), getTop().subject(),
                        struggler.possessiveAdjective(), struggler.pronoun()));
    }

    @Override
    public void escape(Combat c, Character escapee) {
        c.write(escapee, Formatter.format("{self:SUBJECT-ACTION:try} to escape {other:name-possessive} hold, but with"
                        + " {other:direct-object} sitting firmly on {self:possessive} chest, there is nothing {self:pronoun} can do.",
                        escapee, getTop()));
    }
}
