package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.stance.StandingOver;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

public class Dive extends Skill {

    public Dive(CharacterType self) {
        super("Dive", self);
        addTag(SkillTag.positioning);
        addTag(SkillTag.suicidal);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return getSelf().getPure(Attribute.submission) >= 1;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && c.getStance().en == Stance.neutral;
    }

    @Override
    public String describe(Combat c) {
        return "Hit the deck! Avoids some attacks.";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        c.setStance(new StandingOver(target.getType(), self), target, true);
        if (getSelf().human()) {
            c.write(getSelf(), deal(c, 0, Result.normal, target));
        } else {
            c.write(getSelf(), receive(c, 0, Result.normal, target));
        }

        if (getSelf().checkAddiction(AddictionType.MIND_CONTROL, target)) {
            getSelf().unaddictCombat(AddictionType.MIND_CONTROL, 
                            target, Addiction.LOW_INCREASE, c);
            c.write(getSelf(), "Acting submissively voluntarily reduces Mara's control over " + getSelf().nameDirectObject());
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Dive(user.getType());
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return "You take evasive action and dive to the floor. Ok, you're on the floor now. That's as far as you planned.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return getSelf().getName() + " dives dramatically away and lands flat on the floor.";
    }

}
