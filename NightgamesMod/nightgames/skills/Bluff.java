package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.status.IgnoreOrgasm;
import nightgames.status.Unreadable;

public class Bluff extends Skill {

    public Bluff(Character self) {
        super("Bluff", self, 5);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.pokerface) && user.get(Attribute.Cunning) >= 9;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && c.getStance().mobile(getSelf());
    }

    @Override
    public int getMojoCost(Combat c) {
        return 20;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        int m = 20 + Global.random(25);
        writeOutput(c, Result.normal, target);
        if (!getSelf().getArousal().isFull()) {
            getSelf().add(c, new IgnoreOrgasm(getSelf(), 2));
        }
        getSelf().heal(c, m);
        getSelf().calm(c, getSelf().getArousal().max() / 4);
        getSelf().add(c, new Unreadable(getSelf()));
        getSelf().emote(Emotion.confident, 30);
        getSelf().emote(Emotion.dominant, 20);
        getSelf().emote(Emotion.nervous, -20);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Bluff(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.calming;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return "You force yourself to look less tired and horny than you actually are. You even start to believe it yourself.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return String.format("Despite %s best efforts, %s is still looking as calm and composed as ever."
                        + " Either %s %s getting to %s at all, or %s %s really good at hiding it.", 
                        target.nameOrPossessivePronoun(), getSelf().subject(), target.pronoun(),
                        target.action("aren't", "isn't"), getSelf().directObject(), getSelf().pronoun(),
                        getSelf().action("are", "is"));
    }

    @Override
    public String describe(Combat c) {
        return "Regain some stamina and lower arousal. Hides current status from opponent.";
    }

}
