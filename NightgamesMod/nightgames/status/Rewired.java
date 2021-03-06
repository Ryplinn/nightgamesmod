package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

public class Rewired extends DurationStatus {
    public Rewired(CharacterType affected, int duration) {
        super("Rewired", affected, duration);
        flag(Stsflag.rewired);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "Your senses feel... wrong. It's like your sense of pleasure and pain are jumbled.";
        } else {
            return getAffected().getName() + " fidgets uncertainly at the alien sensation of " + getAffected()
                            .possessiveAdjective() + " rewired nerves.";
        }
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s senses is now rewired.\n", getAffected().nameOrPossessivePronoun());
    }

    @Override
    public int damage(Combat c, int x) {
        return 0;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        getAffected().getStamina().reduce((int) Math.round(x));
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
        return new Rewired(newAffected.getType(), getDuration());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Rewired(null, obj.get("duration").getAsInt());
    }
}
