package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.Bound;

public class Binding extends Skill {

    public Binding() {
        super("Binding", 4);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.spellcasting) >= 9;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && !c.getStance().sub(user) && !c.getStance().prone(user)
                        && !c.getStance().prone(target) && user.canAct();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 20;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Bind your opponent's hands with a magic seal.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        target.add(c, new Bound(target.getType(), 45 + 3 * Math.sqrt(user.getAttribute(Attribute.spellcasting)), "seal"));
        target.emote(Emotion.nervous, 5);
        user.emote(Emotion.confident, 20);
        user.emote(Emotion.dominant, 10);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You cast a binding spell on " + target.getName() + ", holding her in place.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.getName()
                        + " gestures at "+target.nameDirectObject()+" and casts a spell. A ribbon of light wraps around "+target.possessiveAdjective()+" wrists and holds them in place.";
    }

}
