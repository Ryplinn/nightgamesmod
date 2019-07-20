package nightgames.status.addiction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.status.Status;
import nightgames.utilities.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static nightgames.global.DebugFlags.DEBUG_ADDICTION;

/**
 * Permanent effects caused by opponents that have associated combat statuses, both from the addiction itself and from
 * withdrawal. Removed only with difficulty or expense.
 */
public abstract class Addiction {
    public static final float LOW_INCREASE = .03f;
    public static final float MED_INCREASE = .08f;
    public static final float HIGH_INCREASE = .15f;

    final CharacterType afflicted;
    // Should be saved
    protected final CharacterType cause;
    protected float magnitude;
    protected boolean overloading;

    public boolean didDaytime() {
        return didDaytime;
    }

    private boolean didDaytime;

    protected final String displayName;
    boolean inWithdrawal;

    // Holds tracking symptom during active combat.
    private transient AddictionSymptom currentTrackingSymptom;

    public Addiction(String displayName, CharacterType afflicted, CharacterType cause) {
        this(displayName, afflicted, cause, 0.1f);
    }

    public Addiction(String displayName, CharacterType afflicted, CharacterType cause, float magnitude) {
        this.displayName = displayName;
        this.afflicted = afflicted;
        this.cause = cause;
        this.magnitude = magnitude;
    }

    public Character getCause() {
        return cause.fromPool().orElse(null);
    }

    public void clearDaytime() {
        didDaytime = false;
    }

    public void flagDaytime() {
        didDaytime = true;
    }

    public abstract Optional<Status> withdrawalEffects();

    public abstract String describeIncrease();

    public abstract String describeDecrease();

    public abstract String describeWithdrawal();

    public void overload() {
        magnitude = 1.0f;
        overloading = true;
    }

    public Optional<Status> startNight() {
        if (!didDaytime || overloading) {
            if (!overloading) {
                float amount = (float) Random.randomdouble() / 4.f;
                getAfflicted().unaddict(null, this, amount);
            }
            if (this.isActive()) {
                inWithdrawal = true;
                if (getAfflicted().human()) {
                    GUI.gui.message(describeWithdrawal());
                }
                return withdrawalEffects();
            }
        }
        return Optional.empty();
    }

    public abstract String initialMessage(Combat c, Status replacement);

    /**
     * Description of ongoing effects, usually during combat.
     *
     * @param c A combat instance. May be null.
     * @param severity How severe the addiction is right now. Usually derived from AddictionSymptom.getCombatSeverity().
     * @return A formatted string that describes the appearance or feeling of the addiction.
     */
    public abstract String describe(Combat c, Severity severity);

    public abstract AddictionType getType();

    public boolean isInWithdrawal() {
        return inWithdrawal;
    }

    public Optional<AddictionSymptom> startCombat(Combat c, Character opp) {
        float initialCombatMagnitude = atLeast(Severity.MED) ? .2f : .0f;
        if (opp.equals(getCause()) && atLeast(Severity.LOW)) {
            AddictionSymptom trackingSymptom = this.createTrackingSymptom(initialCombatMagnitude);
            this.currentTrackingSymptom = trackingSymptom;
            trackingSymptom.applyFlags();
            getAfflicted().add(c, trackingSymptom);
            return Optional.of(trackingSymptom);
        }
        return Optional.empty();
    }

    void endCombat() {
        currentTrackingSymptom = null;
    }

    private void modifyMagnitude(Combat c, float amt) {
        Severity old = getSeverity();
        float oldMag = magnitude;
        String debug;
        String describe;
        if (amt > 0.f) {
            debug = "aggravating";
            describe = describeIncrease();
        } else if (amt < 0.f) {
            debug = "alleviating";
            describe = describeDecrease();
        } else {
            return;
        }
        DEBUG_ADDICTION.printf("%s addiction %s on %s by %.3f\n", debug, getType(), afflicted, amt);
        magnitude = MathUtils.clamp(magnitude + amt);
        if (getSeverity() != old) {
            Formatter.writeIfCombat(c, getCause(), Formatter.format(describe, getAfflicted(), getCause()));
        }
        DEBUG_ADDICTION.printf("%s magnitude is now %.3f (was %.3f)\n", getType(), magnitude, oldMag);
    }

    public void aggravate(Combat c, float amt) {
        assert amt > 0.f;
        modifyMagnitude(c, amt);
    }

