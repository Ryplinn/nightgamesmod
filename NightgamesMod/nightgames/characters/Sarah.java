package nightgames.characters;

import nightgames.actions.Action;
import nightgames.actions.Movement;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.FacePart;
import nightgames.characters.body.WingsPart;
import nightgames.characters.body.mods.FieryMod;
import nightgames.characters.custom.CharacterLine;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.items.Item;

import java.util.Collection;
import java.util.Optional;

public class Sarah extends BasePersonality {
    private static final long serialVersionUID = 8601852023164119671L;

    public Sarah() {
        super(false);
    }

    @Override
    public void applyStrategy(NPC selfNPC) {}

    @Override
    public void applyBasicStats(NPC selfNPC) {
        preferredCockMod = CockMod.error;
        selfNPC.outfitPlan.addByID("frillybra");
        selfNPC.outfitPlan.addByID("frillypanties");

        selfNPC.change();
        selfNPC.modAttributeDontSaveData(Attribute.power, 2);
        selfNPC.modAttributeDontSaveData(Attribute.cunning, 1);
        selfNPC.modAttributeDontSaveData(Attribute.perception, 1);
        selfNPC.modAttributeDontSaveData(Attribute.speed, 2);
        selfNPC.getStamina().setMax(150);
        selfNPC.getArousal().setMax(100);
        selfNPC.rank = 1;
        selfNPC.adjustTraits();

        selfNPC.getMojo().setMax(90);

        selfNPC.setTrophy(Item.HolyWater);
        selfNPC.body.add(BreastsPart.d);
        selfNPC.initialGender = CharacterSex.female;
    }

    @Override
    public void setGrowth(NPC selfNPC) {
        selfNPC.getGrowth().stamina = 5;
        selfNPC.getGrowth().arousal = 6;
        selfNPC.getGrowth().willpower = .8f;
        selfNPC.getGrowth().bonusStamina = 2;
        selfNPC.getGrowth().bonusArousal = 2;

        selfNPC.getGrowth().addTrait(0, Trait.imagination);
        selfNPC.getGrowth().addTrait(0, Trait.pimphand);
        selfNPC.getGrowth().addTrait(10, Trait.QuickRecovery);
        selfNPC.getGrowth().addTrait(15, Trait.sadist);
        selfNPC.getGrowth().addTrait(20, Trait.disablingblows);
        selfNPC.getGrowth().addTrait(25, Trait.nimbletoes);
        selfNPC.getGrowth().addBodyPartMod(30, "pussy", FieryMod.INSTANCE);
        selfNPC.getGrowth().addBodyPart(30, WingsPart.angelic);
        selfNPC.getGrowth().addTrait(30, Trait.valkyrie);
        selfNPC.getGrowth().addTrait(35, Trait.overwhelmingPresence);
        selfNPC.getGrowth().addTrait(40, Trait.bitingwords);
        selfNPC.getGrowth().addTrait(45, Trait.commandingvoice);
        selfNPC.getGrowth().addTrait(50, Trait.oblivious);
        selfNPC.getGrowth().addTrait(55, Trait.resurrection);

        preferredAttributes.add(c -> Optional.of(Attribute.power));
        preferredAttributes.add(c -> c.getLevel() >= 30 ? Optional.of(Attribute.ki) : Optional.empty());
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
    
    @Override public void constructLines(NPC selfNPC) {
        selfNPC.addLine(CharacterLine.BB_LINER, (c, self, other) -> "<i>\"...\"</i> Sarah silently looks at you, with no hint of remorse in her eyes.");

        selfNPC.addLine(CharacterLine.NAKED_LINER, (c, self, other) -> "Sarah looks unfazed at being undressed, but you can clearly see a flush creeping into her face.");

        selfNPC.addLine(CharacterLine.STUNNED_LINER, (c, self, other) -> "<i>\"..!\"</i>");

        selfNPC.addLine(CharacterLine.TAUNT_LINER, (c, self, other) -> "Sarah simply eyes you with a disdainful look. If looks could kill... well this still probably wouldn't kill you. But it definitely hurts your pride.");

        selfNPC.addLine(CharacterLine.TEMPT_LINER, (c, self, other) -> "Sarah cups her large breasts and gives you a show. The gap between her placid face and her lewd actions is surprisingly arousing.");

        selfNPC.addLine(CharacterLine.NIGHT_LINER, (c, self, other) -> "");

        selfNPC.addLine(CharacterLine.ORGASM_LINER, (c, self, other) ->
                        "Sarah's eyes slam shut in a blissful silent orgasm. "
                                        + "You can clearly tell she's turned on like hell, but her face remains impassive as usual.");

        selfNPC.addLine(CharacterLine.MAKE_ORGASM_LINER, (c, self, other) -> "Sarah looks a bit flushed as {other:subject-action:cum|cums} hard. However she does changes neither her blank demeanor nor her stance.");
        
        selfNPC.addLine(CharacterLine.CHALLENGE, (c, self, other) -> {
            int sarahFought = other.getFlag(FOUGHT_SARAH_PET);
            if (other.human()) {
                if (sarahFought == 0)  {
                    other.setFlag(FOUGHT_SARAH_PET, 1);
                    return "The summoned figure stands up while wobbling on her feet. When she lifts her head, you see that it's Angel's friend Sarah! "
                                    + "<br/>On closer inspection though, you see that the easily-embarassed glasses girl seems to have a completely different air about her. "
                                    + "To tell the truth, She looks rather glassy eyed and unstable. "
                                    + "<br/><br/>You look questioningly at Angel and she sighs "
                                    + "<i>\"Sarah is actually a bit too shy to bring into the games, even unconsciously. "
                                    + "Instead of having her freak out, I thought I'd just have her mind come for the ride. Don't worry, I guarantee that she'll enjoy it.\"</i>";
                } else if (self.has(Trait.valkyrie) && sarahFought == 1) {
                    other.setFlag(FOUGHT_SARAH_PET, 2);
                    return "After {self:SUBJECT} materializes as usual from a brillant burst of light, you see that she looks different. "
                                    + "Sarah was always rather tall, but now she looks positively Amazonian. "
                                    + "Subtle but powerful muscles are barely visible under her fleshy body, making her presence larger than ever. "
                                    + "To top it off, a pair of large angelic wings crown her upper back, completing her look as a valkyrie in service to her Goddess. "
                                    + "<br/><br/>"
                                    + "Angel smiles mischievously at you <i>\"Isn't she beautiful? I love Sarah the way she is, but in a fight, a Goddess does need her guardians.\"</i>";
                } else if (self.has(Trait.valkyrie)) {
                    return "{self:SUBJECT} emerges from the pillar of light and stands at attention. "
                                    + "Angel walks over to {self:direct-object} and kisses her on the cheek. <i>\"Sarah dear, let's teach {other:direct-object} the proper way to worship a Goddess.\"<i>";
                } else {
                    return "{self:SUBJECT} opens her glassy eyes and stands silently while Angel coos, <i>\"Mmmm we're going to show you a good time.\"</i>";
                }
            }
            return "{self:SUBJECT} impassively scans the situation and with an approving look from Angel, she gets ready to attack.</i>";
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

    private static String FOUGHT_SARAH_PET = "FOUGHT_SARAH_PET";

    @Override
    public boolean fit(NPC selfNPC) {
        return true;
    }

    @Override
    public boolean checkMood(Combat c, Emotion mood, int value, NPC selfNPC) {
        if (mood == Emotion.angry) {
            return value >= 80;
        }
        return value >= 100;
    }

}
