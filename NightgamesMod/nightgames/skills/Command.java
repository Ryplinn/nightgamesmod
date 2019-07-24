package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.items.clothing.ClothingTable;
import nightgames.skills.damage.DamageType;
import nightgames.stance.*;
import nightgames.status.BodyFetish;
import nightgames.status.Stsflag;

import java.util.*;

public class Command extends Skill {

    public Command() {
        super("Command");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return !user.human();
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !user.human() && user.canRespond() && target.is(Stsflag.enthralled)
                        && !availableCommands(c, user, target).isEmpty();
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 10.0f;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Order your thrall around";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {

        EnumSet<CommandType> available = availableCommands(c, user, target);
        assert !available.isEmpty();

        // Fucking takes priority
        if (available.contains(CommandType.MASTER_INSERT) && Random.random(100) <= 75) {
            executeCommand(CommandType.MASTER_INSERT, c, user, target);
            return true;
        }

        // Then positioning
        Set<CommandType> positioning = new HashSet<>(available);
        positioning.retainAll(Arrays.asList(CommandType.MASTER_BEHIND, CommandType.MASTER_MOUNT,
                        CommandType.MASTER_REVERSE_MOUNT, CommandType.MASTER_FACESIT));
        Optional<CommandType> position = Random.pickRandom(new ArrayList<>(positioning));
        if (position.isPresent() && Random.random(100) <= 75) {
            executeCommand(position.get(), c, user, target);
            return true;
        }

        // Then stripping
        Set<CommandType> stripping = new HashSet<>(available);
        stripping.retainAll(Arrays.asList(CommandType.STRIP_MASTER, CommandType.STRIP_SLAVE));
        Optional<CommandType> strip = Random.pickRandom(new ArrayList<>(stripping));
        if (strip.isPresent() && Random.random(100) <= 75) {
            executeCommand(strip.get(), c, user, target);
            return true;
        }

        // Then 'one-offs'
        Set<CommandType> oneOffs = new HashSet<>(available);
        oneOffs.retainAll(Arrays.asList(CommandType.MASTER_STRAPON, CommandType.SUBMIT));
        Optional<CommandType> oneOff = Random.pickRandom(new ArrayList<>(oneOffs));
        if (oneOff.isPresent() && Random.random(100) <= 75) {
            executeCommand(oneOff.get(), c, user, target);
            return true;
        }

        // Then oral
        if (available.contains(CommandType.WORSHIP_PUSSY)) {
            executeCommand(CommandType.WORSHIP_PUSSY, c, user, target);
            return true;
        }
        if (available.contains(CommandType.WORSHIP_COCK)) {
            executeCommand(CommandType.WORSHIP_COCK, c, user, target);
            return true;
        }
        Set<CommandType> orals = new HashSet<>(available);
        orals.retainAll(Arrays.asList(CommandType.GIVE_ANNILINGUS, CommandType.GIVE_BLOWJOB,
                        CommandType.GIVE_CUNNILINGUS));
        Optional<CommandType> oral = Random.pickRandom(new ArrayList<>(orals));
        if (oral.isPresent() && Random.random(100) <= 75) {
            executeCommand(oral.get(), c, user, target);
            return true;
        }

        // If none chosen yet, just pick anything
        executeCommand(Random.pickRandomGuaranteed(new ArrayList<>(available)), c, user, target);
        return true;
    }

    @Override
    public Skill copy(Character target) {
        return new Command();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        // Not used - executeCommand prints messages
        if (modifier == null) {
            return user.getName() + "'s order confuses you for a moment, snapping her control over you.";
        }
        switch (modifier) {
            case critical:
                switch (damage) {
                    case 0:
                        return "While commanding you to be still, " + user.getName()
                                        + " starts bouncing wildly on your dick.";
                    case 1:
                        return "Her scent overwhelms you and you feel a compulsion to pleasure her.";
                    case 2:
                        return "You feel an irresistible compulsion to lie down on your back";
                    default:
                        break;
                }
            case miss:
                return "You feel an uncontrollable desire to undress yourself";
            case normal:
                return user.getName() + "'s eyes bid you to pleasure yourself on her behalf.";
            case special:
                return user.getName() + "'s voice pulls you in and you cannot resist fucking her";
            case weak:
                return "You are desperate to see more of " + user.getName() + "'s body";
            default:
                return null;
        }
    }

