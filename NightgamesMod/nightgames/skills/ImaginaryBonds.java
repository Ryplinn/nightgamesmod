package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.Bound;

public class ImaginaryBonds extends Skill {

    private ImaginaryBonds() {
        super("Binding", 4);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.hypnotism) >= 9;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return target.isHypnotized();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Trap your opponent with mental bondage.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        target.add(c, new Bound(target.getType(), 45 + 5 * Math.sqrt(user.get(Attribute.hypnotism)), "imaginary bindings"));
        target.emote(Emotion.nervous, 5);
        user.emote(Emotion.confident, 20);
        user.emote(Emotion.dominant, 10);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new ImaginaryBonds();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format("You lean close to {other:name-do} and tell her that {other:pronoun} cannot move {other:possessive} body. "
                        + "{other:NAME-POSSESSIVE} eyes widen as your hypnotic suggestion rings true in {other:possessive} mind.", user, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format("{self:SUBJECT} leans close to you and helpfully informs {other:name-do} that {other:pronoun} cannot move your body. "
                        + "Of course! why didn't {other:pronoun} notice this earlier? ", user, target);
    }

}
