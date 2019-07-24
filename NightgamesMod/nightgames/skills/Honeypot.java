package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.pet.PetCharacter;

import java.util.Optional;

public class Honeypot extends Skill {
    Honeypot() {
        super("Honeypot");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.darkness) >= 9 || user.get(Attribute.seduction) >= 18;
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
        return "Focus on eliminating the enemy pet: 25% arousal";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Optional<PetCharacter> targetPet = Random.pickRandom(c.getPetsFor(target));
        if (targetPet.isPresent()) {
            writeOutput(c, Result.normal, user, targetPet.get());
            double m = Random.random(10, 25);
            targetPet.get().body.pleasure(user, user.body.getRandom("hands"),
                            targetPet.get().body.getRandomGenital(), m, c);
            user.arouse(user.getArousal().max() / 4, c);
        return true;
        } else {
            writeOutput(c, Result.normal, user, target);
            return false;   
        }
    }

    @Override
    public Skill copy(Character user) {
        return new Honeypot();
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
            return Formatter.format("{self:SUBJECT-ACTION:try|tries} to entice {other:name-possessive} pet, but there are none!", user, target);
        }
        return Formatter.format("{self:SUBJECT-ACTION:take|takes} the time to entice {other:name-do}, "
                        + "rubbing {self:reflective} and putting on a show. "
                        + "{other:SUBJECT} takes the bait and approaches {self:direct-object}. With a sudden motion, {self:pronoun-action:capture|captures} "
                        + "{other:direct-object} with {self:possessive} legs and {self:action:have|haves} {self:possessive} way with the poor follower.", user, target);
    }
}
