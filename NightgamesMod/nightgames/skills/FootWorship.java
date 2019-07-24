package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Kneeling;
import nightgames.status.BodyFetish;

import java.util.Optional;

public class FootWorship extends Skill {
    public FootWorship() {
        super("Foot Worship");
        addTag(SkillTag.pleasure);
        addTag(SkillTag.worship);
        addTag(SkillTag.pleasureSelf);
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        Optional<BodyFetish> fetish = user.body.getFetish("feet");
        return user.isPetOf(target) || (fetish.isPresent() && fetish.get().magnitude >= .5);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return target.body.has("feet") && c.getStance().reachBottom(user) && user.canAct()
                        && !c.getStance().behind(user) && !c.getStance().behind(target)
                        && target.outfit.hasNoShoes();
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 150;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int m;
        int n;
        m = 8 + Random.random(6);
        n = 20;
        BodyPart mouth = user.body.getRandom("mouth");
        BodyPart feet = target.body.getRandom("feet");
        if (user.human()) {
            c.write(user, Formatter.format(deal(c, 0, Result.normal, user, target), user, target));
        } else {
            c.write(user, Formatter.format(receive(c, 0, Result.normal, user, target), user, target));
        }
        if (m > 0) {
            target.body.pleasure(user, mouth, feet, m, c, new SkillUsage<>(this, user, target));
            if (mouth.isErogenous()) {
                user.body.pleasure(user, feet, mouth, m, c, new SkillUsage<>(this, user, target));
            }
        }
        target.buildMojo(c, n);
        if (!c.getStance().sub(user)) {
            c.setStance(new Kneeling(target.getType(), user.getType()), user, true);
        }
        c.getCombatantData(user).toggleFlagOn("footworshipped", true);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new FootWorship();
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (!c.getCombatantData(user).getBooleanFlag("footworshipped")) {
            return "You throw yourself at " + target.nameOrPossessivePronoun()
                            + " dainty feet and start sucking on her toes. " + target.subject()
                            + " seems surprised at first, "
                            + "but then grins and shoves her toes further in to your mouth, eliciting a moan from you.";
        } else {
            return "You can't seem to bring yourself to stop worshipping her feet as your tongue makes its way down to {other:name-possessive} soles. {other:SUBJECT} presses her feet against your face and you feel more addicted to her feet.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (!c.getCombatantData(user).getBooleanFlag("footworshipped")) {
            return String.format("%s throws %s at %s feet. %s worshipfully grasps %s feet "
                            + "and starts licking between %s toes, all while %s face displays a mask of ecstasy.",
                            user.subject(), user.reflectivePronoun(), target.nameOrPossessivePronoun(),
                            user.subject(), target.possessiveAdjective(), target.possessiveAdjective(),
                            user.possessiveAdjective());
        }
        return String.format("%s can't seem to get enough of %s feet as %s continues to "
                        + "lick along the bottom of %s soles, %s face further lost in "
                        + "servitude as %s is careful not to miss a spot.", user.subject(),
                        target.nameOrPossessivePronoun(), user.pronoun(),
                        target.possessiveAdjective(), user.possessiveAdjective(),
                        user.pronoun());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Worship opponent's feet: builds mojo for opponent";
    }
}
