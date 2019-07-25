package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.skills.damage.DamageType;

public class Sedate extends Skill {

    public Sedate() {
        super("Sedate");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().mobile(user) && user.canAct() && user.has(Item.Sedative)
                        && !c.getStance().prone(user);
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return user.has(Item.Aersolizer) ? 200 : 65;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.consume(Item.Sedative, 1);
        if (user.has(Item.Aersolizer)) {
            writeOutput(c, Result.special, user, target);
            target.weaken(c, (int) DamageType.biological.modifyDamage(user, target, 50));
            target.loseMojo(c, (int) DamageType.biological.modifyDamage(user, target, 35));
        } else if (target.roll(user, accuracy(c, user, target))) {
            writeOutput(c, Result.normal, user, target);
            target.weaken(c, (int) DamageType.biological.modifyDamage(user, target, 50));
            target.loseMojo(c, (int) DamageType.biological.modifyDamage(user, target, 35));
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
        if (modifier == Result.special) {
            return "You pop a sedative into your Aerosolizer and spray " + target.getName()
                            + " with a cloud of mist. She stumbles out of the cloud looking drowsy and unfocused.";
        } else if (modifier == Result.miss) {
            return "You throw a bottle of sedative at " + target.getName()
                            + ", but she ducks out of the way and it splashes harmlessly on the ground. What a waste.";
        } else {
            return "You through a bottle of sedative at " + target.getName()
                            + ". She stumbles for a moment, trying to clear the drowsiness from her head.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return String.format("%s inserts a bottle into the attachment on %s arm. "
                            + "%s suddenly surrounded by a cloud of dense fog. The "
                            + "fog seems to fill %s head and %s body feels heavy.",
                            user.subject(), user.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(target.action("are", "is")),
                            target.possessiveAdjective(), target.possessiveAdjective());
        } else if (modifier == Result.miss) {
            return String.format("%s splashes a bottle of liquid in %s direction, but none of it hits %s.",
                            user.subject(), target.nameOrPossessivePronoun(), target.directObject());
        } else {
            return String.format("%s hits %s with a flask of liquid. Even the fumes make %s feel"
                            + " sluggish and %s limbs become heavy.",
                            user.subject(), target.nameDirectObject(),
                            target.directObject(), target.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Throw a sedative at your opponent, weakening " + c.getOpponent(user).directObject();
    }
}
