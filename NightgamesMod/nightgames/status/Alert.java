package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

// TODO: Alert status is unused!
public class Alert extends DurationStatus {

    /**
     * Default constructor for loading
     */
    public Alert() {
        this(NPC.noneCharacter().getType());
    }
    public Alert(CharacterType affected) {
        super("Alert", affected, 3);
        flag(Stsflag.alert);
        flag(Stsflag.purgable);
    }

    @Override
    public float fitnessModifier() {
        return 3;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s now more alert\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public String describe(Combat c) {
        return "";
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        getAffected().emote(Emotion.confident, 5);
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
        return 15;
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
        return 15;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new Alert(newAffected.getType());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Alert(null);
    }
}
