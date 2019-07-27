package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.skills.damage.Staleness;
import nightgames.status.Hypersensitive;

public class Tickle extends Skill {
    public Tickle() {
        // tickle has higher decay but pretty fast recovery
        super("Tickle", 0, Staleness.build().withDefault(1.0).withFloor(.5).withDecay(.15).withRecovery(.20));
        addTag(SkillTag.weaken);
        addTag(SkillTag.staminaDamage);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && (c.getStance().mobile(user) || c.getStance().dom(user))
                        && (c.getStance().reachTop(user) || c.getStance().reachBottom(user));
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 7;
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        DamageType type = DamageType.technique;
        if (rollSucceeded) {
            if (target.crotchAvailable() && c.getStance().reachBottom(user) && !c.getStance().havingSex(c)) {
                int bonus = 0;
                int weak = 0;
                Result result = Result.normal;

                if (user.has(Item.Tickler2) && Random.random(2) == 1 && user.canSpend(10)) {
                    user.spendMojo(c, 10);
                    result = Result.special;
                    bonus += 2;
                    weak += 2;
                }
                if (hasTickler(user)) {
                    result = Result.strong;
                    bonus += 5 + Random.random(4);
                    weak += 3 + Random.random(4);
                    type = DamageType.gadgets;
                }
                writeOutput(c, result, user, target);
                if (result == Result.special) {
                    target.add(c, new Hypersensitive(target.getType(), 5));
                }
                if (target.has(Trait.ticklish)) {
                    bonus = 4 + Random.random(3);
                    c.write(target, Formatter.format(
                                    "{other:SUBJECT-ACTION:squirm|squirms} uncontrollably from {self:name-possessive} actions. Yup, definitely ticklish.",
                                    user, target));
                }
                SkillUsage usage = new SkillUsage<>(this, user, target);
                target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandom("skin"),
                                (int) type.modifyDamage(user, target, 2 + Random.random(4)), bonus, c, false, usage);
                target.weaken(c, (int) type.modifyDamage(user, target, weak + Random.random(10, 15)), usage);
            } else if (hasTickler(user) && Random.random(2) == 1) {
                type = DamageType.gadgets;
                int bonus = 0;
                if (target.breastsAvailable() && c.getStance().reachTop(user)) {
                    writeOutput(c, Result.item, user, target);
                    if (target.has(Trait.ticklish)) {
                        bonus = 4 + Random.random(3);
                        c.write(target, Formatter.format(
                                        "{other:SUBJECT-ACTION:squirm|squirms} uncontrollably from {self:name-possessive} actions. Yup definitely ticklish.",
                                        user, target));
                    }
                    target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandom("skin"),
                                    4 + Random.random(4), bonus, c, false, new SkillUsage<>(this, user, target));
                } else {
                    writeOutput(c, Result.weak, user, target);
                    if (target.has(Trait.ticklish)) {
                        bonus = 4 + Random.random(3);
                        c.write(target, Formatter.format(
                                        "{other:SUBJECT-ACTION:squirm|squirms} uncontrollably from {self:name-possessive} actions. Yup definitely ticklish.",
                                        user, target));
                    }
                    target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandom("skin"),
                                    4 + Random.random(2), bonus, c, false, new SkillUsage<>(this, user, target));
                }
                target.weaken(c, (int) type.modifyDamage(user, target, bonus + Random.random(5, 10)));
            } else {
                writeOutput(c, Result.normal, user, target);
                int bonus = 0;
                if (target.has(Trait.ticklish)) {
                    bonus = 2 + Random.random(3);
                    c.write(target, Formatter.format(
                                    "{other:SUBJECT-ACTION:squirm|squirms} uncontrollably from {self:name-possessive} actions. Yup definitely ticklish.",
                                    user, target));
                }
                int m = (int) Math.round((2 + Random.random(3)) * (.25 + target.getExposure()));
                int weak = (int) Math.round(bonus / 2.0 * (.25 + target.getExposure()));
                target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandom("skin"), (int) type
                                                .modifyDamage(user, target, m),
                                bonus, c, false, new SkillUsage<>(this, user, target));
                target.weaken(c, (int) type.modifyDamage(user, target, weak + Random.random(4, 7)));
            }
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.cunning) >= 5;
    }

    @Override
    public int speed(Character user) {
        return 7;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You try to tickle " + target.getName() + ", but she squirms away.";
        } else if (modifier == Result.special) {
            return "You work your fingers across " + target.getName()
                            + "'s most ticklish and most erogenous zones until she is writhing in pleasure and can't even make coherent words.";
        } else if (modifier == Result.critical) {
            return "You brush your tickler over " + target.getName()
                            + "'s body, causing her to shiver and retreat. When you tickle her again, she yelps and almost falls down. "
                            + "It seems like your special feathers made her more sensitive than usual.";
        } else if (modifier == Result.strong) {
            return "You run your tickler across " + target.getName()
                            + "'s sensitive thighs and pussy. She can't help but let out a quiet whimper of pleasure.";
        } else if (modifier == Result.item) {
            return "You tease " + target.getName()
                            + "'s naked upper body with your feather tickler, paying close attention to her nipples.";
        } else if (modifier == Result.weak) {
            return "You catch " + target.getName() + " off guard by tickling her neck and ears.";
        } else {
            return "You tickle " + target.getName() + "'s sides as she giggles and squirms.";
        }

    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to tickle %s, but fails to find a sensitive spot.",
                            user.subject(), target.nameDirectObject());
        } else if (modifier == Result.special) {
            return String.format("%s tickles %s nude body mercilessly, gradually working %s way to %s dick and balls. "
                            + "As %s fingers start tormenting %s privates, %s %s to "
                            + "clear %s head enough to keep from cumming immediately.", user.subject(),
                            target.nameOrPossessivePronoun(), user.possessiveAdjective(),
                            target.possessiveAdjective(), user.possessiveAdjective(), target.possessiveAdjective(),
                            target.pronoun(), target.action("struggle"), target.possessiveAdjective());
        } else if (modifier == Result.critical) {
            return String.format("%s teases %s privates with %s feather tickler. After %s stops,"
                            + " %s an unnatural sensitivity where the feathers touched %s.", user.subject(),
                            target.nameDirectObject(), user.possessiveAdjective(), user.pronoun(),
                            target.subjectAction("feel"), target.directObject());
        } else if (modifier == Result.strong) {
            return String.format("%s brushes %s tickler over %s balls and teases the sensitive head of %s penis.",
                            user.subject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), target.possessiveAdjective());
        } else if (modifier == Result.item) {
            return String.format("%s runs %s feather tickler across %s nipples and abs.",
                            user.subject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun());
        } else if (modifier == Result.weak) {
            return String.format("%s pulls out a feather tickler and teases any exposed skin %s can reach.",
                            user.subject(), user.pronoun());
        } else {
            return String.format("%s suddenly springs toward %s and tickles %s"
                            + " relentlessly until %s can barely breathe.", user.subject(),
                            target.nameDirectObject(), target.directObject(), target.pronoun());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Tickles opponent, weakening and arousing her. More effective if she's nude";
    }

    private boolean hasTickler(Character user) {
        return user.has(Item.Tickler) || user.has(Item.Tickler2);
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
