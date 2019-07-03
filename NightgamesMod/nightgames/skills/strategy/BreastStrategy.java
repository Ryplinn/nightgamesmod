package nightgames.skills.strategy;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BreastStrategy extends KnockdownThenActionStrategy {
    @Override
    public double weight(Combat c, Character self) {
        double weight = .55;
       
        if (self.has(Trait.lactating)) {
            weight += .25;
        }
        if (self.has(Trait.temptingtits)) {
            weight += .25;
        }
        if (self.has(Trait.beguilingbreasts)) {
            weight += .25;
        }
        if (self.getMood().equals(Emotion.confident)) {
            weight += .25;
        }
        return weight;
    }

    @Override
    protected Optional<Set<Skill>> getPreferredSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        return emptyIfSetEmpty(allowedSkills.stream()
                        .filter(skill -> ((skill.getTags(c).contains(SkillTag.usesBreasts)) 
                                        || skill.getTags(c).contains(SkillTag.stripping))
                                        && !skill.getTags(c).contains(SkillTag.suicidal))
                        .collect(Collectors.toSet()));
    }
    
    @Override
    public CombatStrategy instance() {
        return new BreastStrategy();
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Random.random(2, 6);
    }
}
