package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Tactics;

import java.util.ArrayList;
import java.util.List;

public class SlimeMelt extends SimpleEnemySkill {
    public SlimeMelt() {
        super("Slime Melt");
        addTag(SkillTag.stripping);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target) && !(target.crotchAvailable() && target.breastsAvailable());
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 5;
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return 65;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        List<ClothingSlot> strippable = new ArrayList<>();
        if (!target.crotchAvailable() && target.outfit.slotShreddable(ClothingSlot.bottom)) {
            strippable.add(ClothingSlot.bottom);
        }
        if (!target.breastsAvailable() && target.outfit.slotShreddable(ClothingSlot.top)) {
            strippable.add(ClothingSlot.top);
        }
        ClothingSlot targetSlot = Random.pickRandomGuaranteed(strippable);
        if (rollSucceeded) {
            // should never be null here, since otherwise we can't use the skill          
            Clothing stripped = target.strip(targetSlot, c);
            c.write(user, Formatter.format("{self:SUBJECT} pounces on {other:name-do} playfully, "
                            + "and its corrosive body melts {other:possessive} %s as a fortunate accident.", 
                            user, target, stripped.getName()));
            target.emote(Emotion.nervous, 10);
        } else {
            c.write(user, Formatter.format("{self:SUBJECT} launches itself towards {other:name-do}, but {other:SUBJECT-ACTION:sidestep|sidesteps} it handily.",
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
