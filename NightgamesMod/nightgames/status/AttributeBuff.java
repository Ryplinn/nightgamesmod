package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.NPC;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class AttributeBuff extends DurationStatus {
    Attribute modded;
    protected int value;

    public AttributeBuff() {
        this(NPC.noneCharacter().getType(), Attribute.hypnotism, 1, 0);
    }

    public AttributeBuff(CharacterType affected, Attribute att, int value, int duration) {
        this(String.format("%s %+d", att.displayName(), value), affected, att, value, duration);
    }

    public AttributeBuff(String name, CharacterType affected, Attribute att, int value, int duration) {
        super(name, affected, duration);
        flag(Stsflag.purgable);
        if (value < 0) {
            flag(Stsflag.debuff);
        }
        this.modded = att;
        this.value = value;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        int newValue;
        if (replacement == null) {
            newValue = this.value;
        } else {
            newValue = replacement.value();
        }
        if (newValue < 0) {
            return Formatter.format("{self:pronoun-action:feel|seems} %s{self:if-human: than before}{self:if-nonhuman: now}", getAffected(), getAffected(), modded.getLowerPhrase());
        } else {
            return Formatter.format("{self:pronoun-action:feel|seems} %s{self:if-human: than before}{self:if-nonhuman: now}", getAffected(), getAffected(), modded.getRaisePhrase());
        }
    }

    @Override
    public float fitnessModifier() {
        return value / (2.0f * Math.min(1.0f, Math.max(1, getAffected().getPure(modded)) / 10.0f));
    }

    @Override
    public String describe(Combat c) {
        String person, adjective, modification;

        if (getAffected().human()) {
            person = "You feel your";
        } else {
            person = getAffected().getName() + "'s";
        }
        if (Math.abs(value) > 5) {
            adjective = "greatly";
        } else {
            adjective = "";
        }
        if (value > 0) {
            modification = "augmented.";
        } else {
            modification = "sapped.";
        }

        return String.format("%s %s is %s %s\n", person, modded, adjective, modification);
    }

    @Override
    public int mod(Attribute a) {
        if (a == modded) {
            return value;
        }
        return 0;
    }

    public Attribute getModdedAttribute() {
        return modded;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String getVariant() {
        return "ABUFF:" + modded.toString();
    }

    @Override
    public boolean overrides(Status s) {
        return false;
    }

    @Override
    public void replace(Status s) {
        assert s instanceof AttributeBuff;
        AttributeBuff other = (AttributeBuff) s;
        assert other.modded == modded;
        setDuration(Math.max(other.getDuration(), getDuration()));
        value += other.value;
        name = String.format("%s %+d", modded.displayName(), value);
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
    public int escape(Character from) {
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
    public boolean lingering() {
        return true;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new AttributeBuff(newAffected.getType(), modded, value, getDuration());
    }

    @Override
    public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("modded", modded.name());
        obj.addProperty("value", value);
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override
    public Status loadFromJson(JsonObject obj) {
        return new AttributeBuff(null, Attribute.valueOf(obj.get("modded")
                                                    .getAsString()),
                        obj.get("value")
                           .getAsInt(),
                        obj.get("duration")
                           .getAsInt());
    }
}
