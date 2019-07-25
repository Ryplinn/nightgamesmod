package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.PussyPart;
import nightgames.characters.body.mods.GooeyMod;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.clothing.ClothingSlot;
import nightgames.status.Stsflag;

public class ToggleSlimePussy extends Skill {

    ToggleSlimePussy() {
        super("Toggle Slime Pussy");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.slime) > 14;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && (!hasSlimePussy(user) || !c.getStance().vaginallyPenetrated(c, user));
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (hasSlimePussy(user)) {
            return "Remove Pussy";
        } else {
            return "Grow Pussy";
        }
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return (float) ((user.dickPreference() - 5) * (hasSlimePussy(user) ? .3 : -.3));
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 15;
    }

    @Override
    public String describe(Combat c, Character user) {
        if (hasSlimePussy(user)) {
            return "Fill up the hole between your legs with extra slime, closing it off";
        } else {
            return "Form a gooey pussy between your legs";
        }
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        String msg = "{self:SUBJECT-ACTION:close|closes} {self:possessive} eyes ";
        if (hasSlimePussy(user)) {
            if (user.crotchAvailable() || user.human()) {
                msg += "and the cleft of {self:possessive} {self:body-part:pussy} flattens out, leaving only smooth slime.";
            } else {
                msg += user.outfit.getTopOfSlot(ClothingSlot.bottom).getName()
                                + " loses some of its definition, as if something that was beneath them no longer is.";
            }
            user.body.removeAll("pussy");
        } else {
            if (user.crotchAvailable() || user.human()) {
                msg += "and a slit forms in {self:possessive} slime. The new pussy's lips shudder invitingly.";
            } else {
                msg += "but you see no outside changes. Perhaps they are hidden under {self:possessive} clothes?";
            }
            user.body.add(PussyPart.generic.applyMod(GooeyMod.INSTANCE));
        }
        if (!target.human() || !target.is(Stsflag.blinded))
            c.write(user, Formatter.format(msg, user, target));
        else 
            printBlinded(c, user);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    private boolean hasSlimePussy(Character user) {
        return user.hasPussy() && user.body.getRandomPussy().moddedPartCountsAs(user, GooeyMod.INSTANCE);
    }
}
