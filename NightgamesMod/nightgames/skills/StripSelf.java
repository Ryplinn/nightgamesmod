package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Decider;
import nightgames.characters.NPC;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.DebugFlags;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.Clothing;
import nightgames.nskills.tags.SkillTag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StripSelf extends Skill {
    public StripSelf() {
        super("Strip Self");
        addTag(SkillTag.suicidal);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.cunning) >= 3;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        boolean hasClothes = subChoices(c, user).size() > 0;
        return hasClothes && user.canAct() && c.getStance().mobile(user) && !user.isPet();
    }

    @Override
    public Collection<String> subChoices(Combat c, Character user) {
        return user.getOutfit().getAllStrippable().stream().map(Clothing::getName)
                        .collect(Collectors.toList());
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return -4f;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Clothing clothing = null;
        int diff = user.stripDifficulty(target);
        if (!choice.isEmpty() && Random.random(50) < diff) {
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:try|tries} to remove the %s"
                            + " from {self:possessive} body, but it stubbornly sticks"
                            + " to {self:direct-object}.", user, target, choice));
            return false;
        }
        if (user.human()) {
            Optional<Clothing> stripped = user.getOutfit().getEquipped().stream()
                            .filter(article -> article.getName().equals(choice)).findAny();
            if (stripped.isPresent()) {
                clothing = user.getOutfit().unequip(stripped.get());
                c.getCombatantData(user).addToClothesPile(user, clothing);
            }
        } else if (user instanceof NPC) {
            NPC self = (NPC) user;
            HashMap<Clothing, Double> checks = new HashMap<>();
            double selfFit = self.getFitness(c);
            double otherFit = self.getOtherFitness(c, target);
            self.getOutfit().getAllStrippable().forEach(article -> {
                double rating = Decider.rateAction(self, c, selfFit, otherFit, (newCombat, newSelf, newOther) -> {
                    newSelf.strip(article, newCombat);
                    return true;
                });
                checks.put(article, rating);
            });
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILLS)) {
                checks.forEach((key, value) -> System.out.println("Stripping " + key + ": " + value));
            }
            Optional<Clothing> best = checks.entrySet().stream().max((first, second) -> {
                double test = second.getValue() - first.getValue();
                if (test < 0) {
                    return -1;
                }
                if (test > 0) {
                    return 1;
                }
                return 0;
            }).map(Map.Entry::getKey);
            best.ifPresent(cloth -> user.strip(cloth, c));
            clothing = best.orElse(null);
        }
        if (clothing == null) {
            c.write(user, "Skill failed...");
        } else {
            c.write(user, Formatter.format(String.format("{self:SUBJECT-ACTION:strip|strips} off %s %s.",
                            user.possessiveAdjective(), clothing.getName()), user, target));
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.stripping;
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
        return "Strip yourself";
    }

    @Override
    public boolean makesContact() {
        return false;
    }
}
