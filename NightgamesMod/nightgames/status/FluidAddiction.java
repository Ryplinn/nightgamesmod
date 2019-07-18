package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.skills.*;

import java.util.*;

public class FluidAddiction extends DurationStatus {
    protected double magnitude;
    private int activated;
    CharacterType target;

    public FluidAddiction(CharacterType affected, CharacterType target, double magnitude, int duration) {
        super("Addicted", affected, duration);
        this.target = target;
        this.magnitude = magnitude;
        activated = 0;
        flag(Stsflag.fluidaddiction);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    public Character getTarget() {
        return target.fromPoolGuaranteed();
    }

    @Override
    public String describe(Combat c) {
        if (isActive()) {
            if (getAffected().human()) {
                return "You feel a desperate need to taste more of " + getTarget().nameOrPossessivePronoun() + " fluids.";
            } else {
                return getAffected().getName() + " is eyeing "+c.getOpponent(getAffected()).nameDirectObject()+" like a junkie.";
            }
        } else {
            if (getAffected().human()) {
                return "You're not sure why " + getTarget().nameOrPossessivePronoun()
                                + " fluids are so tantalizing, but you know you want some more";
            } else {
                return getAffected().getName() + " seems to want more of "+c.getOpponent(getAffected()).nameOrPossessivePronoun()+" fluids.";
            }
        }
    }

    @Override
    public String getVariant() {
        return "Addiction";
    }

    public boolean isActive() {
        return magnitude > 2;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public float fitnessModifier() {
        return -(float)magnitude;
    }

    @Override
    public void onRemove(Combat c, Character other) {
        getAffected().addlist.add(new Tolerance(affected, 3));
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        getAffected().emote(Emotion.horny, 15);
        return 0;
    }

    @Override
    public boolean overrides(Status s) {
        return false;
    }

    @Override
    public void replace(Status s) {
        assert s instanceof FluidAddiction;
        if (!isActive()) {
            FluidAddiction other = (FluidAddiction) s;
            setDuration(Math.max(other.getDuration(), getDuration()));
            magnitude += other.magnitude;
            if (isActive() && activated == 0) {
                activated = 1;
            }
        }
    }

    @Override
    public String toString() {
        if (isActive()) {
            return "Addicted";
        } else if (magnitude >= .99) {
            return "Piqued";
        } else if (magnitude >= 1.99) {
            return "Hooked";
        }
        return "Addicted?";
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        if (replacement != null) {
            return String.format("%s %s to %s fluids.\n", getAffected().subjectAction("are", "is"),
                            toString().toLowerCase(), getTarget().nameOrPossessivePronoun());
        }
        return String.format("%s now %s to %s fluids.\n", getAffected().subjectAction("are", "is"), toString().toLowerCase(),
                        getTarget().nameOrPossessivePronoun());
    }

    @Override
    public int damage(Combat c, int x) {
        return 0;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return 0;
    }

    @Override
    public int weakened(Combat c, int x) {
        return 0;
    }

    @Override
    public int tempted(Combat c, int x) {
        return 0;
    }

    @Override
    public int evade() {
        return 0;
    }

    @Override
    public int escape() {
        return 0;
    }

    @Override
    public int gainmojo(int x) {
        return 0;
    }

    @Override
    public int spendmojo(int x) {
        return 0;
    }

    @Override
    public int counter() {
        return 0;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Collection<Skill> allowedSkills(Combat c) {
        List<Skill> availSkills;
        if (!isActive()) {
            return Collections.emptySet();
        } else if (getTarget().has(Trait.lactating)) {
            availSkills = Arrays.asList(new Suckle(affected), new LickNipples(affected), new Kiss(affected),
                            new Cunnilingus(affected), new Blowjob(affected));
        } else {
            availSkills = Arrays.asList(new Kiss(affected), new Cunnilingus(affected), new Blowjob(affected));
        }
        if (availSkills.stream().anyMatch(skill -> skill.usable(c, getTarget()))) {
            return availSkills;
        } else {
            return Collections.singletonList(new Beg(affected));
        }
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new FluidAddiction(newAffected.getType(), newOther.getType(), magnitude, getDuration());
    }

    public boolean activated() {
        if (activated == 1) {
            activated = 2;
            return true;
        } else {
            return false;
        }
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("magnitude", magnitude);
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new FluidAddiction(null, null, obj.get("magnitude").getAsInt(), obj.get("duration").getAsInt());
    }
}
