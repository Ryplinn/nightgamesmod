package nightgames.skills.strategy;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.FaceSit;
import nightgames.skills.Skill;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FacesitStrategy extends KnockdownThenActionStrategy {
    @Override
    public double weight(Combat c, Character self) {
        double weight = 1;
        if (self.getMood().equals(Emotion.dominant)) {
            weight += 1;
        }
        if (self.has(Trait.drainingass)) {
            weight += 2;
        }
        if (self.has(Trait.bewitchingbottom)) {
            weight += 1;
        }
        if (self.has(Trait.temptingass)) {
            weight += 1;
        }
        if (self.has(Trait.powerfulcheeks) && weight > 0) {
            weight += 1;
        }
        if (!(new FaceSit(self)).requirements(c, self, c.getOpponent(self))) {
            weight = 0;
        }
        return weight;
    }

    @Override
    protected Optional<Set<Skill>> getPreferredSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        return emptyIfSetEmpty(allowedSkills.stream()
                        .filter(skill -> skill.getTags(c).contains(SkillTag.facesit)
                                        && !skill.getTags(c).contains(SkillTag.suicidal))
                        .collect(Collectors.toSet()));
    }
    
    @Override
    public CombatStrategy instance() {
        return new FacesitStrategy();
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Random.random(2, 6);
    }
}
