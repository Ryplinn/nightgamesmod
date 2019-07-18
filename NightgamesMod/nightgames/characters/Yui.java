package nightgames.characters;

import nightgames.actions.Action;
import nightgames.actions.Movement;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.FacePart;
import nightgames.characters.custom.CharacterLine;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.daytime.Daytime;
import nightgames.global.Random;
import nightgames.items.Item;

import java.util.Collection;
import java.util.Optional;

public class Yui extends BasePersonality {
    /**
     *
     */
    private static final long serialVersionUID = 8601852023164119671L;

    public Yui() {
        // Yui is a start character so that you can gain affection with her straight off the bat.
        // She is disabled when the game starts
        super(true);
    }

    @Override
    public void applyStrategy(NPC selfNPC) {
        selfNPC.plan = Plan.hunting;
        selfNPC.mood = Emotion.confident;
    }

    @Override
    public void applyBasicStats(NPC selfNPC) {
        preferredCockMod = CockMod.error;
        selfNPC.outfitPlan.addByID("sarashi");
        selfNPC.outfitPlan.addByID("shinobigarb");
        selfNPC.outfitPlan.addByID("loincloth");
        selfNPC.outfitPlan.addByID("tabi");

        selfNPC.change();
        selfNPC.modAttributeDontSaveData(Attribute.power, 1);
        selfNPC.modAttributeDontSaveData(Attribute.seduction, 1);
        selfNPC.modAttributeDontSaveData(Attribute.cunning, 1);
        selfNPC.modAttributeDontSaveData(Attribute.perception, 1);
        selfNPC.modAttributeDontSaveData(Attribute.ninjutsu, 1);
        selfNPC.modAttributeDontSaveData(Attribute.speed, 2);
        selfNPC.getStamina().setMax(100);
        selfNPC.getArousal().setMax(90);
        selfNPC.rank = 1;
        selfNPC.adjustTraits();

        selfNPC.getMojo().setMax(130);

        selfNPC.setTrophy(Item.YuiTrophy);
        selfNPC.body.add(BreastsPart.c);
        selfNPC.initialGender = CharacterSex.female;
    }

    @Override
    public void setGrowth(NPC selfNPC) {
        selfNPC.getGrowth().stamina = 3;
        selfNPC.getGrowth().arousal = 7;
        selfNPC.getGrowth().willpower = 1.4f;
        selfNPC.getGrowth().bonusStamina = 2;
        selfNPC.getGrowth().bonusArousal = 2;
        preferredAttributes.add(c -> c.get(Attribute.ninjutsu) < 60 && c.getLevel() >= 10 ? Optional.of(Attribute.ninjutsu)  : Optional.empty());
        preferredAttributes.add(c -> c.get(Attribute.cunning) < 50 ? Optional.of(Attribute.cunning) : Optional.empty());

        selfNPC.getGrowth().addTrait(0, Trait.obedient);
        selfNPC.getGrowth().addTrait(0, Trait.cute);
        selfNPC.getGrowth().addTrait(0, Trait.lickable);
        selfNPC.getGrowth().addTrait(2, Trait.Sneaky);
        selfNPC.getGrowth().addTrait(5, Trait.dexterous);
        selfNPC.getGrowth().addTrait(8, Trait.tongueTraining1);
        selfNPC.getGrowth().addTrait(11, Trait.sexTraining1);
        selfNPC.getGrowth().addTrait(14, Trait.limbTraining1);
        selfNPC.getGrowth().addTrait(17, Trait.analTraining1);
        selfNPC.getGrowth().addTrait(20, Trait.lacedjuices);
        selfNPC.getGrowth().addTrait(23, Trait.responsive);
        selfNPC.getGrowth().addTrait(26, Trait.graceful);
        selfNPC.getGrowth().addTrait(29, Trait.tongueTraining2);
        selfNPC.getGrowth().addTrait(32, Trait.sexTraining2);
        selfNPC.getGrowth().addTrait(35, Trait.limbTraining2);
        selfNPC.getGrowth().addTrait(38, Trait.analTraining2);
        selfNPC.getGrowth().addTrait(41, Trait.calm);
        selfNPC.getGrowth().addTrait(41, Trait.SexualGroove);
        selfNPC.getGrowth().addTrait(41, Trait.alwaysready);
        selfNPC.getGrowth().addTrait(44, Trait.tongueTraining3);
        selfNPC.getGrowth().addTrait(47, Trait.sexTraining3);
        selfNPC.getGrowth().addTrait(50, Trait.limbTraining3);
        selfNPC.getGrowth().addTrait(53, Trait.analTraining3);
        selfNPC.getGrowth().addTrait(56, Trait.tight);
        selfNPC.getGrowth().addTrait(60, Trait.dickhandler);
        // mostly feminine face, cute but not quite at Angel's level
        selfNPC.body.add(new FacePart(.1, 2.9));
    }

