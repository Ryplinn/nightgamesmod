package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.FiredUp;

public class TemptressBlowjob extends Blowjob {

    TemptressBlowjob() {
        super("Skillful Blowjob");
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.oral);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return super.priorityMod(c, user) + 1.5f;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.temptress);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Use your supreme oral skills on your opponent's dick.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        int m = 10 + Random.random(user.getAttribute(Attribute.technique) / 2);

        if (user.has(Trait.silvertongue)) {
            m += 4;
        }

        if (rollSucceeded) {
            if (!target.body.getRandomCock().isReady(target)) {
                m -= 7;
                target.body.pleasure(user, user.body.getRandom("mouth"), target.body.getRandomCock(), m, c, new SkillUsage<>(this, user, target));
                if (target.body.getRandomCock().isReady(target)) {
                    // Was flaccid, got hard
                    c.write(user, deal(c, 0, Result.special, user, target));
                    user.add(c, new FiredUp(user.getType(), target.getType(), "mouth"));
                } else {
                    // Was flaccid, still is
                    c.write(user, deal(c, 0, Result.weak, user, target));
                }
            } else {
                FiredUp status = (FiredUp) user.status.stream().filter(s -> s instanceof FiredUp).findAny()
                                .orElse(null);
                int stack = status == null || !status.getPart().equals("mouth") ? 0 : status.getStack();
                c.write(user, deal(c, stack, Result.normal, user, target));
                target.body.pleasure(user, user.body.getRandom("mouth"), target.body.getRandomCock(),
                                m + m * stack / 2, c, new SkillUsage<>(this, user, target));
                user.add(c, new FiredUp(user.getType(), target.getType(), "mouth"));
            }
        } else {
            c.write(user, deal(c, 0, Result.miss, user, target));
        }
        return true;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        switch (modifier) {
            case miss:
                return String.format("%s towards %s %s, but %s %s hips back.", user.subjectAction("move"),
                                target.nameOrPossessivePronoun(), target.body.getRandomCock().describe(target),
                                target.pronoun(), target.action("pull"));
            case weak:
                return String.format(
                                "%s up %s flaccid %s, doing everything %s"
                                                + " can to get it hard, but %s %s back before %s can manage it.",
                                user.subjectAction("gobble"), target.nameOrPossessivePronoun(),
                                target.body.getRandomCock().describe(target), user.pronoun(), target.pronoun(),
                                target.action("pull"), user.pronoun());
            case special:
                return String.format(
                                "%s %s %s into %s mouth and %s on it powerfully. It hardens"
                                                + " swiftly, as if %s pulled the blood right into it.",
                                user.subjectAction("take"), target.nameOrPossessivePronoun(),
                                target.body.getRandomCock().describe(target), user.possessiveAdjective(),
                                user.action("suck"), user.pronoun());
            default: // should be Result.normal
                switch (damage) {
                    case 0:
                        return String.format(
                                        "%s to town on %s %s, licking it all over."
                                                        + " Long, slow licks along the shaft and small, swift licks"
                                                        + " around the head cause %s to groan in pleasure.",
                                        user.subjectAction("go", "goes"), target.nameOrPossessivePronoun(),
                                        target.body.getRandomCock().describe(target), target.directObject());
                    case 1:
                        return String.format("%s %s lips around the head of %s hard and wet %s "
                                        + "and %s on it forcefully while swirling %s tongue rapidly"
                                        + " around. At the same time, %s hands are massaging and"
                                        + " caressing every bit of sensitive flesh not covered by" + " %s mouth.",
                                        user.subjectAction("lock"), user.possessiveAdjective(),
                                        target.nameOrPossessivePronoun(), target.body.getRandomCock().describe(target),
                                        user.action("suck"), user.possessiveAdjective(),
                                        user.possessiveAdjective(), user.possessiveAdjective());
                    default:
                        return String.format("%s bobbing up and down now, hands still working"
                                        + " on any exposed skin while %s %s, %s and even %s all over %s"
                                        + " over-stimulated manhood. %s %s not even trying to hide %s"
                                        + " enjoyment, and %s %s loudly every time %s teeth graze" + " %s shaft.",
                                        user.subjectAction("are", "is"), user.pronoun(),
                                        user.action("lick"), user.action("suck"),
                                        user.action("nibble"), target.possessiveAdjective(),
                                        target.nameDirectObject(), target.action("are", "is"),
                                        target.possessiveAdjective(), target.pronoun(), target.action("grunt"),
                                        user.possessiveAdjective(), target.possessiveAdjective());
                }
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return deal(c, damage, modifier, user, target);
    }

}
