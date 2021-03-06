package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

public class Lovestruck extends DurationStatus {
    CharacterType other;

    public Lovestruck(CharacterType affected, CharacterType other, int duration) {
        super("Lovestruck", affected, duration);
        this.other = other;
        flag(Stsflag.lovestruck);
        flag(Stsflag.charmed);
        flag(Stsflag.purgable);
        flag(Stsflag.debuff);
        flag(Stsflag.mindgames);
    }

    Character getOther() {
        return other.fromPoolGuaranteed();
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "You feel an irresistible attraction to " + getOther().nameDirectObject() + ".";
        } else {
            return getAffected().getName() + " is looking at " + getOther().nameDirectObject()
                            + " like a lovestruck teenager.";
        }
    }

    @Override
    public float fitnessModifier() {
        return -(2 + getDuration() / 2.0f);
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public void onRemove(Combat c, Character other) {
        getAffected().addlist.add(new Cynical(affected));
    }

    @Override
    public void tick(Combat c) {
        getAffected().loseWillpower(c, 1, 0, false, " (Lovestruck)");
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
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s now lovestruck.\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public int weakened(Combat c, int x) {
        return 0;
    }

    @Override
    public int tempted(Combat c, int x) {
        return 3;
    }

    @Override
    public int evade() {
        return 0;
    }

    @Override
    public int escape(Character from) {
        return -10;
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
        return -10;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new Lovestruck(newAffected.getType(), null, getDuration());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Lovestruck(null, null, getDuration());
    }
}
