package nightgames.skills;

import nightgames.gui.GUIColor;

import java.awt.*;

public enum Tactics {
    damage(TacticGroup.hurt, GUIColor.TACTICS_DAMAGE),
    pleasure(TacticGroup.pleasure, GUIColor.TACTICS_PLEASURE),
    fucking(TacticGroup.pleasure, GUIColor.TACTICS_FUCK),
    positioning(TacticGroup.positioning, GUIColor.TACTICS_POSITION),
    stripping(TacticGroup.positioning, GUIColor.TACTICS_STRIP),
    recovery(TacticGroup.recovery, GUIColor.TACTICS_RECOVER),
    calming(TacticGroup.recovery, GUIColor.TACTICS_CALM),
    debuff(TacticGroup.manipulation, GUIColor.TACTICS_DEBUFF),
    summoning(TacticGroup.manipulation, GUIColor.TACTICS_SUMMON),
    misc(TacticGroup.misc, GUIColor.TACTICS_MISC),
    ;

    private final GUIColor color;
    private final TacticGroup group;
    Tactics(TacticGroup group, GUIColor color) {
        this.color = color;
        this.group = group;
    }

    public Color getColor() {
        return color.color;
    }

    public TacticGroup getGroup() {
        return group;
    }
}
