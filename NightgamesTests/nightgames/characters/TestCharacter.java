package nightgames.characters;

import nightgames.actions.Action;
import nightgames.combat.Combat;
import nightgames.combat.Encounter;
import nightgames.combat.Encs;
import nightgames.combat.Result;
import nightgames.skills.Tactics;
import nightgames.trap.Trap;

import java.util.Optional;

/**
 * TODO: Write class-level documentation.
 */
public class TestCharacter extends Character {
    TestCharacter() {
        this(1);
    }

    private TestCharacter(int level) {
        super(CharacterType.get("TestCharacter"), "TestCharacter", level);
    }

    @Override public void ding(Combat c) {

    }

    @Override public void detect() {

    }

    @Override public void doAction(Action action) {

    }

    @Override public FightIntent faceOff(Character opponent, Encounter enc) {
        return FightIntent.flee;
    }

    @Override public Encs spy(Character opponent, Encounter enc) {
        return Encs.wait;
    }

    @Override public String describe(int per, Combat c) {
        return null;
    }

    @Override public void victory(Combat c, Result flag) {

    }

    @Override public void defeat(Combat c, Result flag) {

    }

    @Override public void intervene3p(Combat c, Character target, Character assist) {

    }

    @Override public void victory3p(Combat c, Character target, Character assist) {

    }

    @Override public boolean resist3p(Combat c, Character target, Character assist) {
        return false;
    }

    @Override public boolean chooseSkill(Combat c) {
        return false;
    }

    @Override public Optional<Action> move() {
        return Optional.empty();
    }

    @Override public void draw(Combat c, Result flag) {

    }

    @Override public boolean human() {
        return false;
    }

    @Override public String bbLiner(Combat c, Character target) {
        return null;
    }

    @Override public String nakedLiner(Combat c, Character target) {
        return null;
    }

    @Override public String stunLiner(Combat c, Character target) {
        return null;
    }

    @Override public String taunt(Combat c, Character target) {
        return null;
    }

    @Override public void decideIntervene(Encounter fight, Character p1, Character p2) {

    }

    @Override public Encs showerSceneResponse(Character target, Encounter encounter) {
        return Encs.wait;
    }

    @Override public void afterParty() {

    }

    @Override public void emote(Emotion emo, int amt) {

    }

    @Override public String challenge(Character other) {
        return null;
    }

    @Override public void promptTrap(Encounter fight, Character target, Trap trap) {

    }

    @Override public void counterattack(Character target, Tactics type, Combat c) {

    }

    @Override public String getPortrait(Combat c) {
        return null;
    }
}
