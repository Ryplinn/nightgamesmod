package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.status.Hypersensitive;
import nightgames.status.Winded;

public class Tickle extends Skill {

    public Tickle(Character self) {
        super("Tickle", self);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && (c.getStance().mobile(getSelf()) || c.getStance().dom(getSelf()))
                        && (c.getStance().reachTop(getSelf()) || c.getStance().reachBottom(getSelf()));
    }

    @Override
    public int getMojoBuilt(Combat c) {
        return 7;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (getSelf().has(Trait.ticklemonster) || target.roll(this, c, accuracy(c))) {
            if (target.crotchAvailable() && c.getStance().reachBottom(getSelf()) && !c.getStance().havingSex()) {
                int bonus = 0;
                int weak = 0;
                Result result = Result.normal;

                if (getSelf().has(Item.Tickler2) && Global.random(2) == 1 && getSelf().canSpend(10)) {
                    getSelf().spendMojo(c, 10);
                    result = Result.special;
                    bonus += 2;
                    weak += 2;
                }
                if (hastickler()) {
                    result = Result.strong;
                    bonus += 5 + Global.random(4);
                    weak += 3 + Global.random(4);
                }
                if (getSelf().human()) {
                    c.write(getSelf(), deal(c, 0, result, target));
                } else if (target.human()) {
                    c.write(getSelf(), receive(c, 0, result, target));
                }
                if (getSelf().has(Trait.ticklemonster) && target.mostlyNude()) {
                    if (getSelf().human()) {
                        c.write(getSelf(), deal(c, 0, Result.special, target));
                    } else if (target.human()) {
                        c.write(getSelf(), receive(c, 0, Result.special, target));
                    }
                    bonus += 5 + Global.random(4);
                    weak += 3 + Global.random(4);
                    if (Global.random(4) == 0) {
                        target.add(c, new Winded(target, 1));
                    }
                }
                if (result == Result.special) {
                    target.add(c, new Hypersensitive(target));
                }
                if (target.has(Trait.ticklish)) {
                    bonus = 4 + Global.random(3);
                    c.write(target, Global.format(
                                    "{other:SUBJECT-ACTION:squirm|squirms} uncontrollably from {self:name-possessive} actions. Yup, definitely ticklish.",
                                    getSelf(), target));
                }
                target.body.pleasure(getSelf(), getSelf().body.getRandom("hands"), target.body.getRandom("skin"),
                                2 + Global.random(4), bonus, c, false, this);
                target.weaken(c, weak / 2 + 2 + target.get(Attribute.Perception) + Global.random(6));
            } else if (hastickler() && Global.random(2) == 1) {
                if (target.breastsAvailable() && c.getStance().reachTop(getSelf())) {
                    if (getSelf().human()) {
                        c.write(getSelf(), deal(c, 0, Result.item, target));
                    } else if (target.human()) {
                        c.write(getSelf(), receive(c, 0, Result.item, target));
                    }
                    int bonus = 0;
                    if (target.has(Trait.ticklish)) {
                        bonus = 4 + Global.random(3);
                        c.write(target, Global.format(
                                        "{other:SUBJECT-ACTION:squirm|squirms} uncontrollably from {self:name-possessive} actions. Yup definitely ticklish.",
                                        getSelf(), target));
                    }
                    target.body.pleasure(getSelf(), getSelf().body.getRandom("hands"), target.body.getRandom("skin"),
                                    4 + Global.random(4), bonus, c, false, this);
                    target.weaken(c, bonus / 2 + 2 + target.get(Attribute.Perception));
                } else {
                    if (getSelf().human()) {
                        c.write(getSelf(), deal(c, 0, Result.weak, target));
                    } else if (target.human()) {
                        c.write(getSelf(), receive(c, 0, Result.weak, target));
                    }
                    int bonus = 0;
                    if (target.has(Trait.ticklish)) {
                        bonus = 4 + Global.random(3);
                        c.write(target, Global.format(
                                        "{other:SUBJECT-ACTION:squirm|squirms} uncontrollably from {self:name-possessive} actions. Yup definitely ticklish.",
                                        getSelf(), target));
                    }
                    target.body.pleasure(getSelf(), getSelf().body.getRandom("hands"), target.body.getRandom("skin"),
                                    4 + Global.random(2), bonus, c, false, this);
                    target.weaken(c, bonus / 2 + target.get(Attribute.Perception));
                }
            } else {
                if (getSelf().human()) {
                    c.write(getSelf(), deal(c, 0, Result.normal, target));
                } else if (target.human()) {
                    c.write(getSelf(), receive(c, 0, Result.normal, target));
                }
                int bonus = 0;
                if (target.has(Trait.ticklish)) {
                    bonus = 2 + Global.random(3);
                    c.write(target, Global.format(
                                    "{other:SUBJECT-ACTION:squirm|squirms} uncontrollably from {self:name-possessive} actions. Yup definitely ticklish.",
                                    getSelf(), target));
                }
                int m = (int) Math.round((4 + Global.random(3)) * (1.5 - target.getExposure()));
                int weak = (int) Math.round((bonus / 2 + Global.random(3 + target.get(Attribute.Perception)))
                                * (1.5 - target.getExposure()));
                target.body.pleasure(getSelf(), getSelf().body.getRandom("hands"), target.body.getRandom("skin"), m,
                                bonus, c, false, this);
                target.weaken(c, weak);
            }
        } else {
            if (getSelf().human()) {
                c.write(getSelf(), deal(c, 0, Result.miss, target));
            } else if (target.human()) {
                c.write(getSelf(), receive(c, 0, Result.miss, target));
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Cunning) >= 5;
    }

    @Override
    public Skill copy(Character user) {
        return new Tickle(user);
    }

    @Override
    public int speed() {
        return 7;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return "You try to tickle " + target.name() + ", but she squirms away.";
        } else if (modifier == Result.special) {
            return "You work your fingers across " + target.name()
                            + "'s most ticklish and most erogenous zones until she is writhing in pleasure and can't even make coherent words.";
        } else if (modifier == Result.critical) {
            return "You brush your tickler over " + target.name()
                            + "'s body, causing her to shiver and retreat. When you tickle her again, she yelps and almost falls down. "
                            + "It seems like your special feathers made her more sensitive than usual.";
        } else if (modifier == Result.strong) {
            return "You run your tickler across " + target.name()
                            + "'s sensitive thighs and pussy. She can't help but let out a quiet whimper of pleasure.";
        } else if (modifier == Result.item) {
            return "You tease " + target.name()
                            + "'s naked upper body with your feather tickler, paying close attention to her nipples.";
        } else if (modifier == Result.weak) {
            return "You catch " + target.name() + " off guard by tickling her neck and ears.";
        } else {
            return "You tickle " + target.name() + "'s sides as she giggles and squirms.";
        }

    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return getSelf().name() + " tries to tickle you, but fails to find a sensitive spot.";
        } else if (modifier == Result.special) {
            return getSelf().name()
                            + " tickles your nude body mercilessly, gradually working her way to your dick and balls. As her fingers start tormenting your privates, you struggle to "
                            + "clear your head enough to keep from ejaculating immediately.";
        } else if (modifier == Result.critical) {
            return getSelf().name()
                            + " teases your privates with her feather tickler. After she stops, you feel an unnatural sensitivity where the feathers touched you.";
        } else if (modifier == Result.strong) {
            return getSelf().name()
                            + " brushes her tickler over your balls and teases the sensitive head of your penis.";
        } else if (modifier == Result.item) {
            return getSelf().name() + " runs her feather tickler across your nipples and abs.";
        } else if (modifier == Result.weak) {
            return getSelf().name() + " pulls out a feather tickler and teases any exposed skin she can reach.";
        } else {
            return getSelf().name()
                            + " suddenly springs toward you and tickles you relentlessly until you can barely breathe.";
        }
    }

    @Override
    public String describe(Combat c) {
        return "Tickles opponent, weakening and arousing her. More effective if she's nude";
    }

    private boolean hastickler() {
        return getSelf().has(Item.Tickler) || getSelf().has(Item.Tickler2);
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
