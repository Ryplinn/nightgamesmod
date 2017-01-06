package nightgames.status;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.google.gson.JsonObject;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.skills.Blowjob;
import nightgames.skills.Cunnilingus;
import nightgames.skills.Kiss;
import nightgames.skills.LickNipples;
import nightgames.skills.Skill;
import nightgames.skills.Suckle;

public class FluidAddiction extends DurationStatus {
    protected double magnitude;
    private int activated;
    Character target;

    public FluidAddiction(Character affected, Character target, double magnitude, int duration) {
        super("Addicted", affected, duration);
        this.target = target;
        this.magnitude = magnitude;
        activated = 0;
        flag(Stsflag.fluidaddiction);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    public FluidAddiction(Character affected, Character target) {
        this(affected, target, 1, 4);
    }

    @Override
    public String describe(Combat c) {
        if (isActive()) {
            if (affected.human()) {
                return "You feel a desperate need to taste more of " + target.nameOrPossessivePronoun() + " fluids.";
            } else {
                return affected.name() + " is eyeing "+c.getOpponent(affected).nameDirectObject()+" like a junkie.";
            }
        } else {
            if (affected.human()) {
                return "You're not sure why " + target.nameOrPossessivePronoun()
                                + " fluids is so tantalizing, but you know you want some more";
            } else {
                return affected.name() + " seems to want more of "+c.getOpponent(affected).nameOrPossessivePronoun()+" fluids.";
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
        affected.addlist.add(new Tolerance(affected, 3));
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        affected.emote(Emotion.horny, 15);
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
    public String initialMessage(Combat c, boolean replaced) {
        if (replaced) {
            return String.format("%s is still %s to %s fluids.\n", affected.subjectAction("are", "is"),
                            toString().toLowerCase(), target.nameOrPossessivePronoun());
        }
        return String.format("%s now %s to %s fluids.\n", affected.subjectAction("are", "is"), toString().toLowerCase(),
                        target.nameOrPossessivePronoun());
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
    public int weakened(int x) {
        return 0;
    }

    @Override
    public int tempted(int x) {
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
        if (!isActive()) {
            return Collections.emptySet();
        } else if (target.has(Trait.lactating)) {
            return Arrays.asList((Skill) new Suckle(affected), new LickNipples(affected), new Kiss(affected),
                            new Cunnilingus(affected), new Blowjob(affected));
        } else {
            return Arrays.asList((Skill) new Kiss(affected), new Cunnilingus(affected), new Blowjob(affected));
        }
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new FluidAddiction(newAffected, newOther, magnitude, getDuration());
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
