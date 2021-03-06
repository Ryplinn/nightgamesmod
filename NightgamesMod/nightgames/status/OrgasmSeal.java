package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class OrgasmSeal extends DurationStatus {
    public OrgasmSeal(CharacterType affected, int duration) {
        super("Orgasm Sealed", affected, duration);
        flag(Stsflag.orgasmseal);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s ability to cum is now sealed!\n", getAffected().subject());
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().hasBalls()) {
            return Formatter.format("A pentagram on {self:name-possessive} ballsack glows with a sinister light.",
                            getAffected(), getAffected());
        } else {
            return Formatter.format("A pentagram on {self:name-possessive} lower belly glows with a sinister light.",
                            getAffected(), getAffected());
        }
    }

    @Override
    public float fitnessModifier() {
        if (getAffected().getArousal().percent() > 80) {
            return -10;
        }
        return 0;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        if (getAffected().getArousal().isFull()) {
            tick(4);
        }
        if (getAffected().getArousal().percent() > 80) {
            getAffected().emote(Emotion.desperate, 10);
            getAffected().emote(Emotion.horny, 10);
        }
        return 0;
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
    public String toString() {
        return "Orgasm Sealed";
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new OrgasmSeal(newAffected.getType(), getDuration());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new OrgasmSeal(null, obj.get("duration").getAsInt());
    }
}
