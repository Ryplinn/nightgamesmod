package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.items.Item;

import java.util.Arrays;
import java.util.List;

public class CommandGive extends PlayerCommand {

    private static final List<Item> TRANSFERABLES =
                    Arrays.asList(Item.EnergyDrink, Item.SPotion, Item.Aphrodisiac, Item.Sedative, Item.Battery,
                                    Item.Beer, Item.Lubricant, Item.Rope, Item.ZipTie, Item.Tripwire, Item.Spring);
    private Item transfer;

    CommandGive() {
        super("Take Item");
        transfer = null;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        if (!super.usable(c, user, target)) {
            return false;
        }
        for (Item transferable : TRANSFERABLES) {
            if (target.has(transferable)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Make your opponent give you an item.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        do {
            transfer = Item.values()[Random.random(Item.values().length)];
            if (!(target.has(transfer) && TRANSFERABLES.contains(transfer))) {
                transfer = null;
            }
        } while (transfer == null);
        target.consume(transfer, 1);
        user.gain(transfer);
        c.write(user, deal(c, 0, Result.normal, user, target));
        transfer = null;
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new CommandGive();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return target.getName() + " takes out " + transfer.pre() + transfer.getName() + " and hands it to you.";
    }

    @Override
    public String receive(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return "<<This should not be displayed, please inform The" + " Silver Bard: CommandGive-receive>>";
    }

}
