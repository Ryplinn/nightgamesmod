package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;

public class Bravado extends Skill {
    private int cost;

    public Bravado() {
        super("Determination", 5);
        cost = 0;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.fearless);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && c.getStance().mobile(user);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        cost = Math.max(20, user.getMojo().get());
        return cost;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int x = cost;
        writeOutput(c, Result.normal, user, target);
        user.calm(c, 20 + x / 2);
        user.heal(c, x);
        user.restoreWillpower(c, 2 + x / 10);
        user.emote(Emotion.confident, 30);
        user.emote(Emotion.dominant, 20);
        user.emote(Emotion.nervous, -20);
        user.emote(Emotion.desperate, -30);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Bravado();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.recovery;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You grit your teeth and put all your willpower into the fight.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character attacker) {
        return user.getName() + " gives "+attacker.nameDirectObject()+" a determined glare as " + user.pronoun() + " seems to gain a second wind.";
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Consume mojo to restore stamina and reduce arousal";
    }

}
