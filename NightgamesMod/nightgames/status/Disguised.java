package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.NPC;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;

public class Disguised extends Status {
    private CharacterType disguiseTarget;

    public Disguised(CharacterType affected, CharacterType disguiseTarget) {
        super("Disguised", affected);
        if (disguiseTarget == affected) {
            throw new RuntimeException("Tried to disguise as oneself!");
        }
        this.disguiseTarget = disguiseTarget;
        this.flag(Stsflag.disguised);
        this.flag(Stsflag.purgable);
    }

    private NPC getDisguiseTarget() {
        return (NPC) disguiseTarget.fromPoolGuaranteed();
    }

    @Override
    public boolean lingering() {
        return true;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return "";
    }

    @Override
    public String describe(Combat c) {
    	return "";
    }

    @Override
    public int mod(Attribute a) {
        int mod = getDisguiseTarget().getAttribute(a) - getAffected().getPure(a);
        if (getAffected().has(Trait.Masquerade)) {
            mod = mod * 3 / 2;
        }
        return mod;
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
    public Status instance(Character newAffected, Character newOther) {
        return new Disguised(newAffected.getType(), disguiseTarget);
    }

     @Override public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("disguiseTarget", disguiseTarget.toString());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Disguised(null, CharacterType.get(obj.get("disguiseTarget").getAsString()));
    }

    public NPC getTarget() {
        return getDisguiseTarget();
    }

    @Override
    public int regen(Combat c) {
        return 0;
    }
}
