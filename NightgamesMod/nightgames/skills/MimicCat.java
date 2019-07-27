package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Kat;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.EarPart;
import nightgames.characters.body.TailPart;
import nightgames.characters.body.mods.FeralMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.GameState;
import nightgames.status.AttributeBuff;
import nightgames.status.SlimeMimicry;
import nightgames.status.Stsflag;

public class MimicCat extends Skill {

    MimicCat() {
        super("Mimicry: Werecat");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.human() && user.getAttribute(Attribute.slime) >= 10;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && !user.is(Stsflag.mimicry) && GameState.getGameState().characterPool.characterTypeInGame(Kat.class.getSimpleName());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Mimics a werecat";
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
            user.addTemporaryTrait(Trait.pheromones, 10);
            if (user.getLevel() >= 20) {
                user.addTemporaryTrait(Trait.nymphomania, 10);
            }
            if (user.getLevel() >= 28) {
                user.addTemporaryTrait(Trait.catstongue, 10);
            }
            if (user.getLevel() >= 36) {
                user.addTemporaryTrait(Trait.FeralStrength, 10);
            }
            if (user.getLevel() >= 44) {
                user.addTemporaryTrait(Trait.BefuddlingFragrance, 10);
            }
            if (user.getLevel() >= 52) {
                user.addTemporaryTrait(Trait.Jackhammer, 10);
            }
            if (user.getLevel() >= 60) {
                user.addTemporaryTrait(Trait.Unsatisfied, 10);
            }
        }
        user.addTemporaryTrait(Trait.augmentedPheromones, 10);
        user.addTemporaryTrait(Trait.nymphomania, 10);
        user.addTemporaryTrait(Trait.lacedjuices, 10);
        user.addTemporaryTrait(Trait.catstongue, 10);
        user.addTemporaryTrait(Trait.FrenzyScent, 10);
        user.body.temporaryAddOrReplacePartWithType(TailPart.slimeycat, 10);
        user.body.temporaryAddOrReplacePartWithType(EarPart.cat, 10);
        BreastsPart part = user.body.getBreastsAbove(BreastsPart.a.getSize());
        if (part != null) {
            user.body.temporaryAddOrReplacePartWithType(part.downgrade(), 10);
        }

        int strength = Math.max(10, user.getAttribute(Attribute.slime)) * 2 / 3;
        if (user.has(Trait.Masquerade)) {
            strength = strength * 3 / 2;
        }
        user.add(c, new AttributeBuff(user.getType(), Attribute.animism, strength, 10));
        user.add(c, new SlimeMimicry("cat", user.getType(), 10));
        user.body.temporaryAddPartMod("pussy", FeralMod.INSTANCE, 10);
        user.body.temporaryAddPartMod("cock", CockMod.primal, 10);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You shift your slime and start mimicking Kat's werecat form.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format("{self:NAME-POSSESSIVE} amorphous body abruptly shifts as {other:subject-action:are|is} facing {self:direct-object}. "
                        + "Not sure what {self:pronoun} is doing, {other:subject} cautiously {other:action:approach|approaches}. Suddenly, {self:possessive} slime solidifies again, "
                        + "and a orange shadow pounces at {other:direct-object} from where {self:pronoun} was before. {other:SUBJECT-ACTION:manage|manages} to dodge it, but looking back at "
                        + "the formerly-crystal blue slime girl, {other:pronoun-action:see|sees} that {self:NAME} has transformed into a caricature of Kat's feral form, "
                        + "complete with faux cat ears and a slimy tail!", user, target);
    }

}
