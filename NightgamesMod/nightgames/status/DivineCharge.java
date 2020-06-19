package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

public class DivineCharge extends Status {
    public double magnitude;

    public DivineCharge(CharacterType affected, double magnitude) {
        super("Divine Energy", affected);
        flag(Stsflag.divinecharge);
        flag(Stsflag.purgable);
        this.magnitude = magnitude;
    }

    private String getPart(Combat c) {
        boolean penetrated = c.getStance()
                              .vaginallyPenetrated(c, getAffected());
        boolean inserted = c.getStance()
                            .inserted(getAffected());
        String part = "body";
        if (penetrated && !inserted) {
            part = "pussy";
        }
        if (!penetrated && inserted) {
            part = "cock";
        }
        if (!penetrated && !inserted && getAffected().has(Trait.zealinspiring)) {
            part = "pussy";
        }
        return part;
    }

    @Override
    public void tick(Combat c) {
        if (c != null) {
            Character opponent = c.getOpponent(getAffected());
            if (!c.getStance().havingSex(c, getAffected()) && !(getAffected().has(Trait.zealinspiring)
                            && !opponent.getAddiction(AddictionType.ZEAL, affected).map(Addiction::isInWithdrawal).orElse(false))) {
                magnitude = magnitude / 2;
                c.write(getAffected(), "The holy energy seeps out of " + getAffected().nameDirectObject() + ".");
                if (magnitude < .05f)
                    getAffected().removelist.add(this);
            }
        }
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        if (replacement != null) {
            return String.format("%s concentrating divine energy in %s %s.\n", getAffected().subjectAction("are", "is"),
                            getAffected().possessiveAdjective(), getPart(c));
        }
        return "";
    }

    @Override
    public void onApply(Combat c, Character other) {
        getAffected().usedAttribute(Attribute.divinity, c, .25);
    }

    @Override
    public String describe(Combat c) {
        return "Concentrated divine energy surges through " + getAffected().nameOrPossessivePronoun() + " " + getPart(c)
                        + " (" + Formatter.formatDecimal(magnitude) + ").";
    }

    @Override
    public float fitnessModifier() {
        return (float) (3 * magnitude);
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
        assert s instanceof DivineCharge;
        DivineCharge other = (DivineCharge) s;
        magnitude = magnitude + other.magnitude;
        // every 10 divinity past 10, you are allowed to add another stack of
        // divine charge.
        // this will get out of hand super quick, but eh, you shouldn't let it
        // get
        // that far.
        double maximum = Math.max(2, Math.pow(2., getAffected().getAttribute(Attribute.divinity) / 5.0) * .25);
        this.magnitude = Math.min(maximum, this.magnitude);

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
    public Status instance(Character newAffected, Character opponent) {
        return new DivineCharge(newAffected.getType(), magnitude);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("magnitude", magnitude);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new DivineCharge(null, obj.get("magnitude").getAsFloat());
    }

    @Override
    public int regen(Combat c) {
        return 0;
    }
}
