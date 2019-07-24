package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.skills.TailSuck;
import nightgames.skills.damage.DamageType;

import java.util.Map;
import java.util.stream.Collectors;

public class TailSucked extends Status implements InsertedStatus {

    private CharacterType sucker;
    private int power;

    public TailSucked(CharacterType affected, CharacterType sucker, int power) {
        super("Tail Sucked", affected);
        this.sucker = sucker;
        this.power = power;
        requirements.add((c, self, other) -> c != null && self != null && other != null
                        && new TailSuck().usable(c, other, self));
        flag(Stsflag.bound);
        flag(Stsflag.debuff);
        flag(Stsflag.tailsucked);
    }

    private Character getSucker() {
        return sucker.fromPoolGuaranteed();
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return String.format("%s tail is sucking %s energy straight from %s %s.", getSucker().nameOrPossessivePronoun(),
                        getAffected().nameOrPossessivePronoun(), getAffected().possessiveAdjective(),
                        getAffected().body.getRandomCock().describe(getAffected()));
    }

    @Override
    public String describe(Combat c) {
        if (!getAffected().hasDick()) {
            getAffected().removelist.add(this);
            return "";
        }
        return String.format("%s tail keeps churning around %s " + "%s, sucking in %s vital energies.",
                        getSucker().nameOrPossessivePronoun(), getAffected().nameOrPossessivePronoun(),
                        getAffected().body.getRandomCock().describe(getAffected()), getAffected().possessiveAdjective());
    }

    @Override
    public void tick(Combat c) {
        BodyPart cock = getAffected().body.getRandomCock();
        BodyPart tail = getSucker().body.getRandom("tail");
        if (cock == null || tail == null || c == null) {
            getAffected().removelist.add(this);
            return;
        }

        c.write(getSucker(), String.format("%s tail sucks powerfully, and %s" + " some of %s strength being drawn in.",
                        getSucker().nameOrPossessivePronoun(), getAffected().subjectAction("feel", "feels"),
                        getAffected().possessiveAdjective()));

        Attribute toDrain = Random.pickRandomGuaranteed(
                        getAffected().att.entrySet().stream().filter(e -> e.getValue() != 0).map(Map.Entry::getKey)
                                        .collect(Collectors.toList()));

        Drained.drain(c, getSucker(), getAffected(), toDrain, power, 20, true);
        getAffected().drain(c, getSucker(), (int) DamageType.drain.modifyDamage(getSucker(), getAffected(), 10), Character.MeterType.STAMINA);
        getAffected().drain(c, getSucker(), 1 + Random.random(power * 3), Character.MeterType.MOJO);
    }

    @Override
    public float fitnessModifier() {
        return -4.0f;
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
    public int weakened(Combat c, int x) {
        return 0;
    }

    @Override
    public int tempted(Combat c, int x) {
        return 0;
    }

    @Override
    public int evade() {
        return power * -5;
    }

    @Override
    public int escape(Character from) {
        return power * -5;
    }

    @Override
    public int gainmojo(int x) {
        return (int) (x * 0.2);
    }

    @Override
    public int spendmojo(int x) {
        return (int) (x * 1.2);
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
        return new TailSucked(newAffected.getType(), newOther.getType(), power);
    }

    @Override public JsonObject saveToJson() {
        return null;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return null;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public BodyPart getHolePart() {
        return getSucker().body.getRandom("tail");
    }

    @Override
    public Character getReceiver() {
        return getSucker();
    }

    @Override
    public BodyPart getStickPart() {
        return getAffected().body.getRandomCock();
    }

    @Override
    public Character getPitcher() {
        return getAffected();
    }
}
