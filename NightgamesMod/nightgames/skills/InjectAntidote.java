package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;

public class InjectAntidote extends Skill {
    private InjectAntidote() {
        super("Inject Antidote");
    }

    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.medicine) >= 7;
    }

    public boolean usable(Combat c, Character user, Character target) {
        return (c.getStance().mobile(user)) && (user.canAct())
                        && user.has(Item.MedicalSupplies, 1);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    public boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            c.write(user, receive(c, 0, Result.normal, user, user));
        }
        user.calm(c, user.getArousal().max() / 10);
        user.purge(c);
        user.consume(Item.MedicalSupplies, 1);
        return true;
    }

    public Tactics type(Combat c, Character user) {
        return Tactics.recovery;
    }

    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "You inject yourself with an antidote. The drug quickly purges any foreign influence from your system.",
                        user, target);
    }

    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "{self:SUBJECT} jabs {self:reflective} with a needle, sighing as {self:pronoun} pushes the needle down. Before {other:name-possessive} eyes, {self:possessive} entire bodily system is purged of all factors, both begin and malign.",
                        user, target);
    }

    public String describe(Combat c, Character user) {
        return "Injects yourself with an pancea";
    }
}