    @Override
    public Action move(Collection<Action> available, Collection<Movement> radar, NPC selfNPC) {
        for (Action act : available) {
            if (act.consider() == Movement.mana) {
                return act;
            }
        }
        return Decider.parseMoves(available, radar, selfNPC);
    }

    @Override
    public void rest(int time, NPC selfNPC) {
        super.rest(time, selfNPC);
        if (!(selfNPC.has(Item.Tickler) || selfNPC.has(Item.Tickler2)) && selfNPC.money >= 300) {
            selfNPC.gain(Item.Tickler);
            selfNPC.money -= 300;
        }
        if (!(selfNPC.has(Item.Onahole) || selfNPC.has(Item.Onahole2)) && selfNPC.money >= 300) {
            selfNPC.gain(Item.Onahole);
            selfNPC.money -= 300;
        }
        if (!selfNPC.has(Item.Onahole2) && selfNPC.has(Item.Onahole) && selfNPC.money >= 300) {
            selfNPC.remove(Item.Onahole);
            selfNPC.gain(Item.Onahole2);
            selfNPC.money -= 300;
        }
        if (selfNPC.rank >= 1) {
            if (selfNPC.money > 0) {
                Daytime.getDay().visit("Body Shop", selfNPC, Random.random(selfNPC.money));
            }
        }

        if (selfNPC.money > 0) {
            Daytime.getDay().visit("XXX Store", selfNPC, Random.random(selfNPC.money));
        }
        if (selfNPC.money > 0) {
            Daytime.getDay().visit("Bookstore", selfNPC, Random.random(selfNPC.money));
        }
        if (selfNPC.money > 0) {
            Daytime.getDay().visit("Hardware Store", selfNPC, Random.random(selfNPC.money));
        }
        if (selfNPC.money > 0) {
            Daytime.getDay().visit("Black Market", selfNPC, Random.random(selfNPC.money));
        }
        int r;

        for (int i = 0; i < time; i++) {
            r = Random.random(8);
            if (r == 1) {
                Daytime.getDay().visit("Exercise", selfNPC, 0);
            } else if (r == 0) {
                Daytime.getDay().visit("Browse Porn Sites", selfNPC, 0);
            }
        }
        Decider.visit(selfNPC);
    }

