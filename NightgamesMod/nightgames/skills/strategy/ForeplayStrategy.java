package nightgames.skills.strategy;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ForeplayStrategy extends KnockdownThenActionStrategy {
    @Override
    public double weight(Combat c, Character self) {
        return 1.;
    }

    @Override
    protected Optional<Set<Skill>> getPreferredAfterKnockdownSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        if (c.getStance().havingSex(c) && c.getStance().getPartsFor(c, self, c.getStance().getPartner(c, self)).stream().allMatch(
                        BodyPart::isGenital)) {
            // terminate this strategy if already fucking
            return Optional.of(Collections.emptySet());
        }
        return emptyIfSetEmpty(allowedSkills.stream().filter(skill -> Tactics.pleasure.equals(skill.type(c)) || skill.getTags(c).contains(SkillTag.stripping)).collect(Collectors.toSet()));
    }

    @Override
    public CombatStrategy instance() {
        return new ForeplayStrategy();
    }
    
    @Override
    public int initialDuration(Combat c, Character self) {
        return Random.random(2, 6);
    }
}
