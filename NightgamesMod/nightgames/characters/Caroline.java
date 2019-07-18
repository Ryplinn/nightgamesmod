package nightgames.characters;

import nightgames.actions.Action;
import nightgames.actions.Movement;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.FacePart;
import nightgames.characters.body.WingsPart;
import nightgames.characters.body.mods.ArcaneMod;
import nightgames.characters.custom.CharacterLine;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;

import java.util.Collection;
import java.util.Optional;

public class Caroline extends BasePersonality {
    private static final long serialVersionUID = 8601852023164119671L;

    public Caroline() {
        super(false);
    }

    @Override
    public void applyStrategy(NPC selfNPC) {}

    @Override
    public void applyBasicStats(NPC selfNPC) {
        preferredCockMod = CockMod.error;
        selfNPC.outfitPlan.addByID("lacybra");
        selfNPC.outfitPlan.addByID("lacepanties");
        selfNPC.outfitPlan.addByID("stockings");

        selfNPC.change();
        selfNPC.modAttributeDontSaveData(Attribute.seduction, 1);
        selfNPC.modAttributeDontSaveData(Attribute.cunning, 2);
        selfNPC.modAttributeDontSaveData(Attribute.perception, 1);
        selfNPC.modAttributeDontSaveData(Attribute.speed, 1);
        selfNPC.getStamina().setMax(120);
        selfNPC.getArousal().setMax(120);
        selfNPC.rank = 1;
        selfNPC.adjustTraits();

        selfNPC.getMojo().setMax(110);

        selfNPC.setTrophy(Item.ExtremeAphrodisiac);
        selfNPC.body.add(BreastsPart.b);
        selfNPC.initialGender = CharacterSex.female;
    }

    @Override
    public void setGrowth(NPC selfNPC) {
        selfNPC.getGrowth().stamina = 4;
        selfNPC.getGrowth().arousal = 7;
        selfNPC.getGrowth().willpower = .4f;
        selfNPC.getGrowth().bonusStamina = 2;
        selfNPC.getGrowth().bonusArousal = 2;

        selfNPC.getGrowth().addTrait(0, Trait.ticklish);
        selfNPC.getGrowth().addTrait(0, Trait.dexterous);
        selfNPC.getGrowth().addTrait(10, Trait.limbTraining1);
        selfNPC.getGrowth().addTrait(10, Trait.tongueTraining1);
        selfNPC.getGrowth().addTrait(15, Trait.healer);
        selfNPC.getGrowth().addTrait(20, Trait.romantic);
        selfNPC.getGrowth().addTrait(25, Trait.hawkeye);
        selfNPC.getGrowth().addBodyPartMod(30, "pussy", ArcaneMod.INSTANCE);
        selfNPC.getGrowth().addBodyPart(30, WingsPart.ethereal);
        selfNPC.getGrowth().addTrait(30, Trait.kabbalah);
        selfNPC.getGrowth().addTrait(35, Trait.protective);
        selfNPC.getGrowth().addTrait(40, Trait.magicEyeFrenzy);
        selfNPC.getGrowth().addTrait(45, Trait.supplicant);
        selfNPC.getGrowth().addTrait(50, Trait.magicEyeTrance);
        selfNPC.getGrowth().addTrait(55, Trait.beguilingbreasts);

        preferredAttributes.add(c -> Optional.of(Attribute.cunning));
        preferredAttributes.add(c -> c.getLevel() >= 30 ? Optional.of(Attribute.spellcasting) : Optional.empty());
        // mostly feminine face, cute but not quite at Angel's level
        selfNPC.body.add(new FacePart(.1, 2.9));
    }

    @Override
    public Action move(Collection<Action> available, Collection<Movement> radar, NPC selfNPC) {
        return Decider.parseMoves(available, radar, selfNPC);
    }

    @Override
    public void rest(int time, NPC selfNPC) {}

    @Override
    public String victory(Combat c, Result flag, NPC selfNPC) {
        return "";
    }

    @Override
    public String defeat(Combat c, Result flag, NPC selfNPC) {
        return "";
    }

    @Override
    public String draw(Combat c, Result flag, NPC selfNPC) {
        return "";
    }

