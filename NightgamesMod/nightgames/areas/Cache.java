package nightgames.areas;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.characters.trait.Trait;
import nightgames.global.DebugFlags;
import nightgames.global.Match;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.items.Item;
import nightgames.items.Loot;
import nightgames.items.clothing.ClothingTable;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class Cache implements Deployable {
    private static final int MAX_VALUE = 23;
    private int dc;
    private int level;
    private Attribute test;
    private Attribute secondary;
    private ArrayList<Loot> reward;

    public Cache(int level) {
        reward = new ArrayList<>();
        dc = 10 + level;
        this.level = level;
        switch (Random.random(4)) {
            case 3:
                test = Attribute.seduction;
                secondary = Attribute.darkness;
                break;
            case 2:
                test = Attribute.cunning;
                secondary = Attribute.science;
                break;
            case 1:
                test = Attribute.perception;
                secondary = Attribute.spellcasting;
                dc -= 8;
                break;
            default:
                test = Attribute.power;
                secondary = Attribute.ki;
                break;
        }
        calcReward(level);
    }

    public void gainMoney(Character active) {
        int reward = 500 + level * 50;
        active.modMoney(reward);
        if (active.human()) {
            GUI.gui.message("You have found " + reward + "$!");
        }
    }

    @Override
    public boolean resolve(Character active) {
        int bonus = 0;
        if (active.state == State.ready) {
            if (active.has(Trait.treasureSeeker)) {
                bonus = 5;
            }
            Random.DieRoll primaryCheck = active.check(test, bonus);
            Random.DieRoll secondaryCheck = active.check(secondary, bonus);
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_CACHE_ROLLS)) {
                System.out.println(String.format("Cache roll for %s:", active.getTrueName()));
                System.out.println(String.format("  Primary [%s]: %s vs dc %d", test.toString(), primaryCheck.debugString(), dc));
                System.out.println(String.format("  Secondary [%s]: %s vs dc %d", secondary.toString(), secondaryCheck.debugString(), dc-5));
            }
            if (primaryCheck.vsDc(dc)) {
                if (DebugFlags.isDebugOn(DebugFlags.DEBUG_CACHE_ROLLS)) {
                    System.out.println("Primary check success!");
                }
                if (active.human()) {
                    switch (test) {
                        case cunning:
                            GUI.gui.message(
                                            "<br/><br/>You notice a conspicuous box lying on the floor connected to a small digital touchscreen. The box is sealed tight, but it looks like "
                                                            + "the touchscreen probably opens it. The screen is covered by a number of abstract symbols with the phrase \"Solve Me\" at the bottom. A puzzle obviously. "
                                                            + "It would probably be a problem to someone less clever. You quickly solve the puzzle and the box opens.<br/><br/>");
                            break;
                        case perception:
                            GUI.gui.message("<br/><br/>Something is off in this "
                                            + Match.getMatch().genericRoomDescription()
                                            + ", but it's hard to put your finger on it. A trap? No, it's not that. You spot a carefully hidden, but "
                                            + "nonetheless out-of-place package. It's not sealed and the contents seem like they could be useful, so you help yourself.<br/><br/>");
                            break;
                        case power:
                            GUI.gui.message(
                                            "<br/><br/>You spot a strange box with a heavy steel lid. Fortunately the lid is slightly askew, so you may actually be able to get it open with your bare "
                                                            + "hands if you're strong enough. With a considerable amount of exertion, you manage to force the lid open. Hopefully the contents are worth it.<br/><br/>");
                            break;
                        case seduction:
                            GUI.gui.message(
                                            "<br/><br/>You stumble upon a small, well crafted box. It's obviously out of place here, but there's no obvious way to open it. The only thing on the "
                                                            + "box is a hole that's too dark to see into and barely big enough to stick a finger into. Fortunately, you're very good with your fingers. With a bit of poking "
                                                            + "around, you feel some intricate mechanisms and maneuver them into place, allowing you to slide the top of the box off.<br/><br/>");
                            break;
                        default:
                            break;
                    }
                }
                for (Loot i : reward) {
                    i.pickup(active);
                }
                active.modMoney(Random.random(500) + 500);
            } else if (secondaryCheck.vsDc(dc - 5)) {
                if (DebugFlags.isDebugOn(DebugFlags.DEBUG_CACHE_ROLLS)) {
                    System.out.println("Secondary check success!");
                }
                if (active.human()) {
                    switch (test) {
                        case cunning:
                            GUI.gui.message(
                                            "<br/><br/>You notice a conspicuous box lying on the floor connected to a small digital touchscreen. The box is sealed tight, but it looks like "
                                                            + "the touchscreen probably opens it. The screen is covered by a number of abstract symbols with the phrase \"Solve Me\" at the bottom. A puzzle obviously. "
                                                            + "Looks unneccessarily complicated. You pull off the touchscreen instead and short the connectors, causing the box to open so you can collect the contents.<br/><br/>");
                            break;
                        case perception:
                            GUI.gui.message("<br/><br/>Something is off in this "
                                            + Match.getMatch().genericRoomDescription()
                                            + ", but it's hard to put your finger on it. A trap? No, it's not that. You summon a minor spirit to search the "
                                            + "area. It's not much good in a fight, but it is pretty decent at finding hidden objects. It leads you to a small hidden box of goodies.<br/><br/>");
                            break;
                        case power:
                            GUI.gui.message(
                                            "<br/><br/>You spot a strange box with a heavy steel lid. Fortunately the lid is slightly askew, so you may actually be able to get it open with your bare "
                                                            + "hands if you're strong enough. You're about to attempt to lift the cover, but then you notice the box is not quite as sturdy as it initially looked. You focus "
                                                            + "your ki and strike the weakest point on the crate, which collapses the side. Hopefully no one's going to miss the box. You're more interested in what's inside.<br/><br/>");
                            break;
                        case seduction:
                            GUI.gui.message(
                                            "<br/><br/>You stumble upon a small, well crafted box. It's obviously out of place here, but there's no obvious way to open it. The only thing on the "
                                                            + "box is a hole that's too dark to see into and barely big enough to stick a finger into. However, the dark works to your advantage. You take control of the "
                                                            + "shadows inside the box, giving them physical form and using them to force the box open. Time to see what's inside.<br/><br/>");
                            break;
                        default:
                            break;
                    }
                }
                for (Loot i : reward) {
                    i.pickup(active);
                }
                active.modMoney(Random.random(500) + 500);
            } else {
                if (DebugFlags.isDebugOn(DebugFlags.DEBUG_CACHE_ROLLS)) {
                    System.out.println("Failed to notice/open cache.");
                }
                switch (test) {
                    case cunning:
                        GUI.gui.message(
                                        "<br/><br/>You notice a conspicuous box lying on the floor connected to a small digital touchscreen. The box is sealed tight, but it looks like "
                                                        + "the touchscreen probably opens it. The screen is covered by a number of abstract symbols with the phrase \"Solve Me\" at the bottom. A puzzle obviously. "
                                                        + "You do your best to decode it, but after a couple of failed attempts, the screen turns off and stops responding.<br/><br/>");
                        break;
                    case perception:
                        GUI.gui.message("<br/><br/>Something is off in this " + Match.getMatch().genericRoomDescription()
                                        + ", but it's hard to put your finger on it. A trap? No, it's not that. Probably nothing.<br/><br/>");
                        break;
                    case power:
                        GUI.gui.message(
                                        "<br/><br/>You spot a strange box with a heavy steel lid. Fortunately the lid is slightly askew, so you may actually be able to get it open with your bare "
                                                        + "hands if you're strong enough. You try to pry the box open, but it's even heavier than it looks. You lose your grip and almost lose your fingertips as the lid "
                                                        + "slams firmly into place. No way you're getting it open without a crowbar.<br/><br/>");
                        break;
                    case seduction:
                        GUI.gui.message(
                                        "<br/><br/>You stumble upon a small, well crafted box. It's obviously out of place here, but there's no obvious way to open it. The only thing on the "
                                                        + "box is a hole that's too dark to see into and barely big enough to stick a finger into. You feel around inside, but make no progress in opening it. Maybe "
                                                        + "you'd have better luck with some precision tools.<br/><br/>");
                        break;
                    default:
                        break;
                }
            }
            active.location().remove(this);
            return true;
        }
        return false;
    }

    private void calcReward(int level) {
        int bonusRollsPossible = (int) Math.floor((double) level / (double) MAX_VALUE);
        int bonusRolls = Random.random(bonusRollsPossible+1);
        int totalRolls = 1 + bonusRolls;
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_CACHE_ROLLS)) {
            System.out.println(String.format("Cache reward rolls at approximate mean level %d (up to %d bonus):", level, bonusRollsPossible));
            System.out.println(String.format("  1 base + %d bonus = %d total rolls", bonusRolls, totalRolls));
        }
        IntStream.range(0, totalRolls).map(i -> Random.random(Math.min(MAX_VALUE, level))).forEach(value -> {
            switch (value) {
                case 23:
                    reward.add(Item.Sprayer);
                    reward.add(Item.Sprayer);
                    reward.add(Item.Sprayer);
                    reward.add(Item.Tripwire);
                    reward.add(Item.Tripwire);
                    reward.add(Item.Talisman);
                    break;
                case 22:
                    reward.add(Item.SPotion);
                    reward.add(Item.SPotion);
                    reward.add(Item.Totem);
                    reward.add(Item.Aphrodisiac);
                    break;
                case 21:
                    reward.add(Item.Rope);
                    reward.add(Item.Rope);
                    reward.add(Item.Rope);
                    reward.add(Item.Rope);
                    reward.add(Item.Tripwire);
                    break;
                case 20:
                    reward.add(Item.Totem);
                    reward.add(Item.SPotion);
                    reward.add(Item.SPotion);
                    reward.add(Item.Lubricant);
                    reward.add(Item.Talisman);
                    break;
                case 19:
                    reward.add(Item.Handcuffs);
                    reward.add(Item.Handcuffs);
                    reward.add(Item.DisSol);
                    break;
                case 18:
                    // TODO: I think this is the source of the empty caches I keep finding.
                    ClothingTable.getByID("cup").ifPresent(cup -> reward.add(cup));
                    break;
                case 17:
                    reward.add(Item.SPotion);
                    reward.add(Item.SPotion);
                    reward.add(Item.Talisman);
                    break;
                case 16:
                    reward.add(Item.Totem);
                    reward.add(Item.Handcuffs);
                    reward.add(Item.FaeScroll);
                    break;
                case 15:
                    reward.add(Item.SPotion);
                    reward.add(Item.Aphrodisiac);
                    reward.add(Item.Sprayer);
                    reward.add(Item.Talisman);
                    break;
                case 14:
                    reward.add(Item.Lubricant);
                    reward.add(Item.Lubricant);
                    reward.add(Item.SPotion);
                    break;
                case 13:
                    reward.add(Item.Rope);
                    reward.add(Item.Rope);
                    reward.add(Item.Sedative);
                    reward.add(Item.Talisman);
                    break;
                case 12:
                    reward.add(Item.Aphrodisiac);
                    reward.add(Item.Aphrodisiac);
                    break;
                case 11:
                    reward.add(Item.FaeScroll);
                    reward.add(Item.Talisman);
                    break;
                case 10:
                    reward.add(Item.DisSol);
                    reward.add(Item.Tripwire);
                    reward.add(Item.Sprayer);
                    reward.add(Item.Talisman);
                    break;
                case 9:
                    reward.add(Item.Totem);
                    reward.add(Item.Talisman);
                    break;
                case 8:
                    reward.add(Item.Aphrodisiac);
                    reward.add(Item.Lubricant);
                    break;
                case 7:
                    reward.add(Item.Tripwire);
                    reward.add(Item.Tripwire);
                    reward.add(Item.Phone);
                    reward.add(Item.Talisman);
                    break;
                case 6:
                    reward.add(Item.SPotion);
                    break;
                case 5:
                    reward.add(Item.Aphrodisiac);
                    break;
                case 4:
                    reward.add(Item.Lubricant);
                    reward.add(Item.DisSol);
                    reward.add(Item.Talisman);
                    break;
                case 3:
                    reward.add(Item.Beer);
                    reward.add(Item.Beer);
                    reward.add(Item.Beer);
                    break;
                case 2:
                    reward.add(Item.ZipTie);
                    reward.add(Item.Tripwire);
                    break;
                case 1:
                    reward.add(Item.FaeScroll);
                    reward.add(Item.Talisman);
                    break;
                default:
                    reward.add(Item.Aphrodisiac);
                    reward.add(Item.Totem);
                    reward.add(Item.DisSol);
                    reward.add(Item.SPotion);
                    reward.add(Item.Handcuffs);
            }
        });
    }

    @Override
    public Character getOwner() {
        // TODO Auto-generated method stub
        return null;
    }
}
