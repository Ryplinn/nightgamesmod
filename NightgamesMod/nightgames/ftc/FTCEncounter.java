package nightgames.ftc;

import nightgames.areas.Area;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.combat.Combat;
import nightgames.combat.Encounter;
import nightgames.combat.Encs;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.items.Item;
import nightgames.stance.Mount;
import nightgames.stance.Pin;
import nightgames.status.Bound;
import nightgames.status.Flatfooted;
import nightgames.trap.Trap;

import static nightgames.combat.Encounter.Initiation.ambushRegular;

public class FTCEncounter extends Encounter {

    private static final long serialVersionUID = 5190164935968044626L;

    FTCEncounter(Area location) {
        super(location);
    }

    @Override
    public void spotCheck() {
        if (!(getP1().eligible(getP2()) && getP2().eligible(getP1())))
            super.spotCheck();
        if (getP1().state == State.inTree) {
            treeAmbush(getP1(), getP2());
        } else if (getP2().state == State.inTree) {
            treeAmbush(getP2(), getP1());
        } else if (getP1().state == State.inBushes) {
            bushAmbush(getP1(), getP2());
        } else if (getP2().state == State.inBushes) {
            bushAmbush(getP2(), getP1());
        } else if (getP1().state == State.inPass) {
            passAmbush(getP1(), getP2());
        } else if (getP2().state == State.inPass) {
            passAmbush(getP2(), getP1());
        } else {
            super.spotCheck();
        }
    }

    private void treeAmbush(Character attacker, Character victim) {
        victim.addNonCombat(new Flatfooted(victim.getType(), 3));
        if (attacker.has(Item.Handcuffs))
            victim.addNonCombat(new Bound(victim.getType(), 75, "handcuffs"));
        else
            victim.addNonCombat(new Bound(victim.getType(), 50, "zip-tie"));
        if (getP1().human() || getP2().human()) {
            fight = new Combat(attacker, victim, attacker.location(), ambushRegular);
            fight.setStance(new Pin(attacker.getType(), victim.getType()));
            String message = "";
            if (victim.human()) {
                message += "As you walk down the trail, you hear a slight rustling in the"
                                + " leaf canopy above you. You look up, but all you see is a flash of ";
                if (attacker.mostlyNude()) {
                    message += "nude flesh";
                } else {
                    message += "clothes";
                }
                message += " before you are pushed to the ground. Before you have a chance to process"
                                + " what's going on, your hands are tied behind your back and your"
                                + " attacker, who now reveals {self:reflective} to be {self:name},"
                                + " whispers in your ear \"Happy to see me, {other:name}?\"";
            } else {
                message += "Your patience finally pays off as {other:name} approaches the"
                                + " tree you are hiding in. You wait until the perfect moment,"
                                + " when {other:pronoun} is right beneath you, before you jump"
                                + " down. You land right on {other:possessive} shoulders, pushing"
                                + " {other:direct-object} firmly to the soft soil. Pulling our a ";
                if (attacker.has(Item.Handcuffs)) {
                    message += "pair of handcuffs, ";
                } else {
                    message += "zip-tie, ";
                }
                message += " you bind {other:possessive} hands together. There are worse" + " ways to start a match.";
            }
            GUI.gui.message(Formatter.format(message, attacker, victim));
        } else {
            GUI.gui.refresh();
            fight = new Combat(attacker, victim, location, ambushRegular);
            fight.setStance(new Pin(attacker.getType(), victim.getType()));
        }
    }

