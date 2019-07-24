package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.stance.StandingOver;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

public class Dive extends Skill {

    public Dive() {
        super("Dive");
        addTag(SkillTag.positioning);
        addTag(SkillTag.suicidal);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.submission) >= 1;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().en == Stance.neutral;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Hit the deck! Avoids some attacks.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        c.setStance(new StandingOver(target.getType(), user.getType()), target, true);
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }

        if (user.checkAddiction(AddictionType.MIND_CONTROL, target)) {
            user.unaddictCombat(AddictionType.MIND_CONTROL,
                            target, Addiction.LOW_INCREASE, c);
            c.write(user, "Acting submissively voluntarily reduces Mara's control over " + user.nameDirectObject());
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Dive();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You take evasive action and dive to the floor. Ok, you're on the floor now. That's as far as you planned.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.getName() + " dives dramatically away and lands flat on the floor.";
    }

}
