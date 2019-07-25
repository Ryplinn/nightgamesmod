package nightgames.status.addiction;

import nightgames.characters.Attribute;
import nightgames.characters.CharacterType;
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
    public Corruption(CharacterType afflicted, CharacterType cause, float magnitude) {
        super("Corruption", afflicted, cause, magnitude);
    }

    public Corruption(CharacterType afflicted, CharacterType cause) {
        this(afflicted, cause, .01f);
    }

    private class CorruptionTrackingSymptom extends AddictionSymptom {
        CorruptionTrackingSymptom(Corruption source, float initialMagnitude) {
            super(afflicted, "Corrupting Seed", source, initialMagnitude);
        }

        @Override public void tick(Combat c) {
            super.tick(c);
            ((Corruption) source).tick(c);
        }

        @Override public String describe(Combat c) {
            // Unlike other addictions, corruptions' effects aren't particular dependent on combat state.
            return source.describe(c, source.getSeverity());
        }

        @Override
        public int mod(Attribute a) {
            return a == Attribute.darkness ? 5 : 0;
        }
    }


    public void tick(Combat c) {
        if (c == null && Random.random(100) < 66) {
            // if you aren't in combat, just apply corrupt 1/3rd of the time.
            return;
        }
        Addiction.Severity sev = getSeverity();
        int amt = sev.ordinal() * 2;
        if (getCause().has(Trait.Subversion) && getAfflicted().is(Stsflag.charmed)) {
            amt *= 1.5;
        }
        Map<Attribute, Integer> buffs = new HashMap<>();
        if (noMoreAttrs() || (this.atLeast(Severity.MED) && Random.random(100) < 5)) {
            if (!this.atLeast(Severity.MED)) {
                Formatter.writeIfCombat(c, getAfflicted(), Formatter.format(
                                "The corruption is churning within {self:name-do}, but it seems that it's done all it can for now.", getAfflicted(),
                                getCause()));
            } else if (!getAfflicted().body.has("tail") || getAfflicted().body.getRandom("tail") != TailPart.demonic) {
                Formatter.writeIfCombat(c, getAfflicted(), Formatter.format( "<b>The dark taint changes {self:name-do} even further, and a spade-tipped tail bursts out of {self:possessive}"
                                + " lower back!</b>", getAfflicted(), getCause()));
                getAfflicted().body.temporaryAddOrReplacePartWithType(TailPart.demonic, Random.random(15, 40));
            } else if (!getAfflicted().body.has("wings") || getAfflicted().body.getRandom("wings") != WingsPart.demonic) {
                Formatter.writeIfCombat(c, getAfflicted(), Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and a set of black bat wings grows from {self:possessive} back!</b>", getAfflicted(),
                                getCause()));
                getAfflicted().body.temporaryAddOrReplacePartWithType(WingsPart.demonic, Random.random(15, 40));
            } else if (getAfflicted().hasPussy() && !getAfflicted().body.getRandomPussy().moddedPartCountsAs(getAfflicted(), DemonicMod.INSTANCE)) {
                Formatter.writeIfCombat(c, getAfflicted(), Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and {self:possessive} pussy turns into that of a succubus!</b>", getAfflicted(),
                                getCause()));
                getAfflicted().body.temporaryAddPartMod("pussy", DemonicMod.INSTANCE, Random.random(15, 40));
            } else if (getAfflicted().hasDick() && !getAfflicted().body.getRandomCock().moddedPartCountsAs(getAfflicted(), CockMod.incubus)) {
                Formatter.writeIfCombat(c, getAfflicted(), Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and {self:possessive} cock turns into that of an incubus!</b>", getAfflicted(),
                                getCause()));
                getAfflicted().body.temporaryAddPartMod("cock", CockMod.incubus, Random.random(15, 40));
            } else if (!this.atLeast(Severity.HIGH)) {
                Formatter.writeIfCombat(c, getAfflicted(), Formatter.format(
                                "The corruption is churning within {self:name-do}, but it seems that it's done all it can for now.", getAfflicted(),
                                getCause()));
            } else if (!getAfflicted().hasPussy() && getCause().hasDick()) {
                Formatter.writeIfCombat(c, getAfflicted(), Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and a succubus's pussy forms between {self:possessive} legs!</b>", getAfflicted(),
                                getCause()));
                getAfflicted().body.temporaryAddOrReplacePartWithType(PussyPart.generic.applyMod(DemonicMod.INSTANCE), Random
                                .random(15, 40));
            } else if (!getAfflicted().hasDick()) {
                Formatter.writeIfCombat(c, getAfflicted(), Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and an incubus's cock forms between {self:possessive} legs!</b>", getAfflicted(),
                                getCause()));
                getAfflicted().body.temporaryAddOrReplacePartWithType(new CockPart().applyMod(new SizeMod(SizeMod.COCK_SIZE_BIG)).applyMod(CockMod.incubus),
                                Random.random(15, 40));
            } else if (!getAfflicted().body.getRandomAss().moddedPartCountsAs(getAfflicted(), DemonicMod.INSTANCE)) {
                Formatter.writeIfCombat(c, getAfflicted(), Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and {self:possessive} asshole darkens with corruption!</b>", getAfflicted(),
                                getCause()));
                getAfflicted().body.temporaryAddPartMod("ass", DemonicMod.INSTANCE, Random.random(15, 40));
            } else if (!getAfflicted().body.getRandom("mouth").moddedPartCountsAs(getAfflicted(), DemonicMod.INSTANCE)) {
                Formatter.writeIfCombat(c, getAfflicted(), Formatter.format(
                                "<b>The dark taint changes {self:name-do} even further, and {self:possessive} lush lips turns black!</b>", getAfflicted(),
                                getCause()));
                getAfflicted().body.temporaryAddPartMod("mouth", DemonicMod.INSTANCE, Random.random(15, 40));
            } else {
                Formatter.writeIfCombat(c, getAfflicted(), Formatter.format(
                                "The corruption is churning within {self:name-do}, but it seems that it's done all it can for now.", getAfflicted(),
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
                    Formatter.writeIfCombat(c, getAfflicted(), Formatter.format( "The corruption is rampaging through {self:name-possessive} soul, rapidly demonizing {self:direct-object}.", getAfflicted(),
                                    getCause()));
                    break;
                case MED:
                    Formatter.writeIfCombat(c, getAfflicted(), Formatter.format(
                                    "The corruption is rapidly subverting {self:name-possessive} skills, putting them to a darker use...", getAfflicted(),
                                    getCause()));
                    break;
                case LOW:
                    Formatter.writeIfCombat(c, getAfflicted(), Formatter.format( "The corruption inside of {self:name-do} is slowly changing {self:possessive} mind...", getAfflicted(),
                                    getCause()));
                    break;
                case NONE:
                    assert buffs.isEmpty();
                default:
            }
            buffs.forEach((att, b) -> getAfflicted().add(c, new Converted(afflicted, Attribute.darkness, att, b, 20)));
        }
        if (c != null && getCause().has(Trait.InfernalAllegiance) && !getAfflicted().is(Stsflag.compelled) && shouldCompel() && c.getOpponent(getAfflicted()).equals(
                        getCause())) {
            Formatter.writeIfCombat(c, getAfflicted(), Formatter.format( "A wave of obedience radiates out from the dark essence within {self:name-do}, constraining"
                            + " {self:possessive} free will. It will make fighting "
                            + getCause().getName() + " much more difficult...", getAfflicted(), getCause()));
            getAfflicted().add(c, new Compulsion(afflicted, cause));
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
        return !UNDRAINABLE_ATTS.contains(att) && getAfflicted().getAttribute(att) > Math.max(10, getAfflicted().getPure(att) * maxDrainFraction);
    }

    private Optional<Attribute> getDrainAttr() {
        Optional<AttributeBuff> darkBuff = getAfflicted().getStatusOfClass(AttributeBuff.class).stream().filter(status -> status.getModdedAttribute() == Attribute.darkness).findAny();
        if (!darkBuff.isPresent() || darkBuff.get().getValue() <  10 + getMagnitude() * 50) {
            return Random.pickRandom(Arrays.stream(Attribute.values()).filter(this::attIsDrainable).toArray(Attribute[]::new));
        }
        return Optional.empty();
    }

    @Override public Optional<Status> withdrawalEffects() {
       return Optional.of(new DarkChaos(afflicted));
    }

    @Override public String describeIncrease() {
        switch (getSeverity()) {
            case HIGH:
                if (getAfflicted().human()) {
                    return getCause().getName() + "'s blackness threatens to overwhelm what purity "
                                    + "remains inside of you, and it's a constant presence in {self:possessive} mind.";
                } else {
                    return getCause().getName() + "'s dark taint threatens to overwhelm what purity "
                                    + "remains inside of {self:name-do}, and you can almost feel that {self:pronoun} has almost given up fighting it.";
                }
            case LOW:
                if (getAfflicted().human()) {
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

    @Override public String describeDecrease() {
        switch (getSeverity()) {
            case HIGH:
                if (getAfflicted().human()) {
                    return "The corruption in {self:possessive} soul is backing off, but "
                                    + "there is work to be done yet if you are to be entirely free of it. ";
                } else {
                    return "The corruption in {self:possessive} soul visibly recedes a bit, taking away some of {self:possessive} demonic attributes along with it.";
                }
            case MED:
                if (getAfflicted().human()) {
                    return "Whatever it was exactly that " + getCause().getName() + " created in you "
                                    + "has weakened somewhat and is no longer taking all of your concentration to resist it. ";
                } else {
                    return "Whatever it was exactly that " + getCause().getName() + " has tainted {self:name-do} with "
                                    + "has weakened somewhat and {self:possessive} gaze doesn't feel as dangerous as before. ";
                }
            case LOW:
                if (getAfflicted().human()) {
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

    @Override public String describeWithdrawal() {
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

    @Override public String describeCombatIncrease() {
        return ""; // Combat messages handled in tick
    }

    @Override public String describeCombatDecrease() {
        return ""; // Combat messages handled in tick
    }

    @Override
    public String describeMorning() {
        return "Something is churning inside of you this morning. It feels both wonderful and disgusting"
                        + " at the same time. You think you hear an echo of a whisper as you go about {self:possessive}"
                        + " daily routine, pushing you to evil acts.";
    }

    @Override public AddictionSymptom createTrackingSymptom(float initialCombatMagnitude) {
        return new CorruptionTrackingSymptom(this, initialCombatMagnitude);
    }

    @Override
    public AddictionType getType() {
        return AddictionType.CORRUPTION;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        if (inWithdrawal) {
            return "The blackness resonates with " + getCause().getName() + ", growing even more powerful and troublesome than before.";
        }
        return "The blackness " + getCause().getName() + " places in you resonates with " + getCause().directObject() + ". You can"
                        + " feel it starting to corrupt " + getAfflicted().possessiveAdjective() + " mind and body!";
    }

    @Override
    public String describe(Combat c, Severity severity) {
        if (getAfflicted().human()) {
            return "";
        } else {
            switch (severity) {
                case HIGH:
                    return Formatter.format("<b>{self:SUBJECT-ACTION:have} been almost completely demonized by " + getCause()
                                                    .nameOrPossessivePronoun() + " demonic influence. "
                                    + "{self:POSSESSIVE} bright eyes have been replaced by ruby-like irises that seem to stare into your very soul. You better finish this one fast!</b>", getAfflicted(),
                                    getCause());
                case MED:
                    return Formatter.format("<b>{self:SUBJECT-ACTION:have} been visibly changed by demonic corruption. "
                                    + "Black lines run along {self:possessive} body where it hadn't before and there's a hungry look in {self:possessive} eyes that "
                                    + "disturbs you almost as much as it turns you on.</b>", getAfflicted(), getCause());
                case LOW:
                    return Formatter.format("<b>{self:SUBJECT-ACTION:look} a bit strange. While you can't quite put your finger on it, something about {self:direct-object} feels a bit off to you. "
                                    + "Probably best not too worry about it too much.</b>", getAfflicted(), getCause());
                case NONE:
                default:
                    return "";
            }
        }
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
