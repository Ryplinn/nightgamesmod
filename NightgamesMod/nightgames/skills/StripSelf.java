package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.*;
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
    public StripSelf(CharacterType self) {
        super("Strip Self", self);
        addTag(SkillTag.suicidal);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.cunning) >= 3;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        boolean hasClothes = subChoices(c).size() > 0;
        return hasClothes && getSelf().canAct() && c.getStance().mobile(getSelf()) && !getSelf().isPet();
    }

    @Override
    public Collection<String> subChoices(Combat c) {
        return getSelf().getOutfit().getAllStrippable().stream().map(Clothing::getName)
                        .collect(Collectors.toList());
    }

    @Override
    public float priorityMod(Combat c) {
        return -4f;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        Clothing clothing = null;
        int diff = getSelf().stripDifficulty(target);
        if (!choice.isEmpty() && Random.random(50) < diff) {
            c.write(getSelf(), Formatter.format("{self:SUBJECT-ACTION:try|tries} to remove the %s"
                            + " from {self:possessive} body, but it stubbornly sticks"
                            + " to {self:direct-object}.", getSelf(), target, choice));
            return false;
        }
        if (getSelf().human()) {
            Optional<Clothing> stripped = getSelf().getOutfit().getEquipped().stream()
                            .filter(article -> article.getName().equals(choice)).findAny();
            if (stripped.isPresent()) {
                clothing = getSelf().getOutfit().unequip(stripped.get());
                c.getCombatantData(getSelf()).addToClothesPile(getSelf(), clothing);
            }
        } else if (getSelf() instanceof NPC) {
            NPC self = (NPC) getSelf();
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
            best.ifPresent(cloth -> getSelf().strip(cloth, c));
            clothing = best.orElse(null);
        }
        if (clothing == null) {
            c.write(getSelf(), "Skill failed...");
        } else {
            c.write(getSelf(), Formatter.format(String.format("{self:SUBJECT-ACTION:strip|strips} off %s %s.",
                            getSelf().possessiveAdjective(), clothing.getName()), getSelf(), target));
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new StripSelf(user.getType());
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.stripping;
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
        return "Strip yourself";
    }

    @Override
    public boolean makesContact() {
        return false;
    }
}
