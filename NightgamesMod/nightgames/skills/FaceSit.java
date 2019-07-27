package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.mods.FeralMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.FaceSitting;
import nightgames.status.BodyFetish;
import nightgames.status.Enthralled;
import nightgames.status.Shamed;

public class FaceSit extends Skill {

    public FaceSit() {
        super("Facesit");
        addTag(SkillTag.pleasureSelf);
        addTag(SkillTag.dominant);
        addTag(SkillTag.facesit);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getLevel() >= 10 || user.getAttribute(Attribute.seduction) >= 30;
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return user.has(Trait.lacedjuices) || user.has(Trait.addictivefluids)
                        || (user.body.has("pussy") && user.body.
                                        getRandomPussy().moddedPartCountsAs(user, FeralMod.INSTANCE)) ? 2.5f : 0;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.crotchAvailable() && user.canAct() && c.getStance().dom(user)
                        && c.getStance().prone(target) && !c.getStance().penetrated(c, user)
                        && !c.getStance().inserted(user) && c.getStance().prone(target)
                        && !user.has(Trait.shy);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Shove your crotch into your opponent's face to demonstrate your superiority";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (user.has(Trait.enthrallingjuices) && Random.random(4) == 0 && !target.wary()) {
            writeOutput(c, Result.special, user, target);
            target.add(c, new Enthralled(target.getType(), user.getType(), 5));
        } else {
            writeOutput(c, user.has(Trait.lacedjuices) ? Result.strong : Result.normal, user, target);
        }
        
        int m = 10;
        if (target.has(Trait.silvertongue)) {
            m = m * 3 / 2;
        }
        if (user.hasBalls()) {
            user.body.pleasure(target, target.body.getRandom("mouth"), user.body.getRandom("balls"), m, c, new SkillUsage<>(this, user, target));
        } else {
            user.body.pleasure(target, target.body.getRandom("mouth"), user.body.getRandom("pussy"), m, c, new SkillUsage<>(this, user, target));
            
            if (Random.random(100) < 1 + user.getAttribute(Attribute.fetishism) / 2) {
                target.add(c, new BodyFetish(target.getType(), user.getType(), "pussy", .05));
            }
        }
        double n = 4 + Random.random(4) + user.body.getHotness(target);
        if (target.has(Trait.imagination)) {
            n *= 1.5;
        }

        target.temptWithSkill(c, user, user.body.getRandom("ass"), (int) Math.round(n / 2), this);
        target.temptWithSkill(c, user, user.body.getRandom("pussy"), (int) Math.round(n / 2), this);

