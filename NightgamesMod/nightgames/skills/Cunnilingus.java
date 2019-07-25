package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.ReverseMount;
import nightgames.stance.SixNine;
import nightgames.status.Enthralled;

@SuppressWarnings("unused")
public class Cunnilingus extends Skill {

    public Cunnilingus() {
        super("Lick Pussy");
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.oral);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        boolean canUse = c.getStance().isBeingFaceSatBy(user, target) && user.canRespond()
                        || user.canAct();
        boolean pussyAvailable = target.crotchAvailable() && target.hasPussy();
        boolean stanceAvailable = c.getStance().oral(user, target) && (!c.getStance().vaginallyPenetrated(c, target) || c.getStance().getPartsFor(c, user, target).contains(user.body.getRandom("mouth")));
        return pussyAvailable && stanceAvailable && canUse;
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return user.has(Trait.silvertongue) ? 1 : 0;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        if (c.getStance().isBeingFaceSatBy(user, c.getOpponent(user))) {
            return 0;
        } else {
            return 5;
        }
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Result results = Result.normal;
        boolean facesitting = c.getStance().isBeingFaceSatBy(user, target);
        int m = 10 + Random.random(8);
        if (user.has(Trait.silvertongue)) {
            m += 4;
        }
        int i = 0;
        if (!facesitting && c.getStance().mobile(target) && !target.roll(user, accuracy(c, user, target))) {
            results = Result.miss;
        } else {
            if (target.has(Trait.enthrallingjuices) && Random.random(4) == 0 && !target.wary()) {
                i = -2;
            } else if (target.has(Trait.lacedjuices)) {
                i = -1;
                user.temptNoSource(c, target, 5, this);
            }
            if (facesitting) {
                results = Result.reverse;
            }
        }
        writeOutput(c, i, results, user, target);
        if (i == -2) {
            user.add(c, new Enthralled(user.getType(), target.getType(), 3));
        }
        if (results != Result.miss) {
            if (results == Result.reverse) {
                target.buildMojo(c, 10);
            }
            if (c.getStance() instanceof ReverseMount) {
                c.setStance(new SixNine(user.getType(), target.getType()), user, true);
            }
            target.body.pleasure(user, user.body.getRandom("mouth"), target.body.getRandom("pussy"), m, c, new SkillUsage<>(this, user, target));
        }
        return results != Result.miss;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 10;
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return !c.getStance().isBeingFaceSatBy(user, target) && c.getStance().reachTop(target)? 75 : 200;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You try to eat out " + target.getName() + ", but she pushes your head away.";
        }
        if (target.getArousal().get() < 10) {
            return "You run your tongue over " + target.getName() + "'s dry vulva, lubricating it with your saliva.";
        }
        if (modifier == Result.special) {
            return "Your skilled tongue explores " + target.getName()
                            + "'s pussy, finding and pleasuring her more sensitive areas. You frequently tease her clitoris until she "
                            + "can't suppress her pleasured moans."
                            + (damage == -1 ? " Under your skilled ministrations, her juices flow freely, and they unmistakably"
                                            + " have their effect on you."
                                            : "")
                            + (damage == -2 ? " You feel a strange pull on you mind,"
                                            + " somehow she has managed to enthrall you with her juices." : "");
        }
        if (modifier == Result.reverse) {
            return "You resign yourself to lapping at " + target.nameOrPossessivePronoun()
                            + " pussy, as she dominates your face with her ass."
                            + (damage == -1 ? " Under your skilled ministrations, her juices flow freely, and they unmistakably"
                                            + " have their effect on you."
                                            : "")
                            + (damage == -2 ? " You feel a strange pull on you mind,"
                                            + " somehow she has managed to enthrall you with her juices." : "");
        }
        if (target.getArousal().percent() > 80) {
            return "You relentlessly lick and suck the lips of " + target.getName()
                            + "'s pussy as she squirms in pleasure. You let up just for a second before kissing her"
                            + " swollen clit, eliciting a cute gasp."
                            + (damage == -1 ? " The highly aroused succubus' vulva is dripping with her "
                                            + "aphrodisiac juices and you consume generous amounts of them."
                                            : "")
                            + (damage == -2 ? " You feel a strange pull on you mind,"
                                            + " somehow she has managed to enthrall you with her juices." : "");
        }
        int r = Random.random(3);
        if (r == 0) {
            return "You gently lick " + target.getName() + "'s pussy and sensitive clit."
                            + (damage == -1 ? " As you drink down her juices, they seem to flow "
                                            + "straight down to your crotch, lighting fires when they arrive."
                                            : "")
                            + (damage == -2 ? " You feel a strange pull on you mind,"
                                            + " somehow she has managed to enthrall you with her juices." : "");
        }
        if (r == 1) {
            return "You thrust your tongue into " + target.getName() + "'s hot vagina and lick the walls of her pussy."
                            + (damage == -1 ? " Your tongue tingles with her juices, clouding your mind with lust."
                                            : "")
                            + (damage == -2 ? " You feel a strange pull on you mind,"
                                            + " somehow she has managed to enthrall you with her juices." : "");
        }
        return "You locate and capture " + target.getName() + "'s clit between your lips and attack it with your tongue."
                        + (damage == -1 ? " Her juices taste wonderful and you cannot help but desire more." : "")
                        + (damage == -2 ? " You feel a strange pull on you mind,"
                                        + " somehow she has managed to enthrall you with her juices." : "");
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String special;
        switch (damage) {
            case -1:
                special = String.format(" %s aphrodisiac juices manage to arouse %s as much as %s aroused %s.", 
                                target.nameOrPossessivePronoun(), user.nameDirectObject(),
                                user.pronoun(), target.nameDirectObject());
                break;
            case -2:
                special = String.format(" %s tainted juices quickly reduce %s into a willing thrall.",
                                target.nameOrPossessivePronoun(), user.nameDirectObject());
                break;
            default:
                special = "";
        }
        if (modifier == Result.miss) {
            return String.format("%s tries to tease %s cunt with %s mouth, but %s %s %s face away from %s box.",
                            user.subject(), target.nameOrPossessivePronoun(), user.possessiveAdjective(),
                            target.pronoun(), target.action("push", "pushes"), user.nameOrPossessivePronoun(),
                            target.possessiveAdjective());
        } else if (modifier == Result.special) {
            return String.format("%s skilled tongue explores %s pussy, finding and pleasuring %s more sensitive areas. "
                            + "%s repeatedly attacks %s clitoris until %s can't suppress %s pleasured moans.%s",
                            user.nameOrPossessivePronoun(), target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(user.pronoun()), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.possessiveAdjective(), special);
        } else if (modifier == Result.reverse) {
            return String.format("%s obediently laps at %s pussy as %s %s on %s face.%s",
                            user.subject(), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("sit"), user.possessiveAdjective(),
                            special);
        }
        return String.format("%s locates and captures %s clit between %s lips and attacks it with %s tongue.%s", 
                        user.subject(), target.nameOrPossessivePronoun(), user.possessiveAdjective(),
                        user.possessiveAdjective(), special);
    }
    
    @Override
    public String describe(Combat c, Character user) {
        return "Perform cunnilingus on opponent";
    }
}