    public void alleviate(Combat c, float amt) {
        assert amt > 0.f;
        modifyMagnitude(c, -amt);
    }

    public boolean isActive() {
        return atLeast(Severity.LOW);
    }

    public void endNight() {
        inWithdrawal = false;
        clearDaytime();
        if (overloading) {
            magnitude = 0.f;
            overloading = false;
            GUI.gui
                  .message("<b>The overload treatment seems to have worked, and you are now rid of all traces of"
                                  + " your " + displayName + ".\n</b>");
            getAfflicted().removeAddiction(this);
        }
    }

    public abstract String describeMorning();

    public float getMagnitude() {
        return magnitude;
    }

    public boolean shouldRemove() {
        return magnitude <= 0.001f;
    }

    public abstract AddictionSymptom createTrackingSymptom(float initialCombatMagnitude);

    public AddictionSymptom createTrackingSymptom() {
        return createTrackingSymptom(0.0f);
    }

    /**
     * Queries the afflicted character for an existing tracking symptom for this addiction.
     *
     * @return An optional containing the existing tracking symptom if available.
     */
    public Optional<AddictionSymptom> activeTracker() {
        return Optional.ofNullable(currentTrackingSymptom);
    }

    public void describeInitial() {
        GUI.gui.message(describeIncrease());
    }

    public void refreshWithdrawal() {
        if (isInWithdrawal()) {
            Optional<Status> opt = withdrawalEffects();
            if (opt.isPresent() && !getAfflicted().has(opt.get()))
                getAfflicted().addNonCombat(opt.get().instance(getAfflicted(), getCause()));
        }
    }

    public Character getAfflicted() {
        return afflicted.fromPoolGuaranteed();
    }

    public abstract String informantsOverview();

    public abstract String describeCombatIncrease();

    public abstract String describeCombatDecrease();

    public boolean wasCausedBy(Character target) {
        return target != null && target.getType().equals(getCause().getType());
    }

    public void removeImmediately() {

    }

    public enum Severity {
        NONE(0f),
        LOW(.15f),
        MED(.4f),
        HIGH(.7f);

        public float threshold;

        Severity(float threshold) {
            this.threshold = threshold;
        }

        public static Severity severityLevel(float magnitude) {
            if (magnitude < LOW.threshold) {
                return NONE;
            } else if (magnitude < MED.threshold) {
                return LOW;
            } else if (magnitude < HIGH.threshold) {
                return MED;
            } else {
                return HIGH;
            }
        }

        public boolean atLeast(Severity target) {
            return this.ordinal() >= target.ordinal();
        }
    }

    public final boolean atLeast(Severity target) {
        return getSeverity().atLeast(target);
    }

    public Severity getSeverity() {
        return Severity.severityLevel(magnitude);
    }

    public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getType().name());
        obj.addProperty("cause", cause.toString());
        obj.addProperty("magnitude", magnitude);
        obj.addProperty("overloading", overloading);
        obj.addProperty("reinforced", didDaytime);
        return obj;
    }

    public static Addiction load(Character afflicted, JsonObject object) {
        AddictionType type = AddictionType.valueOf(object.get("type").getAsString());
        CharacterType cause = CharacterType.get(object.get("cause").getAsString());
        float magnitude = object.get("magnitude").getAsFloat();
        boolean overloading = object.get("overloading").getAsBoolean();
        boolean reinforced;
        // legacy spelling
        if (object.has("reenforced")) {
            reinforced = object.get("reenforced").getAsBoolean();
        } else {
            reinforced = object.get("reinforced").getAsBoolean();
        }
        Addiction addiction = type.build(afflicted.getType(), cause, magnitude);
        addiction.overloading = overloading;
        addiction.didDaytime = reinforced;
        return addiction;
    }

    public String describeStatus() {
        return String.format("%s's %s: %s (%.3f)", getCause().getName(), displayName,
                        getSeverity().name().toLowerCase(), getMagnitude());
    }

    public static List<Addiction> loadAddictions(Character afflicted, JsonArray jsonElements) {
        List<Addiction> addictions = new ArrayList<>();
        jsonElements.forEach(element -> {
            JsonObject object = element.getAsJsonObject();
            addictions.add(load(afflicted, object));
        });
        return addictions;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Addiction addiction = (Addiction) o;
        return cause.equals(addiction.cause) && afflicted.equals(addiction.afflicted);
    }

    @Override public int hashCode() {
        return Objects.hash(cause, afflicted);
    }
}
