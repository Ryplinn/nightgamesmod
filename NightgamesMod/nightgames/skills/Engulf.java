package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.custom.CharacterLine;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Engulfed;
import nightgames.stance.Stance;

public class Engulf extends CounterBase {
    public Engulf() {
        super("Engulf", 5, counterDesc(), 2);
        addTag(SkillTag.fucking);
        addTag(SkillTag.positioning);
    }

    private static String counterDesc() {
            return "{self:subject-action:have} spread {self:reflective} thin, {self:if-human:ready to engulf your opponent}{self:if-nonhuman:arms opened invitingly}.";
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 2;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.slime) > 19;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().en != Stance.engulfed && target.mostlyNude();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 30;
    }

    @Override
    public int speed(Character user) {
        return -20;
    }

    @Override
    public String getBlockedString(Combat c, Character user, Character target) {
        return Formatter.format(
                        "{self:SUBJECT-ACTION:move|moves} to engulf {other:subject} "
                                        + "in {self:possessive} slime, but {other:pronoun} stays out of {self:possessive} reach.",
                        user, target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Set up a counter to engulf the opponent in your slime";
    }

    @Override
    public Skill copy(Character user) {
        return new Engulf();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "You spread out your slime, getting ready to trap {other:name} in it.",
                        user, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "{self:NAME}'s body spreads out across the floor. From {self:possessive} lowered position, "
                        + "{self:pronoun} smiles deviously up at {other:name-do}, goading {other:direct-object} into an attack.",
                        user, target);
    }

    @Override
    public void resolveCounter(Combat c, Character user, Character target) {
        String msg = "As {other:subject-action:approach|approaches}, {self:subject} suddenly {self:action:rush|rushes}"
                        + " forward, folding {self:possessive} slime around {other:direct-object}. ";
        if (!target.outfit.isNude()) {
            target.nudify();
            msg += "{self:NAME-POSSESSIVE} slime vibrates wildly around {other:direct-object}, causing"
                            + " {other:possessive} clothes to dissolve without a trace. ";
        }
        msg += "As {self:pronoun} {self:action:reform|reforms} {self:possessive} body around {other:direct-object},"
                        + " {self:possessive} head appears besides {other:possessive}. {self:SUBJECT-ACTION:giggle|giggles}"
                        + " softly into {other:possessive} ear as {self:possessive} slime massages {other:possessive} ";
        if (target.hasDick())
            msg += "cock, ";
        if (target.hasBalls())
            msg += "balls, ";
        if (target.hasPussy())
            msg += "pussy, ";
        msg += "ass and every other inch of {other:possessive} skin. ";
        msg += user.getRandomLineFor(CharacterLine.ENGULF_LINER, c, target);
        c.write(user, Formatter.format(msg, user, target));
        c.setStance(new Engulfed(user.getType(), target.getType()), user, true);
        user.emote(Emotion.dominant, 50);
        user.emote(Emotion.horny, 30);
        target.emote(Emotion.nervous, 50);
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
