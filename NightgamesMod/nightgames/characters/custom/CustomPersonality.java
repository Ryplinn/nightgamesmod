package nightgames.characters.custom;

import nightgames.characters.BasePersonality;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.NPC;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.items.ItemAmount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Personality class for custom NPCs.
 */
public class CustomPersonality extends BasePersonality {
    private static final long serialVersionUID = 5222426700525293379L;
    private final NPCData data;

    CustomPersonality(NPCData data) {
        super(data.isStartCharacter());
        this.data = data;
    }

    @Override
    public void applyBasicStats(NPC selfNPC) {
        CustomNPC self = (CustomNPC) selfNPC;
        preferredAttributes = new ArrayList<>(data.getPreferredAttributes());

        self.outfitPlan.addAll(data.getTopOutfit());
        self.outfitPlan.addAll(data.getBottomOutfit());
        self.closet.addAll(self.outfitPlan);
        self.change();
        self.att = new HashMap<>(data.getStats().attributes);
        self.clearTraits();
        data.getStats().traits.forEach(self::addTraitDontSaveData);
        self.getArousal().setMax(data.getStats().arousal);
        self.getStamina().setMax(data.getStats().stamina);
        self.getMojo().setMax(data.getStats().mojo);
        self.getWillpower().setMax(data.getStats().willpower);
        self.setTrophy(data.getTrophy());
        self.custom = true;

        try {
            self.body = data.getBody().clone(self);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        self.initialGender = data.getSex();

        for (ItemAmount i : data.getStartingItems()) {
            self.gain(i.item, i.amount);
        }

        self.adjustTraits();
    }

    @Override
    public void applyStrategy(NPC selfNPC) {
        CustomNPC self = (CustomNPC) selfNPC;
        self.isStartCharacter = data.isStartCharacter();
        self.plan = data.getPlan();
        self.mood = Emotion.confident;
    }

    @Override public void setGrowth(NPC selfNPC) {
        CustomNPC self = (CustomNPC)selfNPC;
        self.setGrowth(data.getGrowth());
    }

    @Override
    public void rest(int time, NPC selfNPC) {
        CustomNPC self = (CustomNPC) selfNPC;
        for (ItemAmount i : data.getPurchasedItems()) {
            buyUpTo(i.item, i.amount, self);
        }
    }

    @Override public void constructLines(NPC selfNPC) {
        CustomNPC custom = (CustomNPC) selfNPC;
        for (String lineType : CharacterLine.ALL_LINES) {
            custom.addLine(lineType, (c, self, other) -> custom.data.getLine(lineType, c, self, other));
        }
    }

    @Override
    public String victory(Combat c, Result flag, NPC selfNPC) {
        CustomNPC self = (CustomNPC) selfNPC;
        self.getArousal().empty();
        return data.getLine("victory", c, self, c.getOpponent(self));
    }

    @Override
    public String defeat(Combat c, Result flag, NPC selfNPC) {
        CustomNPC self = (CustomNPC) selfNPC;
        return data.getLine("defeat", c, self, c.getOpponent(self));
    }

    @Override
    public String draw(Combat c, Result flag, NPC selfNPC) {
        CustomNPC self = (CustomNPC) selfNPC;
        return data.getLine("draw", c, self, c.getOpponent(self));
    }

    @Override
    public boolean fightFlight(Character opponent, NPC selfNPC) {
        CustomNPC self = (CustomNPC) selfNPC;
        return !self.mostlyNude() || opponent.mostlyNude();
    }

    @Override
    public boolean attack(Character opponent, NPC selfNPC) {
        return true;
    }

    @Override
    public String victory3p(Combat c, Character target, Character assist, NPC selfNPC) {
        CustomNPC self = (CustomNPC) selfNPC;
        if (target.human()) {
            return data.getLine("victory3p", c, self, assist);
        } else {
            return data.getLine("victory3pAssist", c, self, target);
        }
    }

    @Override
    public String intervene3p(Combat c, Character target, Character assist, NPC selfNPC) {
        CustomNPC self = (CustomNPC) selfNPC;
        if (target.human()) {
            return data.getLine("intervene3p", c, self, assist);
        } else {
            return data.getLine("intervene3pAssist", c, self, target);
        }
    }

    @Override
    public boolean fit(NPC selfNPC) {
        CustomNPC self = (CustomNPC) selfNPC;
        return !self.mostlyNude() && self.getStamina().percent() >= 50;
    }

    @Override
    public boolean checkMood(Combat c, Emotion mood, int value, NPC selfNPC) {
        CustomNPC self = (CustomNPC) selfNPC;
        return data.checkMood(self, mood, value);
    }

    @Override
    public String image(Combat c, NPC selfNPC) {
        CustomNPC self = (CustomNPC) selfNPC;
        Character other = null;
        if (c != null) {
            other = c.getOpponent(self);
        }
        return data.getPortraitName(c, self, other);
    }

    @Override
    public RecruitmentData getRecruitmentData(NPC selfNPC) {
        return data.getRecruitment();
    }

    @Override
    public AiModifiers getAiModifiers(NPC selfNPC) {
        return data.getAiModifiers();
    }

    @Override
    public Map<CommentSituation, String> getComments(Combat c, NPC selfNPC) {
        CustomNPC self = (CustomNPC) selfNPC;
        Map<CommentSituation, String> all = data.getComments();
        Map<CommentSituation, String> applicable = new HashMap<>();
        all.entrySet().stream().filter(e -> e.getKey().isApplicable(c, self, c.getOpponent(self)))
                        .forEach(e -> applicable.put(e.getKey(), e.getValue()));
        return applicable;
    }
}
