package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.mods.SizeMod;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.clothing.ClothingSlot;
import nightgames.status.Stsflag;

public class ToggleSlimeCock extends Skill {

    ToggleSlimeCock() {
        super("Toggle Slime Cock");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.slime) > 14;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && (!hasSlimeCock(user) || !c.getStance().inserted(user));
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return (float) ((user.dickPreference() - 5) * (hasSlimeCock(user) ? -.3 : .3));
    }

    @Override
    public String getLabel(Combat c, Character user) {
        return hasSlimeCock(user) ? "Retract Cock" : "Form Cock";
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 15;
    }

    @Override
    public String describe(Combat c, Character user) {
        return hasSlimeCock(user) ? "Pull your slime cock back into your body" : "Form a cock using your slime";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        String msg = "{self:SUBJECT-ACTION:close|closes} {self:possessive} eyes and ";
        if (hasSlimeCock(user)) {
            if (user.human() || user.crotchAvailable()) {
                msg += "{self:possessive} {self:body-part:cock} retreats back into {self:possessive} body.";
            } else {
                msg += "the bulge in {self:possessive} " + user.outfit.getTopOfSlot(ClothingSlot.bottom).getName()
                                + " shrinks considerably.";
            }
            user.body.removeTemporaryParts("cock");
        } else {
            if (user.human() || user.crotchAvailable()) {
                msg += "a thick, slimy cock forms between {self:possessive} legs.";
            } else {
                msg += "a sizable bulge forms in " + user.outfit.getTopOfSlot(ClothingSlot.bottom).getName() + ".";
            }
            user.body.temporaryAddOrReplacePartWithType(new CockPart().applyMod(new SizeMod(SizeMod.COCK_SIZE_BIG)).applyMod(CockMod.slimy), 100);
        }
        if (!target.human() || !target.is(Stsflag.blinded))
            c.write(user, Formatter.format(msg, user, target));
        else 
            printBlinded(c, user);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new ToggleSlimeCock();
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

    private boolean hasSlimeCock(Character user) {
        return user.hasDick() && user.body.getRandomCock().moddedPartCountsAs(user, CockMod.slimy);
    }
}
