package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.body.mods.SizeMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.ClothingSlot;
import nightgames.stance.Stance;

public class FondleBreasts extends Skill {

    public FondleBreasts() {
        super("Fondle Breasts");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().reachTop(user) && target.hasBreasts() && user.canAct();
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 7;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        int m = 6 + Random.random(4);
        Result result = Result.normal;
        if (rollSucceeded) {
            if (target.breastsAvailable()) {
                m += 4;
                result = Result.strong;
            } else if (target.outfit.getTopOfSlot(ClothingSlot.top).getLayer() <= 1 && user.has(Trait.dexterous)) {
                m += 4;
                result = Result.special;
            }
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }

        writeOutput(c, result, user, target);
        target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandom("breasts"), m,
                        c, new SkillUsage<>(this, user, target));

        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public int speed(Character user) {
        return 6;
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return c.getStance().en == Stance.neutral ? 70 : 100;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You grope at " + target.getName() + "'s breasts, but miss. (Maybe you should get closer?)";
        } else if (modifier == Result.strong) {
            return "You massage " + target.getName()
            + "'s soft breasts and pinch her nipples, causing her to moan with desire.";
        } else if (modifier == Result.special) {
            return "You slip your hands into " + target.nameOrPossessivePronoun() + " " + target.outfit.getTopOfSlot(ClothingSlot.top).getName() + ", massaging " + target.getName()
            + "'s soft breasts and pinching her nipples.";
        } else {
            return "You massage " + target.getName() + "'s breasts over her "
                            + target.getOutfit().getTopOfSlot(ClothingSlot.top).getName() + ".";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s gropes at %s %s, but misses the mark.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            target.body.getRandomBreasts().describe(target));
        } else if (modifier == Result.strong) {
            return String.format("%s massages %s %s, and pinches %s nipples, causing %s to moan with desire.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            target.body.getRandomBreasts().describe(target),
                            target.possessiveAdjective(), target.directObject());
        } else if (modifier == Result.special) {
            return Formatter.format("{self:SUBJECT-ACTION:slip|slips} {self:possessive} agile fingers into {other:name-possessive} bra, massaging and pinching at {other:possessive} nipples.",
                            user, target);
        } else {
            return String.format("%s massages %s %s over %s %s.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            target.body.getRandomBreasts().describe(target), target.possessiveAdjective(),
                            target.getOutfit().getTopOfSlot(ClothingSlot.top).getName());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Grope your opponents breasts. More effective if she's topless";
    }

    @Override
    public String getLabel(Combat c, Character user) {
        return c.getOpponent(user).body.getBreastsAbove(SizeMod.getMinimumSize("breasts")) != null ? "Fondle Breasts"
                        : "Tease Chest";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FOREPLAY;
    }
}
