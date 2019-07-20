package nightgames.stance;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;

import java.util.Optional;

public class Missionary extends Position {

    public Missionary(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.missionary);
        this.domType = DomType.MALEDOM;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You are penetrating " + getBottom().getName() + " in traditional Missionary position.";
        } else {
            return String.format("%s between %s legs, fucking %s in the traditional Missionary position.",
                            getTop().subjectAction("are", "is"), getBottom().nameOrPossessivePronoun(),
                            getBottom().directObject());
        }
    }

    @Override
    public String image() {
        return "missionary.jpg";
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
    public boolean behind(Character c) {
        return false;
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new Mount(top, bottom));
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        boolean coiled = Random.random(2) == 0;
        if (!coiled) {
            if (writeMessage) {
                c.write(getBottom(), Formatter.format(
                                "{self:SUBJECT-ACTION:wrap|wraps} {self:possessive} legs around {other:name-possessive} waist and suddenly {self:action:pull|pulls} {other:direct-object} into a deep kiss. {other:SUBJECT-ACTION:are|is} so surprised by this sneak attack that {other:subject-action:don't|doesn't} "
                                                + "even notice {self:pronoun} {self:action:rolling|rolling} {other:direct-object} onto {other:possessive} back until {other:subject-action:feel|feels} {self:possessive} weight on {other:possessive} hips. {self:PRONOUN} {self:action:move|moves} {self:possessive} hips experimentally, enjoying the control "
                                                + "{self:pronoun} {self:action:have|has} in cowgirl position.",
                                getBottom(), getTop()));
            }
            return new Cowgirl(bottom, top);
        } else {
            if (writeMessage) {
                c.write(getBottom(), Formatter.format(
                                "{self:SUBJECT-ACTION:wrap|wraps} {self:possessive} legs around {other:name-possessive} waist and suddenly {self:action:pull|pulls} {other:direct-object} into a deep kiss. {other:SUBJECT-ACTION:are|is} so surprised by this sneak attack that {other:subject-action:don't|doesn't} "
                                                + "even notice {self:reflective} getting trapped until {other:subject-action:feel|feels} {self:possessive} limbs wrapped around {other:possessive} body. {self:PRONOUN} {self:action:smile|smiles} widely, enjoying the control "
                                                + "{self:pronoun} {self:action:have|has} coiled around {other:direct-object}.",
                                getBottom(), getTop()));
            }
            return new CoiledSex(bottom, top);
        }
    }

    public static Position similarInstance(Character top, Character bottom) {
        if (top.get(Attribute.power) > 25 && Random.random(2) == 0) {
            return new UpsideDownMaledom(top.getType(), bottom.getType());
        }
        return new Missionary(top.getType(), bottom.getType());
    }
    
    @Override
    public int dominance() {
        return 3;
    }
}
