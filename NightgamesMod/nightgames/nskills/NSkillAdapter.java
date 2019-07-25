package nightgames.nskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

import java.util.Optional;

public class NSkillAdapter extends Skill {
    SkillInterface skill;

    public NSkillAdapter(SkillInterface skill) {
        super(skill.getName());
        this.skill = skill;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return skill.getHighestPriorityUsableResult(c, user, target).isPresent();
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return skill.getHighestPriorityUsableResult(c, user, target).isPresent();
    }

    @Override
    public String describe(Combat c, Character user) {
        Optional<SkillResult> maybeResult = skill.getHighestPriorityUsableResult(c, user, c.getOpponent(user));
        return maybeResult.isPresent() ? maybeResult.get().getDescription() : "";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        return skill.resolve(c, user, target);
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return "";
    }
}
