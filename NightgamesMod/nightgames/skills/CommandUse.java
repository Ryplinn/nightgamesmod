package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.status.Hypersensitive;
import nightgames.status.Oiled;
import nightgames.status.Stsflag;

import java.util.Arrays;
import java.util.List;

public class CommandUse extends PlayerCommand {

    private static final List<Item> CANDIDATES = Arrays.asList(Item.Lubricant, Item.SPotion);
    private Item used;

    CommandUse() {
        super("Force Item Use");
        used = null;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        if (!super.usable(c, user, target) || !target.mostlyNude()) {
            return false;
        }
        boolean usable = false;
        for (Item candidate : CANDIDATES) {
            if (target.has(candidate)) {
                switch (candidate) {
                    case Lubricant:
                        usable = !target.is(Stsflag.oiled);
                        break;
                    case SPotion:
                        usable = !target.is(Stsflag.hypersensitive);
                        break;
                    default:
                        break;
                }
            }
        }
        return usable;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Force your thrall to use a harmful item on themselves";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        do {
            used = Item.values()[Random.random(Item.values().length)];
            boolean hasStatus = false;
            switch (used) {
                case Lubricant:
                    hasStatus = target.is(Stsflag.oiled);
                    break;
                case SPotion:
                    hasStatus = target.is(Stsflag.hypersensitive);
                    break;
                default:
                    break;
            }
            if (!(CANDIDATES.contains(used) && target.has(used)) && !hasStatus) {
                used = null;
            }
        } while (used == null);
        switch (used) {
            case Lubricant:
                target.add(c, new Oiled(target.getType()));
                c.write(user, deal(c, 0, Result.normal, user, target));
                break;
            case SPotion:
                target.add(c, new Hypersensitive(target.getType()));
                c.write(user, deal(c, 0, Result.special, user, target));
                break;
            default:
                c.write(user, "<<This should not be displayed, please inform The" + " Silver Bard: CommandUse-resolve>>");
                return false;
        }
        target.consume(used, 1);
        used = null;
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int magnitude, Result modifier, Character user, Character target) {
        switch (modifier) {
            case normal:
                return target.getName() + " coats herself in a shiny lubricant at your 'request'.";
            case special:
                return "Obediently, " + target.getName() + " smears a sensitivity potion on herself.";
            default:
                return "<<This should not be displayed, please inform The" + " Silver Bard: CommandUse-deal>>";
        }
    }

    @Override
    public String receive(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return "<<This should not be displayed, please inform The" + " Silver Bard: CommandUse-receive>>";
    }

}
