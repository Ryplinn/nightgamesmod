package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.ModdedCockPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.items.clothing.ClothingSlot;
import nightgames.status.Stsflag;

public class ToggleSlimeCock extends Skill {

    public ToggleSlimeCock(Character self) {
        super("Toggle Slime Cock", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return getSelf().get(Attribute.Slime) > 14;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canRespond() && (!hasSlimeCock() || !c.getStance().inserted(getSelf()));
    }

    @Override
    public float priorityMod(Combat c) {
        return (float) ((getSelf().dickPreference() - 5) * (hasSlimeCock() ? -.3 : .3));
    }

    @Override
    public String getLabel(Combat c) {
        return hasSlimeCock() ? "Retract Cock" : "Form Cock";
    }

    @Override
    public int getMojoCost(Combat c) {
        return 15;
    }

    @Override
    public String describe(Combat c) {
        return hasSlimeCock() ? "Pull your slime cock back into your body" : "Form a cock using your slime";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        String msg = "{self:SUBJECT-ACTION:close|closes} {self:possessive} eyes and ";
        if (hasSlimeCock()) {
            if (getSelf().human() || getSelf().crotchAvailable()) {
                msg += "{self:possessive} {self:body-part:cock} retreats back into {self:possessive} body.";
            } else {
                msg += "the bulge in {self:possessive} " + getSelf().outfit.getTopOfSlot(ClothingSlot.bottom).getName()
                                + " shrinks considerably.";
            }
            getSelf().body.removeTemporaryParts("cock");
        } else {
            if (getSelf().human() || getSelf().crotchAvailable()) {
                msg += "a thick, slimy cock forms between {self:possessive} legs.";
            } else {
                msg += "a sizable bulge forms in " + getSelf().outfit.getTopOfSlot(ClothingSlot.bottom).getName() + ".";
            }
            getSelf().body.temporaryAddOrReplacePartWithType(new ModdedCockPart(CockPart.Size.big, CockPart.Mod.slimy), 100);
        }
        if (!target.human() || !target.is(Stsflag.blinded))
            c.write(getSelf(), Global.global.format(msg, getSelf(), target));
        else 
            printBlinded(c);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new ToggleSlimeCock(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return null;
    }

    private boolean hasSlimeCock() {
        return getSelf().hasDick() && getSelf().body.getRandomCock().moddedPartCountsAs(getSelf(), CockPart.Mod.slimy);
    }
}
