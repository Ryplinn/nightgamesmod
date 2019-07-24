package nightgames.skills.strategy;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UseToyStrategy extends KnockdownThenActionStrategy {
    @Override
    public double weight(Combat c, Character self) {
        return 1.;
    }

    @Override
    protected Optional<Set<Skill>> getPreferredSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        return emptyIfSetEmpty(allowedSkills.stream()
                        .filter(skill -> (skill.getTags(c, self).contains(SkillTag.usesToy)
                                        || skill.getTags(c, self).contains(SkillTag.stripping))
                                        && !skill.getTags(c, self).contains(SkillTag.suicidal))
                        .collect(Collectors.toSet()));
    }
    
    @Override
    public CombatStrategy instance() {
        return new UseToyStrategy();
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Random.random(2, 6);
    }
}
