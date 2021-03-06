package nightgames.skills.strategy;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.AssFuck;
import nightgames.skills.Skill;

import java.util.Set;
import java.util.stream.Collectors;

public class ReceiveAnalStrategy extends AbstractStrategy {

    @Override
    public double weight(Combat c, Character self) {
        double weight = -3;
        if (self.getMood().equals(Emotion.horny)) {
            weight = 1;
        }
        if (self.has(Trait.drainingass)) {
            weight += 2;
        }
        if (self.has(Trait.bewitchingbottom)) {
            weight += 1;
        }
        if (self.has(Trait.powerfulcheeks) && weight > 0) {
            weight += 1;
        }
        if (weight > 0 && new AssFuck().usable(c, c.getOpponent(self), self)) {
            weight *= 1.5;
        }
        return weight;
    }

    @Override
    protected Set<Skill> filterSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        if (c.getStance().anallyPenetrated(c, self)) {
            return new FuckStrategy().filterSkills(c, self, allowedSkills);
        }
        Set<Skill> anal = allowedSkills.stream().filter(s -> s.getTags(c, self).contains(SkillTag.anal)).collect(Collectors.toSet());
        if (anal.isEmpty()) {
            if (new AssFuck().usable(c, c.getOpponent(self), self)) {
                return new ForeplayStrategy().filterSkills(c, self, allowedSkills);
            }
            return new FuckStrategy().filterSkills(c, self, allowedSkills);
        }
        return anal;
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Random.random(5, 8);
    }

    @Override
    public CombatStrategy instance() {
        return new ReceiveAnalStrategy();
    }
    
}
