package nightgames.skills.strategy;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Blowjob;
import nightgames.skills.Cunnilingus;
import nightgames.skills.Skill;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OralStrategy extends KnockdownThenActionStrategy {
    @Override
    public double weight(Combat c, Character self) {
        double weight = .55;
        if (!(new Cunnilingus()).requirements(c, self, c.getOpponent(self)) && !(new Blowjob().requirements(c, self, c.getOpponent(self)))) {

            return 0;
        }
        if (self.has(Trait.silvertongue)) {
            weight += .25;
        }
        if (self.has(Trait.soulsucker)) {
            weight += .25;
        }
        if (self.has(Trait.experttongue)) {
            weight += .25;
        }
        if (self.has(Trait.Corrupting)) {
            weight += 10;
        }
        if (self.getMood().equals(Emotion.confident)) {
            weight += .25;
        }
        if (c.getStance().havingSex(c) && !self.has(Trait.Corrupting)) {
            return 0;
        }
        return weight;
    }

    @Override
    protected Optional<Set<Skill>> getPreferredSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        if (c.getStance().havingSex(c)) {
            return Optional.empty();
        }
        return emptyIfSetEmpty(allowedSkills.stream()
                        .filter(skill -> ((skill.getTags(c, self).contains(SkillTag.oral)
                                        && skill.getTags(c, self).contains(SkillTag.pleasure)
                                        ) || skill.getTags(c, self).contains(SkillTag.stripping))
                                        && !skill.getTags(c, self).contains(SkillTag.suicidal))
                        .collect(Collectors.toSet()));
    }
    
    @Override
    public CombatStrategy instance() {
        return new OralStrategy();
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Random.random(4, 8);
    }
}
