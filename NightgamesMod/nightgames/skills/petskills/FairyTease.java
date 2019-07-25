package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.ClothingSlot;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Tactics;

public class FairyTease extends SimpleEnemySkill {
    public FairyTease() {
        super("Fairy Tease");
        addTag(SkillTag.pleasure);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 5;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return super.requirements(c, user, target) && gendersMatch(user, target);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            int m = (int) (Random.random(10, 16) + Math.sqrt(user.getLevel())) / 2;
            if (target.crotchAvailable() && !c.getStance().penisInserted(target) && target.hasDick()) {
                c.write(user, Formatter.format("{self:SUBJECT} hugs {other:name-possessive} dick and rubs it with "
                                    + "{self:possessive} entire body until {other:pronoun-action:pull|pulls} {self:direct-object} off.",
                                    user, target));
                m += 5;
                target.body.pleasure(user, user.body.getRandom("skin"), target.body.getRandomCock(), m, c);
            } else if (target.hasDick() && !c.getStance().penisInserted(target) ) {
                c.write(user, Formatter.format("{self:SUBJECT} slips into {other:name-possessive} %s and plays with "
                                + "{other:possessive} penis until {other:pronoun-action:manage|manages} to remove {self:direct-object}.",
                                user, target, target.getOutfit().getTopOfSlot(ClothingSlot.bottom).getName()));
                target.body.pleasure(user, user.body.getRandom("skin"), target.body.getRandomCock(), m, c);
            } else if (target.breastsAvailable()) {
                c.write(user, Formatter.format("{self:SUBJECT} hugs {other:name-possessive} chest and rubs {other:possessive} nipples with "
                                + "{self:possessive} entire body until {other:pronoun-action:pull|pulls} {self:direct-object} off.",
                                user, target));
                m += 5;
                target.body.pleasure(user, user.body.getRandom("skin"), target.body.getRandomBreasts(), m, c);
            } else {
                c.write(user, Formatter.format("{self:SUBJECT} slips into {other:name-possessive} %s and plays with "
                                + "{other:possessive} sensitive nipples until {other:pronoun-action:manage|manages} to remove {self:direct-object}.",
                                user, target, target.getOutfit().getTopOfSlot(ClothingSlot.top).getName()));
                target.body.pleasure(user, user.body.getRandom("skin"), target.body.getRandomBreasts(), m, c);
            }
        } else {
            c.write(user, Formatter
                            .format("{self:SUBJECT} flies around the edge of the fight looking for an opening.", user, target));
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
        return Tactics.pleasure;
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
