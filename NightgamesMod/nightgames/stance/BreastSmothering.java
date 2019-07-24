package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.skills.*;
import nightgames.skills.damage.DamageType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class BreastSmothering extends Position {
    public BreastSmothering(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.breastsmothering);
        facingType = FacingType.FACING;
    }
    
    public String image() {
        return "breast_smother.png";
    }

    @Override
    public String describe(Combat c) {
        return Formatter.format("{self:subject-action:keep} {other:name-possessive} face between {self:possessive} tits, with {self:possessive} large breasts fully encompassing {other:possessive} view. {other:SUBJECT} cannot even breathe except for the short pauses when {self:subject-action:allow|allows} {other:direct-object} to by loosening {self:possessive} grip.", getTop(), getBottom());
    } 

    @Override
    public int distance() {
        return 1;
    }

    @Override
    public boolean mobile(Character c) {
        return c.getType() != bottom;
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
        return c.getType() != bottom;
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
    public Collection<Skill> availSkills(Combat c, Character self) {
        if (self.getType() != bottom) {
            return Collections.emptySet();
        } else {
            Collection<Skill> avail = new HashSet<>();
            avail.add(new FondleBreasts());
            avail.add(new Suckle());
            avail.add(new Tickle());
            avail.add(new Finger());
            avail.add(new Nurple());
            avail.add(new Escape());
            avail.add(new Struggle());
            avail.add(new Nothing());
            avail.add(new Wait());
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
    public void struggle(Combat c, Character struggler) {
        c.write(struggler, Formatter.format("{self:SUBJECT-ACTION:attempt} to struggle out of {other:name-possessive} {other:body-part:breasts}, "
                        + "but {other:pronoun-action:have} other ideas.", struggler, getTop()));
        (new BreastSmother()).resolve(c, struggler, getBottom());
    }

    @Override
    public void escape(Combat c, Character escapee) {
        c.write(escapee, Formatter.format("{self:SUBJECT-ACTION:attempt} to extract {self:reflective} out of {other:name-possessive} {other:body-part:breasts}, "
                        + "but {other:pronoun-action:have} other ideas.", escapee, getTop()));
        (new BreastSmother()).resolve(c, escapee, getBottom());
    }
}
