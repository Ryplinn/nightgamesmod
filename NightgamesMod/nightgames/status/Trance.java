package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.skills.Masturbate;
import nightgames.skills.Piston;
import nightgames.skills.Skill;
import nightgames.skills.Thrust;

import java.util.Arrays;
import java.util.Collection;

public class Trance extends DurationStatus {
    private boolean makesCynical;

    public Trance(CharacterType affected, int duration) {
        this(affected, duration, duration > 1);
    }

    public Trance(CharacterType affected, int duration, boolean makesCynical) {
        super("Trance", affected, duration);
        flag(Stsflag.trance);
        flag(Stsflag.disabling);
        flag(Stsflag.purgable);
        flag(Stsflag.mindgames);
        this.makesCynical = makesCynical;
    }

    public Trance(CharacterType affected) {
        this(affected, 3, true);
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "You know that you should be fighting back, but it's so much easier to just surrender.";
        } else {
            return getAffected().getName() + " is flush with desire and doesn't seem interested in fighting back.";
        }
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        if (replacement != null) {
            return String.format("%s now entranced.\n", getAffected().subjectAction("are", "is"));
        } else {
            return String.format("%s already entranced.\n", getAffected().subjectAction("are", "is"));
        }
    }

    @Override
    public float fitnessModifier() {
        return -(2 + Math.min(5, getDuration()) / 2.0f);
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public void tick(Combat c) {
        getAffected().loseWillpower(c, 1, 0, false, " (Trance)");
        getAffected().emote(Emotion.horny, 15);
    }

    @Override
    public void onRemove(Combat c, Character other) {
        if (makesCynical) {
            getAffected().addlist.add(new Cynical(affected));
        }
    }

    @Override
    public boolean overrides(Status s) {
        return false;
    }

    @Override
    public Collection<Skill> allowedSkills(Combat c) {
        return Arrays.asList(new Masturbate(), new Thrust(), new Piston());
    }

    @Override
    public int damage(Combat c, int x) {
        getAffected().removelist.add(this);
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
    public Status instance(Character newAffected, Character newOther) {
        return new Trance(newAffected.getType(), this.getDuration(), this.makesCynical);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("makesCynical", makesCynical);
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Trance(null, obj.get("duration").getAsInt(), obj.get("makesCynical").getAsBoolean());
    }
}
