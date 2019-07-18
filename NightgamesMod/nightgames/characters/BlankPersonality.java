package nightgames.characters;

import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.items.Item;

/**
 * Null-type personality, or maybe for those with a flat affect.
 */
public class BlankPersonality extends BasePersonality {
    private static final long serialVersionUID = 1L;

    public BlankPersonality() {
        super(false);
    }

    @Override public void constructLines(NPC selfNPC) {

    }

    @Override public String victory(Combat c, Result flag, NPC selfNPC) {
        return null;
    }

    @Override public String defeat(Combat c, Result flag, NPC selfNPC) {
        return null;
    }

    @Override public String victory3p(Combat c, Character target, Character assist, NPC selfNPC) {
        return null;
    }

    @Override public String intervene3p(Combat c, Character target, Character assist, NPC selfNPC) {
        return null;
    }

    @Override public String draw(Combat c, Result flag, NPC selfNPC) {
        return null;
    }

    @Override public boolean fightFlight(Character opponent, NPC selfNPC) {
        return false;
    }

    @Override public boolean attack(Character opponent, NPC selfNPC) {
        return false;
    }

    @Override public boolean fit(NPC selfNPC) {
        return false;
    }

    @Override public boolean checkMood(Combat c, Emotion mood, int value, NPC selfNPC) {
        return value >= 100;
    }

    @Override public void setGrowth(NPC selfNPC) {}

    @Override
    public void applyBasicStats(NPC selfNPC) {
        selfNPC.setTrophy(Item.MiscTrophy);
    }

    @Override
    public void applyStrategy(NPC selfNPC) {}
}
