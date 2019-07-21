package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Decider;
import nightgames.characters.NPC;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.DebugFlags;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.items.ItemEffect;

import java.util.*;

public class ThrowDraft extends Skill {
    private static final Set<Item> transformativeItems = new HashSet<>();

    static {
        transformativeItems.add(Item.SuccubusDraft);
        transformativeItems.add(Item.BustDraft);
        transformativeItems.add(Item.TinyDraft);
        transformativeItems.add(Item.TentacleTonic);
        transformativeItems.add(Item.PriapusDraft);
        transformativeItems.add(Item.FemDraft);
    }

    ThrowDraft(CharacterType self) {
        super("Throw draft", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        boolean hasItems = subChoices(c).size() > 0;
        return hasItems && getSelf().canAct() && c.getStance().mobile(getSelf())
                        && (c.getStance().reachTop(getSelf()) || c.getStance().reachBottom(getSelf())) && !getSelf().isPet();
    }

    @Override
    public Collection<String> subChoices(Combat c) {
        ArrayList<String> usables = new ArrayList<>();
        for (Item i : getSelf().getInventory().keySet()) {
            if (getSelf().has(i) && i.getEffects().get(0).throwable()) {
                usables.add(i.getName());
            }
        }
        return usables;
    }

    private Item pickBest(Combat c, NPC self, Character target, List<Item> usables) {
        HashMap<Item, Double> checks = new HashMap<>();
        double selfFitness = self.getFitness(c);
        double targetFitness = self.getOtherFitness(c, target);
        usables.forEach(item -> {
            double rating = Decider.rateAction(self, c, selfFitness, targetFitness, (newCombat, newSelf, newOther) -> {
                for (ItemEffect e : item.getEffects()) {
                    e.use(newCombat, newOther, newSelf, item);
                }
                return true;
            });
            checks.put(item, rating);
        });
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILLS)) {
            checks.forEach((key, value) -> System.out.println("Item " + key + ": " + value));
        }
        return checks.entrySet().stream().min((first, second) -> {
            double test = second.getValue() - first.getValue();
            if (test < 0) {
                return -1;
            }
            if (test > 0) {
                return 1;
            }
            return 0;
        }).map(Map.Entry::getKey).orElse(null);
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        Item used = null;
        if (getSelf().human()) {
            for (Item i : getSelf().getInventory().keySet()) {
                if (i.getName().equals(choice)) {
                    used = i;
                    break;
                }
            }
        } else {
            ArrayList<Item> usables = new ArrayList<>();
            for (Item i : getSelf().getInventory().keySet()) {
                if (i.getEffects().get(0).throwable() && i.usable(c, getSelf(), c.getOpponent(getSelf()))) {
                    usables.add(i);
                }
            }
            if (usables.size() > 0 && getSelf() instanceof NPC) {
                used = pickBest(c, (NPC) getSelf(), target, usables);
            }
        }
        if (used == null) {
            c.write(getSelf(), "Skill failed...");
        } else {
            String verb = used.getEffects().get(0).getOtherVerb();
            if (verb.isEmpty()) {
                verb = "throw";
            }
            c.write(getSelf(), Formatter.format(
                            String.format("{self:SUBJECT-ACTION:%s|%ss} %s%s.", verb, verb, used.pre(), used.getName()),
                            getSelf(), target));
            if (transformativeItems.contains(used) && target.has(Trait.stableform)) {
                c.write(target, "...But nothing happened (Stable Form).");
            } else {
                boolean eventful = false;
                if (used.usable(c, getSelf(), getSelf())) {
                    for (ItemEffect e : used.getEffects()) {
                        eventful |= e.use(c, target, getSelf(), used);
                    }
                }
                if (!eventful) {
                    c.write(getSelf(), "...But nothing happened.");
                }
            }
            getSelf().consume(used, 1);
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new ThrowDraft(user.getType());
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
        return "Throw a draft at your opponent";
    }

    @Override
    public boolean makesContact() {
        return false;
    }
}
