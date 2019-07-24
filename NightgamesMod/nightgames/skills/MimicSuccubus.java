package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Reyka;
import nightgames.characters.body.*;
import nightgames.characters.body.mods.DemonicMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.GameState;
import nightgames.status.AttributeBuff;
import nightgames.status.SlimeMimicry;
import nightgames.status.Stsflag;

public class MimicSuccubus extends Skill {

    MimicSuccubus() {
        super("Mimicry: Succubus");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.human() && user.get(Attribute.slime) >= 10;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && !user.is(Stsflag.mimicry) && GameState.getGameState().characterPool.characterTypeInGame(Reyka.class.getSimpleName());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Mimics a succubus's abilities";
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
            user.addTemporaryTrait(Trait.succubus, 10);
            user.addTemporaryTrait(Trait.energydrain, 10);
            if (user.getLevel() >= 20) {
                user.addTemporaryTrait(Trait.spiritphage, 10);
            }
            if (user.getLevel() >= 28) {
                user.addTemporaryTrait(Trait.lacedjuices, 10);
            }
            if (user.getLevel() >= 36) {
                user.addTemporaryTrait(Trait.RawSexuality, 10);
            }
            if (user.getLevel() >= 44) {
                user.addTemporaryTrait(Trait.soulsucker, 10);
            }
            if (user.getLevel() >= 52) {
                user.addTemporaryTrait(Trait.gluttony, 10);
            }
            if (user.getLevel() >= 60) {
                user.body.temporaryAddPartMod("ass", DemonicMod.INSTANCE, 10);
                user.body.temporaryAddPartMod("hands", DemonicMod.INSTANCE, 10);
                user.body.temporaryAddPartMod("feet", DemonicMod.INSTANCE, 10);
                user.body.temporaryAddPartMod("mouth", DemonicMod.INSTANCE, 10);
            }
        }
        user.addTemporaryTrait(Trait.succubus, 10);
        user.addTemporaryTrait(Trait.soulsucker, 10);
        user.addTemporaryTrait(Trait.energydrain, 10);
        user.addTemporaryTrait(Trait.spiritphage, 10);
        user.body.temporaryAddOrReplacePartWithType(WingsPart.demonicslime, 10);
        user.body.temporaryAddOrReplacePartWithType(TailPart.demonicslime, 10);
        user.body.temporaryAddOrReplacePartWithType(EarPart.pointed, 10);
        BreastsPart part = user.body.getBreastsBelow(BreastsPart.h.getSize());
        if (part != null) {
            user.body.temporaryAddOrReplacePartWithType(part.upgrade().upgrade(), 10);
        }

        int strength = Math.max(10, user.get(Attribute.slime)) * 2 / 3;
        if (user.has(Trait.Masquerade)) {
            strength = strength * 3 / 2;
        }
        user.add(c, new AttributeBuff(user.getType(), Attribute.darkness, strength, 10));
        user.add(c, new SlimeMimicry("succubus", user.getType(), 10));
        user.body.temporaryAddPartMod("pussy", DemonicMod.INSTANCE, 10);
        user.body.temporaryAddPartMod("cock", CockMod.incubus, 10);

        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new MimicSuccubus();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You shift your slime into a demonic form.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format("{self:NAME-POSSESSIVE} mercurial form seems to suddenly expand, then collapse onto itself. "
                        + "Her crystal blue goo glimmers and shifts into a deep obsidian. After reforming her features out of "
                        + "her erratically flowing slime, {other:subject-action:see|sees} that she has taken on an appearance reminiscent of Reyka's succubus form, "
                        + "complete with large translucent gel wings, a thick tail and her characteristic lascivious grin.", user, target);
    }

}
