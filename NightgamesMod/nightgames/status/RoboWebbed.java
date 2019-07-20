package nightgames.status;

import com.google.gson.JsonObject;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.NPC;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.gui.GUI;
import nightgames.trap.Trap;

public class RoboWebbed extends Bound {
    public RoboWebbed(CharacterType affected, double dc, Trap roboWeb) {
        super("RoboWebbed", affected, dc, "robo-web", roboWeb);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return "";
    }

    @Override
    public String describe(Combat c) {
        return Formatter.format("{self:SUBJECT-ACTION:are|is} hopelessly tangled up in"
                        + " synthetic webbing, which is sending pleasurable sensations"
                        + " through {self:possessive} entire body.", getAffected(), NPC.noneCharacter());
    }

    @Override
    public void tick(Combat c) {
        int dmg = (int) (getAffected().getArousal().max() * .25);
        // Message handled in describe
        if (c == null && trap != null) {
            if (getAffected().human()) {
                GUI.gui.message(Formatter.format("{self:SUBJECT-ACTION:are|is} hopelessly tangled up in"
                                + " synthetic webbing, which is sending pleasurable sensations"
                                + " through {self:possessive} entire body.", getAffected(), NPC.noneCharacter()));
            }
            getAffected().tempt(dmg);
            getAffected().location().opportunity(getAffected(), trap);
        } else {
            getAffected().temptNoSkillNoTempter(c, dmg);
        }
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new RoboWebbed(newAffected.getType(), toughness, trap);
    }

    @Override
    public JsonObject saveToJson() {
        return null;
    }

    @Override
    public Status loadFromJson(JsonObject obj) {
        return null;
    }
}
