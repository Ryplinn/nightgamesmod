package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.items.ItemEffect;

public class UseSemen extends Skill {
    UseSemen(CharacterType self) {
        super("Drink Semen Bottle", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        boolean hasItems = getSelf().has(Item.semen);
        return hasItems && getSelf().canAct() && getSelf().has(Trait.succubus) && c.getStance().mobile(getSelf());
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        Item used = Item.semen;
        boolean eventful = false;
        c.write(getSelf(),
                        Formatter.format("{self:SUBJECT-ACTION:take|takes} out a bottle of milky white semen and {self:action:gulp|gulps} it down in one breath.",
                                        getSelf(), target));
        for (ItemEffect e : used.getEffects()) {
            eventful = e.use(c, getSelf(), target, used) || eventful;
        }
        if (!eventful) {
            c.write(getSelf(), "...But nothing happened.");
        }
        getSelf().consume(used, 1);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new UseSemen(user.getType());
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return "";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return "";
    }

    @Override
    public String describe(Combat c) {
        return "Drink a bottle of semen";
    }

    @Override
    public boolean makesContact() {
        return false;
    }
}
