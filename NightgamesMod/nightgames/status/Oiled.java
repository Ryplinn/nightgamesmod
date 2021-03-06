package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class Oiled extends Status {
    public Oiled(CharacterType affected) {
        super("Oiled", affected);
        flag(Stsflag.oiled);
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "Your skin is slick with oil and kinda feels weird.";
        } else {
            return String.format("%s is shiny with lubricant, making %s more tempted to touch and rub %s skin.",
                            getAffected().subject(), c.getOpponent(getAffected()).subject(), getAffected().possessiveAdjective());
        }
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s now lubricated.\n", getAffected().subjectAction("are", "is"));
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int regen(Combat c) {
        return 0;
    }

    @Override
    public int damage(Combat c, int x) {
        return 0;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        c.write(getAffected(), Formatter.format("{self:NAME-POSSESSIVE} slippery oiled form makes {self:direct-object} all the more sensitive.", getAffected(), c.getOpponent(getAffected())));
        return x / 4;
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
        return 8;
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
        return new Oiled(newAffected.getType());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Oiled(null);
    }
}
