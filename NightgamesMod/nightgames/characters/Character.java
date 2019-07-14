package nightgames.characters;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nightgames.actions.Action;
import nightgames.actions.Move;
import nightgames.actions.Movement;
import nightgames.areas.Area;
import nightgames.areas.NinjaStash;
import nightgames.characters.body.*;
import nightgames.characters.body.arms.ArmManager;
import nightgames.characters.body.arms.ArmType;
import nightgames.characters.body.mods.DemonicMod;
import nightgames.characters.body.mods.SizeMod;
import nightgames.characters.custom.AiModifiers;
import nightgames.characters.custom.CharacterLine;
import nightgames.characters.trait.Trait;
import nightgames.combat.*;
import nightgames.ftc.FTCMatch;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.global.*;
import nightgames.gui.GUI;
import nightgames.gui.GUIColor;
import nightgames.items.Item;
import nightgames.items.clothing.*;
import nightgames.json.JsonUtils;
import nightgames.pet.CharacterPet;
import nightgames.pet.PetCharacter;
import nightgames.skills.*;
import nightgames.skills.damage.DamageType;
import nightgames.stance.Neutral;
import nightgames.stance.Stance;
import nightgames.status.*;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.Addiction.Severity;
import nightgames.status.addiction.AddictionType;
import nightgames.status.addiction.Dominance;
import nightgames.status.addiction.MindControl;
import nightgames.trap.Trap;
import nightgames.utilities.DebugHelper;
import nightgames.utilities.ProseUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nightgames.gui.GUIColor.*;

public abstract class Character extends Observable implements Cloneable {
    private static final String APOSTLES_COUNT = "APOSTLES_COUNT";
    private String name;
    public CharacterSex initialGender;
    public int level;
    public int xp;
    public int rank;
    public int money;
    public Map<Attribute, Integer> att;
    protected Meter stamina;
    protected Meter arousal;
    protected Meter mojo;
    protected Meter willpower;
    public Outfit outfit;
    public OutfitPlan outfitPlan;
    protected Area location;
    private CopyOnWriteArrayList<Skill> skills;
    public CopyOnWriteArrayList<Status> status;
    private Set<Stsflag> statusFlags;
    private CopyOnWriteArrayList<Trait> traits;
    private Map<Trait, Integer> temporaryAddedTraits;
    private Map<Trait, Integer> temporaryRemovedTraits;
    public Set<Status> removelist;
    public Set<Status> addlist;
    private Map<String, Integer> cooldowns;
    // List of characters who will not fight this character until after this character resupplies.
    private CopyOnWriteArrayList<String> mercy;
    protected Map<Item, Integer> inventory;
    private Map<String, Integer> flags;
    protected Item trophy;
    public State state;
    protected int busy;
    protected Map<String, Integer> attractions;
    protected Map<String, Integer> affections;
    public Set<Clothing> closet;
    public List<Challenge> challenges;
    public Body body;
    public int availableAttributePoints;
    public boolean orgasmed;
    public boolean custom;
    private boolean pleasured;
    public int orgasms;
    int cloned;
    private Map<Integer, LevelUpData> levelPlan;
    int levelsToGain;
    private Growth growth;
    public transient int lastInitRoll;
    
    public Character(String name, int level) {
        this.name = name;
        this.level = level;
        this.growth = new Growth();
        cloned = 0;
        custom = false;
        body = new Body(this);
        att = new EnumMap<>(Attribute.class);
        cooldowns = new HashMap<>();
        flags = new HashMap<>();
        levelPlan = new HashMap<>();
        att.put(Attribute.power, 5);
        att.put(Attribute.cunning, 5);
        att.put(Attribute.seduction, 5);
        att.put(Attribute.perception, 5);
        att.put(Attribute.speed, 5);
        money = 0;
        stamina = new Meter(22 + 3 * level);
        stamina.fill();
        arousal = new Meter(90 + 10 * level);
        mojo = new Meter(100);
        willpower = new Meter(40);
        orgasmed = false;
        pleasured = false;

        outfit = new Outfit();
        outfitPlan = new OutfitPlan();

        closet = new HashSet<>();
        skills = new CopyOnWriteArrayList<>();
        status = new CopyOnWriteArrayList<>();
        statusFlags = EnumSet.noneOf(Stsflag.class);
        traits = new CopyOnWriteArrayList<>();
        temporaryAddedTraits = new HashMap<>();
        temporaryRemovedTraits = new HashMap<>();
        removelist = new HashSet<>();
        addlist = new HashSet<>();
        mercy = new CopyOnWriteArrayList<>();
        inventory = new HashMap<>();
        attractions = new HashMap<>(2);
        affections = new HashMap<>(2);
        challenges = new ArrayList<>();
        location = new Area("", "", null);
        state = State.ready;
        busy = 0;
        setRank(0);

        SkillPool.learnSkills(this);
        levelsToGain = 0;
    }

    public void adjustTraits() {
        if (getPure(Attribute.darkness) >= 6 && !has(Trait.darkpromises)) {
            add(Trait.darkpromises);
        } else if (!(getPure(Attribute.darkness) >= 6) && has(Trait.darkpromises)) {
            remove(Trait.darkpromises);
        }
        boolean pheromonesRequirements = getPure(Attribute.animism) >= 2 || has(Trait.augmentedPheromones);
        if (pheromonesRequirements && !has(Trait.pheromones)) {
            add(Trait.pheromones);
        } else if (!pheromonesRequirements && has(Trait.pheromones)) {
            remove(Trait.pheromones);
        }
    }

    // TODO: Java's clone() method is pretty fiddly. For a class as mutable and reference-laden as Character(), it is probably preferable to use a copy constructor.
    @Override
    public Character clone() throws CloneNotSupportedException {
        Character c = (Character) super.clone();
        c.att = new EnumMap<>(att);
        c.stamina = stamina.clone();
        c.cloned = cloned + 1;
        c.arousal = arousal.clone();
        c.mojo = mojo.clone();
        c.willpower = willpower.clone();
        c.outfitPlan = new OutfitPlan(this.outfitPlan);
        c.outfit = new Outfit(outfit);
        c.flags = new HashMap<>(flags);
        c.status = status; // Will be deep-copied in finishClone()
        c.traits = new CopyOnWriteArrayList<>(traits);
        c.temporaryAddedTraits = new HashMap<>(temporaryAddedTraits);
        c.temporaryRemovedTraits = new HashMap<>(temporaryRemovedTraits);

        c.growth = (Growth) growth.clone();

        c.removelist = new HashSet<>(removelist);
        c.addlist = new HashSet<>(addlist);
        c.mercy = new CopyOnWriteArrayList<>(mercy);
        c.inventory = new ConcurrentHashMap<>(inventory);
        c.attractions = new HashMap<>(attractions);
        c.affections = new HashMap<>(affections);
        c.skills = (new CopyOnWriteArrayList<>(getSkills()));
        c.body = body.clone();
        c.body.character = c;
        c.orgasmed = orgasmed;
        c.statusFlags = EnumSet.copyOf(statusFlags);
        c.levelPlan = new HashMap<>();
        for (Entry<Integer, LevelUpData> entry : levelPlan.entrySet()) {
            levelPlan.put(entry.getKey(), (LevelUpData)entry.getValue().clone());
        }
        return c;
    }

    public void finishClone(Character other) {
        List<Status> oldstatus = status;
        status = new CopyOnWriteArrayList<>();
        for (Status s : oldstatus) {
            status.add(s.instance(this, other));
        }
    }

    public String getTrueName() {
        return name;
    }

    private List<Resistance> getResistances(Combat c) {
        List<Resistance> resistances = traits.stream().map(Trait::getResistance).collect(Collectors.toList());
        if (c != null) {
            Optional<PetCharacter> petOptional = c.getPetsFor(this).stream().filter(pet -> pet.has(Trait.protective)).findAny();
            petOptional.ifPresent(petCharacter -> resistances.add((combat, self, status) -> {
                if (Random.random(100) < 50 && status.flags().contains(Stsflag.debuff) && status.flags()
                                .contains(Stsflag.purgable)) {
                    return petCharacter.nameOrPossessivePronoun() + " Protection";
                }
                return "";
            }));
        }
        return resistances;
    }

    int getXPReqToNextLevel(int level) {
        return Math.min(45 + 5 * level, 100);
    }

    int getXPReqToNextLevel() {
        return getXPReqToNextLevel(getLevel());
    }

    public int get(Attribute a) {
        if (a == Attribute.slime && !has(Trait.slime)) {
            // always return 0 if there's no trait for it.
            return 0;
        }
        int total = getPure(a);
        for (Status s : getStatuses()) {
            total += s.mod(a);
        }
        total += body.mod(a, total);
        switch (a) {
            case spellcasting:
                if (outfit.has(ClothingTrait.mystic)) {
                    total += 2;
                }
                if (has(Trait.kabbalah)) {
                    total += 10;
                }
                break;
            case darkness:
                if (outfit.has(ClothingTrait.broody)) {
                    total += 2;
                }
                if (has(Trait.fallenAngel)) {
                    total += 10;
                }
                break;
            case ki:
                if (outfit.has(ClothingTrait.martial)) {
                    total += 2;
                }
                if (has(Trait.valkyrie)) {
                    total += 5;
                }
                break;
            case fetishism:
                if (outfit.has(ClothingTrait.kinky)) {
                    total += 2;
                }
                break;
            case cunning:
                if (has(Trait.FeralAgility) && is(Stsflag.feral)) {
                    // extra 5 strength at 10, extra 17 at 60.
                    total += Math.pow(getLevel(), .7);
                }
                break;
            case power:
                if (has(Trait.testosterone) && hasDick()) {
                    total += Math.min(20, 10 + getLevel() / 4);
                }
                if (has(Trait.FeralStrength) && is(Stsflag.feral)) {
                    // extra 5 strength at 10, extra 17 at 60.
                    total += Math.pow(getLevel(), .7);
                }
                if (has(Trait.valkyrie)) {
                    total += 10;
                }
                break;
            case science:
                if (has(ClothingTrait.geeky)) {
                    total += 2;
                }
                break;
            case hypnotism:
                if (has(Trait.Illusionist)) {
                    total += getPure(Attribute.spellcasting) / 2;
                }
                break;
            case speed:
                if (has(ClothingTrait.bulky)) {
                    total -= 1;
                }
                if (has(ClothingTrait.shoes)) {
                    total += 1;
                }
                if (has(ClothingTrait.heels) && !has(Trait.proheels)) {
                    total -= 2;
                }
                if (has(ClothingTrait.highheels) && !has(Trait.proheels)) {
                    total -= 1;
                }
                if (has(ClothingTrait.higherheels) && !has(Trait.proheels)) {
                    total -= 1;
                }
                break;
            case seduction:
                if (has(Trait.repressed)) {
                    total /= 2;
                }
                break;
            default:
                break;
        }
        return Math.max(0, total);
    }

    /**
     * Returns a read-only view of all of the character's current attributes, including temporary effects.
     *
     * @return The map of the character's attribute values.
     */
    public Map<Attribute, Integer> getAttributes() {
        Map<Attribute, Integer> currentAttributes = Arrays.stream(Attribute.values()).filter(attribute -> this.get(attribute) != 0)
                        .collect(Collectors.toMap(attribute -> attribute, this::get));
        return Collections.unmodifiableMap(currentAttributes);
    }

    public boolean has(ClothingTrait attribute) {
        return outfit.has(attribute);
    }

    public int getPure(Attribute a) {
        int total = 0;
        if (att.containsKey(a) && !a.equals(Attribute.willpower)) {
            total = att.get(a);
        }
        return total;
    }

    // TODO: Review whether checks consider low or high rolls successes
    public boolean checkVsDc(Attribute a, int dc) {
        return checkVsDc(a, 0, dc);
    }

