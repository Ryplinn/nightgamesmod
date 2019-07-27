package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.items.clothing.ClothingTrait;
import nightgames.status.Falling;
import nightgames.status.Winded;

public class StunBlast extends Skill {

    StunBlast() {
        super("Stun Blast");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.science) >= 9;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && c.getStance().front(user)
                        && (user.has(Item.Battery, 4) ||
                                        (target.has(Trait.conducivetoy) &&
                                        target.has(ClothingTrait.harpoonDildo) || 
                                        target.has(ClothingTrait.harpoonOnahole)));
    }

    @Override
    public String describe(Combat c, Character user) {
        return "A blast of light and sound with a chance to stun: 4 Battery";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (target.has(Trait.conducivetoy) && target.has(ClothingTrait.harpoonDildo) || 
                                        target.has(ClothingTrait.harpoonOnahole)) { 
            writeOutput(c, Result.special, user, target);
            target.getStamina().empty();
            target.add(c, new Winded(target.getType()));
            target.add(c, new Falling(target.getType()));
            return true;
        }
        user.consume(Item.Battery, 4);
        if (Random.random(10) >= 4) {
            writeOutput(c, Result.normal, user, target);
            target.getStamina().empty();
            target.add(c, new Falling(target.getType()));
            target.add(c, new Winded(target.getType()));
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You overload the emitter on your arm, but " + target.getName()
                            + " shields her face to avoid the flash.";
        } else {
            return "You overload the emitter on your arm, duplicating the effect of a flashbang. " + target.getName()
                            + " staggers as the blast disorients her.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s covers %s face and points a device in %s direction. Sensing "
                            + "danger, %s %s %s eyes just as the flashbang goes off.", user.subject(),
                            user.possessiveAdjective(), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("cover"), target.possessiveAdjective());
        } else if (modifier == Result.special) {
            return Formatter.format("{self:SUBJECT} presses a button on {self:possessive} arm device,"
                            + "and a bright flash suddenly travels along {self:possessive} connection to"
                            + " the toy which is still stuck to you. When it reaches you, a huge shock"
                            + " stuns your body, leaving you helpless on the ground while the toy"
                            + " still merrily churns away.."
                            , user, target);
        } else {
            return String.format("%s points a device in %s direction that glows slightly. A sudden "
                            + "flash of light disorients %s and %s ears ring from the blast.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            target.directObject(), target.possessiveAdjective());
        }
    }

}
