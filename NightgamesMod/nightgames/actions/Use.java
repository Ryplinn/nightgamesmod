package nightgames.actions;

import nightgames.characters.Character;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.items.Item;
import nightgames.status.Buzzed;
import nightgames.status.Oiled;

public class Use extends Action {
    private static final long serialVersionUID = 6212525023016041538L;
    private Item item;

    public Use(Item item) {
        super("Use " + item.getName());
        if (item == Item.Lubricant) {
            name = "Oil up";
        } else if (item == Item.EnergyDrink) {
            name = "Energy Drink";
        } else if (item == Item.Beer) {
            name = "Beer";
        }
        this.item = item;
    }

    @Override
    public boolean usable(Character user) {
        return user.has(item) && !user.bound();
    }

    @Override
    public Movement execute(Character user) {
        if (item == Item.Lubricant) {
            if (user.human()) {
                GUI.gui.message(
                                "You cover yourself in slick oil. It's a weird feeling, but it should make it easier to escape from a hold.");
            }
            user.addNonCombat(new Oiled(user.getType()));
            user.consume(Item.Lubricant, 1);
            return Movement.oil;
        } else if (item == Item.EnergyDrink) {
            if (user.human()) {
                GUI.gui.message(
                                "You chug down the unpleasant drink. Your tiredness immediately starts to recede.");
            }
            user.heal(null, 10 + Random.random(10));
            user.consume(Item.EnergyDrink, 1);
            return Movement.energydrink;
        } else if (item == Item.Beer) {
            if (user.human()) {
                GUI.gui.message("You pop open a beer and chug it down, feeling buzzed and a bit sluggish.");
            }
            user.addNonCombat(new Buzzed(user.getType()));
            user.consume(Item.Beer, 1);
            return Movement.beer;
        }
        return Movement.wait;
    }

    @Override
    public Movement consider() {
        if (item == Item.Lubricant) {
            return Movement.oil;
        } else if (item == Item.EnergyDrink) {
            return Movement.energydrink;
        } else if (item == Item.Beer) {
            return Movement.beer;
        }
        return Movement.wait;
    }

}
