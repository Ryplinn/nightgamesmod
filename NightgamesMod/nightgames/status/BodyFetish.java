package nightgames.status;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.google.gson.JsonObject;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.skills.Anilingus;
import nightgames.skills.Blowjob;
import nightgames.skills.BreastWorship;
import nightgames.skills.CockWorship;
import nightgames.skills.FootWorship;
import nightgames.skills.Grind;
import nightgames.skills.Invitation;
import nightgames.skills.Piston;
import nightgames.skills.PussyWorship;
import nightgames.skills.ReverseAssFuck;
import nightgames.skills.ReverseCarry;
import nightgames.skills.ReverseFly;
import nightgames.skills.Skill;
import nightgames.skills.SpiralThrust;
import nightgames.skills.Thrust;
import nightgames.skills.WildThrust;

public class BodyFetish extends DurationStatus {
    private CharacterType origin;
    public String part;
    public double magnitude;

    public BodyFetish(CharacterType affected, CharacterType origin, String part, double magnitude) {
        super(Formatter.capitalizeFirstLetter(part) + " Fetish", affected, 10);
        flag(Stsflag.bodyfetish);
        this.origin = origin;
        this.part = part;
        this.magnitude = magnitude;
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    private Character getOrigin() {
        return origin.fromPoolGuaranteed();
    }

    @Override
    public boolean lingering() {
        return true;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        if (replacement != null) {
            return String.format("%s %s fetish has grown.\n", getAffected().nameOrPossessivePronoun(), part);
        } else {
            return String.format("%s now affected by a %s fetish.\n", getAffected().subjectAction("are", "is"), part);
        }
    }

    @Override
    public String describe(Combat c) {
        String desc;
        if (magnitude < .26) {
            desc = "slight ";
        } else if (magnitude < .51) {
            desc = "";
        } else if (magnitude < .99) {
            desc = "fierce ";
        } else {
            desc = "overwhelming ";
        }
        String magString = Formatter.formatDecimal(magnitude);
        if (getAffected().human()) {
            if (origin != null && c != null && c.getOpponent(getAffected()).equals(getOrigin())) {
                return Formatter.capitalizeFirstLetter(
                                desc + "fantasies of worshipping " + getOrigin().nameOrPossessivePronoun() + " " + part
                                                + " run through your mind (" + magString + ").");
            } else {
                return Formatter.capitalizeFirstLetter(desc + "fantasies of worshipping " + part
                                + " run through your mind (" + magString + ").");
            }
        } else {
            return getAffected().getName() + " is affected by " + desc + part + " fetish (" + magString + ").";
        }
    }

    @Override
    public Collection<Skill> allowedSkills(Combat c) {
        if (magnitude <= .99) {
            return Collections.emptySet();
        } else if (part.equals("pussy")) {
            return Collections.singletonList(new PussyWorship(affected));
        } else if (part.equals("breasts")) {
            return Collections.singletonList(new BreastWorship(affected));
        } else if (part.equals("feet")) {
            return Collections.singletonList(new FootWorship(affected));
        } else if (part.equals("ass")) {
            return Collections.singletonList(new Anilingus(affected));
        } else if (part.equals("cock")) {
            return Arrays.asList(new Blowjob(affected), new ReverseAssFuck(affected), new ReverseFly(affected),
                            new ReverseCarry(affected), new Invitation(affected), new Thrust(affected),
                            new Piston(affected), new Grind(affected), new SpiralThrust(affected),
                            new CockWorship(affected), new WildThrust(affected));
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public float fitnessModifier() {
        return -(float) magnitude * 3;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public boolean overrides(Status s) {
        return false;
    }

    @Override
    public void replace(Status s) {
        assert s instanceof BodyFetish;
        BodyFetish other = (BodyFetish) s;
        assert other.part.equals(part);
        magnitude = Math.min(3.0, magnitude + other.magnitude);
        this.setDuration(Math.max(getDuration(), other.getDuration()));
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
        return 0;
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new BodyFetish(newAffected.getType(), newOther.getType(), part, magnitude);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("part", part);
        obj.addProperty("magnitude", magnitude);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new BodyFetish(null, null, obj.get("part").getAsString(), obj.get("magnitude").getAsFloat());
    }

    @Override
    public int regen(Combat c) {
        if (magnitude > .25) {
            magnitude = Math.max(.25, magnitude - .01);
        }
        return 0;
    }
}
