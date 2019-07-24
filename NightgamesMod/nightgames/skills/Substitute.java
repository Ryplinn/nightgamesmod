package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.stance.Behind;

public class Substitute extends Skill {

    Substitute() {
        super("Substitute");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.ninjutsu) >= 21;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !c.getStance()
                 .mobile(user)
                        && c.getStance()
                            .sub(user)
                        && user.canAct();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Use a decoy to slip behind your opponent: 10 Mojo.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            c.write(user, String.format("By the time %s realizes %s's pinning a dummy, you're already behind %s.",
                            target.getName(), target.pronoun(), target.directObject()));
        } else {
            c.write(user,
                            String.format("%s a good hold of %s body, and %s is surprisingly pliable..."
                                            + " %s wrestling a blow-up doll! The real %s is standing behind %s! How- How"
                                            + " did %s make the switch?!", target.subjectAction("take"),
                                            user.nameOrPossessivePronoun(), user.pronoun(),
                                            target.subjectAction("are", "is"), user.nameDirectObject(), target.directObject(),
                                            user.pronoun()));
        }
        user.emote(Emotion.dominant, 10);
        target.emote(Emotion.nervous, 10);
        c.setStance(new Behind(user.getType(), target.getType()), user, true);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Substitute();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

}
