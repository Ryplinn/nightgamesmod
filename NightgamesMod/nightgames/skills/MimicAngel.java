package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.WingsPart;
import nightgames.characters.body.mods.DivineMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.GameState;
import nightgames.status.AttributeBuff;
import nightgames.status.SlimeMimicry;
import nightgames.status.Stsflag;

public class MimicAngel extends Skill {

    MimicAngel() {
        super("Mimicry: Angel");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.human() && user.get(Attribute.slime) >= 10;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && !user.is(Stsflag.mimicry) && GameState.getGameState().characterPool.getNPC("Angel").has(Trait.demigoddess);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Mimics an angel's powers";
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
            user.addTemporaryTrait(Trait.divinity, 10);
            if (user.getLevel() >= 20) {
                user.addTemporaryTrait(Trait.objectOfWorship, 10);
            }
            if (user.getLevel() >= 28) {
                user.addTemporaryTrait(Trait.lastStand, 10);
            }
            if (user.getLevel() >= 36) {
                user.addTemporaryTrait(Trait.erophage, 10);
            }
            if (user.getLevel() >= 44) {
                user.addTemporaryTrait(Trait.sacrosanct, 10);
            }
            if (user.getLevel() >= 52) {
                user.addTemporaryTrait(Trait.genuflection, 10);
            }
            if (user.getLevel() >= 60) {
                user.addTemporaryTrait(Trait.revered, 10);
            }
        }
        user.body.temporaryAddOrReplacePartWithType(WingsPart.angelicslime, 10);
        BreastsPart part = user.body.getBreastsBelow(BreastsPart.h.getSize());
        if (part != null) {
            user.body.temporaryAddOrReplacePartWithType(part.upgrade().upgrade(), 10);
        }
        int strength = Math.max(10, user.get(Attribute.slime)) * 2 / 3;
        if (user.has(Trait.Masquerade)) {
            strength = strength * 3 / 2;
        }
        user.add(c, new AttributeBuff(user.getType(), Attribute.divinity, strength, 10));
        user.add(c, new SlimeMimicry("angel", user.getType(), 10));
        user.body.temporaryAddPartMod("pussy", DivineMod.INSTANCE, 10);
        user.body.temporaryAddPartMod("cock", CockMod.blessed, 10);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You shift your slime and start mimicking Angel's... angel form.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format("{self:NAME-POSSESSIVE} amorphous body jiggles violently and she shrinks her body into a sphere. "
                        + "{other:SUBJECT} cautiously {other:action:approach|approaches} the unknown object, but hesitate when {other:pronoun-action:see|sees} it suddenly turns pure white "
                        + "as if someone dumped a bucket of bleach on it. "
                        + "The sphere unwraps itself in layers, with each layer forming a pair of pristine translucent gelatinous feathered wings. "
                        + "{self:NAME} {self:reflective} stands up in the center, giving {other:name-do} a haughty look. "
                        + "Looks like {self:NAME} is mimicking Angel's er... angel form!", user, target);
    }

}