    private void bushAmbush(Character attacker, Character victim) {
        victim.addNonCombat(new Flatfooted(victim.getType(), 3));
        if (attacker.has(Item.Handcuffs))
            victim.addNonCombat(new Bound(victim.getType(), 75, "handcuffs"));
        else
            victim.addNonCombat(new Bound(victim.getType(), 50, "zip-tie"));
        if (getP1().human() || getP2().human()) {
            fight = new Combat(attacker, victim, attacker.location(), ambushRegular);
            fight.setStance(new Mount(attacker.getType(), victim.getType()));
            String message = "";
            if (victim.human()) {
                message += "You are having a little difficulty wading through the dense"
                                + " bushes. Your foot hits something, causing you to trip and fall flat"
                                + " on your face. A weight settles on your back and your arms are"
                                + " pulled behind your back and tied together with something. You"
                                + " are rolled over, and {self:name} comes into view as {self:pronoun}"
                                + " settles down on your belly. \"Hi, {other:name}. Surprise!\"";
            } else {
                message += "Hiding in the bushes, your vision is somewhat obscured. This is"
                                + " not a big problem, though, as the rustling leaves alert you to"
                                + " passing prey. You inch closer to where you suspect they are headed,"
                                + " and slowly {other:name} comes into view. Just as {other:pronoun}"
                                + " passes you, you stick out a leg and trip {other:direct-object}."
                                + " With a satisfying crunch of the leaves, {other:pronoun} falls."
                                + " Immediately you jump on {other:possessive} back and tie "
                                + "{other:possessive} hands together.";
            }
            GUI.gui.message(Formatter.format(message, attacker, victim));
        } else {
            GUI.gui.refresh();
            fight = new Combat(attacker, victim, location, ambushRegular);
            fight.setStance(new Pin(attacker.getType(), victim.getType()));
        }
    }

    private void passAmbush(Character attacker, Character victim) {
        int attackerScore = 30 + attacker.getAttribute(Attribute.speed) * 10 + attacker.getAttribute(Attribute.perception) * 5
                        + Random.random(30);
        int victimScore = victim.getAttribute(Attribute.speed) * 10 + victim.getAttribute(Attribute.perception) * 5 + Random.random(30);
        String message = "";
        if (attackerScore > victimScore) {
            if (attacker.human()) {
                message += "You wait in a small alcove, waiting for someone to pass you."
                                + " Eventually, you hear footsteps approaching and you get ready."
                                + " As soon as {other:name} comes into view, you jump out and push"
                                + " {other:direct-object} against the opposite wall. The impact seems to"
                                + " daze {other:direct-object}, giving you an edge in the ensuing fight.";
            } else if (victim.human()) {
                message += "Of course you know that walking through a narrow pass is a"
                                + " strategic risk, but you do so anyway. Suddenly, {self:name}"
                                + " flies out of an alcove, pushing you against the wall on the"
                                + " other side. The impact knocks the wind out of you, putting you"
                                + " at a disadvantage.";
            }
            fight = new Combat(attacker, victim, attacker.location());
            victim.addNonCombat(new Flatfooted(victim.getType(), 3));
        } else {
            if (attacker.human()) {
                message += "While you are hiding behind a rock, waiting for someone to"
                                + " walk around the corner up ahead, you hear a soft crunch behind"
                                + " you. You turn around, but not fast enough. {other:name} is"
                                + " already on you, and has grabbed your shoulders. You are unable"
                                + " to prevent {other:direct-object} from throwing you to the ground,"
                                + " and {other:pronoun} saunters over. \"Were you waiting for me,"
                                + " {self:name}? Well, here I am.\"";
            } else if (victim.human()) {
                message += "You are walking through the pass when you see {self:name}"
                                + " crouched behind a rock. Since {self:pronoun} is very focused"
                                + " in looking the other way, {self:pronoun} does not see you coming."
                                + " Not one to look a gift horse in the mouth, you sneak up behind"
                                + " {self:direct-object} and grab {self:direct-object} in a bear hug."
                                + " Then, you throw {self:direct-object} to the side, causing"
                                + " {self:direct-object} to fall to the ground.";
            }
            fight = new Combat(attacker, victim, attacker.location());
            attacker.addNonCombat(new Flatfooted(attacker.getType(), 3));
        }
        if (attacker.human() || victim.human()) {
            GUI.gui.message(Formatter.format(message, attacker, victim));
        }
    }

    @Override
    public void parse(Encs choice, Character self, Character target) {
        parse(choice, self, target, null);
    }

    @Override
    public void parse(Encs choice, Character attacker, Character target, Trap trap) {
        assert trap != null || choice != Encs.capitalizeontrap;
        if (!isFTCSpecific(choice)) {
            super.parse(choice, attacker, target, trap);
        } else {
            switch (choice) {
                case treeAmbush:
                    treeAmbush(attacker, target);
                    break;
                case bushAmbush:
                    bushAmbush(attacker, target);
                    break;
                case passAmbush:
                    passAmbush(attacker, target);
                    break;
                default:
            }
        }
    }

    private static boolean isFTCSpecific(Encs enc) {
        return enc == Encs.treeAmbush || enc == Encs.bushAmbush || enc == Encs.passAmbush;
    }
}
