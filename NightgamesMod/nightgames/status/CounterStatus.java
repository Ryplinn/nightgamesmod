package nightgames.status;

import com.google.gson.JsonObject;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.skills.CounterBase;

public class CounterStatus extends DurationStatus {
    private CounterBase skill;
    private String descriptionFormat;

    public CounterStatus(CharacterType affected, CounterBase skill, String descriptionFormat, int duration) {
        super("Counter", affected, duration);
        this.skill = skill;
        this.descriptionFormat = descriptionFormat;
        flag(Stsflag.counter);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s ready for a counter.\n", getAffected().subjectAction("get", "gets"));
    }

    @Override
    public String describe(Combat c) {
        return Formatter.format(descriptionFormat, getAffected(), c.getOpponent(getAffected()));
    }

    @Override
    public float fitnessModifier() {
        return .5f;
    }

    @Override
    public int mod(Attribute a) {
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
    public int escape() {
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
        return -100;
    }

    @Override
    public int value() {
        return 0;
    }

    public void resolveSkill(Combat c, Character target) {
        getAffected().removelist.add(this);
        skill.resolveCounter(c, target);
    }

    public CounterBase getCounterSkill() {
        return skill;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new CounterStatus(newAffected.getType(), skill, descriptionFormat, getDuration());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        // TODO Support this once skill loading is in the game
        obj.addProperty("type", getClass().getSimpleName());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        // TODO Support this once skill loading is in the game
        throw new UnsupportedOperationException();
    }
}
