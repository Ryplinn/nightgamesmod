package nightgames.characters.body;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterSex;
import nightgames.characters.CharacterType;
import nightgames.characters.body.mods.PartMod;
import nightgames.characters.body.mods.SizeMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.DebugFlags;
import nightgames.global.Flag;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUIColor;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.json.JsonUtils;
import nightgames.nskills.tags.SkillTag;
import nightgames.pet.PetCharacter;
import nightgames.skills.Divide;
import nightgames.skills.Skill;
import nightgames.status.AttributeBuff;
import nightgames.status.BodyFetish;
import nightgames.status.Status;
import nightgames.status.Stsflag;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Body implements Cloneable {
    static class PartReplacement {
        public Set<BodyPart> added;
        public Set<BodyPart> removed;
        public int duration;

        PartReplacement(int duration) {
            added = new LinkedHashSet<>(2);
            removed = new LinkedHashSet<>(2);
            this.duration = duration;
        }

        PartReplacement(PartReplacement original) {
            added = new LinkedHashSet<>(original.added);
            removed = new LinkedHashSet<>(original.removed);
            duration = original.duration;
        }
    }
    static class PartModReplacement {
        private String type;
        private PartMod mod;
        private int duration;

        PartModReplacement(String type, PartMod mod, int duration) {
            this.mod = mod;
            this.type = type;
            this.duration = duration;
        }

        PartModReplacement(PartModReplacement rep) {
            this.mod = rep.mod;
            this.type = rep.type;
            this.duration = rep.duration;
        }

        public PartMod getMod() {
            return mod;
        }
        public String getType() {
            return type;
        }
        public int getDuration() {
            return duration;
        }
    }

    // yeah i know :(
    public static BodyPart nonePart = new GenericBodyPart("none", 0, 1, 1, "none", "");
    private static Set<String> pluralParts = new HashSet<>(Arrays.asList("hands", "feet", "wings", "breasts", "balls"));
    private final static BodyPart[] requiredParts = {new GenericBodyPart("hands", 0, 1, 1, "hands", ""),
                    new GenericBodyPart("feet", 0, 1, 1, "feet", ""), new GenericBodyPart("skin", 0, 1, 1, "skin", ""),
                    AssPart.generateGeneric().applyMod(new SizeMod(SizeMod.ASS_SIZE_NORMAL)), new MouthPart(), new BreastsPart().applyMod(new SizeMod(0)), EarPart.normal};
    private final static String[] fetishParts = {"ass", "feet", "cock", "wings", "tail", "tentacles", "breasts"};

    private LinkedHashSet<BodyPart> bodyParts;
    public double hotness;
    private transient Collection<PartReplacement> replacements;
    private transient Map<String, List<PartModReplacement>> modReplacements;
    private transient Collection<BodyPart> currentParts;
    transient public CharacterType character;
    public double baseFemininity;
    private double height;

    public Body() {
        bodyParts = new LinkedHashSet<>();
        currentParts = ConcurrentHashMap.newKeySet();
        replacements = new ArrayList<>();
        modReplacements = new HashMap<>();
        hotness = 1.0;
        height = 170;
    }

    public Body(CharacterType character) {
        this(character, 1);
    }

    public Body(CharacterType character, double hotness) {
        this();
        this.character = character;
        this.hotness = hotness;
    }

    public Character getCharacter() {
        return character.fromPoolGuaranteed();
    }

    public Collection<BodyPart> getCurrentParts() {
        return currentParts;
    }

    public List<BodyPart> getCurrentPartsThatMatch(Predicate<BodyPart> filterPredicate){
        return currentParts.stream().filter(filterPredicate).collect(Collectors.toList());
    }

    private void updateCurrentParts() {
        LinkedHashSet<BodyPart> parts = new LinkedHashSet<>(bodyParts);
        for (PartReplacement r : replacements) {
            parts.removeAll(r.removed);
            parts.addAll(r.added);
        }
        currentParts.clear();
        for (BodyPart part : parts) {
            if (modReplacements.containsKey(part.getType()) && part instanceof GenericBodyPart) {
                GenericBodyPart genericPart = (GenericBodyPart) part;
                for (PartModReplacement replacement : modReplacements.get(part.getType())) {
                    genericPart = genericPart.applyMod(replacement.getMod());
                }
                currentParts.add(genericPart);
            } else {
                currentParts.add(part);
            }
        }
    }

    public void temporaryAddPart(BodyPart part, int duration) {
        PartReplacement replacement = new PartReplacement(duration);
        replacement.added.add(part);
        replacements.add(replacement);
        updateCurrentParts();
        if (character != null) {
            updateCharacter();
        }
    }

    public void temporaryRemovePart(BodyPart part, int duration) {
        PartReplacement replacement = new PartReplacement(duration);
        replacement.removed.add(part);
        replacements.add(replacement);
        updateCurrentParts();
        if (character != null) {
            updateCharacter();
        }
    }

    public void temporaryAddOrReplacePartWithType(BodyPart part, int duration) {
        temporaryAddOrReplacePartWithType(part, getRandom(part.getType()), duration);
    }

    private BodyPart getPartIn(String type, Collection<BodyPart> parts) {
        for (BodyPart p : parts) {
            if (p.isType(type)) {
                return p;
            }
        }
        return null;
    }

    public boolean temporaryAddOrReplacePartWithType(BodyPart part, BodyPart removed, int duration) {
        PartReplacement replacement = null;
        if (removed != null)
            for (PartReplacement r : replacements) {
                BodyPart other;
                if (r.added.contains(removed)) {
                    other = removed;
                } else {
                    other = getPartIn(removed.getType(), r.added);
                }
                if (other != null) {
                    replacement = r;
                    r.added.remove(other);
                    r.added.add(part);
                    replacement.duration = Math.max(duration, replacement.duration);
                    break;
                }
            }
        if (replacement == null) {
            replacement = new PartReplacement(duration);
            replacement.removed.add(removed);
            replacement.added.add(part);
            replacements.add(replacement);
        }
        updateCurrentParts();
        if (character != null) {
            updateCharacter();
        }
        return true;
    }

    public void describe(StringBuilder b, Character other, String delimiter) {
        describe(b, other, delimiter, true);
    }

    public void describe(StringBuilder b, Character other, String delimiter, boolean hideInvisible) {
        List<BodyPart> sortedParts = new ArrayList<>(getCurrentParts());
        sortedParts.sort(SORTER);
        for (BodyPart part : sortedParts) {
            if ((!hideInvisible || part.isVisible(getCharacter())) && part.isNotable()) {
                int prevLength = b.length();
                part.describeLong(b, getCharacter());
                if (prevLength != b.length()) {
                    b.append(delimiter);
                }
            }
        }
        b.append(formatHotnessText(other));
    }

    private String formatHotnessText(Character other) {
        double hotness = getHotness(other);
        String message;
        int topLayer = Optional.ofNullable(getCharacter().getOutfit().getTopOfSlot(ClothingSlot.top)).map(Clothing::getLayer).orElse(-1);
        int bottomLayer = Optional.ofNullable(getCharacter().getOutfit().getTopOfSlot(ClothingSlot.bottom)).map(Clothing::getLayer).orElse(-1);

        String bodyString;
        String startString = "Overall, ";
        // TODO: Check this logic
        if (topLayer >= 2 && bottomLayer >= 2) {
            bodyString = "clothed form";
            startString = "Even though much of it is hidden away, ";
        } else if (topLayer < 0 && bottomLayer < 0){
            startString = "Nude and on full display, ";
            bodyString = "naked body";
        } else if (topLayer <= 1 && topLayer >= 0) {
            if (bottomLayer <= 1) {
                bodyString = "underwear-clad body";
            } else {
                bodyString = "shirtless body";
            }
        } else if (bottomLayer == 1) {
            if (topLayer <= 1) {
                bodyString = "underwear-clad body";
            } else {
                bodyString = "bare-legged body";
            }
        } else if (bottomLayer >= 0 && topLayer >= 0) {
            bodyString = "half-clothed figure";
        } else {
            bodyString = "half-naked figure";
        }

        GUIColor hotnessColor;
        if (hotness > 3.2) {
            message = "%1$s{self:possessive} %2$s is <font color=%3$s>absolute perfection</font>, "
                            + "as if perfectly sculpted by a divine hand.";
            hotnessColor = GUIColor.HOTNESS_PERFECT;
        } else if (hotness > 2.6){
            message = "%1$s{self:possessive} %2$s is <font color=%3$s>exquisitely beautiful</font>. "
                            + "There aren't many like {self:direct-object} in the world.";
            hotnessColor = GUIColor.HOTNESS_EXQUISITE;
        } else if (hotness > 2.1){
            message = "%1$s{self:pronoun-action:have|has} a <font color=%3$s>{self:if-female:lovely}{self:if-male:handsome} %2$s</font>"
                            + "{other:if-human: that definitely ignites a fire between your legs}.";
            hotnessColor = GUIColor.HOTNESS_LOVELY;
        } else if (hotness > 1.6){
            message = "%1$s{self:possessive} %2$s is <font color=%3$s>quite attractive</font>, "
                            + "although not particularly outstanding in any regard.";
            hotnessColor = GUIColor.HOTNESS_ATTRACTIVE;
        } else if (hotness > 1.0) {
            message = "%1$s{self:possessive} %2$s is <font color=%3$s>so-so</font>. "
                            + "{self:PRONOUN} would blend in with all the other {self:guy}s on campus.";
            hotnessColor = GUIColor.HOTNESS_SOSO;
        } else {
            message = "%1$s{self:possessive} %2$s is <font color=%3$s>not very attractive</font>... "
                            + "Hopefully {self:pronoun} can make up for it in technique.";
            hotnessColor = GUIColor.HOTNESS_NOT;
        }
        if (Flag.checkFlag(Flag.systemMessages)) {
            message += String.format(" (%.01f)", hotness);
        }
        return Formatter.format(message, getCharacter(), other, startString, bodyString, hotnessColor.rgbHTML());
    }
    private static final BodyPartSorter SORTER = new BodyPartSorter();
    public void describeBodyText(StringBuilder b, Character other, boolean notableOnly) {
        b.append(Formatter.format("{self:POSSESSIVE} body has ", getCharacter(), null));
        BodyPart previous = null;
        List<BodyPart> sortedParts = new ArrayList<>(getCurrentParts());
        sortedParts.sort(SORTER);
        for (BodyPart part : sortedParts) {
            if (!notableOnly || part.isNotable()) {
                if (previous != null) {
                    b.append(Formatter.prependPrefix(previous.prefix(), previous.fullDescribe(getCharacter())));
                    b.append(", ");
                }
                previous = part;
            }
        }
        if (previous == null) {
            b.append("nothing notable.<br/>");
        } else {
            b.append("and ");
            b.append(Formatter.prependPrefix(previous.prefix(), previous.fullDescribe(getCharacter())));
            b.append(".<br/>");
        }
        b.append(formatHotnessText(other));
    }

    public void add(BodyPart part) {
        assert part != null;
        bodyParts.add(part);
        updateCurrentParts();
        updateCharacter();
    }

    private void updateCharacter() {
        if (character != null) {
            getCharacter().update();
        }
    }

    public boolean contains(BodyPart part) {
        return getCurrentParts().contains(part);
    }

    public List<BodyPart> get(String type) {
        return currentParts.stream()
                           .filter(p -> p.isType(type))
                           .collect(Collectors.toList());
    }

    public List<BodyPart> getPure(String type) {
        return bodyParts.stream()
                           .filter(p -> p.isType(type))
                           .collect(Collectors.toList());
    }

    public PussyPart getRandomPussy() {
        return (PussyPart) getRandom("pussy");

    }

    public WingsPart getRandomWings() {
        return (WingsPart) getRandom("wings");
    }

    public AssPart getRandomAss() {
        return (AssPart) getRandom("ass");
    }

    public BreastsPart getRandomBreasts() {
        return (BreastsPart) getRandom("breasts");
    }

    public BreastsPart getLargestBreasts() {
        List<BodyPart> parts = get("breasts");
        BreastsPart breasts = BreastsPart.flat;
        for (BodyPart part : parts) {
            BreastsPart b = (BreastsPart) part;
            if (b.getSize() > breasts.getSize()) {
                breasts = b;
            }
        }
        return breasts;
    }

    public CockPart getCockBelow(double size) {
        List<BodyPart> parts = get("cock");
        List<CockPart> upgradable = new ArrayList<>();
        for (BodyPart part : parts) {
            CockPart cock = (CockPart) part;
            if (cock.getSize() < size) {
                upgradable.add(cock);
            }
        }
        if (upgradable.size() == 0) {
            return null;
        }

        return upgradable.get(Random.random(upgradable.size()));
    }

    public CockPart getCockAbove(double size) {
        List<BodyPart> parts = get("cock");
        List<CockPart> upgradable = new ArrayList<>();
        for (BodyPart part : parts) {
            CockPart b = (CockPart) part;
            if (b.getSize() > size) {
                upgradable.add(b);
            }
        }
        if (upgradable.size() == 0) {
            return null;
        }

        return upgradable.get(Random.random(upgradable.size()));
    }

    public BreastsPart getBreastsBelow(double size) {
        List<BodyPart> parts = get("breasts");
        List<BreastsPart> upgradable = new ArrayList<>();
        for (BodyPart part : parts) {
            BreastsPart b = (BreastsPart) part;
            if (b.getSize() < size) {
                upgradable.add(b);
            }
        }
        if (upgradable.size() == 0) {
            return null;
        }

        return upgradable.get(Random.random(upgradable.size()));
    }

    public BreastsPart getBreastsAbove(double size) {
        List<BodyPart> parts = get("breasts");
        List<BreastsPart> upgradable = new ArrayList<>();
        for (BodyPart part : parts) {
            BreastsPart b = (BreastsPart) part;
            if (b.getSize() > size) {
                upgradable.add(b);
            }
        }
        if (upgradable.size() == 0) {
            return null;
        }

        return upgradable.get(Random.random(upgradable.size()));
    }

    public Optional<BodyFetish> getFetish(String part) {
        Optional<Status> fs = getCharacter().status.stream().filter(status -> {
                                                  if (status.flags().contains(Stsflag.bodyfetish)) {
                                                      BodyFetish fetish = (BodyFetish) status;
                                                      return fetish.part.equalsIgnoreCase(part);
                                                  }
                                                  return false;
                                              }).findFirst();
        return fs.map(status -> (BodyFetish) status);
    }

    /**
     * How hot your opponent thinks your body is.
     * @param opponent The character whose preferences are used.
     * @return The composite hotness of this body.
     */
    public double getHotness(Character opponent) {
        // represents tempt damage
        double bodyHotness = hotness;
        for (BodyPart part : getCurrentParts()) {
            bodyHotness += part.getHotness(getCharacter(), opponent) * (getFetish(part.getType()).isPresent() ? 2 : 1);
        }
        double clothingHotness = getCharacter().getOutfit().getHotness();
        double totalHotness = bodyHotness * (.5 + getCharacter().getExposure()) + clothingHotness;
        if (getCharacter().is(Stsflag.glamour)) {
            totalHotness += 2.0;
        }
        if (getCharacter().is(Stsflag.alluring)) {
            totalHotness *= 1.5;
        }
        if (getCharacter().has(Trait.attractive)) {
            totalHotness *= 1.25;
        }
        if (getCharacter().has(Trait.unpleasant)) {
            totalHotness *= .75;
        }
        if (getCharacter().has(Trait.PinkHaze) && opponent.is(Stsflag.charmed)) {
            totalHotness = Math.max(totalHotness * 1.5, totalHotness + 3.0);
        }
        return totalHotness;
    }

    public double getHotness() {
        // If no one else is around, only you can judge yourself.
        return getHotness(getCharacter());
    }

    public void remove(BodyPart part) {
        bodyParts.remove(part);

        updateCurrentParts();
        if (character != null) {
            updateCharacter();
        }
    }

    public void removeOne(String type) {
        BodyPart removed = null;
        for (BodyPart part : bodyParts) {
            if (part.isType(type)) {
                removed = part;
                break;
            }
        }
        if (removed != null) {
            bodyParts.remove(removed);
            updateCurrentParts();
            if (character != null) {
                updateCharacter();
            }
        }
    }

    // returns how many are removed
    public int removeAll(String type) {
        List<BodyPart> removed = new ArrayList<>();
        for (BodyPart part : bodyParts) {
            assert part != null;
            if (part.isType(type)) {
                removed.add(part);
            }
        }
        for (BodyPart part : removed) {
            bodyParts.remove(part);
        }
        updateCurrentParts();

        if (character != null) {
            updateCharacter();
        }
        return removed.size();
    }

    public void removeTemporaryParts(String type) {
        replacements.removeIf(rep -> rep.added.stream()
                                              .anyMatch(part -> part.getType()
                                                                    .equals(type)));
        updateCurrentParts();
    }

    public CockPart getRandomCock() {
        return (CockPart) getRandom("cock");
    }
    
    public List<BodyPart> getAllGenitals() {
        List<String> partTypes = Arrays.asList("cock", "pussy", "strapon", "ass");
        return getCurrentPartsThatMatch(part -> partTypes.contains(part.getType()));
    }

    public BodyPart getRandomInsertable() {
        BodyPart part = getRandomCock();
        if (part == null && getCharacter().has(Trait.strapped)) {
            part = StraponPart.generic;
        }
        return part;
    }

    public boolean has(String type) {
        return get(type).size() > 0;
    }

    public BodyPart getRandom(String type) {
        List<BodyPart> parts = get(type);
        BodyPart part = null;
        if (parts.size() > 0) {
            part = parts.get(Random.random(parts.size()));
        }
        return part;
    }

    public int pleasure(Character opponent, BodyPart with, BodyPart target, double magnitude, Combat c) {
        return pleasure(opponent, with, target, magnitude, 0, c, false, null);
    }

    public int pleasure(Character opponent, BodyPart with, BodyPart target, double magnitude, Combat c, Skill skill) {
        return pleasure(opponent, with, target, magnitude, 0, c, false, skill);
    }

    public int pleasure(Character opponent, BodyPart with, BodyPart target, double magnitude, int bonus, Combat c,
                    boolean sub, Skill skill) {
        if (target == null) {
            target = nonePart;
        }
        if (with == null) {
            with = nonePart;
        }
        if (target.getType()
                  .equals("strapon")) {
            return 0;
        }

        double sensitivity = target.getSensitivity(opponent, with);
        if (getCharacter().has(Trait.desensitized)) {
            sensitivity -= .5;
        }
        if (getCharacter().has(Trait.desensitized2)) {
            sensitivity -= .5;
        }
        if (target.isErogenous() && getCharacter().has(Trait.hairtrigger)) {
            sensitivity += 1;
        }

        final double moddedSensitivity = sensitivity;
        sensitivity += getCharacter().status.stream()
                                       .mapToDouble(status -> status.sensitivity(moddedSensitivity))
                                       .sum();

        double pleasure = 1;
        if (!with.isType("none")) {
            pleasure = with.getPleasure(opponent, target);
        }
        double perceptionBonus = 1.0;
        if (opponent != null) {
            perceptionBonus *= 1 + (opponent.body.getCharismaBonus(c, getCharacter()) - 1) / 2;
        }
        double baseBonusDamage = bonus;
        if (opponent != null) {
            baseBonusDamage += with.applyBonuses(opponent, getCharacter(), target, magnitude, c);
            baseBonusDamage += target.applyReceiveBonuses(getCharacter(), opponent, with, magnitude, c);
            if (!sub) {
                for (BodyPart p : opponent.body.getCurrentParts()) {
                    baseBonusDamage += p.applySubBonuses(opponent, getCharacter(), with, target, magnitude, c);
                }
            }
            // double the base damage if the opponent is submissive and in a
            // submissive stance
            if (c.getStance().sub(opponent) && opponent.has(Trait.submissive) && target.isErogenous()) {
                baseBonusDamage += baseBonusDamage + magnitude;
            } else if (c.getStance().dom(opponent) && opponent.has(Trait.submissive) && !opponent.has(Trait.flexibleRole) && target.isErogenous()) {
                baseBonusDamage -= (baseBonusDamage + magnitude) * 1. / 3.;
            }
        }

        if (getCharacter().has(Trait.NaturalHeat) && getCharacter().is(Stsflag.frenzied)) {
            baseBonusDamage -= (baseBonusDamage + magnitude) / 2;
        }

        Optional<BodyFetish> fetish = getFetish(with.getType());
        if (fetish.isPresent()) {
            perceptionBonus += fetish.get().magnitude * 3;
            if (opponent != null) {
                getCharacter().add(c, new BodyFetish(character, opponent.getType(), with.getType(), .05));
            }
        }
        double base = baseBonusDamage + magnitude;

        // use the status bonus damage as part of the multiplier instead of adding to the base.
        double statusBonusDamage = 0;
        for (Status s : getCharacter().status) {
            statusBonusDamage += s.pleasure(c, with, target, base);
        }

        if (base > 0) {
            double statusMultiplier = (base + statusBonusDamage) / base;
            sensitivity += statusMultiplier - 1;
        }

        boolean unsatisfied = false;
        if (getCharacter().has(Trait.Unsatisfied)
                        && (getCharacter().getArousal().percent() >= 50)
                        && (skill == null || !skill.getTags(c).contains(SkillTag.fucking))
                        && !(with.isGenital() && target.isGenital() && c.getStance().havingSex(c))) {
            if (c != null && c.getOpponent(getCharacter()).human()) {
                pleasure -= 4;
            } else {
                pleasure -= .8;
            }
            unsatisfied = true;
        }

        double dominance = 0.0;
        if (c != null && opponent != null && getCharacter().checkAddiction(AddictionType.DOMINANCE, opponent) && c
                        .getStance().dom(opponent)) {
            float mag = getCharacter().getAddiction(AddictionType.DOMINANCE, opponent).map(Addiction::getMagnitude)
                            .orElse(0f);
            float dom = c.getStance().getDominanceOfStance(opponent);
            dominance = mag * (dom / 5.0);
        }
        perceptionBonus += dominance;

        double multiplier = Math.max(0, 1 + ((sensitivity - 1) + (pleasure - 1) + (perceptionBonus - 1)));
        double staleness = 1.0;
        double stageMultiplier = 0.0;
        boolean staleMove = false;
        if (skill != null) {
            if (c != null && skill.getSelf() != null && c.getCombatantData(skill.getSelf()) != null) {
                staleness = c.getCombatantData(skill.getSelf()).getMoveModifier(skill);
            }
            if (staleness <= .51) {
                staleMove = true;
            }
            stageMultiplier = skill.getStage().multiplierFor(getCharacter());
        }
        multiplier = Math.max(0, multiplier + stageMultiplier) * staleness;

        double damage = base * multiplier;
        double perceptionlessDamage = base * (multiplier - (perceptionBonus - 1));

        int result = (int) Math.round(damage);
        if (getCharacter().is(Stsflag.rewired)) {
            getCharacter().pain(c, opponent, result, false, false);
            return 0;
        }
        if (opponent != null) {
            String pleasuredBy = opponent.nameOrPossessivePronoun() + " " + with.describe(opponent);
            if (with == nonePart) {
                pleasuredBy = opponent.subject();
            }
            GUIColor firstColor = GUIColor.characterColor(getCharacter());
            GUIColor secondColor = GUIColor.characterColor(opponent);
            String bonusString = baseBonusDamage > 0 ?
                            String.format(" + <font color=%s>%.1f</font>", GUIColor.AROUSAL_BONUS.rgbHTML(),
                                            baseBonusDamage) :
                            baseBonusDamage < 0 ?
                                            String.format(" + <font color=%s>%.1f</font>",
                                                            GUIColor.AROUSAL_MALUS.rgbHTML(), baseBonusDamage) :
                                            "";
            String stageString = skill == null ? "" : String.format(" + stage:%.2f", skill.multiplierForStage(getCharacter()));
            String dominanceString = dominance < 0.01 ? "" : String.format(" + dominance:%.2f", dominance);
            String staleString = staleness < .99 ? String.format(" x staleness: %.2f", staleness) : "";
            String battleString =
                            String.format("<font color=%s>%s %s</font> was pleasured by <font color=%s>%s</font> for <font color=%s>%d</font> "
                                                            + "base:%.1f (%.1f%s) x multiplier: %.2f (1 + sen:%.1f + ple:%.1f + per:%.1f %s %s)%s\n",
                                            firstColor.rgbHTML(),
                                            Formatter.capitalizeFirstLetter(getCharacter().nameOrPossessivePronoun()),
                                            target.describe(getCharacter()), secondColor.rgbHTML(), pleasuredBy,
                                            GUIColor.AROUSAL_GAIN.rgbHTML(), result, base, magnitude, bonusString,
                                            multiplier, sensitivity - 1, pleasure - 1, perceptionBonus - 1, stageString,
                                            dominanceString, staleString);
            c.writeSystemMessage(battleString);
            Optional<BodyFetish> otherFetish = opponent.body.getFetish(target.getType());
            if (otherFetish.isPresent() && otherFetish.get().magnitude > .3 && perceptionlessDamage > 0 && skill != null
                            && skill.self.equals(character) && opponent.getType() != character && opponent.canRespond()) {
                c.write(getCharacter(), Formatter.format("Playing with {other:possessive} {other:body-part:%s} arouses {self:direct-object} almost as much as {other:direct-object}.", opponent, getCharacter(), target.getType()));
                opponent.temptNoSkill(c, getCharacter(), target, (int) Math.round(perceptionlessDamage * (otherFetish.get().magnitude - .2)));
            }
        } else {
            GUIColor firstColor = GUIColor.characterColor(getCharacter());
            String bonusString = baseBonusDamage > 0
                            ? String.format(" + <font color=%s>%.1f</font>", GUIColor.AROUSAL_BONUS, baseBonusDamage)
                            : "";
            String battleString =
                            String.format("<font color=%s>%s %s</font> was pleasured for <font color=%s>%d</font> "
                                                            + "base:%.1f (%.2f%s) x multiplier: %.2f (sen:%.1f + ple:%.1f + per:%.1f)\n",
                                            firstColor.rgbHTML(),
                                            Formatter.capitalizeFirstLetter(getCharacter().nameOrPossessivePronoun()),
                                            target.describe(getCharacter()), GUIColor.AROUSAL_GAIN.rgbHTML(), result, base,
                                            magnitude, bonusString, multiplier, sensitivity - 1, pleasure - 1,
                                            perceptionBonus - 1);
            if (c != null) {
                c.writeSystemMessage(battleString);
            }
        }
        if (unsatisfied) {
            if (c != null) {
                c.write(getCharacter(), Formatter.format("Foreplay doesn't seem to do it for {self:name-do} anymore. {self:PRONOUN-ACTION:clearly need|clearly needs} to fuck!", getCharacter(), opponent));
            }
        }
        if (staleMove && skill.getUser().human()) {
            c.write(opponent, Formatter.format("This seems to be a getting bit boring for {other:direct-object}... Maybe it's time to switch it up?", opponent, getCharacter()));
        }
        double percentPleasure = 100.0 * result / getCharacter().getArousal().max();
        if (getCharacter().has(Trait.sexualDynamo) && percentPleasure >= 5 && Random.random(4) == 0) {
            if (c != null) {
                c.write(getCharacter(), Formatter
                                .format("Sexual pleasure seems only to feed {self:name-possessive} ", getCharacter(), opponent));
            }
            getCharacter().buildMojo(c, (int)Math.floor(percentPleasure));
        }
        if (opponent != null && getCharacter().has(Trait.showmanship) && percentPleasure >= 5 && opponent.isPet()
                        && ((PetCharacter) opponent).getSelf().owner().equals(getCharacter())) {
            Character voyeur = c.getOpponent(getCharacter());
            c.write(getCharacter(), Formatter.format(
                            "{self:NAME-POSSESSIVE} moans as {other:subject-action:make|makes} a show of pleasing {other:possessive} {self:master} "
                                            + "turns %s on immensely.", getCharacter(), opponent,
                            voyeur.nameDirectObject()));
            voyeur.temptWithSkill(c, getCharacter(), null, Math.max(Random.random(14, 20), result / 3), skill);
        }

        getCharacter().resolvePleasure(result, c, opponent, target, with);

        if (opponent != null && Arrays.asList(fetishParts)
                                      .contains(with.getType())) {
            if (opponent.has(Trait.fetishTrainer)
                            && Random.random(100) < Math.min(opponent.get(Attribute.fetishism), 25)) {
                c.write(getCharacter(), getCharacter().subjectAction("now have", "now has") + " a new fetish, courtesy of "
                                + opponent.directObject() + ".");
                getCharacter().add(c, new BodyFetish(character, opponent.getType(), with.getType(), .25));
            }
        }
        return result;
    }

    private static Map<Integer, Double> SEDUCTION_DIMINISHING_RETURNS_CURVE = new HashMap<>();
    static {
        SEDUCTION_DIMINISHING_RETURNS_CURVE.put(0, .06); // 0.6
        SEDUCTION_DIMINISHING_RETURNS_CURVE.put(1, .05); // 1.1
        SEDUCTION_DIMINISHING_RETURNS_CURVE.put(2, .04); // 1.5
        SEDUCTION_DIMINISHING_RETURNS_CURVE.put(3, .03); // 1.8
        SEDUCTION_DIMINISHING_RETURNS_CURVE.put(4, .02); // 2.1
    }

    /**
     * Gets how much your opponent views this body. 
     */
    public double getCharismaBonus(Combat c, Character opponent) {
        // you don't get turned on by yourself
        if (opponent.equals(getCharacter())) {
            return 1.0;
        } else {
            double effectiveSeduction = getCharacter().get(Attribute.seduction);
            if (c.getStance().dom(getCharacter()) && getCharacter().has(Trait.brutesCharisma)) {
                effectiveSeduction += c.getStance().getDominanceOfStance(getCharacter()) * (getCharacter().get(Attribute.power) / 5.0 + getCharacter().get(Attribute.ki) / 5.0);
            }

            if (getCharacter().has(Trait.PrimalHeat) && getCharacter().is(Stsflag.frenzied)) {
                effectiveSeduction += getCharacter().get(Attribute.animism) / 2;
            }

            if (opponent.has(Trait.MindlessDesire) && getCharacter().is(Stsflag.frenzied)) {
                effectiveSeduction /= 2;
            }

            int seductionDiff = (int) Math.max(0, effectiveSeduction - opponent.get(Attribute.seduction));
            double seductionBonus = 0;
            for (int i = 0; i < seductionDiff; i++) {
                seductionBonus += SEDUCTION_DIMINISHING_RETURNS_CURVE.getOrDefault((i / 10), 0.01);
            }
            double hotness = (getHotness(opponent) - 1) / 2 + 1;
            double perception = (1.0 + (opponent.get(Attribute.perception) - 5) / 10.0);
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_DAMAGE) && DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILLS_RATING)) {
                System.out.println(String.format("Seduction Bonus: %.1f, hotness: %.1f, perception: %.1f", seductionBonus, hotness, perception));
            }
            double perceptionBonus = (hotness + seductionBonus) * perception;

            if (opponent.is(Stsflag.lovestruck)) {
                perceptionBonus += 1;
            }
            if (getCharacter().has(Trait.romantic)) {
                perceptionBonus += Math.max(0, opponent.getArousal().percent() - 70) / 100.0;
            }

            if (getCharacter().has(Trait.MindlessClone)) {
                perceptionBonus /= 3;
            }
            return perceptionBonus;
        }
    }

    public void addReplace(BodyPart part, int max) {
        int n = Math.min(Math.max(1, removeAll(part.getType())), max);
        for (int i = 0; i < n; i++) {
            add(part);
        }
    }

    public double getFemininity() {
        double femininity = baseFemininity;
        femininity += SizeMod.getMaximumSize("breasts") / ((double) BreastsPart.maximumSize().getSize());
        femininity += getCurrentParts().stream()
                                       .mapToDouble(part -> part.getFemininity(getCharacter()))
                                       .sum();
        return femininity;
    }

    public void finishBody(CharacterSex sex) {
        switch (sex) {
            case female:
                baseFemininity += 2;
                if (!has("face")) {
                    add(new FacePart(0, 2));
                }
                if (get("breasts").size() == 0) {
                    add(BreastsPart.b);
                }
                if (get("ass").size() == 0) {
                    add(AssPart.generateGeneric().upgrade().upgrade());
                }
                break;
            case male:
                baseFemininity -= 2;
                if (!has("face")) {
                    add(new FacePart(0, -2));
                }
                break;
            case trap:
                baseFemininity += 2;
                if (!has("face")) {
                    add(new FacePart(0, 2));
                }
                if (get("ass").size() == 0) {
                    add(AssPart.generateGeneric().upgrade());
                }
                break;
            case herm:
                baseFemininity += 1;
                if (!has("face")) {
                    add(new FacePart(0, 1));
                }
                if (get("breasts").size() == 0) {
                    add(BreastsPart.b);
                }
                if (get("ass").size() == 0) {
                    add(AssPart.generateGeneric().upgrade().upgrade());
                }
                break;
            case shemale:
                baseFemininity += 1;
                if (!has("face")) {
                    add(new FacePart(0, 1));
                }
                if (get("breasts").size() == 0) {
                    add(BreastsPart.d);
                }
                if (get("ass").size() == 0) {
                    add(AssPart.generateGeneric().upgrade().upgrade());
                }
                break;
            case asexual:
                baseFemininity += 0;
                if (!has("face")) {
                    add(new FacePart(0, 0));
                }
                break;
            default:
                break;
        }
        for (BodyPart part : requiredParts) {
            if (!has(part.getType())) {
                add(part);
            }
        }
    }

    private void replacePussyWithCock() {
        PussyPart pussy = getRandomPussy();
        removeAll("pussy");
        add(pussy == null ? CockPart.generic : pussy.getEquivalentCock());
    }

    private void replaceCockWithPussy() {
        CockPart cock = getRandomCock();
        removeAll("cock");
        add(cock == null ? PussyPart.generic : cock.getEquivalentPussy());
    }

    private void addEquivalentCockAndPussy() {
        boolean hasPussy = getRandomPussy() != null;
        boolean hasCock = getRandomCock() != null;
        if (!hasPussy) {
            CockPart cock = getRandomCock();
            add(cock == null ? PussyPart.generic : cock.getEquivalentPussy());
        }
        if (!hasCock) {
            PussyPart pussy = getRandomPussy();
            add(pussy == null ? CockPart.generic : pussy.getEquivalentCock());
        }
    }

    private void addBallsIfNeeded() {
        if (getRandom("balls") == null) {
            add(new GenericBodyPart("balls", 0, 1.0, 1.5, "balls", ""));
        }
    }

    private void growBreastsUpTo(BreastsPart part) {
        if (SizeMod.getMaximumSize("breasts") < part.getSize()) {
            addReplace(part, 1);
        }
    }

    /**
     * Guesses the character sex based on the current attributes.
     * I'm sorry if I whatever you want to be considered, you're free to add it yourself.
     */
    public CharacterSex guessCharacterSex() {
        if (getRandomCock() != null && getRandomPussy() != null) {
            return CharacterSex.herm;
        } else if (getRandomCock() == null && getRandomPussy() == null) {
            return CharacterSex.asexual;
        } else if (getRandomCock() == null && getRandomPussy() != null) {
            return CharacterSex.female;
        } else {
            if (SizeMod.getMaximumSize("breasts") > BreastsPart.a.getSize() && getFace().getFemininity(getCharacter()) > 0) {
                return CharacterSex.shemale;
            } else if (getFace().getFemininity(getCharacter()) >= 1) {
                return CharacterSex.trap;
            }
            return CharacterSex.male;
        }
    }
    
    public void temporaryAddPartMod(String partType, PartMod mod, int duration) {
        modReplacements.computeIfAbsent(partType, type -> new ArrayList<>());
        modReplacements.get(partType).add(new PartModReplacement(partType, mod, duration));
        updateCurrentParts();
        if (character != null) {
            updateCharacter();
        }
    }

    public void autoTG() {
        CharacterSex currentSex = guessCharacterSex();
        if (currentSex == CharacterSex.herm || currentSex == CharacterSex.asexual) {
            // no TG for herms or asexuals
            return;
        }
        if (getCharacter().useFemalePronouns() && Flag.checkFlag(Flag.femaleTGIntoHerm)) {
            changeSex(CharacterSex.herm);
            return;
        }
        if (currentSex == CharacterSex.female) {
            changeSex(CharacterSex.male);
            return;
        }
        if (currentSex == CharacterSex.male || currentSex == CharacterSex.shemale || currentSex == CharacterSex.trap) {
            changeSex(CharacterSex.female);
        }
    }
    
    private void changeSex(CharacterSex newSex) {
        FacePart face = ((FacePart)getRandom("face"));
        double femininity = face.getFemininity(getCharacter());
        switch (newSex) {
            case male:
                femininity = Math.min(0, femininity);
                replacePussyWithCock();
                addBallsIfNeeded();
                addReplace(BreastsPart.flat, 1);
                break;
            case female:
                femininity = Math.max(2, femininity);
                replaceCockWithPussy();
                growBreastsUpTo(BreastsPart.c);
                break;
            case herm:
                femininity = Math.max(1, femininity);
                addEquivalentCockAndPussy();
                growBreastsUpTo(BreastsPart.b);
                break;
            case shemale:
                femininity = Math.max(1, femininity);
                replacePussyWithCock();
                growBreastsUpTo(BreastsPart.d);
                addBallsIfNeeded();
                break;
            case trap:
                femininity = Math.max(2, femininity);
                replacePussyWithCock();
                addReplace(BreastsPart.flat, 1);
                addBallsIfNeeded();
                break;
            case asexual:
                femininity = Math.max(0, femininity);
                break;
            default:
                break;
        }
        if (newSex.hasBalls()) {
            addBallsIfNeeded();
        } else {
            removeAll("balls");
        }
        addReplace(new FacePart(face.hotness, femininity), 1);
    }

    public void makeGenitalOrgans(CharacterSex sex) {
        if (sex.hasPussy()) {
            if (!has("pussy")) {
                add(PussyPart.generic);
            }
        }
        if (sex.hasCock()) {
            if (!has("cock")) {
                add(new CockPart().applyMod(new SizeMod(SizeMod.COCK_SIZE_AVERAGE)));
            }
        }
        if (sex.hasBalls()) {
            if (!has("balls")) {
                add(new GenericBodyPart("balls", 0, 1.0, 1.5, "balls", ""));
            }
        }
    }

    @Override
    public Body clone() throws CloneNotSupportedException {
        Body newBody = (Body) super.clone();
        newBody.replacements = new ArrayList<>();
        replacements.forEach(rep -> newBody.replacements.add(new PartReplacement(rep)));
        newBody.modReplacements = new HashMap<>();
        modReplacements.forEach((type, reps) -> {
            newBody.modReplacements.put(type, new ArrayList<>());
            reps.forEach(rep -> newBody.modReplacements.get(type).add(new PartModReplacement(rep)));
        });
        newBody.bodyParts = new LinkedHashSet<>(bodyParts);
        newBody.currentParts = new HashSet<>(currentParts);
        return newBody;
    }

     public JsonObject save() {
        JsonObject bodyObj = new JsonObject();
        bodyObj.addProperty("hotness", hotness);
        bodyObj.addProperty("femininity", baseFemininity);
        JsonArray partsArr = new JsonArray();
        for (BodyPart part : bodyParts) {
            JsonObject obj = part.save();
            obj.addProperty("class", part.getClass()
                                 .getCanonicalName());
            partsArr.add(obj);
        }
        bodyObj.add("parts", partsArr);
        return bodyObj;
    }

    private void loadParts(JsonArray partsArr) {
        for (JsonElement element : partsArr) {
            JsonObject partJson = element.getAsJsonObject();
            this.add(JsonUtils.getGson().fromJson(partJson, BodyPart.class));
        }
    }

    public static Body load(JsonObject bodyObj, Character character) {
        double hotness = bodyObj.get("hotness").getAsDouble();
        Body body = new Body(character.getType(), hotness);
        body.loadParts(bodyObj.getAsJsonArray("parts"));
        double defaultFemininity = 0;
        if (body.has("pussy")) {
            defaultFemininity += 2;
        }
        if (body.has("cock")) {
            defaultFemininity -= 2;
        }
        body.baseFemininity = JsonUtils.getOptional(bodyObj, "femininity").map(JsonElement::getAsDouble)
                        .orElse(defaultFemininity);
        body.updateCurrentParts();
        return body;
    }

    private void advancedTemporaryParts(Combat c) {
        ArrayList<PartReplacement> expired = new ArrayList<>();
        ArrayList<PartModReplacement> expiredMods = new ArrayList<>();
        for (PartReplacement r : replacements) {
            r.duration -= 1;
            if (r.duration <= 0) {
                expired.add(r);
            }
        }
        for (PartModReplacement r : modReplacements.values().stream().flatMap(List::stream).collect(Collectors.toList())) {
            r.duration -= 1;
            if (r.duration <= 0) {
                expiredMods.add(r);
            }
        }
        Collections.reverse(expired);
        for (PartReplacement r : expired) {
            replacements.remove(r);
            updateCurrentParts();
            StringBuilder sb = new StringBuilder();
            LinkedList<BodyPart> added = new LinkedList<>(r.added);
            LinkedList<BodyPart> removed = new LinkedList<>(r.removed);
            if (added.size() > 0 && removed.size() == 0) {
                sb.append(Formatter.format("{self:NAME-POSSESSIVE} ", getCharacter(), getCharacter()));
                for (BodyPart p : added.subList(0, added.size() - 1)) {
                    sb.append(p.fullDescribe(getCharacter()))
                      .append(", ");
                }
                if (added.size() > 1) {
                    sb.append(" and ");
                }
                sb.append(added.get(added.size() - 1)
                               .fullDescribe(getCharacter()));
                sb.append(" disappeared.");
            } else if (removed.size() > 0 && added.size() == 0) {
                sb.append(Formatter.format("{self:NAME-POSSESSIVE} ", getCharacter(), getCharacter()));
                for (BodyPart p : removed.subList(0, removed.size() - 1)) {
                    sb.append(p.fullDescribe(getCharacter()))
                      .append(", ");
                }
                if (removed.size() > 1) {
                    sb.append(" and ");
                }
                sb.append(removed.get(removed.size() - 1)
                                 .fullDescribe(getCharacter()));
                sb.append(" reappeared.");
            } else {
                if (removed.size() > 0) {
                    added.size();
                    sb.append(Formatter.format("{self:NAME-POSSESSIVE} ", getCharacter(), getCharacter()));
                    for (BodyPart p : added.subList(0, added.size() - 1)) {
                        sb.append(p.fullDescribe(getCharacter())).append(", ");
                    }
                    if (added.size() > 1) {
                        sb.append(" and ");
                    }
                    sb.append(added.get(added.size() - 1).fullDescribe(getCharacter()));
                    if (removed.size() == 1 && removed.get(0) == null) {
                        sb.append(" disappeared");
                    } else {
                        sb.append(" turned back into ");
                    }
                    for (BodyPart p : removed.subList(0, removed.size() - 1)) {
                        sb.append(Formatter.prependPrefix(p.prefix(), p.fullDescribe(getCharacter()))).append(", ");
                    }
                    if (removed.size() > 1) {
                        sb.append(" and ");
                    }
                    BodyPart last = removed.get(removed.size() - 1);
                    if (last != null)
                        sb.append(Formatter.prependPrefix(last.prefix(), last.fullDescribe(getCharacter())));
                    sb.append('.');
                }
            }
            Formatter.writeIfCombat(c, getCharacter(), sb.toString());
        }
        for (PartModReplacement r : expiredMods) {
            Formatter.writeIfCombat(c, getCharacter(), Formatter
                            .format("{self:NAME-POSSESSIVE} %s lost its %s.", getCharacter(), getCharacter(), r.getType(), r.getMod().describeAdjective(r.getType())));
            modReplacements.get(r.getType()).remove(r);
        }
    }

    public void tick(Combat c) {
        advancedTemporaryParts(c);
        if (character != null) {
            updateCharacter();
        }
    }

    public BodyPart getRandomHole() {
        BodyPart part = getRandomPussy();
        if (part == null) {
            part = getRandom("ass");
        }
        return part;
    }

    public void clearReplacements() {
        replacements.clear();
        modReplacements.clear();
        updateCurrentParts();
        if (character != null) {
            updateCharacter();
        }
    }

    public int mod(Attribute a, int total) {
        int res = 0;
        for (BodyPart p : getCurrentParts()) {
            total += p.mod(a, total);
        }
        return res;
    }

    public void receiveCum(Combat c, Character opponent, BodyPart part) {
        if (part == null) {
            part = getCharacter().body.getRandom("skin");
        }
        part.receiveCum(c, getCharacter(), opponent, part);
        if (getCharacter().has(Trait.spiritphage)) {
            c.write(getCharacter(), "<br/><b>" + Formatter.capitalizeFirstLetter(getCharacter().subjectAction("glow", "glows")
                            + " with power as the cum is absorbed by " + getCharacter().possessiveAdjective() + " "
                            + part.describe(getCharacter()) + ".</b>"));
            getCharacter().add(c, new AttributeBuff(character, Attribute.power, 5, 10));
            getCharacter().add(c, new AttributeBuff(character, Attribute.seduction, 10, 10));
            getCharacter().add(c, new AttributeBuff(character, Attribute.cunning, 5, 10));
            getCharacter().buildMojo(c, 100);
        }
        if (opponent.has(Trait.hypnoticsemen)) {
            c.write(getCharacter(), Formatter.format(
                            "<br/><b>{other:NAME-POSSESSIVE} hypnotic semen takes its toll on {self:name-possessive} willpower, rendering {self:direct-object} doe-eyed and compliant.</b>",
                            getCharacter(), opponent));
            getCharacter().loseWillpower(c, 10 + Random.random(10));
        }
        if (part.getType().equals("ass") || part.getType().equals("pussy")) {
            if (getCharacter().has(Trait.RapidMeiosis) && getCharacter().has(Trait.slime)) {
                c.write(opponent, Formatter.format("{self:NAME-POSSESSIVE} hungry %s seems to vacuum {other:name-possessive} sperm into itself as {other:pronoun-action:cum|cums}. "
                                + "As {other:pronoun-action:lay|lays} there heaving in exertion, {self:possessive} belly rapidly bloats up "
                                + "as if going through 9 months of pregnancy within seconds. With a groan, {self:pronoun-action:expel|expels} a massive quantity of slime onto the floor. "
                                + "The slime seems to quiver for a second before reforming itself into an exact copy of {self:name-do}!", getCharacter(), opponent, part.describe(getCharacter())));
                c.addPet(getCharacter(), Divide.makeClone(c, getCharacter()).getSelf());
            }
            if (opponent.has(Trait.RapidMeiosis) && opponent.has(Trait.slime)) {
                c.write(opponent, Formatter.format("After {other:name-possessive} gooey cum fills {self:name-possessive} %s, "
                                + "{self:pronoun-action:feel|feels} {self:possessive} belly suddenly churn and inflate. "
                                + "The faux-semen seems to be multiplying inside {self:direct-object}! "
                                + "Without warning, the sticky liquid makes a quick exit out of {self:possessive} orifice "
                                + "and reforms itself into a copy of {other:name-do}!", getCharacter(), opponent, part.describe(getCharacter())));
                c.addPet(opponent, Divide.makeClone(c, opponent).getSelf());
            }
        }
    }

    public void tickHolding(Combat c, Character opponent, BodyPart selfOrgan, BodyPart otherOrgan) {
        if (selfOrgan != null && otherOrgan != null) {
            selfOrgan.tickHolding(c, getCharacter(), opponent, otherOrgan);
        }
    }

    public float penetrationFitnessModifier(Character self, Character other, boolean pitcher, boolean anal) {
        int totalCounterValue = 0;

        if (anal) {
            if (!pitcher) {
                totalCounterValue += get("ass").stream()
                                               .flatMapToInt(ass -> other.body.get("cock")
                                                                         .stream()
                                                                         .mapToInt(cock -> ass.counterValue(cock, self, other)))
                                               .sum();
            } else {
                totalCounterValue += get("cock").stream()
                                                .flatMapToInt(cock -> other.body.get("ass")
                                                                           .stream()
                                                                           .mapToInt(ass -> cock.counterValue(ass, self, other)))
                                                .sum();
            }
        } else {
            if (!pitcher) {
                totalCounterValue += get("pussy").stream()
                                                 .flatMapToInt(pussy -> other.body.get("cock")
                                                                             .stream()
                                                                             .mapToInt(cock -> pussy.counterValue(
                                                                                             cock, self, other)))
                                                 .sum();
            } else {
                totalCounterValue += get("cock").stream()
                                                .flatMapToInt(cock -> other.body.get("pussy")
                                                                           .stream()
                                                                           .mapToInt(pussy -> cock.counterValue(pussy, self, other)))
                                                .sum();
            }
        }
        return 20 * totalCounterValue;
    }

    public Body clone(Character other) throws CloneNotSupportedException {
        Body res = clone();
        res.character = other.getType();
        return res;
    }

    public void purge(Combat c) {
        for (List<PartModReplacement> replacements : modReplacements.values()) {
            for (PartModReplacement r : replacements) {
                r.duration = 0;
            }
        }
        for (PartReplacement r : replacements) {
            r.duration = 0;
        }
        advancedTemporaryParts(c);
    }

    public BodyPart getRandomGenital() {
        List<BodyPart> parts = new ArrayList<>();
        BodyPart pussy = getRandomPussy();
        BodyPart cock = getRandomCock();
        if (pussy != null) {
            parts.add(pussy);
        }
        if (cock != null) {
            parts.add(cock);
        }
        Collections.shuffle(parts);
        if (parts.size() >= 1) {
            return parts.get(0);
        } else {
            return getRandomBreasts();
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Body body = (Body) o;

        if (!(Math.abs(body.hotness - hotness) < 1e-6))
            return false;
        if (!(Math.abs(body.baseFemininity - baseFemininity) < 1e-6))
            return false;
        return bodyParts.equals(body.bodyParts);
    }

    @Override public int hashCode() {
        int result;
        long temp;
        result = bodyParts.hashCode();
        temp = Double.doubleToLongBits(hotness);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(baseFemininity);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public FacePart getFace() {
        return (FacePart)getRandom("face");
    }

    public void removeTemporaryPartMod(String type, PartMod mod) {
        List<PartModReplacement> replacements = modReplacements.get(type);
        if (replacements != null) {
            replacements.removeIf(partModReplacement -> partModReplacement.getMod().equals(mod));
        }
    }

    public AssPart getAssBelow(int size) {
        List<BodyPart> parts = get("ass");
        List<AssPart> upgradable = new ArrayList<>();
        for (BodyPart part : parts) {
            AssPart b = (AssPart) part;
            if (b.getSize() < size) {
                upgradable.add(b);
            }
        }
        if (upgradable.size() == 0) {
            return null;
        }
        return Random.pickRandomGuaranteed(upgradable);
    }

    public AssPart getAssAbove(int size) {
        List<BodyPart> parts = get("ass");
        List<AssPart> downgradable = new ArrayList<>();
        for (BodyPart part : parts) {
            AssPart b = (AssPart) part;
            if (b.getSize() > size) {
                downgradable.add(b);
            }
        }
        if (downgradable.size() == 0) {
            return null;
        }
        return Random.pickRandomGuaranteed(downgradable);
    }

    public static String partPronoun(String type) {
        if (pluralParts.contains(type)) {
            return "they";
        } else {
            return "it";
        }
    }

    // yeah i know it's not that simple, but best try right now
    public static String partArticle(String type) {
        if (pluralParts.contains(type)) {
            return "";
        } else if ("aeiouAEIOU".contains(type.substring(0, 1))){
            return "an ";
        } else {
            return "a ";
        }
    }

    public void applyMod(String partType, PartMod mod) {
        BodyPart part = Random.pickRandom(getPure(partType)).orElse(null);
        if (part instanceof GenericBodyPart) {
            GenericBodyPart genericPart = (GenericBodyPart) part;
            addReplace(genericPart.applyMod(mod), 1);
        } else {
            System.err.println("Tried to apply mod " + mod + " but found non-generic part: " + part);
        }
    }

    public void removeMod(String partType, PartMod mod) {
        Optional<BodyPart> part = getPure(partType).stream().filter(p -> p.moddedPartCountsAs(getCharacter(), mod)).findAny();
        if (part.isPresent() && part.get() instanceof GenericBodyPart) {
            GenericBodyPart genericPart = (GenericBodyPart) part.get();
            addReplace(genericPart.removeMod(mod), 1);
        } else {
            System.err.println("Tried to remove mod " + mod + " but found non-generic part: " + part);
        }
    }
}
