package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;

public class Feral extends Status {
    public Feral(CharacterType affected) {
        super("Feral", affected);
        flag(Stsflag.feral);
        flag(Stsflag.purgable);
    }

    @Override
    public String describe(Combat c) {
        return String.format("%s seems beyond reason in %s feral lust.\n",
                        Formatter.capitalizeFirstLetter(getAffected().subject()), getAffected().possessiveAdjective());
    }

    @Override
    public float fitnessModifier() {
        return 4;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s turned feral.\n", getAffected().subjectAction("have", "has"));
    }

    @Override
    public int mod(Attribute a) {
        switch (a) {
            case power:
                return 1 + getAffected().getPure(Attribute.animism) / 2;
            case cunning:
                return 3;
            case seduction:
            case speed:
                return 2;
            case animism:
                return getAffected().getPure(Attribute.animism) / 2;
            default:
                break;
        }
        return 0;
    }

    @Override
    public int regen(Combat c) {
        if (getAffected().getArousal().percent() < 40) {
            getAffected().removelist.add(this);
        }
        int ignoreOrgasmChance = Math.max(3, 8 - getAffected().getAttribute(Attribute.animism) / 20);
        if (Random.random(ignoreOrgasmChance) == 0) {
            getAffected().addlist.add(new IgnoreOrgasm(affected, 0));
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
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new Feral(newAffected.getType());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Feral(null);
    }
}
