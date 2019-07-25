package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.status.IgnoreOrgasm;
import nightgames.status.Unreadable;

public class Bluff extends Skill {

    public Bluff() {
        super("Bluff", 5);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.pokerface) && user.getAttribute(Attribute.cunning) >= 9;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 20;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int m = 20 + Random.random(25);
        writeOutput(c, Result.normal, user, target);
        if (!user.getArousal().isFull()) {
            user.add(c, new IgnoreOrgasm(user.getType(), 2));
        }
        user.heal(c, m);
        user.calm(c, user.getArousal().max() / 4);
        user.add(c, new Unreadable(user.getType()));
        user.emote(Emotion.confident, 30);
        user.emote(Emotion.dominant, 20);
        user.emote(Emotion.nervous, -20);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.calming;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You force yourself to look less tired and horny than you actually are. You even start to believe it yourself.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("Despite %s best efforts, %s is still looking as calm and composed as ever."
                        + " Either %s %s getting to %s at all, or %s %s really good at hiding it.", 
                        target.nameOrPossessivePronoun(), user.subject(), target.pronoun(),
                        target.action("aren't", "isn't"), user.directObject(), user.pronoun(),
                        user.action("are", "is"));
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Regain some stamina and lower arousal. Hides current status from opponent.";
    }

}
