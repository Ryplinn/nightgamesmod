package nightgames.skills.strategy;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Footjob;
import nightgames.skills.Skill;
import nightgames.skills.StandUp;
import nightgames.skills.TakeOffShoes;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FootjobStrategy extends KnockdownThenActionStrategy {
    @Override
    public double weight(Combat c, Character self) {
        double weight = .25;
        if (!(new Footjob()).requirements(c, self, c.getOpponent(self))) {
            return 0;
        }
        if (c.getOpponent(self).has(Trait.footfetishist)) {
            weight += 2;
        }
        if (self.has(Trait.nimbletoes)) {
            weight += 1;
        }
        if (self.getMood().equals(Emotion.dominant)) {
            weight += .75;
        }
        return weight;
    }

    @Override
    protected Optional<Set<Skill>> getPreferredSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        Set<Skill> footjobSkills = allowedSkills.stream()
                        .filter(skill -> (skill.getTags(c, self).contains(SkillTag.usesFeet))
                                        && !skill.getTags(c, self).contains(SkillTag.suicidal))
                        .collect(Collectors.toSet());

        if (!footjobSkills.isEmpty()) {
            return Optional.of(footjobSkills);
        }
        if (!c.getOpponent(self).crotchAvailable()) {
            Set<Skill> strippingSkills = allowedSkills.stream()
                            .filter(skill -> (skill.getTags(c, self).contains(SkillTag.stripping))
                                            && !skill.getTags(c, self).contains(SkillTag.suicidal))
                            .collect(Collectors.toSet());
            return Optional.of(strippingSkills);
        }
        
        if (!self.outfit.hasNoShoes()) {
            return Optional.of(Collections.singleton(new TakeOffShoes()));
        }

        StandUp standup = new StandUp();
        if (allowedSkills.contains(standup)) {
            return Optional.of(Collections.singleton(standup));
        }

        return Optional.empty();
    }
    
    @Override
    public CombatStrategy instance() {
        return new FootjobStrategy();
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Random.random(2, 6);
    }
}
