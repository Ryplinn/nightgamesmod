package nightgames.skills.strategy;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.skills.Skill;

import java.util.Set;

public class DefaultStrategy extends AbstractStrategy {
    @Override
    public double weight(Combat c, Character self) {
        return 5.;
    }

    @Override
    protected Set<Skill> filterSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        return allowedSkills;
    }
    
    @Override
    public CombatStrategy instance() {
        return new DefaultStrategy();
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Random.random(1, 3);
    }
}
