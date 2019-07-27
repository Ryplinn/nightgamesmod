package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.ClothingTrait;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Tactics;
import nightgames.skills.damage.DamageType;
import nightgames.status.Stsflag;

public class ImpAssault extends SimpleEnemySkill {
    public ImpAssault() {
        super("Imp Assault");
        addTag(SkillTag.staminaDamage);
        addTag(SkillTag.positioning);
        addTag(SkillTag.hurt);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target) && target.stunned() && target.is(Stsflag.braced);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 5;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (rollSucceeded) {
            int m = 8 + user.getLevel() + Random.random(5);
            if (target.hasBalls()) {
                if (target.has(Trait.achilles) && !target.has(ClothingTrait.armored)) {
                    m += 8;
                }
                c.write(user, Formatter.format("While {other:name-possessive} attention is focused on {self:possessive} master, "
                                + "{self:subject} creeps close to {other:direct-object} and uppercuts {other:direct-object} in the balls.", 
                                user, target));
                target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, m));
                target.emote(Emotion.nervous, 10);
                target.emote(Emotion.angry, 10);
            } else {
                c.write(user, Formatter.format("{self:SUBJECT} runs up to {other:name-do} and punches {other:direct-object} in the gut.", user, target));
                target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, m));
                target.emote(Emotion.nervous, 10);
                target.emote(Emotion.angry, 10);
            }
        } else {
            c.write(user, String.format("%s tries to kick %s but %s %s %s small legs before they reach %s.",
                            user.subject(), target.nameDirectObject(),
                            target.pronoun(), target.action("catch", "catches"),
                            user.possessiveAdjective(),
                            target.directObject()));
            return false;
        }
        return true;
    }

    @Override
    public int speed(Character user) {
        return 8;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
