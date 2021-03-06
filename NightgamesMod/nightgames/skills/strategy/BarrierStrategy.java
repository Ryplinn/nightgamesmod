package nightgames.skills.strategy;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.skills.Barrier;
import nightgames.skills.Skill;
import nightgames.status.Stsflag;

import java.util.Collections;
import java.util.Set;

public class BarrierStrategy extends AbstractStrategy {
    private static final Barrier BARRIER = new Barrier();
    
    @Override
    public double weight(Combat c, Character self) {
        double weight = 3;
        if (self.is(Stsflag.shielded) || BARRIER.requirements(c, self, c.getOpponent(self))) {
            return 0;
        }
        return weight;
    }

    @Override
    protected Set<Skill> filterSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        if (self.is(Stsflag.shielded)) {
            return Collections.emptySet();
        }
        if (allowedSkills.contains(BARRIER)) {
            return Collections.singleton(new Barrier());
        }
        return Collections.emptySet();
    }
    
    @Override
    public CombatStrategy instance() {
        return new BarrierStrategy();
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Random.random(3, 5);
    }
}
