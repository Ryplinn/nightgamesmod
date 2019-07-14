package nightgames.status.addiction;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.GameState;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.status.Status;
import nightgames.status.Stsflag;

import java.util.Optional;

import static nightgames.global.DebugFlags.DEBUG_ADDICTION;

// TODO: Addictions should have their own overarching game concept.
// Treating them as regular statuses is leading to some wonkiness. Their combat effects should still be statuses, of course.
// In particular, combat effects that require the presence of the addiction's cause should only apply when the cause is in the combat.
// FIXME: Stop prostrating in front of Angel when fighting Samantha.
public abstract class AddictionSymptom extends Status {

    public static final float LOW_INCREASE = .03f;
    public static final float MED_INCREASE = .08f;
    public static final float HIGH_INCREASE = .15f;

    public static final float LOW_THRESHOLD = .15f;
    public static final float MED_THRESHOLD = .4f;
    public static final float HIGH_THRESHOLD = .7f;

    private final transient String cause;
    protected float magnitude;
    protected float combatMagnitude;

    // should be saved
    private boolean didDaytime;
    private boolean overloading;

    protected boolean inWithdrawal;

    protected AddictionSymptom(Character affected, String name, String cause, float magnitude) {
        super(name, affected);
        flag(Stsflag.permanent);
        this.name = name;
        this.cause = cause;
        this.magnitude = magnitude;
        combatMagnitude = .01f;
        didDaytime = false;
        inWithdrawal = false;
        overloading = false;
    }

    protected AddictionSymptom(Character affected, String name, String cause) {
        this(affected, name, cause, .01f);
    }
    
    public Character getCause() {
        return GameState.gameState.characterPool.getCharacterByType(cause);
    }

    @Override
    public void tick(Combat c) {
        combatMagnitude += magnitude / 14.0;
    }
    
    public final void clearDaytime() {
        didDaytime = false;
    }

    public final void flagDaytime() {
        didDaytime = true;
    }

    public final float getMagnitude() {
        return magnitude;
    }

    public final Severity getSeverity() {
        if (magnitude < LOW_THRESHOLD) {
            return Severity.NONE;
        } else if (magnitude < MED_THRESHOLD) {
            return Severity.LOW;
        } else if (magnitude < HIGH_THRESHOLD) {
            return Severity.MED;
        } else {
            return Severity.HIGH;
        }
    }
    
    public final Severity getCombatSeverity() {
        if (combatMagnitude < LOW_THRESHOLD) {
            return Severity.NONE;
        } else if (combatMagnitude < MED_THRESHOLD) {
            return Severity.LOW;
        } else if (combatMagnitude < HIGH_THRESHOLD) {
            return Severity.MED;
        } else {
            return Severity.HIGH;
        }
    }

    public final boolean atLeast(Severity threshold) {
        return getSeverity().ordinal() >= threshold.ordinal();
    }

