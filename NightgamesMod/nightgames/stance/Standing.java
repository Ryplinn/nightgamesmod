package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.skills.damage.DamageType;

import java.util.Optional;

public class Standing extends Position {
    public Standing(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.standing);
        domType = DomType.MALEDOM;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You are holding " + getBottom().getName() + " in the air while buried deep in her pussy.";
        } else {
            return String.format("%s is holding %s in %s arms while pumping into %s girl parts.",
                            getTop().subject(), getBottom().nameDirectObject(), getTop().possessiveAdjective(),
                            getBottom().possessiveAdjective());
        }
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != top && c.getType() != bottom;
    }

    @Override
    public boolean inserted(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return c.getType() == top || c.getType() == bottom;
    }

    @Override
    public String image() {
        return "standing.jpg";
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
        return false;
    }

    @Override
    public boolean behind(Character c) {
        return false;
    }

    @Override
    public Optional<Position> insertRandom(Combat c) {
        return Optional.of(new Neutral(top, bottom));
    }

    @Override
    public void decay(Combat c) {
        time++;
        getTop().weaken(c, (int) DamageType.stance.modifyDamage(getBottom(), getTop(), 2));
    }

    @Override
    public Optional<Position> checkOngoing(Combat c) {
        if (getTop().getStamina().get() < 10) {
            if (getTop().human()) {
                c.write("Your legs give out and you fall on the floor. " + getBottom().getName()
                                + " lands heavily on your lap.");
                return Optional.of(new Cowgirl(bottom, top));
            } else {
                c.write(Formatter.format("{self:SUBJECT-ACTION:lose} {self:possessive} balance and {self:action:fall}, "
                                + "pulling {other:name-do} down on top of {self:direct-object}.", getTop(), getBottom()));
                return Optional.of(new Cowgirl(bottom, top));
            }
        } else {
            return super.checkOngoing(c);
        }
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(getBottom(), Formatter.format(
                            "{self:SUBJECT-ACTION:wrap|wraps} {self:possessive} legs around {other:name-possessive} waist and suddenly {self:action:pull|pulls} {other:direct-object} into a deep kiss. {other:SUBJECT-ACTION:are|is} so surprised by this sneak attack that {other:subject-action:don't|doesn't} "
                                            + "even notice {other:reflective} falling forward until {other:subject-action:feel|feels} {self:possessive} limbs wrapped around {other:possessive} body. {self:PRONOUN} {self:action:move|moves} {self:possessive} hips experimentally, enjoying the control "
                                            + "{self:pronoun} {self:action:have|has} coiled around {other:direct-object}.",
                            getBottom(), getTop()));
        }
        return new CoiledSex(bottom, top);
    }
    
    @Override
    public int dominance() {
        return 3;
    }
}
