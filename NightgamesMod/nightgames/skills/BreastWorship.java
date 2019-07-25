package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.status.BodyFetish;

import java.util.Optional;

public class BreastWorship extends Skill {
    public BreastWorship() {
        super("Breast Worship");
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.worship);
        addTag(SkillTag.pleasureSelf);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return target.breastsAvailable() && c.getStance().reachTop(user) && c.getStance().front(user)
                        && (user.canAct() || c.getStance().enumerate() == Stance.nursing && user.canRespond())
                        && c.getStance().facing(user, target);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Result results = target.has(Trait.lactating) ? Result.special : Result.normal;
        int m = 8 + Random.random(6);
        writeOutput(c, results, user, target);
        if (user.has(Trait.silvertongue)) {
            m += 4;
        }
        target.body.pleasure(user, user.body.getRandom("mouth"), target.body.getRandom("breasts"), m, c,
                        new SkillUsage<>(this, user, target));
        if (user.hasDick() && (!user.hasPussy() || Random.random(2) == 0)) {
            user.body.pleasure(user, user.body.getRandom("hands"), user.body.getRandomCock(), m, c, new SkillUsage<>(this, user, target));
        } else if (user.hasPussy()) {
            user.body.pleasure(user, user.body.getRandom("hands"), user.body.getRandomPussy(), m,
                            c, new SkillUsage<>(this, user, target));
        } else {
            user.body.pleasure(user, user.body.getRandom("hands"), user.body.getRandomHole(), m, c, new SkillUsage<>(this, user, target));
        }
        if (results == Result.special) {
            user.temptWithSkill(c, target, target.body.getRandomBreasts(), (3 + target.body.getRandomBreasts().getSize()) * 2, this);
            target.buildMojo(c, 10);
        } else {
            target.buildMojo(c, 5);
        }
        return true;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 150;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        Optional<BodyFetish> fetish = user.body.getFetish("breasts");
        return user.isPetOf(target) || (fetish.isPresent() && fetish.get().magnitude >= .5);
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.normal) {
            return "You worshipfully circle your tongue around each of " + target.getName()
                            + "'s nipples, and start sucking like a newborn while furiously masturbating.";
        } else {
            return "You worshipfully circle your tongue around each of " + target.getName()
                            + "'s nipples, and start sucking like a newborn while furiously masturbating. "
                            + "Her milk slides smoothly down your throat, and you're left with a warm comfortable feeling.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.normal) {
            return user.getName()
                            + " worshipfully licks and sucks "+target.nameOrPossessivePronoun()+
                            " nipples while uncontrollably playing with "+user.reflectivePronoun()+".";
        } else {
            return String.format("%s worshipfully licks and sucks %s nipples while uncontrollably masturbating, drawing forth "
                            + "a gush of breast milk from %s teats. %s drinks deeply of %s milk, gurgling happily "
                            + "as more of the smooth liquid flows down %s throat.",
                            user.getName(), target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            user.getName(), target.possessiveAdjective(), user.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Worships your opponent's breasts.";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
