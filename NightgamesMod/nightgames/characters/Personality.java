package nightgames.characters;

import nightgames.actions.Action;
import nightgames.actions.Movement;
import nightgames.characters.body.BodyPart;
import nightgames.characters.custom.AiModifiers;
import nightgames.characters.custom.CommentSituation;
import nightgames.characters.custom.RecruitmentData;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.skills.Skill;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A Personality is the preferences and tendencies of a particular NPC.
 */
public interface Personality extends Serializable {
    Skill chooseSkill(HashSet<Skill> available, Combat c, NPC selfNPC);

    Action move(Collection<Action> available, Collection<Movement> radar, NPC selfNPC);

    void rest(int time, NPC selfNPC);

    void constructLines(NPC selfNPC);

    String victory(Combat c, Result flag, NPC selfNPC);

    String defeat(Combat c, Result flag, NPC selfNPC);

    String victory3p(Combat c, Character target, Character assist, NPC selfNPC);

    String intervene3p(Combat c, Character target, Character assist, NPC selfNPC);

    String draw(Combat c, Result flag, NPC selfNPC);

    boolean fightFlight(Character opponent, NPC selfNPC);

    boolean attack(Character opponent, NPC selfNPC);

    void ding(Character self);

    boolean fit(NPC selfNPC);

    boolean checkMood(Combat c, Emotion mood, int value, NPC selfNPC);

    String image(Combat c, NPC selfNPC);

    void pickFeat(NPC selfNPC);

    String describeAll(Combat c, Character self);

    RecruitmentData getRecruitmentData(NPC selfNPC);

    AiModifiers getAiModifiers(NPC selfNPC);

    void setAiModifiers(AiModifiers mods, NPC selfNPC);

    void resetAiModifiers(NPC selfNPC);

    String resist3p(Combat combat, Character target, Character assist);
    List<PreferredAttribute> getPreferredAttributes();

    Map<CommentSituation, String> getComments(Combat c, NPC selfNPC);

    default void resolveOrgasm(Combat c, NPC self, Character opponent, BodyPart selfPart, BodyPart opponentPart, int times,
                    int totalTimes) {
        // no op
    }

    default void eot(Combat c, NPC selfNPC, Character opponent) {
        // noop
    }

    void applyBasicStats(NPC selfNPC);
    void applyStrategy(NPC selfNPC);

    void setGrowth(NPC npc);
}
