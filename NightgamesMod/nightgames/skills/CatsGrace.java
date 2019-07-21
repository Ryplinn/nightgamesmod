package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.stance.Stance;
import nightgames.status.Nimble;
import nightgames.status.Stsflag;

public class CatsGrace extends Skill {

    CatsGrace(CharacterType self) {
        super("Cat's Grace", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.animism) >= 3;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return !getSelf().is(Stsflag.nimble) && c.getStance().en == Stance.neutral && getSelf().canAct() && c.getStance().mobile(getSelf())
                        && getSelf().getArousal().percent() >= 20;
    }

    @Override
    public String describe(Combat c) {
        return "Use your instinct to nimbly avoid attacks";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        writeOutput(c, Result.normal, target);
        getSelf().add(c, new Nimble(self, 4));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new CatsGrace(user.getType());
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return "You rely on your animal instincts to quicken your movements and avoid attacks.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return getSelf().getName()
                        + " focuses for a moment and "+getSelf().possessiveAdjective()
                        +" movements start to speed up and become more animalistic.";
    }

}
