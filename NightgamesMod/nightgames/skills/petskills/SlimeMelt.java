package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

import java.util.ArrayList;
import java.util.List;

public class SlimeMelt extends SimpleEnemySkill {
    public SlimeMelt(Character self) {
        super("Slime Melt", self);
        addTag(SkillTag.stripping);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return super.usable(c, target) && !(target.crotchAvailable() && target.breastsAvailable());
    }

    @Override
    public int getMojoCost(Combat c) {
        return 5;
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return 65;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        List<ClothingSlot> strippable = new ArrayList<>();
        if (!target.crotchAvailable() && target.outfit.slotShreddable(ClothingSlot.bottom)) {
            strippable.add(ClothingSlot.bottom);
        }
        if (!target.breastsAvailable() && target.outfit.slotShreddable(ClothingSlot.top)) {
            strippable.add(ClothingSlot.top);
        }
        ClothingSlot targetSlot = Random.pickRandom(strippable).get();
        if (target.roll(getSelf(), accuracy(c, target))) {
            // should never be null here, since otherwise we can't use the skill          
            Clothing stripped = target.strip(targetSlot, c);
            c.write(getSelf(), Formatter.format("{self:SUBJECT} pounces on {other:name-do} playfully, "
                            + "and its corrosive body melts {other:possessive} %s as a fortunate accident.", 
                            getSelf(), target, stripped.getName()));
            target.emote(Emotion.nervous, 10);
        } else {
            c.write(getSelf(), Formatter.format("{self:SUBJECT} launches itself towards {other:name-do}, but {other:SUBJECT-ACTION:sidestep|sidesteps} it handily.",
                            getSelf(), target));
            return false;
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new SlimeMelt(user);
    }

    @Override
    public int speed() {
        return 8;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.stripping;
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
