package nightgames.characters;

import nightgames.actions.Action;
import nightgames.actions.Move;
import nightgames.actions.Movement;
import nightgames.characters.custom.effect.CustomEffect;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.daytime.Daytime;
import nightgames.global.DebugFlags;
import nightgames.global.Flag;
import nightgames.global.Match;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.pet.PetCharacter;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;
import nightgames.skills.Wait;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Decider {
    private static void addAllSkillsWithPriority(ArrayList<WeightedSkill> priority, HashSet<Skill> skills,
                    float weight) {
        for (Skill s : skills) {
            priority.add(new WeightedSkill(weight, s));
        }
    }

    static ArrayList<WeightedSkill> parseSkills(HashSet<Skill> available, Combat c, NPC character) {
        HashSet<Skill> damage = new HashSet<>();
        HashSet<Skill> pleasure = new HashSet<>();
        HashSet<Skill> fucking = new HashSet<>();
        HashSet<Skill> position = new HashSet<>();
        HashSet<Skill> debuff = new HashSet<>();
        HashSet<Skill> recovery = new HashSet<>();
        HashSet<Skill> calming = new HashSet<>();
        HashSet<Skill> summoning = new HashSet<>();
        HashSet<Skill> stripping = new HashSet<>();
        HashSet<Skill> misc = new HashSet<>();
        ArrayList<WeightedSkill> priority = new ArrayList<>();
        for (Skill a : available) {
            if (a.type(c, character) == Tactics.damage) {
                damage.add(a);
            } else if (a.type(c, character) == Tactics.pleasure) {
                pleasure.add(a);
            } else if (a.type(c, character) == Tactics.fucking) {
                fucking.add(a);
            } else if (a.type(c, character) == Tactics.positioning) {
                position.add(a);
            } else if (a.type(c, character) == Tactics.debuff) {
                debuff.add(a);
            } else if (a.type(c, character) == Tactics.recovery) {
                recovery.add(a);
            } else if (a.type(c, character) == Tactics.calming) {
                calming.add(a);
            } else if (a.type(c, character) == Tactics.summoning) {
                summoning.add(a);
            } else if (a.type(c, character) == Tactics.stripping) {
                stripping.add(a);
            } else if (a.type(c, character) == Tactics.misc) {
                misc.add(a);
            }
        }
        switch (character.mood) {
            case confident:
                // i can do whatever i want
                addAllSkillsWithPriority(priority, position, 1.0f);
                addAllSkillsWithPriority(priority, stripping, 1.0f);
                addAllSkillsWithPriority(priority, debuff, 1.0f);
                addAllSkillsWithPriority(priority, pleasure, 1.0f);
                addAllSkillsWithPriority(priority, fucking, 1.0f);
                addAllSkillsWithPriority(priority, damage, 1.0f);
                addAllSkillsWithPriority(priority, summoning, .5f);
                addAllSkillsWithPriority(priority, misc, 1f);
                break;
            case angry:
                addAllSkillsWithPriority(priority, damage, 2.5f);
                addAllSkillsWithPriority(priority, position, 2.0f);
                addAllSkillsWithPriority(priority, debuff, 2.0f);
                addAllSkillsWithPriority(priority, stripping, 1.0f);
                addAllSkillsWithPriority(priority, pleasure, 0.0f);
                addAllSkillsWithPriority(priority, misc, 1f);
                addAllSkillsWithPriority(priority, summoning, 0f);
                break;
            case nervous:
                addAllSkillsWithPriority(priority, summoning, 2.0f);
                addAllSkillsWithPriority(priority, debuff, 2.0f);
                addAllSkillsWithPriority(priority, calming, 2.0f);
                addAllSkillsWithPriority(priority, recovery, 2.0f);
                addAllSkillsWithPriority(priority, position, 1.0f);
                addAllSkillsWithPriority(priority, damage, .5f);
                addAllSkillsWithPriority(priority, pleasure, 0.0f);
                addAllSkillsWithPriority(priority, misc, 1f);
                break;
            case desperate:
                // and probably a bit confused
                addAllSkillsWithPriority(priority, calming, 4.0f);
                addAllSkillsWithPriority(priority, recovery, 4.0f);
                addAllSkillsWithPriority(priority, debuff, 4.0f);
                addAllSkillsWithPriority(priority, misc, 3.0f);
                addAllSkillsWithPriority(priority, position, 2.0f);
                addAllSkillsWithPriority(priority, damage, 2.0f);
                addAllSkillsWithPriority(priority, pleasure, 1.0f);
                addAllSkillsWithPriority(priority, fucking, 1.0f);
                break;
            case horny:
                addAllSkillsWithPriority(priority, fucking, 5.0f);
                addAllSkillsWithPriority(priority, stripping, 1.0f);
                addAllSkillsWithPriority(priority, pleasure, 1.0f);
                addAllSkillsWithPriority(priority, position, 1.0f);
                addAllSkillsWithPriority(priority, debuff, 0f);
                addAllSkillsWithPriority(priority, misc, 1f);
                break;
            case dominant:
                addAllSkillsWithPriority(priority, position, 3.0f);
                addAllSkillsWithPriority(priority, fucking, 2.0f);
                addAllSkillsWithPriority(priority, stripping, 2.0f);
                addAllSkillsWithPriority(priority, pleasure, 2.0f);
                addAllSkillsWithPriority(priority, debuff, 2.0f);
                addAllSkillsWithPriority(priority, damage, 2.0f);
                addAllSkillsWithPriority(priority, summoning, 1.0f);
                addAllSkillsWithPriority(priority, misc, 1f);
                break;
        }
        /*
         * if(character.getArousal().percent()>85||character.getStamina().percent()<10){ priority.add(recovery); priority.add(damage); priority.add(pleasure); } if((c.stance.penetration(character)&&c.stance.dom(character))||c.stance.enumerate()==Stance.sixnine||(c.stance.dom(character)&&c.stance.enumerate()==Stance.behind)){ priority.add(pleasure); priority.add(pleasure); priority.add(damage); priority.add(recovery); } if(!target.canAct()){ priority.add(stripping); priority.add(pleasure); priority.add(summoning); priority.add(position); } else if(!target.nude()&&(target.getArousal().percent()>60||target.getStamina().percent()<50)){ priority.add(stripping); priority.add(pleasure); priority.add(damage); priority.add(position); } else if(c.stance.dom(character)){ priority.add(pleasure); priority.add(stripping); priority.add(summoning); priority.add(damage); priority.add(position); priority.add(debuff); } else{ priority.add(summoning); priority.add(pleasure); priority.add(debuff); priority.add(damage); priority.add(position); priority.add(stripping); priority.add(recovery); }
         */ return priority;
    }

    static Action parseMoves(Collection<Action> available, Collection<Movement> radar, NPC character) {
        HashSet<Action> enemy = new HashSet<>();
        HashSet<Action> onlyWhenSafe = new HashSet<>();
        HashSet<Action> utility = new HashSet<>();
        HashSet<Action> tactic = new HashSet<>();
        if (character.mostlyNude()) {
            for (Action act : available) {
                if (act.consider() == Movement.resupply) {
                    return act;
                } else if (act.getClass() == Move.class) {
                    Move movement = (Move) act;
                    if (movement.consider() == Movement.union && !radar.contains(Movement.union)
                                    || movement.consider() == Movement.dorm && !radar.contains(Movement.dorm)) {
                        return act;
                    }
                }
            }
        }
        if (character.getArousal().percent() >= 40 && !character.location().humanPresent() && radar.isEmpty()) {
            for (Action act : available) {
                if (act.consider() == Movement.masturbate) {
                    return act;
                }
            }
        }
        if (character.getStamina().percent() <= 60 || character.getArousal().percent() >= 30) {
            for (Action act : available) {
                if (act.consider() == Movement.bathe) {
                    return act;
                } else if (act.getClass() == Move.class) {
                    Move movement = (Move) act;
                    if (movement.consider() == Movement.pool || movement.consider() == Movement.shower) {
                        return act;
                    }
                }
            }
        }
        if (character.getAttribute(Attribute.science) >= 1 && !character.has(Item.Battery, 10)) {
            for (Action act : available) {
                if (act.consider() == Movement.recharge) {
                    return act;
                }
            }
            String workshop = Flag.checkFlag(Flag.FTC) ? "Cabin" : "Workshop";
            Move path = character.findPath(Match.getMatch().gps(workshop));
            if (path != null) {
                return path;
            }
        }
        for (Action act : available) {
            if (radar.contains(act.consider())) {
                enemy.add(act);
            } else if (act.consider() == Movement.bathe || act.consider() == Movement.craft
                            || act.consider() == Movement.scavenge || act.consider() == Movement.hide
                            || act.consider() == Movement.trap || act.consider() == Movement.wait
                            || act.consider() == Movement.engineering || act.consider() == Movement.dining
                            || act.consider() == Movement.disguise) {
                onlyWhenSafe.add(act);
            } else {
                utility.add(act);
            }
        }
        
        if (character.plan == Plan.hunting && !enemy.isEmpty()) {
            tactic.addAll(enemy);
        }
        if (!character.location().humanPresent()) {
            tactic.addAll(onlyWhenSafe);
        }
        tactic.addAll(utility);
        if (tactic.isEmpty()) {
            tactic.addAll(available);
        }
        // give disguise some priority when just picking something random
        if (tactic.stream().anyMatch(a -> a.consider() == Movement.disguise) && Random.random(5) == 0) {
            return tactic.stream().filter(a -> a.consider() == Movement.disguise).findFirst().orElse(null);
        }
        Action[] actions = tactic.toArray(new Action[0]);
        return actions[Random.random(actions.length)];
    }

    public static void visit(Character self) {
        if (Flag.checkCharacterDisabledFlag(self.getType())) {
            return;
        }
        int max = 0;
        Character bff = null;
        if (!self.attractions.isEmpty()) {
            for (CharacterType key : self.attractions.keySet()) {
                Character friend = key.fromPool().orElseThrow(() -> new CharacterPool.CharacterNotFoundException(key));
                if (self.getAttraction(friend) > max && !friend.human()) {
                    max = self.getAttraction(friend);
                    bff = friend;
                }
            }
            if (bff != null) {
                self.gainAffection(bff, Random.random(3) + 1);
                bff.gainAffection(self, Random.random(3) + 1);
                switch (Random.random(3)) {
                    case 0:
                        Daytime.train(self, bff, Attribute.power);
                    case 1:
                        Daytime.train(self, bff, Attribute.cunning);
                    default:
                        Daytime.train(self, bff, Attribute.seduction);
                }
            }
        }
    }

    public static WeightedSkill prioritizePet(PetCharacter self, Character target, List<Skill> plist, Combat c) {
        List<WeightedSkill> weightedList = plist.stream().map(skill -> new WeightedSkill(1.0, skill)).collect(Collectors.toList());
        return prioritizePetWithWeights(self, target, weightedList, c);
    }

    private static WeightedSkill prioritizePetWithWeights(PetCharacter self, Character target,
                    List<WeightedSkill> plist, Combat c) {
        if (plist.isEmpty()) {
            return new WeightedSkill(1.0, new Wait());
        }
        // The higher, the better the AI will plan for "rare" events better
        final int RUN_COUNT = 3;
        // Decrease to get an "easier" AI. Make negative to get a suicidal AI.
        final double RATING_FACTOR = 0.02f;

        // Starting fitness
        Character master = self.getSelf().owner();
        Character other = c.getOpponent(self);
        double masterFit = master.getFitness(c);
        double otherFit = master.getOtherFitness(c, other);

        // Now simulate the result of all actions
        ArrayList<WeightedSkill> moveList = new ArrayList<>();
        double sum = 0;

        for (WeightedSkill wskill : plist) {
            // Run it a couple of times
            double rating, raw_rating = 0;
            self.getTraits().stream().filter(trait -> trait.baseTrait != null).map(trait -> trait.baseTrait)
                            .map(baseTrait -> baseTrait.skillWeightMod(wskill.skill, c, self))
                            .forEach(mod -> wskill.weight += mod);
            for (int j = 0; j < RUN_COUNT; j++) {
                raw_rating += ratePetMove(self, wskill.skill, target, c, masterFit, otherFit);
            }

            // Sum up rating, add to map
            rating = Math.pow(2, RATING_FACTOR * raw_rating + wskill.weight + wskill.skill.priorityMod(c, self)
                            + Match.getMatch().condition.getSkillModifier().encouragement(wskill.skill, c, self));
            sum += rating;
            moveList.add(new WeightedSkill(sum, raw_rating, rating, wskill.skill));
        }
        if (sum == 0 || moveList.size() == 0) {
            return null;
        }
        // Debug
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILLS)) {
            StringBuilder s = new StringBuilder("Pet choices: ");
            for (WeightedSkill entry : moveList) {
                s.append(String.format("\n(%.1f\t\t%.1f\t\tculm: %.1f\t\t/ %.1f)\t\t-> %s", entry.raw_rating,
                                entry.rating, entry.weight, entry.rating * 100.0f / sum, entry.skill.getLabel(c, self)));
            }
            System.out.println(s);
        }
        // Select
        double s = Random.randomdouble() * sum;
        for (WeightedSkill entry : moveList) {
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILLS)) {
                System.out.printf("%.1f/%.1f %s\n", entry.weight, s, entry.skill.toString());
            }
            if (entry.weight > s) {
                return entry;
            }
        }
        return moveList.get(moveList.size() - 1);
    }

    static Skill prioritizeNew(Character self, List<WeightedSkill> plist, Combat c) {
        if (plist.isEmpty()) {
            return null;
        }
        // The higher, the better the AI will plan for "rare" events better
        final int RUN_COUNT = 5;
        // Decrease to get an "easier" AI. Make negative to get a suicidal AI.
        final double RATING_FACTOR = 0.02f;

        // Starting fitness
        Character other = c.getOpponent(self);
        double selfFit = self.getFitness(c);
        double otherFit = self.getOtherFitness(c, other);

        // Now simulate the result of all actions
        ArrayList<WeightedSkill> moveList = new ArrayList<>();
        double sum = 0;
        for (WeightedSkill wskill : plist) {
            // Run it a couple of times
            double rating, raw_rating = 0;
            self.getTraits().stream().filter(trait -> trait.baseTrait != null).map(trait -> trait.baseTrait)
                            .map(baseTrait -> baseTrait.skillWeightMod(wskill.skill, c, self)).forEach(mod -> wskill.weight += mod);
            if (wskill.skill.type(c, self) == Tactics.fucking && self.has(Trait.experienced)) {
                wskill.weight += 1.0;
            }
            for (int j = 0; j < RUN_COUNT; j++) {
                raw_rating += rateMove(self, wskill.skill, c, selfFit, otherFit);
            }

            if (self instanceof NPC) {
                NPC selfNPC = (NPC) self;
                wskill.weight += (selfNPC.ai.getAiModifiers(selfNPC).modAttack(wskill.skill.getClass()));
            }
            // Sum up rating, add to map
            rating = Math.pow(2, RATING_FACTOR * raw_rating + wskill.weight + wskill.skill.priorityMod(c, self)
                            + Match.getMatch().condition.getSkillModifier().encouragement(wskill.skill, c, self));
            sum += rating;
            moveList.add(new WeightedSkill(sum, raw_rating, rating, wskill.skill));
        }
        if (sum == 0 || moveList.size() == 0) {
            return null;
        }
        // Debug
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILLS)) {
            StringBuilder s = new StringBuilder("AI choices: ");
            for (WeightedSkill entry : moveList) {
                s.append(String.format("\n(%.1f\t\t%.1f\t\tculm: %.1f\t\t/ %.1f)\t\t-> %s", entry.raw_rating,
                                entry.rating, entry.weight, entry.rating * 100.0f / sum, entry.skill.getLabel(c, self)));
            }
            System.out.println(s);
        }
        // Select
        double s = Random.randomdouble() * sum;
        for (WeightedSkill entry : moveList) {
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILLS)) {
                System.out.printf("%.1f/%.1f %s\n", entry.weight, s, entry.skill.toString());
            }
            if (entry.weight > s) {
                return entry.skill;
            }
        }
        return moveList.get(moveList.size() - 1).skill;
    }

    private static double ratePetMove(PetCharacter self, Skill skill, Character target, Combat c, double masterFit, double otherFit) {
        // Clone ourselves a new combat... This should clone our characters, too
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILLS_RATING) && (c.p1.human() || c.p2.human())) {
            System.out.println("===> Rating " + skill);
            System.out.println("Before:\n" + c.debugMessage());
        }
        return rateActionWithObserver(self, self.getSelf().owner(), target, c, masterFit, otherFit, (combat, selfCopy, other) -> {
            skill.resolve(combat, selfCopy, other, true);
            return true;
        });
    }

    private static double rateMove(Character self, Skill skill, Combat c, double selfFit, double otherFit) {
        // Clone ourselves a new combat... This should clone our characters, too
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILLS_RATING) && (c.p1.human() || c.p2.human())) {
            System.out.println("===> Rating " + skill);
            System.out.println("Before:\n" + c.debugMessage());
        }
        // FIXME: null pointer exception here attempting to rate Tighten while fighting Eve during a threesome
        return rateAction(self, c, selfFit, otherFit, (combat, selfCopy, other) -> {
            skill.resolve(combat, selfCopy, other, true);
            return true;
        });
    }

    private static Character getCopyFromCombat(Combat c, Combat clonedCombat, Character self) {
        if (c.p1.equals(self)) {
            return clonedCombat.p1;
        } else if (c.p2.equals(self)) {
            return clonedCombat.p2;
        } else {
            if (c.otherCombatantsContains(self)) {
                return clonedCombat.getOtherCombatants().stream().filter(other -> other.equals(self)).findAny()
                                .orElseThrow(() -> new IllegalArgumentException("Tried to use a badly cloned combat"));
            } else {
                throw new IllegalArgumentException("Tried to use a badly cloned combat");
            }
        }
    }

    public static double rateAction(Character skillUser, Combat c, double selfFit, double otherFit, CustomEffect effect) {
        return rateActionWithObserver(skillUser, skillUser, c.getOpponent(skillUser), c, selfFit, otherFit, effect);
    }

    private static double rateActionWithObserver(Character skillUser, Character fitnessObserver, Character target,
                    Combat c, double selfFit, double otherFit, CustomEffect effect) {
        // Clone ourselves a new combat... This should clone our characters, too
        Combat c2;
        try {
            c2 = c.clone();
        } catch (CloneNotSupportedException e) {
            return 0;
        }
        CharacterPool previousPool = CharacterType.lastUsedPool;
        CharacterPool simPool = new CharacterPool();
        simPool.putAll(c2.p1, c2.p2);
        simPool.combatStart(c2);
        CharacterType.usePool(simPool);

        DebugFlags.debugSimulation += 1;
        Character newSkillUser = getCopyFromCombat(c, c2, skillUser);
        Character newObserver = getCopyFromCombat(c, c2, fitnessObserver);
        Character newOpponent = c2.getOpponent(newSkillUser);
        Character newTarget = getCopyFromCombat(c, c2, target);

        effect.execute(c2, newSkillUser, newTarget);
        DebugFlags.debugSimulation -= 1;
        double selfFitnessDelta = newObserver.getFitness(c2) - selfFit;
        double otherFitnessDelta = newObserver.getOtherFitness(c2, newOpponent) - otherFit;
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILLS_RATING) && (c2.p1.human() || c2.p2.human())) {
            System.out.println("After:\n" + c2.debugMessage());
        }
        simPool.combatEnd();
        CharacterType.usePool(previousPool);
        return selfFitnessDelta - otherFitnessDelta;
    }
}
