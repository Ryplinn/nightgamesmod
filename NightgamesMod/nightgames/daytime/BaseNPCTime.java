package nightgames.daytime;

import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.characters.trait.Trait;
import nightgames.global.Flag;
import nightgames.global.Formatter;
import nightgames.gui.GUI;
import nightgames.gui.GUIColor;
import nightgames.gui.LabeledValue;
import nightgames.items.Item;
import nightgames.items.Loot;
import nightgames.items.clothing.Clothing;
import nightgames.requirements.RequirementWithDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseNPCTime extends Activity {
    protected NPC npc;
    String knownFlag = "";
    String noRequestedItems = "{self:SUBJECT} frowns when {self:pronoun} sees that you don't have the requested items.";
    String notEnoughMoney = "{self:SUBJECT} frowns when {self:pronoun} sees that you don't have the money required.";
    String giftedString = "\"Awww thanks!\"";
    String giftString = "\"A present? You shouldn't have!\"";
    String transformationOptionString = "Transformations";
    String loveIntro = "[Placeholder]<br/>LoveIntro";
    String transformationIntro = "[Placeholder]<br/>TransformationIntro";
    String transformationFlag = "";
    Trait advTrait = null;

    BaseNPCTime(Player player, NPC npc) {
        super(npc.getTrueName(), player);
        this.npc = npc;
        buildTransformationPool();
    }

    @Override
    public boolean known() {
        return knownFlag.isEmpty() || Flag.checkFlag(knownFlag);
    }

    List<TransformationOption> options;

    public abstract void buildTransformationPool();

    private List<Loot> getGiftables() {
        List<Loot> giftables = new ArrayList<>();
        player.closet.stream().filter(article -> !npc.has(article)).forEach(giftables::add);
        return giftables;
    }

    public abstract void subVisit(String choice, List<LabeledValue<String>> nextChoices);

    public abstract void subVisitIntro(String choice, List<LabeledValue<String>> nextChoices);

    public Optional<String> getAddictionOption() {
        return Optional.empty();
    }
    
    private String formatRequirementString(String description, boolean meets) {
        if (meets) {
            return String.format("<font color=%s>%s</font>", GUIColor.EVENT_REQUIREMENT_MEETS.rgbHTML(), description);
        } else {
            return String.format("<font color=%s>%s</font>", GUIColor.EVENT_REQUIREMENT_NOTMEETS.rgbHTML(), description);
        }
    }

    @Override
    public void visit(String choice, int page, List<LabeledValue<String>> nextChoices, ActivityInstance instance)
                    throws InterruptedException {
        GUI.gui.clearText();
        GUI.gui.clearCommand();
        List<Loot> giftables = getGiftables();
        Optional<TransformationOption> transformationOption =
                        options.stream().filter(opt -> choice.equals(opt.option)).findFirst();
        Optional<Loot> giftOption = giftables.stream()
                        .filter(gift -> choice.equals(Formatter.capitalizeFirstLetter(gift.getName()))).findFirst();

        if (transformationOption.isPresent()) {
            TransformationOption option = transformationOption.get();
            boolean hasAll = option.ingredients.entrySet().stream()
                            .allMatch(entry -> player.has(entry.getKey(), entry.getValue()));
            int moneyCost = option.moneyCost.apply(this.player);
            if (!hasAll) {
                GUI.gui.message(Formatter.format(noRequestedItems, npc, player));
                choose("Back", nextChoices);
            } else if (player.money < moneyCost) {
                GUI.gui.message(Formatter.format(notEnoughMoney, npc, player));
                choose("Back", nextChoices);
            } else {
                GUI.gui.message(Formatter.format(option.scene, npc, player));
                option.ingredients.forEach((key, value) -> player.consume(key, value, false));
                option.effect.execute(null, player, npc);
                if (moneyCost > 0) {
                    player.modMoney(- moneyCost);
                }
                choose("Leave", nextChoices);
            }
        } else if (giftOption.isPresent()) {
            GUI.gui.message(Formatter.format(giftedString, npc, player));
            if (giftOption.get() instanceof Clothing) {
                Clothing clothingGift = (Clothing) giftOption.get();
                player.closet.remove(clothingGift);
                npc.closet.add(clothingGift);
            }
            player.gainAffection(npc, 2);
            npc.gainAffection(player, 2);
            choose("Back", nextChoices);
        } else if (choice.equals("Gift")) {
            GUI.gui.message(Formatter.format(giftString, npc, player));
            giftables.forEach(loot -> choose(Formatter.capitalizeFirstLetter(loot.getName()), nextChoices));
            choose("Back", nextChoices);
        } else if (choice.equals("Change Outfit")) {
            GUI.gui.changeClothes(npc);
        } else if (choice.equals(transformationOptionString)) {
            GUI.gui.message(Formatter.format(transformationIntro, npc, player));
            if (!transformationFlag.equals("")) {
                Flag.flag(transformationFlag);
            }
            options.forEach(opt -> {
                boolean allowed = true;
                GUI.gui.message(opt.option + ":");
                for (Map.Entry<Item, Integer> entry : opt.ingredients.entrySet()) {
                    String message = entry.getValue() + " " + entry.getKey().getName();
                    boolean meets = player.has(entry.getKey(), entry.getValue());
                    GUI.gui.message(formatRequirementString(message, meets));
                    allowed &= meets;
                }
                for (RequirementWithDescription req : opt.requirements) {
                    boolean meets = req.getRequirement().meets(null, player, npc);
                    GUI.gui.message(formatRequirementString(req.getDescription(), meets));
                    allowed &= meets;
                }
                int moneyCost = opt.moneyCost.apply(this.player);
                if (moneyCost > 0) {
                    boolean meets = player.money >= moneyCost;
                    GUI.gui.message(formatRequirementString(moneyCost + "$", meets));
                    allowed &= meets;
                }
                if (allowed) {
                    choose(opt.option, nextChoices);
                }
                GUI.gui.message("<br/>");
            });
            choose("Back", nextChoices);
        }
        // "Change Outfit" above blocks until the closet GUI closes, so we should be back to visit selection.
        if (choice.equals("Start") || choice.equals("Back") || choice.equals("Change Outfit")) {
            if (npc.getAffection(player) > 25 && (advTrait == null || npc.has(advTrait))) {
                GUI.gui.message(Formatter.format(loveIntro, npc, player));
                choose("Games", nextChoices);
                choose("Sparring", nextChoices);
                choose("Sex", nextChoices);
                if (!options.isEmpty()) {
                    choose(transformationOptionString, nextChoices);
                }
                if (npc.getAffection(player) > 30) {
                    choose("Gift", nextChoices);
                }
                if (npc.getAffection(player) > 35) {
                    choose("Change Outfit", nextChoices);
                }
                getAddictionOption().ifPresent(addictionString -> choose(addictionString, nextChoices));
                choose("Leave", nextChoices);
            } else {
                subVisitIntro(choice, nextChoices);
            }
        } else if (choice.equals("Leave")) {
            done(true, instance);
        } else {
            subVisit(choice, nextChoices);
        }
    }

    @Override
    public void shop(NPC paramCharacter, int paramInt) {
        paramCharacter.gainAffection(npc, 1);
        npc.gainAffection(paramCharacter, 1);

    }

}
