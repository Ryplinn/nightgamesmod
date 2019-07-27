package nightgames.gui;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.skills.Skill;
import nightgames.skills.Stage;

import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SkillButton extends ValueButton<Skill> {
    private static final long serialVersionUID = -1253735466299929203L;
    protected Skill skill;
    private List<String> subChoices;
    protected Combat combat;

    SkillButton(Combat combat, final Skill skill, Character user, Character target,
                    CompletableFuture<Skill> chosenSkill) {
        super(skill, skill.getLabel(combat, user), chosenSkill);
        getButton().setBorderPainted(false);
        getButton().setOpaque(true);
        getButton().setFont(fontForStage(skill.getStage()));
        this.skill = skill;
        this.subChoices = new ArrayList<>(skill.subChoices(combat, user));
        int actualAccuracy = target.getChanceToHit(user, skill.baseAccuracy(combat, user, target));
        int clampedAccuracy = Math.min(100, Math.max(0, actualAccuracy));
        String text = "<html>" + skill.describe(combat, user) + " <br/><br/>Accuracy: " + (actualAccuracy >= 150 ?
                        "---" :
                        clampedAccuracy + "%") + "</p>";
        Color bgColor = skill.type(combat, user).getColor();
        getButton().setBackground(bgColor);
        getButton().setForeground(foregroundColor(bgColor));

        if (skill.getMojoCost(combat, user) > 0) {
            setBorder(new LineBorder(Color.RED, 3));
            text += "<br/>Mojo cost: " + skill.getMojoCost(combat, user);
        } else if (skill.getMojoBuilt(combat, user) > 0) {
            setBorder(new LineBorder(new Color(53, 201, 255), 3));
            text += "<br/>Mojo generated: " + skill.getMojoBuilt(combat, user) + "%";
        } else {
            setBorder(new LineBorder(getButton().getBackground(), 3));
        }
        if (!user.cooldownAvailable(skill)) {
            getButton().setEnabled(false);
            text += String.format("<br/>Remaining Cooldown: %d turns", user.getCooldown(skill));
            getButton().setForeground(Color.WHITE);
            getButton().setBackground(getBackground().darker());
        }

        text += "</html>";
        setToolTipText(text);
        this.combat = combat;
        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(500, 20));
        add(getButton());
    }

    private static Color foregroundColor(Color bgColor) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getRed(), hsb);
        if (hsb[2] < .6) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    private static Font fontForStage(Stage stage) {
        switch (stage) {
            case FINISHER:
                return new Font("Baskerville Old Face", Font.BOLD, 18);
            case FOREPLAY:
                return new Font("Baskerville Old Face", Font.ITALIC, 18);
            default:
                return new Font("Baskerville Old Face", Font.PLAIN, 18);

        }
    }

    @Override protected void run() {
        if (subChoices.size() != 0) {
            GUI.gui.commandPanel.reset();
            for (String choice : subChoices) {
                GUI.gui.commandPanel.add(new SubSkillButton(skill, choice, this.future));
            }
            GUI.gui.commandPanel.refresh();
        } else {
            super.run();
        }
    }
}
