package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.HeldPaizuri;
import nightgames.stance.Stance;

public class PinningPaizuri extends Skill {
    PinningPaizuri() {
        super("Titfuck Pin");
        addTag(SkillTag.positioning);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.oral);
        addTag(SkillTag.foreplay);
        addTag(SkillTag.usesBreasts);
    }

    
    private final static int MIN_REQUIRED_BREAST_SIZE = 3;
    
    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 28;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().mobile(user)
                && c.getStance().dom(user)
                && c.getStance().facing(user, target)
                && (c.getStance().prone(target)  ||  c.getStance().en == Stance.paizuripin)
                && target.crotchAvailable() && user.canAct()
                && !c.getStance().connected(c)
                && c.getStance().en != Stance.paizuripin
                && user.hasBreasts() && user.body.getLargestBreasts().getSize() >= MIN_REQUIRED_BREAST_SIZE
                && target.hasDick() && user.breastsAvailable() && target.crotchAvailable();
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
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        writeOutput(c, Result.normal, user, target);
        
        c.setStance(new HeldPaizuri(user.getType(), target.getType()), user, true);
     
        new Paizuri().resolve(c, user, target, true);
        
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
        
        
        if( c.getStance().en == Stance.oralpin)
        {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:free|frees} {other:possessive} cock from her mouth, and quickly {self:action:wrap|wraps} {self:possessive} breasts around {other:possessive} cock.",
                            user, target);
        }else
        {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:bow|bows} {other:name-do} over, and {self:action:wrap|wraps} {self:possessive} breasts around {other:possessive} cock.",
                            user, target);
        }             
        
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Hold your opponent down and use your tits";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