        target.loseWillpower(c, 5);
        target.add(c, new Shamed(target.getType()));
        if (!c.getStance().isFaceSitting(user)) {
            c.setStance(new FaceSitting(user.getType(), target.getType()), user, true);
        }
        int fetishChance = 5 + 2 * user.getAttribute(Attribute.fetishism);
        if (user.has(Trait.bewitchingbottom)) {
            fetishChance *= 2;
        }
        if (Random.random(100) < fetishChance) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), "ass", .25));
        }
      
        return true;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 25;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (user.hasBalls() && !user.hasPussy()) {
            return "Teabag";
        } else if (!c.getStance().isFaceSitting(user)) {
            return "Facesit";
        } else {
            return "Ride Face";
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (user.hasBalls()) {
            if (modifier == Result.special) {
                return "You crouch over " + target.nameOrPossessivePronoun()
                                + " face and dunk your balls into her mouth. She can do little except lick them submissively, which does feel "
                                + "pretty good. She's so affected by your manliness that her eyes glaze over and she falls under your control. Oh yeah. You're awesome.";
            } else if (modifier == Result.strong) {
                return "You crouch over " + target.nameOrPossessivePronoun()
                                + " face and dunk your balls into her mouth. She can do little except lick them submissively, which does feel "
                                + "pretty good. Your powerful musk is clearly starting to turn her on. Oh yeah. You're awesome.";
            } else {
                return "You crouch over " + target.nameOrPossessivePronoun()
                                + " face and dunk your balls into her mouth. She can do little except lick them submissively, which does feel "
                                + "pretty good. Oh yeah. You're awesome.";
            }
        } else {
            if (modifier == Result.special) {
                return "You straddle " + target.nameOrPossessivePronoun()
                                + " face and grind your pussy against her mouth, forcing her to eat you out. Your juices take control of her lust and "
                                + "turn her into a pussy licking slave. Ooh, that feels good. You better be careful not to get carried away with this.";
            } else if (modifier == Result.strong) {
                return "You straddle " + target.nameOrPossessivePronoun()
                                + " face and grind your pussy against her mouth, forcing her to eat you out. She flushes and seeks more of your tainted juices. "
                                + "Ooh, that feels good. You better be careful not to get carried away with this.";
            } else {
                return "You straddle " + target.nameOrPossessivePronoun()
                                + " face and grind your pussy against her mouth, forcing her to eat you out. Ooh, that feels good. You better be careful "
                                + "not to get carried away with this.";
            }
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (user.hasBalls()) {
            if (modifier == Result.special) {
                return String.format("%s straddles %s head and dominates %s by putting %s balls in %s mouth. "
                                + "For some reason, %s mind seems to cloud over and %s %s "
                                + "desperate to please %s. %s gives a superior smile as %s obediently %s on %s nuts.",
                                user.subject(), target.nameOrPossessivePronoun(), target.directObject(),
                                user.possessiveAdjective(), target.possessiveAdjective(),
                                target.nameOrPossessivePronoun(), target.pronoun(),
                                target.action("are", "is"), user.directObject(),
                                Formatter.capitalizeFirstLetter(user.subject()),
                                target.subject(), target.action("suck"), user.possessiveAdjective());
            } else if (modifier == Result.strong) {
                return String.format("%s straddles %s head and dominates %s by putting %s balls in %s mouth. "
                                + "Despite the humiliation, %s scent is turning %s on incredibly. "
                                + "%s gives a superior smile as %s obediently %s on %s nuts.",
                                user.subject(), target.nameOrPossessivePronoun(), target.directObject(),
                                user.possessiveAdjective(), target.possessiveAdjective(),
                                user.nameOrPossessivePronoun(), target.subject(),
                                user.subject(), target.subject(), target.action("suck"),
                                user.possessiveAdjective());
            } else {
                return String.format("%s straddles %s head and dominates %s by putting %s balls in %s mouth. "
                                + "%s gives a superior smile as %s obediently %s on %s nuts.",
                                user.subject(), target.nameOrPossessivePronoun(), target.directObject(),
                                user.possessiveAdjective(),
                                target.possessiveAdjective(),
                                user.subject(), target.subject(), target.action("suck"),
                                user.possessiveAdjective());
            }
        } else {
            if (modifier == Result.special) {
                return String.format("%s straddles %s face and presses %s pussy against %s mouth. %s "
                                + "%s mouth and %s to lick %s freely offered muff, but %s just smiles "
                                + "while continuing to queen %s. As %s %s %s juices, %s %s"
                                + " eyes start to bore into %s mind. %s can't resist %s. %s %s even want to.",
                                user.subject(), target.nameOrPossessivePronoun(), user.possessiveAdjective(),
                                target.possessiveAdjective(), target.subjectAction("open"), target.possessiveAdjective(),
                                target.action("start"), user.possessiveAdjective(), user.pronoun(),
                                target.directObject(), target.pronoun(),
                                target.action("drink"), user.possessiveAdjective(),
                                target.subjectAction("feel"), user.nameOrPossessivePronoun(),
                                target.possessiveAdjective(),
                                Formatter.capitalizeFirstLetter(target.pronoun()),
                                Formatter.capitalizeFirstLetter(target.pronoun()),
                                user.nameDirectObject(), target.action("don't", "doesn't"));
            } else if (modifier == Result.strong) {
                return String.format("%s straddles %s face and presses %s pussy against %s mouth. %s "
                                + "%s mouth and start to lick %s freely offered muff, but %s just smiles "
                                + "while continuing to queen %s. %s %s body start to heat up as %s "
                                + "juices flow into %s mouth, %s %s giving %s a mouthful of aphrodisiac straight from "
                                + "the source!", user.subject(), target.nameOrPossessivePronoun(),
                                user.possessiveAdjective(), target.possessiveAdjective(), target.subjectAction("open"),
                                target.possessiveAdjective(), user.nameDirectObject(), user.pronoun(),
                                 target.directObject(), Formatter.capitalizeFirstLetter(target.subjectAction("feel")),
                                 target.possessiveAdjective(), user.nameOrPossessivePronoun(), target.possessiveAdjective(),
                                 user.pronoun(), user.action("are", "is"), target.directObject());
            } else {
                return String.format("%s straddles %s face and presses %s pussy against %s mouth. %s "
                                + "%s mouth and start to lick %s freely offered muff, but %s just smiles "
                                + "while continuing to queen %s. %s clearly doesn't mind accepting some pleasure"
                                + " to demonstrate %s superiority.",user.subject(), target.nameOrPossessivePronoun(),
                                user.possessiveAdjective(), target.possessiveAdjective(), target.subjectAction("open"),
                                target.possessiveAdjective(), user.nameDirectObject(), user.pronoun(),
                                 target.directObject(), Formatter.capitalizeFirstLetter(user.pronoun()), user.possessiveAdjective());
            }
        }
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
