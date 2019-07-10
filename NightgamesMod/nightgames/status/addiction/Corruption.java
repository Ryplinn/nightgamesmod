package nightgames.status.addiction;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.body.*;
import nightgames.characters.body.mods.DemonicMod;
import nightgames.characters.body.mods.SizeMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.*;

import java.util.*;

public class Corruption extends Addiction {
    public Corruption(Character affected, String cause, float magnitude) {
        super(affected, "Corruption", cause, magnitude);
    }

    public Corruption(Character affected, String cause) {
        this(affected, cause, .01f);
    }

    @Override
    public void tick(Combat c) {
        super.tick(c);
        if (c == null && Random.random(100) < 66) {
            // if you aren't in combat, just apply corrupt 1/3rd of the time.
            return;
        }
        Severity sev = getCombatSeverity();
        int amt = sev.ordinal() * 2;
        if (getCause().has(Trait.Subversion) && affected.is(Stsflag.charmed)) {
            amt *= 1.5;
        }
        Map<Attribute, Integer> buffs = new HashMap<>();
        if (noMoreAttrs() || (atLeast(Severity.MED) && Random.random(100) < 5)) {
            if (!atLeast(Severity.MED)) {
                Formatter.writeIfCombat(c, affected, Formatter.format(
                                "The corruption is churning within {self:name-do}, but it seems that it's done all it can for now.", affected,
                                getCause()));
            } else if (!affected.body.has("tail") || affected.body.getRandom("tail") != TailPart.demonic) {
                Formatter.writeIfCombat(c, affected, Formatter.format( "<b>The dark taint changes {self:name-do} even further, and a spade-tipped tail bursts out of {self:possessive}"
                                + " lower back!</b>", affected, getCause()));
                affected.body.temporaryAddOrReplacePartWithType(TailPart.demonic, Random.random(15, 40));
            } else if (!affected.body.has("wings") || affected.body.getRandom("wings") != WingsPart.demonic) {
                Formatter.writeIfCombat(c, affected, Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and a set of black bat wings grows from {self:possessive} back!</b>", affected,
                                getCause()));
                affected.body.temporaryAddOrReplacePartWithType(WingsPart.demonic, Random.random(15, 40));
            } else if (affected.hasPussy() && !affected.body.getRandomPussy().moddedPartCountsAs(affected, DemonicMod.INSTANCE)) {
                Formatter.writeIfCombat(c, affected, Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and {self:possessive} pussy turns into that of a succubus!</b>", affected,
                                getCause()));
                affected.body.temporaryAddPartMod("pussy", DemonicMod.INSTANCE, Random.random(15, 40));
            } else if (affected.hasDick() && !affected.body.getRandomCock().moddedPartCountsAs(affected, CockMod.incubus)) {
                Formatter.writeIfCombat(c, affected, Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and {self:possessive} cock turns into that of an incubus!</b>", affected,
                                getCause()));
                affected.body.temporaryAddPartMod("cock", CockMod.incubus, Random.random(15, 40));
            } else if (!atLeast(Severity.HIGH)) {
                Formatter.writeIfCombat(c, affected, Formatter.format(
                                "The corruption is churning within {self:name-do}, but it seems that it's done all it can for now.", affected,
                                getCause()));
            } else if (!affected.hasPussy() && getCause().hasDick()) {
                Formatter.writeIfCombat(c, affected, Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and a succubus's pussy forms between {self:possessive} legs!</b>", affected,
                                getCause()));
                affected.body.temporaryAddOrReplacePartWithType(PussyPart.generic.applyMod(DemonicMod.INSTANCE), Random
                                .random(15, 40));
            } else if (!affected.hasDick()) {
                Formatter.writeIfCombat(c, affected, Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and an incubus's cock forms between {self:possessive} legs!</b>", affected,
                                getCause()));
                affected.body.temporaryAddOrReplacePartWithType(new CockPart().applyMod(new SizeMod(SizeMod.COCK_SIZE_BIG)).applyMod(CockMod.incubus),
                                Random.random(15, 40));
            } else if (!affected.body.getRandomAss().moddedPartCountsAs(affected, DemonicMod.INSTANCE)) {
                Formatter.writeIfCombat(c, affected, Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and {self:possessive} asshole darkens with corruption!</b>", affected,
                                getCause()));
                affected.body.temporaryAddPartMod("ass", DemonicMod.INSTANCE, Random.random(15, 40));
            } else if (!affected.body.getRandom("mouth").moddedPartCountsAs(affected, DemonicMod.INSTANCE)) {
                Formatter.writeIfCombat(c, affected, Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and {self:possessive} lush lips turns black!</b>", affected,
                                getCause()));
                affected.body.temporaryAddPartMod("mouth", DemonicMod.INSTANCE, Random.random(15, 40));
            } else {
                Formatter.writeIfCombat(c, affected, Formatter.format(
                                "The corruption is churning within {self:name-do}, but it seems that it's done all it can for now.", affected,
                                getCause()));
            }
        } else {
            for (int i = 0; i < amt; i++) {
                Optional<Attribute> att = getDrainAttr();
                if (!att.isPresent()) {
                    break;
                }
                buffs.compute(att.get(), (a, old) -> old == null ? 1 : old + 1);
            }
            switch (sev) {
                case HIGH:
                    Formatter.writeIfCombat(c, affected, Formatter.format( "The corruption is rampaging through {self:name-possessive} soul, rapidly demonizing {self:direct-object}.", affected,
                                    getCause()));
                    break;
                case MED:
                    Formatter.writeIfCombat(c, affected, Formatter.format(
                                    "The corruption is rapidly subverting {self:name-possessive} skills, putting them to a darker use...", affected,
                                    getCause()));
                    break;
                case LOW:
                    Formatter.writeIfCombat(c, affected, Formatter.format( "The corruption inside of {self:name-do} is slowly changing {self:possessive} mind...", affected,
                                    getCause()));
                    break;
                case NONE:
                    assert buffs.isEmpty();
                default:
            }
            buffs.forEach((att, b) -> affected.add(c, new Converted(affected, Attribute.darkness, att, b, 20)));
        }
        if (c != null && getCause().has(Trait.InfernalAllegiance) && !affected.is(Stsflag.compelled) && shouldCompel() && c.getOpponent(affected).equals(
                        getCause())) {
            Formatter.writeIfCombat(c, affected, Formatter.format( "A wave of obedience radiates out from the dark essence within {self:name-do}, constraining"
                            + " {self:possessive} free will. It will make fighting " 
                            + getCause().getName() + " much more difficult...", affected, getCause()));
            affected.add(c, new Compulsion(affected, getCause()));
        }
    }

    private boolean shouldCompel() {
        return getMagnitude() * 50 > Random.random(100);
    }

    private boolean noMoreAttrs() {
        return !getDrainAttr().isPresent();
    }

    private static final Set<Attribute> UNDRAINABLE_ATTS = EnumSet.of(Attribute.darkness, Attribute.speed, Attribute.perception);

    private boolean attIsDrainable(Attribute att) {
        double maxDrainFraction = 1 - getMagnitude();
        return !UNDRAINABLE_ATTS.contains(att) && affected.get(att) > Math.max(10, affected.getPure(att) * maxDrainFraction);
    }

    private Optional<Attribute> getDrainAttr() {
        Optional<AttributeBuff> darkBuff = affected.getStatusOfClass(AttributeBuff.class).stream().filter(status -> status.getModdedAttribute() == Attribute.darkness).findAny();
        if (!darkBuff.isPresent() || darkBuff.get().getValue() <  10 + getMagnitude() * 50) {
            return Random.pickRandom(Arrays.stream(Attribute.values()).filter(this::attIsDrainable).toArray(Attribute[]::new));
        }
        return Optional.empty();
    }

    @Override
    protected Optional<Status> withdrawalEffects() {
       return Optional.of(new DarkChaos(affected));
    }

    @Override
    protected Optional<Status> addictionEffects() {
        return Optional.of(this);
    }

    @Override
    protected String describeIncrease() {
        switch (getSeverity()) {
            case HIGH:
                if (affected.human()) {
                    return getCause().getName() + "'s blackness threatens to overwhelm what purity "
                                    + "remains inside of you, and it's a constant presence in {self:possessive} mind.";
                } else {
                    return getCause().getName() + "'s dark taint threatens to overwhelm what purity "
                                    + "remains inside of {self:name-do}, and you can almost feel that {self:pronoun} has almost given up fighting it.";
                }
            case LOW:
                if (affected.human()) {
                    return "The blackness " + getCause().getName() + " poured into you is still "
                                    + "there, and it feels like it's alive somehow; a churning mass of corruption and depravity.";
                } else {
                    return "The blackness " + getCause().getName() + " poured into {self:name-do} is still "
                                    + "there, and you can almost feel it inside {self:direct-object}; a churning mass of corruption and depravity.";
                }
            case MED:
                return "The corruption in {self:possessive} soul spreads further, seeping into {self:possessive} flesh and bones.";
            case NONE:
            default:
                return "";
        }
    }

    @Override
    protected String describeDecrease() {
        switch (getSeverity()) {
            case HIGH:
                if (affected.human()) {
                    return "The corruption in {self:possessive} soul is backing off, but "
                                    + "there is work to be done yet if you are to be entirely free of it. ";
                } else {
                    return "The corruption in {self:possessive} soul visibly recedes a bit, taking away some of {self:possessive} demonic attributes along with it.";
                }
            case MED:
                if (affected.human()) {
                    return "Whatever it was exactly that " + getCause().getName() + " created in you "
                                    + "has weakened somewhat and is no longer taking all of your concentration to resist it. ";
                } else {
                    return "Whatever it was exactly that " + getCause().getName() + " has tainted {self:name-do} with "
                                    + "has weakened somewhat and {self:possessive} gaze doesn't feel as dangerous as before. ";
                }
            case LOW:
                if (affected.human()) {
                    return "Whatever it was exactly that " + getCause().getName() + " created in you "
                                    + "has weakened considerably and is no longer corrupting {self:possessive} every thought. ";
                } else {
                    return "Whatever it was exactly that " + getCause().getName() + " has tainted {self:name-do} with "
                                    + "has weakened considerably and some of {self:possessive} old gentleness is showing through. ";
                }
            case NONE:
                return "The last of the infernal corruption is purified "
                + "from {self:possessive} soul, bringing {self:direct-object} back to normal. Well, as normal as {self:subject-action:are} ever going to be, anyway. ";
            default:
                return "";
        }
    }

    @Override
    protected String describeWithdrawal() {
        switch (getSeverity()) {
            case HIGH:
                return "<b>" + getCause().getName() + "'s corruption is working hard to punish you "
                                + "for not feeding it today, and it will cause all kinds of trouble tonight.</b>";
            case LOW:
                return "<b>Something is not quite right. The blackness " + getCause().getName()
                                + " put in you is stirring, causing all kinds of strange sensations. Perhaps it's hungry?</b>";
            case MED:
                return "<b>The powerful corruption within {self:name-do} is rebelling"
                                + " against not being fed today. Expect the unexpected tonight.</b>";
            case NONE:
            default:
                return "";
        }
    }

    @Override
    protected String describeCombatIncrease() {
        return ""; // Combat messages handled in tick
    }

    @Override
    protected String describeCombatDecrease() {
        return ""; // Combat messages handled in tick
    }

    @Override
    public String describeMorning() {
        return "Something is churning inside of you this morning. It feels both wonderful and disgusting"
                        + " at the same time. You think you hear an echo of a whisper as you go about {self:possessive}"
                        + " daily routine, pushing you to evil acts.";
    }

    @Override
    public AddictionType getType() {
        return AddictionType.CORRUPTION;
    }

    @Override
    public String initialMessage(Combat c, Optional<Status> replacement) {
        if (inWithdrawal) {
            return "The blackness resonates with " + getCause().getName() + ", growing even more powerful and troublesome than before.";
        }
        return "The blackness " + getCause().getName() + " places in you resonates with " + getCause().directObject() + ". You can"
                        + " feel it starting to corrupt " + affected.possessiveAdjective() + " mind and body!";
    }

    @Override
    public String describe(Combat c) {
        if (affected.human()) {
            return "";
        } else {
            switch (getSeverity()) {
                case HIGH:
                    return Formatter.format("<b>{self:SUBJECT-ACTION:have} been almost completely demonized by " + getCause()
                                                    .nameOrPossessivePronoun() + " demonic influence. "
                                    + "{self:POSSESSIVE} bright eyes have been replaced by ruby-like irises that seem to stare into your very soul. You better finish this one fast!</b>", affected,
                                    getCause());
                case MED:
                    return Formatter.format("<b>{self:SUBJECT-ACTION:have} been visibly changed by demonic corruption. "
                                    + "Black lines run along {self:possessive} body where it hadn't before and there's a hungry look in {self:possessive} eyes that "
                                    + "disturbs you almost as much as it turns you on.</b>", affected, getCause());
                case LOW:
                    return Formatter.format("<b>{self:SUBJECT-ACTION:look} a bit strange. While you can't quite put your finger on it, something about {self:direct-object} feels a bit off to you. "
                                    + "Probably best not too worry about it too much.</b>", affected, getCause());
                case NONE:
                default:
                    return "";
            }
        }
    }

    @Override
    public int mod(Attribute a) {
        return a == Attribute.darkness ? 5 : 0;
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
        return new Corruption((Character)newAffected, newOther.getType(), magnitude);
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Corruption(NPC.noneCharacter(), obj.get("cause").getAsString(),
                        (float) obj.get("magnitude").getAsInt());
    }

    @Override
    public String informantsOverview() {
        return "Dude. Not cool. I like " + getCause().getName() + " shaking " + getCause().directObject() + " evil ass around at night as much"
                        + " as the next guy, but the evil should stay there, you know? Now, the"
                        + " rest of the competitors will not appreciate {self:possessive} new attitude either."
                        + " I don't see them jumping to {self:possessive} defence any time soon. You should also"
                        + " worry about this thing inside of you taking over the uncorrupted parts of"
                        + " your mind. Also, I would imagine that that evil part of you won't appreciate"
                        + " any efforts to get rid of it. Who knows what chaos it might cause? Of course,"
                        + " if it's the Dark skills you're interested in, then it's probably a good thing."
                        + " But you're not like that, are you?";
    }

}
