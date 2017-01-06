package nightgames.gui;

import nightgames.characters.Character;
import nightgames.combat.IEncounter;
import nightgames.global.Encs;
import nightgames.global.Global;
import nightgames.trap.Trap;

import javax.swing.*;
import java.awt.*;

public class EncounterButton extends JButton {
    /**
     * 
     */
    private static final long serialVersionUID = 6203417731879208074L;
    private IEncounter enc;
    private Character target;
    private Encs choice;
    private Trap trap;

    public EncounterButton(String label, IEncounter enc, Character target, Encs choice) {
        super(label);
        setFont(new Font("Baskerville Old Face", 0, 18));
        this.enc = enc;
        this.target = target;
        this.choice = choice;
        addActionListener(arg0 -> {
            EncounterButton.this.enc.parse(EncounterButton.this.choice, Global.global.getPlayer(),
                            EncounterButton.this.target);
            Global.global.getMatch().resume();
        });
    }

    public EncounterButton(String label, IEncounter enc2, Character target, Encs choice, Trap trap) {
        super(label);
        setFont(new Font("Baskerville Old Face", 0, 18));
        this.enc = enc2;
        this.target = target;
        this.choice = choice;
        this.trap = trap;
        addActionListener(arg0 -> {
            EncounterButton.this.enc
                            .parse(EncounterButton.this.choice, Global.global.getPlayer(), EncounterButton.this.target,
                            EncounterButton.this.trap);
            Global.global.getMatch().resume();
        });
    }
}