    private static String FOUGHT_CAROLINE_PET = "FOUGHT_CAROLINE_PET";
    @Override public void constructLines(NPC selfNPC) {
        selfNPC.addLine(CharacterLine.BB_LINER, (c, self, other) -> "Caroline seems all business even after brutalizing {other:name-possessive} genitals <i>\"Don't worry, I don't think the damage is permanent... I hope.\"</i>");
        selfNPC.addLine(CharacterLine.NAKED_LINER, (c, self, other) -> "Caroline doesn't even flinch after being stripped. <i>\"You know, you get used to these things after being friends with Angel this long.\"</i>");
        selfNPC.addLine(CharacterLine.STUNNED_LINER, (c, self, other) -> "Caroline staggers as she falls <i>\"You don't go easy do you...\"</i>");
        selfNPC.addLine(CharacterLine.TAUNT_LINER, (c, self, other) -> "<i>\"Come on, put in some more effort. You'll just be another notch on our bedpost at this rate.\"</i>");
        selfNPC.addLine(CharacterLine.TEMPT_LINER, (c, self, other) -> "Caroline turns around and spreads her lower lips with her fingers, <i>\"Mmm, I may not be as good as Angel, but I'm confident you wont last 10 seconds in me. Want to give it a go?\"</i>");
        selfNPC.addLine(CharacterLine.ORGASM_LINER, (c, self, other) -> "Caroline groans as she love juices drips endlessly between her legs <i>\"You're pretty good...\"</i>");
        selfNPC.addLine(CharacterLine.MAKE_ORGASM_LINER, (c, self, other) -> "<i>\"Come on, come on! Let's go for another round!\"</i>");
        selfNPC.addLine(CharacterLine.CHALLENGE, (c, self, other) -> {
            int carolineFought = other.getFlag(FOUGHT_CAROLINE_PET);
            if (other.human()) {
                if (carolineFought == 0)  {
                    other.setFlag(FOUGHT_CAROLINE_PET, 1);
                    return "You see runic circles twist and rotate in front of Angel, summoning a humanoid figure into the fight. "
                                    + "With a loud bang, you are thrown on your ass as the circles collapse inwards, wrapping themselves around the newly formed body. "
                                    + "Cautiously you pick yourself off the ground and check out the intruder. Oh shit. "
                                    + "You'd recognize that bob cut and competitive look anywhere. "
                                    + "It's Angel's friend Caroline!"
                                    + "<br/><br/>"
                                    + "Caroline looks around and spots you and Angel. <i>\"Hmmm I'm not entirely sure what's going on here, "
                                    + "but looks like some kind of sex fight? Sounds fun, I'm in!\"</i> You groan. Well she sure is adaptable...";
                } else if (self.has(Trait.kabbalah) && carolineFought == 1) {
                    other.setFlag(FOUGHT_CAROLINE_PET, 2);
                    return "Caroline emerges again from the runic circles you're used to seeing by now. However, she looks a bit different. "
                                    + "Angel must have shared some more of her divine power with her in thet summoning since Caroline now sports translucent "
                                    + "ethereal-looking wings between her shoulder blades and runic tattoos all over her body. "
                                    + "Moreover, she is holding a heavy tome in her hands that you've never seen before. "
                                    + "<br/><br/>"
                                    + "Caroline seems a bit surprised too. She inspects herself for a moment and tries tracing something in front of herself. "
                                    + "From her fingertips a glowing pattern emerges from thin air. Caroline smiles and says <i>\"This is way cool. "
                                    + "I wonder what else I can do?\"</i>";
                } else if (self.has(Trait.kabbalah)) {
                    return "{self:SUBJECT} emerges from the runic portal and unfurls her ethereal wings. "
                                    + "<i>\"Hmmm I can't seem to remember any of this during the day time, but I'm having so much fun I can't really complain. Ready {other:name}?\"</i>";
                } else {
                    return "{self:SUBJECT} opens her eyes and takes in the situation. Oooh a rematch? I'm game!</i>";
                }
            }
            return Formatter.format("{self:SUBJECT} quickly scans the situation and with an approving look from Angel, she gets ready to attack!</i>", self, other);
        });
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
        return "";
    }

    @Override
    public String intervene3p(Combat c, Character target, Character assist, NPC selfNPC) {
        return "";
    }

    @Override
    public boolean fit(NPC selfNPC) {
        return true;
    }

    @Override
    public boolean checkMood(Combat c, Emotion mood, int value, NPC selfNPC) {
        return value >= 100;
    }
}
