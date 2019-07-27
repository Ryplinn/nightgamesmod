package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;

public class NakedBloom extends Skill {

    NakedBloom() {
        super("Naked Bloom");
        addTag(SkillTag.stripping);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.spellcasting) >= 15;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().prone(user)
                        && !target.reallyNude();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 30;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Cast a spell to transform your opponent's clothes into flower petals: 20 Mojo";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
            c.write(target, target.nakedLiner(c, target));
        } else if (c.shouldPrintReceive(target, c)) {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }
        target.nudify();
        target.emote(Emotion.nervous, 10);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.stripping;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You chant a short spell and turn " + target.getName()
                        + "'s clothes into a burst of flowers. The cloud of flower petals flutters to the ground, exposing her nude body.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s mumbles a spell and %s suddenly surrounded by an eruption of flower petals. "
                        + "As the petals settle, %s %s %s %s been stripped completely "
                        + "naked.", user.subject(), target.subjectAction("are", "is"),
                        target.pronoun(), target.action("realize"), target.pronoun(),
                        target.action("have", "has"));
    }

}
