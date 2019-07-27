package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Match;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.Staleness;
import nightgames.status.FiredUp;
import nightgames.status.Status;
import nightgames.status.Stsflag;

import java.util.*;

// TODO: Separate Skills into skill specifications and skill usage instances. Should make it easier to reduce
// squirreliness with CharacterTypes vs Characters.
public abstract class Skill {
    /**
     *
     */
    private String name;
    private int cooldown;
    private Set<SkillTag> tags;
    public String choice;
    private Staleness staleness;

    public Skill(String name) {
        this(name, 0);
    }
    public Skill(String name, int cooldown) {
        this(name, cooldown, Staleness.build().withDecay(.1).withFloor(.5).withRecovery(.05));
    }

    public Skill(String name, int cooldown, Staleness staleness) {
        this.name = name;
        this.cooldown = cooldown;
        this.staleness = staleness;
        choice = "";
        tags = new HashSet<>();
    }

    public static boolean skillIsUsable(Combat c, Skill s, Character user) {
        return skillIsUsable(c, s, user, null);
    }

    public static boolean skillIsUsable(Combat c, Skill skill, Character user, Character target) {
        if (target == null) {
            target = skill.getDefaultTarget(c, user);
        }
        boolean charmRestricted =
                        (user.is(Stsflag.charmed)) && skill.type(c, user) != Tactics.fucking
                                        && skill.type(c, user) != Tactics.pleasure
                                        && skill.type(c, user) != Tactics.misc;
        boolean allureRestricted = target.is(Stsflag.alluring) && (skill.type(c, user) == Tactics.damage
                        || skill.type(c, user) == Tactics.debuff);
        boolean modifierRestricted = !Match.getMatch().condition.getSkillModifier().allowedSkill(c, skill);
        return skill.usable(c, user, target) && user
                        .canSpend(skill.getMojoCost(c, user)) && !charmRestricted && !allureRestricted
                        && !modifierRestricted;
    }

    public static boolean skillIsUsable(Combat c, SkillUsage usage) {
        return skillIsUsable(c, usage.skill, usage.user, usage.target);
    }

    public abstract boolean requirements(Combat c, Character user, Character target);

    public static void filterAllowedSkills(Combat c, Collection<Skill> skills, Character user) {
        filterAllowedSkills(c, skills, user, null);
    }
    public static void filterAllowedSkills(Combat c, Collection<Skill> skills, Character user, Character target) {
        boolean filtered = false;
        Set<Skill> stanceSkills = new HashSet<>(c.getStance().availSkills(c, user));

        if (stanceSkills.size() > 0) {
            skills.retainAll(stanceSkills);
            filtered = true;
        }
        Set<Skill> availSkills = new HashSet<>();
        for (Status st : user.status) {
            for (Skill sk : st.allowedSkills(c)) {
                if ((target != null && skillIsUsable(c, sk, user, target)) || skillIsUsable(c, sk, user)) {
                    availSkills.add(sk);
                }
            }
        }
        if (availSkills.size() > 0) {
            skills.retainAll(availSkills);
            filtered = true;
        }
        Set<Skill> noReqs = new HashSet<>();
        if (!filtered) {
            // if the skill is restricted by status/stance, do not check for
            // requirements
            for (Skill sk : skills) {
                if (sk.getTags(c, user).contains(SkillTag.mean) && user.has(Trait.softheart)) {
                    continue;
                }
                if (!sk.requirements(c, user, target != null? target : sk.getDefaultTarget(c, user))) {
                    noReqs.add(sk);
                }
            }
            skills.removeAll(noReqs);
        }
    }

    public int getMojoBuilt(Combat c, Character user) {
        return 0;
    }

    public int getMojoCost(Combat c, Character user) {
        return 0;
    }

    public abstract boolean usable(Combat c, Character user, Character target);

    public abstract String describe(Combat c, Character user);

