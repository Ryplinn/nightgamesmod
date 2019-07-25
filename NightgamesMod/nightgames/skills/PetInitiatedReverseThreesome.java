package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.pet.PetCharacter;

public class PetInitiatedReverseThreesome extends ReversePetThreesome {
    PetInitiatedReverseThreesome() {
        super("Initiate Reverse Threesome", 0);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 2.0f;
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
