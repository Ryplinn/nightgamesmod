package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.global.GameState;
import nightgames.global.Random;
import nightgames.gui.GUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

abstract class DaytimeEvent {

    private List<EventVariation> scenes;
    
    protected DaytimeEvent() {
        scenes = new ArrayList<>();
    }
    
    protected void register(String name, int priority) {
        scenes.add(new EventVariation(name, priority));
    }

    // Mandatory and morning scenes do not trigger if an event listed higher in 
    // DaytimeEventManager.EVENT_TYPES already did.
    
    public boolean playMorning() {
        return playIfPresent(getMorningReason());
    }
    
    public boolean playMandatory() {
        return playIfPresent(getMandatoryReason());
    }
    
    private boolean playIfPresent(Optional<String> reason) {
        if (reason.isPresent()) {
            try {
                runScene(reason.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }
    
    public boolean playAny() {
        for (EventVariation scene : scenes) {
            if (Random.random(100) < scene.priority) {
                try {
                    runScene(scene.getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private void runScene(String scene) throws InterruptedException {
        play(scene);
        GUI.gui.next("Next").await();
    }
    
    abstract boolean available();
    
    protected abstract Optional<String> getMandatoryReason();
    protected abstract Optional<String> getMorningReason();
    protected abstract void play(String response);

    protected Character getPlayer() {
        return GameState.getGameState().characterPool.getPlayer();
    }


    protected class EventVariation {
        private String name;
        private int priority;
        protected EventVariation(String name, int priority) {
            this.name = name;
            this.priority = priority;
        }
    
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public int getPriority() {
            return priority;
        }
        
        public void setPriority(int priority) {
            this.priority = priority;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + priority;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EventVariation other = (EventVariation) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (priority != other.priority)
                return false;
            return true;
        }

        private DaytimeEvent getOuterType() {
            return DaytimeEvent.this;
        }

        @Override
        public String toString() {
            return "EventVariation [name=" + name + ", priority=" + priority + "]";
        }
        
        
    }
}
