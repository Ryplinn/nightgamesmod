package nightgames.skills.strategy;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class KnockdownThenActionStrategy extends AbstractStrategy {

    protected Optional<Set<Skill>> getPreferredSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        return Optional.empty();
    }
    protected Optional<Set<Skill>> getPreferredAfterKnockdownSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        return Optional.empty(); 
    }
    
    static Optional<Set<Skill>> emptyIfSetEmpty(Set<Skill> skills) {
        if (skills.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(skills);
        }
    }
    
    @Override
    protected Set<Skill> filterSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        Character other = c.getOpponent(self);
        
        Optional<Set<Skill>> preferredSkills = getPreferredSkills(c, self, allowedSkills);

        if (preferredSkills.isPresent()) {
            return preferredSkills.get();
        }

        Set<SkillTag> positioningTags = new HashSet<>();
        positioningTags.add(SkillTag.staminaDamage);
        positioningTags.add(SkillTag.positioning);

        Set<Skill> positioningSkills = allowedSkills.stream()
                        .filter(skill -> positioningTags.stream().anyMatch(tag -> skill.getTags(c, self).contains(tag)))
                        .filter(skill -> !skill.getTags(c, self).contains(SkillTag.suicidal))
                        .collect(Collectors.toSet());
        if (!c.getStance().mobile(self) || c.getStance().mobile(other)) {
            return positioningSkills;
        }
        return getPreferredAfterKnockdownSkills(c, self, allowedSkills).orElse(Collections.emptySet());
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Random.random(2, 6);
    }
}
