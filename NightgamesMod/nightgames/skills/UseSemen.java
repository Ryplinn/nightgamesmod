package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.items.ItemEffect;

public class UseSemen extends Skill {
    UseSemen() {
        super("Drink Semen Bottle");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        boolean hasItems = user.has(Item.semen);
        return hasItems && user.canAct() && user.has(Trait.succubus) && c.getStance().mobile(user);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        Item used = Item.semen;
        boolean eventful = false;
        c.write(user,
                        Formatter.format("{self:SUBJECT-ACTION:take|takes} out a bottle of milky white semen and {self:action:gulp|gulps} it down in one breath.",
                                        user, target));
        for (ItemEffect e : used.getEffects()) {
            eventful = e.use(c, user, target, used) || eventful;
        }
        if (!eventful) {
            c.write(user, "...But nothing happened.");
        }
        user.consume(used, 1);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return "";
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Drink a bottle of semen";
    }

    @Override
    public boolean makesContact() {
        return false;
    }
}
