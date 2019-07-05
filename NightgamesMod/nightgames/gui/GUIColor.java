package nightgames.gui;

import nightgames.characters.Character;
import nightgames.pet.PetCharacter;

import java.awt.*;

public enum GUIColor {
    BG_DARK(new Color(0, 10, 30)),
    BG_LIGHT(new Color(18, 30, 49)),
    TEXT_LIGHT(new Color(240, 240, 255)),

    STAMINA_FOREGROUND(new Color(164, 8, 2)),
    AROUSAL_FOREGROUND(new Color(254, 1, 107)),
    MOJO_FOREGROUND(new Color(51, 153, 255)),
    WILLPOWER_FOREGROUND(new Color(68, 170, 85)),
    METER_BACKGROUND(new Color(50, 50, 50)),

    STAMINA_GAIN(new Color(100, 240, 30)),
    STAMINA_LOSS(new Color(250, 10, 10)),
    AROUSAL_GAIN(new Color(255, 50, 200)),
    AROUSAL_LOSS(new Color(80, 145, 200)),
    MOJO_GAIN(new Color(100, 200, 255)),
    MOJO_LOSS(new Color(150, 150, 250)),
    MOJO_SPEND(new Color(150, 150, 250)),
    WILLPOWER_GAIN(new Color(181, 230, 30)),
    WILLPOWER_LOSS(new Color(220, 130, 40)),
    TEMPT_BONUS(new Color(240, 60, 220)),
    TEMPT_MALUS(new Color(120, 180, 200)),
    AROUSAL_TEMPT(new Color(240, 100, 100)),
    AROUSAL_BONUS(new Color(255, 100, 50)),
    AROUSAL_MALUS(new Color(50, 100, 255)),
    AROUSAL_OVERFLOW(new Color(255, 50, 200)),

    PLAYER_COLOR(new Color(175, 175, 255)),
    NPC_COLOR(new Color(255, 175, 175)),
    PLAYER_PET_COLOR(new Color(130, 255, 200)),
    NPC_PET_COLOR(new Color(210, 130, 255)),

    HOTNESS_PERFECT(new Color(100, 255, 250)),
    HOTNESS_EXQUISITE(new Color(85, 185, 255)),
    HOTNESS_LOVELY(new Color(210, 130, 250)),
    HOTNESS_ATTRACTIVE(new Color(250, 130, 220)),
    HOTNESS_SOSO(new Color(255, 130, 150)),
    HOTNESS_NOT(new Color(255, 105, 105)),

    EVENT_REQUIREMENT_MEETS(new Color(90, 210, 100)),
    EVENT_REQUIREMENT_NOTMEETS(new Color(210, 90, 90)),

    COMBAT_OBSERVE_P1_DESC(new Color(255, 220, 220)),
    COMBAT_OBSERVE_P2_DESC(new Color(220, 220, 255)),
    COMBAT_OBSERVE_STANCE_DESC(new Color(134, 196, 49)),

    CLOCK_NIGHT(new Color(51, 101, 202)),
    CLOCK_DAY(new Color(253, 184, 19)),

    STATS_ATTRIBUTE_FULL(new Color(100, 255, 255)),
    STATS_ATTRIBUTE_DRAINED(new Color(255, 100, 100)),
    STATS_BONUS(new Color(0, 255, 0)),
    STATS_MALUS(new Color(255, 0, 0)),

    TACTICS_DAMAGE(new Color(150, 0, 0)),
    TACTICS_PLEASURE(Color.PINK),
    TACTICS_FUCK(new Color(255, 100, 200)),
    TACTICS_POSITION(new Color(0, 100, 0)),
    TACTICS_STRIP(new Color(0, 100, 0)),
    TACTICS_RECOVER(Color.WHITE),
    TACTICS_CALM(Color.WHITE),
    TACTICS_DEBUFF(Color.CYAN),
    TACTICS_SUMMON(Color.YELLOW),
    TACTICS_MISC(new Color(200, 200, 200)),
    ;

    public Color color;

    GUIColor(Color color) {
        this.color = color;
    }

    public static GUIColor characterColor(Character character) {
        if (character.isPet()) {
            if (((PetCharacter)character).getSelf().owner().human()) {
                return GUIColor.PLAYER_PET_COLOR;
            } else {
                return GUIColor.NPC_PET_COLOR;
            }
        } else if (character.human()) {
            return GUIColor.PLAYER_COLOR;
        } else {
            return GUIColor.NPC_COLOR;
        }
    }

    static String rgbHTML(Color color) {
        return String.format("'rgb(%d,%d,%d)'", color.getRed(), color.getGreen(), color.getBlue());
    }

    public String rgbHTML() {
        return rgbHTML(color);
    }
}