    private EnumSet<CommandType> availableCommands(Combat c, Character user, Character target) {
        EnumSet<CommandType> available = EnumSet.of(CommandType.HURT_SELF);

        if (strippable(user))
            available.add(CommandType.STRIP_MASTER);

        if (strippable(target))
            available.add(CommandType.STRIP_SLAVE);

        if (user.crotchAvailable() && !c.getStance().havingSex(c)) {

            if (target.body.getFetish("cock").isPresent() && user.hasDick())
                available.add(CommandType.WORSHIP_COCK);

            if (target.body.getFetish("pussy").isPresent() && user.hasPussy())
                available.add(CommandType.WORSHIP_PUSSY);

            if (user.hasDick())
                available.add(CommandType.GIVE_BLOWJOB);

            if (user.hasPussy())
                available.add(CommandType.GIVE_CUNNILINGUS);

            available.add(CommandType.GIVE_ANNILINGUS);
        }

        if (c.getStance().en == Stance.neutral) {
            available.add(CommandType.SUBMIT);
            available.add(CommandType.MASTER_BEHIND);
        }

        if (c.getStance()
             .dom(user) && c.getStance().en == Stance.standingover) {
            available.add(CommandType.MASTER_MOUNT);
            available.add(CommandType.MASTER_REVERSE_MOUNT);
            if (user.crotchAvailable())
                available.add(CommandType.MASTER_FACESIT);
        }

        if (!user.hasDick() && !user.has(Trait.strapped)
                        && (user.has(Item.Strapon) || user.has(Item.Strapon2)))
            available.add(CommandType.MASTER_STRAPON);

        if (target.crotchAvailable())
            available.add(CommandType.MASTURBATE);

        if (user.getSkills()
                  .stream()
                  .filter(skill -> Tactics.fucking.equals(skill.type(c, user)))
                  .map(s -> s.copy(user))
                  .anyMatch(s -> s.requirements(c, user, target) && s.usable(c, user, target)))
            available.add(CommandType.MASTER_INSERT);

        return available;
    }

    private boolean strippable(Character ch) {
        if (ch.outfit.isNude() || (ch.outfit.slotOpen(ClothingSlot.top) && ch.outfit.slotOpen(ClothingSlot.bottom)))
            return false;
        if (!ch.outfit.slotOpen(ClothingSlot.top))
            return true;
        return !ch.outfit.slotOpen(ClothingSlot.bottom) && !ch.outfit.getTopOfSlot(ClothingSlot.bottom).getID()
                        .equals("strapon");
    }

    private Clothing getStripTarget(Character ch) {
        List<Clothing> strippable = ch.outfit.getAllStrippable();
        strippable.removeIf(c -> c.getID()
                                  .equals("strapon"));
        List<Clothing> highPriority = new ArrayList<>(strippable);
        highPriority.removeIf(c -> !c.getSlots()
                                   .contains(ClothingSlot.top)
                        && !c.getSlots()
                            .contains(ClothingSlot.bottom));
        assert !strippable.isEmpty();
        if (!highPriority.isEmpty())
            return (Clothing) highPriority.toArray()[Random.random(highPriority.size())];
        return (Clothing) strippable.toArray()[Random.random(strippable.size())];
    }

