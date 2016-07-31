package nightgames.status;

import com.google.gson.JsonObject;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

public class CockBound extends Status {
    private float toughness;
    public String binding;

    public CockBound(Character affected, float dc, String binding) {
        super("Cock Bound", affected);
        toughness = dc;
        this.binding = binding;
        flag(Stsflag.cockbound);
    }

    @Override
    public String describe(Combat c) {
        if (affected.human()) {
            return "Your dick is bound by " + binding + ".";
        } else {
            return "Her girl-cock is restrained by " + binding + ".";
        }
    }

    @Override
    public float fitnessModifier() {
        return -toughness / 10.0f;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int regen(Combat c) {
        if (!c.getStance().inserted(affected)) {
            affected.removelist.add(this);
        }
        affected.emote(Emotion.desperate, 10);
        affected.emote(Emotion.nervous, 10);
        affected.emote(Emotion.horny, 20);
        toughness -= 1;
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
    public String initialMessage(Combat c, boolean replaced) {
        return String.format("%s cock is now bound by %s.\n", affected.nameOrPossessivePronoun(), binding);
    }

    @Override
    public int weakened(int x) {
        return 0;
    }

    @Override
    public int tempted(int x) {
        return 0;
    }

    @Override
    public int evade() {
        return -15;
    }

    @Override
    public int escape() {
        float dc = toughness;
        return Math.round(dc * 10);
    }

    @Override
    public void struggle(Character self) {
        toughness = Math.round(toughness * .5f);
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
    public String toString() {
        return "Bound by " + binding;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new CockBound(newAffected, toughness, binding);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("toughness", toughness);
        obj.addProperty("binding", binding);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new CockBound(null, obj.get("toughness").getAsFloat(), obj.get("binding").getAsString());
    }
}
