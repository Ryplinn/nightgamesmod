package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.HeldOral;
import nightgames.stance.Stance;

public class PinAndBlow extends Skill {
    PinAndBlow() {
        super("Oral Pin");
        addTag(SkillTag.positioning);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.oral);
        addTag(SkillTag.foreplay);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 22 && user.getAttribute(Attribute.power) >= 15;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().mobile(user)
                && c.getStance().dom(user)
                && (c.getStance().prone(target)  ||  c.getStance().en == Stance.paizuripin)
                && c.getStance().facing(user, target)
                && target.crotchAvailable() && user.canAct()
                && !c.getStance().connected(c)
                && c.getStance().en != Stance.oralpin;
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 0;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 5;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        c.setStance(new HeldOral(user.getType(), target.getType()), user, true);
        if (target.hasDick()) {
            new Blowjob().resolve(c, user, target);
        } else if (target.hasPussy()) {
            new Cunnilingus().resolve(c, user, target);
        } else if (target.body.has("ass")) {
            new Anilingus().resolve(c, user, target);
        }
        return true;
    }

    @Override
    public int speed(Character user) {
        return 5;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return receive(c, damage, modifier, user, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        
        if( c.getStance().en == Stance.paizuripin)
        {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:free|frees} {other:possessive} cock from her breasts, and quickly {self:action:settle|settles} {self:possessive} head between {other:possessive} legs.",
                            user, target);
        }else
        {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:bow|bows} {other:name-do} over, and {self:action:settle|settles} {self:possessive} head between {other:possessive} legs.",
                            user, target);
        }                       
                       
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Holds your opponent down and use your mouth";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
