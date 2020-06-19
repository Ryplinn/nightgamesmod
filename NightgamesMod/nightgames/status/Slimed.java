package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.utilities.MathUtils;

public class Slimed extends DurationStatus {
    private static final int MAX_STACKS = 10;
    private CharacterType origin;
    private int stacks;

    public Slimed(CharacterType affected, CharacterType other, int stacks) {
        super("parasited", affected, 4);
        this.origin = other;
        if (getOrigin().has(Trait.EnduringAdhesive)) {
            setDuration(6);
        }
        this.stacks = stacks;
        // don't auto-remove when cleared.
        requirements.clear();
        flag(Stsflag.slimed);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    private Character getOrigin() {
        return origin.fromPoolGuaranteed();
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
    	if (replacement != null) {
    	    if (((Slimed)replacement).stacks < 0) {
                return Formatter.format("Some of the slime covering {self:direct-object} fall off {self:name-possessive} body.\n", getAffected(), getOrigin());
    	    } else {
    	        return Formatter.format("More pieces of {other:name-possessive} slime are getting stuck to {self:name-possessive} body.\n", getAffected(), getOrigin());
    	    }
    	}
        return Formatter.format("Pieces of {other:name-possessive} slime are stuck to {self:name-possessive} body!\n", getAffected(), getOrigin());
    }

    @Override
    public String describe(Combat c) {
    	if (stacks < 2) {
    		return Formatter.format("A few chunks of {other:name-possessive} slimey body is stuck on {self:direct-object}.", getAffected(), getOrigin());
    	} else if (stacks < 5) {
    		return Formatter.format("Bits and pieces of {other:name-possessive} slime are stuck on {self:name-do}.", getAffected(), getOrigin());
    	} else if (stacks < 8) {
    		return Formatter.format("It's becoming difficult to move with so much of {other:name-possessive} slime on {self:name-possessive} body.", getAffected(), getOrigin());
    	} else if (stacks < 10) {
    		return Formatter.format("It's very difficult to move with so much of {other:name-possessive} slime on {self:name-possessive} body.", getAffected(), getOrigin());
    	} else {
    		return Formatter.format("{self:SUBJECT-ACTION:are|is} covered head to toe with {other:name-possessive} slime, making it impossible to move!", getAffected(), getOrigin());
    	}
    }

    @Override
    public float fitnessModifier() {
        return -stacks;
    }

    @Override
    public int mod(Attribute a) {
    	if (a == Attribute.speed) {
    		return -stacks / 10;
    	}

        return 0;
    }

    @Override
    public void tick(Combat c) {
    	super.tick(c);
    	if (getAffected().is(Stsflag.plasticized)) {
            Formatter.writeFormattedIfCombat(c, "The slime just slides off {self:possessive} plastic-wrapped form.", getAffected(), getOrigin());
            getAffected().removeStatus(this);
            return;
    	}
        if (getDuration() <= 0) {
        	stacks = Math.max(0, stacks - 10);
        	if (stacks == 0) {
        		Formatter.writeFormattedIfCombat(c, "{self:SUBJECT-ACTION:finally shake|finally shakes} off all of {other:name-possessive} slime!", getAffected(), getOrigin());
        		getAffected().removeStatus(this);
        	} else {
	    		Formatter.writeFormattedIfCombat(c, "{self:SUBJECT-ACTION:shake|shakes} off some of {other:name-possessive} sticky slime.", getAffected(), getOrigin());
	    		// be lazy and use the same function as the constructor to set the durations
	        	setDuration((new Slimed(affected, origin, 1)).getDuration());
        	}
        }
        if (stacks >= MAX_STACKS && getOrigin().has(Trait.PetrifyingPolymers)) {
        	Formatter.writeFormattedIfCombat(c, "There's so much slime on {self:name-do} that it solidifies into a sheet of hard plastic!.", getAffected(), getOrigin());
        	stacks = 0;
        	getAffected().removeStatus(this);
        	getAffected().add(c, new Plasticized(affected));
        }
        if (stacks >= 0 && getOrigin().has(Trait.ParasiticBond)) {
            Formatter.writeFormattedIfCombat(c, "While not connected directly to {other:direct-object}, {other:name-possessive} slime seems to be eroding {self:name-possessive} stamina while energizing {other:direct-object}", getAffected(), getOrigin());
            getAffected().drain(c, getOrigin(), 2 + stacks / 4, Character.MeterType.STAMINA, Character.MeterType.MOJO, 1.0f);
        }
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
        return -stacks;
    }

    @Override
    public boolean overrides(Status s) {
        return false;
    }

    @Override
    public void replace(Status s) {
        Slimed other = (Slimed) s;
        setDuration(Math.max(other.getDuration(), getDuration()));
        stacks = MathUtils.clamp(stacks + other.stacks, 0, MAX_STACKS);
        if (stacks == 0) {
            setDuration(0);
            stacks = 0;
        }
    }

    @Override
    public int escape(Character from) {
        return -stacks / 3;
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
        return -stacks / 10;
    }

    public String toString() {
        return "Slimed";
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new Slimed(newAffected.getType(), opponent.getType(), stacks);
    }

    public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        obj.addProperty("stacks", stacks);
        return obj;
    }

    public Status loadFromJson(JsonObject obj) {
    	// TODO implement me
        return new Slimed(null, null, 0);
    }

    @Override
    public int regen(Combat c) {
        return 0;
    }
}