    public boolean checkVsDc(Attribute a, int extra, int dc) {
        Random.DieRoll roll = check(a, extra);
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_DAMAGE)) {
            System.out.println("Checked roll of " + roll.result() + " against dc " + dc + "." +
                            (roll.criticalHit() ? "Critical hit!" : "") + (roll.criticalMiss() ? "Critical miss!" : ""));
        }
        return roll.vsDc(dc);
    }

    public Random.DieRoll check(Attribute a) {
        return check(a, 0);
    }

    public Random.DieRoll check(Attribute a, int extra) {
        Random.DieRoll roll = new Random.DieRoll(20, get(a) + extra);
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_DAMAGE)) {
            System.out.println("Rolled " + a + " = " + get(a) + " with extra " + extra + ", rolled " + roll.roll);
        }
        return roll;
    }

    public int getLevel() {
        return level;
    }

    public void gainXPPure(int i) {
        xp += i;
        update();
    }

    public void gainXP(int i) {
        assert i >= 0;
        double rate = 1.0;
        if (has(Trait.fastLearner)) {
            rate += .2;
        }
        rate *= GameState.gameState.xpRate;
        i = (int) Math.round(i * rate);

        if (!has(Trait.leveldrainer)) {
            gainXPPure(i);
        }
    }

    public void setXP(int i) {
        xp = i;
        update();
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void rankup() {
        rank++;
    }

    public abstract void ding(Combat c);

    public void ding(Combat c, int levelsToGain) {
        for (int i = 0; i < levelsToGain; i++) {
            ding(c);
        }

    }

    public String dong() {
        getLevelUpFor(getLevel()).unapply(this);
        getGrowth().levelDown(this);
        levelPlan.remove(getLevel());
        level--;
        this.adjustTraits();
        return Formatter.capitalizeFirstLetter(subject()) + " lost a level! <br/>";
    }

    public int getXP() {
        return xp;
    }

    public boolean isVulnerable() {
        return state.isVulnerable();
    }

    public void pain(Combat c, Character other, int i) {
        pain(c, other, i, true, true);
    }

    public void pain(Combat c, Character other, int i, boolean primary, boolean physical) {
        int pain = i;
        int bonus = 0;
        if (is(Stsflag.rewired) && physical) {
            String message = String.format("%s pleasured for <font color=%s>%d<font color='white'>\n",
                            Formatter.capitalizeFirstLetter(subjectWas()), AROUSAL_GAIN.rgbHTML(), pain);
            if (c != null) {
                c.writeSystemMessage(message);
            }
            arouse(pain, c);
            return;
        }
        if (has(Trait.slime)) {
            bonus -= pain / 2;
            if (c != null) {
                c.write(this, "The blow glances off " + nameOrPossessivePronoun() + " slimy body.");
            }
        }
        if (c != null) {
            if (has(Trait.cute) && other != null && other != this && primary && physical) {
                bonus -= Math.min(get(Attribute.seduction), 50) * pain / 100;
                c.write(this, Formatter.format(
                                "{self:NAME-POSSESSIVE} innocent appearance throws {other:direct-object} off and {other:subject-action:use|uses} much less strength than intended.",
                                this, other));
            }
            if (other != null && other != this && other.has(Trait.dirtyfighter) && (c.getStance().prone(other)
                            || c.getStance()
                                .sub(other))
                            && physical) {
                bonus += 10;
                c.write(this, Formatter.format(
                                "{other:SUBJECT-ACTION:know|knows} how to fight dirty, and {other:action:manage|manages} to give {self:direct-object} a lot more trouble than {self:subject} expected despite being in a compromised position.",
                                this, other));
            }

            if (has(Trait.sacrosanct) && physical && primary) {
                c.write(this, Formatter.format(
                                "{other:SUBJECT-ACTION:well|wells} up with guilt at hurting such a holy being. {self:PRONOUN-ACTION:become|becomes} temporarily untouchable in {other:possessive} eyes.",
                                this, other));
                add(c, new Alluring(this, 1));
            }
            for (Status s : getStatuses()) {
                bonus += s.damage(c, pain);
            }
        }
        pain += bonus;
        pain = Math.max(1, pain);
        emote(Emotion.angry, pain / 3);

        // threshold at which pain calms you down
        int painAllowance = Math.max(10, getStamina().max() / 6);
        if (other != null && other.has(Trait.wrassler)) {
            painAllowance *= 1.5;
        }
        int difference = pain - painAllowance;
        // if the pain exceeds the threshold and you aren't a masochist
        // calm down by the overflow

        if (c != null) {
            c.writeSystemMessage(String.format("%s hurt for <font color=%s>%d<font color='white'>",
                            subjectWas(), STAMINA_LOSS.rgbHTML(), pain));
        }
        if (difference > 0 && !is(Stsflag.masochism)) {
            if (other != null && other.has(Trait.wrassler)) {
                calm(c, difference / 2);
            } else {
                calm(c, difference);
            }
        }
        if (other != null && other.has(Trait.sadist) && !is(Stsflag.masochism)) {
            if (c != null) {
                c.write("<br/>"+ Formatter.capitalizeFirstLetter(
                                String.format("%s blows hits all the right spots and %s to some masochistic tendencies.",
                                                other.nameOrPossessivePronoun(), subjectAction("awaken"))));
            }
            add(c, new Masochistic(this));
        }
        // if you are a masochist, arouse by pain up to the threshold.
        if (is(Stsflag.masochism) && physical) {
            this.arouse(Math.max(i, painAllowance), c);
        }
        if (other != null && other.has(Trait.disablingblows) && Random.random(5) == 0) {
            int mag = Random.random(3) + 1;
            if (c != null) {
                c.write(other, Formatter.format("Something about the way {other:subject-action:hit|hits}"
                                + " {self:name-do} seems to strip away {self:possessive} strength.", this, other));
            }
            add(c, new AttributeBuff(this, Attribute.power, -mag, 10));
        }
        stamina.reduce(pain);
    }

    public void weaken(Combat c, final int i) {
        int weak = i;
        int bonus = 0;
        for (Status s : getStatuses()) {
            bonus += s.weakened(c, i);
        }
        weak += bonus;
        weak = Math.max(1, weak);
        if (weak >= stamina.get()) {
            weak = stamina.get();
        }
        if (weak > 0) {
            if (c != null) {
                c.writeSystemMessage(String.format("%s weakened by <font color=%s>%d<font color='white'>",
                                subjectWas(), STAMINA_LOSS.rgbHTML(), weak));
            }
            stamina.reduce(weak);
        }
    }

    public void heal(Combat c, int i) {
        heal(c, i, "");
    }
    public void heal(Combat c, int i, String reason) {
        i = Math.max(1, i);
        if (c != null) {
            c.writeSystemMessage(String.format("%s healed for <font color=%s>%d<font color='white'>%s",
                            subjectWas(), STAMINA_GAIN.rgbHTML(), i, reason));
        }
        stamina.restore(i);
    }

    public String subject() {
        return getName();
    }

    public int pleasure(int i, Combat c, Character source) {
        return resolvePleasure(i, c, source, Body.nonePart, Body.nonePart);
    }

    public int resolvePleasure(int i, Combat c, Character source, BodyPart selfPart, BodyPart opponentPart) {
        int pleasure = i;

        emote(Emotion.horny, i / 4 + 1);
        if (pleasure < 1) {
            pleasure = 1;
        }
        pleasured = true;
        // pleasure = 0;
        arousal.restoreNoLimit(pleasure);
        if (checkOrgasm()) {
            doOrgasm(c, source, selfPart, opponentPart);
        }
        return pleasure;
    }

    public void temptNoSkillNoTempter(Combat c, int i) {
        temptNoSkillNoSource(c, null, i);
    }

    public void temptNoSkillNoSource(Combat c, Character tempter, int i) {
        tempt(c, tempter, null, i, null);
    }

    public void temptNoSource(Combat c, Character tempter, int i, Skill skill) {
        tempt(c, tempter, null, i, skill);
    }

    public void temptNoSkill(Combat c, Character tempter, BodyPart with, int i) {
        tempt(c, tempter, with, i, null);
    }

    public void temptWithSkill(Combat c, Character tempter, BodyPart with, int i, Skill skill) {
        tempt(c, tempter, with, i, skill);
    }

    private void tempt(Combat c, Character tempter, BodyPart with, int i, Skill skill) {
        String extraMsg = "";
        double baseModifier = 1.0;
        if (has(Trait.oblivious)) {
            extraMsg += " (Oblivious)";
            baseModifier *= .1;
        }
        if (has(Trait.Unsatisfied) && (getArousal().percent() >= 50 || getWillpower().percent() < 25)) {
            extraMsg += " (Unsatisfied)";
            if (c != null && c.getOpponent(this).human()) {
                baseModifier *= .2;
            } else {
                baseModifier *= .66;
            }
        }

        int bonus = 0;
        for (Status s : getStatuses()) {
            bonus += s.tempted(c, i);
        }

        if (has(Trait.desensitized2)) {
            bonus -= i / 2;
        }

        String bonusString = "";
        if (bonus > 0) {
            bonusString = String.format(" + <font color=%s>%d<font color='white'>", TEMPT_BONUS.rgbHTML(), bonus);
        } else if (bonus < 0) {
            bonusString = String.format(" - <font color=%s>%d<font color='white'>", TEMPT_MALUS.rgbHTML(), Math.abs(bonus));
        }

        if (tempter != null) {
            int dmg;
            String message;
            double temptMultiplier = baseModifier;
            double stalenessModifier = 1.0;
            String stalenessString = "";

            if (skill != null) {
                if (c != null) {
                    stalenessModifier = c.getCombatantData(skill.getSelf()).getMoveModifier(skill);
                }
                if (Math.abs(stalenessModifier - 1.0) >= .1 ) {
                    stalenessString = String.format(", staleness: %.1f", stalenessModifier);
                }
            }

            if (with != null) {
                // triple multiplier for the body part
                temptMultiplier *= tempter.body.getCharismaBonus(c, this) + with.getHotness(tempter, this) * 2;
                dmg = (int) Math.max(0, Math.round((i + bonus) * temptMultiplier * stalenessModifier));
                message = String.format(
                                "%s tempted by %s %s for <font color=%s>%d<font color='white'> (base:%d%s, charisma:%.1f%s)%s\n",
                                Formatter.capitalizeFirstLetter(subjectWas()), tempter.nameOrPossessivePronoun(),
                                with.describe(tempter), AROUSAL_TEMPT.rgbHTML(), dmg, i, bonusString, temptMultiplier, stalenessString, extraMsg);
            } else {
                temptMultiplier *= tempter.body.getCharismaBonus(c, this);
                if (c != null && tempter.has(Trait.obsequiousAppeal) && c.getStance()
                                                                         .sub(tempter)) {
                    temptMultiplier *= 2;
                }
                dmg = Math.max((int) Math.round((i + bonus) * temptMultiplier * stalenessModifier), 0);
                message = String.format(
                                "%s tempted %s for <font color=%s>%d<font color='white'> (base:%d%s, charisma:%.1f%s)%s\n",
                                Formatter.capitalizeFirstLetter(tempter.subject()),
                                tempter == this ? reflectivePronoun() : nameDirectObject(), AROUSAL_TEMPT.rgbHTML(), dmg, i, bonusString, temptMultiplier, stalenessString, extraMsg);
            }

            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_DAMAGE)) {
                System.out.print(message);
            }
            if (c != null) {
                c.writeSystemMessage(message);
            }
            tempt(dmg);

            if (tempter.has(Trait.mandateOfHeaven)) {
                double arousalPercent = dmg / getArousal().max() * 100;
                CombatantData data;
                if (c != null) {
                    data = c.getCombatantData(this);
                    data.setDoubleFlag(Combat.TEMPT_WORSHIP_BONUS, data.getDoubleFlag(Combat.TEMPT_WORSHIP_BONUS) + arousalPercent);
                    double newWorshipBonus = data.getDoubleFlag(Combat.TEMPT_WORSHIP_BONUS);
                    if (newWorshipBonus > 10 && newWorshipBonus < 25) {
                        c.write(tempter, Formatter.format("There's a nagging urge for {self:name-do} to throw {self:reflective} at {other:name-possessive} feet and beg for release.", this, tempter));
                    } else if (newWorshipBonus < 50) {
                        c.write(tempter, Formatter.format("{self:SUBJECT-ACTION:feel|feels} an urge to throw {self:reflective} at {other:name-possessive} feet and beg for release.", this, tempter));
                    } else {
                        c.write(tempter, Formatter.format("{self:SUBJECT-ACTION:are|is} feeling an irresistable urge to throw {self:reflective} at {other:name-possessive} feet and beg for release.", this, tempter));
                    }
                }
            }
        } else {
            int damage = Math.max(0, (int) Math.round((i + bonus) * baseModifier));
            if (c != null) {
                c.writeSystemMessage(
                                String.format("%s tempted for <font color=%s>%d<font color='white'>%s\n",
                                                subjectWas(), AROUSAL_TEMPT.rgbHTML(), damage, extraMsg));
            }
            tempt(damage);
        }
    }

    public void arouse(int i, Combat c) {
        arouse(i, c, "");
    }

    public void arouse(int i, Combat c, String source) {
        // NOTE: This is for non-physical, non-tempting lust arousal. Normal arousal is handled by Body.pleasure().
        String extraMsg = "";
        if (has(Trait.Unsatisfied) && (getArousal().percent() >= 50 || getWillpower().percent() < 25)) {
            extraMsg += " (Unsatisfied)";
            // make it much less effective vs NPCs because they're bad at exploiting the weakness
            if (c != null && c.getOpponent(this).human()) {
                i = Math.max(1, i / 5);
            } else {
                i = Math.max(1, i * 2 / 3);
            }
        }
        String message = String.format("%s aroused for <font color=%s>%d<font color='white'> %s%s\n",
                        Formatter.capitalizeFirstLetter(subjectWas()), AROUSAL_TEMPT.rgbHTML(), i, source, extraMsg);
        if (c != null) {
            c.writeSystemMessage(message);
        }
        tempt(i);
    }

    public String subjectAction(String verb, String pluralverb) {
        return subject() + " " + pluralverb;
    }

    public String subjectAction(String verb) {
        return subjectAction(verb, ProseUtils.getThirdPersonFromFirstPerson(verb));
    }

    public String subjectWas() {
        return subject() + " was";
    }

    public void tempt(int i) {
        emote(Emotion.horny, i / 4);
        arousal.restoreNoLimit(i);
    }

    public void calm(Combat c, int i) {
        i = Math.min(arousal.get(), i);
        if (i > 0) {
            if (c != null) {
                String message = String.format("%s calmed down by <font color=%s>%d<font color='white'>\n",
                                Formatter.capitalizeFirstLetter(subjectAction("have", "has")), AROUSAL_LOSS.rgbHTML(),
                                i);
                c.writeSystemMessage(message);
            }
            arousal.reduce(i);
        }
    }

    public Meter getStamina() {
        return stamina;
    }

    public Meter getArousal() {
        return arousal;
    }

    public Meter getMojo() {
        return mojo;
    }

    public Meter getWillpower() {
        return willpower;
    }

    public void buildMojo(Combat c, int percent) {
        buildMojo(c, percent, "");
    }

    public void buildMojo(Combat c, int percent, String source) {
        if (Dominance.mojoIsBlocked(this, c)) {
            c.write(c.getOpponent(this), 
                            String.format("Enraptured by %s display of dominance, %s no mojo.", 
                                            c.getOpponent(this).nameOrPossessivePronoun(), subjectAction("build")));
            return;
        }
        
        int x = percent * Math.min(mojo.max(), 200) / 100;
        int bonus = 0;
        for (Status s : getStatuses()) {
            bonus += s.gainmojo(x);
        }
        x += bonus;
        if (x > 0) {
            mojo.restore(x);
            if (c != null) {
                c.writeSystemMessage(Formatter.capitalizeFirstLetter(
                                String.format("%s <font color=%s>%d<font color='white'> mojo%s.",
                                                subjectAction("built", "built"), MOJO_GAIN.rgbHTML(), x, source)));
            }
        } else if (x < 0) {
            loseMojo(c, x);
        }
    }

    public void spendMojo(Combat c, int i) {
        int cost = i;
        int bonus = 0;
        for (Status s : getStatuses()) {
            bonus += s.spendmojo(i);
        }
        cost += bonus;
        mojo.reduce(cost);
        if (mojo.get() < 0) {
            mojo.set(0);
        }
        if (c != null && i != 0) {
            c.writeSystemMessage(Formatter.capitalizeFirstLetter(
                            String.format("%s <font color=%s>%d<font color='white'> mojo.",
                                            subjectAction("spent", "spent"), MOJO_SPEND.rgbHTML(), cost)));
        }
    }

    public int loseMojo(Combat c, int i) {
        return loseMojo(c, i, "");
    }

    public int loseMojo(Combat c, int i, String source) {
        int amt = Math.min(mojo.get(), i);
        mojo.reduce(amt);
        if (mojo.get() < 0) {
            mojo.set(0);
        }
        if (c != null) {
            c.writeSystemMessage(Formatter.capitalizeFirstLetter(
                            String.format("%s <font color=%s>%d<font color='white'> mojo%s.",
                                            subjectAction("lost", "lost"), MOJO_LOSS.rgbHTML(), amt, source)));
        }
        return amt;
    }

    public Area location() {
        return location;
    }

    public int init() {
        return att.get(Attribute.speed) + Random.random(10);
    }

    public boolean reallyNude() {
        return topless() && pantsless();
    }

    public boolean torsoNude() {
        return topless() && pantsless();
    }

    public boolean mostlyNude() {
        return breastsAvailable() && crotchAvailable();
    }

    public boolean breastsAvailable() {
        return outfit.slotOpen(ClothingSlot.top);
    }

    public boolean crotchAvailable() {
        return outfit.slotOpen(ClothingSlot.bottom);
    }

    void dress(Combat c) {
        outfit.dress(c.getCombatantData(this).getClothespile());
    }

    public void change() {
        outfit.undress();
        outfit.dress(outfitPlan);
        if (Match.getMatch() != null) {
            Match.getMatch().condition.handleOutfit(this);
        }
    }

    public String getName() {
        Disguised disguised = (Disguised) getStatus(Stsflag.disguised);
        if (disguised != null) {
            return disguised.getTarget().getTrueName();
        }
        return name;
    }

    public void completelyNudify(Combat c) {
        List<Clothing> articles = outfit.undress();
        if (c != null) {
            articles.forEach(article -> c.getCombatantData(this).addToClothesPile(this, article));
        }
    }

    /* undress without any modifiers */
    public void undress(Combat c) {
        if (!breastsAvailable() || !crotchAvailable()) {
            // first time only strips down to what blocks fucking
            outfit.strip().forEach(article -> c.getCombatantData(this).addToClothesPile(this, article));
        } else {
            // second time strips down everything
            outfit.undress().forEach(article -> c.getCombatantData(this).addToClothesPile(this, article));
        }
    }

    /* undress non indestructibles */
    public boolean nudify() {
        if (!breastsAvailable() || !crotchAvailable()) {
            // first time only strips down to what blocks fucking
            outfit.forcedStrip();
        } else {
            // second time strips down everything
            outfit.undressOnly(c -> !c.is(ClothingTrait.indestructible));
        }
        return mostlyNude();
    }

    public Clothing strip(Clothing article, Combat c) {
        if (article == null) {
            return null;
        }
        Clothing res = outfit.unequip(article);
        c.getCombatantData(this).addToClothesPile(this, res);
        return res;
    }

    public Clothing strip(ClothingSlot slot, Combat c) {
        return strip(outfit.getTopOfSlot(slot), c);
    }

    Clothing stripRandom(Combat c) {
        return stripRandom(c, false);
    }

    void gainTrophy(Combat c, Character target) {
        Optional<Clothing> underwear = target.outfitPlan.stream()
                        .filter(article -> article.getSlots().contains(ClothingSlot.bottom) && article.getLayer() == 0)
                        .findFirst();
        if (!underwear.isPresent() || c.getCombatantData(target).getClothespile().contains(underwear.get())) {
            this.gain(target.getTrophy());
        }
    }

    public Clothing shredRandom() {
        ClothingSlot slot = outfit.getRandomShreddableSlot();
        if (slot != null) {
            return shred(slot);
        }
        return null;
    }

    private boolean topless() {
        return outfit.slotEmpty(ClothingSlot.top);
    }

    public boolean pantsless() {
        return outfit.slotEmpty(ClothingSlot.bottom);
    }

    public Clothing stripRandom(Combat c, boolean force) {
        return strip(force ? outfit.getRandomEquippedSlot() : outfit.getRandomNakedSlot(), c);
    }

    public Clothing shred(ClothingSlot slot) {
        Clothing article = outfit.getTopOfSlot(slot);
        if (article == null || article.is(ClothingTrait.indestructible)) {
            System.err.println("Tried to shred clothing that doesn't exist at slot " + slot.name() + " at clone "
                            + cloned);
            System.err.println(outfit.toString());
            Thread.dumpStack();
            return null;
        } else {
            // don't add it to the pile
            return outfit.unequip(article);
        }
    }

    private void countdown(Map<Trait, Integer> counters) {
        Iterator<Map.Entry<Trait, Integer>> it = counters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Trait, Integer> ent = it.next();
            int remaining = ent.getValue() - 1;
            if (remaining > 0) {
                ent.setValue(remaining);
            } else {
                it.remove();
            }
        }
    }

    public void tick(Combat c) {
        body.tick(c);
        status.forEach(s -> s.tick(c));
        countdown(temporaryAddedTraits);
        countdown(temporaryRemovedTraits);
    }

    public Collection<Trait> getTraits() {
        Collection<Trait> allTraits = new HashSet<>();
        allTraits.addAll(traits);
        allTraits.addAll(temporaryAddedTraits.keySet());
        allTraits.removeAll(temporaryRemovedTraits.keySet());
        return allTraits;
    }
    
    public void clearTraits() {
        List<Trait> traitsToRemove = new ArrayList<>(traits);
        traitsToRemove.forEach(this::removeTraitDontSaveData);
    }

    public Collection<Trait> getTraitsPure() {
        return Collections.unmodifiableCollection(traits);
    }

    public boolean addTemporaryTrait(Trait t, int duration) {
        if (!getTraits().contains(t)) {
            temporaryAddedTraits.put(t, duration);
            return true;
        } else if (temporaryAddedTraits.containsKey(t)) {
            temporaryAddedTraits.put(t, Math.max(duration, temporaryAddedTraits.get(t)));
            return true;
        }
        return false;
    }

    public void removeTemporarilyAddedTrait(Trait t) {
        temporaryAddedTraits.remove(t);
    }

    public boolean removeTemporaryTrait(Trait t, int duration) {
        if (temporaryRemovedTraits.containsKey(t)) {
            temporaryRemovedTraits.put(t, Math.max(duration, temporaryRemovedTraits.get(t)));
            return true;
        } else if (traits.contains(t)) {
            temporaryRemovedTraits.put(t, duration);
            return true;
        }
        return false;
    }

    public LevelUpData getLevelUpFor(int level) {
        return levelPlan.computeIfAbsent(level, k -> new LevelUpData());
    }

    public void modAttributeDontSaveData(Attribute a, int i) {
        modAttributeDontSaveData(a, i, false);
    }

    public void modAttributeDontSaveData(Attribute a, int i, boolean silent) {
        if (human() && i != 0 && !silent && cloned == 0) {
            Formatter.writeIfCombatUpdateImmediately(
                            GUI.gui.combat, this, "You have " + (i > 0 ? "gained" : "lost") + " " + Math.abs(i) + " " + a.name());
        }
        if (a.equals(Attribute.willpower)) {
            getWillpower().gain(i * 2);
        } else {
            att.put(a, att.getOrDefault(a, 0) + i);
        }
    }

    public void mod(Attribute a, int i) {
        mod(a, i, false);
    }

    public void mod(Attribute a, int i, boolean silent) {
        modAttributeDontSaveData(a, i, silent);
        getLevelUpFor(getLevel()).modAttribute(a, i);
    }

    public boolean addTraitDontSaveData(Trait t) {
        if (t == null) {
            System.err.println("Tried to add an null trait!");
            DebugHelper.printStackFrame(5, 1);
            return false;
        }
        if (traits.addIfAbsent(t)) {
            if (t.equals(Trait.mojoMaster)) {
                mojo.gain(20);
            }
            return true;
        }
        return false;
    }

    public boolean add(Trait t) {
        if (addTraitDontSaveData(t)) {
            getLevelUpFor(getLevel()).addTrait(t);
            return true;
        }
        return false;
    }

    boolean removeTraitDontSaveData(Trait t) {
        if (traits.remove(t)) {
            if (t.equals(Trait.mojoMaster)) {
                mojo.gain(-20);
            }
            return true;
        }
        return false;
    }

    public boolean remove(Trait t) {
        if (removeTraitDontSaveData(t)) {
            getLevelUpFor(getLevel()).removeTrait(t);
            return true;
        }
        return false;
    }

    public boolean hasPure(Trait t) {
        return getTraits().contains(t);
    }

    public boolean has(Trait t) {
        boolean hasTrait = false;
        if (t.parent != null) {
            hasTrait = getTraits().contains(t.parent);
        }
        if (outfit.has(t)) {
            return true;
        }
        hasTrait = hasTrait || hasPure(t);
        return hasTrait;
    }

    public boolean hasDick() {
        return body.get("cock").size() > 0;
    }

    public boolean hasBalls() {
        return body.get("balls").size() > 0;
    }

    public boolean hasPussy() {
        return body.get("pussy").size() > 0;
    }

    public boolean hasBreasts() {
        return body.get("breasts").size() > 0;
    }

    public void regen() {
        regen(null, false);
    }

    public void regen(Combat c) {
        regen(c, true);
    }

    public void regen(Combat c, boolean combat) {
        getAddictions().forEach(Addiction::refreshWithdrawal);
        int regen = 1;
        // TODO can't find the concurrent modification error, just use a copy
        // for now I guess...
        for (Status s : new HashSet<>(getStatuses())) {
            regen += s.regen(c);
        }
        if (has(Trait.BoundlessEnergy)) {
            regen += 1;
        }
        if (regen > 0) {
            heal(c, regen);
        } else {
            weaken(c, -regen);
        }
        if (combat) {
            if (has(Trait.exhibitionist) && mostlyNude()) {
                buildMojo(c, 5);
            }
            if (outfit.has(ClothingTrait.stylish)) {
                buildMojo(c, 1);
            }
            if (has(Trait.SexualGroove)) {
                buildMojo(c, 3);
            }
            if (outfit.has(ClothingTrait.lame)) {
                buildMojo(c, -1);
            }
        }
    }

    public void preturnUpkeep() {
        orgasmed = false;
    }

    public void addNonCombat(Status status) {
        add(null, status);
    }

    public boolean has(Status status) {
        return this.status.stream().anyMatch(s -> s.flags().containsAll(status.flags()) && status.flags()
                        .containsAll(status.flags()) && s.getClass().equals(status.getClass()) && s.getVariant().equals(status.getVariant()));
    }

    public void add(Combat c, Status status) {
        boolean cynical = false;
        String message = "";
        boolean done = false;
        Status effectiveStatus = status;
        for (Status s : getStatuses()) {
            if (s.flags().contains(Stsflag.cynical)) {
                cynical = true;
            }
        }
        if (cynical && status.mindgames()) {
            message = subjectAction("resist", "resists") + " " + status.name + " (Cynical).";
            done = true;
        } else {
            for (Resistance r : getResistances(c)) {
                String resistReason;
                resistReason = r.resisted(c, this, status);
                if (!resistReason.isEmpty()) {
                    message = subjectAction("resist", "resists") + " " + status.name + " (" + resistReason + ").";
                    done = true;
                    break;
                }
            }
        }
        if (!done) {
            boolean unique = true;
            for (Status s : this.status) {
                if (s.getClass().equals(status.getClass()) && s.getVariant().equals(status.getVariant())) {
                    s.replace(status);
                    message = s.initialMessage(c, Optional.of(status));
                    done = true;
                    effectiveStatus = s;
                    break;
                }
                if (s.overrides(status)) {
                    unique = false;
                }
            }
            if (!done && unique) {
                this.status.add(status);
                message = status.initialMessage(c, Optional.empty());
                done = true;
            }
        }
        if (done) {
            if (!message.isEmpty()) {
                message = Formatter.capitalizeFirstLetter(message);
                if (c != null) {
                    if (!c.getOpponent(this).human() || !c.getOpponent(this).is(Stsflag.blinded)) {
                        c.write(this, "<b>" + message + "</b>");
                    }
                    effectiveStatus.onApply(c, c.getOpponent(this));
                } else if (human() || location() != null && location().humanPresent()) {
                    GUI.gui.message("<b>" + message + "</b>");
                    effectiveStatus.onApply(null, null);
                }
            }
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
                System.out.println(message);
            }
        }
    }

    private double getPheromonesChance(Combat c) {
        double baseChance = .1 + getExposure() / 3 + (arousal.getOverflow() + arousal.get()) / (float) arousal.max();
        double mod = c.getStance().pheromoneMod(this);
        if (has(Trait.FastDiffusion)) {
            mod = Math.max(2, mod);
        }
        return Math.min(1, baseChance * mod);
    }

    boolean rollPheromones(Combat c) {
        double chance = getPheromonesChance(c);
        double roll = Random.randomdouble();
        return roll < chance;
    }

    int getPheromonePower() {
        return 5;
    }

    private void dropStatus(Combat c, Character opponent) {
        Set<Status> removedStatuses = status.stream().filter(s -> !s.meetsRequirements(c, this, opponent))
                        .collect(Collectors.toSet());
        removedStatuses.addAll(removelist);
        removedStatuses.forEach(s -> {
            if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
                System.out.println(s.name + " removed from " + getTrueName());
            }
            s.onRemove(c, opponent);
        });
        status.removeAll(removedStatuses);
        for (Status s : addlist) {
            add(c, s);
        }
        removelist.clear();
        addlist.clear();
    }

    public void removeStatusNoSideEffects() {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.println("Purging (remove no sideeffects) " + getTrueName());
        }
        status.removeAll(removelist);
        removelist.clear();
    }

    public boolean is(Stsflag sts) {
        if (statusFlags.contains(sts))
            return true;
        for (Status s : getStatuses()) {
            if (s.flags().contains(sts)) {
                return true;
            }
        }
        return false;
    }

    public boolean is(Stsflag sts, String variant) {
        for (Status s : getStatuses()) {
            if (s.flags().contains(sts) && s.getVariant().equals(variant)) {
                return true;
            }
        }
        return false;
    }

    public boolean stunned() {
        for (Status s : getStatuses()) {
            if (s.flags().contains(Stsflag.stunned) || s.flags().contains(Stsflag.falling)) {
                return true;
            }
        }
        return false;
    }

    public boolean distracted() {
        for (Status s : getStatuses()) {
            if (s.flags().contains(Stsflag.distracted) || s.flags().contains(Stsflag.trance)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasStatus(Stsflag flag) {
        for (Status s : getStatuses()) {
            if (s.flags().contains(flag)) {
                return true;
            }
        }
        return false;
    }

    public void removeStatus(Status status) {
        removelist.add(status);
    }

    public void removeStatus(Stsflag flag) {
        for (Status s : getStatuses()) {
            if (s.flags().contains(flag)) {
                removelist.add(s);
            }
        }
    }

    public boolean bound() {
        return is(Stsflag.bound);
    }

    public void free() {
        for (Status s : getStatuses()) {
            if (s.flags().contains(Stsflag.bound)) {
                removelist.add(s);
            }
        }
    }

    public void struggle() {
        for (Status s : getStatuses()) {
            s.struggle(this);
        }
    }

    public int getEscape(Combat c, Character from) {
        int total = 0;
        for (Status s : getStatuses()) {
            total += s.escape();
        }
        if (has(Trait.freeSpirit)) {
            total += 5;
        }
        if (has(Trait.Slippery)) {
            total += 10;
        }
        if (from != null) {
            if (from.has(Trait.Clingy)) {
                total -= 5;
            }
            if (from.has(Trait.FeralStrength) && from.is(Stsflag.feral)) {
                total -= 5;
            }
        }
        if (c != null && checkAddiction(AddictionType.DOMINANCE, c.getOpponent(this))) {
            total -= getAddiction(AddictionType.MIND_CONTROL).map(Addiction::getCombatSeverity).map(Enum::ordinal).orElse(0) * 8;
        }
        if (has(Trait.FeralStrength) && is(Stsflag.feral)) {
            total += 5;
        }
        if (c != null) {
            int stanceMod = c.getStance().getEscapeMod(c, this);
            if (stanceMod < 0) {
                if (bound()) {
                    total += stanceMod / 2;
                } else {
                    total += stanceMod;
                }
            }
        }
        return total;
    }

    public boolean canMasturbate() {
        return !(stunned() || bound() || is(Stsflag.distracted) || is(Stsflag.enthralled));
    }

    public boolean canAct() {
        return !(stunned() || distracted() || bound() || is(Stsflag.enthralled));
    }

    public boolean canRespond() {
        return !(stunned() || distracted() || is(Stsflag.enthralled));
    }

    public abstract void detect();

    public abstract void doAction(Action action);

    public int getTraitMod(Trait trait, int mod) {
        return has(trait) ? mod : 0;
    }

    public enum FightIntent {
        fight,
        flee,
        smoke
    }

    public abstract FightIntent faceOff(Character opponent, Encounter enc);

    public abstract Encs spy(Character opponent, Encounter enc);

    public abstract String describe(int per, Combat c);

    public abstract void victory(Combat c, Result flag);

    public abstract void defeat(Combat c, Result flag);

    public abstract void intervene3p(Combat c, Character target, Character assist);

    public abstract void victory3p(Combat c, Character target, Character assist);

    public abstract boolean resist3p(Combat c, Character target, Character assist);

    /**
     * @param c combat to act in
     * @return true if combat should be paused.
     */
    public abstract boolean chooseSkill(Combat c) throws InterruptedException;

    public abstract Optional<Action> move() throws InterruptedException;

    public abstract void draw(Combat c, Result flag);

    boolean chooseSkillInteractive(Combat c) {
        Character target;
        if (c.p1 == this) {
            target = c.p2;
        } else {
            target = c.p1;
        }
        showSkillChoices(c, target);
        try {
            c.chooseSkill(this, GUI.gui.getChosenSkill());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Already paused while completing skill choice future
        return false;
    }

    public abstract boolean human();

    public abstract String bbLiner(Combat c, Character target);

    public abstract String nakedLiner(Combat c, Character target);

    public abstract String stunLiner(Combat c, Character target);

    public abstract String taunt(Combat c, Character target);

    public abstract void decideIntervene(Encounter fight, Character p1, Character p2) throws InterruptedException;

    public abstract Encs showerSceneResponse(Character target, Encounter encounter);

    public boolean humanControlled(Combat c) {
        return human() || DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILL_CHOICES) && c.getOpponent(this).human();
    }

    public JsonObject save() {
        JsonObject saveObj = new JsonObject();
        saveObj.addProperty("name", name);
        saveObj.addProperty("type", getType());
        saveObj.addProperty("level", level);
        saveObj.addProperty("rank", getRank());
        saveObj.addProperty("xp", xp);
        saveObj.addProperty("money", money);
        {
            JsonObject resources = new JsonObject();
            resources.addProperty("stamina", stamina.trueMax());
            resources.addProperty("arousal", arousal.trueMax());
            resources.addProperty("mojo", mojo.trueMax());
            resources.addProperty("willpower", willpower.trueMax());
            saveObj.add("resources", resources);
        }
        saveObj.add("affections", JsonUtils.JsonFromMap(affections));
        saveObj.add("attractions", JsonUtils.JsonFromMap(attractions));
        saveObj.add("attributes", JsonUtils.JsonFromMap(att));
        saveObj.add("outfit", JsonUtils.jsonFromCollection(outfitPlan));
        saveObj.add("closet", JsonUtils.jsonFromCollection(closet));
        saveObj.add("traits", JsonUtils.jsonFromCollection(traits));
        saveObj.add("body", body.save());
        saveObj.add("inventory", JsonUtils.JsonFromMap(inventory));
        saveObj.addProperty("human", human());
        saveObj.add("flags", JsonUtils.JsonFromMap(flags));
        saveObj.add("levelUps", JsonUtils.JsonFromMap(levelPlan));
        saveObj.add("growth", JsonUtils.getGson().toJsonTree(growth));
        // TODO eventually this should load any status, for now just load addictions
        JsonArray status = new JsonArray();
        getAddictions().stream().map(Addiction::saveToJson).forEach(status::add);
        saveObj.add("status", status);
        saveInternal();
        return saveObj;
    }

    private void saveInternal() {

    }

    public abstract String getType();

    public void load(JsonObject object) {
        name = object.get("name").getAsString();
        level = object.get("level").getAsInt();
        rank = object.get("rank").getAsInt();
        xp = object.get("xp").getAsInt();
        if (object.has("growth")) {
            growth = JsonUtils.getGson().fromJson(object.get("growth"), Growth.class);
            growth.removeNullTraits();
        }
        money = object.get("money").getAsInt();
        {
            JsonObject resources = object.getAsJsonObject("resources");
            stamina.setMax(resources.get("stamina").getAsFloat());
            arousal.setMax(resources.get("arousal").getAsFloat());
            mojo.setMax(resources.get("mojo").getAsFloat());
            willpower.setMax(resources.get("willpower").getAsFloat());
        }

        affections = JsonUtils.mapFromJson(object.getAsJsonObject("affections"), String.class, Integer.class);
        attractions = JsonUtils.mapFromJson(object.getAsJsonObject("attractions"), String.class, Integer.class);

        {
            outfitPlan.clear();
            JsonUtils.getOptionalArray(object, "outfit").ifPresent(this::addClothes);
        }
        {
            closet = JsonUtils.collectionFromJson(object.getAsJsonArray("closet"), Clothing.class).stream()
                            .filter(Objects::nonNull).collect(Collectors.toSet());
        }
        {
            traits = JsonUtils.collectionFromJson(object.getAsJsonArray("traits"), Trait.class).stream()
                            .filter(Objects::nonNull).collect(Collectors.toCollection(CopyOnWriteArrayList::new));
            if (getType().equals("Airi"))
                traits.remove(Trait.slime);
        }
        
        body = Body.load(object.getAsJsonObject("body"), this);
        att = JsonUtils.mapFromJson(object.getAsJsonObject("attributes"), Attribute.class, Integer.class);
        inventory = JsonUtils.mapFromJson(object.getAsJsonObject("inventory"), Item.class, Integer.class);

        flags.clear();
        JsonUtils.getOptionalObject(object, "flags")
                        .ifPresent(obj -> flags.putAll(JsonUtils.mapFromJson(obj, String.class, Integer.class)));
        if (object.has("levelUps")) {
            levelPlan = JsonUtils.mapFromJson(object.getAsJsonObject("levelUps"), Integer.class, LevelUpData.class);
        } else {
            levelPlan = new HashMap<>();
        }
        List<Status> statusList = new ArrayList<>();
        for (JsonElement element : Optional.ofNullable(object.getAsJsonArray("status")).orElse(new JsonArray())) {
            try {
                Addiction addiction = Addiction.load(this, element.getAsJsonObject());
                if (addiction != null) {
                    statusList.add(addiction);
                }
            } catch (Exception e) {
                System.err.println("Failed to load status:");
                System.err.println(JsonUtils.getGson().toJson(element));
                throw e;
            }
        }
        status = new CopyOnWriteArrayList<>(statusList);
        change();
        SkillPool.learnSkills(this);
    }

    private void addClothes(JsonArray array) {
        outfitPlan.addByID(JsonUtils.stringsFromJson(array));
    }

    public abstract void afterParty();

    private boolean checkOrgasm() {
        return getArousal().isFull() && !is(Stsflag.orgasmseal) && pleasured;
    }

    public void doOrgasm(Combat c, Character opponent, BodyPart selfPart, BodyPart opponentPart) {
        int total = 1;
        if (this != opponent && opponent != null) {
            if (opponent.has(Trait.carnalvirtuoso)) {
                total++;
            }
            if (opponent.has(Trait.intensesuction) && (outfit.has(ClothingTrait.harpoonDildo)
                            || outfit.has(ClothingTrait.harpoonOnahole)) && Random.random(3) == 0) {
                total++;
            }
        }
        for (int i = 1; i <= total; i++) {
            resolveOrgasm(c, opponent, selfPart, opponentPart, i, total);
        }
    }

    private static final OrgasmicTighten TIGHTEN_SKILL = new OrgasmicTighten(null);
    private static final OrgasmicThrust THRUST_SKILL = new OrgasmicThrust(null);

    protected void resolveOrgasm(Combat c, Character opponent, BodyPart selfPart, BodyPart opponentPart, int times, int totalTimes) {
        if (has(Trait.HiveMind) && !c.getPetsFor(this).isEmpty()) {
            // don't use opponent, use opponent of the current combat
            c.write(this, Formatter.format("Just as {self:subject-action:seem} about to orgasm, {self:possessive} expression shifts. "
                            + "{self:POSSESSIVE} eyes dulls and {self:possessive} expressions slacken."
                            + "{other:if-human: Shit you've seen this before, she somehow switched bodies with one of her clones!}"
                            , this, c.getOpponent(this)));
            while (!c.getPetsFor(this).isEmpty() && checkOrgasm()) {
                int amount = Math.min(getArousal().get(), getArousal().max());
                getArousal().reduce(amount);
                Character pet = c.getPetsFor(this).iterator().next();
                pet.arouse(amount, c, Formatter.format("({self:master}'s orgasm)", this, opponent));
                pet.doOrgasm(c, pet, null, null);
            }
            c.setStance(new Neutral(this, c.getOpponent(this)));
            if (!checkOrgasm()) {
                return;
            } else {
                c.write(this, Formatter.format("{other:if-human:Luckily }{self:pronoun} didn't seem to be able to shunt all {self:possessive arousal} "
                                + "into {self:possessive clones, and rapidly reaches the peak anyways."
                                , this, c.getOpponent(this)));
            }
        }

        String orgasmLiner = "<b>" + orgasmLiner(c, opponent == null ? c.getOpponent(this) : opponent) + "</b>";
        String opponentOrgasmLiner = (opponent == null || opponent == this || opponent.isPet()) ? "" : 
            "<b>" + opponent.makeOrgasmLiner(c, this) + "</b>";
        orgasmed = true;
        if (times == 1) {
            c.write(this, "<br/>");
        }
        if (opponent == this) {
            resolvePreOrgasmForSolo(c, opponent, selfPart, times);
        } else {
            resolvePreOrgasmForOpponent(c, opponent, selfPart, opponentPart, times);
        }
        int overflow = arousal.getOverflow();
        c.write(this, String.format("<font color=%s>%s<font color='white'> arousal overflow", AROUSAL_OVERFLOW.rgbHTML(), overflow));
        if (this != opponent) {
            resolvePostOrgasmForOpponent(c, opponent, selfPart, opponentPart);
        }
        getArousal().empty();
        if (has(Trait.insatiable)) {
            arousal.restore((int) (arousal.max() * .2));
        }
        if (is(Stsflag.feral)) {
            arousal.restore(arousal.max() / 2);
        }
        float extra = 25.0f * overflow / (arousal.max());

        loseWillpower(c, getOrgasmWillpowerLoss(), Math.round(extra), true, "");
        if (has(Trait.sexualDynamo)) {
            c.write(this, Formatter.format("{self:NAME-POSSESSIVE} climax makes {self:direct-object} positively gleam with erotic splendor; "
                            + "{self:possessive} every move seems more seductive than ever.", this, opponent));
            add(c, new AttributeBuff(this, Attribute.seduction, 5, 10));
        }
        if (has(Trait.lastStand)) {
            OrgasmicTighten tightenCopy = (OrgasmicTighten) TIGHTEN_SKILL.copy(this);
            OrgasmicThrust thrustCopy = (OrgasmicThrust) THRUST_SKILL.copy(this);
            if (tightenCopy.usable(c, opponent)) {
                tightenCopy.resolve(c, opponent);
            }
            if (thrustCopy.usable(c, opponent)) {
                thrustCopy.resolve(c, opponent);
            }
        }
        if (this != opponent && times == totalTimes && canRespond()) {
            c.write(this, orgasmLiner);
            c.write(opponent, opponentOrgasmLiner);
        }

        if (has(Trait.nymphomania) && (
                        Random.random(100) < Math.sqrt(get(Attribute.nymphomania) + get(Attribute.animism)) * 10) && !getWillpower().isEmpty() && times == totalTimes) {
            if (human()) {
                c.write("Cumming actually made you feel kind of refreshed, albeit with a burning desire for more.");
            } else {
                c.write(Formatter.format(
                                "After {self:subject} comes down from {self:possessive} orgasmic high, {self:pronoun} doesn't look satisfied at all. There's a mad glint in {self:possessive} eye that seems to be endlessly asking for more.",
                                this, opponent));
            }
            restoreWillpower(c, 5 + Math.min((get(Attribute.animism) + get(Attribute.nymphomania)) / 5, 15));
        }

        if (times == totalTimes) {
            List<Status> purgedStatuses = getStatuses().stream()
                            .filter(status -> (status.mindgames() && status.flags().contains(Stsflag.purgable)) || status.flags().contains(Stsflag.orgasmPurged))
                            .collect(Collectors.toList());
            if (!purgedStatuses.isEmpty()){
                if (human()) {
                    c.write(this, "<b>Your mind clears up after your release.</b>");
                } else {
                    c.write(this, "<b>You see the light of reason return to " + nameDirectObject() + "  after " + possessiveAdjective() + " release.</b>");
                }
                purgedStatuses.forEach(this::removeStatus);
            }
        }

        if (checkAddiction(AddictionType.CORRUPTION, opponent) && selfPart != null && opponentPart != null) {
            if (c.getStance().havingSex(c, this) && (c.getCombatantData(this).getIntegerFlag("ChoseToFuck") == 1)) {
                c.write(this, Formatter.format("{self:NAME-POSSESSIVE} willing sacrifice to {other:name-do} greatly reinforces"
                                + " the corruption inside of {self:direct-object}.", this, opponent));
                addict(c, AddictionType.CORRUPTION, opponent, Addiction.HIGH_INCREASE);
            }
            if (opponent != null && opponent.has(Trait.TotalSubjugation)
                            && c.getStance().en == Stance.succubusembrace) {
                c.write(this, Formatter.format(
                                "The succubus takes advantage of {self:name-possessive} moment of vulnerability and overwhelms {self:posssessive} mind with {other:possessive} soul-corroding lips.",
                                this, opponent));
                addict(c, AddictionType.CORRUPTION, opponent, Addiction.HIGH_INCREASE);
            }
        }
        if (checkAddiction(AddictionType.ZEAL, opponent) && selfPart != null && opponentPart != null 
                        && selfPart.isType("cock")) {
            c.write(this, Formatter.format("Experiencing so much pleasure inside of {other:name-do} reinforces {self:name-possessive} faith in the lovely goddess.", this, opponent));
            addict(c, AddictionType.ZEAL, opponent, Addiction.MED_INCREASE);
        }
        if (checkAddiction(AddictionType.ZEAL, opponent) && selfPart != null && opponentPart != null 
                        && opponentPart.isType("cock") && (selfPart
                        .isType("pussy") || selfPart.isType("ass"))) {
            c.write(this, Formatter.format("Experiencing so much pleasure from {other:name-possessive} cock inside {self:direct-object} reinforces {self:name-possessive} faith.", this, opponent));
            addict(c, AddictionType.ZEAL, opponent, Addiction.MED_INCREASE);
        }
        if (checkAddiction(AddictionType.BREEDER, opponent)) {
            // Clear combat addiction
            unaddictCombat(AddictionType.BREEDER, opponent, 1.f, c);
        }
        if (checkAddiction(AddictionType.DOMINANCE, opponent) && c.getStance().dom(opponent)) {
            if (opponent != null) {
                c.write(this, "Getting dominated by " + opponent.nameDirectObject() +" seems to excite " + nameDirectObject() + " even more.");
            }
            addict(c, AddictionType.DOMINANCE, opponent, Addiction.LOW_INCREASE);
        }
        orgasms += 1;
    }

    private void resolvePreOrgasmForSolo(Combat c, Character opponent, BodyPart selfPart, int times) {
        if (selfPart != null && selfPart.isType("cock")) {
            if (times == 1) {
                c.write(this, Formatter.format(
                                "<b>{self:NAME-POSSESSIVE} back arches as thick ropes of jizz fire from {self:possessive} dick and land on {self:reflective}.</b>",
                                this, opponent));
            } else {
                c.write(this, Formatter.format(
                                "<b>{other:SUBJECT-ACTION:expertly coax|expertly coaxes} yet another orgasm from {self:name-do}, leaving {self:direct-object} completely spent.</b>",
                                this, opponent));
            }
        } else {
            if (times == 1) {
                c.write(this, Formatter.format(
                                "<b>{self:SUBJECT-ACTION:shudder|shudders} as {self:pronoun} {self:action:bring|brings} {self:reflective} to a toe-curling climax.</b>",
                                this, opponent));
            } else {
                c.write(this, Formatter.format(
                                "<b>{other:SUBJECT-ACTION:expertly coax|expertly coaxes} yet another orgasm from {self:name-do}, leaving {self:direct-object} completely spent.</b>",
                                this, opponent));
            }
        }
    }

    private void resolvePreOrgasmForOpponent(Combat c, Character opponent, BodyPart selfPart, BodyPart opponentPart,
                    int times) {
        if (c.getStance().inserted(this) && !has(Trait.strapped)) {
            Character partner = c.getStance().getPenetratedCharacter(c, this);
            BodyPart holePart = Random.pickRandom(c.getStance().getPartsFor(c, partner, this)).orElse(null);
            if (times == 1) {
                String hole = "pulsing hole";
                if (holePart != null && holePart.isType("breasts")) {
                    hole = "cleavage";
                } else if (holePart != null && holePart.isType("mouth")) {
                    hole = "hungry mouth";
                }
                c.write(this, Formatter.format(
                                "<b>{self:SUBJECT-ACTION:tense|tenses} up as {self:possessive} hips wildly buck against {other:name-do}. In no time, {self:possessive} hot seed spills into {other:possessive} %s.</b>",
                                this, partner, hole));
            } else {
                c.write(this, Formatter.format(
                                "<b>{other:NAME-POSSESSIVE} devilish orfice does not let up, and {other:possessive} intense actions somehow force {self:name-do} to cum again instantly.</b>",
                                this, partner));
            }
            Optional<BodyPart> opponentHolePart = Random.pickRandom(c.getStance().getPartsFor(c, opponent, this));
            opponentHolePart.ifPresent(bodyPart -> partner.body.receiveCum(c, this, bodyPart));
        } else if (selfPart != null && selfPart.isType("cock") && opponentPart != null
                        && !opponentPart.isType("none")) {
            if (times == 1) {
                c.write(this, Formatter.format(
                                "<b>{self:NAME-POSSESSIVE} back arches as thick ropes of jizz fire from {self:possessive} dick and land on {other:name-possessive} "
                                                + opponentPart.describe(opponent) + ".</b>",
                                this, opponent));
            } else {
                c.write(this, Formatter.format(
                                "<b>{other:SUBJECT-ACTION:expertly coax|expertly coaxes} yet another orgasm from {self:name-do}, leaving {self:direct-object} completely spent.</b>",
                                this, opponent));
            }
            opponent.body.receiveCum(c, this, opponentPart);
        } else {
            if (times == 1) {
                c.write(this, Formatter.format(
                                "<b>{self:SUBJECT-ACTION:shudder|shudders} as {other:subject-action:bring|brings} {self:direct-object} to a toe-curling climax.</b>",
                                this, opponent));
            } else {
                c.write(this, Formatter.format(
                                "<b>{other:SUBJECT-ACTION:expertly coax|expertly coaxes} yet another orgasm from {self:name-do}, leaving {self:direct-object} completely spent.</b>",
                                this, opponent));
            }
        }
        if (opponent.has(Trait.mindcontroller) && cloned == 0) {
            MindControl.Result res = new MindControl.Result(this, opponent, c.getStance());
            String message = res.getDescription();
            if (res.hasSucceeded()) {
                if (opponent.has(Trait.EyeOpener) && outfit.has(ClothingTrait.harpoonDildo)) {
                    message += "Below, the vibrations of the dildo reach a powerful crescendo,"
                                    + " and your eyes open wide in shock, a perfect target for "
                                    + " what's coming next.";
                    addict(c, AddictionType.MIND_CONTROL, opponent, Addiction.LOW_INCREASE);
                } else if (opponent.has(Trait.EyeOpener) && outfit.has(ClothingTrait.harpoonOnahole)) {
                    message += "The warm sheath around your dick suddenly tightens, pulling incredibly"
                                    + ", almost painfully tight around the shaft. At the same time, it starts"
                                    + " vibrating powerfully. The combined assault causes your eyes to open"
                                    + " wide and defenseless."; 
                    addict(c, AddictionType.MIND_CONTROL, opponent, Addiction.LOW_INCREASE);
                }
                message += "While your senses are overwhelmed by your violent orgasm, the deep pools of Mara's eyes"
                                + " swirl and dance. You helplessly stare at the intricate movements and feel a strong"
                                + " pressure on your mind as you do. When your orgasm dies down, so do the dancing patterns."
                                + " With a satisfied smirk, Mara tells you to lift an arm. Before you have even processed"
                                + " her words, you discover that your right arm is sticking straight up into the air. This"
                                + " is probably really bad.";
                addict(c, AddictionType.MIND_CONTROL, opponent, Addiction.MED_INCREASE);
            }
            c.write(this, message);
        }
    }

    public String getRandomLineFor(String lineType, Combat c, Character target) {
        return "";
    }

    private void resolvePostOrgasmForOpponent(Combat c, Character opponent, BodyPart selfPart, BodyPart opponentPart) {
        if (selfPart != null && opponentPart != null) {
            selfPart.onOrgasmWith(c, this, opponent, opponentPart, true);
            opponentPart.onOrgasmWith(c, opponent, this, selfPart, false);
        } else if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.printf("Could not process %s's orgasm against %s: self=%s, opp=%s, pos=%s", this, opponent,
                            selfPart, opponentPart, c.getStance());
        }
        body.getCurrentParts().forEach(part -> part.onOrgasm(c, this, opponent));

        if (opponent.has(Trait.erophage)) {
            c.write(Formatter.capitalizeFirstLetter("<b>" + opponent.subjectAction("flush", "flushes")
                            + " as the feedback from " + nameOrPossessivePronoun() + " orgasm feeds "
                            + opponent.possessiveAdjective() + " divine power.</b>"));
            opponent.add(c, new Alluring(opponent, 5));
            opponent.buildMojo(c, 100);
            if (c.getStance().inserted(this) && opponent.has(Trait.divinity)) {
                opponent.add(c, new DivineCharge(opponent, 1));
            }
        }
        if (opponent.has(Trait.sexualmomentum)) {
            c.write(Formatter.capitalizeFirstLetter(
                            "<b>" + opponent.subjectAction("are more composed", "seems more composed") + " as "
                                            + nameOrPossessivePronoun() + " forced orgasm goes straight to "
                                            + opponent.possessiveAdjective() + " ego.</b>"));
            opponent.restoreWillpower(c, 10 + Random.random(10));
        }
        if (opponent.has(Trait.leveldrainer) && (!has(Trait.leveldrainer) || opponent.has(Trait.IndiscriminateThief))
                        && (((c.getStance().penetratedBy(c, opponent, this) || c.getStance().penetratedBy(c, this, opponent))
                                        && !has(Trait.strapped)
                                        && !opponent.has(Trait.strapped))
                        || c.getStance().en == Stance.trib)) {
            if (getLevel() > 1 && (!c.getCombatantData(opponent).getBooleanFlag("has_drained") 
                            || opponent.has(Trait.ExpertLevelDrainer))) {
                c.getCombatantData(opponent).toggleFlagOn("has_drained", true);
                if (c.getStance().penetratedBy(c, opponent, this)) {
                    c.write(opponent, Formatter.format("<b>{other:NAME-POSSESSIVE} %s contracts around {self:name-possessive} %s, reinforcing"
                            + " {self:possessive} orgasm and drawing upon {self:possessive} very strength and experience. Once it's over, {other:pronoun-action:are}"
                                                    + " left considerably more powerful at {self:possessive} expense.</b>",
                                    this, opponent, c.getStance().insertablePartFor(c, opponent, this).describe(opponent),
                                    c.getStance().insertedPartFor(c, this).describe(this)));
                } else if (c.getStance().penetratedBy(c, this, opponent)) {
                    c.write(opponent, Formatter.format("<b>{other:NAME-POSSESSIVE} cock pistons rapidly into {self:name-do} as {self:subject-action:cum|cums}, "
                                    + "drawing out {self:possessive} very strength and experience on every return stroke. "
                                    + "Once it's over, {other:pronoun-action:are} left considerably more powerful at {self:possessive} expense.</b>",
                                    this, opponent));
                } else {
                    c.write(opponent, Formatter.format("<b>{other:NAME-POSSESSIVE} greedy {other:body-part:pussy} sucks itself tightly to {self:name-possessive} {self:body-part:pussy}, "
                                    + "drawing in {self:possessive} very strength and experience along the pleasures of {self:possessive} orgasm. "
                                    + "Once it's over, {other:pronoun-action:are|is} left considerably more powerful at {self:possessive} expense.</b>",
                                    this, opponent));
                }
                String leveldrainLiner = opponent.getRandomLineFor(CharacterLine.LEVEL_DRAIN_LINER, c, this);
                if (!leveldrainLiner.isEmpty()) {
                    c.write(opponent, leveldrainLiner);
                }
                int gained;
                if (Flag.checkFlag(Flag.hardmode)) {
                    drain(c, opponent, 30 + Random.random(50), MeterType.STAMINA);
                    gained = opponent.getXPReqToNextLevel();
                } else {
                    gained = opponent.getXPReqToNextLevel();
                }
                int xpStolen = getXP();
                c.write(dong());
                xp = Math.max(xp, Math.min(getXPReqToNextLevel() - 1, gained - xpStolen));
                opponent.gainXPPure(gained);
                opponent.spendXP();
            } else {
                c.write(opponent, String.format("<b>%s %s pulses, but fails to"
                                                + " draw in %s experience.</b>", Formatter.capitalizeFirstLetter(opponent.nameOrPossessivePronoun()),
                                opponent.body.getRandomPussy().describe(opponent),
                                nameOrPossessivePronoun()));
            }
        }
    }

    public void loseWillpower(Combat c, int i) {
        loseWillpower(c, i, 0, false, "");
    }

    public void loseWillpower(Combat c, int i, boolean fromOrgasm) {
        loseWillpower(c, i, 0, fromOrgasm, "");
    }

    public void loseWillpower(Combat c, int i, int extra, boolean fromOrgasm, String source) {
        int amt = i + extra;
        String reduced = "";
        if (has(Trait.strongwilled) && fromOrgasm) {
            amt = amt * 2 / 3 + 1;
            reduced = " (Strong-willed)";
        }
        if (is(Stsflag.feral) && fromOrgasm) {
            amt = amt / 2;
            reduced = " (Feral)";
        }
        int old = willpower.get();
        willpower.reduce(amt);
        if (c != null) {
            c.writeSystemMessage(
                            String.format("%s lost <font color=%s>%s<font color='white'> willpower" + reduced + "%s.",
                                            Formatter.capitalizeFirstLetter(subject()), WILLPOWER_LOSS.rgbHTML(),
                                            extra == 0 ? Integer.toString(amt) : i + "+" + extra + " (" + amt + ")",
                                            source));
        } else if (human()) {
            GUI.gui.systemMessage(String
                            .format("%s lost <font color=%s>%d<font color='white'> willpower" + reduced
                                            + "%s.", subject(), WILLPOWER_LOSS.rgbHTML(), amt, source));
        }
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.printf("will power reduced from %d to %d\n", old, willpower.get());
        }
    }

    public void restoreWillpower(Combat c, int i) {
        willpower.restore(i);
        c.writeSystemMessage(String.format("%s regained <font color=%s>%d<font color='white'> willpower.", subject(),
                        WILLPOWER_GAIN.rgbHTML(), i));
    }

    private static List<String> ANGEL_APOSTLES_QUOTES = Arrays.asList(
                    "The space around {self:name-do} starts abruptly shimmering. "
                    + "{other:SUBJECT-ACTION:look|looks} up in alarm, but {self:subject} just chuckles. "
                    + "<i>\"{other:NAME}, a Goddess should have followers don't you agree? Let's see how you fare in a ménage-à-trois, yes?\"</i>",
                    "A soft light starts growing around {self:name-do}, causing {other:subject} to pause. "
                    + "{self:SUBJECT} holds up her arms as if to welcome someone. "
                    + "<i>\"Sex with just two is just so <b>lonely</b> don't you think? Let's spice it up a bit!\"</i>",
                    "Suddenly, several pillars of light descend from the sky and converge in front of {self:name-do} in the form of a humanoid figure. "
                    + "Not knowing what's going on, {other:subject-action:cautiously approach|cautiously approaches}. "
                    + "{self:SUBJECT} reaches into the light and holds the figure's hands. "
                    + "<i>\"See {other:name}, I'm not a greedy {self:girl}. I can share with my friends.\"</i>"
                    );

    private void handleInserted(Combat c) {
        List<Character> partners = c.getStance().getAllPartners(c, this);
        partners.forEach(opponent -> {
            Iterator<BodyPart> selfOrganIt;
            Iterator<BodyPart> otherOrganIt;
            selfOrganIt = c.getStance().getPartsFor(c, this, opponent).iterator();
            otherOrganIt = c.getStance().getPartsFor(c, opponent, this).iterator();
            if (selfOrganIt.hasNext() && otherOrganIt.hasNext()) {
                BodyPart selfOrgan = selfOrganIt.next();
                BodyPart otherOrgan = otherOrganIt.next();
                if (has(Trait.energydrain) && selfOrgan != null && otherOrgan != null) {
                    c.write(this, Formatter.format(
                                    "{self:NAME-POSSESSIVE} body glows purple as {other:subject-action:feel|feels} {other:possessive} very spirit drained into {self:possessive} "
                                                    + selfOrgan.describe(this) + " through your connection.",
                                    this, opponent));
                    int m = Random.random(5) + 5;
                    opponent.drain(c, this, (int) DamageType.drain.modifyDamage(this, opponent, m), MeterType.STAMINA);
                }
                body.tickHolding(c, opponent, selfOrgan, otherOrgan);
            }
        });
    }

    public void eot(Combat c, Character opponent) {
        dropStatus(c, opponent);
        tick(c);
        List<String> removed = new ArrayList<>();
        for (String s : cooldowns.keySet()) {
            if (cooldowns.get(s) <= 1) {
                removed.add(s);
            } else {
                cooldowns.put(s, cooldowns.get(s) - 1);
            }
        }
        for (String s : removed) {
            cooldowns.remove(s);
        }
        handleInserted(c);
        if (outfit.has(ClothingTrait.tentacleSuit)) {
            c.write(this, Formatter.format("The tentacle suit squirms against {self:name-possessive} body.", this,
                            opponent));
            if (hasBreasts()) {
                TentaclePart.pleasureWithTentacles(c, this, 5, body.getRandomBreasts());
            }
            TentaclePart.pleasureWithTentacles(c, this, 5, body.getRandom("skin"));
        }
        if (outfit.has(ClothingTrait.tentacleUnderwear)) {
            String undieName = "underwear";
            if (hasPussy()) {
                undieName = "panties";
            }
            c.write(this, Formatter.format("The tentacle " + undieName + " squirms against {self:name-possessive} crotch.",
                            this, opponent));
            if (hasDick()) {
                TentaclePart.pleasureWithTentacles(c, this, 5, body.getRandomCock());
                body.pleasure(null, null, body.getRandom("cock"), 5, c);
            }
            if (hasBalls()) {
                TentaclePart.pleasureWithTentacles(c, this, 5, body.getRandom("balls"));
            }
            if (hasPussy()) {
                TentaclePart.pleasureWithTentacles(c, this, 5, body.getRandomPussy());
            }
            TentaclePart.pleasureWithTentacles(c, this, 5, body.getRandomAss());
        }
        if (outfit.has(ClothingTrait.harpoonDildo)) {
            if (!hasPussy()) {
                c.write(Formatter.format("Since {self:name-possessive} pussy is now gone, the dildo that was stuck inside of it falls"
                                + " to the ground. {other:SUBJECT-ACTION:reel|reels} it back into its slot on"
                                + " {other:possessive} arm device.", this, opponent));
            } else {
                int damage = 5;
                if (opponent.has(Trait.pussyhandler)) {
                    damage += 2;
                }
                if (opponent.has(Trait.yank)) {
                    damage += 3;
                }
                if (opponent.has(Trait.conducivetoy)) {
                    damage += 3;
                }
                if (opponent.has(Trait.intensesuction)) {
                    damage += 3;
                }

                c.write(Formatter.format("{other:NAME-POSSESSIVE} harpoon dildo is still stuck in {self:name-possessive}"
                                + " {self:body-part:pussy}, vibrating against {self:possessive} walls.", this, opponent));
                body.pleasure(opponent, ToysPart.dildo, body.getRandomPussy(), damage, c);
            }
        }
        if (outfit.has(ClothingTrait.harpoonOnahole)) {
            if (!hasDick()) {
                c.write(Formatter.format("Since {self:name-possessive} dick is now gone, the onahole that was stuck onto it falls"
                                + " to the ground. {other:SUBJECT-ACTION:reel|reels} it back into its slot on"
                                + " {other:possessive} arm device.", this, opponent));
            } else {
                int damage = 5;
                if (opponent.has(Trait.dickhandler)) {
                    damage += 2;
                }
                if (opponent.has(Trait.yank)) {
                    damage += 3;
                }
                if (opponent.has(Trait.conducivetoy)) {
                    damage += 3;
                }
                if (opponent.has(Trait.intensesuction)) {
                    damage += 3;
                }
                
                c.write(Formatter.format("{other:NAME-POSSESSIVE} harpoon onahole is still stuck on {self:name-possessive}"
                                + " {self:body-part:cock}, vibrating against {self:possessive} shaft.", this, opponent));
                body.pleasure(opponent, ToysPart.onahole, body.getRandomCock(), damage, c);
            }
        }
        if (getPure(Attribute.animism) >= 4 && getArousal().percent() >= 50 && !is(Stsflag.feral)) {
            add(c, new Feral(this));
        }
        
        if (opponent.has(Trait.temptingass) && !is(Stsflag.frenzied)) {
            int chance = 20;
            chance += Math.max(0, Math.min(15, opponent.get(Attribute.seduction) - get(Attribute.seduction)));
            if (is(Stsflag.feral))
                chance += 10;
            if (is(Stsflag.charmed) || opponent.is(Stsflag.alluring))
                chance += 5;
            if (has(Trait.assmaster) || has(Trait.analFanatic))
                chance += 5;
            Optional<BodyFetish> fetish = body.getFetish("ass");
            if (fetish.isPresent() && opponent.has(Trait.bewitchingbottom)) {
                chance += 20 * fetish.get().magnitude;
            }
            if (chance >= Random.random(100)) {
                AssFuck fuck = new AssFuck(this);
                if (fuck.requirements(c, opponent) && fuck.usable(c, opponent)) {
                    c.write(opponent,
                                    Formatter.format("<b>The look of {other:name-possessive} ass,"
                                                    + " so easily within {self:possessive} reach, causes"
                                                    + " {self:subject} to involuntarily switch to autopilot."
                                                    + " {self:SUBJECT} simply {self:action:NEED|NEEDS} that ass.</b>",
                                    this, opponent));
                    add(c, new Frenzied(this, 1));
                }
            }
        }

        pleasured = false;
        Optional<PetCharacter> randomOpponentPetOptional = Random.pickRandom(c.getPetsFor(opponent));
        if (randomOpponentPetOptional.isPresent()) {
            PetCharacter pet = randomOpponentPetOptional.get();
            boolean weakenBetter = DamageType.physical.modifyDamage(this, pet, 100) / pet.getStamina().remaining()
                            > 100 / pet.getStamina().remaining();
            if (canAct() && c.getStance().mobile(this) && pet.roll(this, 20)) {
                if (weakenBetter) {
                    c.write(Formatter.format("{self:SUBJECT-ACTION:focus|focuses} {self:possessive} attentions on {other:name-do}, "
                                    + "thoroughly exhausting {other:direct-object} in a game of cat and mouse.", this, pet));
                    pet.weaken(c, (int) DamageType.physical.modifyDamage(this, pet, Random.random(10, 20)));
                } else {
                    c.write(Formatter.format("{self:SUBJECT-ACTION:focus|focuses} {self:possessive} attentions on {other:name-do}, "
                                    + "harassing and toying with {other:possessive} body as much as {self:pronoun} can.", this, pet));
                    pet.body.pleasure(this, body.getRandom("hands"), pet.body.getRandomGenital(), Random.random(10, 20), c);
                }
            }
        }

        if (canRespond() && has(Trait.apostles) && c.getCombatantData(this).getIntegerFlag(APOSTLES_COUNT) >= 4) {
            List<Personality> possibleApostles = Stream.of(new Mei(), new Caroline(), new Sarah())
                            .filter(possible -> c.getOtherCombatantTypes().stream().noneMatch(type -> type.equals(possible.getType())))
                            .collect(Collectors.toList());

            Optional<CharacterPet> petOptional = Random.pickRandom(possibleApostles).map(Personality::getCharacter)
                            .map(character -> new CharacterPet(this, character, getLevel() - 5, getLevel() / 4));
            if (petOptional.isPresent()) {
                CharacterPet pet = petOptional.get();
                c.write(this, Formatter.format(Random.pickRandom(ANGEL_APOSTLES_QUOTES).orElse(""), this, opponent));
                c.addPet(this, pet.getSelf());
                c.getCombatantData(this).setIntegerFlag(APOSTLES_COUNT, 0);
            }
        }
        if (c.getPetsFor(this).size() < getPetLimit()) {
            c.getCombatantData(this).setIntegerFlag(APOSTLES_COUNT, c.getCombatantData(this).getIntegerFlag(APOSTLES_COUNT) + 1);
        }

        if (has(Trait.Rut) && Random.random(100) < ((getArousal().percent() - 25) / 2) && !is(Stsflag.frenzied)) {
            c.write(this, Formatter.format("<b>{self:NAME-POSSESSIVE} eyes dilate and {self:possessive} body flushes as {self:pronoun-action:descend|descends} into a mating frenzy!</b>", this, opponent));
            add(c, new Frenzied(this, 3, true));
        }
    }

    public String orgasmLiner(Combat c, Character target) {
        return "";
    }

    public String makeOrgasmLiner(Combat c, Character target) {
        return "";
    }

    private int getOrgasmWillpowerLoss() {
        return 25;
    }

    public abstract void emote(Emotion emo, int amt);

    public void learn(Skill copy) {
        skills.addIfAbsent(copy.copy(this));
    }

    public void forget(Skill copy) {
        getSkills().remove(copy);
    }

    public boolean stealthCheck(int perception) {
        return checkVsDc(Attribute.cunning, Random.random(20) + perception) || state == State.hidden;
    }

    public boolean spotCheck(Character checked) {
        if (bound()) {
            return false;
        }
        int dc = checked.get(Attribute.cunning) / 3;
        if (checked.state == State.hidden) {
            dc += (checked.get(Attribute.cunning) * 2 / 3) + 20;
        }
        if (checked.has(Trait.Sneaky)) {
            dc += 20;
        }
        dc -= dc * 5 / Math.max(1, get(Attribute.perception));
        return checkVsDc(Attribute.cunning, dc);
    }

    public void travel(Area dest) {
        state = State.ready;
        location.exit(this);
        location = dest;
        dest.enter(this);
        if (dest.name.isEmpty()) {
            throw new RuntimeException("empty location");
        }
    }

    public void flee(Area location2) {
        Area[] adjacent = location2.adjacent.toArray(new Area[0]);
        travel(adjacent[Random.random(adjacent.length)]);
        location2.endEncounter();
    }

    public void upkeep() {
        getTraits().forEach(trait -> {
            if (trait.status != null) {
                Status newStatus = trait.status.instance(this, null);
                if (!has(newStatus)) {
                    addNonCombat(newStatus);
                }
            }
        });
        regen();
        tick(null);
        if (has(Trait.Confident)) {
            willpower.restore(10);
            mojo.reduce(5);
        } else {
            willpower.restore(5);
            mojo.reduce(10);
        }
        if (has(Trait.exhibitionist) && mostlyNude()) {
            mojo.restore(2);
        }
        dropStatus(null, null);
        if (has(Trait.QuickRecovery)) {
            heal(null, Random.random(4, 7), " (Quick Recovery)");
        }
        update();
        notifyObservers();
    }
    
    public String debugMessage(Combat c) {
        String mood;
        if (this instanceof NPC) { // useOfInstanceOfWithThis
            mood = "mood: " + ((NPC) this).mood.toString();
        } else {
            mood = "";
        }
        return String.format("[%s] %s s: %d/%d a: %d/%d m: %d/%d w: %d/%d c:%d f:%f", name, mood, stamina.getReal(),
                        stamina.max(), arousal.getReal(), arousal.max(), mojo.getReal(), mojo.max(),
                        willpower.getReal(), willpower.max(), outfit.getEquipped().size(), getFitness(c));
    }

    public void gain(Item item) {
        gain(item, 1);
    }

    public void remove(Item item) {
        gain(item, -1);
    }

    public void gain(Clothing item) {
        closet.add(item);
        setChanged();
    }

    private void gainIfAbsent(Clothing clothing) {
            if (!has(clothing)) {
                gain(clothing);
            }
    }

    public void gainIfAbsent(String clothingID) {
        Optional<Clothing> clothing = ClothingTable.getByID(clothingID);
        if (clothing.isPresent()) {
            this.gainIfAbsent(clothing.get());
        } else {
            System.err.println("Unknown clothing ID " + clothingID + ".");
        }
    }

    public void gain(Item item, int q) {
        int amt = 0;
        if (inventory.containsKey(item)) {
            amt = count(item);
        }
        inventory.put(item, Math.max(0, amt + q));
        setChanged();
    }

    public boolean has(Item item) {
        return has(item, 1);
    }

    public boolean has(Item item, int quantity) {
        return inventory.containsKey(item) && inventory.get(item) >= quantity;
    }

    void unequipAllClothing() {
        closet.addAll(outfitPlan);
        outfitPlan.clear();
        change();
    }

    public boolean has(Clothing item) {
        return closet.contains(item) || outfit.getEquipped().contains(item);
    }

    public void consume(Item item, int quantity) {
        consume(item, quantity, true);
    }

    public void consume(Item item, int quantity, boolean canBeResourceful) {
        if (canBeResourceful && has(Trait.resourceful) && Random.random(5) == 0) {
            quantity--;
        }
        if (inventory.containsKey(item)) {
            gain(item, -quantity);
        }
    }

    public int count(Item item) {
        if (inventory.containsKey(item)) {
            return inventory.get(item);
        }
        return 0;
    }

    public void chargeBattery() {
        int power = count(Item.Battery);
        if (power < 20) {
            gain(Item.Battery, 20 - power);
        }
    }

    public void defeated(Character victor) {
        mercy.addIfAbsent(victor.getType());
    }

    public void resupply() {
        for (String victorType : mercy) {
            Character victor = GameState.gameState.characterPool.getCharacterByType(victorType);
            victor.bounty(has(Trait.event) ? 5 : 1, victor);
        }
        mercy.clear();
        change();
        state = State.ready;
        getWillpower().fill();
        if (location().present.size() > 1) {
            if (location().id() == Movement.dorm) {
                if (Match.getMatch().gps("Quad").present.isEmpty()) {
                    if (human()) {
                        GUI.gui
                                        .message("You hear your opponents searching around the dorm, so once you finish changing, you hop out the window and head to the quad.");
                    }
                    travel(Match.getMatch().gps("Quad"));
                } else {
                    if (human()) {
                        GUI.gui
                                        .message("You hear your opponents searching around the dorm, so once you finish changing, you quietly move downstairs to the laundry room.");
                    }
                    travel(Match.getMatch().gps("Laundry"));
                }
            }
            if (location().id() == Movement.union) {
                if (Match.getMatch().gps("Quad").present.isEmpty()) {
                    if (human()) {
                        GUI.gui
                                        .message("You don't want to be ambushed leaving the student union, so once you finish changing, you hop out the window and head to the quad.");
                    }
                    travel(Match.getMatch().gps("Quad"));
                } else {
                    if (human()) {
                        GUI.gui
                                        .message("You don't want to be ambushed leaving the student union, so once you finish changing, you sneak out the back door and head to the pool.");
                    }
                    travel(Match.getMatch().gps("Pool"));
                }
            }
        }
    }

    public void finishMatch() {
        for (String victorType : mercy) {
            Character victor = GameState.gameState.characterPool.getCharacterByType(victorType);
            victor.bounty(has(Trait.event) ? 5 : 1, victor);
        }
        GUI.gui.clearImage();
        mercy.clear();
        change();
        clearStatus();
        temporaryAddedTraits.clear();
        temporaryRemovedTraits.clear();
        body.clearReplacements();
        getStamina().fill();
        getArousal().empty();
        getMojo().empty();
    }

    public void place(Area loc) {
        location = loc;
        loc.enter(this);
        if (loc.name.isEmpty()) {
            throw new RuntimeException("empty location");
        }
    }

    private void bounty(int points, Character victor) {
        int score = points;
        if (Flag.checkFlag(Flag.FTC) && points == 1) {
            FTCMatch match = (FTCMatch) Match.getMatch();
            if (match.isPrey(this)) {
                score = 3;
            } else if (!match.isPrey(victor)) {
                score = 2;
            } else {
                score = 0; // Hunter beating prey gets no points, only for flag.
            }
        }
        Match.getMatch().score(this, score);
    }

    /**
     * This character(p1) is eligible to fight another character(p2) if p1 is not resupplying and p2 is not on p1's mercy list.
     * @param p2 The character to fight.
     * @return Whether p1 is eligible to fight p2.
     */
    public boolean eligible(Character p2) {
        // Whether FTC match eligibility conditions are met, if applicable
        boolean ftc = true;
        if (Flag.checkFlag(Flag.FTC)) {
            FTCMatch match = (FTCMatch) Match.getMatch();
            ftc = !match.inGracePeriod() || (!match.isPrey(this) && !match.isPrey(p2));
        }
        return ftc && !mercy.contains(p2.getType()) && state != State.resupplying;
    }

    public void setTrophy(Item trophy) {
        this.trophy = trophy;
    }

    public Item getTrophy() {
        return trophy;
    }

    public void bathe() {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.println("(Bathing) Purging " + getTrueName());
        }
        status.removeIf(s -> s.flags().contains(Stsflag.purgable));
        stamina.fill();
        state = State.ready;
        setChanged();
    }

    public void masturbate() {
        arousal.empty();
        state = State.ready;
        setChanged();
    }

    public void craft() {
        int roll = Random.random(15);
        if (checkVsDc(Attribute.cunning, 25)) {
            if (roll == 9) {
                gain(Item.Aphrodisiac);
                gain(Item.DisSol);
            } else if (roll >= 5) {
                gain(Item.Aphrodisiac);
            } else {
                gain(Item.Lubricant);
                gain(Item.Sedative);
            }
        } else if (checkVsDc(Attribute.cunning, 20)) {
            if (roll == 9) {
                gain(Item.Aphrodisiac);
            } else if (roll >= 7) {
                gain(Item.DisSol);
            } else if (roll >= 5) {
                gain(Item.Lubricant);
            } else if (roll >= 3) {
                gain(Item.Sedative);
            } else {
                gain(Item.EnergyDrink);
            }
        } else if (checkVsDc(Attribute.cunning, 15)) {
            if (roll == 9) {
                gain(Item.Aphrodisiac);
            } else if (roll >= 8) {
                gain(Item.DisSol);
            } else if (roll >= 7) {
                gain(Item.Lubricant);
            } else if (roll >= 6) {
                gain(Item.EnergyDrink);
            }
        } else {
            if (roll >= 7) {
                gain(Item.Lubricant);
            } else if (roll >= 5) {
                gain(Item.Sedative);
            }
        }
        state = State.ready;
        setChanged();
    }

    public void search() {
        int roll = Random.random(15);
        switch (roll) {
            case 9:
                gain(Item.Tripwire);
                gain(Item.Tripwire);
                break;
            case 8:
                gain(Item.ZipTie);
                gain(Item.ZipTie);
                gain(Item.ZipTie);
                break;
            case 7:
                gain(Item.Phone);
                break;
            case 6:
                gain(Item.Rope);
                break;
            case 5:
                gain(Item.Spring);
        }
        state = State.ready;

    }

    public abstract String challenge(Character other);

    public void delay(int i) {
        busy += i;
    }

    public abstract void promptTrap(Encounter fight, Character target, Trap trap);

    public int lvlBonus(Character opponent) {
        if (opponent.getLevel() > getLevel()) {
            return 12 * (opponent.getLevel() - getLevel());
        } else {
            return 0;
        }
    }

    public int getVictoryXP(Character opponent) {
        return 25 + lvlBonus(opponent);
    }

    int getAssistXP(Character opponent) {
        return 18 + lvlBonus(opponent);
    }

    public int getDefeatXP(Character opponent) {
        if (opponent.has(Trait.leveldrainer)) {
            return 0;
        }
        return 18 + lvlBonus(opponent);
    }

    public int getAttraction(Character other) {
        if (other == null) {
            System.err.println("Other is null");
            Thread.dumpStack();
            return 0;
        }
        return attractions.getOrDefault(other.getType(), 0);
    }

    public void gainAttraction(Character other, int x) {
        if (other == null) {
            System.err.println("Other is null");
            Thread.dumpStack();
            return;
        }
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.printf("%s gained attraction for %s\n", getTrueName(), other.getTrueName());
        }
        if (attractions.containsKey(other.getType())) {
            attractions.put(other.getType(), attractions.get(other.getType()) + x);
        } else {
            attractions.put(other.getType(), x);
        }
    }

    public Map<String, Integer> getAffections() {
        return Collections.unmodifiableMap(affections);
    }

    public int getAffection(Character other) {
        if (other == null) {
            System.err.println("Other is null");
            Thread.dumpStack();
            return 0;
        }

        return affections.getOrDefault(other.getType(), 0);
    }

    public void gainAffection(Character other, int x) {
        if (other == null) {
            System.err.println("Other is null");
            Thread.dumpStack();
            return;
        }
        if (other == this) {
            //skip narcissism.
            return;
        }

        if (other.has(Trait.affectionate) && Random.random(2) == 0) {
            x += 1;
        }

        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.printf("%s gained %d affection for %s\n", getTrueName(), x, other.getTrueName());
        }
        if (affections.containsKey(other.getType())) {
            affections.put(other.getType(), affections.get(other.getType()) + x);
        } else {
            affections.put(other.getType(), x);
        }
    }

    public int evasionBonus() {
        int ac = 0;
        for (Status s : getStatuses()) {
            ac += s.evade();
        }
        if (has(Trait.clairvoyance)) {
            ac += 5;
        }
        if (has(Trait.FeralAgility) && is(Stsflag.feral)) {
            ac += 5;
        }
        return ac;
    }

    private Collection<Status> getStatuses() {
        return status;
    }

    public int counterChance(Character opponent) {
        int counter = 3;
        // subtract some counter chance if the opponent is more cunning than you.
        // 1% decreased counter chance per 5 points of cunning over you.
        counter += Math.min(0, get(Attribute.cunning) - opponent.get(Attribute.cunning)) / 5;
        // increase counter chance by perception difference
        counter += get(Attribute.perception) - opponent.get(Attribute.perception);
        // 1% increased counter chance per 2 speed over your opponent.
        counter += getSpeedDifference(opponent) / 2;
        for (Status s : getStatuses()) {
            counter += s.counter();
        }
        if (has(Trait.clairvoyance)) {
            counter += 3;
        }
        if (has(Trait.aikidoNovice)) {
            counter += 3;
        }
        if (has(Trait.fakeout)) {
            counter += 3;
        }
        if (opponent.is(Stsflag.countered)) {
            counter -= 10;
        }
        if (has(Trait.FeralAgility) && is(Stsflag.feral)) {
            counter += 5;
        }
        // Maximum counter chance is 3 + 5 + 2 + 3 + 3 + 3 + 5 = 24, which is super hard to achieve.
        // I guess you also get some more counter with certain statuses effects like water form.
        // Counters should be pretty rare.
        return Math.max(0, counter);
    }

    private int getSpeedDifference(Character opponent) {
        return Math.min(Math.max(get(Attribute.speed) - opponent.get(Attribute.speed), -5), 5);
    }
    
    public int getChanceToHit(Character attacker, int accuracy) {
        int hitDiff = attacker.getSpeedDifference(this) + (attacker.get(Attribute.perception) - get(
                        Attribute.perception));
        int levelDiff = Math.min(attacker.level - level, 5);
        levelDiff = Math.max(levelDiff, -5);

        // with no level or hit differences and an default accuracy of 80, 80%
        // hit rate
        // each level the attacker is below the target will reduce this by 2%,
        // to a maximum of 10%
        // each point in accuracy of skill affects changes the hit chance by 1%
        // each point in speed and perception will increase hit by 5%
        int chanceToHit = 2 * levelDiff + accuracy + 5 * (hitDiff - evasionBonus());
        if (has(Trait.hawkeye)) {
            chanceToHit += 5;
        }
        return chanceToHit;
    }

    public boolean roll(Character attacker, int accuracy) {
        int attackroll = Random.random(100);
        int chanceToHit = getChanceToHit(attacker, accuracy);
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.printf("Rolled %s against %s\n",
                            attackroll, chanceToHit);
        }

        return attackroll < chanceToHit;
    }

    public int knockdownDC() {
        int dc = 10 + getStamina().get() / 10 + getStamina().percent() / 5;
        if (is(Stsflag.braced)) {
            dc += getStatus(Stsflag.braced).value();
        }
        if (has(Trait.stabilized)) {
            dc += 12 + 3 * Math.sqrt(get(Attribute.science));
        }
        if (has(ClothingTrait.heels) && !has(Trait.proheels)) {
            dc -= 7;
        }
        if (has(ClothingTrait.highheels) && !has(Trait.proheels)) {
            dc -= 8;
        }
        if (has(ClothingTrait.higherheels) && !has(Trait.proheels)) {
            dc -= 10;
        }
        return dc;
    }

    public abstract void counterattack(Character target, Tactics type, Combat c);

    public void clearStatus() {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.println("Clearing " + getTrueName());
        }
        status.removeIf(status -> !status.flags().contains(Stsflag.permanent));
    }

    public Status getStatus(Stsflag flag) {
        return getStatusStreamWithFlag(flag).findFirst().orElse(null);
    }

    // terrible code? who me? nahhhhh.
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getStatusOfClass(Class<T> clazz) {
        return status.stream().filter(s -> s.getClass().isInstance(clazz)).map(s -> (T)s).collect(Collectors.toList());
    }

    public Collection<InsertedStatus> getInsertedStatus() {
        return getStatusOfClass(InsertedStatus.class);
    }

    public Integer prize() {
        if (getRank() >= 2) {
            return 500;
        } else if (getRank() == 1) {
            return 200;
        } else {
            return 50;
        }
    }

    Move findPath(Area target) {
        if (location.name.equals(target.name)) {
            return null;
        }
        ArrayDeque<Area> queue = new ArrayDeque<>();
        Vector<Area> vector = new Vector<>();
        HashMap<Area, Area> parents = new HashMap<>();
        queue.push(location);
        vector.add(location);
        Area last = null;
        while (!queue.isEmpty()) {
            Area t = queue.pop();
            parents.put(t, last);
            if (t.name.equals(target.name)) {
                while (!location.adjacent.contains(t)) {
                    t = parents.get(t);
                }
                return new Move(t);
            }
            for (Area area : t.adjacent) {
                if (!vector.contains(area)) {
                    vector.add(area);
                    queue.push(area);
                }
            }
            last = t;
        }
        return null;
    }

    public boolean knows(Skill skill) {
        for (Skill s : getSkills()) {
            if (s.equals(skill)) {
                return true;
            }
        }
        return false;
    }

    public void endofbattle(Combat c) {
        for (Status s : status) {
            if (!s.lingering() && !s.flags().contains(Stsflag.permanent)) {
                removelist.add(s);
            }
        }
        cooldowns.clear();
        dropStatus(c, c.getOpponent(this));
        orgasms = 0;
        setChanged();
        if (has(ClothingTrait.heels)) {
            setFlag("heelsTraining", getFlag("heelsTraining") + 1);
        }
        if (has(ClothingTrait.highheels)) {
            setFlag("heelsTraining", getFlag("heelsTraining") + 1);
        }
        if (has(ClothingTrait.higherheels)) {
            setFlag("heelsTraining", getFlag("heelsTraining") + 1);
        }
        if (is(Stsflag.disguised) || has(Trait.slime)) {
            purge(c);
        }
    }

    public void setFlag(String string, int i) {
        flags.put(string, i);
    }

    public int getFlag(String string) {
        if (flags.containsKey(string)) {
            return flags.get(string);
        }
        return 0;
    }

    public boolean canSpend(int mojo) {
        int cost = mojo;
        for (Status s : getStatuses()) {
            cost += s.spendmojo(mojo);
        }
        return getMojo().get() >= cost;
    }

    public Map<Item, Integer> getInventory() {
        return inventory;
    }

    public String dumpstats(boolean notableOnly) {
        StringBuilder b = new StringBuilder();
        b.append("<b>");
        b.append(getTrueName()).append(": Level ").append(getLevel()).append("; ");
        for (Attribute a : att.keySet()) {
            b.append(a.name()).append(" ").append(att.get(a)).append(", ");
        }
        b.append("</b>");
        b.append("<br/>Max Stamina ").append(stamina.max()).append(", Max Arousal ").append(arousal.max())
                        .append(", Max Mojo ").append(mojo.max()).append(", Max Willpower ").append(willpower.max())
                        .append(".");
        b.append("<br/>");
        // ALWAYS GET JUDGED BY ANGEL. lol.
        // Angel responds, "Judge thyself."
        body.describeBodyText(b, this, notableOnly);
        if (getTraits().size() > 0) {
            b.append("<br/>Traits:<br/>");
            List<Trait> traits = new ArrayList<>(getTraits());
            traits.sort(Comparator.comparing(Trait::toString));
            for (Trait t : traits) {
                b.append(t).append(": ").append(t.getDesc());
                b.append("<br/>");
            }
        }
        b.append("</p>");

        return b.toString();
    }

    public void accept(Challenge c) {
        challenges.add(c);
    }

    public void evalChallenges(Combat c, Character victor) {
        for (Challenge chal : challenges) {
            chal.check(c, victor);
        }
    }

    public String toString() {
        return getType();
    }

    private void showSkillChoices(Combat c, Character target) {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SKILL_CHOICES)) {
            c.updateGUI();
            c.write(this, nameOrPossessivePronoun() + " turn...");
            c.updateGUI();
        }
        HashSet<Skill> available = new HashSet<>();
        HashSet<Skill> cds = new HashSet<>();
        for (Skill a : getSkills()) {
            if (Skill.skillIsUsable(c, a)) {
                if (cooldownAvailable(a)) {
                    available.add(a);
                } else {
                    cds.add(a);
                }
            }
        }
        HashSet<Skill> damage = new HashSet<>();
        HashSet<Skill> pleasure = new HashSet<>();
        HashSet<Skill> fucking = new HashSet<>();
        HashSet<Skill> position = new HashSet<>();
        HashSet<Skill> debuff = new HashSet<>();
        HashSet<Skill> recovery = new HashSet<>();
        HashSet<Skill> summoning = new HashSet<>();
        HashSet<Skill> stripping = new HashSet<>();
        HashSet<Skill> misc = new HashSet<>();
        Skill.filterAllowedSkills(c, available, this, target);
        if (available.size() == 0) {
            available.add(new Nothing(this));
        }
        available.addAll(cds);
        GUI.gui.clearCommand();
        Skill lastUsed = null;
        for (Skill a : available) {
            if (a.getName().equals(c.getCombatantData(this).getLastUsedSkillName())) {
                lastUsed = a;
            } else if (a.type(c) == Tactics.damage) {
                damage.add(a);
            } else if (a.type(c) == Tactics.pleasure) {
                pleasure.add(a);
            } else if (a.type(c) == Tactics.fucking) {
                fucking.add(a);
            } else if (a.type(c) == Tactics.positioning) {
                position.add(a);
            } else if (a.type(c) == Tactics.debuff) {
                debuff.add(a);
            } else if (a.type(c) == Tactics.recovery || a.type(c) == Tactics.calming) {
                recovery.add(a);
            } else if (a.type(c) == Tactics.summoning) {
                summoning.add(a);
            } else if (a.type(c) == Tactics.stripping) {
                stripping.add(a);
            } else {
                misc.add(a);
            }
        }
        if (lastUsed != null) {
            GUI.gui.addSkill(c, lastUsed, target);
        }
        for (Skill a : stripping) {
            GUI.gui.addSkill(c, a, target);
        }
        for (Skill a : position) {
            GUI.gui.addSkill(c, a, target);
        }
        for (Skill a : fucking) {
            GUI.gui.addSkill(c, a, target);
        }
        for (Skill a : pleasure) {
            GUI.gui.addSkill(c, a, target);
        }
        for (Skill a : damage) {
            GUI.gui.addSkill(c, a, target);
        }
        for (Skill a : debuff) {
            GUI.gui.addSkill(c, a, target);
        }
        for (Skill a : summoning) {
            GUI.gui.addSkill(c, a, target);
        }
        for (Skill a : recovery) {
            GUI.gui.addSkill(c, a, target);
        }
        for (Skill a : misc) {
            GUI.gui.addSkill(c, a, target);
        }
        GUI.gui.showSkills();
    }

    public float getOtherFitness(Combat c, Character other) {
        float fit = 0;
        // Urgency marks
        float arousalMod = 1.0f;
        float staminaMod = 1.0f;
        float mojoMod = 1.0f;
        float usum = arousalMod + staminaMod + mojoMod;
        int escape = other.getEscape(c, this);
        if (escape > 1) {
            fit += 8 * Math.log(escape);
        } else if (escape < -1) {
            fit += -8 * Math.log(-escape);
        }
        int totalAtts = 0;
        for (Attribute attribute : att.keySet()) {
            totalAtts += att.get(attribute);
        }
        fit += Math.sqrt(totalAtts) * 5;

        // what an average piece of clothing should be worth in fitness
        double topFitness = 8.0;
        double bottomFitness = 6.0;
        // If I'm horny, I want the other guy's clothing off, so I put more
        // fitness in them
        if (getMood() == Emotion.horny || has(Trait.leveldrainer)) {
            topFitness += 6;
            bottomFitness += 8;
            // If I'm horny, I want to make the opponent cum asap, put more
            // emphasis on arousal
            arousalMod = 2.0f;
        }

        // check body parts based on my preferences
        if (other.hasDick()) {
            fit -= (dickPreference() - 3) * 4;
        }
        if (other.hasPussy()) {
            fit -= (pussyPreference() - 3) * 4;
        }

        fit += c.getPetsFor(other).stream().mapToDouble(pet -> (10 + pet.getSelf().power()) * ((100 + pet.percentHealth()) / 200.0) / 2).sum();

        fit += other.outfit.getFitness(c, bottomFitness, topFitness);
        fit += other.body.getCharismaBonus(c, this);
        // Extreme situations
        if (other.arousal.isFull()) {
            fit -= 50;
        }
        // will power empty is a loss waiting to happen
        if (other.willpower.isEmpty()) {
            fit -= 100;
        }
        if (other.stamina.isEmpty()) {
            fit -= staminaMod * 3;
        }
        fit += other.getWillpower().getReal() * 5.33f;
        // Short-term: Arousal
        fit += arousalMod / usum * 100.0f * (other.getArousal().max() - other.getArousal().get()) / Math
                        .min(100, other.getArousal().max());
        // Mid-term: Stamina
        fit += staminaMod / usum * 50.0f * (1 - Math
                        .exp(-((float) other.getStamina().get()) / Math.min(other.getStamina().max(), 100.0f)));
        // Long term: Mojo
        fit += mojoMod / usum * 50.0f * (1 - Math
                        .exp(-((float) other.getMojo().get()) / Math.min(other.getMojo().max(), 40.0f)));
        for (Status status : other.getStatuses()) {
            fit += status.fitnessModifier();
        }
        // hack to make the AI favor making the opponent cum
        fit -= 100 * other.orgasms;
        // special case where if you lost, you are super super unfit.
        if (other.orgasmed && other.getWillpower().isEmpty()) {
            fit -= 1000;
        }
        return fit;
    }

    public float getFitness(Combat c) {

        float fit = 0;
        // Urgency marks
        float arousalMod = 1.0f;
        float staminaMod = 2.0f;
        float mojoMod = 1.0f;
        float usum = arousalMod + staminaMod + mojoMod;
        Character other = c.getOpponent(this);

        int totalAtts = 0;
        for (Attribute attribute : att.keySet()) {
            totalAtts += att.get(attribute);
        }
        fit += Math.sqrt(totalAtts) * 5;
        // Always important: Position
        fit += (c.getStance().priorityMod(this) + c.getStance().getDominanceOfStance(this)) * 4;
        fit += c.getPetsFor(this).stream().mapToDouble(pet -> (10 + pet.getSelf().power()) * ((100 + pet.percentHealth()) / 200.0) / 2).sum();

        int escape = getEscape(c, other);
        if (escape > 1) {
            fit += 8 * Math.log(escape);
        } else if (escape < -1) {
            fit += -8 * Math.log(-escape);
        }
        // what an average piece of clothing should be worth in fitness
        double topFitness = 4.0;
        double bottomFitness = 4.0;
        // If I'm horny, I don't care about my clothing, so I put more less
        // fitness in them
        if (getMood() == Emotion.horny || is(Stsflag.feral) | has(Trait.leveldrainer)) {
            topFitness = .5;
            bottomFitness = .5;
            // If I'm horny, I put less importance on my own arousal
            arousalMod = .7f;
        }
        fit += outfit.getFitness(c, bottomFitness, topFitness);
        fit += body.getCharismaBonus(c, other);
        if (c.getStance().inserted()) { // If we are fucking...
            // ...we need to see if that's beneficial to us.
            fit += body.penetrationFitnessModifier(this, other, c.getStance().inserted(this),
                            c.getStance().anallyPenetrated(c));
        }
        if (hasDick()) {
            fit += (dickPreference() - 3) * 4;
        }

        if (hasPussy()) {
            fit += (pussyPreference() - 3) * 4;
        }
        if (has(Trait.pheromones)) {
            fit += 5 * getPheromonePower();
            fit += 15 * getPheromonesChance(c) * (2 + getPheromonePower());
        }

        // Also somewhat of a factor: Inventory (so we don't
        // just use it without thinking)
        for (Item item : inventory.keySet()) {
            fit += (float) item.getPrice() / 10;
        }
        // Extreme situations
        if (arousal.isFull()) {
            fit -= 100;
        }
        if (stamina.isEmpty()) {
            fit -= staminaMod * 3;
        }
        fit += getWillpower().getReal() * 5.3f;
        // Short-term: Arousal
        fit += arousalMod / usum * 100.0f * (getArousal().max() - getArousal().get())
                        / Math.min(100, getArousal().max());
        // Mid-term: Stamina
        fit += staminaMod / usum * 50.0f
                        * (1 - Math.exp(-((float) getStamina().get()) / Math.min(getStamina().max(), 100.0f)));
        // Long term: Mojo
        fit += mojoMod / usum * 50.0f * (1 - Math.exp(-((float) getMojo().get()) / Math.min(getMojo().max(), 40.0f)));
        for (Status status : getStatuses()) {
            fit += status.fitnessModifier();
        }

        if (this instanceof NPC) {
            NPC me = (NPC) this;
            AiModifiers mods = me.ai.getAiModifiers();
            fit += mods.modPosition(c.getStance().enumerate()) * 6;
            fit += status.stream().flatMap(s -> s.flags().stream()).mapToDouble(mods::modSelfStatus).sum();
            fit += c.getOpponent(this).status.stream().flatMap(s -> s.flags().stream())
                            .mapToDouble(mods::modOpponentStatus).sum();
        }
        // hack to make the AI favor making the opponent cum
        fit -= 100 * orgasms;
        // special case where if you lost, you are super super unfit.
        if (orgasmed && getWillpower().isEmpty()) {
            fit -= 1000;
        }
        return fit;
    }

    public String nameOrPossessivePronoun() {
        return getName() + "'s";
    }

    public double getExposure(ClothingSlot slot) {
        return outfit.getExposure(slot);
    }

    public double getExposure() {
        return outfit.getExposure();
    }

    public abstract String getPortrait(Combat c);

    public void modMoney(int i) {
        setMoney((int) (money + Math.round(i * GameState.gameState.moneyRate)));
    }

    public void setMoney(int i) {
        money = i;
        update();
    }

    public void loseXP(int i) {
        assert i >= 0;
        xp -= i;
        update();
    }

    public String pronoun() {
        if (useFemalePronouns()) {
            return "she";
        } else {
            return "he";
        }
    }

    public Emotion getMood() {
        return Emotion.confident;
    }

    public String possessiveAdjective() {
        if (useFemalePronouns()) {
            return "her";
        } else {
            return "his";
        }
    }
    
    public String possessivePronoun() {
        if (useFemalePronouns()) {
            return "hers";
        } else {
            return "his";
        }
    }

    public String directObject() {
        if (useFemalePronouns()) {
            return "her";
        } else {
            return "him";
        }
    }

    public boolean useFemalePronouns() {
        return hasPussy() 
                        || !hasDick() 
                        || (body.getLargestBreasts().getSize() > SizeMod.getMinimumSize("breasts") && body.getFace().getFemininity(this) > 0) 
                        || (body.getFace().getFemininity(this) >= 1.5) 
                        || (human() && Flag.checkFlag(Flag.PCFemalePronounsOnly))
                        || (!human() && Flag.checkFlag(Flag.NPCFemalePronounsOnly));
    }

    public String nameDirectObject() {
        return getName();
    }

    public String reflectivePronoun() {
        String self = possessiveAdjective() + "self";
        if (self.equals("hisself")) {
            // goddammit english.
            return "himself";
        } else {
            return self;
        }
    }

    public boolean clothingFuckable(BodyPart part) {
        if (part.isType("strapon")) {
            return true;
        }
        if (part.isType("cock")) {
            return outfit.slotEmptyOrMeetsCondition(ClothingSlot.bottom,
                            (article) -> (!article.is(ClothingTrait.armored) && !article.is(ClothingTrait.bulky)
                                            && !article.is(ClothingTrait.persistent)));
        } else if (part.isType("pussy") || part.isType("ass")) {
            return outfit.slotEmptyOrMeetsCondition(ClothingSlot.bottom,
                            (article) -> article.is(ClothingTrait.skimpy) || article.is(ClothingTrait.open)
                            || article.is(ClothingTrait.flexible));
        } else {
            return false;
        }
    }

    public double pussyPreference() {
        return 11 - Flag.getValue(Flag.malePref);
    }

    public double dickPreference() {
        return Flag.getValue(Flag.malePref);
    }

    public boolean wary() {
        return hasStatus(Stsflag.wary);
    }

    public void gain(Combat c, Item item) {
        if (c != null) {
            c.write(Formatter.format("<b>{self:subject-action:have|has} gained " + item.pre() + item.getName() + "</b>",
                            this, this));
        }
        gain(item, 1);
    }

    public String temptLiner(Combat c, Character target) {
        if (c.getStance().sub(this)) {
            return Formatter.format("{self:SUBJECT-ACTION:try} to entice {other:name-do} by wiggling suggestively in {other:possessive} grip.", this, target);
        }
        return Formatter.format("{self:SUBJECT-ACTION:pat} {self:possessive} groin and {self:action:promise} {self:pronoun-action:will} show {other:direct-object} a REAL good time.", this, target);
    }

    public String action(String firstPerson, String thirdPerson) {
        return thirdPerson;
    }

    public String action(String verb) {
        return action(verb, ProseUtils.getThirdPersonFromFirstPerson(verb));
    }

    public void addCooldown(Skill skill) {
        if (skill.getCooldown() <= 0) {
            return;
        }
        if (cooldowns.containsKey(skill.toString())) {
            cooldowns.put(skill.toString(), cooldowns.get(skill.toString()) + skill.getCooldown());
        } else {
            cooldowns.put(skill.toString(), skill.getCooldown());
        }
    }

    public boolean cooldownAvailable(Skill s) {
        boolean cooledDown = true;
        if (cooldowns.containsKey(s.toString()) && cooldowns.get(s.toString()) > 0) {
            cooledDown = false;
        }
        return cooledDown;
    }

    public Integer getCooldown(Skill s) {
        if (cooldowns.containsKey(s.toString()) && cooldowns.get(s.toString()) > 0) {
            return cooldowns.get(s.toString());
        } else {
            return 0;
        }
    }

    public boolean checkLoss(Combat c) {
        return (orgasmed || c.getTimer() > 150) && willpower.isEmpty();
    }

    public boolean isCustomNPC() {
        return custom;
    }

    public int stripDifficulty(Character other) {
        if (outfit.has(ClothingTrait.tentacleSuit) || outfit.has(ClothingTrait.tentacleUnderwear)) {
            return other.get(Attribute.science) + 20;
        }
        if (outfit.has(ClothingTrait.harpoonDildo) || outfit.has(ClothingTrait.harpoonOnahole)) {
            int diff = 20;
            if (other.has(Trait.yank)) {
                diff += 5;
            }
            if (other.has(Trait.conducivetoy)) {
                diff += 5;
            }
            if (other.has(Trait.intensesuction)) {
                diff += 5;
            }
            return diff;
        }
        return 0;
    }

    public void startBattle() {
        orgasms = 0;
    }

    public void drain(Combat c, Character drainer, int i, MeterType drainType, MeterType restoreType, float efficiency) {
        int drained = i;
        int bonus = 0;
        int overkill;
        Meter targetMeter = drainType.getMeter(this);
        Meter drainerMeter = restoreType.getMeter(drainer);

        for (Status s : getStatuses()) {
            bonus += s.drained(c, drained);
        }
        drained += bonus;
        overkill = Math.max(0, drained - targetMeter.get());
        drained -= overkill;
        int restored = Math.round(drained * efficiency);
        if (c != null) {
            String subjectText = String.format("%s drained of", subjectWas());
            String drainText = String.format(" <font color=%s>%d<font color='white'> %s%s", drainType.lossColor.rgbHTML(),
                                            drained, drainType.name,
                                            overkill > 0 ? " (" + overkill + " overkill)" : "");
            // Only display restore text if the amount or type restored is different from the amount and type drained.
            String restoreText = drainType == restoreType && drained == restored ?
                            "" :
                            String.format(" as <font color=%s>%d<font color='white'> %s",
                                            restoreType.gainColor.rgbHTML(), restored, restoreType.name);
            String drainerText = String.format(" by %s", drainer.subject());
            c.writeSystemMessage(subjectText + drainText + restoreText + drainerText);
        }
        targetMeter.reduce(drained);
        drainerMeter.restore(restored);
    }

    public void drain(Combat c, Character drainer, int i, MeterType drainType, MeterType restoreType) {
        drain(c, drainer, i, drainType, restoreType, 1.0f);
    }

    public void drain(Combat c, Character drainer, int i, MeterType drainType) {
        // If the restore type isn't specified, it's the same as the drain type.
        drain(c, drainer, i, drainType, drainType);
    }

    public void update() {
        setChanged();
        notifyObservers();
    }

    public Outfit getOutfit() {
        return outfit;
    }

    public boolean footAvailable() {
        Clothing article = outfit.getTopOfSlot(ClothingSlot.feet);
        return article == null || article.getLayer() < 2;
    }

    public boolean hasInsertable() {
        return hasDick() || has(Trait.strapped);
    }

    public String guyOrGirl() {
        return useFemalePronouns() ? "girl" : "guy";
    }

    public String boyOrGirl() {
        return useFemalePronouns() ? "girl" : "boy";
    }

    public boolean isDemonic() {
        return has(Trait.succubus) || body.get("pussy").stream()
                        .anyMatch(part -> part.moddedPartCountsAs(this, DemonicMod.INSTANCE)) || body.get("cock")
                        .stream().anyMatch(part -> part.moddedPartCountsAs(this, CockMod.incubus));
    }

    public int baseDisarm() {
        int disarm = 0;
        if (has(Trait.cautious)) {
            disarm += 5;
        }
        return disarm;
    }

    public float modRecoilPleasure(Combat c, float mt) {
        float total = mt;
        if (c.getStance().sub(this)) {
            total += get(Attribute.submission) / 2;
        }
        if (has(Trait.responsive)) {
            total += total / 2;
        }
        return total;
    }

    public boolean isPartProtected(BodyPart target) {
        return target.isType("hands") && has(ClothingTrait.nursegloves);
    }

    public void purge(Combat c) {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.println("Purging " + getTrueName());
        }
        temporaryAddedTraits.clear();
        temporaryRemovedTraits.clear();
        status = status.stream().filter(s -> !s.flags().contains(Stsflag.purgable))
                        .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        body.purge(c);
    }

    /**
     * applies bonuses and penalties for using an attribute.
     */
    public void usedAttribute(Attribute att, Combat c, double baseChance) {
        // divine recoil applies at 20% per magnitude
        if (att == Attribute.divinity && Random.randomdouble() < baseChance) {
            add(c, new DivineRecoil(this, 1));
        }
    }

    /**
     * Attempts to knock down this character
     */
    public void knockdown(Combat c, Character other, Set<Attribute> attributes, int strength, int roll) {
        if (canKnockDown(other, attributes, strength, roll)) {
            add(c, new Falling(this));
        }
    }

    private int knockdownBonus() {
        return 0;
    }

    public boolean canKnockDown(Character other, Set<Attribute> attributes, int strength, double roll) {
        return knockdownDC() < strength + (roll * 100) + attributes.stream().mapToInt(other::get).sum() + other
                        .knockdownBonus();
    }

    /**
     * If true, count insertions by this character as voluntary
     */
    public boolean canMakeOwnDecision() {
        return !is(Stsflag.charmed) && !is(Stsflag.lovestruck) && !is(Stsflag.frenzied);
    }

    public String printStats() {
        return "Character{" + "name='" + name + '\'' + ", type=" + getType() + ", level=" + level + ", xp=" + xp
                        + ", rank=" + rank + ", money=" + money + ", att=" + att + ", stamina=" + stamina.max()
                        + ", arousal=" + arousal.max() + ", mojo=" + mojo.max() + ", willpower=" + willpower.max()
                        + ", outfit=" + outfit + ", traits=" + traits + ", inventory=" + inventory + ", flags=" + flags
                        + ", trophy=" + trophy + ", closet=" + closet + ", body=" + body + ", availableAttributePoints="
                        + availableAttributePoints + '}';
    }

    public void addLevels(int levelsToGain) {
        this.levelsToGain += levelsToGain;
    }

    public void addLevelsImmediate(Combat c, int levelsToGain) {
        addLevels(levelsToGain);
        spendLevels(c);
    }

    public void spendLevels(Combat c) {
        ding(c, levelsToGain);
        levelsToGain = 0;
    }

    public int getMaxWillpowerPossible() {
        return Integer.MAX_VALUE;
    }

    // TODO: move XP spending to ding() so we can't forget about it.
    // or level spending to spendXP(), or something
    // Addendum: Looks like we want to be able to add levels while ignoring XP, like with Item.LevelUpEffect.
    public void spendXP() {
        int req;
        while (xp - (req = getXPReqToNextLevel(level + levelsToGain)) >= 0) {
            xp -= req;
            levelsToGain++;
        }
    }

    public void matchPrep(Match m) {
        if(getPure(Attribute.ninjutsu)>=9){
            this.adjustTraits();
            placeNinjaStash(m);
        }
        ArmManager manager = m.getMatchData().getDataFor(this).getArmManager();
        manager.selectArms(this);
        if (manager.getActiveArms().stream().anyMatch(a -> a.getType() == ArmType.STABILIZER)) {
            add(Trait.stabilized);
        } else {
            remove(Trait.stabilized);
        }
        if (has(Trait.RemoteControl)) {
            int currentCount = inventory.getOrDefault(Item.RemoteControl, 0);
            gain(Item.RemoteControl, 2 - currentCount + get(Attribute.science) / 10);
        }
    }

    private void placeNinjaStash(Match m) {
        String location;
        switch(Random.random(6)){
        case 0:
            location = "Library";
            break;
        case 1:
            location = "Dining";
            break;
        case 2:
            location = "Lab";
            break;
        case 3:
            location = "Workshop";
            break;
        case 4:
            location = "Storage";
            break;
        default:
            location = "Liberal Arts";
            break;
        }
        m.gps(location).place(new NinjaStash(this));
        if(human()){
            GUI.gui.message("<b>You've arranged for a hidden stash to be placed in the "+m.gps(location).name+".</b>");
        }
    }

    public boolean hasSameStats(Character character) {
        if (!name.equals(character.name)) {
            return false;
        }
        if (!getType().equals(character.getType())) {
            return false;
        }
        if (!(level == character.level)) {
            return false;
        }
        if (!(xp == character.xp)) {
            return false;
        }
        if (!(rank == character.rank)) {
            return false;
        }
        if (!(money == character.money)) {
            return false;
        }
        if (!att.equals(character.att)) {
            return false;
        }
        if (!(stamina.max() == character.stamina.max())) {
            return false;
        }
        if (!(arousal.max() == character.arousal.max())) {
            return false;
        }
        if (!(mojo.max() == character.mojo.max())) {
            return false;
        }
        if (!(willpower.max() == character.willpower.max())) {
            return false;
        }
        if (!outfit.equals(character.outfit)) {
            return false;
        }
        if (!(new HashSet<>(traits).equals(new HashSet<>(character.traits)))) {
            return false;
        }
        if (!inventory.equals(character.inventory)) {
            return false;
        }
        if (!flags.equals(character.flags)) {
            return false;
        }
        if (!trophy.equals(character.trophy)) {
            return false;
        }
        if (!closet.equals(character.closet)) {
            return false;
        }
        if (!body.equals(character.body)) {
            return false;
        }
        return availableAttributePoints == character.availableAttributePoints;

    }

    public void flagStatus(Stsflag flag) {
        statusFlags.add(flag);
    }

    public void unflagStatus(Stsflag flag) {
        statusFlags.remove(flag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || !getClass().equals(o.getClass()))
            return false;
        if (o == NPC.noneCharacter() || this == NPC.noneCharacter())
            return false;
        Character character = (Character) o;
        return getType().equals(character.getType()) && name.equals(character.name);
    }

    @Override public int hashCode() {
        int result = getType().hashCode();
        return result * 31 + name.hashCode();
    }

    public Growth getGrowth() {
        return growth;
    }

    public void setGrowth(Growth growth) {
        this.growth = growth;
    }
    public Collection<Skill> getSkills() {
        return skills;
    }

    protected void distributePoints(List<PreferredAttribute> preferredAttributes) {
        if (availableAttributePoints <= 0) {
            return;
        }
        List<Attribute> avail = new ArrayList<>();
        Deque<PreferredAttribute> preferred = new ArrayDeque<>(preferredAttributes);
        for (Attribute a : att.keySet()) {
            if (Attribute.isTrainable(this, a)) {
                avail.add(a);
            }
        }
        if (avail.size() == 0) {
            avail.add(Attribute.cunning);
            avail.add(Attribute.power);
            avail.add(Attribute.seduction);
        }
        int noPrefAdded = 2;
        for (; availableAttributePoints > 0; availableAttributePoints--) {
            Attribute selected = null;
            // remove all the attributes that isn't in avail
            preferred = preferred.stream().filter(p -> {
                Optional<Attribute> att = p.getPreferred(this);
                return att.isPresent() && avail.contains(att.get());
            }).collect(Collectors.toCollection(ArrayDeque::new));
            if (preferred.size() > 0) {
                if (noPrefAdded > 1) {
                    noPrefAdded = 0;
                    Optional<Attribute> pref = preferred.removeFirst()
                                                        .getPreferred(this);
                    if (pref.isPresent()) {
                        selected = pref.get();
                    }
                } else {
                    noPrefAdded += 1;
                }
            }

            if (selected == null) {
                selected = avail.get(Random.random(avail.size()));
            }
            mod(selected, 1);
        }
    }

    public boolean isPetOf(Character other) {
        return false;
    }

    public boolean isPet() {
        return false;
    }

    public int getPetLimit() {
        return has(Trait.congregation) ? 2 : 1;
    }

    Collection<Action> allowedActions() {
        return status.stream().flatMap(s -> s.allowedActions().stream()).collect(Collectors.toSet());
    }

    public boolean isHypnotized() {
        return is(Stsflag.drowsy) || is(Stsflag.enthralled) || is(Stsflag.charmed) || is(Stsflag.trance) || is(Stsflag.lovestruck);
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Addiction> getAddictions() {
        return getAddictionStream().collect(Collectors.toList());
    }

    private Stream<Status> getStatusStreamWithFlag(Stsflag flag) {
        return status.stream().filter(status -> status.flags().contains(flag));
    }

    private Stream<Addiction> getAddictionStream() {
        return status.stream().filter(status -> status instanceof Addiction).map(s -> (Addiction)s);
    }

    public boolean hasAddiction(AddictionType type) {
        return getAddictionStream().anyMatch(a -> a.getType() == type);
    }

    public Optional<Addiction> getAddiction(AddictionType type) {
        return getAddictionStream().filter(a -> a.getType() == type).findAny();
    }

    public Optional<Addiction> getStrongestAddiction() {
        return getAddictionStream().max(Comparator.comparing(Addiction::getSeverity));
    }

    private static final Set<AddictionType> NPC_ADDICTABLES = EnumSet.of(AddictionType.CORRUPTION);
    public void addict(Combat c, AddictionType type, Character cause, float mag) {
        boolean dbg = DebugFlags.isDebugOn(DebugFlags.DEBUG_ADDICTION);
        if (!human() && !NPC_ADDICTABLES.contains(type)) {
            if (dbg) {
                System.out.printf("Skipping %s addiction on %s because it's not supported for NPCs", type.name(), getType());
            }
        }
        Optional<Addiction> addiction = getAddiction(type);
        if (addiction.isPresent() && Objects.equals(addiction.map(Addiction::getCause).orElse(null), cause)) {
            Addiction a = addiction.get();
            a.aggravate(c, mag);
        } else {
            if (dbg) {
                System.out.printf("Creating initial %s on %s with %.3f\n", type.name(), getTrueName(), mag);
            }
            Addiction addict = type.build(this, cause.getType(), mag);
            addNonCombat(addict);
            addict.describeInitial();
        }
    }

    public void unaddict(Combat c, AddictionType type, float mag) {
        Optional<Addiction> addiction = getAddiction(type);
        if (!addiction.isPresent()) {
            return;
        }
        Addiction addict = addiction.get();
        addict.alleviate(c, mag);
        if (addict.shouldRemove()) {
            DebugFlags.DEBUG_ADDICTION.printf("Removing %s from %s\n", type.name(), this.getTrueName());
            removeStatusImmediately(addict);
        }
    }

    public void removeStatusImmediately(Status status) {
        this.status.remove(status);
    }

    public void unaddictCombat(AddictionType type, Character cause, float mag, Combat c) {
        boolean dbg = DebugFlags.isDebugOn(DebugFlags.DEBUG_ADDICTION);
        Optional<Addiction> addict = getAddiction(type);
        if (addict.isPresent()) {
            if (dbg) {
                System.out.printf("Alleviating %s on player by %.3f (Combat vs %s)\n", type.name(), mag,
                                cause.getTrueName());
            }
            addict.get().alleviateCombat(c, mag);
        }
    }

    public Severity getAddictionSeverity(AddictionType type) {
        return getAddiction(type).map(Addiction::getSeverity).orElse(Severity.NONE);
    }

    public boolean checkAddiction() {
        return getAddictionStream().anyMatch(a -> a.atLeast(Severity.LOW));
    }

    public boolean checkAddiction(AddictionType type) {
        return getAddiction(type).map(Addiction::isActive).orElse(false);
    }

    public boolean checkAddiction(AddictionType type, Character cause) {
        return getAddiction(type).map(addiction -> addiction.isActive() && addiction.wasCausedBy(cause)).orElse(false);
    }

    /**
     * Initiative determines turn order.
     *
     * Characters with higher initiative move first.
     */
    public void rollInitiative() {
        lastInitRoll = get(Attribute.speed) + Random.random(20);
    }

    public enum MeterType {
        STAMINA("stamina", STAMINA_GAIN, STAMINA_LOSS),
        AROUSAL("arousal", AROUSAL_GAIN, AROUSAL_LOSS),
        MOJO("mojo", MOJO_GAIN, MOJO_LOSS),
        WILLPOWER("willpower", WILLPOWER_GAIN, WILLPOWER_LOSS),
        ;

        String name;
        GUIColor gainColor;
        GUIColor lossColor;

        MeterType(String name, GUIColor gainColor, GUIColor lossColor) {
            this.name = name;
            this.gainColor = gainColor;
            this.lossColor = lossColor;
        }

        Meter getMeter(Character character) {
            switch (this) {
                case STAMINA:
                    return character.stamina;
                case AROUSAL:
                    return character.arousal;
                case MOJO:
                    return character.mojo;
                case WILLPOWER:
                    return character.willpower;
                default:
                    throw new RuntimeException("Invalid Meter Type");
            }
        }
    }

}
