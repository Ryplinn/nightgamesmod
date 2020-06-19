package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.skills.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Frenzied extends DurationStatus {

    private static final Collection<Skill> FUCK_SKILLS = new HashSet<>();

    static {
        // Skills that either lead to penetration, or can be used during it.
        Character p = NPC.noneCharacter();
        FUCK_SKILLS.add(new AssFuck());
        FUCK_SKILLS.add(new Carry());
        FUCK_SKILLS.add(new Shove());
        FUCK_SKILLS.add(new Tackle());
        FUCK_SKILLS.add(new Straddle());
        FUCK_SKILLS.add(new Tear());
        FUCK_SKILLS.add(new Undress());
        FUCK_SKILLS.add(new Fly());
        FUCK_SKILLS.add(new Fuck());
        FUCK_SKILLS.add(new Invitation());
        FUCK_SKILLS.add(new WildThrust());
        FUCK_SKILLS.add(new ReverseAssFuck());
        FUCK_SKILLS.add(new ReverseCarry());
        FUCK_SKILLS.add(new ReverseFly());
        FUCK_SKILLS.add(new ReverseFuck());
        FUCK_SKILLS.add(new SubmissiveHold());
        FUCK_SKILLS.add(new ToggleKnot());
    }

    private boolean selfInflicted;
    public Frenzied(CharacterType affected, int duration) {
        this(affected, duration, false);
    }

    public Frenzied(CharacterType affected, int duration, boolean selfInflicted) {
        super("Frenzied", affected, duration);
        flag(Stsflag.frenzied);
        if (!selfInflicted && !getAffected().has(Trait.NaturalHeat)) {
            flag(Stsflag.debuff);
            flag(Stsflag.mindgames);
        }
        flag(Stsflag.purgable);
        this.selfInflicted = selfInflicted;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        if (getAffected().has(Trait.Rut) && !getAffected().human()) {
            return Formatter.format("There's a frenzied look in {self:name-possessive} eyes as they zero in on {other:name-possessive} crotch. "
                            + "This could be bad.", getAffected(), c.getOpponent(getAffected()));
        }
        return String.format("%s mind blanks, leaving only the bestial need to breed.",
                        getAffected().nameOrPossessivePronoun());
    }

    @Override
    public String describe(Combat c) {
        String msg;
        if (getAffected().human()) {
            msg = "You cannot think about anything other than fucking all that moves.";
        } else {
            msg = String.format("%s has a frenzied look in %s eyes, interested in nothing but raw, hard sex.",
                            getAffected().getName(), getAffected().possessiveAdjective());
        }
        if (getAffected().has(Trait.PrimalHeat)) {
            msg += Formatter.format(" Somehow {self:possessive} crazed animal desperation makes {self:direct-object} seem more attractive than ever.", getAffected(), c.getOpponent(getAffected()));
        }
        return msg;
    }

    @Override
    public int mod(Attribute a) {
        if (a == Attribute.cunning) {
            return -5;
        }
        if (a == Attribute.power) {
            return 8;
        }
        if (a == Attribute.animism) {
            return 8;
        }
        return 0;
    }

    @Override
    public void onRemove(Combat c, Character other) {
        if (!selfInflicted) {
            getAffected().addlist.add(new Cynical(affected));
        }
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        getAffected().buildMojo(c, 25);
        getAffected().emote(Emotion.horny, 15);
        return 0;
    }

    @Override
    public int damage(Combat c, int x) {
        return (int) (-x * 0.2);
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return 0;
    }

    @Override
    public int weakened(Combat c, int x) {
        return (int) (-x * 0.2);
    }

    @Override
    public int tempted(Combat c, int x) {
        return (int) (x * 0.2);
    }

    @Override
    public int evade() {
        return -10;
    }

    @Override
    public int escape(Character from) {
        return -10;
    }

    @Override
    public int gainmojo(int x) {
        return (int) (x * 1.25);
    }

    @Override
    public int spendmojo(int x) {
        return 0;
    }

    @Override
    public int counter() {
        return -20;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public void tick(Combat c) {
        if (c == null) {
            getAffected().removelist.add(this);
            getAffected().removeStatusNoSideEffects();
        }
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new Frenzied(newAffected.getType(), getDuration());
    }

    @Override
    public Collection<Skill> allowedSkills(Combat c) {
        // Gather the preferred skills for which the character meets the
        // requirements
        Character affected = getAffected();
        return FUCK_SKILLS.stream().filter(s -> s.requirements(c, affected, c.getOpponent(affected)))
                        .collect(Collectors.toSet());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Frenzied(null, obj.get("duration").getAsInt());
    }
}
