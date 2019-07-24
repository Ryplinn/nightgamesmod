package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.Decider;
import nightgames.characters.NPC;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.DebugFlags;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.items.ItemEffect;
import nightgames.status.Stsflag;

import java.util.*;

public class UseDraft extends Skill {
    UseDraft() {
        super("Use draft");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        boolean hasItems = subChoices(c, user).size() > 0;
        return hasItems && user.canAct() && c.getStance().mobile(user) && !user.isPet();
    }

    @Override
    public Collection<String> subChoices(Combat c, Character user) {
        ArrayList<String> usables = new ArrayList<>();
        for (Item i : user.getInventory().keySet()) {
            if (user.has(i) && i.getEffects().get(0).drinkable() && i.usable(c, user, user)) {
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
                    e.use(newCombat, newSelf, newOther, item);
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
    public boolean resolve(Combat c, Character user, Character target) {
        Item used = null;
        if (user.human()) {
            for (Item i : user.getInventory().keySet()) {
                if (i.getName().equals(choice)) {
                    used = i;
                    break;
                }
            }
        } else {
            ArrayList<Item> usables = new ArrayList<>();
            for (Item i : user.getInventory().keySet()) {
                if (i.getEffects().get(0).drinkable() && i.usable(c, user, user)) {
                    usables.add(i);
                }
            }
            if (usables.size() > 0 && user instanceof NPC) {
                used = pickBest(c, (NPC) user, target, usables);
            }
        }
        if (used == null) {
            c.write(user, "Skill failed...");
        } else {
            boolean eventful = false;
            if (shouldPrint(target))
                c.write(user, Formatter.format(
                            String.format("{self:SUBJECT-ACTION:%s|%ss} %s%s", used.getEffects().get(0).getSelfVerb(),
                                            used.getEffects().get(0).getSelfVerb(), used.pre(), used.getName()),
                            user, target));
            for (ItemEffect e : used.getEffects()) {
                eventful = e.use(c, user, target, used) || eventful;
            }
            if (!eventful && shouldPrint(target)) {
                c.write(user, "...But nothing happened.");
            }
            user.consume(used, 1);
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new UseDraft();
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
        return "Drink a draft";
    }

    @Override
    public boolean makesContact() {
        return false;
    }
    
    private boolean shouldPrint(Character target) {
        return !target.human() || !target.is(Stsflag.blinded);
    }
}