    public final boolean combatAtLeast(Severity threshold) {
        return getCombatSeverity().ordinal() >= threshold.ordinal();
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getType().name());
        obj.addProperty("cause", cause);
        obj.addProperty("magnitude", magnitude);
        obj.addProperty("combat", combatMagnitude);
        obj.addProperty("overloading", overloading);
        obj.addProperty("reenforced", didDaytime);
        return obj;
    }

    protected abstract Optional<Status> withdrawalEffects();

    protected abstract Optional<Status> addictionEffects();

    protected abstract String describeIncrease();

    protected abstract String describeDecrease();

    protected abstract String describeWithdrawal();

    protected abstract String describeCombatIncrease();

    protected abstract String describeCombatDecrease();

    public abstract String informantsOverview();

    public abstract String describeMorning();

    public abstract AddictionType getType();

    public void overload() {
        magnitude = 1.0f;
        overloading = true;
    }

    public Optional<Status> startNight() {
        if (!didDaytime || overloading) {
            if (!overloading) {
                float amount = (float) Random.randomdouble() / 4.f;
                affected.unaddict(null, this.getType(), amount);
            }
            if (isActive()) {
                inWithdrawal = true;
                if (affected.human()) {
                    GUI.gui.message(describeWithdrawal());
                }
                return withdrawalEffects();
            }
        }
        return Optional.empty();
    }

    public void refreshWithdrawal() {
        if (inWithdrawal) {
            Optional<Status> opt = withdrawalEffects();
            if (opt.isPresent() && !affected.has(opt.get()))
                affected.addNonCombat(opt.get().instance(affected, getCause()));
        }
    }

    public void endNight() {
        inWithdrawal = false;
        clearDaytime();
        if (overloading) {
            magnitude = 0.f;
            overloading = false;
            GUI.gui
                  .message("<b>The overload treatment seems to have worked, and you are now rid of all traces of"
                                  + " your " + name + ".\n</b>");
            affected.removeStatusImmediately(this);
        }
    }

    public Optional<Status> startCombat(Combat c, Character opp) {
        combatMagnitude = atLeast(Severity.MED) ? .2f : .0f;
        if (opp.equals(getCause()) && atLeast(Severity.LOW)) {
            flags.forEach(affected::flagStatus);
            return addictionEffects();
        }
        return Optional.empty();
    }

    public void endCombat(Combat c, Character opp) {
        flags.forEach(affected::unflagStatus);
    }

    public boolean isActive() {
        return atLeast(Severity.LOW);
    }

    public boolean shouldRemove() {
        return magnitude <= 0.001f;
    }

    public void aggravate(Combat c, float amt) {
        Severity old = getSeverity();
        float oldMag = magnitude;
        magnitude = clamp(magnitude + amt);
        if (getSeverity() != old) {
            Formatter.writeIfCombat(c, getCause(), Formatter.format(describeIncrease(), affected, getCause()));
        }
        DEBUG_ADDICTION.printf("Aggravating addiction %s on %s by %.3f\n", this.getType(), affected.getTrueName(), amt);
        DEBUG_ADDICTION.printf("%s magnitude is now %.3f (was %.3f)\n", this.getType(), this.getMagnitude(), oldMag);
    }

    public void alleviate(Combat c, float amt) {
        Severity old = getSeverity();
        float oldMag = magnitude;
        magnitude = clamp(magnitude - amt);
        if (getSeverity() != old) {
            Formatter.writeIfCombat(c, getCause(), Formatter.format(describeDecrease(), affected, getCause()));
        }
        DEBUG_ADDICTION.printf("Alleviating addiction %s on %s by %.3f\n", this.getType(), affected.getTrueName(), amt);
        DEBUG_ADDICTION.printf("%s magnitude is now %.3f (was %.3f)\n", this.getType(), this.magnitude, oldMag);
    }

    public void aggravateCombat(Combat c, float amt) {
        Severity old = getCombatSeverity();
        combatMagnitude = clamp(combatMagnitude + amt);
        if (getSeverity() != old) {
            Formatter.writeIfCombat(c, getCause(), Formatter.format(describeCombatIncrease(), affected, getCause()));
        }
    }

    public void alleviateCombat(Combat c, float amt) {
        Severity old = getCombatSeverity();
        combatMagnitude = clamp(combatMagnitude - amt);
        if (getSeverity() != old) {
            Formatter.writeIfCombat(c, getCause(), Formatter.format(describeCombatDecrease(), affected, getCause()));
        }
    }

    private float clamp(float amt) {
        if (amt < 0.f)
            return 0.f;
        if (amt > 1.f)
            return 1.f;
        return amt;
    }
    
    public final boolean isAddiction() {
        return true;
    }

    public enum Severity {
        NONE,
        LOW,
        MED,
        HIGH;
    }

    public void describeInitial() {
        GUI.gui.message(describeIncrease());
    }

    public static AddictionSymptom load(Character self, JsonObject object) {
        String cause = object.get("cause").getAsString();
        if (cause == null) {
            return null;
        }
        AddictionType type = AddictionType.valueOf(object.get("type").getAsString());
        float mag = object.get("magnitude").getAsFloat();
        float combat = object.get("combat").getAsFloat();
        boolean overloading = object.get("overloading").getAsBoolean();
        boolean reenforced = object.get("reenforced").getAsBoolean();
        AddictionSymptom a = type.build(self, cause, mag);
        a.magnitude = mag;
        a.combatMagnitude = combat;
        a.overloading = overloading;
        a.didDaytime = reenforced;
        return a;
    }

    public boolean isInWithdrawal() {
        return inWithdrawal;
    }

    public boolean wasCausedBy(Character target) {
        return target != null && target.getType().equals(getCause().getType());
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AddictionSymptom addiction = (AddictionSymptom) o;

        if (Float.compare(addiction.magnitude, magnitude) != 0)
            return false;
        if (Float.compare(addiction.combatMagnitude, combatMagnitude) != 0)
            return false;
        if (didDaytime != addiction.didDaytime)
            return false;
        if (overloading != addiction.overloading)
            return false;
        if (inWithdrawal != addiction.inWithdrawal)
            return false;
        return cause.equals(addiction.cause);
    }

    @Override public int hashCode() {
        int result = cause.hashCode();
        result = 31 * result + (magnitude != +0.0f ? Float.floatToIntBits(magnitude) : 0);
        result = 31 * result + (combatMagnitude != +0.0f ? Float.floatToIntBits(combatMagnitude) : 0);
        result = 31 * result + (didDaytime ? 1 : 0);
        result = 31 * result + (overloading ? 1 : 0);
        result = 31 * result + (inWithdrawal ? 1 : 0);
        return result;
    }
}