    public abstract boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded);

    public abstract Tactics type(Combat c, Character user);

    public abstract String deal(Combat c, int damage, Result modifier, Character user, Character target);

    public abstract String receive(Combat c, int damage, Result modifier, Character user, Character target);

    public boolean isReverseFuck(Character user, Character target) {
        return target.hasDick() && user.hasPussy();
    }

    public float priorityMod(Combat c, Character user) {
        return 0.0f;
    }

    public int baseAccuracy(Combat c, Character user, Character target) {
        return 200;
    }

    public Staleness getStaleness() {
        return this.staleness;
    }

    public int speed(Character user) {
        return 5;
    }

    public String getLabel(Combat c, Character user) {
        return getName(c, user);
    }

    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Skill)) return false;
        return toString().equals(other.toString());
    }

    @Override
    public int hashCode() {
        return ("Skill:" + toString()).hashCode();
    }

    public String getName(Combat c, Character user) {
        return toString();
    }

    public boolean makesContact() {
        return false;
    }

    public final int accuracy(Combat c, Character user, Character target) {
        return this.baseAccuracy(c, user, target) + user.getTraits().stream().filter(trait -> trait.baseTrait != null)
                        .mapToInt(trait -> trait.baseTrait.modAccuracy(c, user, target, this)).sum();
    }

    public static boolean resolve(Skill skill, Combat c, Character user, Character target) {
        user.addCooldown(skill);
        // save the mojo built of the skill before resolving it (or the status
        // may change)
        int generated = skill.getMojoBuilt(c, user);

        // Horrendously ugly, I know.
        // But you were the one who removed getWithOrganType...
        if (user.has(Trait.temptress)) {
            FiredUp status = (FiredUp) user.status.stream().filter(s -> s instanceof FiredUp).findAny()
                            .orElse(null);
            if (status != null) {
                if (status.getPart().equals("hands") && skill.getClass() != TemptressHandjob.class
                                || status.getPart().equals("mouth") && skill.getClass() != TemptressBlowjob.class
                                || status.getPart().equals("pussy") && skill.getClass() != TemptressRide.class) {
                    user.removeStatus(Stsflag.firedup);
                }
            }
        }

        boolean success = skill.resolve(c, user, target, target.roll(user, skill.accuracy(c, user, target)));
        user.getTraits().stream().filter(trait -> trait.baseTrait != null).map(trait -> trait.baseTrait)
                        .forEach(baseTrait -> baseTrait.onSkillUse(skill, c, user, target));
        user.spendMojo(c, skill.getMojoCost(c, user));
        if (success) {
            user.buildMojo(c, generated);
        } else if (target.has(Trait.tease) && Random.random(4) == 0) {
            c.write(target, Formatter.format("Dancing just past {other:name-possessive} reach gives {self:name-do} a minor high.", target, user));
            target.buildMojo(c, 20);
        }
        if (success && c.getCombatantData(user) != null) {
            c.getCombatantData(user).decreaseMoveModifier(c, skill);
        }
        if (c.getCombatantData(user) != null) {
            c.getCombatantData(user).setLastUsedSkillName(skill.getName());
        }
        return success;
    }

    public Optional<Status> statusCheck(Status possible, Combat c, Character user, Character target, double baseChance) {
        double chance = baseChance * (1 + user.getTraits().stream().filter(trait -> trait.baseTrait != null)
                        .map(trait -> trait.baseTrait)
                        .mapToDouble(baseTrait -> baseTrait.statusChanceMultiplier(this, c, user, target, possible))
                        .sum());
        if (Random.randomdouble() < chance) {
            return Optional.of(possible);
        } else {
            return Optional.empty();
        }
    }

    public int getCooldown() {
        return cooldown;
    }

    public Collection<String> subChoices(Combat c, Character user) {
        return Collections.emptySet();
    }

    protected void printBlinded(Combat c, Character user) {
        c.write(user, "<i>You're sure something is happening, but you can't figure out what it is.</i>");
    }
    
    public Stage getStage() {
        return Stage.REGULAR;
    }

    public Character getDefaultTarget(Combat c, Character user) {
        return c.getOpponent(user);
    }

    public final double multiplierForStage(Character target) {
        return getStage().multiplierFor(target);
    }
    
    public void writeOutput(Combat c, Result result, Character user, Character target) {
        writeOutput(c, 0, result, user, target);
    }
    
    protected void writeOutput(Combat c, int mag, Result result, Character user, Character target) {
        if (user.human()) {
            c.write(user, deal(c, mag, result, user, target));
        } else if (c.shouldPrintReceive(target, c)) {
            c.write(user, receive(c, mag, result, user, target));
        }
    }

    protected void addTag(SkillTag tag) {
        tags.add(tag);
    }

    void removeTag(SkillTag tag) {
        tags.remove(tag);
    }
    public final Set<SkillTag> getTags(Combat c, Character user) {
        return getTags(c, user, c.getOpponent(user));
    }

    public Set<SkillTag> getTags(Combat c, Character user, Character target) {
        return Collections.unmodifiableSet(tags);
    }

    public static class SkillUsage<S extends Skill> {
        public final S skill;
        public final Character user;
        public final Character target;

        public SkillUsage(S skill, Character user, Character target) {
            assert user != null;
            this.skill = skill;
            this.user = user;
            this.target = target;
        }

        public final boolean requirements(Combat c) {
            return skill.requirements(c, user, target);
        }

        public boolean resolve(Combat c) {
            return Skill.resolve(skill, c, user, target);
        }
    }
}
