package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;

public class OrgasmicThrust extends Thrust {

    public OrgasmicThrust() {
        super("Orgasmic Thrust");
        addTag(SkillTag.pleasureSelf);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return false;
    }

    public BodyPart getSelfOrgan(Combat c, Character user, Character target) {
        BodyPart part = super.getSelfOrgan(c, user, target);
        if (part != null && part.isType("cock")) {
            return part;
        }
        return null;
    }

    public int[] getDamage(Combat c, Character user, Character target) {
        int[] results = new int[2];

        int m = Random.random(25, 40);
        if (c.getStance().anallyPenetrated(c, target) && user.has(Trait.assmaster)) {
            m *= 1.5;
        }

        results[0] = m;

        return results;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 0;
    }

    @Override
    public Skill copy(Character user) {
        return new OrgasmicThrust();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.anal) {
            return Formatter.format("As {self:pronoun-action:are|is} about to cum, {self:subject} rapidly and almost involuntarily "
                            + "{self:action:pump|pumps} {other:name-possessive} ass with {self:possessive} rock hard cock. "
                            + "The only thing {other:pronoun} can manage to do is try and hold on.", user, target);
        } else {
            return Formatter.format("As {self:pronoun-action:are|is} about to cum, {self:subject} rapidly and almost involuntarily "
                            + "{self:action:pump|pumps} {other:name-possessive} hot sex with {self:possessive} rock hard cock. "
                            + "The only thing {other:pronoun} can manage to do is try and hold on.", user, target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return deal(c, damage, modifier, user, target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Involuntary skill";
    }

    @Override
    public Character getDefaultTarget(Combat c, Character user) {
        return c.getStance().getPartner(c, user);
    }

    @Override
    public boolean makesContact() {
        return true;
    }

    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
