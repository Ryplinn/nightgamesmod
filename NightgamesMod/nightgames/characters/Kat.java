package nightgames.characters;

import nightgames.characters.body.*;
import nightgames.characters.body.mods.FeralMod;
import nightgames.characters.custom.CharacterLine;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.CombatScene;
import nightgames.combat.CombatSceneChoice;
import nightgames.combat.Result;
import nightgames.daytime.Daytime;
import nightgames.global.Flag;
import nightgames.global.Formatter;
import nightgames.global.GameState;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.skills.strategy.FacesitStrategy;
import nightgames.status.Feral;
import nightgames.status.Pheromones;
import nightgames.status.Stsflag;

import java.util.Arrays;
import java.util.Optional;

import static nightgames.requirements.RequirementShortcuts.*;

public class Kat extends BasePersonality {
    /**
     *
     */
    private static final long serialVersionUID = -8169646189131720872L;

    public Kat() {
        super(false);
    }

    @Override
    public void applyStrategy(NPC selfNPC) {
        selfNPC.plan = Plan.retreating;
        selfNPC.mood = Emotion.confident;
        selfNPC.addPersonalStrategy(new FacesitStrategy());
    }

    @Override
    public void applyBasicStats(NPC selfNPC) {
        preferredCockMod = CockMod.primal;
        selfNPC.outfitPlan.addByID("bra");
        selfNPC.outfitPlan.addByID("Tshirt");
        selfNPC.outfitPlan.addByID("panties");
        selfNPC.outfitPlan.addByID("skirt");
        selfNPC.outfitPlan.addByID("sneakers");
        selfNPC.outfitPlan.addByID("socks");
        selfNPC.change();
        selfNPC.setTrophy(Item.KatTrophy);
        selfNPC.modAttributeDontSaveData(Attribute.power, 1);
        selfNPC.modAttributeDontSaveData(Attribute.animism, 1);
        selfNPC.modAttributeDontSaveData(Attribute.cunning, 1);
        selfNPC.modAttributeDontSaveData(Attribute.speed, 1);
        selfNPC.getStamina().setMax(100);
        selfNPC.getArousal().setMax(100);
        selfNPC.getMojo().setMax(80);

        selfNPC.body.add(BreastsPart.a);
        selfNPC.body.add(PussyPart.generic.applyMod(FeralMod.INSTANCE));
        selfNPC.body.add(AssPart.generateGeneric().upgrade());
        selfNPC.body.add(TailPart.cat);
        selfNPC.body.add(EarPart.cat);
        // mostly feminine face
        selfNPC.body.add(new FacePart(.1, 2.3));
        selfNPC.initialGender = CharacterSex.female;
    }

    private static final String KAT_SPEED_FOCUS = "KatSpeedFocus";
    private static final String KAT_POWER_FOCUS = "KatPowerFocus";
    private static final String KAT_PHEROMONE_FOCUS = "KatPheromoneFocus";
    private static final String KAT_FRENZY_FOCUS = "KatFrenzyFocus";
    private void useSpeed(NPC selfNPC) {
        Flag.flag(KAT_SPEED_FOCUS);
        selfNPC.getGrowth().addTrait(13, Trait.FeralAgility);
        selfNPC.getGrowth().addTrait(19, Trait.NimbleRecovery);
        selfNPC.getGrowth().addTrait(34, Trait.CrossCounter);
        selfNPC.getGrowth().addTrait(54, Trait.Catwalk);

    }

    private void usePower(NPC selfNPC) {
        Flag.flag(KAT_POWER_FOCUS);
        selfNPC.getGrowth().addTrait(13, Trait.FeralStrength);
        selfNPC.getGrowth().addTrait(19, Trait.Unwavering);
        selfNPC.getGrowth().addTrait(34, Trait.Untamed);
        selfNPC.getGrowth().addTrait(54, Trait.strongwilled);
    }

    private void usePheromones(NPC selfNPC) {
        Flag.flag(KAT_PHEROMONE_FOCUS);
        selfNPC.getGrowth().addTrait(22, Trait.lacedjuices);
        selfNPC.getGrowth().addTrait(28, Trait.BefuddlingFragrance);
        if (Flag.checkFlag(KAT_SPEED_FOCUS)) {
            selfNPC.getGrowth().addTrait(43, Trait.FastDiffusion);
        }
        if (Flag.checkFlag(KAT_POWER_FOCUS)){
            selfNPC.getGrowth().addTrait(43, Trait.PiercingOdor);
        }
        selfNPC.getGrowth().addTrait(49, Trait.ComplexAroma);
        selfNPC.getGrowth().addTrait(58, Trait.FrenzyScent);
    }

    private void useFrenzy(NPC selfNPC) {
        Flag.flag(KAT_FRENZY_FOCUS);
        selfNPC.getGrowth().addTrait(22, Trait.Rut);
        selfNPC.getGrowth().addTrait(22, Trait.NaturalHeat);
        selfNPC.getGrowth().addTrait(28, Trait.PrimalHeat);
        if (Flag.checkFlag(KAT_SPEED_FOCUS)) {
            selfNPC.getGrowth().addTrait(43, Trait.Jackhammer);
        }
        if (Flag.checkFlag(KAT_POWER_FOCUS)){
            selfNPC.getGrowth().addTrait(43, Trait.Piledriver);
        }
        selfNPC.getGrowth().addTrait(49, Trait.MindlessDesire);
        selfNPC.getGrowth().addTrait(58, Trait.Unsatisfied);
    }

