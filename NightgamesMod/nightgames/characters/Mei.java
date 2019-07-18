package nightgames.characters;

import nightgames.actions.Action;
import nightgames.actions.Movement;
import nightgames.characters.body.*;
import nightgames.characters.body.mods.DemonicMod;
import nightgames.characters.body.mods.ExtendedTonguedMod;
import nightgames.characters.custom.CharacterLine;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.items.Item;

import java.util.Collection;
import java.util.Optional;

public class Mei extends BasePersonality {
    private static final long serialVersionUID = 8601852023164119671L;

    public Mei() {
        super(false);
    }

    @Override
    public void applyStrategy(NPC selfNPC) {}

    @Override
    public void applyBasicStats(NPC selfNPC) {
        preferredCockMod = CockMod.error;
        selfNPC.outfitPlan.addByID("negligee");
        selfNPC.outfitPlan.addByID("lacythong");
        selfNPC.outfitPlan.addByID("garters");

        selfNPC.change();
        selfNPC.modAttributeDontSaveData(Attribute.power, 1);
        selfNPC.modAttributeDontSaveData(Attribute.seduction, 1);
        selfNPC.modAttributeDontSaveData(Attribute.cunning, 1);
        selfNPC.modAttributeDontSaveData(Attribute.perception, 1);
        selfNPC.modAttributeDontSaveData(Attribute.speed, 1);
        selfNPC.getStamina().setMax(100);
        selfNPC.getArousal().setMax(150);
        selfNPC.rank = 1;
        selfNPC.adjustTraits();

        selfNPC.getMojo().setMax(110);

        selfNPC.setTrophy(Item.ExtremeAphrodisiac);
        selfNPC.body.add(BreastsPart.b);
        selfNPC.initialGender = CharacterSex.female;
    }

    @Override
    public void setGrowth(NPC selfNPC) {
        selfNPC.getGrowth().stamina = 3;
        selfNPC.getGrowth().arousal = 8;
        selfNPC.getGrowth().willpower = .8f;
        selfNPC.getGrowth().bonusStamina = 2;
        selfNPC.getGrowth().bonusArousal = 2;

        selfNPC.getGrowth().addTrait(0, Trait.hairtrigger);
        selfNPC.getGrowth().addTrait(0, Trait.petite);
        selfNPC.getGrowth().addTrait(10, Trait.cute);
        selfNPC.getGrowth().addTrait(15, Trait.lacedjuices);
        selfNPC.getGrowth().addTrait(20, Trait.tight);
        selfNPC.getGrowth().addTrait(25, Trait.sexTraining1);
        selfNPC.getGrowth().addBodyPartMod(30, "pussy", DemonicMod.INSTANCE);
        selfNPC.getGrowth().addBodyPart(30, TailPart.demonic);
        selfNPC.getGrowth().addBodyPart(30, WingsPart.fallenangel);
        selfNPC.getGrowth().addTrait(30, Trait.fallenAngel);
        selfNPC.getGrowth().addTrait(35, Trait.energydrain);
        selfNPC.getGrowth().addTrait(40, Trait.soulsucker);
        selfNPC.getGrowth().addTrait(45, Trait.gluttony);
        selfNPC.getGrowth().addBodyPartMod(50, "pussy", ExtendedTonguedMod.INSTANCE);
        selfNPC.getGrowth().addTrait(55, Trait.carnalvirtuoso);
        preferredAttributes.add(c -> Optional.of(Attribute.seduction));

        preferredAttributes.add(c -> c.getLevel() >= 30 ? Optional.of(Attribute.darkness) : Optional.empty());
        // mostly feminine face, cute but not quite at Angel's level
        selfNPC.body.add(new FacePart(.1, 2.9));
    }

