package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.EarPart;
import nightgames.characters.body.mods.PlantMod;
import nightgames.characters.body.mods.TentacledMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Match;
import nightgames.status.AttributeBuff;
import nightgames.status.SlimeMimicry;
import nightgames.status.Stsflag;

public class MimicDryad extends Skill {
    MimicDryad() {
        super("Mimicry: Dryad");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.human() && user.getAttribute(Attribute.slime) >= 10;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && !user.is(Stsflag.mimicry) && Match.getParticipants().stream().anyMatch(character -> character.has(Trait.dryad));
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Mimics a dryad's abilities";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else if (c.shouldPrintReceive(target, c)) {
            if (!target.is(Stsflag.blinded))
                c.write(user, receive(c, 0, Result.normal, user, target));
            else 
                printBlinded(c, user);
        }
        if (user.has(Trait.ImitatedStrength)) {
            user.addTemporaryTrait(Trait.dryad, 10);
            if (user.getLevel() >= 20) {
                user.addTemporaryTrait(Trait.magicEyeFrenzy, 10);
            }
            if (user.getLevel() >= 28) {
                user.addTemporaryTrait(Trait.lacedjuices, 10);
            }
            if (user.getLevel() >= 36) {
                user.addTemporaryTrait(Trait.RawSexuality, 10);
            }
            if (user.getLevel() >= 44) {
                user.addTemporaryTrait(Trait.temptingtits, 10);
            }
            if (user.getLevel() >= 52) {
                user.addTemporaryTrait(Trait.addictivefluids, 10);
            }
            if (user.getLevel() >= 60) {
                user.body.temporaryAddPartMod("pussy", TentacledMod.INSTANCE, 10);
            }
        }
        user.addTemporaryTrait(Trait.dryad, 10);
        user.addTemporaryTrait(Trait.magicEyeFrenzy, 10);
        user.addTemporaryTrait(Trait.frenzyingjuices, 10);
        user.addTemporaryTrait(Trait.RawSexuality, 10);
        user.addTemporaryTrait(Trait.temptingtits, 10);
        user.body.temporaryAddOrReplacePartWithType(EarPart.pointed, 10);
        BreastsPart part = user.body.getBreastsBelow(BreastsPart.h.getSize());
        if (part != null) {
            user.body.temporaryAddOrReplacePartWithType(part.upgrade(), 10);
        }

        int strength = Math.max(10, user.getAttribute(Attribute.slime)) * 2 / 3;
        if (user.has(Trait.Masquerade)) {
            strength = strength * 3 / 2;
        }
        user.add(c, new AttributeBuff(user.getType(), Attribute.bio, strength, 10));
        user.add(c, new SlimeMimicry("dryad", user.getType(), 10));
        user.body.temporaryAddPartMod("pussy", PlantMod.INSTANCE, 10);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You shift your slime into a one mimicking a dryad.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format("{self:NAME-POSSESSIVE} amorphous body quivers and collapses into a puddle. "
                        + "Starting from the center, the slime matter dyes itself green, transforming itself into a verdant emerald hue within seconds. "
                        + "After reforming her features out of her slime, {other:subject-action:see|sees} that {self:NAME} has taken on an appearance reminiscent of Rosea the dryad, "
                        + "complete with a large slime-parody of a flower replacing where her usual vagina is.", user, target);
    }

}
