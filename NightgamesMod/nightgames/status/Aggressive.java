package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.NPC;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.skills.Skill;
import nightgames.skills.SkillPool;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Aggressive extends DurationStatus {

    private static final Collection<Skill> CONTACT_SKILLS = Collections.unmodifiableSet(
                    SkillPool.skillPool.stream().map(Supplier::get)
                                    .filter(Skill::makesContact).collect(Collectors.toSet()));

    private final String cause;

    /**
     * Default constructor for loading
     */
    public Aggressive() {
        this(NPC.noneCharacter().getType(), "none", 0);
    }

    public Aggressive(CharacterType affected, String cause, int duration) {
        super("Aggressive", affected, duration);
        this.cause = cause;
        flag(Stsflag.aggressive);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s now aggressive, and cannot use non-physical skills.",
                        getAffected().subjectAction("are", "is"));
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "Affected by " + cause + ", you are incapable of anything but an all-out assault.";
        }
        return String.format("%s has an aggressive look on %s face: eyes wide open, teeth bared.", getAffected().getName(),
                        getAffected().possessiveAdjective());
    }

    @Override
    public Collection<Skill> allowedSkills(Combat c) {
        Character affected = getAffected();
        Character target = c.getOpponent(affected);
        return CONTACT_SKILLS.stream().filter(
                        s -> s.requirements(c, affected, target) && Skill
                                        .skillIsUsable(c, s, affected, target)).collect(Collectors.toSet());
    }

    @Override
    public int mod(Attribute a) {
        if (a == Attribute.cunning)
            return -3;
        if (a == Attribute.power)
            return 3;
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
        return -5;
    }

    @Override
    public int escape(Character from) {
        return 5;
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
        return new Aggressive(newAffected.getType(), cause, getDuration());
    }

     @Override public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        obj.addProperty("cause", cause);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Aggressive(null, obj.get("cause").getAsString(), obj.get("duration").getAsInt());
    }
}
