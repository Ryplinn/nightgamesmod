package nightgames.skills.strategy;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Finger;
import nightgames.skills.Handjob;
import nightgames.skills.Skill;

public class UseHandsStrategy extends KnockdownThenActionStrategy {
    @Override
    public double weight(Combat c, Character self) {
        double weight = .75;
        if (!(new Handjob(self)).requirements(c, self, c.getOpponent(self)) && !(new Finger(self).requirements(c, c.getOpponent(self)))) {
            return 0;
        }
        if (self.has(Trait.defthands)) {
            weight += 1;
        }
        if (self.getMood().equals(Emotion.confident)) {
            weight += .25;
        }
        return weight;
    }

    @Override
    protected Optional<Set<Skill>> getPreferredSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        return emptyIfSetEmpty(allowedSkills.stream()
                        .filter(skill -> ((skill.getTags(c).contains(SkillTag.usesHands)
                                        && skill.getTags(c).contains(SkillTag.pleasure))
                                        || skill.getTags(c).contains(SkillTag.stripping))
                                        && !skill.getTags(c).contains(SkillTag.suicidal))
                        .collect(Collectors.toSet()));
    }
    
    @Override
    public CombatStrategy instance() {
        return new UseHandsStrategy();
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Global.random(2, 6);
    }
}