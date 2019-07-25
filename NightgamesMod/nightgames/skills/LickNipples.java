package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;

public class LickNipples extends Skill {

    public LickNipples() {
        super("Lick Nipples");
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return target.breastsAvailable() && c.getStance().reachTop(user) && c.getStance().front(user)
                        && user.canAct() && c.getStance().facing(user, target);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 7;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return c.getStance().en != Stance.neutral ? 200 : 70;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int m = 3 + Random.random(6);
        if (target.roll(user, accuracy(c, user, target))) {
            writeOutput(c, Result.normal, user, target);
            if (user.has(Trait.silvertongue)) {
                m += 4;
            }
            target.body.pleasure(user, user.body.getRandom("mouth"), target.body.getRandom("breasts"), m, c, new SkillUsage<>(this, user, target));

        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.seduction) >= 14;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You go after " + target.getName() + "'s nipples, but she pushes you away. (Maybe try getting closer?)";
        } else {
            return "You slowly circle your tongue around each of " + target.getName()
                            + "'s nipples, making her moan and squirm in pleasure.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to suck on %s chest, but %s %s %s.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("avoid"), user.directObject());
        } else {
            return String.format("%s licks and sucks %s nipples, sending a surge of excitement straight to %s groin.",
                            user.subject(), target.nameOrPossessivePronoun(), target.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Suck your opponent's nipples";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
