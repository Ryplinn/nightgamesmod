package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.custom.CustomStringEntry;
import nightgames.combat.Combat;
import nightgames.combat.Result;

import java.util.Optional;

public class CustomSkill extends Skill {
    private LoadedSkillData data;

    public CustomSkill(LoadedSkillData data) {
        super(data.name, data.cooldown);
        this.data = data;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return data.skillRequirements.stream().allMatch(req -> req.meets(c, user, target));
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return data.usableRequirements.stream().allMatch(req -> req.meets(c, user, target));
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return data.priorityMod;
    }

    @Override
    public String describe(Combat c, Character user) {
        return data.description;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new CustomSkill(data);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return data.mojoBuilt;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return data.mojoCost;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return data.tactics;
    }

    @Override
    public boolean makesContact() {
        return data.makesContact;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return data.accuracy;
    }

    @Override
    public int speed(Character user) {
        return data.speed;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        Optional<CustomStringEntry> picked = data.labels.stream()
                        .filter(entry -> entry.meetsRequirements(c, user, c.getOpponent(user))).findFirst();
        if (picked.isPresent()) {
            return picked.get().getLine(c, user, c.getOpponent(user));
        }
        return getName(c, user);
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }
}
