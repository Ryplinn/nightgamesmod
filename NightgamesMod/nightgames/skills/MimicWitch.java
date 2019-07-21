package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
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
    MimicWitch(CharacterType self) {
        super("Mimicry: Witch", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.human() && user.get(Attribute.slime) >= 10;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canRespond() && !getSelf().is(Stsflag.mimicry) && GameState.getGameState().characterPool.getNPC("Cassie").has(Trait.witch);
    }

    @Override
    public String describe(Combat c) {
        return "Mimics a witch's abilities";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (getSelf().human()) {
            c.write(getSelf(), deal(c, 0, Result.normal, target));
        } else if (c.shouldPrintReceive(target, c)) {
            if (!target.is(Stsflag.blinded))
                c.write(getSelf(), receive(c, 0, Result.normal, target));
            else 
                printBlinded(c);
        }
        if (getSelf().has(Trait.ImitatedStrength)) {
            getSelf().addTemporaryTrait(Trait.witch, 10);
            getSelf().addTemporaryTrait(Trait.lactating, 10);
            if (getSelf().getLevel() >= 20) {
                getSelf().addTemporaryTrait(Trait.responsive, 10);
            }
            if (getSelf().getLevel() >= 28) {
                getSelf().addTemporaryTrait(Trait.temptingtits, 10);
            }
            if (getSelf().getLevel() >= 36) {
                getSelf().addTemporaryTrait(Trait.beguilingbreasts, 10);
            }
            if (getSelf().getLevel() >= 44) {
                getSelf().addTemporaryTrait(Trait.sedativecream, 10);
            }
            if (getSelf().getLevel() >= 52) {
                getSelf().addTemporaryTrait(Trait.enchantingVoice, 10);
            }
            if (getSelf().getLevel() >= 60) {
                getSelf().body.temporaryAddPartMod("mouth", ArcaneMod.INSTANCE, 10);
            }
        }
        getSelf().addTemporaryTrait(Trait.witch, 10);
        getSelf().addTemporaryTrait(Trait.enchantingVoice, 10);
        getSelf().addTemporaryTrait(Trait.magicEyeEnthrall, 10);
        getSelf().addTemporaryTrait(Trait.lactating, 10);
        getSelf().addTemporaryTrait(Trait.beguilingbreasts, 10);
        getSelf().addTemporaryTrait(Trait.sedativecream, 10);
        BreastsPart part = getSelf().body.getBreastsBelow(BreastsPart.h.getSize());
        if (part != null) {
            getSelf().body.temporaryAddOrReplacePartWithType(part.upgrade(), 10);
        }

        int strength = Math.max(10, getSelf().get(Attribute.slime)) * 2 / 3;
        if (getSelf().has(Trait.Masquerade)) {
            strength = strength * 3 / 2;
        }
        getSelf().add(c, new AttributeBuff(self, Attribute.spellcasting, strength, 10));
        getSelf().add(c, new SlimeMimicry("witch", self, 10));

        getSelf().body.temporaryAddPartMod("pussy", ArcaneMod.INSTANCE, 10);
        getSelf().body.temporaryAddPartMod("cock", CockMod.runic, 10);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new MimicWitch(user.getType());
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return "You shift your slime and start mimicking Cassie's witch form.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return Formatter.format("{self:NAME-POSSESSIVE} amorphous body shakes violently and her human-features completely dissolve. "
                        + "After briefly becoming something that resembles a mannequin, her goo shifts colors into a glowing purple hue. "
                        + "Facial features forms again out of her previously smooth slime into something very familiar to {other:name-do}. "
                        + "Looks like {self:NAME} is mimicking Cassie's witch form!", getSelf(), target);
    }

}
