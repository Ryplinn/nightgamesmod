package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.ClothingSlot;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.status.BodyFetish;

public class AssJob extends Skill {

    AssJob() {
        super("Assjob");
        addTag(SkillTag.anal);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 25;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && target.hasDick() && selfNakedOrUnderwear(user)
                        && !c.getStance().havingSex(c, target)
                        && !c.getStance().facing(user, target)
                        && (c.getStance().behind(target)
                                        || (c.getStance().en == Stance.reversemount && c.getStance().dom(user))
                                        || c.getStance().mobile(user) && !c.getStance().prone(user)
                                                        && !c.getStance().behind(user));
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Hump your opponent's cock with your ass";
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return c.getStance().behind(target) ? 200 : 75;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (c.getStance().behind(target)) {
            writeOutput(c, Result.special, user, target);
            int m = Random.random(10, 14);
            int fetishChance = 20 + user.getAttribute(Attribute.fetishism) / 2;
            if (target.crotchAvailable()) {
                if (user.crotchAvailable()) {
                    m += 6;
                    fetishChance += 30;
                } else {
                    m += 3;
                    fetishChance += 15;
                }
                if (user.has(Trait.bewitchingbottom)) {
                    fetishChance *= 2;
                }
            }
            target.body.pleasure(user, user.body.getRandomAss(), target.body.getRandomCock(), m, c, new SkillUsage<>(this, user, target));

            if (Random.random(100) < fetishChance) {
                target.add(c, new BodyFetish(target.getType(), user.getType(), "ass", .1 + user.getAttribute(Attribute.fetishism) * .05));
            }
        } else if (rollSucceeded) {
            if (c.getStance().en == Stance.reversemount) {
                writeOutput(c, Result.strong, user, target);
                int m = Random.random(14, 19);
                int fetishChance = 20 + user.getAttribute(Attribute.fetishism) / 2;
                if (target.crotchAvailable()) {
                    if (user.crotchAvailable()) {
                        m += 6;
                        fetishChance += 30;
                    } else {
                        m += 3;
                        fetishChance += 15;
                    }
                    if (user.has(Trait.bewitchingbottom)) {
                        fetishChance *= 2;
                    }
                }
                if (target.body.getRandomCock().isReady(target)) {
                    target.body.pleasure(user, user.body.getRandomAss(), target.body.getRandomCock(), m, c, new SkillUsage<>(this, user, target));
                } else {
                    target.temptWithSkill(c, user, user.body.getRandomAss(), m, this);
                }

                if (Random.random(100) < fetishChance) {
                    target.add(c, new BodyFetish(target.getType(), user.getType(), "ass", .1 + user.getAttribute(Attribute.fetishism) * .05));
                }
            } else {
                writeOutput(c, Result.normal, user, target);
                int m = Random.random(10, 14);
                if (target.crotchAvailable()) {
                    if (user.crotchAvailable()) {
                        m += 6;
                    } else {
                        m += 3;
                    }
                }
                target.body.pleasure(user, user.body.getRandomAss(), target.body.getRandomCock(), m, c, new SkillUsage<>(this, user, target));
            }
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    private boolean selfNakedOrUnderwear(Character user) {
        return user.getOutfit().slotEmptyOrMeetsCondition(ClothingSlot.bottom, c -> c.getLayer() == 0);
    }

    private boolean selfWearingUnderwear(Character user) {
        return user.getOutfit().getSlotAt(ClothingSlot.bottom, 0) != null;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        switch (modifier) {
            case special:
                if (user.crotchAvailable() && target.crotchAvailable()) {
                    return String.format("You push your naked ass back against" + " %s %s, rubbing it with vigor.",
                                    target.nameOrPossessivePronoun(), target.body.getRandomCock().describe(target));
                } else {
                    return String.format("You relax slightly in %s arms and rub your ass" + " into %s crotch.",
                                    target.nameOrPossessivePronoun(), target.possessiveAdjective());
                }
            case strong:
                if (!target.crotchAvailable()) {
                    return String.format("You hump your ass against %s covered groin.",
                                    target.nameOrPossessivePronoun());
                } else if (target.body.getRandomCock().isReady(user)) {
                    return String.format(
                                    "You wedge %s %s in your soft crack and"
                                                    + " firmly rub it up against you, eliciting a quiet moan from"
                                                    + " %s.",
                                    target.nameOrPossessivePronoun(), target.body.getRandomCock().describe(target),
                                    target.directObject());
                } else {
                    return String.format(
                                    "You lean back and rub your ass against %s, but"
                                                    + " %s %s is still too soft to really get into it.",
                                    target.getName(), target.possessiveAdjective(),
                                    target.body.getRandomCock().describe(target));
                }
            case normal:
                return String.format("You back up against %s and grab %s by the waist."
                                + " Before %s has a chance to push you away, you rub your ass against" + " %s crotch.",
                                target.getName(), target.directObject(), target.pronoun(), target.possessiveAdjective());
            case miss:
            default:
                return String.format("You try to mash your ass against %s crotch, but %s" + " pushes you away.",
                                target.nameOrPossessivePronoun(), target.pronoun());
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        switch (modifier) {
            case special:
                String res = String.format(
                                "%s %s tight, thinking %s intends to break "
                                                + "free from %s hold, but instead %s pushes %s firm asscheeks"
                                                + " against %s cock and grinds them against %s. ",
                                target.subjectAction("hold"), user.getName(), user.pronoun(),
                                target.possessiveAdjective(), user.pronoun(),
                                user.possessiveAdjective(), target.possessiveAdjective(), target.directObject());
                if (user.crotchAvailable() && target.crotchAvailable()) {
                    res += String.format("%s %s slides between %s mounds as if it belongs there.",
                                    target.possessiveAdjective(), target.body.getRandomCock().describe(target), 
                                    user.possessiveAdjective());
                } else {
                    res += String.format(
                                    "The swells of %s ass feel great on %s cock even through the clothing between %s.",
                                    user.possessiveAdjective(), target.possessiveAdjective(), c.bothDirectObject(target));
                }
                return res;
            case strong:
                if (!target.crotchAvailable()) {
                    return String.format(
                                    "%s sits firmly on %s crotch and starts "
                                                    + "dryhumping %s with an impish grin. As %s grinds against %s "
                                                    + "%s restlessly, %s %s definitely feeling it much more than %s is.",
                                    user.getName(), target.nameOrPossessivePronoun(), target.directObject(),
                                    user.pronoun(), target.possessiveAdjective(),
                                    target.outfit.getTopOfSlot(ClothingSlot.bottom).getName(),
                                    target.pronoun(), target.action("are", "is"), user.pronoun());
                } else if (target.body.getRandomCock().isReady(user)) {
                    return String.format(
                                    "%s lays back on %s, squeezing %s %s between %s soft asscheeks. %s %s to "
                                                    + "crawl away, but %s grinds %s perky butt against %s, massaging %s hard-on %s.",
                                    user.getName(), target.subject(), target.possessiveAdjective(),
                                    target.body.getRandomCock().describe(user),
                                    user.possessiveAdjective(),
                                    Formatter.capitalizeFirstLetter(target.pronoun()),
                                    target.action("try", "tries"),
                                    user.pronoun(), user
                                                    .possessiveAdjective(),
                                                    target.directObject(),
                                                    target.possessiveAdjective(),
                                    selfWearingUnderwear(user)
                                                    ? "with "+user.possessiveAdjective()+" soft " + user.getOutfit()
                                                                    .getBottomOfSlot(ClothingSlot.bottom).getName()
                                                    : "in "+user.possessiveAdjective()+" luscious crack");
                } else {
                    return String.format(
                                    "%s to slide from under %s, but %s leans "
                                                    + "forward, holding down %s legs. %s feel %s round ass press"
                                                    + " against %s groin as %s sits back on %s. <i>\"Like what "
                                                    + "you see?\"</i> - %s taunts %s, shaking %s hips invitingly.",
                                                    target.subjectAction("try", "tries"),
                                    user.getName(), user.pronoun(),
                                    target.possessiveAdjective(), Formatter.capitalizeFirstLetter(target.pronoun()),
                                    user.possessiveAdjective(), target.possessiveAdjective(),
                                    user.pronoun(), target.directObject(),
                                    user.pronoun(), target.directObject(), user.possessiveAdjective());
                }
            case normal:
                return String.format(
                                "Unexpectedly, %s turns around and rams %s waist against "
                                                + "%s groin, taking hold of %s arms before %s can recover %s balance."
                                                + " %s takes the opportunity to tease %s, rubbing %s bubble butt against "
                                                + "%s sensitive %s.",
                                user.getName(), user.possessiveAdjective(),
                                target.possessiveAdjective(), target.possessiveAdjective(), target.pronoun(), target.possessiveAdjective(),
                                Formatter.capitalizeFirstLetter(user.pronoun()),
                                target.directObject(), user.possessiveAdjective(),
                                target.possessiveAdjective(), target.body.getRandomCock().describe(target));
            case miss:
            default:
                return String.format("%s moves %s ass towards %s crotch, but %s %s her away.", user.getName(),
                                user.possessiveAdjective(), target.nameOrPossessivePronoun(), target.pronoun(),
                                target.action("push", "pushes"));
        }
    }

}
