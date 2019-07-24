package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Parasited;
import nightgames.status.Slimed;
import nightgames.status.Stsflag;

public class Parasite extends Skill {

    public Parasite() {
        super("Parasite", 5);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.slime) >= 21;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.is(Stsflag.parasited) && c.getStance().kiss(user, target) && !target.canAct() && user.canAct();
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Implant a slime parasite in your opponent";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.human()) {
            receive(c, 0, Result.normal, user, target);
        } else {
            deal(c, 0, Result.normal, user, target);
        }

        target.add(c, new Parasited(target.getType(), user.getType()));
        if (user.has(Trait.VolatileSubstrate)) {
            target.add(c, new Slimed(target.getType(), user.getType(), Random.random(2, 4)));
        }

        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Parasite();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format("Taking advantage of {other:name-possessive} inability to respond, {self:subject} leans in close to {other:possessive} face and plunges {self:possessive} abnormally long translucent tongue into {other:possessive} ear! "
                        + "{other:PRONOUN-ACTION:moan|moans} as {self:pronoun} briefly explores the folds outside before diving inside {other:possessive} ear canal. Strangely enough {other:subject-action:don't|doesn't} feel anything unpleasant when {self:name-possessive} "
                        + "slimy appendage snakes past {other:possessive} eardrum and embeds itself into {other:possessive} inner ear. Finally, {self:subject} retracts {self:possessive} tongue from {other:direct-object} with an audible pop and gives {other:direct-object} a smug smile. "
                        + "{other:SUBJECT} briefly {other:action:wonder|wonders} what {self:pronoun} did, before realizing in horror that a cold sensation is still moving inside {other:possessive} head!", user, target);
    }

}
