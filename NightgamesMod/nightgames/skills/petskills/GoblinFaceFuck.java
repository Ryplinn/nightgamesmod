package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;
import nightgames.status.Shamed;
import nightgames.status.Stsflag;

public class GoblinFaceFuck extends SimpleEnemySkill {
    public GoblinFaceFuck() {
        super("Goblin Face Fuck");
        addTag(SkillTag.pleasure);
        addTag(SkillTag.debuff);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target) && c.getStance().prone(target)
                        && user.hasDick() && c.getStance().faceAvailable(target) && !target.is(Stsflag.shamed);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 5;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        c.write(user, Formatter.format("{self:SUBJECT} straddles {other:name-possessive} head, giving {other:direct-object} an eyeful of her assorted genitals. "
                        + "She pulls the vibrator out of her pussy, causing a rain of love juice to splash {other:possessive} face. "
                        + "{self:SUBJECT} then wipes her leaking cock on {other:name-possessive} forehead, smearing {other:direct-object} with precum. "
                        + "{other:NAME-POSSESSIVE} face flushes with shame as the goblin marks {other:direct-object} with her fluids.", user, target));
        user.body.pleasure(target, target.body.getRandom("skin"), user.body.getRandomCock(), 10, c);
        target.add(c, new Shamed(target.getType()));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new GoblinFaceFuck();
    }

    @Override
    public int speed(Character user) {
        return 8;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
