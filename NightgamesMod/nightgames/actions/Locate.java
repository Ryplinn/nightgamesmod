package nightgames.actions;

import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.global.Match;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;
import nightgames.items.Item;
import nightgames.status.Detected;
import nightgames.status.Horny;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Locate extends Action {
    private static final long serialVersionUID = 1L;
    private static final int MINIMUM_SCRYING_REQUIREMENT = 5;

    public Locate() {
        super("Locate");
    }

    @Override
    public boolean usable(Character self) {
        boolean hasUnderwear = false;
        for (Item i : self.getInventory().keySet()) {
            // i hate myself for having to add this null check... why is inventory even public...
            if (i != null && i.toString().contains("Trophy")) {
                hasUnderwear = true;
            }
        }
        return self.has(Trait.locator) && hasUnderwear && !self.bound();
    }

    @Override
    public Movement execute(Character self) {
        if (!self.human()) {
            return Movement.locating;
        }
        GUI gui = GUI.gui;
        gui.clearCommand();
        gui.clearText();
        gui.validate();
        gui.message("Thinking back to your 'games' with Reyka, you take out a totem to begin a scrying ritual: ");
        List<LabeledValue<Character>> choices = Match.getMatch().combatants.stream()
                        .filter(c -> self.getAffection(c) >= MINIMUM_SCRYING_REQUIREMENT).map(character -> new LabeledValue<>(
                                        character, character.getTrueName())).collect(Collectors.toList());
        CompletableFuture<Character> choice =  gui.promptFuture(choices);
        gui.cancel("Leave", choice);
        try {
            Character target = choice.get();
            Area area = target.location();
            gui.clearText();
            if (area != null) {
                gui.message("Drawing on the dark energies inside the talisman, you attempt to scry for " + target
                                .nameOrPossessivePronoun() + " location. In your mind, an image of the <b><i>"
                                + area.name
                                + "</i></b> appears. It falls apart as quickly as it came to be, but you know where "
                                + target.getTrueName()
                                + " currently is. Your small talisman is already burning up in those creepy "
                                + "purple flames, the smoke flowing from your nose straight to your crotch and setting another fire there.");
                target.addNonCombat(new Detected(target.getType(), 10));
            } else {
                gui.message("Drawing on the dark energies inside the talisman, you attempt to scry for " + target
                                .nameOrPossessivePronoun() + " location. "
                                + "However, you draw a blank. Your small talisman is already burning up in those creepy "
                                + "purple flames, the smoke flowing from your nose straight to your crotch and setting another fire there.");
            }
            self.addNonCombat(new Horny(self.getType(), self.getArousal().max() / 10, 10, "Scrying Ritual"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (CancellationException e) {
            // continue
        }
        gui.clearText();
        gui.clearCommand();
        return Movement.locating;
    }

    @Override
    public Movement consider() {
        return Movement.locating;
    }

    @Override
    public boolean freeAction() {
        return true;
    }

}