    private void executeCommand(CommandType chosen, Combat c, Character user, Character target) {
        user.emote(Emotion.confident, 30);
        user.emote(Emotion.dominant, 40);
        switch (chosen) {
            case GIVE_ANNILINGUS:
                c.write(user,
                                String.format("%s presents %s ass to %s, and %s"
                                                + " instantly %s towards it and %s it fervently.", user.getName(),
                                                user.possessiveAdjective(), target.nameDirectObject(),
                                                target.pronoun(), target.action("dive"), target.action("lick")));
                int m = target.has(Trait.silvertongue) ? 15 : 10;
                user.body.pleasure(target, target.body.getRandom("mouth"), user.body.getRandomAss(),
                                7 + Random.random(m), c, new SkillUsage<>(this, user, target));
                if (Random.random(50) < user.get(Attribute.fetishism) + 10) {
                    target.add(c, new BodyFetish(target.getType(), user.getType(), "ass", .1));
                }
                if (c.getStance().dom(target)) {
                    c.setStance(new Kneeling(user.getType(), target.getType()));
                }
                target.temptNoSkillNoSource(c, user, 7 + Random.random(20));
                user.buildMojo(c, 15);
                break;
            case GIVE_BLOWJOB:
                c.write(user,
                                String.format("%s holds up %s %s, and %s simply can't resist"
                                                + " the tantalizing appendage. %s %s head and %s and %s"
                                                + " it all over.", user.getName(), user.possessiveAdjective(),
                                                user.body.getRandomCock()
                                                              .describe(user), target.subject(),
                                                              target.subjectAction("lower"),
                                                              target.possessiveAdjective(), target.action("lick"),
                                                              target.action("suck")));
                m = target.has(Trait.silvertongue) ? 15 : 10;
                user.body.pleasure(target, target.body.getRandom("mouth"), user.body.getRandomCock(),
                                7 + Random.random(m), c, new SkillUsage<>(this, user, target));
                if (Random.random(50) < user.get(Attribute.fetishism) + 10) {
                    target.add(c, new BodyFetish(target.getType(), user.getType(), "cock", .1));
                }

                if (c.getStance().dom(target)) {
                    c.setStance(new Kneeling(user.getType(), target.getType()));
                }
                target.temptNoSkillNoSource(c, user, 7 + Random.random(20));
                user.buildMojo(c, 15);
                break;
            case GIVE_CUNNILINGUS:
                c.write(user, String.format(
                                "%s spreads %s labia and before %s can"
                                                + " even tell %s what to do, %s already between %s legs"
                                                + " slavering away at it.",
                                user.getName(), user.possessiveAdjective(), user.pronoun(),
                                target.directObject(), target.subjectAction("are", "is"),
                                user.possessiveAdjective()));
                m = target.has(Trait.silvertongue) ? 15 : 10;
                user.body.pleasure(target, target.body.getRandom("mouth"), user.body.getRandomPussy(),
                                7 + Random.random(m), c, new SkillUsage<>(this, user, target));
                if (Random.random(50) < user.get(Attribute.fetishism) + 10) {
                    target.add(c, new BodyFetish(target.getType(), user.getType(), "pussy", .1));
                }

                if (c.getStance().dom(target)) {
                    c.setStance(new Kneeling(user.getType(), target.getType()));
                }
                target.temptNoSkillNoSource(c, user, 7 + Random.random(20));
                user.buildMojo(c, 15);
                break;
            case MASTER_BEHIND:
                c.write(user,
                                String.format("Freezing %s in place with a mere"
                                                + " glance, %s casually walks around %s and grabs %s from"
                                                + " behind.", target.directObject(), user.getName(),
                                                target.nameDirectObject(),
                                                target.directObject()));
                c.setStance(new Behind(user.getType(), target.getType()), target, false);
                user.buildMojo(c, 5);
                break;
            case MASTER_MOUNT:
                c.write(user,
                                String.format("%s tells %s to remain still and"
                                                + " gracefully lies down on %s, %s face right above %ss.",
                                                user.getName(), target.subject(),
                                                target.directObject(), user.possessiveAdjective(),
                                                target.possessiveAdjective()));
                c.setStance(new Mount(user.getType(), target.getType()), target, false);
                user.buildMojo(c, 5);
                break;
            case MASTER_REVERSE_MOUNT:
                c.write(user,
                                String.format("%s fixes %s with an intense glare, telling"
                                                + " %s to stay put. Moving a muscle does not even begin to enter"
                                                + " %s thoughts as %s turns away from %s and sits down on %s"
                                                + " belly.", user.getName(), target.subject(),
                                                target.directObject(), target.possessiveAdjective(), user.pronoun(),
                                                target.directObject(), target.possessiveAdjective()));
                c.setStance(new ReverseMount(user.getType(), target.getType()), target, false);
                user.buildMojo(c, 5);
                break;
            case MASTER_STRAPON:
                c.write(user,
                                String.format("%s affixes an impressive-looking strapon"
                                                + " to %s crotch. At first %s a bit intimidated, but once %s"
                                                + " tells %s that %s the look of it, %s %s practically"
                                                + " salivating.", user.getName(), user.possessiveAdjective(),
                                                target.directObject(),
                                                target.subjectAction("are", "is"), 
                                                user.subject(), target.subjectAction("like"),
                                                target.pronoun(), target.action("are", "is")));
                if (user.has(Item.Strapon2)) {
                    c.write(user, "The phallic toy vibrates softly but insistently, "
                                    + "obviously designed to make the recipient squeal.");
                }
                user.getOutfit()
                         .equip(ClothingTable.getByID("strapon"));
                user.buildMojo(c, 10);
                break;
            case MASTURBATE:
                BodyPart pleasured =
                                target.body.getRandom(target.hasDick() ? "cock" : target.hasPussy() ? "pussy" : "ass");
                c.write(user,
                                String.format("Feeling a bit uninspired, %s just tells %s"
                                                + " to play with %s %s for %s.", user.getName(),
                                                target.subject(), target.possessiveAdjective(),
                                                pleasured.describe(target), user.directObject()));
                target.body.pleasure(target, target.body.getRandom("hands"), pleasured, 10 + Random.random(20), c, new SkillUsage<>(this, user, target));
                break;
            case HURT_SELF:
                c.write(user,
                                String.format("Following a voiceless command,"
                                                + " %s %s elbow into %s gut as hard as %s can."
                                                + " It hurts, but the look of pure amusement on %s face"
                                                + " makes everything alright.", target.subjectAction("slam"),
                                                target.possessiveAdjective(), target.possessiveAdjective(),
                                                target.pronoun(), user.nameOrPossessivePronoun()));
                target.pain(c, target, (int) DamageType.physical.modifyDamage(target, target, Random.random(30, 50)));
                break;
            case STRIP_MASTER:
                Clothing removed = getStripTarget(user);
                if (removed == null)
                    return;
                user.getOutfit()
                         .unequip(removed);
                c.write(user,
                                String.format("%s tells %s to remove %s %s for %s."
                                                + " %s gladly %s, eager to see more of %s perfect physique.",
                                                user.subject(), target.subject(),
                                                user.possessiveAdjective(), removed.getName(),
                                                user.directObject(),
                                                Formatter.capitalizeFirstLetter(target.pronoun()),
                                                target.action("comply", "complies"),
                                                user.nameOrPossessivePronoun()));
                target.temptNoSkillNoSource(c, user, 7 + Random.random(20));
                break;
            case STRIP_SLAVE:
                removed = getStripTarget(target);
                if (removed == null)
                    return;
                target.getOutfit()
                      .unequip(removed);
                c.write(user,
                                String.format("With a dismissive gesture, %s tells %s"
                                                + " that %s would feel far better without %s %s on. Of course!"
                                                + " That would make <i>everything</i> better! %s eagerly %s"
                                                + " the offending garment.", user.getName(),
                                                target.subject(), target.pronoun(), 
                                                target.possessiveAdjective(), removed.getName(),
                                                Formatter.capitalizeFirstLetter(target.pronoun()),
                                                target.action("remove")));
                break;
            case SUBMIT:
                c.write(user,
                                String.format("%s stares deeply into %s soul and tells"
                                                + " %s that %s should lie down on the ground. %s obey the order"
                                                + " without hesitation.", user.getName(),
                                                target.nameOrPossessivePronoun(), target.directObject(),
                                                target.pronoun(), 
                                                Formatter.capitalizeFirstLetter(target.subjectAction("obey"))));
                c.setStance(new StandingOver(user.getType(), target.getType()), target, false);
                break;
            case WORSHIP_COCK:
                c.write(user,
                                String.format("%s has a cock. %s that cock. %s humbly"
                                                + " %s for %s permission and %s is letting %s! %s enthusiastically"
                                                + " %s %s at %s feet and %s the beautiful %s with"
                                                + " almost religious zeal. At the same time, %s cannot contain %s lust"
                                                + " and simply must play with %s.", user.getName(),
                                                target.subjectAction("NEED", "NEEDS"),
                                                Formatter.capitalizeFirstLetter(target.pronoun()),
                                                target.action("beg"),
                                                user.nameOrPossessivePronoun(), user.pronoun(),
                                                target.directObject(),
                                                Formatter.capitalizeFirstLetter(target.pronoun()),
                                                target.action("throw"), target.reflectivePronoun(), user.possessiveAdjective(),
                                                target.action("worship"),
                                                user.body.getRandomCock().describe(target),
                                                target.pronoun(), target.possessiveAdjective(),
                                                target.reflectivePronoun()));
                user.body.pleasure(target, target.body.getRandom("mouth"), user.body.getRandomCock(),
                                10 + Random.random(8), c, new SkillUsage<>(this, user, target));
                if (target.hasDick())
                    target.body.pleasure(target, target.body.getRandom("hands"), target.body.getRandomCock(),
                                    10 + Random.random(8), c, new SkillUsage<>(this, user, target));
                else if (target.hasPussy())
                    target.body.pleasure(target, target.body.getRandom("hands"), target.body.getRandomPussy(),
                                    10 + Random.random(8), c, new SkillUsage<>(this, user, target));
                break;
            case WORSHIP_PUSSY:
                c.write(user,
                                String.format("%s has a pussy. %s that pussy. %s humbly"
                                                + " %s for %s permission and %s is letting %s! %s enthusiastically"
                                                + " %s %s at %s feet and %s the beautiful %s with"
                                                + " almost religious zeal. At the same time, %s cannot contain %s lust"
                                                + " and simply must play with %s.", user.getName(),
                                                target.subjectAction("NEED", "NEEDS"),
                                                Formatter.capitalizeFirstLetter(target.pronoun()),
                                                target.action("beg"),
                                                user.nameOrPossessivePronoun(), user.pronoun(),
                                                target.directObject(),
                                                Formatter.capitalizeFirstLetter(target.pronoun()),
                                                target.action("throw"), target.reflectivePronoun(), user.possessiveAdjective(),
                                                target.action("worship"),
                                                user.body.getRandomPussy().describe(target),
                                                target.pronoun(), target.possessiveAdjective(),
                                                target.reflectivePronoun()));
                user.body.pleasure(target, target.body.getRandom("mouth"), user.body.getRandomPussy(),
                                10 + Random.random(8), c, new SkillUsage<>(this, user, target));
                if (target.hasDick())
                    target.body.pleasure(target, target.body.getRandom("hands"), target.body.getRandomCock(),
                                    10 + Random.random(8), c, new SkillUsage<>(this, user, target));
                else if (target.hasPussy())
                    target.body.pleasure(target, target.body.getRandom("hands"), target.body.getRandomPussy(),
                                    10 + Random.random(8), c, new SkillUsage<>(this, user, target));
                break;
            case MASTER_INSERT:
                if (c.getStance().havingSex(c, user)) {
                    c.write(user, Formatter
                                    .format("{self:SUBJECT-ACTION:order} {other:name-do} to be still.", user, target));
                } else {
                    c.write(user,
                                    String.format("With a mischievous smile, %s tells %s to be still,"
                                                    + " and that %s has a special surprise for %s.", user.getName(),
                                                    target.subject(), user.pronoun(), target.directObject()));
                }
                user.getSkills().stream().filter(skill -> Tactics.fucking.equals(skill.type(c, user)))
                                .map(s -> s.copy(user))
                                .filter(s -> s.requirements(c, user, target) && s.usable(c, user, target)).findAny()
                                .ifPresent(skill -> skill.resolve(c, user, target));
                break;
            case MASTER_FACESIT:
                c.write(user, String.format("%s stands over %s face and slowly lowers %s down onto it.",
                                user.getName(), target.nameOrPossessivePronoun(), user.reflectivePronoun()));
                c.setStance(new FaceSitting(user.getType(), target.getType()), target, false);
                break;
        }
    }

    private enum CommandType {
        STRIP_MASTER,
        STRIP_SLAVE,
        WORSHIP_COCK,
        WORSHIP_PUSSY,
        GIVE_BLOWJOB,
        GIVE_CUNNILINGUS,
        GIVE_ANNILINGUS,
        MASTURBATE,
        HURT_SELF,
        SUBMIT,
        MASTER_MOUNT,
        MASTER_FACESIT,
        MASTER_REVERSE_MOUNT,
        MASTER_BEHIND,
        MASTER_STRAPON,
        MASTER_INSERT
    }
}