    @Override
    public String victory(Combat c, Result flag, NPC selfNPC) {
        if (c.getStance().anallyPenetrated(c, c.getOpponent(selfNPC))) {
            selfNPC.arousal.empty();
            return "Yui fucks you from behind.";
        } else if (c.getStance().vaginallyPenetrated(c, selfNPC)) {
            return "Yui's expert control of her love canal forces you over the edge. You desperately buckle and moan while trying to at least even the playing field, but a quick squeeze from her well trained "
                            + "vaginal muscles destroys any self control you may have had. Yui looks proudly at you and inquires, <i>\"Master! How is it? The women of the Ishida clan are well trained in the arts of "
                            + "seduction in addition to martial arts. I've been told they're very effective in things like information extraction and subversion! In these peaceful times, it's a bit hard getting hands "
                            + "on experience, so I'm not sure if I'm very good yet, but Master, it looks like you're having a good time!\"</i>"
                            + "<br/>"
                            + "You think to yourself that \"Having a good time\" may be the understatement of the century as you desperately try to hold back your impendending climax. You wonder why you bother though, as Yui's "
                            + "devilish hole slides repeatedly across your rock hard shaft as she rocks against you. You're pretty proud of yourself that you haven't cum yet with the amount of raw pleasure coming from your dick. "
                            + "Unfortunately Yui notices this too, and gives you a cute smile. <i>\"Ooooo Master, it looks like you're about to cum right? It's okay, let it aa-aalllll out inside! Here, let me help you out, "
                            + "I learnt this one from my grandmother, the previous clan matriarch. Rumor has it that she used this technique to even keep world leaders under her thumb back when she was young!\"</i>"
                            + "<br/>"
                            + "Okay, you're not sure you like the sounds of that. However theres no stopping her as Yui sits herself on top of your cock. You aren't sure what she's about to do at first, but then you feel a tight "
                            + "ring of muscle tighten near the base of your cock. The ring travels agonizingly slowly upwards, bringing the first spout of your boiling cum into Yui's pussy. It doesn't stop there. Yui forces "
                            + "continues milking your cock by alternating the tightness in her vagina, starting and stopping your orgasm as she pleases. At this point you can't even think any more. You try to throw her off you, "
                            + "but you cannot even muster the strength. You can only lie there as she slowly milks you in an continuous orgasm that seems unthinkable for a man."
                            + "<br/>"
                            + "Finally, you're completely spent and Yui gives you a deep kiss before standing up, <i>\"*giggle*, how was it Master? If you enjoyed loving your Yui, please ask me any time! Your servant is always ready.\"</i>";
        } else {
            return "Yui looks placidly at the proof of your defeat staining her body and says sheepishly <i>\"My apologies Master, we of the Ishida clan have been trained since we hit puberty on sexual techniques. "
                            + "It's not really a fair fight.\"</i> You flush red at her unintended insult. You halfheartedly try making a grab for her boob again, but Yui seems to disappear. While you look around confused, "
                            + "you hear her familiar voice behind you as she reaches into your pants and stokes your cock again, <i>\"It's okay, as much as you want, I will keep Master company. You only need to ask!\"</i>"
                            + "<br/>"
                            + "You groan as she manages to tease yet another geyser of white cum from your cock. Maybe she's too much for you right now.";
        }
    }

    @Override
    public String defeat(Combat c, Result flag, NPC selfNPC) {
        return "Yui was defeated";
    }

    @Override
    public String draw(Combat c, Result flag, NPC selfNPC) {
        return "";
    }

    @Override public void constructLines(NPC selfNPC) {
        selfNPC.addLine(CharacterLine.BB_LINER, (c, self, other) -> {
            if (other.human()) {
                return "Yui seems apologetic. <i>\"I'm sorry Master, but you did order a fair fight.\"</i>";
            } else {
                return "Yui seems apologetic. <i>\"I'm sorry, but it's master's orders.\"</i>";
            }
        });

        selfNPC.addLine(CharacterLine.NAKED_LINER, (c, self, other) -> "Yui doesn't seem too fazed. <i>\"If Master wanted to see my body, you need just to ask.\"</i>");

        selfNPC.addLine(CharacterLine.STUNNED_LINER, (c, self, other) -> "Yui groans as she falls, <i>\"Master, you are pretty good at this!\"</i>.");

        selfNPC.addLine(CharacterLine.TAUNT_LINER, (c, self, other) -> {
            if (other.human()) {
                return "Yui blows you a kiss. <i>\"Master, your servant will comfort you soon!\"</i>";
            } else {
                return "Yui taunts " + other + ", <i>\"Soon I'll have you on your knees serving master!\"</i>";
            }
        });

        selfNPC.addLine(CharacterLine.TEMPT_LINER, (c, self, other) -> {
            if (other.human()) {
                return "Yui cups her breasts and looks at you slyly, <i>\"Master, keep your eyes on me.\"</i>";
            } else {
                return "Yui cups her breasts and looks at " + other.nameDirectObject() + " slyly, <i>\"Mmm don't look away.\"</i>";
            }
        });

        selfNPC.addLine(CharacterLine.ORGASM_LINER, (c, self, other) -> "<i>\"Aaahhhh! Masteeerrr!\"</i>");

        selfNPC.addLine(CharacterLine.MAKE_ORGASM_LINER, (c, self, other) -> "Yui smiles, <i>\"Don't worry Master, you just need to everything to your humble servant.\"</i>");

        selfNPC.addLine(CharacterLine.CHALLENGE, (c, self, other) -> "{self:SUBJECT} bows respectifully towards {other:name-do} before sliding into an easy stance");

        selfNPC.addLine(CharacterLine.DESCRIBE_LINER, (c, self, other) -> selfNPC.subject()
                        + " is a cute girl with her short blonde hair in a what's almost a pixie cut. However, her long bangs hangs over her blue eyes, and makes it hard for you to tell what's in her mind."
                        + "She looks a bit strange dressed in what's obviously traditional eastern clothing while being very clearly white. Looking your way, she gives you a polite bow before taking her stance.");
    }

