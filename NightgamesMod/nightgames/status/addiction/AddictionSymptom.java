package nightgames.status.addiction;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.status.Status;
import nightgames.status.Stsflag;
import nightgames.utilities.MathUtils;

import java.util.Optional;

/**
 * AddictionSymptoms track the strength of an addiction's effects while in combat. By default, it starts mild and
 * worsens each round.
 */
public abstract class AddictionSymptom extends Status {

    public final Addiction source;
    float combatMagnitude;

    protected AddictionSymptom(CharacterType affected, String name, Addiction source, float initialMagnitude) {
        super(name, affected);
        flag(Stsflag.permanent);
        this.name = name;
        this.source = source;
        this.combatMagnitude = initialMagnitude;
    }

    public Character getCause() {
        return source.getCause();
    }

    public Addiction getSource() {
        return source;
    }

    @Override public AddictionSymptom instance(Character newAffected, Character opponent) {
        CharacterType cause = this.source.cause;
        Optional<Addiction> foundAddiction = newAffected.getAddiction(getSource().getType(), cause);
        return foundAddiction.map(Addiction::createTrackingSymptom).orElseThrow(() -> new RuntimeException(
                        String.format("Could not create symptom instance for addiction %s. Afflicted: %s, Source: %s",
                                        this.source, this.affected, cause)));
    }

    @Override
    public void tick(Combat c) {
        combatMagnitude += source.magnitude / 14.0;
    }

    public float getCombatMagnitude() {
        return combatMagnitude;
    }

    public final Addiction.Severity getCombatSeverity() {
        return Addiction.Severity.severityLevel(combatMagnitude);
    }

    final boolean combatAtLeast(Addiction.Severity target) {
        return getCombatSeverity().atLeast(target);
    }

    private String describeCombatIncrease() {
        return source.describeCombatIncrease();
    }

    private String describeCombatDecrease() {
        return source.describeCombatDecrease();
    }

    @Override public String initialMessage(Combat c, Status replacement) {
        return source.initialMessage(c, replacement);
    }

    @Override
    public String describe(Combat combat) {
        return source.describe(combat, getCombatSeverity());
    }

    @Override public int mod(Attribute a) {
        return 0;
    }

    @Override public int regen(Combat c) {
        return 0;
    }

    @Override public int damage(Combat c, int x) {
        return 0;
    }

    @Override public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return 0;
    }

    @Override public int weakened(Combat c, int x) {
        return 0;
    }

    @Override public int tempted(Combat c, int x) {
        return 0;
    }

    @Override public int evade() {
        return 0;
    }

    @Override public int escape(Character from) {
        return 0;
    }

    @Override public int gainmojo(int x) {
        return 0;
    }

    @Override public int spendmojo(int x) {
        return 0;
    }

    @Override public int counter() {
        return 0;
    }

    @Override public int value() {
        return 0;
    }

    @Override
    public void endCombat(Combat c, Character opp) {
        flags.forEach(getAffected()::unflagStatus);
        getSource().endCombat();
    }

    private void modifyCombat(Combat c, float amt) {
        Addiction.Severity oldSeverity = getCombatSeverity();
        combatMagnitude = MathUtils.clamp(combatMagnitude + amt);
        Addiction.Severity newSeverity = getCombatSeverity();
        if (oldSeverity == newSeverity) {
            return;
        }
        String format;
        if (amt > 0.f) {
            format = describeCombatIncrease();
        } else {
            format = describeCombatDecrease();
        }
        Formatter.writeIfCombat(c, getCause(), Formatter.format(format, getAffected(), getCause()));
    }

    public void aggravateCombat(Combat c, float amt) {
        assert amt > 0.f;
        modifyCombat(c, amt);
    }

    public void alleviateCombat(Combat c, float amt) {
        assert amt > 0.f;
        modifyCombat(c, -amt);
    }

    // This method of finding the correct Addiction requires that characters' addictions be loaded before statuses.
    public static AddictionSymptom load(JsonObject object, Character self) {
        String cause = object.get("cause").getAsString();
        if (cause == null) {
            return null;
        }
        AddictionType type = AddictionType.valueOf(object.get("type").getAsString());
        float combat = object.get("combat").getAsFloat();
        return self.getAddiction(type, cause).map(addiction -> addiction.createTrackingSymptom(combat)).orElse(null);
    }

    @Override public JsonObject saveToJson() {
        JsonObject json = new JsonObject();
        json.addProperty("cause", getCause().getType().toString()); // character type
        json.addProperty("type", source.getType().name());  // addiction type
        json.addProperty("combat", combatMagnitude);
        return json;
    }

    @Override public AddictionSymptom loadFromJson(JsonObject obj) {
        throw new RuntimeException("Not supported: Addiction loading should be handled by Addiction.loadAddictions().");
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AddictionSymptom symptom = (AddictionSymptom) o;

        if (Float.compare(symptom.combatMagnitude, combatMagnitude) != 0)
            return false;
        return source.equals(symptom.source);
    }

    @Override public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + (combatMagnitude != +0.0f ? Float.floatToIntBits(combatMagnitude) : 0);
        return result;
    }

    void applyFlags() {
        flags.forEach(getAffected()::flagStatus);
    }
}
