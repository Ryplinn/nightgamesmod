package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;

public class PussyGrind extends Skill {

    private PussyGrind(String name, CharacterType self, int cooldown) {
        super(name, self, cooldown);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.fucking);
        addTag(SkillTag.petDisallowed);
    }

    public PussyGrind(CharacterType self) {
        this("Pussy Grind", self, 0);
    }

    public BodyPart getSelfOrgan() {
        return getSelf().body.getRandomPussy();
    }

    public BodyPart getTargetOrgan(Character target) {
        return target.body.getRandomPussy();
    }

    private boolean fuckable(Combat c, Character target) {
        return BodyPart.hasType(c.getStance().getPartsFor(c, getSelf(), target), "pussy")
                        && BodyPart.hasType(c.getStance().getPartsFor(c, target, getSelf()), "pussy");
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return fuckable(c, target) && c.getStance().mobile(getSelf()) && getSelf().canAct();
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        BodyPart selfO = getSelfOrgan();
        BodyPart targetO = getTargetOrgan(target);
        writeOutput(c, Result.normal, target);
        int m = 10 + Random.random(10);
        int otherm = 5 + Random.random(6);
        target.body.pleasure(getSelf(), selfO, targetO, m, c, this);
        getSelf().body.pleasure(target, targetO, selfO, otherm, c, this);
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new PussyGrind(user.getType());
    }

    @Override
    public int speed() {
        return 2;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.normal) {
            return Formatter.format(
                            "You rock your tangled bodies back and forth, grinding your loins into hers. {other:subject} passionately gasps as the stimulation overwhelms her. "
                                            + "Soon the floor is drenched with the fruits of your combined labor.",
                            getSelf(), target);
        }
        return "Bad stuff happened";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.normal) {
            return Formatter.format(
                            "{self:SUBJECT} rocks {other:name-possessive} tangled bodies back and forth, grinding {self:possessive}"
                            + " crotch into %s. {other:SUBJECT-ACTION:moan|moans} passionately as the stimulation overwhelms {other:direct-object}. "
                                            + "Soon the floor is drenched with the fruits of %s combined labor.",
                            getSelf(), target, target.human() ? "yours" : target.useFemalePronouns() ? "hers" : "his",
                                            c.bothPossessive(target));
        }
        return "Bad stuff happened";
    }

    @Override
    public String describe(Combat c) {
        return "Grinds your pussy against your opponent's.";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
