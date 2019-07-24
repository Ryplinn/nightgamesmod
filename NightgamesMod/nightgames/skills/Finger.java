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

public class Finger extends Skill {

    public Finger() {
        super("Finger");
        addTag(SkillTag.usesHands);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().reachBottom(user)
                        && (target.crotchAvailable() || user.has(Trait.dexterous)
                                        && target.getOutfit().getTopOfSlot(ClothingSlot.bottom).getLayer() <= 1)
                        && target.hasPussy() && user.canAct() && !c.getStance().vaginallyPenetrated(c, target);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            int m = Random.random(8, 13);
            if (user.get(Attribute.seduction) >= 8) {
                m += 6;
                if (user.human()) {
                    c.write(user, deal(c, m, Result.normal, user, target));
                } else {
                    c.write(user, receive(c, 0, Result.normal, user, target));
                }
                target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandom("pussy"), m,
                                c, new SkillUsage<>(this, user, target));
            } else {
                if (user.human()) {
                    c.write(user, deal(c, m, Result.weak, user, target));
                } else {
                    c.write(user, receive(c, 0, Result.weak, user, target));
                }
                target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandom("pussy"), m,
                                c, new SkillUsage<>(this, user, target));
            }
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
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
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.seduction) >= 5;
    }

    @Override
    public Skill copy(Character user) {
        return new Finger();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You grope at " + target.getName() + "'s pussy, but miss. (Maybe you should get closer?)";
        }
        if (modifier == Result.weak) {
            return "You grope between " + target.getName()
                            + "'s legs, not really knowing what you're doing. You don't know where she's the most sensitive, so you rub and "
                            + "stroke every bit of moist flesh under your fingers.";
        } else {
            if (target.getArousal().get() <= 15) {
                return "You softly rub the petals of " + target.getName() + "'s closed flower.";
            } else if (target.getArousal().percent() < 50) {
                return target.getName()
                                + "'s sensitive lower lips start to open up under your skilled touch and you can feel her becoming wet.";
            } else if (target.getArousal().percent() < 80) {
                return "You locate " + target.getName()
                                + "'s clitoris and caress it directly, causing her to tremble from the powerful stimulation.";
            } else {
                return "You stir " + target.getName()
                                + "'s increasingly soaked pussy with your fingers and rub her clit with your thumb.";
            }
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s gropes at %s pussy, but misses the mark.",
                            user.subject(), target.nameOrPossessivePronoun());
        }
        if (modifier == Result.weak) {
            return String.format("%s gropes between %s legs, not really knowing what %s is doing. "
                            + "%s doesn't know where %s the most sensitive, so %s rubs and "
                            + "strokes every bit of %s moist flesh under %s fingers.",
                            user.subject(), target.nameOrPossessivePronoun(), user.pronoun(),
                            user.subject(), target.subjectAction("are", "is"), user.pronoun(),
                            target.possessiveAdjective(), user.possessiveAdjective());
        } else {
            if (target.getArousal().get() <= 15) {
                return String.format("%s softly rubs the petals of %s closed flower.",
                                user.subject(), target.nameOrPossessivePronoun());
            } else if (target.getArousal().percent() < 50) {
                return String.format("%s sensitive lower lips start to open up under"
                                + " %s skilled touch and %s can feel %s becoming wet.",
                                target.nameOrPossessivePronoun(), user.nameOrPossessivePronoun(),
                                target.pronoun(), target.reflectivePronoun());
            } else if (target.getArousal().percent() < 80) {
                return String.format("%s locates %s clitoris and caress it directly, causing"
                                + " %s to tremble from the powerful stimulation.",
                                user.subject(), target.nameOrPossessivePronoun(), target.directObject());
            } else {
                return String.format("%s stirs %s increasingly soaked pussy with %s fingers and "
                                + "rubs %s clit directly with %s thumb.",
                                user.subject(), target.nameOrPossessivePronoun(),
                                user.possessiveAdjective(), target.possessiveAdjective(),
                                user.possessiveAdjective());
            }
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Digitally stimulate opponent's pussy, difficult to land without pinning her down.";
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
