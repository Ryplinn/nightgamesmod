package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.pet.CharacterPet;
import nightgames.pet.Pet;

public class Simulacrum extends Skill {
    public Simulacrum() {
        super("Simulacrum");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.divinity) >= 12;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().prone(user)
                        && c.getPetsFor(user).size() < user.getPetLimit() && !target.isPet();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 30;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Summons a copy of your opponent to assist you.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Pet pet;
        int power = Math.max(10, user.getLevel() - 2);
        int ac = 4 + power / 3;

        String cloneName = String.format("%s clone", target.nameOrPossessivePronoun());
        if (target instanceof Player) {
            pet = new CharacterPet(cloneName, user, target, power, ac);
        } else if (target instanceof NPC) {
            pet = new CharacterPet(cloneName, user, target, power, ac);
        } else {
            c.write(user, formatMessage(Result.miss, CharacterSex.asexual, CharacterSex.asexual, user, target));
            return false;
        }
        c.addPet(user, pet.getSelf());
        CharacterSex initialSex = pet.getSelf().body.guessCharacterSex();
        pet.getSelf().body.autoTG();
        CharacterSex finalSex = pet.getSelf().body.guessCharacterSex();
        c.write(user, formatMessage(Result.normal, initialSex, finalSex, user, target));

        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Simulacrum();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.summoning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "unused";
    }
    
    private String getSubText(CharacterSex initialSex, CharacterSex finalSex) {
        switch(finalSex) {
            case asexual:
                return "As the figure stands up, you see that she looks extremely familiar. It's a face that you've seen in the mirror every day. "
                                + "The clone looks like your identical twin, complete with the missing genitalia! She gives her newly formed nipples a few experimental tweaks before turning to face you. ";
            case shemale:
            case herm:
                if (initialSex == CharacterSex.herm) {
                    return "As the figure stands up, you see that she looks extremely familiar. It's a face that you've seen in the mirror every day. "
                                    + "The clone looks like your identical twin, complete with your signature dual genitalia! "
                                    + "She gives her newly formed cock a few experimental pumps before "
                                    + "turning to facing you. ";
                } else {
                    return "As the figure stands up, you see that she looks extremely familiar. It's a face that you've seen in the mirror every day. "
                                    + "The clone looks like your identical twin at first, but when your gaze slides lower, you see her sporting a large rod that you definitely don't remember owning! "
                                    + "She gives her newly formed cock a few experimental pumps before turning to facing you. ";
                }
            case female:
                return "As the figure stands up, you see that she looks extremely familiar. It's a face that you've seen in the mirror every day. "
                                + "The similarities end there, however; you see that the rest of the clone looks like an idealized female version of yourself with bountiful breasts and a shapely rear. "
                                + "She smiles at you and licks her lips while cupping her newly formed tits. ";
            case male:
                return "As the figure stands up, you see that he looks extremely familiar. It's a face that you've seen in the mirror every day. "
                                + "The similarities end there, however; you see that the rest of the clone looks like an idealized male version of yourself, with chiseled abs and a stiff cock raring to go. "
                                + "He gives his newly formed cock a few experimental pumps before turning to facing you.";
            default:
                return "";
        }
    }

    private String formatMessage(Result modifier, CharacterSex initialSex, CharacterSex finalSex, Character user,
                    Character target) {
        if (user.human()) {
            if (modifier == Result.miss) {
                return Formatter.format("Reaching into your divine spark, you command {other:name-possessive} very soul to serve you. "
                                + "{other:PRONOUN} looks momentarily confused as nothing happened.", user, target);
            }
            return Formatter.format("Reaching into your divine spark, you command {other:name-possessive} very soul to serve you. "
                            + "{other:PRONOUN} looks confused for a second before suddenly noticing a translucent figure shifting into existence between you and {other:direct-object}. "
                            + "The projection stabilizes into a split image of {other:name-do}!", user, target);
        } else {
            if (modifier == Result.miss) {
                return Formatter.format("{self:SUBJECT} closes {self:possessive} eyes momentarily before slowly rising into the air. "
                                + "{other:SUBJECT-ACTION:are|is} not sure what {self:pronoun} is up to, but it's definitely not good for {other:direct-object}. "
                                + "Fortunately, {other:subject:were|was} close enough to leap at {self:direct-object} and interrupt whatever {self:pronoun} was trying to do.", 
                                user, target);
            }
            return Formatter.format("{self:SUBJECT} closes {self:possessive} eyes momentarily before slowly rising into the air. "
                            + "{other:SUBJECT-ACTION:are|is} not sure what {self:pronoun} is up to, but it's definitely not good for {other:direct-object}. "
                            + "{other:SUBJECT-ACTION:run|runs} towards {other:direct-object} in a mad dash to try interrupting whatever it is {self:pronoun} is doing. "
                            + "However it is too late, {self:subject} opens her now-glowing golden eyes and intones <i>\"{other:NAME}... SERVE ME.\"</i> "
                            + "The command pierces through {other:direct-object} giving {other:direct-object} a strange sense of vertigo. {other:SUBJECT-ACTION:almost collapse|almost collapses} "
                            + "but when {other:pronoun-action:raise|raises} {other:possessive} head, {other:subject-action:see|sees} a figure kneeling before {self:name-do}. "
                            + "<br/><br/>"
                            + getSubText(initialSex, finalSex)
                            + "You're now fighting your own clone!", user, target);
        }
    }
    
    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return "unused";
    }
}
