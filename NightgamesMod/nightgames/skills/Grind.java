package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.stance.Stance;

public class Grind extends Thrust {
    private static final String divineName = "Sacrament";

    public Grind() {
        super("Grind");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.seduction) >= 14;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return havingSex(c, user, target) && (c.getStance().canthrust(c, user) || user.has(Trait.powerfulhips)) && c.getStance().en != Stance.anal;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 10;
    }

    @Override
    public int[] getDamage(Combat c, Character user, Character target) {
        int[] results = new int[2];

        int ms = 12;
        int mt = 6;
        if (getLabel(c, user).equals(divineName)) {
            ms = 16;
            mt = 10;
        }

        if (user.has(Trait.experienced)) {
            mt = mt * 2 / 3;
        }
        results[0] = ms;
        results[1] = mt;

        return results;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        boolean res = super.resolve(c, user, target);
        if (getLabel(c, user).equals(divineName)) {
            target.heal(c, 20);
            target.buildMojo(c, 5);
            target.loseWillpower(c, Random.random(3) + 2, false);
            user.usedAttribute(Attribute.divinity, c, .5);
        }
        return res;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.reverse) {
            if (getLabel(c, user).equals(divineName)) {
                return Formatter.format(
                                "{self:SUBJECT-ACTION:fill|fills} {self:possessive} pussy with divine power until it's positively dripping with glowing golden mists. {self:PRONOUN} {self:action:then grind|grinds} against {other:direct-object} with {self:possessive} "
                                                + getSelfOrgan(c, user, target).fullDescribe(user)
                                                + ", stimulating {other:possessive} entire manhood, completely obliterating any resistance from {other:possessive} mind.",
                                user, target);
            }
            return Formatter.format(
                            "{self:SUBJECT-ACTION:grind|grinds} against {other:direct-object} with {self:possessive} "
                                            + getSelfOrgan(c, user, target).fullDescribe(user)
                                            + ", stimulating {other:possessive} entire manhood and bringing {other:direct-object} closer to climax.",
                            user, target);
        } else {
            if (getLabel(c, user).equals(divineName)) {
                // TODO divine for fucking someone
                return Formatter.format(
                                "{self:SUBJECT} grind {self:possessive} hips against {other:direct-object} without thrusting. {other:SUBJECT} trembles and gasps as the movement stimulates {other:possessive} clit and the walls of {other:possessive} {other:body-part:pussy}.",
                                user, target);
            }
            return Formatter.format(
                            "{self:SUBJECT} grind {self:possessive} hips against {other:direct-object} without thrusting. {other:SUBJECT} trembles and gasps as the movement stimulates {other:possessive} clit and the walls of {other:possessive} {other:body-part:pussy}.",
                            user, target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character attacker) {
        return deal(c, damage, modifier, user, attacker);
    }

    @Override
    public String describe(Combat c, Character user) {
        if (getLabel(c, user).equals(divineName)) {
            return "Grind against your opponent with minimal thrusting. Extremely consistent pleasure and builds some mojo";
        } else {
            return "Grind against your opponent with minimal thrusting. Extremely consistent pleasure and builds some mojo for both player";
        }
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (user.get(Attribute.divinity) >= 10) {
            return divineName;
        } else {
            return "Grind";
        }
    }

    @Override
    public boolean makesContact() {
        return true;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
