package nightgames.areas;

import nightgames.characters.Character;

public interface Deployable {
    public void resolve(Character active);

    public Character owner();
    
    default int priority() {return 5;}
}