    @Override
    public boolean fightFlight(Character opponent, NPC selfNPC) {
        return true;
    }

    @Override
    public boolean attack(Character opponent, NPC selfNPC) {
        return true;
    }

    public double dickPreference() {
        return 0;
    }

    @Override
    public String victory3p(Combat c, Character target, Character assist, NPC selfNPC) {
        if (target.human()) {
            return "Yui looks apologetic as she bends down and stokes your cock. <i>\"Sorry Master, the rules says a victory is a victory, and I cannot afford to lose with your orders.\"</i>";
        }
        if (target.hasDick()) {
            return String.format(
                            "Yui kneels between %s's legs and takes a hold of %s cock. "
                                            + "<i>Master, thank you for assisting your servant. Don't worry, this will just take a second...</i> And indeed, %s blows %s load literally within a second of Yui touching %s. Wow.",
                            target.getName(), target.possessiveAdjective(), target.getName(), target.pronoun(), target.possessiveAdjective());
        }
        return String.format(
                        "Yui kneels between %s's legs and hooks two fingers inside %s pussy. "
                                        + "<i>Master, thank you for assisting your servant. Don't worry, this will just take a second...</i> And indeed, %s back arches and lets out a wail within a second of Yui touching %s. Wow.",
                                        target.getName(), target.possessiveAdjective(), target.getName(), target.possessiveAdjective());
}

    @Override
    public String intervene3p(Combat c, Character target, Character assist, NPC selfNPC) {
        return target.human()?"Your fight with " + assist.getName() + " has barely started when you hear a familiar voice call out to you. <i>\"Master! I was hoping you would be here.\"</i> " 
                        + "Before you can react, Yui grabs you and eagerly kisses you on the lips. Your surprise quickly gives way to extreme lightheadedness and drowsiness. " 
                        + "Your legs give out and you collapse into her arms. Did Yui drug you? <i>\"Please forgive this betrayal, Master. You work so hard fighting and training " 
                        + "every night. For the sake of your health, I thought it was neccessary to make you take a break.\"</i> "
                        + "She sounds genuinely apologetic, but also a little excited. <br/><i>\"Don\'t worry. We\'ll take good care of you until you can move again.\"</i> "
                        + "She carefully lowers your limp upper body onto her lap as " + assist.getName() + " fondles your dick to full hardness. "
                        + "<i>\"I\'m sure we can relieve some of your built up stress too.\"</i><br/>"
                        :
                            "This fight could certainly have gone better than this. You\'re completely naked and have your hands bound behind your back. " 
                        + target.getName() + " is just taking " + "her time to finish you off. A familiar voice calls out to her. <i>\"I see you\'ve caught my master. "
                                        + "I\'ve always wanted to get him in this position.\"</i> You " + "both surprised to see Yui standing nearby. "
                                        + "She hadn\'t made a sound when she approached. <i>\"Do you mind if I play with him for a moment? I promise I " 
                                        + "won\'t make him cum.\"</i> ";
    }

    @Override
    public boolean fit(NPC selfNPC) {
        return !selfNPC.mostlyNude() && selfNPC.getStamina().percent() >= 50
                        && selfNPC.getArousal().percent() <= 50;
    }

    @Override
    public boolean checkMood(Combat c, Emotion mood, int value, NPC selfNPC) {
        switch (mood) {
            case nervous:
                return value >= 50;
            case angry:
                return value >= 150;
            default:
                return value >= 100;
        }
    }
}
