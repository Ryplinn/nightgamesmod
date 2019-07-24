package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.items.clothing.ClothingSlot;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;

public class Handjob extends Skill {

    public Handjob() {
        this("Handjob");
        addTag(SkillTag.usesHands);
        addTag(SkillTag.pleasure);
    }

    public Handjob(String string) {
        super(string);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().reachBottom(user)
                        && (target.crotchAvailable() || user.has(Trait.dexterous)
                                        && target.getOutfit().getTopOfSlot(ClothingSlot.bottom).getLayer() <= 1)
                        && target.hasDick() && user.canAct()
                        && (!c.getStance().inserted(target));
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 7;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return c.getStance().en == Stance.neutral ? 50 : 100;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int m = Random.random(8, 13);

        if (target.roll(user, accuracy(c, user, target))) {
            if (user.get(Attribute.seduction) >= 8) {
                m += 6;
                writeOutput(c, Result.normal, user, target);
                target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandom("cock"), m, c, new SkillUsage<>(this, user, target));
            } else {
                writeOutput(c, Result.weak, user, target);
                target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandom("cock"), m, c, new SkillUsage<>(this, user, target));
            }
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return !user.has(Trait.temptress) && user.get(Attribute.seduction) >= 5;
    }

    @Override
    public Skill copy(Character user) {
        return new Handjob();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You reach for " + target.getName() + "'s dick but miss. (Maybe you should get closer?)";
        } else {
            return "You grab " + target.getName()
                            + "'s girl-cock and stroke it using the techniques you use when masturbating.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s grabs for %s dick and misses.",
                            user.subject(), target.nameOrPossessivePronoun());
        }
        int r;
        if (!target.crotchAvailable()) {
            return String.format("%s slips %s hand into %s %s and strokes %s dick.",
                            user.subject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(),
                            target.getOutfit().getTopOfSlot(ClothingSlot.bottom).getName(),
                            target.possessiveAdjective());
        } else if (modifier == Result.weak) {
            return String.format("%s clumsily fondles %s crotch. It's not skillful by"
                            + " any means, but it's also not entirely ineffective.",
                            user.subject(), target.nameOrPossessivePronoun());
        } else {
            if (target.getArousal().get() < 15) {
                return String.format("%s grabs %s soft penis and plays with the sensitive organ "
                                + "until it springs into readiness.",
                                user.subject(), target.nameOrPossessivePronoun());
            }

            else if ((r = Random.random(3)) == 0) {
                return String.format("%s strokes and teases %s dick, sending shivers of pleasure up %s spine.",
                                user.subject(), target.nameOrPossessivePronoun(),
                                target.possessiveAdjective());
            } else if (r == 1) {
                return String.format("%s rubs the sensitive head of %s penis and fondles %s balls.",
                                user.subject(), target.nameOrPossessivePronoun(),
                                target.possessiveAdjective());
            } else {
                return String.format("%s jerks %s off like she's trying to milk every drop of %s cum.",
                                user.subject(), target.subject(),
                                target.possessiveAdjective());
            }
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Rub your opponent's dick";
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
