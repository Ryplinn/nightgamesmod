package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BreastsPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class SuccubusEmbrace extends Position {
    public SuccubusEmbrace(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.succubusembrace);
        this.domType = DomType.FEMDOM;
    }

    private boolean hasBreasts(Character c) {
        return c.body.getLargestBreasts() != BreastsPart.flat;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "TODO?";
        }
        String breastDesc;
        if (hasBreasts(getTop()) && hasBreasts(getBottom())) {
            breastDesc = "your {other:body-part:breasts} against {self:possessive} own pair";
        } else if (hasBreasts(getTop())) {
            breastDesc = "your chest against {self:possessive} soft tits";
        } else if (hasBreasts(getBottom())) {
            breastDesc = "your sensitive nipples against {self:possessive} hard chest";
        } else {
            breastDesc = "you tightly against {self:direct-object}";
        }
        return Formatter.format("{self:name} is sitting on top of you, with your"
                        + " {other:body-part:cock} nestled deep within {self:possessive}"
                        + " {self:body-part:pussy}. {self:POSSESSIVE} {self:body-part:wings}"
                        + " are wrapped around your back, pressing %s.", getTop(), getBottom(), breastDesc);
    }

    @Override
    public int dominance() {
        return 4;
    }
    
    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(getBottom(), Formatter.format(
                            "{self:SUBJECT-ACTION:pinch|pinches} {other:possessive} clitoris with {self:possessive} hands as {other:subject-action:try|tries} to ride {self:direct-object}. "
                                            + "While {other:subject-action:yelp|yelps} with surprise, {self:subject-action:take|takes} the chance to swing around into a dominant missionary position.",
                            getBottom(), getTop()));
        }
        return new CoiledSex(top, bottom);
    }
    
    @Override
    public boolean mobile(Character c) {
        return c.getType() == top;
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
    public String image() {
        return "succubus_embrace.jpg";
    }

}
