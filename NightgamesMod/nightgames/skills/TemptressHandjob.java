package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.FiredUp;

public class TemptressHandjob extends Handjob {

    TemptressHandjob() {
        super("Skillful Handjob");
        addTag(SkillTag.usesHands);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.temptress) && user.get(Attribute.seduction) >= 5;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Rub your opponent's dick with supreme skill.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int m = 7 + Random.random(user.get(Attribute.technique) / 2);

        if (target.roll(user, accuracy(c, user, target))) {
            if (!target.body.getRandomCock().isReady(target)) {
                m -= 7;
                target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandomCock(), m, c, new SkillUsage<>(this, user, target));
                if (target.body.getRandomCock().isReady(target)) {
                    // Was flaccid, got hard
                    c.write(user, deal(c, 0, Result.special, user, target));
                    user.add(c, new FiredUp(user.getType(), target.getType(), "hands"));
                } else {
                    // Was flaccid, still is
                    c.write(user, deal(c, 0, Result.weak, user, target));
                }
            } else {
                FiredUp status = (FiredUp) user.status.stream().filter(s -> s instanceof FiredUp).findAny()
                                .orElse(null);
                int stack = status == null || !status.getPart().equals("hands") ? 0 : status.getStack();
                c.write(user, deal(c, stack, Result.normal, user, target));
                target.body.pleasure(user, user.body.getRandom("hands"), target.body.getRandomCock(),
                                m + m * stack / 2, c, new SkillUsage<>(this, user, target));
                user.add(c, new FiredUp(user.getType(), target.getType(), "hands"));
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
                return String.format("%s down to %s groin, but %s pulls %s hips back.",
                                user.subjectAction("reach", "reaches"), target.nameOrPossessivePronoun(),
                                target.pronoun(), target.possessiveAdjective());
            case weak:
                return String.format("%s %s limp %s and %s it expertly, but it remains flaccid despite %s best efforts.",
                                user.subjectAction("grab"), target.nameOrPossessivePronoun(),
                                target.body.getRandomCock().describe(target), user.action("fondle"),
                                user.possessiveAdjective());
            case special:
                return String.format(
                                "%s %s limp %s and %s it expertly, and it grows fully hard under %s skilled touch.",
                                user.subjectAction("grab"), target.nameOrPossessivePronoun(),
                                target.body.getRandomCock().describe(target), user.action("massage"),
                                user.possessiveAdjective());
            default: // should be Result.normal
                // already hard
                switch (damage) {
                    case 0:
                        return String.format(
                                        "%s hold of %s %s and %s %s fingers over it briskly, hitting all the right spots.",
                                        user.subjectAction("take"), target.nameOrPossessivePronoun(),
                                        target.body.getRandomCock().describe(target), user.action("run"),
                                        user.possessiveAdjective());
                    case 1:
                        return String.format(
                                        "%s hold on %s %s tightens, and where once there were gentle touches there are now firm jerks.",
                                        user.nameOrPossessivePronoun(), target.nameOrPossessivePronoun(),
                                        target.body.getRandomCock().describe(target));
                    default:
                        return String.format(
                                        "%s latched on to %s %s with both hands now, twisting them in a fierce milking movement and eliciting pleasured groans from %s.",
                                        user.subjectAction("have", "has"), target.nameOrPossessivePronoun(),
                                        target.body.getRandomCock().describe(target), target.directObject());
                }
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        // use formatted strings in deal
        return deal(c, damage, modifier, user, target);
    }
    
    @Override
    public Stage getStage() {
        return Stage.FOREPLAY;
    }
}
