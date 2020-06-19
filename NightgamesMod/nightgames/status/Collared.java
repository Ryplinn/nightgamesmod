package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class Collared extends Status implements Compulsive {

    private int charges;
    private final CharacterType owner;
    
    public Collared(CharacterType affected, CharacterType owner) {
        super("Collared", affected);
        flag(Stsflag.collared);
        flag(Stsflag.compelled);
        charges = 10;
        this.owner = owner;
    }

    public Character getOwner() {
        return owner.fromPoolGuaranteed();
    }

    public void tick(Combat c) {
        if (charges <= 0) {
            c.write("<b>The collar around " + getAffected().nameOrPossessivePronoun()
                            + " neck runs out of power and falls off.</b>");
            getAffected().removelist.add(this);
        }
    }

    public void recharge() {
        charges += 15;
    }
    
    @Override
    public String initialMessage(Combat c, Status replacement) {
        return Formatter.format("{self:SUBJECT} now {self:action:have|has} a metallic collar around"
                        + " {self:possessive} neck!", getAffected(), c.getOpponent(getAffected()));
    }

    @Override
    public String describe(Combat c) {
        return Formatter.format("{self:SUBJECT-ACTION:are|is} wearing a training collar, which"
                        + " is hampering {self:possessive} ability to fight.", getAffected(), c.getOpponent(getAffected()));
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int regen(Combat c) {
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
        return -20;
    }

    @Override
    public boolean lingering() {
        return true;
    }
    
    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new Collared(newAffected.getType(), opponent.getType());
    }

    @Override
    public JsonObject saveToJson() {
        return null;
    }

    @Override
    public Status loadFromJson(JsonObject obj) {
        return null;
    }

    @Override
    public String describe(Combat c, Situation sit) {
        switch (sit) {
            case PREVENT_ESCAPE:
                return Formatter.format("{self:SUBJECT-ACTION:try|tries} to struggle, but"
                                + " the collar is having none of it and shocks {self:direct-object}"
                                + " into submission.", getAffected(), getOwner());
            case PUNISH_PAIN:
                return Formatter.format("The training collar around {self:name-possessive}"
                                + "neck reacts to {self:possessive} aggression by sending"
                                + " a powerful shock down {self:possessive} spine.", 
                                getAffected(), getOwner());
            case PREVENT_REMOVE_BOMB:
                return Formatter.format("{self:SUBJECT-ACTION:reach|reaches} up to grab the sphere on "
                                + "{self:possessive} chest, but the collar around {self:possessive} neck"
                + " does not appreciate the sentiment and shocks {self:direct-object} to keep your arms down.",
                    getAffected(), getOwner());
            case PREVENT_STRUGGLE:
                return Formatter.format("{self:SUBJECT-ACTION:try|tries} to struggle, but"
                                + " the collar is having none of it and shocks {self:direct-object}"
                                + " into submission.", getAffected(), getOwner());
            case STANCE_FLIP:
                return c.getStance().reverse(c, false).equals(c.getStance()) ?
                                Formatter.format("Distracted by a shock from the collar around {self:possessive}"
                                + " neck, {self:subject-action:have|has} no chance to resist as"
                                + " {other:subject-action:put|puts} {self:direct-object}"
                                + " in a pin.", getAffected(), getOwner())
                            :
                                Formatter.format("Appearantly punishing {self:name-do} for being dominant, the collar"
                                                + " around {self:possessive} neck gives {self:direct-object} a painful"
                                                + " shock. At the same time, {other:subject-action:grab|grabs}"
                                                + " hold of {self:possessive} body and gets {other:reflective}"
                                                + " into a more advantegeous position.", getAffected(), getOwner());
            case PREVENT_REVERSAL:
                return Formatter.format("{self:SUBJECT-ACTION:try|tries} to get the"
                            + " upper hand, but the collar adamantly refuses by"
                            + " shocking {self:direct-object}.", getAffected(), getOwner());
            default:
                return "ERROR: Missing compulsion type in Collared";
            
        }
    }
    
    @Override
    public void doPostCompulsion(Combat c, Situation sit) {
        int cost;
        switch (sit) {
            case PREVENT_ESCAPE:
            case PREVENT_STRUGGLE:
            case PREVENT_REVERSAL:
            case STANCE_FLIP:
                cost = 2;
                break;
            case PREVENT_REMOVE_BOMB:
            case PUNISH_PAIN:
            default:
                cost = 1;
        }
        charges = Math.max(0, charges - cost);
    }

}
