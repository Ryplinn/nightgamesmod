package nightgames.skills.strategy;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.skills.*;
import nightgames.stance.Stance;
import nightgames.status.Stsflag;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DisablingStrategy extends KnockdownThenActionStrategy {
    @Override
    public double weight(Combat c, Character self) {
        if (!self.has(Trait.SuccubusWarmth)) {
            return Double.MIN_VALUE;
        }
        double weight = 2;
        if (self.has(Trait.Pacification)) {
            weight += 2;
        }
        if (self.has(Trait.DemonsEmbrace)) {
            weight++;
        }
        if (self.has(Trait.VampireWings)) {
            weight += 2;
        }
        
        return weight;
    }

    @Override
    public CombatStrategy instance() {
        return new DisablingStrategy();
    }
    
    @Override
    protected Set<Skill> filterSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        if (c.getOpponent(self).is(Stsflag.charmed) && c.getStance().en == Stance.succubusembrace) {
            return Collections.emptySet();
        }
        Set<Skill> skills = new HashSet<>();
        skills.add(new Embrace(self));
        
        if (c.getStance().sub(self)) {
            skills.add(new Reversal(self));
        }
        if (self.has(Trait.Pacification)) {
            skills.add(new SuccubusNurse(self));
        }
        if (self.has(Trait.DemonsEmbrace)) {
            skills.add(new WingWrap(self));
        }
        if (skills.stream().anyMatch(skill -> skill.usable(c, c.getOpponent(self)))) {
            return skills;
        }
        return skills = new FuckStrategy().filterSkills(c, self, allowedSkills);
    }
}