    @Override
    public void setGrowth(NPC selfNPC) {
        selfNPC.getGrowth().stamina = 2;
        selfNPC.getGrowth().arousal = 7;
        selfNPC.getGrowth().bonusStamina = 1;
        selfNPC.getGrowth().bonusArousal = 2;
        selfNPC.addCombatScene(new CombatScene(and(level(13), not(flag(KAT_POWER_FOCUS)), not(flag(KAT_SPEED_FOCUS))),
                        (c, self, player) -> Formatter.format("Exhilarated after the fight, Kat pounces on you once again. "
                                        + "She doesn't seem too keen on having more sex, so you just hold her in your lap and pet her cute cat ears. "
                                        + "Kat narrows her eyes and purrs, <i>\"So what do you think nya? I've gotten better right?\"</i> "
                                        + "You grin and continue scratching her fluffy ears, letting her know that she has. "
                                        + "Unfortunately Kat seems to think you're just teasing her, and puffs up her cheeks cutely. "
                                        + "<i>\"Mrrrr! You're not taking me seriously! I'm actually older than you, you know?\"</i>"
                                        + "<br/><br/>"
                                        + "Face with a pouting Kat, you need to say something to appease her. Maybe some flattery would do?", self, player),
                        Arrays.asList(new CombatSceneChoice("You're fast like a cheetah!", (c, self, other) -> {
                            c.write("Kat grins and jumps to her feet, <i>\"Nya! Am I? I am right? I can go even faster you know? Meowrr!\"</i> "
                                            + "Indeed, she seems even faster than before, dancing around the room with a feline grace. "
                                            + "You can hardly even track her with your eyes! Suddenly you feel two small mounds press "
                                            + "against your back and a voice in your ear, <i>\"Thanks " + other
                                            .getName() + ", nya! "
                                            + "Next time we fight, you will nyat catch me!\"</i>." + "<br/><br/>"
                                            + "Looks like you've got your work cut out for you.");
                            useSpeed(selfNPC);
                            return true;
                        }), new CombatSceneChoice("You're strong like a tiger!", (c, self, other) -> {
                            c.write("<i>\"A tigerrr? Nyahahaha you can nyat be serious!\"</i> Kat laughs after hearing your response, "
                                            + "<i>\"But thanks! I am a bit stronger than before. The cat spirit helps me out nya!\"</i> "
                                            + "She flexes her thin looking arms and looks at you expectantly. You were going to give her "
                                            + "another compliment but instead giggle at the absurd thought of a super buff Kat."
                                            + "<br/><br/>"
                                            + "<i>\"Mrrr..! Don't laugh nya! I'll show you, I can be strong too!\"</i> "
                                            + "She stalks off in an angry but cute way, leaving you wondering if that was the right thing to say.");
                            usePower(selfNPC);
                            return true;
                        }), new CombatSceneChoice("You're cute like a housecat! You can be my pet! [Hard Mode]",
                                        (c, self, other) -> {
                                            c.write("<i>\"MRRrr!\"</i> Kat grows at you, <i>\"You're still underestimating me, nya! "
                                                            + "I'll show you I can be just as fast and strong as everyone else!\"</i> "
                                                            + "She gets up angrily and stomps away. Okay you <b>PROBABLY</b> shouldn't have done that. "
                                                            + "Her new resolve could be... <i>troubling</i>.");
                                            useSpeed(selfNPC);
                                            usePower(selfNPC);
                                            selfNPC.getGrowth().extraAttributes += 1;
                                            GameState.getGameState().characterPool.getPlayer().getGrowth()
                                                            .addTraitPoints(new int[] {12, 39},
                                                                            GameState.getGameState().characterPool
                                                                                            .getPlayer());
                                            return true;
                                        }))));
        selfNPC.addCombatScene(
                        new CombatScene(and(level(22), not(flag(KAT_FRENZY_FOCUS)), not(flag(KAT_PHEROMONE_FOCUS)),
                                        or(flag(KAT_POWER_FOCUS), flag(KAT_SPEED_FOCUS))), (c, self, player) ->
                                        "Kat smells like an animal in heat, and her scent is driving you wild. "
                                                        + "You're not sure if she notices this herself, but you can definitely tell that "
                                                        + "you're not going to calm down any time soon. Against your better judgement, "
                                                        + "you naturally gravitate towards her again, seeking to inhale more of that "
                                                        + "delightful tangy air. Kat seems a bit wary as you start sniffing her neck and arms "
                                                        + "but her lust from you indulging in your perversity quickly overwhelms her."
                                                        + "<br/>"
                                                        + "Kat begs you pitifully, <i>\"Meowr... don't just sniff me! That's nyat enough! Fuck me! Give it to me, myaa!\"</i>"
                                                        + "She splays her legs invitingly, and offers you a chance to continue.",
                                        Arrays.asList(new CombatSceneChoice("Continue sniffing her",
                                                        (c, self, other) -> {
                                                            c.write("You ignore Kat's invitation, causing her to huff angrily. It hardly bothers you though, when your head is full of "
                                                                            + "Kat's enticing pheromones. You lift Kat's arm above her head and plunge your nose into her armpit. "
                                                                            + "She smells wonderful! The complex aroma is difficult to describe, containing hints of musk, cinnamon "
                                                                            + "and good old fashioned female arousal. "
                                                                            + "<br/><br/>"
                                                                            + "You continue to hump her legs while moaning into her armpit, making her blush bright crimson. "
                                                                            + "She probably thinks you're a complete pervert at this point, but it's pretty difficult to give that thought the time of day "
                                                                            + "when she smells so good. Finally, Kat seems to have had enough of your unintended teasing and shoves you off her and pounces on you."
                                                                            + "<i>\"T-that was myean " + other.getName()
                                                                            + "! You can't just get off by yourself nya! "
                                                                            + "If you want to s-sniff, do it w-while... d-don't myake me say it!\"</i> "
                                                                            + "Even though her words are rather demure, her actions certainly aren't. She sits herself right on your face, "
                                                                            + "shoving her soaked pussy onto your lips. You don't really mind though, "
                                                                            + "her feral cunt is the source of her where her laviscious smell, and you dig right in. "
                                                                            + "You're not sure how much timed passed or how many screeching climaxes you brought her to, "
                                                                            + "but after Kat finally peels you off her muff, you can only sit there in a daze."
                                                                            + "<br/><br/>"
                                                                            + "Kat dusts herself off and gives you a big kiss on the lips. <i>I did nyat know you had a smell fetish... "
                                                                            + "Don't worry, I wont tell anyone-nyaa. J-just... next time you feel the nyeed, come see me won't you?</i>");
                                                            usePheromones(selfNPC);
                                                            return true;
                                                        }), new CombatSceneChoice("Fuck her like she asked",
                                                        (c, self, other) -> {
                                                            c.write(Formatter.format("Well you're not one to refuse a lady. Holding Kat's lovely legs above your shoulders, you slam into her drenched snatch. "
                                                                                            + "She screeches at your sudden intrusion, but soon her voice mellows out to sweet moans as you pound her with reckless abandon. "
                                                                                            + "The two of you spend a while just enjoying the sensations of your bodys meeting again and again; her velvety wetness against your rockhard pole. "
                                                                                            + "Alas all good things have to come to an end. With a final few pumps, you ejaculate your thick cum into the petite girl, eliciting a pleasant purr from her. "
                                                                                            + "<br/><br/>"
                                                                                            + "Whew, after a job well done, you try to disentangle yourself from the strawberry blonde. Kat seems to have different ideas though. "
                                                                                            + "She hooks her legs behind your back and grabs onto your neck, not letting go. One look at her flushed face and you know she's not going to let you go easily. "
                                                                                            + "Rolling you onto your back, she mounts you again. Since you just came, your penis is still completely flaccid. However that doesn't even seem to register in Kat's mind. "
                                                                                            + "Unable to feed your cock inside herself, the sex-kitten gives up and starts dry humping your leg while licking at your nipples. "
                                                                                            + "You didn't think you had another shot in you, but a combination of Kat's desperate assault and the sex pheromones she's been releasing all this time gives you a final boost. "
                                                                                            + "Finally noticing your dick poking into her stomach again, Kat gives you a happy little growl, <i>\"Give it to myoew, fuck mee fuck fuck fuck!\"</i> "
                                                                                            + "Your dick still feels super sensitive from all the activity you've been putting it through tonight, but Kat doesn't really give you a choice. "
                                                                                            + "Slipping your meat rod inside her, she rides you wildly, buckling her hips and slamming her ass into your body with each motion. "
                                                                                            + "<br/><br/>"
                                                                                            + "By the time you cum for the last time tonight, Kat probably had innumerable tiny climaxes. Not that any of that stopped her frenzied mating. "
                                                                                            + "You finally get the chance to pull her off you and let her calm down a bit. She looks still dazed at first, but after a few minutes in the night air, "
                                                                                            + "reason returns to her eyes. Realizing what she did, Kat stammers, <i>\"I-it's nyat like that! S-sorry... I d-didn't m-mean to... "
                                                                                            + "S-sometimes the animal spirit gets restless and I go into h-h-heat. I can't really control myself, meowrr\"</i> You calm the little kittycat down "
                                                                                            + "and reassure her that you're not angry. In fact, you found her frantic self adorable - not to mention hot as hell. It took a bit of time and she is "
                                                                                            + "still red as a tomato when she whispers, <i>\"T-thanks "
                                                                                            + other.getName()
                                                                                            + ", that myeans a lot to m-meowrr. If you l-liked it, I'll see if I can control it nya-ext time!\"</i> "
                                                                                            + "You smile wryly as Kat leaves. You feel that she'll be a force to be reckoned with the next time you see her.",
                                                                            self, other));
                                                            useFrenzy(selfNPC);
                                                            return true;
                                                        }), new CombatSceneChoice(
                                                        "Sex, but continue indulging in her scent [Hard Mode]",
                                                        (c, self, other) -> {
                                                            c.write(Formatter.format("Sex with Kat sounds like a good idea, but you can't bring yourself to stop inhaling her sexy odor. "
                                                                            + "The pheromones must be getting to your head, since it took a good ten seconds of indecision before the proverbial lightbulb lit up. "
                                                                            + "Why not just do both? Not even separating your nose from her skin for a split second, you hug her body close to you and "
                                                                            + "plung into her tightness in a seated position."
                                                                            + "<br/><br/>"
                                                                            + "That was the best idea ever! On one hand, you have her heavenly scent right next to your face. "
                                                                            + "On the other, you have a fiery catgirl squriming on your cock. You can't help but moan at the intense stimulation, "
                                                                            + "both physical and olfactory. The two of you keep at the act, oblvious of the situation around you for a good length of time. "
                                                                            + "Only when you're so spent that you can't continue does Kat let up her frenzied buckling."
                                                                            + "<br/><br/>"
                                                                            + "Kat grins with a flushed face, <i>\"T-that was amazing! Y-you know, if that's what it takes to get you going, I'll do whatever I can-nya!\"</i> "
                                                                            + "You show a trouble face when she leaves. Having sex with a hungry sex kitten while high on pheromones did feel great, but you're not sure if you can last the night again.", self, other));
                                                            usePheromones(selfNPC);
                                                            useFrenzy(selfNPC);
                                                            selfNPC.getGrowth().extraAttributes += 1;
                                                            // some compensation for the added difficulty. She gets 6 traits and 1 attribute point/level, and you only get 2 traits, but you are fighting more people than just her.
                                                            GameState.getGameState().characterPool.getPlayer().getGrowth()
                                                                            .addTraitPoints(new int[] {21, 48},
                                                                                            GameState.getGameState().characterPool
                                                                                                            .getPlayer());
                                                            return true;
                                                        }))));
        preferredAttributes.add(c -> Optional.of(Attribute.animism));
        selfNPC.getGrowth().addTrait(0, Trait.dexterous);
        selfNPC.getGrowth().addTrait(0, Trait.pheromones);
        selfNPC.getGrowth().addTrait(0, Trait.shy);
        selfNPC.getGrowth().addTrait(0, Trait.petite);
        selfNPC.getGrowth().addTrait(10, Trait.sympathetic);
        selfNPC.getGrowth().addTrait(16, Trait.cute);
        selfNPC.getGrowth().addTrait(20, Trait.breeder);
        selfNPC.getGrowth().addTrait(25, Trait.nymphomania);
        selfNPC.getGrowth().addTrait(31, Trait.tight);
        selfNPC.getGrowth().addTrait(37, Trait.catstongue);
        selfNPC.getGrowth().addTrait(40, Trait.graceful);
        selfNPC.getGrowth().addTrait(46, Trait.sexTraining1);
        selfNPC.getGrowth().addTrait(55, Trait.responsive);
        selfNPC.getGrowth().addTrait(61, Trait.sexTraining2);
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
        if (selfNPC.money > 0) {
            Daytime.getDay().visit("Body Shop", selfNPC, Random.random(selfNPC.money));
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
        selfNPC.addLine(CharacterLine.BB_LINER, (c, self, other) -> "Kat gives you a look of concern and sympathy. <i>\"Nya... Are you ok? I didn't mean to hit you that hard.\"</i>");

        selfNPC.addLine(CharacterLine.CHALLENGE, (c, self, other) -> "Kat looks a bit nervous, but her tail wags slowly in anticipation. <i>\"Let's have some funNya.\"</i>");

        selfNPC.addLine(CharacterLine.NAKED_LINER, (c, self, other) -> {
            if (self.getArousal()
                    .percent() >= 50) {
                return "Kat makes no effort to hide the moisture streaming down her thighs. <i>\"You want my pussy? I'm nyot going to myake it easy for you.\"</i>";
            } else {
                return "Kat blushes deep red and bashfully tries to cover her girl parts with her tail. <i>\"Don't stare too much, ok?\"</i>";
            }
        });

        selfNPC.addLine(CharacterLine.STUNNED_LINER, (c, self, other) -> "Kat mews pitifully on the floor. <i>\"Don't be so meaNya.\"</i>");

        selfNPC.addLine(CharacterLine.TAUNT_LINER, (c, self, other) -> "Kat smiles excitedly and bats at your cock. <i>\"Are you already close to cumming? Nya! I want to play with you more!\"</i>");

        selfNPC.addLine(CharacterLine.TEMPT_LINER, (c, self, other) -> "Kat winks at you and looks at your crotch, <i>\"MmM! That looks tasty nya!\"</i>");

        selfNPC.addLine(CharacterLine.NIGHT_LINER, (c, self, other) ->
                        "You walk back to your dorm after the match, but something is bothering you the whole way. You feel like you're being watched, but there's no menace to the sensation. "
                                        + "You hear a rustling behind you and look back just in time to see someone duck behind a bush. 'Someone'.... The cat ears on that hat make your stalker's identity "
                                        + "pretty obvious. You pretend you didn't see anything and continue toward your dorm. So, a stray Kat is following you home tonight. It would make more sense for her "
                                        + "to just come out so you can walk together, but you know how shy she can be about things like this. On the other hand, she is following you back to your room at night "
                                        + "with obvious implications, so... does that count as being forward? <br/><br/>You reach the door to your dorm building and look back to see Kat clumsily attempting to hide behind a "
                                        + "tree. Oh good grief. How long is she planning to stay hidden? The door to the building locks automatically, so if you go inside she'll be stuck out here. Besides, the closer "
                                        + "you get to your room, the harder it's going to be for her to work up to courage to approach you. You walk over to where she's hiding and she freezes in panic. Before she "
                                        + "can run off, you catch her and gently pat her on the head. It's starting to get chilly out here. She should just come inside with you. Kat blushes furiously, but looks "
                                        + "delighted as you lead her to your room.");

        selfNPC.addLine(CharacterLine.ORGASM_LINER, (c, self, other) -> "<i>\"NYAAAH! uuu...\"</i> Kat glares at you reproachfully as she comes down from her high.");

        selfNPC.addLine(CharacterLine.MAKE_ORGASM_LINER, (c, self, other) -> "<i>\"Nyahaha! I gotcha! Hey hey, do you think you can cum again-nyaa? Let's try it out!\"</i>");

        selfNPC.addLine(CharacterLine.DESCRIBE_LINER, (c, self, other) ->
                        "It's easy to forget that Kat's older than you when she looks like she's about to start high school. She's a very short and slim, though you know she's "
                                        + "stronger than she looks. Adorable cat ears poke through her short, strawberry blonde hair. She "
                                        + "looks a bit timid, but there's a gleam of desire in her eyes.");

        selfNPC.addLine(CharacterLine.LEVEL_DRAIN_LINER, (c, self, other) -> {
            if (other.getLevel() < self.getLevel() - 5) {
                return "You don't even think Kat notices your meager strength draining into her much larger pool as she passionately fucks you in her bestial lust. <i>\"Nyahaha! Can't stop... WONT stop!\"</i>";
            } else if (other.getLevel() >= self.getLevel()) {
                return "Your eyes widen as you feel your experience and training leave you and enter Kat's hungry body alongside your climax. "
                                + "Kat however doesn't seem to really know what is going on, <i>\"Meowrrrr... That feels <b>REALLY</b> gooooodd... mmm...\"</i>";
            } else {
                String part = Random.pickRandom(c.getStance().getPartsFor(c, self, other)).map(bp -> bp.describe(self)).orElse("pussy");
                return "The sexy kitty doesn't pause for even a second as you cum your life force into her gluttonous " + part + ". <i>Aahh ahh! You're delicious nyaa!</i>";
            }
        });
    }

    @Override
    public String victory(Combat c, Result flag, NPC selfNPC) {
        Character opponent = c.getOpponent(selfNPC);
        selfNPC.arousal.empty();
        if (c.getStance().vaginallyPenetrated(c, selfNPC)) {
            opponent.add(c, Pheromones.getWith(selfNPC, opponent, 5, 10));
            return "She pounces at you, pushing you onto your back and holds you down with the weight of her body. A cute mew of a smile crosses her face, and her tongue sticks "
                            + "out slightly from between her lips. She is riding your cock in a regular rhythm now, not worried as she knows you are much closer to your climax than her.<br/><br/>"
                            + "As you gasp and wriggle, trying to escape from a loss she reaches out and gently scratching and tickling your nipples.<br/><br/>"
                            + "<i>\"Nyahaha!\"</i> she giggles, toying with your body as a cat would toy with a mouse it caught.<br/><br/>"
                            + "The added sensation of having your nipples molested is enough to drive you over the edge and you feel your cock begin pulsating inside of her tight cunt. "
                            + "You grunt a couple of times as her phermones prolong the contractions of your climax. When the cum has finally been drunk from your cock by her tight love "
                            + "hole she gasps and lets you slip out.<br/><br/>"
                            + "Collapsing onto your back your chest heaves, your mind a blurry mess from the massive orgasm you just experienced. You can feel your orgasmic high subside "
                            + "slowly and peek over at the catgirl to see how she's doing. To your surprise you can see that she's curled up beside you with a small trail of your semen "
                            + "leaking out of her pussy and forming a little puddle around her ass.<br/><br/>The look on her face is one of mixed emotions. Something in between the satisfaction "
                            + "of having a primal need sated and frustration because something else is lacking. The catgirl seems confused, not sure how to react to all the emotions "
                            + "bubbling up inside of her. A low <i>\"meeeow~\"</i> escapes her lips as she stares at you, her big round eyes blinking slowly. She begins mumble something "
                            + "else, stopping just before the second syllable passes her lips. Her tail begins swaying back and forth suggesting a persistent unquenched agitation.<br/><br/>"
                            + "To you the catgirl looks more like a cat who caught the smell of her master cooking chicken and came running to beg for a piece rather than the sly little "
                            + "vixen that just pounced at you, milking your excited cock for all the semen she could. Then you realised the problem she was facing, receiving your cum in "
                            + "her pussy only served to satisfy her primal instinct; but that primal instinct was waning now and it was her human side that was beginning to surface. That "
                            + "human side that desired to do more than simply reproduce, that human desire for pleasure.  To satisfy that deep, human need to be allowed an orgasm, she's "
                            + "not about to let you go until she's completely satisfied. Crawling up over you she meets your gaze, her eyes begging for you to give her that relished release. "
                            + "She doesn't need to utter a single sound; you're certain what she needs, even if she doesn't quite know herself.<br/><br/>"
                            + "You debate how to repay her, eventually settling on your skilled fingers. You roll over on top of Kat, whispering hot breathy nothings into her furry ear. "
                            + "She squirms and wriggles at the attention, her pussy moistening under your skilled manipulation of her clitoris. She is putty in your hands now, you plunge "
                            + "two fingers past her labia, feeling them easily slide into her body. Her aching g-spot longs to be rubbed and massaged. Her arms clench tightly around your "
                            + "back, as you are pulled in closer to her body you feel her warmth radiating off her hot skin.<br/><br/>"
                            + "<i>\"NyaaaaAAAAAAA!\"</i> she howls in an animalistic outburst, her hips bucking uncontrollably against the perpetual rubbing of your fingertips. In mere "
                            + "minutes you have lifted her to an orgasmic high, soaking your hand in her sweet essence before crashing back to Earth in exhaustion.<br/><br/>"
                            + "<i>\"Wow\"</i> she remarks, easing her grip around your back as you slowly withdraw your fingers. Her tail tickles your side as it curls back around. She "
                            + "stands up and puts her clothes back on before rushing away down the corridor, <i>\"See you around!\"</i>";
        } else {
            opponent.add(c, Pheromones.getWith(selfNPC, opponent, 5, 10));
            opponent.add(c, Pheromones.getWith(selfNPC, opponent, 20, 5, " feral musk"));
            opponent.arousal.restore(opponent.arousal.max() / 3);
            return "As Kat pleasures you, you're quickly reaching the limit of your control. You try to put some distance between you and her to catch your breath. The familiar "
                            + "tightness in your groin warns you that any stimulation will probably set you off right now. Eager to finish you off, Kat pounces on you and grabs your "
                            + "dick with both hands. <i>\"Nyaha! I got you!\"</i> The last of your endurance is blown away as she jerks you off excitedly. You shoot your load into the air, "
                            + "narrowly missing her face.\n\n You lay on the floor, trying to catch your breath and notice that Kat is staring at you intently, still holding onto your "
                            + "member. If she plans to keep milking you, you're going to need a few minutes to recover. <i>\"I caught you. You're nyat getting away until we're done.\"</i> "
                            + "There's no way she failed to notice your ejaculation, the fight's clearly over. Kat bites her lip and you notice her hips squirming with need. Ah, that's "
                            + "what she means. You were never planning to leave without returning the favor. You ask her to let you up so you can service her properly. She shakes her head emphatically. "
                            + "<i>\"I'm nyat letting go of you until I'm satisfied.\"</i> This must be an instinctive thing. She's caught her prey, so she's reluctant to give it up. Fortunately, "
                            + "she's pretty light, so you're able to simply drag her hips closer to your face. She still refuses to loosen her grip on your penis, which is starting to "
                            + "harden again from the continous stimulation, but at least you have access to her girls parts now. You slide a finger into her wet pussy and she gives an "
                            + "appreciative moan. You spot her little love bud peeking out between her lips, but you leave it alone for now, you're aiming for a different target. You rub "
                            + "your probing finger along her inner walls until you notice a spot with a rougher texture. As you stimulate this area, Kat's waist trembles and her voice catches "
                            + "in her throat. G-Spot successfully located.<br/><br/>You add a second finger and focus on rubbing her G-Spot until she's writhing in pleasure and mewing uncontrollably. "
                            + "Only then do you close your lips around her clitoris and attack it with your tongue. This triggers Kat's climax instantly. Her hands involuntarily squeeze your "
                            + "dick as her pussy squeezes your fingers just as firmly. As her orgasm ends and she lays on the floor panting, she finally lets go of your dick. <i>\"I'm sorry about "
                            + "being stubborn,\"</i> you hear her mumble. <i>\"Sometimes my cat instincts make it hard to think and I get carried away. I hope I didn't hurt your- Nya!?\"</i> She lets out "
                            + "a surprised yelp as she opens her eyes and sees your fully engorged cock inches away from her face. This is apparently too much for her to handle right now, because "
                            + "she turns beet red and runs away in a panic, leaving you frustrated and unsatisfied.";
        }
    }

    @Override
    public String defeat(Combat c, Result flag, NPC selfNPC) {
        Character opponent = c.getOpponent(selfNPC);
        if (c.getStance().vaginallyPenetrated(c, selfNPC)) {
            opponent.add(c, Pheromones.getWith(selfNPC, opponent, 5, 10));
            return "Kat squeaks as you pump your cock inside her over and over, penetrating her deeper with each thrust. She seems to be particularly vulnerable to being fucked"
                            + " doggy style; perhaps it makes her g-spot easier to hit each time you thrust into her.<br/>"
                            + "<i>\"Ah ahhhh eeeepppp!\"</i><br/>"
                            + "Her back suddenly arches after a particularly strong thrust. You feel her pussy walls clamp and quiver around your cock, undulating as she rides out a climax.<br/><br/>"
                            + "<i>\"Nyaaa!\"</i><br/><br/>"
                            + "Her final pleasurable moan escapes her lips before she slides off your cock and onto the floor, catching her breath.<br/><br/>"
                            + "<i>\"A-alright, this is the part when I...\"</i> she reaches up to your stiff cock with trembling hands and grabs it with a nervous excitement. For a second "
                            + "you think she is going to jerk you off, then you hear her again, <i>\"L-lie down\"</i><br/><br/>"
                            + "You comply, lying on your back while she positions herself above you, straddling your waist. She is clearly in heat, her cheeks blushing a crimson red, and "
                            + "her pussy as hot and wet as any girl you've ever felt. As she squats down, taking your member inside her she half-gasps, half-purrs. It doesn't take long "
                            + "for her to pick up the pace and establish a steady rhythm of gliding up and down your shaft. Her ears twerk back and forth and she leans forward, resting "
                            + "her palms on your shoulders.<br/><br/>"
                            + "You can barely take it anymore, her pussy feels is a perfect fit, massaging your cock to a blissful orgasm. You grunt twice and shoot a load deep inside her. "
                            + "This seems to satisfy her as much, if not more than her first orgasm. Perhaps some primal instinct to breed? Sliding off your cock she plants a quick kiss "
                            + "on your forehead and scurries out of the room, leaving you gasping and pulling yourself back to your feet.";
        } else {
            opponent.add(c, Pheromones.getWith(selfNPC, opponent, 5, 10));
            opponent.add(c, Pheromones.getWith(selfNPC, opponent, 20, 5, " feral musk"));
            return "Kat whimpers in pleasure and drops limp to the floor, either too aroused to resist or simply realizing it's futile. Your hand busily works between her "
                            + "open thighs as she moans and squirms erotically. Her pert, round breasts jiggle with her movements and you can't resist covering them in wet kisses. You "
                            + "part her lower lips with your fingers and gently tickle her sensitive love button. Her back arches and she lets out a loud meow as she climaxes."
                            + "<br/><br/>Kat lays on the floor in a daze for a "
                            + "short while before self-consciously covering her private parts. <i>\"You got me. Your fingers are very...\"</i> She reddens when she notices your erection and quickly "
                            + "averts her eyes. <i>\"W-would you like me to help deal with... that?\"</i> Her innocent reactions are adorable. You feel compelled to see her even more embarrassed, "
                            + "and of course to see her orgasm again. You forcefully pin her arms by her sides, exposing her naked body, and you stick your rigid member on front of her "
                            + "face. \"Nya!? Should I.. should I use my mouth?\" She hesitantly sticks out her tongue and starts licking the head of your penis. You release her hands "
                            + "and she softly grasps and strokes your dick.<br/><br/>Her timid efforts are surprisingly effective, so you reward her by pulling her legs open and sticking your "
                            + "face into her crotch. She stutters out a protest, but it devolves into whimpering when you run your tongue over her pussy lips. She's so enthralled "
                            + "by your oral skills that she momentarily forgets to service your dick and you have to stop licking her until she resumes. She gradually starts licking and "
                            + "sucking your penis more passionately and soon there's no trace of her previous shyness. Her tail is twitching erratically and almost hits you in the face "
                            + "a few times as she lets out little mewling noises despite her mouth being full. You feel pleasure building up in your dick as you enter the home stretch. You "
                            + "quickly locate her swollen clit and focus your tongue-work on it while you push two fingers into her soaked pussy. You feel her shudder and tense under you while "
                            + "you give in to the pleasure and fill her mouth with cum.<br/><br/>In the afterglow, Kat seems so exhausted and content that you have to make sure she's ok. She gives "
                            + "you a shy smile as she sits up. <i>\"That was a little overwhelming, but I don't mind that sort of thing.\"</i> You give her a kiss on the cheek and stroke her head "
                            + "for a minute before you head out on your way.";
        }
    }

    @Override
    public String draw(Combat c, Result flag, NPC selfNPC) {
        Character opponent = c.getOpponent(selfNPC);
        if (flag == Result.intercourse) {
            opponent.add(c, Pheromones.getWith(selfNPC, opponent, 5, 10));
            return "Kat lets out a near-constant mewing of pleasure as you thrust repeatedly into her tight pussy. You feel yourself rapidly approaching orgasm and judging by "
                            + "how she's desperately clinging to you and thrashing her tail, she's probably in a similar state. As you feel the pressure building up in your abdomen, you "
                            + "suck on her neck hard enough to give her a hickey. Your cock spurts inside her and she yowls as she hits her climax. <br/><br/>You enjoy your afterglow, holding her "
                            + "small warm body while she pants with exertion. You softly stroke her cat ears and she looks up at you with content eyes. <i>\"Feels nice... That and down there.\"</i> "
                            + "You grin and grind your cock -starting to soften, but still inside her- against her love bud. She lets out a soft whimper and shivers with pleasure. <i>\"Nya! "
                            + "Really nice....\"</i><br/><br/>The fight is over and you're not physically ready for a second round, but Kat doesn't seem to be in any hurry to leave. You don't mind spoiling "
                            + "her a bit. You kiss her softly on the lips and gently run your fingers over her smooth skin. You're not focusing on any erogenous zones, but she still shivers "
                            + "and coos appreciatively under your touch. <i>\"Nya....\"</i> She lets out a happy noise as you release her lips. <i>\"Spoil me as much as you like.\"</i><br/><br/>You sit up and pull "
                            + "Kat onto your lap, holding her with one hand, while the other plays with her messy girl parts. Even if she wasn't wet with her own juices, your previous load of "
                            + "spunk would be plenty of lubrication. She wraps her arms around your neck and hugs you as you finger her to ecstasy. When she starts to shudder in your arms, "
                            + "signalling her second climax, you press your lips firmly against hers. <i>\"Thanks,\"</i> She whispers shyly after she recovers. <i>\"You're really sweet. I still can't "
                            + "let you win without a fight, but if we met during the day....\"</i> She blushes deeply and quickly excuses herself.";
        } else {
            opponent.add(c, Pheromones.getWith(selfNPC, opponent, 5, 10));
            opponent.add(c, Pheromones.getWith(selfNPC, opponent, 20, 5, " feral musk"));
            return "Kat has you backed up against a wall, your lips locked in a passionate kiss. Her nimble fingers are wrapped around your stiff cock, pumping on your member as "
                            + "quickly as she can. The feeling of her tongue dancing in your mouth, is driving you to the brink of an orgasm. You're almost certain you're not going to make it.<br/><br/>"
                            + "You reach down, fumbling desperately between her thighs, trying to find her clitoris and make a comeback. It's difficult to concentrate though, as she starts "
                            + "gently rolling your balls in between her skilled fingers.<br/><br/>"
                            + "Suddenly you locate her clitoris with your thumb, rubbing it quickly as you plunge two fingers up into her dripping pussy.<br/><br/>"
                            + "<i>\"Nyyaaaa!\"</i> she meows in ecstasy, breaking the kiss. Her fingers return to your shaft and resume pumping it furiously. You seize the opportunity to regain "
                            + "lost ground and teasingly nibble on her ultra-sensitive ear, letting your tongue dance over the edge of it. Suddenly your gut clenches as you feel the first "
                            + "waves of an orgasm begin to build at the base of your cock. You're going to cum, you're going to-<br/><br/>"
                            + "<i>\"NYYAAAAAAAAAAAAAA!\"</i><br/>"
                            + "Kat's squeals echo down the corridors as a climax crashes through her body, making her spasm and writhe in ecstasy. You explode at the same time, shooting "
                            + "thick streams of cum over her belly. The two of you lean against each other in the wake of the climax.<br/><br/>"
                            + "Kat recovers a little bit quicker than you and scampers away down the corridor.<br/>"
                            + "<i>\"See ya around!\"</i>";
        }
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
            return "Kat crouches between your legs, watching your erect penis twitch and gently bob. She playfully bats it back and forth, clearly enjoying herself. That is "
                            + "most definitely not a toy, but she seems to disagree. As your boner wags from side to side, she catches it with her mouth, fortunately displaying the "
                            + "presence of mind to keep her teeth clear. She covers the head of your dick with her mouth and starts to lick it intently, while she teases your shaft "
                            + "with both hands. Oh God. You're not sure whether or not this is still a game to her, but she's being very effective. With the combined pleasure from her "
                            + "mouth and hands, it's not long before your hips start bucking in anticipation of release. She jerks you off with both hands and sucks firmly on your glans. "
                            + "You groan as you ejaculate into her mouth and she eagerly swallows every drop.";
        } else {
            return "Kat stalks over to " + target.getName()
                            + " like a cat. Very much like a cat. She leans toward the other girl's breasts and starts to softly lick her nipples. "
                            + target.getName()
                            + " stifles a moan of pleasure as Kat gradually covers her breasts in saliva, not missing an inch. Kat gradually works her way down, giving "
                            + "equal attention to " + target.getName()
                            + "'s bellybutton. By the helpless girl's shudders and stifled giggles, it clearly tickles, but also seems to heighten "
                            + "her arousal. You feel her body tense up in anticipation when Kat licks her way down towards her waiting pussy. Instead Kat veers off and starts kissing "
                            + "and licking her inner thighs. " + target.getName()
                            + " trembles in frustration and she must realize she has no chance to win, because she yells out: <i>\"Please! "
                            + "Stop teasing me and let me cum!\"</i> Kat blinks for a moment and looks surprised, like she had forgotten her original purpose, but she soon smiles and dives "
                            + "enthusiastically between " + target.getName()
                            + "'s legs. It doesn't take long before you feel her shudder in orgasm from Kat's intense licking, but even then, "
                            + "Kat shows no sign of stopping. " + target.getName()
                            + " stammers out a protest and you sympathetically release her hands, but she's too overwhelmed by pleasure "
                            + "to defend herself as Kat licks her to a second orgasm.";
        }
    }

