package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.ClothingSlot;
import nightgames.status.BodyFetish;

import java.util.Optional;

public class TakeOffShoes extends Skill {

    public TakeOffShoes() {
        super("Remove Shoes");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return (user.getAttribute(Attribute.cunning) >= 5 && !user.human()) || target.body.getFetish("feet").isPresent() && user.has(Trait.direct);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !user.outfit.hasNoShoes();
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Remove your own shoes";
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return c.getOpponent(user).body.getFetish("feet").isPresent() && c.getOpponent(user).body.getFetish("feet").get().magnitude > .25 && !c.getOpponent(user).stunned() ? 1.0f : -5.0f;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        user.strip(ClothingSlot.feet, c);
        if (target.body.getFetish("feet").isPresent() && target.body.getFetish("feet").get().magnitude > .25) {
            writeOutput(c, Result.special, user, target);
            target.temptWithSkill(c, user, user.body.getRandom("feet"), Random.random(17, 26), this);
        } else {
            writeOutput(c, Result.normal, user, target);
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        if (user != null) {
            Optional<BodyFetish> footFetish =
                            c.getOpponent(user).body.getFetish("feet").filter(fetish -> fetish.magnitude > .25);
            if (footFetish.isPresent()) {
                return Tactics.pleasure;
            }
        }
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return Formatter.format("{self:SUBJECT} take a moment to slide off {self:possessive} footwear with slow exaggerated motions. {other:SUBJECT-ACTION:gulp|gulps}. "
                            + "While {other:pronoun-action:know|knows} what {self:pronoun} are doing, it changes nothing as desire fills {other:possessive} eyes.", user, target);
        }
        return "You take a moment to kick off your footwear.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return Formatter.format("{self:SUBJECT} takes a moment to slide off {self:possessive} footwear with slow exaggerated motions. {other:SUBJECT-ACTION:gulp|gulps}. "
                            + "While {other:pronoun-action:know|knows} what {self:pronoun} is doing, it changes nothing as desire fills {other:possessive} eyes.", user, target);
        }
        return user.subject() + " takes a moment to kick off " + user.possessiveAdjective() + " footwear.";
    }
}
