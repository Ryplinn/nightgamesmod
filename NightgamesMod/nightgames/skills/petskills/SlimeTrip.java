package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Tactics;
import nightgames.status.Falling;

public class SlimeTrip extends SimpleEnemySkill {
    public SlimeTrip() {
        super("Slime Trip");
        addTag(SkillTag.positioning);
        addTag(SkillTag.knockdown);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target) && !c.getStance().prone(target);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 5;
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return 50;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (rollSucceeded) {
            c.write(user, Formatter.format("{other:SUBJECT-ACTION:slip|slips} on {self:name-do} as it clings to {other:possessive} feet, losing {other:possessive} balance.",
                            user, target));
            target.add(c, new Falling(target.getType()));
        } else {
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:stumble|stumbles} as {self:subject} clings to {other:possessive} leg. "
                            + "{other:SUBJECT-ACTION:manage|manages} to catch {other:reflective} and {other:action:scrape|scrapes} off the clingy blob.",
                            user, target));
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
        return Tactics.stripping;
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
