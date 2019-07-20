package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.skills.*;
import nightgames.skills.damage.DamageType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class NursingHold extends Position {
    public NursingHold(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.nursing);
        facingType = FacingType.FACING;
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You are cradling " + getBottom().nameOrPossessivePronoun()
                            + " head in your lap with your breasts dangling in front of " + getBottom().directObject();
        } else {
            return String.format("%s is holding %s head in %s lap, with %s enticing "
                            + "breasts right in front of %s mouth.", getTop().subject(),
                            getBottom().nameOrPossessivePronoun(), getTop().possessiveAdjective(),
                            getTop().possessiveAdjective(), getBottom().possessiveAdjective());
        }
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
    }

    @Override
    public String image() {
        return "nursing.jpg";
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return target.getType() == top && c.getType() != bottom;
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
    public void decay(Combat c) {
        time++;
        getBottom().weaken(c, (int) DamageType.temptation.modifyDamage(getTop(), getBottom(), 3));
        getTop().emote(Emotion.dominant, 10);
    }

    @Override
    public float priorityMod(Character self) {
        return dom(self) ? self.has(Trait.lactating) ? 5 : 2 : 0;
    }

    @Override
    public Collection<Skill> availSkills(Combat c, Character self) {
        if (self.getType() != bottom) {
            return Collections.emptySet();
        } else {
            Collection<Skill> avail = new HashSet<>();
            avail.add(new Suckle(bottom));
            avail.add(new Escape(bottom));
            avail.add(new Struggle(bottom));
            avail.add(new Nothing(bottom));
            avail.add(new Wait(bottom));
            return avail;
        }
    }

    @Override
    public boolean faceAvailable(Character target) {
        return target.getType() == top;
    }

    @Override
    public double pheromoneMod(Character self) {
        return 3;
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
        if (struggler.human()) {
            c.write(struggler, "You try to free yourself from " + getTop().getName()
                            + ", but she pops a teat into your mouth and soon you're sucking like a newborn again.");
        } else if (c.shouldPrintReceive(getTop(), c)) {
            c.write(struggler, String.format("%s struggles against %s, but %s %s %s nipple "
                            + "against %s mouth again, forcing %s to suckle.", struggler.subject(),
                            getTop().nameDirectObject(), getTop().pronoun(), getTop().action("presses"),
                            getTop().possessiveAdjective(), struggler.possessiveAdjective(),
                            struggler.directObject()));
        }
        (new Suckle(struggler.getType())).resolve(c, getTop());
    }

    @Override
    public void escape(Combat c, Character escapee) {
        c.write(escapee, Formatter.format("{self:SUBJECT-ACTION:try} to escape {other:name-possessive} hold, but with"
                        + " {other:direct-object} impressive chest in front of {self:possessive} face, {self:pronoun-action:are} easily convinced to stop.",
                        escapee, getTop()));
        (new Suckle(escapee.getType())).resolve(c, getTop());
    }
}
