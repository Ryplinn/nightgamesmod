package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.DebugFlags;
import nightgames.gui.GUI;
import nightgames.pet.PetCharacter;

public class Enthralled extends DurationStatus {
    private int timesRefreshed;
    private boolean makesCynical;
    public CharacterType master;
    public Enthralled(CharacterType self, CharacterType master, int duration) {
        this(self, master, duration, duration > 1);
    }

    public Enthralled(CharacterType self, CharacterType master, int duration, boolean makesCynical) {
        super("Enthralled", self, duration);
        timesRefreshed = 0;
        this.master = master;
        if (getMaster().isPet()) {
            this.master = ((PetCharacter) getMaster()).getSelf().owner().getType();
        }
        flag(Stsflag.enthralled);
        flag(Stsflag.debuff);
        flag(Stsflag.disabling);
        flag(Stsflag.purgable);
        flag(Stsflag.mindgames);
        this.makesCynical = makesCynical;
    }

    public Character getMaster() {
        return master.fromPoolGuaranteed();
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        if (replacement != null) {
            return String.format("%s %s control of %s.\n", getMaster().subjectAction("reinforce", "reinforces"),
                            getMaster().possessiveAdjective(), getAffected().nameDirectObject());
        } else {
            return String.format("%s now enthralled by %s.\n", getAffected().subjectAction("are", "is"), getMaster().subject());
        }
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "You feel a constant pull on your mind, forcing you to obey " + getMaster().possessiveAdjective()
                            + " every command.";
        } else {
            return getAffected().subject() + " looks dazed and compliant, ready to follow "
                                +c.getOpponent(getAffected()).nameOrPossessivePronoun()+" orders.";
        }
    }

    @Override
    public String getVariant() {
        return "enthralled by " + getMaster().getTrueName();
    }

    @Override
    public boolean overrides(Status s) {
        return false;
    }

    @Override
    public void replace(Status s) {
        assert s instanceof Enthralled;
        Enthralled other = (Enthralled) s;
        setDuration(Math.max(getDuration() + 1, other.getDuration() - 2 * (timesRefreshed + 1)));
        timesRefreshed += 1;
    }

    @Override
    public float fitnessModifier() {
        return -getDuration() * 5;
    }

    @Override
    public int mod(Attribute a) {
        if (a == Attribute.perception) {
            return -5;
        }
        return -2;
    }

    @Override
    public void onRemove(Combat c, Character other) {
        if (makesCynical) {
            getAffected().addlist.add(new Cynical(affected));
        }
        if (c != null && getAffected().human()) {
            c.write(getAffected(),
                            "Everything around you suddenly seems much clearer,"
                                            + " like a lens snapped into focus. You don't really remember why"
                                            + " you were heading in the direction you were...");
        } else if (getAffected().human()) {
            GUI.gui.message("Everything around you suddenly seems much clearer,"
                            + " like a lens snapped into focus. You don't really remember why"
                            + " you were heading in the direction you were...");
        }
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        return 0;
    }

    @Override
    public void tick(Combat c) {
        if (getAffected().checkVsDc(Attribute.cunning, getMaster().get(Attribute.seduction) / 2 + getMaster().get(Attribute.spellcasting) / 2
                        + getMaster().get(Attribute.darkness) / 2 + 10 + 10 * (getDuration() - timesRefreshed))) {
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
                System.out.println("Escaped from Enthralled");
            }
            setDuration(0);
        }
        getAffected().loseMojo(c, 5, " (Enthralled)");
        getAffected().loseWillpower(c, 1, 0, false, " (Enthralled)");
        getAffected().emote(Emotion.horny, 15);
    }

    @Override
    public int damage(Combat c, int paramInt) {
        return 0;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double paramInt) {
        return paramInt / 4;
    }

    @Override
    public int weakened(Combat c, int paramInt) {
        return 0;
    }

    @Override
    public int tempted(Combat c, int paramInt) {
        return paramInt / 4;
    }

    @Override
    public int evade() {
        return -20;
    }

    @Override
    public int escape(Character from) {
        return -20;
    }

    @Override
    public int gainmojo(int paramInt) {
        return -paramInt;
    }

    @Override
    public int spendmojo(int paramInt) {
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
        return new Enthralled(newAffected.getType(), newOther.getType(), getDuration(), makesCynical);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        obj.addProperty("makesCynical", makesCynical);
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Enthralled(null, NPC.noneCharacter().getType(), obj.get("duration").getAsInt(), obj.get("makesCynical").getAsBoolean());
    }
}
