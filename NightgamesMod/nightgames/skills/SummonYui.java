package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.GameState;
import nightgames.pet.CharacterPet;

public class SummonYui extends Skill {
    SummonYui() {
        super("Summon Yui");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return GameState.getGameState().characterPool.getCharacterByType("Yui").getAffection(user) >= 10;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().prone(user)
                        && c.getPetsFor(user).size() < user.getPetLimit();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Summon Yui to help you in your fight. Costs a bit of affection.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        NPC yui = GameState.getGameState().characterPool.getNPCByType("Yui");
        int power = (user.getLevel() + target.getLevel()) / 2;
        int ac = 4 + power / 3;
        writeOutput(c, Result.normal, user, target);
        yui.gainAffection(user, -1);
        c.addPet(user, new CharacterPet(user, yui, power, ac).getSelf());

        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.summoning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You pull out one of those tattered scrolls Yui has given you and unroll it. "
                        + "With a firm image of the blonde girl's face in your mind, you smear the ink circle drawn on the page. "
                        + "A split second later the ink on the page seems to twist and blur until it finally coalesces into the loyal ninja's form.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format("{self:SUBJECT-ACTION:pull|pulls} out a tattered scroll and {self:action:unroll|unrolls} it. "
                        + "{self:PRONOUN} smears the ink circle drawn on the page with {self:possessive} thumb and drops it onto the ground. "
                        + "A split second later the ink on the page seems to twist and blur until it finally coalesces into the familiar ninja's form", user, target);
    }
}
