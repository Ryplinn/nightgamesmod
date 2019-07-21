package nightgames.skills.strategy;

import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.combat.Combat;
import nightgames.skills.Skill;
import nightgames.skills.WindUp;
import nightgames.status.Primed;

import java.util.Collections;
import java.util.Set;

public class WindUpStrategy extends AbstractStrategy {
    private static final WindUp WINDUP = new WindUp(NPC.noneCharacter().getType());
    
    @Override
    public double weight(Combat c, Character self) {
        double weight = 2;
        if ((WINDUP).requirements(c, c.getOpponent(self)) && Primed.isPrimed(self, 2)) {
            return 0;
        }
        return weight;
    }

    @Override
    protected Set<Skill> filterSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        if (allowedSkills.contains(WINDUP)) {
            return Collections.singleton(new WindUp(self.getType()));
        }
        return Collections.emptySet();
    }
    
    @Override
    public CombatStrategy instance() {
        return new WindUpStrategy();
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return 1;
    }
}
