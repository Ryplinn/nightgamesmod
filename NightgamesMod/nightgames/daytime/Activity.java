package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.global.Global;

public abstract class Activity {
    protected String name;
    protected int time;
    protected Character player;
    protected int page;

    public Activity(String name, Character player) {
        this.name = name;
        time = 1;
        this.player = player;
        page = 0;
    }

    public abstract boolean known();

    public abstract void visit(String choice);

    public int time() {
        return time;
    }

    public void next() {
        page++;
    }

    public void done(boolean acted) {
        if (acted) {
            Global.global.getDay().advance(time);
        }
        page = 0;
        Global.global.gui().clearImage();
        Global.global.gui().clearPortrait();
        Global.global.getDay().plan();
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract void shop(Character npc, int budget);
}
