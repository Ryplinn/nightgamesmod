package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;

import java.util.*;
import java.util.stream.Collectors;

public class StripMinor extends Skill {

    StripMinor() {
        super("Strip Minor");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.cunning) >= 3;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        // ignore target for now... because yup I don't want to refactor subchoices just yet.
        List<Clothing> strippable = getStrippableArticles(c, user);
        return user.canAct() && !strippable.isEmpty();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return c.getStance().dom(user) ? 0 : 8;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Attempt to remove a minor article of clothing from your opponent.";
    }

    private static final List<ClothingSlot> TOP_SLOTS = Arrays.asList(ClothingSlot.head, ClothingSlot.neck, ClothingSlot.arms, ClothingSlot.hands);
    private static final List<ClothingSlot> BOTTOM_SLOTS = Arrays.asList(ClothingSlot.legs, ClothingSlot.feet);

    private boolean canStripArticle(Combat c, Character user, Clothing article) {
        // if it contains top or bottom, don't let character use strip minor. That's in strip top/bottom
        if (Collections.disjoint(article.getSlots(), Arrays.asList(ClothingSlot.top, ClothingSlot.bottom))) {
            // if you can reach top and there's something to strip from the top, then do that
            if (!Collections.disjoint(article.getSlots(), TOP_SLOTS) && c.getStance().reachTop(user)) {
                return true;
            }
            // if you can reach bottom and there's something to strip from the bottom, then do that
            return !Collections.disjoint(article.getSlots(), BOTTOM_SLOTS) && c.getStance().reachBottom(user);
        }
        return false;
    }

    private List<Clothing> getStrippableArticles(Combat c, Character user) {
        return c.getOpponent(user).getOutfit()
                        .getAllStrippable()
                        .stream()
                        .filter(article -> canStripArticle(c, user, article))
                        .collect(Collectors.toList());
    }

    @Override
    public Collection<String> subChoices(Combat c, Character user) {
        return getStrippableArticles(c, user).stream().map(Clothing::getName).map(Formatter::capitalizeFirstLetter).collect(Collectors.toList());
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return -2f; // minor clothing is usually not important, don't waste a turn
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Clothing clothing;
        Optional<Clothing> articleToStrip;
        if (user.human()) {
            articleToStrip = target.getOutfit().getEquipped()
                                                   .stream()
                                                   .filter(article -> article.getName().toLowerCase().equals(choice.toLowerCase()))
                                                   .findAny();
        } else {
            articleToStrip = Random.pickRandom(getStrippableArticles(c, user));
        }
        if (!articleToStrip.isPresent()) {
            c.write(user, Formatter.format("{self:SUBJECT} tried to go after {other:name-possessive} clothing, "
                            + "but found that the intended piece is already gone.", user, target));
            return false;
        }
        clothing = articleToStrip.get();
        int difficulty = clothing.dc() 
                        + target.getLevel()
                        + (target.getStamina().percent() / 4
                        - target.getArousal().percent()) / 5
                        - (!target.canAct() || c.getStance().sub(target) ? 20 : 0);
        difficulty -= 15;
        if (user.checkVsDc(Attribute.cunning, difficulty) || !target.canAct()) {
            c.write(user,
                            Formatter.format("{self:SUBJECT-ACTION:reach|reaches} for"
                                            + " {other:name-possessive} %s and {self:action:pull|pulls} "
                                            + "it away from {other:direct-object}.", user, target,
                                            clothing.getName()));
            target.strip(clothing, c);
        } else {
            c.write(user,
                            Formatter.format("{self:SUBJECT-ACTION:try|tries} to remove"
                                            + " {other:name-possessive} %s, but {other:pronoun-action:keep|keeps}"
                                            + " it in place.", user, target, clothing.getName()));
            return false;
        }

        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new StripMinor();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.stripping;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }
}
