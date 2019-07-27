package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.status.Hypersensitive;

public class BreastRay extends Skill {
    BreastRay() {
        super("Breast Ray");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.science) >= 12;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().prone(user)
                        && target.mostlyNude() && user.has(Item.Battery, 2);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 2.f;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Grow your opponent's boobs to make her more sensitive: 2 Batteries";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        user.consume(Item.Battery, 2);
        boolean permanent = Random.random(20) == 0 && (user.human() || c.shouldPrintReceive(target, c))
                        && !target.has(Trait.stableform);
        writeOutput(c, permanent ? 1 : 0, Result.normal, user, target);
        target.add(c, new Hypersensitive(target.getType(), 10));
        BreastsPart part = target.body.getBreastsBelow(BreastsPart.f.getSize());
        if (permanent) {
            if (part != null) {
                target.body.addReplace(part.upgrade(), 1);
                target.body.temporaryAddOrReplacePartWithType(part.upgrade().upgrade().upgrade(), 10);
            }
        } else {
            if (part != null) {
                target.body.temporaryAddOrReplacePartWithType(part.upgrade().upgrade().upgrade(), 10);
            }
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        String message;
        message = "You point your growth ray at " + target.getName()
                        + "'s breasts and fire. Her breasts balloon up and the new sensitivity causes her to moan.";
        if (damage > 0) {
            message += " The change in " + target.getName() + " looks permanent!";
        }
        return message;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String message;
        boolean plural = target.body.getRandomBreasts().getSize() > 0 || target.getAttribute(Attribute.power) > 25;
        message = String.format("%s a device at %s chest and giggles as %s %s"
                        + " %s ballooning up. %s %s and %s to cover %s, but the increased sensitivity "
                        + "distracts %s in a delicious way.",
                        user.subjectAction("point"), target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                        target.body.getRandomBreasts().describe(target), plural ? "start" : "starts",
                                        Formatter.capitalizeFirstLetter(target.pronoun()),
                                        target.action("flush", "flushes"),
                                        target.action("try", "tries"),
                                        target.reflectivePronoun(), target.directObject());
        if (damage > 0) {
            message += " You realize the effects are permanent!";
        }
        return message;
    }

}
