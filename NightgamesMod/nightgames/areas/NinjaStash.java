package nightgames.areas;

import java.util.ArrayList;

import nightgames.characters.Character;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.items.Item;

public class NinjaStash implements Deployable {

    private Character owner;
    private ArrayList<Item> contents;
    
    public NinjaStash(Character owner){
        this.owner = owner;
        contents = new ArrayList<Item>();
        for(int i=0; i<4; i++){
            switch(Random.random(3)){
            case 0:
                contents.add(Item.Needle);
                contents.add(Item.Needle);
                break;
            case 1: 
                contents.add(Item.SmokeBomb);
                break;
            }
        }
    }
    @Override
    public boolean resolve(Character active) {
        if(owner==active&&active.human()){
            GUI.gui.message("You have a carefully hidden stash of emergency supplies here. You can replace your clothes and collect the items if you need to.");
        }
        return false;
    }

    public void collect(Character active){
        for(Item i: contents){
            active.gain(i);
        }
        contents.clear();
    }
    
    public String toString(){
        return "Ninja Stash";
    }

    @Override
    public Character getOwner() {
        return owner;
    }
    
    @Override
    public int priority() {
        return 0;
    }

}
