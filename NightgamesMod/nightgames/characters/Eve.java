package nightgames.characters;

import nightgames.characters.body.*;
import nightgames.characters.body.mods.SizeMod;
import nightgames.characters.custom.CharacterLine;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.daytime.Daytime;
import nightgames.global.Random;
import nightgames.items.Item;

import java.util.Optional;

public class Eve extends BasePersonality {
    /**
     *
     */
    private static final long serialVersionUID = -8169646189131720872L;

    public Eve() {
        super(false);
    }

    @Override
    public void applyStrategy(NPC selfNPC) {
        selfNPC.plan = Plan.hunting;
        selfNPC.mood = Emotion.confident;
    }

    @Override
    public void applyBasicStats(NPC selfNPC) {
        selfNPC.outfitPlan.addByID("tanktop");
        selfNPC.outfitPlan.addByID("crotchlesspanties");
        selfNPC.outfitPlan.addByID("jeans");
        selfNPC.outfitPlan.addByID("stilettopumps");
        selfNPC.outfitPlan.addByID("garters");

        selfNPC.change();
        selfNPC.modAttributeDontSaveData(Attribute.power, 1);
        selfNPC.modAttributeDontSaveData(Attribute.fetishism, 1);
        selfNPC.modAttributeDontSaveData(Attribute.cunning, 1);
        selfNPC.modAttributeDontSaveData(Attribute.speed, 1);
        selfNPC.modAttributeDontSaveData(Attribute.seduction, 2);
        selfNPC.adjustTraits();
        selfNPC.setTrophy(Item.EveTrophy);
        selfNPC.body.add(BreastsPart.d);
        selfNPC.body.add(new CockPart().applyMod(new SizeMod(SizeMod.COCK_SIZE_BIG)));
        selfNPC.body.add(PussyPart.generic);
        selfNPC.getMojo().setMax(120);

        selfNPC.getStamina().setMax(90);
        selfNPC.getArousal().setMax(80);
        // somewhat androgynous face
        selfNPC.body.add(new FacePart(.1, .9));
        selfNPC.initialGender = CharacterSex.shemale;
        preferredCockMod = CockMod.error;
    }

    @Override
    public void setGrowth(NPC selfNPC) {
        selfNPC.getGrowth().stamina = 2;
        selfNPC.getGrowth().arousal = 6;
        selfNPC.getGrowth().bonusStamina = 1;
        selfNPC.getGrowth().bonusArousal = 3;
        preferredAttributes.add(c -> c.getAttribute(Attribute.fetishism) < 80 ? Optional.of(Attribute.fetishism) : Optional.empty());
        preferredAttributes.add(c -> Optional.of(Attribute.seduction));
        selfNPC.getGrowth().addTrait(0, Trait.exhibitionist);
        selfNPC.getGrowth().addTrait(0, Trait.proheels);
        selfNPC.getGrowth().addTrait(0, Trait.insatiable);
        selfNPC.getGrowth().addTrait(0, Trait.assmaster);
        selfNPC.getGrowth().addTrait(0, Trait.analFanatic);

        selfNPC.getGrowth().addTrait(2, Trait.alwaysready);
        selfNPC.getGrowth().addTrait(5, Trait.limbTraining1);
        selfNPC.getGrowth().addTrait(8, Trait.expertGoogler);
        selfNPC.getGrowth().addTrait(11, Trait.sexTraining1);
        selfNPC.getGrowth().addTrait(14, Trait.testosterone);
        selfNPC.getGrowth().addTrait(17, Trait.experienced);
        selfNPC.getGrowth().addTrait(20, Trait.asshandler);
        selfNPC.getGrowth().addTrait(23, Trait.sexTraining2);
        selfNPC.getGrowth().addTrait(26, Trait.limbTraining2);
        selfNPC.getGrowth().addTrait(29, Trait.dickhandler);
        selfNPC.getGrowth().addTrait(32, Trait.polecontrol);
        selfNPC.getGrowth().addTrait(35, Trait.desensitized);
        selfNPC.getGrowth().addTrait(38, Trait.powerfulhips);
        selfNPC.getGrowth().addTrait(41, Trait.responsive);
        selfNPC.getGrowth().addTrait(44, Trait.strongwilled);
        selfNPC.getGrowth().addTrait(47, Trait.insertion);
        selfNPC.getGrowth().addTrait(50, Trait.sexTraining3);
        selfNPC.getGrowth().addTrait(53, Trait.limbTraining3);
        selfNPC.getGrowth().addTrait(56, Trait.desensitized2);
    }

