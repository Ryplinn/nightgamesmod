package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Hypersensitive;
import nightgames.status.addiction.AddictionSymptom;
import nightgames.status.addiction.AddictionType;

public class DemandArousal extends Skill {

    public DemandArousal(Character self) {
        super("Demand Arousal", self, 4);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.ControlledRelease);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canRespond() && c.getStance().facing(getSelf(), target) 
                        && target.checkAddiction(AddictionType.MIND_CONTROL, getSelf());
    }

    @Override
    public String describe(Combat c) {
        return "Inspire arousal in your opponent. Weakens your control.";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        AddictionSymptom addict = target.getAddiction(AddictionType.MIND_CONTROL)
                            .get();
        int dmg = (int) ((20 + Random.randomdouble() * 20) * addict.getMagnitude());
        float alleviation;

        String msg = Formatter.format("\"<i><b>{other:name}. Listen to me.</b></i>\" {self:NAME-POSSESSIVE}"
                                + " looks deeply into {other:possessive} eyes, and {self:possessive}"
                                + " words ", getSelf(), target);
        switch (addict.getSeverity()) {
            case HIGH:
                msg = Formatter.format("pound {other:name-possessive} psyche like a hammer, each blow echoing throughout"
                                + " your body. {self:PRONOUN-ACTION:speak|speaks}, but "
                                + "{other:pronoun} can't even hear {self:direct-object}. {other:POSSESSIVE}"
                                + " body does, though. It grows hot, and all of {other:possessive}"
                                + " skin becomes incredibly sensitive. Once {self:name} has stopped speaking,"
                                + " {other:pronoun-action:are|is} aroused out of {other:possessive} mind."
                                , getSelf(), target);
                alleviation = AddictionSymptom.MED_INCREASE;
                target.add(c, new Hypersensitive(target, 2));
                break;
            case LOW:
                msg += Formatter.format("seem to have more weight behind them than usual. \"<i>"
                                + "{other:name}, can you feel your %s?</i> Strangely, yes,"
                                + " {other:pronoun-action:do|does}. {other:PRONOUN-ACTION:feel|feels}"
                                + " a heat pour into {other:possessive} {other:main-genitals} as"
                                + " {self:subject-action:speak|speaks}.", getSelf(), target,
                                target.hasDick() ? "dick getting hard" : target.hasPussy()
                                                ? "pussy getting wet" : "nipples tingling");
                alleviation = AddictionSymptom.LOW_INCREASE;
                break;
            case MED:
                msg = Formatter.format("resonate powerfully in your mind. \"<i>You are getting"
                                + " very excited, {other:name}. Your {other:main-genitals} obey me."
                                + " You </i>will<i> cum for me, {other:name}.</i>\"", getSelf(), target);
                alleviation = AddictionSymptom.MED_INCREASE * .67f;
                break;
            case NONE:
            default:
                alleviation = 0.f;
                msg = Formatter.format("<b>[[[DemandArousal executed even though the player isn't noticably addicted...]]]</b>",
                                getSelf(), target);
                break;
        }
        c.write(getSelf(), Formatter.format(msg, getSelf(), target));
        target.temptWithSkill(c, getSelf(), null, dmg, this);
        addict.alleviate(c, alleviation);

        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new DemandArousal(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return null;
    }

}
