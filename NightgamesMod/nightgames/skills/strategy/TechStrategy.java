package nightgames.skills.strategy;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.skills.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TechStrategy extends AbstractStrategy {

    @Override
    public double weight(Combat c, Character self) {
        double score = 0;
        if (self.has(Trait.harpoon)) {
            score += .8;
        }
        if (self.has(Trait.bomber)) {
            score += .5;
        }
        if (self.has(Trait.maglocks)) {
            score += .5;
        }
        if (self.has(Trait.trainingcollar)) {
            score += .5;
        }
        if (self.has(Trait.yank)) {
            score += .2;
        }
        if (self.has(Trait.conducivetoy)) {
            score += .2;
        }
        return score > 0 ? score : -9999;
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Random.random(5, 10);
    }

    @Override
    public CombatStrategy instance() {
        return new TechStrategy();
    }

    @Override
    protected Set<Skill> filterSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        Set<Skill> preferred = new HashSet<>();
        Set<Skill> secondary = new HashSet<>();
        if (self.has(Trait.harpoon)) {
            preferred.add(new LaunchHarpoon());
            preferred.add(new Yank());
            secondary.addAll(new UseToyStrategy().getPreferredSkills(c, self, allowedSkills)
                                                 .orElse(Collections.emptySet()));
        }
        if (self.has(Trait.bomber)) {
            preferred.add(new ThrowBomb());
        }
        if (self.has(Trait.maglocks)) {
            preferred.add(new MagLock());
            secondary.addAll(new KnockdownStrategy().filterSkills(c, self, allowedSkills));
        }
        if (self.has(Trait.trainingcollar)) {
            preferred.add(new Collar());
            secondary.addAll(new KnockdownStrategy().filterSkills(c, self, allowedSkills));
        }

        preferred.removeIf(s -> !Skill.skillIsUsable(c, s, self, null));
        secondary.removeIf(s -> !Skill.skillIsUsable(c, s, self, null));
        
        if (!preferred.isEmpty()) {
            return preferred;
        }

        if (!secondary.isEmpty() && Random.randomdouble() < .7) {
            return secondary;
        }
        
        return Collections.emptySet();
    }

}