    @Override
    public Action move(Collection<Action> available, Collection<Movement> radar, NPC selfNPC) {
        Action proposed = Decider.parseMoves(available, radar, selfNPC);
        return proposed;
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
        selfNPC.addLine(CharacterLine.BB_LINER, (c, self, other) -> {
            return "<i>They taught that one in self-defense class!</i>";
       });

        selfNPC.addLine(CharacterLine.NAKED_LINER, (c, self, other) -> {
            return "While covering herself with her arms, Mei fake-screams <i>What do you think you're doing!?</i> Her lewd smile however speaks volumes about her true thoughts.";
       });

        selfNPC.addLine(CharacterLine.STUNNED_LINER, (c, self, other) -> {
            return "<i>Angel... Sorry...</i>";
       });

        selfNPC.addLine(CharacterLine.TAUNT_LINER, (c, self, other) -> {
            return "<i>\"That's right, you're just a little " + other.boyOrGirl() + "toy for us. So why don't you just stay still?\"</i>";
       });

        selfNPC.addLine(CharacterLine.TEMPT_LINER, (c, self, other) -> {
            return "Mei runs her hands all over her body while teasing you, <i>\"Mmmm you want some of this? Just ask and we'll do as you please.\"</i>";
       });

        selfNPC.addLine(CharacterLine.ORGASM_LINER, (c, self, other) -> {
            return "Mei yelps as she cums <i>\"Oh fuuuuckk!\"</i>";
       });

        selfNPC.addLine(CharacterLine.MAKE_ORGASM_LINER, (c, self, other) -> {
            return "<i>Try a little harder wont you? At this rate there's no way you'll be suitable for her!</i>";
       });
        
       selfNPC.addLine(CharacterLine.CHALLENGE, (c, self, other) -> {
           int meiFought = other.getFlag(FOUGHT_MEI_PET);
           if (other.human()) {
               if (meiFought == 0)  {
                   other.setFlag(FOUGHT_MEI_PET, 1);
                   return "Standing up in the fading light, {self:SUBJECT} looks around bewilderedly before catching sight of you and Angel. "
                                   + "{self:SUBJECT} waves happily at {other:name-do} <i>\"Hiya {other:name}, fancy meeting you here! Huh are we on campus? "
                                   + "I seem to be half naked and Angel has wings? Wait what's going on?\"</i> "
                                   + "<br/><br/>"
                                   + "When neither you nor Angel deigned to respond, she just shrugs <i>\"Ahhhh, I get it! This must be one of those sexy dreams right? "
                                   + "I wonder if I'm just pent up... ah well, no point in minding the details.\"<i/> Mei cracks her fingers. <i>\"Since we're doing this, I'm going all out!\"</i>"
                                   + "<br/><br/>"
                                   + "Errr... while you're glad she's so adaptable, it looks like the fight's become a two on one!";
               } else if (self.has(Trait.fallenAngel) && meiFought == 1) {
                   other.setFlag(FOUGHT_MEI_PET, 2);
                   return "After {self:SUBJECT} materializes as usual, she notices that her body has changed. "
                                   + "Pitch black feathered wings grow out of her shoulder blades, and a thick demonic tail sprouts from her bum. "
                                   + "With her eyes wide, Mei exclaims <i>\"Oh wow, what am I supposed to be now? Some kind of fallen angel? "
                                   + "I knew I've been reading too much fantasy smut before bed...\"</i>";
               } else if (self.has(Trait.fallenAngel)) {
                   return "{self:SUBJECT} opens her eyes and stretches her black wings. "
                                   + "<i>This dream again? Well, it got pretty hot last time, so no complaints from me! So cum again for me will you? "
                                   + "My little demon pussy seems pretty hungry!</i>";
               } else {
                   return "{self:SUBJECT} opens her eyes and takes in the situation. "
                                   + "<i>This dream again? Well, it got pretty hot last time, so no complaints from me!</i>";
               }
           }
           return "{self:SUBJECT} curiously scans the situation and with an approving look from Angel, she gets ready to attack!</i>";
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

    private static String FOUGHT_MEI_PET = "FOUGHT_MEI_PET";

    @Override
    public boolean fit(NPC selfNPC) {
        return true;
    }

    @Override
    public boolean checkMood(Combat c, Emotion mood, int value, NPC selfNPC) {
        switch (mood) {
            case horny:
                return value >= 50;
            default:
                return value >= 100;
        }
    }
}
