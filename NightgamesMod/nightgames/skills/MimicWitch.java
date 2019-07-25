package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.mods.ArcaneMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.GameState;
import nightgames.status.AttributeBuff;
import nightgames.status.SlimeMimicry;
import nightgames.status.Stsflag;

public class MimicWitch extends Skill {
    MimicWitch() {
        super("Mimicry: Witch");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.human() && user.getAttribute(Attribute.slime) >= 10;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && !user.is(Stsflag.mimicry) && GameState.getGameState().characterPool.getNPC("Cassie").has(Trait.witch);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Mimics a witch's abilities";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else if (c.shouldPrintReceive(target, c)) {
            if (!target.is(Stsflag.blinded))
                c.write(user, receive(c, 0, Result.normal, user, target));
            else 
                printBlinded(c, user);
        }
        if (user.has(Trait.ImitatedStrength)) {
            user.addTemporaryTrait(Trait.witch, 10);
            user.addTemporaryTrait(Trait.lactating, 10);
            if (user.getLevel() >= 20) {
                user.addTemporaryTrait(Trait.responsive, 10);
            }
            if (user.getLevel() >= 28) {
                user.addTemporaryTrait(Trait.temptingtits, 10);
            }
            if (user.getLevel() >= 36) {
                user.addTemporaryTrait(Trait.beguilingbreasts, 10);
            }
            if (user.getLevel() >= 44) {
                user.addTemporaryTrait(Trait.sedativecream, 10);
            }
            if (user.getLevel() >= 52) {
                user.addTemporaryTrait(Trait.enchantingVoice, 10);
            }
            if (user.getLevel() >= 60) {
                user.body.temporaryAddPartMod("mouth", ArcaneMod.INSTANCE, 10);
            }
        }
        user.addTemporaryTrait(Trait.witch, 10);
        user.addTemporaryTrait(Trait.enchantingVoice, 10);
        user.addTemporaryTrait(Trait.magicEyeEnthrall, 10);
        user.addTemporaryTrait(Trait.lactating, 10);
        user.addTemporaryTrait(Trait.beguilingbreasts, 10);
        user.addTemporaryTrait(Trait.sedativecream, 10);
        BreastsPart part = user.body.getBreastsBelow(BreastsPart.h.getSize());
        if (part != null) {
            user.body.temporaryAddOrReplacePartWithType(part.upgrade(), 10);
        }

        int strength = Math.max(10, user.getAttribute(Attribute.slime)) * 2 / 3;
        if (user.has(Trait.Masquerade)) {
            strength = strength * 3 / 2;
        }
        user.add(c, new AttributeBuff(user.getType(), Attribute.spellcasting, strength, 10));
        user.add(c, new SlimeMimicry("witch", user.getType(), 10));

        user.body.temporaryAddPartMod("pussy", ArcaneMod.INSTANCE, 10);
        user.body.temporaryAddPartMod("cock", CockMod.runic, 10);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You shift your slime and start mimicking Cassie's witch form.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format("{self:NAME-POSSESSIVE} amorphous body shakes violently and her human-features completely dissolve. "
                        + "After briefly becoming something that resembles a mannequin, her goo shifts colors into a glowing purple hue. "
                        + "Facial features forms again out of her previously smooth slime into something very familiar to {other:name-do}. "
                        + "Looks like {self:NAME} is mimicking Cassie's witch form!", user, target);
    }

}
