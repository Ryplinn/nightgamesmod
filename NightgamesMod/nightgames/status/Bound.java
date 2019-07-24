package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.DebugFlags;
import nightgames.global.Formatter;
import nightgames.gui.GUI;
import nightgames.trap.Trap;

public class Bound extends Status {
    protected double toughness;
    protected String binding;
    protected Trap trap;

    public Bound(CharacterType affected, double dc, String binding) {
        this(affected, dc, binding, null);
    }
    public Bound(CharacterType affected, double dc, String binding, Trap trap) {
        this("Bound", affected, dc, binding, trap);
    }

    public Bound(String type, CharacterType affected, double dc, String binding, Trap trap) {
        super(type, affected);
        toughness = dc;
        this.binding = binding;
        this.trap = trap;
        flag(Stsflag.bound);
        flag(Stsflag.debuff);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s now bound by %s.\n", getAffected().subjectAction("are", "is"), binding);
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "Your hands are bound by " + binding + ".";
        } else {
            return getAffected().possessiveAdjective() + " hands are restrained by " + binding + ".";
        }
    }

    @Override
    public String getVariant() {
        return binding;
    }

    @Override
    public float fitnessModifier() {
        return (float) -(5 + Math.min(20, toughness) / 2);
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int regen(Combat c) {
        getAffected().emote(Emotion.desperate, 10);
        getAffected().emote(Emotion.nervous, 10);
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
        return -15;
    }

    @Override
    public int escape(Character from) {
        return (int) -Math.round(toughness);
    }

    @Override
    public void struggle(Character self) {
        int struggleAmount = (int) (5 + Math.sqrt((self.getLevel() + self.get(Attribute.power) + self.get(Attribute.cunning))));
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_DAMAGE)) {
            System.out.println("Struggled for " + struggleAmount);
        }
        toughness = Math.max(toughness - struggleAmount, 0);
    }

    @Override
    public boolean lingering() {
        return false;
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

    public void tick(Combat c) {
        if (c == null && trap != null) {
            if (getAffected().human()) {
                GUI.gui.message(Formatter.format("{self:SUBJECT-ACTION:are|is} still trapped by the %s.", getAffected(), NPC
                                .noneCharacter(), trap.getName().toLowerCase()));
            }
            getAffected().location().opportunity(getAffected(), trap);
        }
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new Bound(newAffected.getType(), toughness, binding, trap);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("toughness", toughness);
        obj.addProperty("binding", binding);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Bound(null, obj.get("toughness").getAsDouble(), obj.get("binding").getAsString());
    }
}
