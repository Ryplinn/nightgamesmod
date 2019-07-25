package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.pet.PetCharacter;
import nightgames.skills.damage.DamageType;

import java.util.Optional;

public class FlyCatcher extends Skill {
    FlyCatcher() {
        super("Fly Catcher");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.ki) >= 9 || user.get(Attribute.cunning) >= 18;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !c.getPetsFor(target).isEmpty() && user.canAct() && c.getStance().mobile(user)
                        && !c.getStance().prone(user);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 15;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Focus on eliminating the enemy pet: 25% Stamina";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Optional<PetCharacter> targetPet = Random.pickRandom(c.getPetsFor(target));
        if (targetPet.isPresent()) {
            writeOutput(c, Result.normal, user, targetPet.get());
            double m = Random.random(30, 50);
            targetPet.get().pain(c, user, (int) DamageType.physical.modifyDamage(user, targetPet.get(), m));
            user.weaken(c, user.getStamina().max() / 4);
        return true;
        } else {
            writeOutput(c, Result.normal, user, target);
            return false;   
        }
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.summoning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return receive(c, damage, modifier, user, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return Formatter.format("{self:SUBJECT-ACTION:try|tries} to chase down {other:name-possessive} pet, but there are none!", user, target);
        }
        return Formatter.format("{self:SUBJECT-ACTION:take|takes} the time to focus on chasing down {other:name-do}, "
                        + "finally catching {other:direct-object} in a submission hold.", user, target);
    }

}
