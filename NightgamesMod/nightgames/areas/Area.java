package nightgames.areas;

import nightgames.actions.Movement;
import nightgames.characters.Character;
import nightgames.characters.CharacterPool.CharacterNotFoundException;
import nightgames.characters.CharacterType;
import nightgames.combat.Encounter;
import nightgames.global.Match;
import nightgames.status.Stsflag;
import nightgames.trap.Trap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Area implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1372128249588089014L;
    public String name;
    public HashSet<Area> adjacent;
    public HashSet<Area> shortcut;
    public HashSet<Area> jump;
    public List<CharacterType> present;
    public String description;
    public Encounter activeEncounter;
    public boolean alarm;
    public List<Deployable> env;
    public transient MapDrawHint drawHint;
    private Movement enumerator;
    private boolean pinged;

    public Area(String name, String description, Movement enumerator) {
        this(name, description, enumerator, new MapDrawHint());
    }

    public Area(String name, String description, Movement enumerator, MapDrawHint drawHint) {
        this.name = name;
        this.description = description;
        this.enumerator = enumerator;
        adjacent = new HashSet<>();
        shortcut = new HashSet<>();
        jump = new HashSet<>();
        present = new ArrayList<>();
        env = new ArrayList<>();
        alarm = false;
        activeEncounter = null;
        this.drawHint = drawHint;
    }

    public void link(Area adj) {
        adjacent.add(adj);
    }

    void shortcut(Area sc) {
        shortcut.add(sc);
    }
    
    public void jump(Area adj){
        jump.add(adj);
    }

    public boolean open() {
        return enumerator == Movement.quad || enumerator == Movement.ftcCenter;
    }

    public boolean corridor() {
        return enumerator == Movement.bridge || enumerator == Movement.tunnel || enumerator == Movement.ftcTrail
                        || enumerator == Movement.ftcPass || enumerator == Movement.ftcPath;
    }

    public boolean materials() {
        return enumerator == Movement.workshop || enumerator == Movement.storage || enumerator == Movement.ftcCabin
                        || enumerator == Movement.ftcDump;
    }

    public boolean potions() {
        return enumerator == Movement.lab || enumerator == Movement.kitchen || enumerator == Movement.ftcLodge;
    }

    public boolean bath() {
        return enumerator == Movement.shower || enumerator == Movement.pool || enumerator == Movement.ftcPond
                        || enumerator == Movement.ftcWaterfall;
    }

    public boolean resupply() {
        return enumerator == Movement.dorm || enumerator == Movement.union;
    }

    public boolean recharge() {
        return enumerator == Movement.workshop || enumerator == Movement.ftcCabin;
    }

    public boolean mana() {
        return enumerator == Movement.la || enumerator == Movement.ftcOak;
    }

    public List<Character> getPresent() {
        return present.stream().map(CharacterType::fromPool).filter(Optional::isPresent).map(Optional::get)
                        .collect(Collectors.toList());
    }

    public boolean ping(int perception) {
        if (activeEncounter != null) {
            return true;
        }
        for (Character c : getPresent()) {
            if (!c.stealthCheck(perception) || open()) {
                return true;
            }
        }
        return alarm;
    }

    public void enter(Character p) {
        present.add(p.getType());
        System.out.printf("%s enters %s: %s\n", p.getTrueName(), name, env);
        List<Deployable> deps = new ArrayList<>(env);
        for (Deployable dep : deps) {
            if (dep != null && dep.resolve(p)) {
                return;
            }
        }
    }

    public boolean hasEncounter() {
        return activeEncounter != null;
    }

    public Optional<Encounter> encounter() throws InterruptedException {
        if (!hasEncounter() && present.size() > 1) {
            activeEncounter = Match.getMatch().buildEncounter(this);
        } else if (present.size() > 2) {
            Character intruder =
                            present.get(2).fromPoolGuaranteed();
            if (activeEncounter.checkIntrudePossible(intruder)) {
                intruder.decideIntervene(activeEncounter, activeEncounter.getP1(), activeEncounter.getP2());
            }
        }
        return Optional.ofNullable(activeEncounter);
    }

    public boolean opportunity(Character target, Trap trap) {
        if (present.size() > 1) {
            for (Character opponent : getPresent()) {
                if (opponent != target) {
                    if (target.eligible(opponent) && opponent.eligible(target) && activeEncounter == null) {
                        activeEncounter = Match.getMatch().buildEncounter(this);
                        opponent.promptTrap(activeEncounter, target, trap);
                        return true;
                    }
                }
            }
        }
        remove(trap);
        return false;
    }

    public boolean humanPresent() {
        for (Character player : getPresent()) {
            if (player.human()) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return present.isEmpty();
    }

    public void exit(Character p) {
        present.remove(p.getType());
    }

    public void endEncounter() {
        if (activeEncounter != null) {
            activeEncounter.finish();
            activeEncounter = null;
        }
    }

    public Movement id() {
        return enumerator;
    }

    public void place(Deployable thing) {
        if (thing instanceof Trap) {
            env.add(0, thing);
        } else {
            env.add(thing);
        }
    }

    public void remove(Deployable triggered) {
        env.remove(triggered);
    }

    public Deployable get(Class<? extends Deployable> type) {
        for (Deployable thing : env) {
            if (thing.getClass() == type) {
                return thing;
            }
        }
        return null;
    }

    public void setPinged(boolean b) {
        this.pinged = b;
    }

    public boolean isPinged() {
        return pinged;
    }

    public boolean isDetected() {
        return getPresent().stream().anyMatch(c -> c.is(Stsflag.detected));
    }

    public boolean isTrapped() {
        return env.stream().anyMatch(d -> d instanceof Trap);
    }
}