    @Override
    public void rest(int time, NPC selfNPC) {
        super.rest(time, selfNPC);
        if (!(selfNPC.has(Item.Dildo) || selfNPC.has(Item.Dildo2)) && selfNPC.money >= 250) {
            selfNPC.gain(Item.Dildo);
            selfNPC.money -= 250;
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
        if (selfNPC.money > 0) {
            Daytime.getDay().visit("XXX Store", selfNPC, Random.random(selfNPC.money));
        }
        if (selfNPC.money > 0) {
            Daytime.getDay().visit("Black Market", selfNPC, Random.random(selfNPC.money));
        }
        if (selfNPC.money > 0) {
            Daytime.getDay().visit("Bookstore", selfNPC, Random.random(selfNPC.money));
        }
        if (selfNPC.money > 0) {
            Daytime.getDay().visit("Hardware Store", selfNPC, Random.random(selfNPC.money));
        }
        Decider.visit(selfNPC);
        int r;

        for (int i = 0; i < time; i++) {
            r = Random.random(8);
            if (r == 1) {
                Daytime.getDay().visit("Exercise", selfNPC, 0);
            } else if (r == 0) {
                Daytime.getDay().visit("Browse Porn Sites", selfNPC, 0);
            }
        }
    }

    @Override public void constructLines(NPC selfNPC) {
        selfNPC.addLine(CharacterLine.BB_LINER, (c, self, other) ->
                        "Eve grins at you and pats her own groin. <i>\"Better you than me, " + other.boyOrGirl() + ".\"</i>");

        selfNPC.addLine(CharacterLine.CHALLENGE, (c, self, other) -> "{self:SUBJECT} gives {other:name-do} a dominant grin and cracks {self:possessive} knuckles. <i>\"Come on {other:name}, let's play.\"</i>");

        selfNPC.addLine(CharacterLine.NAKED_LINER, (c, self, other) -> "Eve seems more comfortable with her cock and balls hanging out than she was with her clothes on. <i>\"Like what you see? We're just getting started.\"</i>");

        selfNPC.addLine(CharacterLine.STUNNED_LINER, (c, self, other) ->
                        "Eve lets out a soft growl as she lays flat on the floor. <i>\"Enjoy it while you can, " + other.boyOrGirl() + ". As soon as I catch my breath, your ass is mine.\"</i>");

        selfNPC.addLine(CharacterLine.TAUNT_LINER, (c, self, other) -> "Eve grins sadistically. <i>\"If you're intimidated by my cock, don't worry. Size isn't everything.\"</i>");

        selfNPC.addLine(CharacterLine.TEMPT_LINER, (c, self, other) -> "Eve grins sadistically. <i>\"I'm an expert at making people like you squeal.\"</i>");

        selfNPC.addLine(CharacterLine.NIGHT_LINER, (c, self, other) -> "");

        selfNPC.addLine(CharacterLine.ORGASM_LINER, (c, self, other) -> {
            if (c.getStance().anallyPenetrated(c, other)) {
                return "<i>\"Oh fuck! You are one tight little cum bucket! Let's go again!\"</i>"
                                + " Eve immediately resumes her thrusting.";
            }
            return "<i>\"Ahhh shit! Wouldn't it have been sooo much better to have " + "taken that load up your ass?\"</i>";
        });

        selfNPC.addLine(CharacterLine.MAKE_ORGASM_LINER, (c, self, other) -> {
            if (c.getStance().anallyPenetrated(c, self)) {
                return "Eve laughs maniacally as you cum. <i>\"I knew you'd like it"
                                + ", you little ass slut! But you're not done yet!\"</i>";
            }
            return "<i>\"That's it! Now, how about you return the favor?\"</i>";
        });

        selfNPC.addLine(CharacterLine.DESCRIBE_LINER, (c, self, other) ->
                        "If there's one word to describe Eve's appearance, it would have to be 'wild'. Her face is quite pretty, though her eyes are an unnerving silver color. "
                                        + "She has bright purple hair gathered in a messy ponytail, a variety of tattoos decorating her extremely shapely body, and of couse it's "
                                        + "impossible to miss the larger than average cock and balls hanging between between her legs.");
    }

    @Override
    public String victory(Combat c, Result flag, NPC selfNPC) {
        selfNPC.arousal.empty();
        if (c.getStance().anallyPenetratedBy(c, c.getOpponent(selfNPC), selfNPC)) {
            return "As Eve pounds you mercilessly in the ass, your body is overwhelmed"
                            + " by the strange sensations radiating from your insides. <i>\"How"
                            + " does your prostate feel? I could probably milk you like this, but"
                            + " this silly cock of your looks so desperate for attention.\"</i> "
                            + "She grabs your exposed dick and starts to vigorously stroke you off."
                            + " The simulation from both ends makes you feel like you're melting. "
                            + "You can't control your muscles, much less struggle. All you can do "
                            + "is give your body to Eve as she uses you like a sexdoll.<br/><br/>Eve "
                            + "presses her heavy breasts against your back and you hear her"
                            + " ragged breathing in your ear. She's pegging you for her own "
                            + "enjoyment, but you're obviously the one who will break first. A "
                            + "shameful moan escapes you as you're hit with a wave of pleasure. "
                            + "Your cock spurts eagerly as the last of your dignity escapes you. "
                            + "<i>\"Whoops. I was aiming to cum together, but I guess I didn't "
                            + "realize you were such a quick shot.\"</i> She quickens her pace, "
                            + "thrusting with abandon. Her hand, which was just giving you such "
                            + "amazing pleasure, moves to a more sadistic goal. She grabs your "
                            + "sensitive testicles and squeezes them hard.<br/><br/>You cry out and the "
                            + "pain causes your ass to clench up involuntarily. Eve buries her "
                            + "cock deep in your ass and you feel her hot seed fill you. You slump "
                            + "to the floor as she releases you, defeated and spent. <i>\"You make"
                            + " a pretty good cocksleeve, " + c.getOpponent(selfNPC).boyOrGirl() + ". Let's do this again sometime.\"</i>";
        }

        return "You try valiantly to outlast Eve, but it's no good. She's got you right "
                        + "where she wants you. She pins you down and skillfully strokes your "
                        + "cock. You feel pleasure rapidly building in your abdomen as you pass"
                        + " the point of no return. You can't hold back anymore. You're about to"
                        + " ejaculate in her hands. She grins evily and fondles your balls softly"
                        + " while continuing her handjob. <br/><br/>You suddenly feel a powerful shock "
                        + "run through you. It's a pleasurable sensation, but lacks the sense of "
                        + "release you get from an orgasm and leaves you unsatisfied. You try to "
                        + "sit up to figure out what happened, but your body doesn't obey. You can't"
                        + " move! You're still breathing and can look around, but your arms and "
                        + "legs are completely numb. Maybe numb isn't the right word. You can't"
                        + " move a finger, but you can accutely feel every touch. Eve kneads your "
                        + "sack with both hands, and you feel and unnatural warth flow into you."
                        + " <i>\"Feeling pretty helpless and frustrated now? I overloaded your "
                        + "nervous system by denying your orgasm. It's not a very useful technique"
                        + " because it only works on someone who is about to cum. But it does let"
                        + " me play with you much longer, so it's also the best technique.\"</i> "
                        + "<br/><br/>You're still fully erect and leaking pre-cum, but she's abandoned "
                        + "your dick and you can't lift a finger to help yourself. <i>\"Do you want"
                        + " to cum? Not quite yet. I'm still preparing you.\"</i> You feel a "
                        + "churning in your testicles and the urge to ejaculate suddenly increases."
                        + " It feels like she's turned up your semen production, leaving you "
                        + "even more pent-up. It's making your balls much more sensitive, so her"
                        + " fondling is giving you a lot of pleasure, but no release. If she's"
                        + " trying to drive you insane, she's doing a pretty good job of it.<br/><br/>Eve"
                        + " suddenly lets go of your junk and straddles your waist. She rubs "
                        + "her slit along your cock while idly stroking herself. <i>\"Are you "
                        + "feeling desperate yet? You're almost ready for the big moment. You "
                        + "just need to figure out the million dollar question.\"</i> She leans "
                        + "close to your face with a wicked grin. <i>\"What fetish did I imbue"
                        + " you with?\"</i> Of course she gave you an artificial fetish. That's"
                        + " why you're feeling so unnaturally horny. It's not just the physical"
                        + " pleasure. You're getting incredibly turned on from being at Eve's "
                        + "mercy, and the fact that she's torturing you and keeping you on edge"
                        + ".<br/><br/>So that's it. She's imbued you with masochism. <i>\"Bingo!\"</i> "
                        + "Eve slams her knee into your sensitive balls. You're hit with "
                        + "simultaneous waves of pleasure and agony. A fountain of cum shoots "
                        + "from your cock, even as you're busy curling up into the fetal position."
                        + " Through the haze of pain, you're vaguely aware that Eve is jerking "
                        + "off while watching your reaction, and she soon adds her ejaculation "
                        + "to the pool of semen you shot out. <i>\"You sure put on a hell of a "
                        + "show, " + c.getOpponent(selfNPC).boyOrGirl() + ". Don't worry, I didn't hit you nearly as hard as it must have"
                        + " felt. You'll be ok in a couple minutes.\"</i>";
    }

    @Override
    public String defeat(Combat c, Result flag, NPC selfNPC) {
        return "As you pleasure Eve, she gradually stops fighting back, apparently more interested in enjoying her orgasm than in the outcome of the fight. You grab her throbbing "
                        + "cock and pump it rapidly. She lets out a scream of pleasure as she cums and fires powerful jets of semen into the air. She relaxes on the floor with a blissful "
                        + "expression, idly playing with her own fluids and occasionally licking them off her fingers. <br/><br/>You wave your unsatisfied erection in front of her face to remind her "
                        + "that she still owes you an orgasm. Eve smiles up at you lewdly. <i>\"Don't worry, " + c.getOpponent(
                        selfNPC).boyOrGirl() + ". I'll get you off properly, but I don't want you just sitting back and enjoying "
                        + "it. Sucking you dry is bound to get me all hot and bothered again, and I don't plan to walk away with a boner. Make sure you give as good as you get.\"</i> She's pretty "
                        + "demanding for a loser. Fortunately for her, you're in a generous mood. Eve seems eager to hold up her side of the deal. She takes most of your cock into her mouth "
                        + "without much difficulty and begins to explore your length with her tongue. You watch her dick harden again, sooner than you would have expected.";
    }

    @Override
    public String draw(Combat c, Result flag, NPC selfNPC) {
        return "You feel like you've got Eve pretty aroused, but you can't keep up"
                        + " with her amazing handjob. She's just too damn good at handling "
                        + "a cock. Just as you've given up, she grabs your wrist with her"
                        + " free hand. You don't even try to resist. At this point, you're "
                        + "going to lose whether she's restraining you or not. To your surprise,"
                        + " she pulls your hand between her legs and shoves your fingers into "
                        + "her soaked pussy. Is she that confident? She's put your fingers "
                        + "exactly where you want them. Instead of questioning it, you just "
                        + "start pumping and fingering her.<br/><br/>Once you start pleasuring her, "
                        + "she releases your right hand. She strokes your dick with one hand "
                        + "and her own with the other. She moans with abandon from the pleasure "
                        + "being inflicted on both her male and female parts. If she's going to "
                        + "get herself off, your best plan is going to be to just outlast her. "
                        + "Unfortunately, her masturbation hasn't detracted at all from her "
                        + "handjob skill. You quickly feel your ejaculation start to build, "
                        + "despite your best efforts.<br/><br/><i>\"Fuck! I'm cumming!\"</i> Eve cries "
                        + "out in pleasure just as your resistance gives way. Her girl parts "
                        + "squeeze your fingers as both your dicks spurt in unision. A wave of "
                        + "pleasure washes over you and you find yourself transfixed by her "
                        + "gorgeous face twisted in pleasure.<br/><br/>You both lie on the floor, "
                        + "drained of energy and fluids. Eve looks ready to take a nap, but you"
                        + " feel compelled to ask her why she gave up her win at the last minute. "
                        + "She looks at you, tiredly, apparently not understanding your question. "
                        + "She totally had you beat, but she forced a draw by making herself "
                        + "cum at the same time. Did she take pity on you? That doesn't sound"
                        + " like Eve.<br/><br/>She yawns and tries to find a more comfortable position. "
                        + "<i>\"A draw pays just as good as a win and is way more satisfying."
                        + " Why deprive myself of a good orgasm?\"</i> A draw isn't really the"
                        + " same as a win. Eve probably doesn't care about losing her clothes, "
                        + "but giving you a free point isn't the way to win a match. She lets out "
                        + "surprisingly girly giggle in response. <i>\"You're adorably naive if "
                        + "you think winning is the point of all this. I like seeing dumb " + c.getOpponent(
                        selfNPC).boyOrGirl() + "s "
                        + "like you try your hardest.\"</i> She sits up and smiles at you in pure "
                        + "amusement. <i>\"Go on then Tiger. Let's see you win this match and "
                        + "prove you're the best sexfighter on campus. Keep imagining it makes a "
                        + "difference in the end.\"</i>";
    }

    @Override
    public boolean fightFlight(Character opponent, NPC selfNPC) {
        return !selfNPC.mostlyNude() || opponent.mostlyNude();
    }

    @Override
    public boolean attack(Character opponent, NPC selfNPC) {
        return true;
    }

    @Override
    public String victory3p(Combat c, Character target, Character assist, NPC selfNPC) {
        if (target.human()) {
            return "Eve lifts up your legs, putting you in an extremely undignified position."
                            + " She pulls open your ass cheeks and starts to probe at your puckered"
                            + " hole. Oh Fuck. You'd pretty much given up winning this one, but there"
                            + " are many better ways to lose than getting pegged in the ass. You "
                            + "struggle as best you can, but you're well aware it's completely futile."
                            + " Pretty quickly, she gives you a slap on the ass and leaves your anus"
                            + " alone. <i>\"Bad angle for insertion, lucky for you " + target.boyOrGirl() + ". You're going"
                            + " to have to find another way to entertain me.\"</i> It seems a little"
                            + " unfair that she's putting the responsibility of entertainment on you"
                            + ", when you're almost completely incapacitated. She doesn't actually "
                            + "seem to be waiting for you though. She straddles your hips and grinds"
                            + " her cock against yours. <i>\"You were just freaking out that I was"
                            + " going to plug you in the ass, but you got hard again already. Such "
                            + "an eager little penis.\"</i> She continues to hump your rod as you "
                            + "squirm helplessly. If your cocks are dueling, hers definitely has "
                            + "the advantage. She also uses her hands to tease your balls and shaft,"
                            + " eroding your willpower. When you cum, she immediately uses your "
                            + "jizz as lubricant to frot against you more aggressively. Your "
                            + "overstimulated dick becomes uncomfortably sensitive, but she doesn't "
                            + "let up until she ejaculates onto you.";
        }

        if (assist.eligible(selfNPC)) {
            assist.defeated(selfNPC);
            assist.nudify();
        }
        return "Eve kneels between " + target.getName()
                        + "'s legs and plays with the helpless girl's slit. <i>\"Nice and wet. Good.\"</i> She flicks a finger across "
                        + target.getName() + "'s "
                        + "clit and you feel her body jerk at the sensation. <i>\"Sensitive too, and all mine? I don't mind if I do.\"</i> She lines up her impressive "
                        + "member with the other girl's dripping entrance and penetrates her with one firm thrust. Both girls moan in pleasure and you feel your boner stand at attention in "
                        + "response to the enticing scene. Eve grins at you confidently. <i>\"Jealous? I'm sure you wish your cock was buried deep in this tight, wet cunt, but I got "
                        + "here first.\"</i> She thrusts her hips several times for emphasis. <i>\"You could always go for the back door.\"</i> "
                        + target.getName() + " frantically shakes her "
                        + "head in protest, though she's moaning too much to speak. <i>\"Aww... She doesn't want to be double penetrated. You'll just have to settle for this.\"</i> <br/>"
                        + "Without slowing down her thrusts, she pushes the other girl into your lap. Suddenly "
                        + target.getName() + "'s soft butt is rubbing firmly and pleasurably against your erection. "
                        + "Shit. She's going to make you cum too, even when you're helping her. <i>\"Don't let go of her, or you won't get credit for her orgasm.\"</i> Unable to struggle "
                        + "free, the sensation of " + target.getName()
                        + "'s ass grinding against your dick makes you cum right after she does.";

    }

    @Override
    public String intervene3p(Combat c, Character target, Character assist, NPC selfNPC) {
        if (target.human()) {
            return "Your fight with " + assist.getName()
                            + " is interrupted when you're suddenly grabbed from behind. The big breasts pressed against your back don't narrow down the "
                            + "suspects too much, but the hard bulge hitting your ass does. The last thing you want to do is expose your ass to Eve, but fortunately she doesn't have a "
                            + "firm grip on you yet. You jerk forward to try to escape her grasp. <i>\"Stop squirming!\"</i> She slams her knee up between your legs, hitting you squarely "
                            + "in the balls. The intense pain takes all the fight out of you and you go limp in her arms. <i>\"Are you done struggling or do you need another kick?\"</i> "
                            + "You meekly shake your head and let her secure her grip.<br/>";
        }

        return "Your fight with " + target.getName()
                        + " quickly renders you both naked and aroused. She manages to trip you, dropping you solidly to the floor. She wastes no time "
                        + "and bends down to suck on your defenseless dick. You groan in pleasure as her tongue plays with your glans. Suddenly, she yelps in surprise and loses her balance. "
                        + "You spot Eve standing behind her, fondling her exposed girl parts. " + target.getName()
                        + " tries to get back to her feet, but Eve easily forces her onto her back. "
                        + "<i>\"When I saw your sexy ass waving in front of me, I thought about giving you a good fucking, but then I noticed how enthusiastically you were blowing that " + assist.boyOrGirl() + ".\"</i> "
                        + "She straddles the other girl's face and presses her girl-cock against her lips. <i>\"Let's see you put those skills to better use.\"</i><br/>"
                        + "Apparently you've been forgotten. Oh well. " + target.getName()
                        + "'s pussy looks pretty lonely. Looks like this will be your win.<br/>";

    }

    @Override
    public boolean fit(NPC selfNPC) {
        return !selfNPC.mostlyNude() && selfNPC.getStamina().percent() >= 50;
    }

    @Override
    public boolean checkMood(Combat c, Emotion mood, int value, NPC selfNPC) {
        switch (mood) {
            case horny:
            case dominant:
                return value >= 50;
            case nervous:
            case desperate:
                return value >= 150;
            default:
                return value >= 100;
        }
    }
}
