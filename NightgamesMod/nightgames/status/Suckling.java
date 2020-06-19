package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.skills.Skill;
import nightgames.skills.Suckle;

import java.util.Collection;
import java.util.Collections;

public class Suckling extends DurationStatus {

    public Suckling(CharacterType affected, int duration) {
        super("Suckling", affected, duration);
        flag(Stsflag.suckling);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
        flag(Stsflag.mindgames);
    }

    @Override
    public Collection<Skill> allowedSkills(Combat c) {
        return Collections.singleton(new Suckle());
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s fighting an urge to drink from %s nipples.\n",
                        getAffected().subjectAction("are", "is"), c.getOpponent(getAffected()));
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "You feel an irresistible urge to suck on " + c.getOpponent(getAffected()).nameOrPossessivePronoun() + " nipples.";
        } else {
            return getAffected().getName() + " is looking intently at " + c.getOpponent(getAffected())
                            .nameOrPossessivePronoun() + " breasts.";
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
    public int regen(Combat c) {
        super.regen(c);
        getAffected().emote(Emotion.horny, 5);
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
    public void onRemove(Combat c, Character other) {
        getAffected().addlist.add(new Cynical(affected));
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new Suckling(newAffected.getType(), getDuration());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Suckling(null, obj.get("duration").getAsInt());
    }
}
