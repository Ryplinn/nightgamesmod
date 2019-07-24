package nightgames.modifier.skill;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.modifier.ModifierCategory;
import nightgames.modifier.ModifierComponent;
import nightgames.skills.Skill;
import nightgames.skills.SkillPool;
import nightgames.skills.Tactics;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class SkillModifier implements ModifierCategory<SkillModifier>, ModifierComponent {
    public static final SkillModifierLoader loader = new SkillModifierLoader();
    public static final SkillModifierCombiner combiner = new SkillModifierCombiner();

    public Set<Skill> bannedSkills() {
        return Collections.emptySet();
    }

    public Set<Tactics> bannedTactics() {
        return Collections.emptySet();
    }

    public Map<Skill, Double> encouragedSkills() {
        return Collections.emptyMap();
    }

    public boolean playerOnly() {
        return true;
    }

    public Set<Skill> allowedSkills(Combat c) {
        Set<Skill> skills = SkillPool.skillPool.stream()
                        .map(Supplier::get).collect(Collectors.toSet());
        skills.removeIf(s -> bannedSkills().contains(s));
        skills.removeIf(s -> bannedTactics().contains(s.type(c, null)));
        return skills;
    }

    public SkillModifier combine(SkillModifier next) {
        SkillModifier first = this;
        return new SkillModifier() {
            @Override public String toString() {
                return first.toString() + ", " + next.toString();
            }

            @Override public String name() {
                return first.name() + " then " + next.name();
            }

            @Override public Set<Skill> allowedSkills(Combat c) {
                // allows only skills allowed by both modifiers
                Set<Skill> allowed = new HashSet<>(first.allowedSkills(c));
                allowed.retainAll(next.allowedSkills(c));
                return allowed;
            }

            @Override public Set<Skill> bannedSkills() {
                // bans skills banned by either modifier
                Set<Skill> banned = new HashSet<>(first.bannedSkills());
                banned.addAll(next.bannedSkills());
                return banned;
            }

            @Override public Set<Tactics> bannedTactics() {
                // bans tactics banned by either modifier
                Set<Tactics> banned = new HashSet<>(first.bannedTactics());
                banned.addAll(next.bannedTactics());
                return banned;
            }

            @Override public Map<Skill, Double> encouragedSkills() {
                // encourages skills encouraged by either modifier
                // If a skill is encouraged by both modifiers, the encouragements are added together.
                Map<Skill, Double> encouraged = new HashMap<>(first.encouragedSkills());
                for (Map.Entry<Skill, Double> entry : next.encouragedSkills().entrySet()) {
                    encouraged.merge(entry.getKey(), entry.getValue(), Double::sum);
                }
                return encouraged;
            }

            @Override public double encouragement(Skill s, Combat c, Character u) {
                // encourages a skill by the sum of each weight
                return first.encouragement(s, c, u) + next.encouragement(s, c, u);
            }
        };
    }

    public boolean allowedSkill(Combat c, Skill s) {
        return !(bannedSkills().contains(s) || bannedTactics().contains(s.type(c, null)));
    }

    public double encouragement(Skill s, Combat c, Character user) {
        return encouragedSkills().getOrDefault(s, 0.0);
    }

    @Override
    public abstract String toString();

}