    @Override
    public String intervene3p(Combat c, Character target, Character assist, NPC selfNPC) {
        if (target.human()) {
            return "Your fight with " + assist.getName()
                            + " goes back and forth for several minutes, leaving you both naked and aroused, but without a clear winner. You back off "
                            + "a couple steps to catch your breath, when suddenly you're blindsided and knocked to the floor. You look up to see Kat straddling your chest in full cat-mode. "
                            + "She's clearly happy to see you, kissing and nuzzling your neck while mewing happily. She's very cute, but unfortunately you're unable to dislodge her from "
                            + "her perch on your upper body. More unfortunately, your completely defenseless lower body is exposed to "
                            + assist.getName() + ", who doesn't let the opportunity go " + "to waste.";
        } else {
            return "Your fight with " + target.getName()
                            + " goes back and forth for several minutes, leaving you both naked and aroused, but without a clear winner. You back off "
                            + "a couple steps to catch your breath, when you notice a blur of motion out of the corner of your eye. Distracted as you are, you aren't paying attention to "
                            + "your footing and stumble backwards. " + target.getName()
                            + " advances to take advantage of your mistake, but she soon realizes you aren't looking at her, but "
                            + "behind her. She turns around just in time to get pounced on by an exuberant catgirl. Kat starts to playfully tickle her prey, incapacitating the poor, "
                            + "naked girl with uncontrollable fits of laughter. You stand up, brush off your butt, and leisurely walk towards your helpless opponent to finish her off.";
        }
    }

    @Override
    public boolean fit(NPC selfNPC) {
        return !selfNPC.mostlyNude() && selfNPC.getStamina().percent() >= 50
                        || selfNPC.getArousal().percent() > 50;
    }

    @Override
    public boolean checkMood(Combat c, Emotion mood, int value, NPC selfNPC) {
        if (selfNPC.getArousal().percent() >= 50) {
            if (!selfNPC.is(Stsflag.feral)) {
                selfNPC.add(c, new Feral(selfNPC.getType()));
            }
            if (!selfNPC.has(Trait.shameless)) {
                selfNPC.add(Trait.shameless);
                selfNPC.remove(Trait.shy);
            }
            switch (mood) {
                case horny:
                    return value >= 50;
                case nervous:
                    return value >= 150;
                default:
                    return value >= 100;
            }
        } else {
            if (!selfNPC.has(Trait.shy)) {
                selfNPC.add(Trait.shy);
                selfNPC.remove(Trait.shameless);
                selfNPC.removeStatus(Stsflag.feral);
            }
            switch (mood) {
                case nervous:
                    return value >= 50;
                case angry:
                case horny:
                    return value >= 150;
                default:
                    return value >= 100;
            }
        }
    }
}
