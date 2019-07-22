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
    private final WindUp windUp;

    public WindUpStrategy(NPC self) {
        this.windUp = new WindUp(self.getType());
    }

    @Override
    public double weight(Combat c, Character self) {
        double weight = 2;
        if (windUp.requirements(c, c.getOpponent(self)) && Primed.isPrimed(self, 2)) {
            return 0;
        }
        return weight;
    }

    @Override
    protected Set<Skill> filterSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        if (allowedSkills.contains(windUp)) {
            return Collections.singleton(windUp);
        }
        return Collections.emptySet();
    }
    
    @Override
    public CombatStrategy instance() {
        return new WindUpStrategy((NPC) NPC.noneCharacter());
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return 1;
    }
}
