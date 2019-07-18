package nightgames.status;

import nightgames.characters.CharacterType;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.requirements.DurationRequirement;

public abstract class DurationStatus extends Status {
    private DurationRequirement req;

    public DurationStatus(String name, CharacterType affected, int duration) {
        super(name, affected);
        if (getAffected().has(Trait.PersonalInertia)) {
            duration = Math.round(1.33f * duration);
        }
        req = new DurationRequirement(duration);

        requirements.add(req);
    }

    public int getDuration() {
        return req.remaining();
    }

    public void setDuration(int duration) {
        req.reset(duration);
    }

    @Override
    public int regen(Combat c) {
        tick(1);
        return 0;
    }

    public void tick(int i) {
        if (affected != null && getAffected().has(Trait.QuickRecovery) && flags
                        .contains(Stsflag.disabling)) {
            i *= 2;
        }
        req.tick(i);
    }
}
