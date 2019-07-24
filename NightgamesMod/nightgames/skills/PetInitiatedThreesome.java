package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.pet.PetCharacter;

public class PetInitiatedThreesome extends PetThreesome {
    public PetInitiatedThreesome() {
        super("Initiate Threesome", 0);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 2.0f;
    }

    @Override
    public Skill copy(Character user) {
        return new PetInitiatedThreesome();
    }

    protected Character getFucker(Combat c, Character user) {
        return user;
    }

    protected Character getMaster(Combat c, Character user) {
        if (user instanceof PetCharacter) {
            return ((PetCharacter)user).getSelf().owner();
        }
        return null;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target) && c.getStance().time >= 2 && user instanceof PetCharacter;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Fucks the opponent as a pet.";
    }
}
