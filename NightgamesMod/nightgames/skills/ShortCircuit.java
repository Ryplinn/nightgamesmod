package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.status.Rewired;

public class ShortCircuit extends Skill {

    ShortCircuit() {
        super("Short-Circuit");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.science) >= 15;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().prone(user)
                        && target.mostlyNude() && user.has(Item.Battery, 3);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Fire a  blast of energy to confuse your opponent's nerves so she can't tell pleasure from pain: 3 Batteries.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.consume(Item.Battery, 3);
        writeOutput(c, Result.normal, user, target);
        target.add(c, new Rewired(target.getType(), 4 + Random.random(3)));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You send a light electrical current through " + target.getName()
                        + "'s body, disrupting her nerve endings. She'll temporarily feel pleasure as pain and pain as pleasure.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s aims a device at %s and %s %s a strange shiver run "
                        + "across %s skin. %s indescribably weird. %s has "
                        + "done something to %s sense of touch.", user.subject(),
                        target.nameDirectObject(), target.pronoun(), target.action("feel"),
                        target.possessiveAdjective(),
                        Formatter.capitalizeFirstLetter(target.subjectAction("feel")),
                        user.subject(), target.possessiveAdjective());
    }

}
