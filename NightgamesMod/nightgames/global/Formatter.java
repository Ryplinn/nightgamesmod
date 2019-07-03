package nightgames.global;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.StraponPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.gui.GUI;
import nightgames.pet.PetCharacter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {
    interface MatchAction {
        String replace(Character self, String first, String second, String third);
    }
    private static Map<String, MatchAction> matchActions = null;

    static {
        buildParser();
    }
    private static DecimalFormat formatter = new DecimalFormat("#.##");

    private static void buildParser() {
        matchActions = new HashMap<>();
        matchActions.put("possessive", (self, first, second, third) -> {
            if (self != null) {
                return self.possessiveAdjective();
            }
            return "";
        });
        matchActions.put("name-possessive", (self, first, second, third) -> {
            if (self != null) {
                return self.nameOrPossessivePronoun();
            }
            return "";
        });
        matchActions.put("name", (self, first, second, third) -> {
            if (self != null) {
                return self.getName();
            }
            return "";
        });
        matchActions.put("subject-action", (self, first, second, third) -> {
            if (self != null && third != null) {
                String[] verbs = third.split("\\|");
                if (verbs.length > 1) {
                    return self.subjectAction(verbs[0], verbs[1]);
                } else {
                    return self.subjectAction(verbs[0]);
                }
            }
            return "";
        });
        matchActions.put("pronoun-action", (self, first, second, third) -> {
            if (self != null && third != null) {
                String[] verbs = third.split("\\|");
                if (verbs.length > 1) {
                    return self.pronoun() + " " + self.action(verbs[0], verbs[1]);
                } else {
                    return self.pronoun() + " " + self.action(verbs[0]);
                }
            }
            return "";
        });
        matchActions.put("action", (self, first, second, third) -> {
            if (self != null && third != null) {
                String verbs[] = third.split("\\|");
                if (verbs.length > 1) {
                    return self.action(verbs[0], verbs[1]);
                } else {
                    return self.action(verbs[0]);
                }
            }
            return "";
        });
        matchActions.put("if-female", (self, first, second, third) -> {
            if (self != null && third != null) {
                return self.useFemalePronouns() ? third : "";
            }
            return "";
        });
        matchActions.put("if-male", (self, first, second, third) -> {
            if (self != null && third != null) {
                return self.useFemalePronouns() ? "" : third;
            }
            return "";
        });
        matchActions.put("if-human", (self, first, second, third) -> {
            if (self != null && third != null) {
                return self.human() ? third : "";
            }
            return "";
        });

        matchActions.put("if-nonhuman", (self, first, second, third) -> {
            if (self != null && third != null) {
                return !self.human() ? third : "";
            }
            return "";
        });
        matchActions.put("subject", (self, first, second, third) -> {
            if (self != null) {
                return self.subject();
            }
            return "";
        });
        matchActions.put("direct-object", (self, first, second, third) -> {
            if (self != null) {
                return self.directObject();
            }
            return "";
        });
        matchActions.put("name-do", (self, first, second, third) -> {
            if (self != null) {
                return self.nameDirectObject();
            }
            return "";
        });
        matchActions.put("body-part", (self, first, second, third) -> {
            if (self != null && third != null) {
                BodyPart part = self.body.getRandom(third);
                if (part == null && third.equals("cock") && self.has(Trait.strapped)) {
                    part = StraponPart.generic;
                }
                if (part != null) {
                    return part.describe(self);
                }
            }
            return "";
        });
        matchActions.put("pronoun", (self, first, second, third) -> {
            if (self != null) {
                return self.pronoun();
            }
            return "";
        });
        matchActions.put("reflective", (self, first, second, third) -> {
            if (self != null) {
                return self.reflectivePronoun();
            }
            return "";
        });

        matchActions.put("main-genitals", (self, first, second, third) -> {
            if (self != null) {
                if (self.hasDick()) {
                    return "dick";
                } else if (self.hasPussy()) {
                    return "pussy";
                } else {
                    return "crotch";
                }
            }
            return "";
        });

        matchActions.put("balls-vulva", (self, first, second, third) -> {
            if (self != null) {
                if (self.hasBalls()) {
                    return "testicles";
                } else if (self.hasPussy()) {
                    return "vulva";
                } else {
                    return "crotch";
                }
            }
            return "";
        });

        matchActions.put("master", (self, first, second, third) -> {
            if (self.useFemalePronouns()) {
                return "mistress";
            } else {
                return "master";
            }
        });

        matchActions.put("mister", (self, first, second, third) -> {
            if (self.useFemalePronouns()) {
                return "miss";
            } else {
                return "mister";
            }
        });

        matchActions.put("true-name", (self, first, second, third) -> self.getTrueName());

        matchActions.put("girl", (self, first, second, third) -> self.guyOrGirl());
        matchActions.put("guy", (self, first, second, third) -> self.guyOrGirl());

        matchActions.put("man", (self, first, second, third) -> self.useFemalePronouns() ? "woman" : "man");

        matchActions.put("boy", (self, first, second, third) -> self.boyOrGirl());

        matchActions.put("poss-pronoun", (self, first, second, third) -> {
            if (self != null) {
                return self.possessivePronoun();
            }
            return "";
        });
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null) {
            return "";
        }
        if (original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static String format(String format, Character self, Character target, Object... strings) {
        // pattern to find stuff like {word:otherword:finalword} in strings
        Pattern p = Pattern.compile("\\{((?:self)|(?:other)|(?:master))(?::([^:}]+))?(?::([^:}]+))?}");
        format = String.format(format, strings);

        Matcher matcher = p.matcher(format);
        StringBuffer b = new StringBuffer();
        while (matcher.find()) {
            String first = matcher.group(1);
            String second = matcher.group(2);
            if (second == null) {
                second = "";
            }
            String third = matcher.group(3);
            Character character = null;
            if (first.equals("self")) {
                character = self;
            } else if (first.equals("other")) {
                character = target;
            } else if (first.equals("master") && self instanceof PetCharacter) {
                character = ((PetCharacter)self).getSelf().owner();
            }
            String replacement = matcher.group(0);
            boolean caps = false;
            if (second.toUpperCase().equals(second)) {
                second = second.toLowerCase();
                caps = true;
            }
            MatchAction action = matchActions.get(second);

            if (action == null) {
                System.out.println(second);
            }
            if (action != null) {
                replacement = action.replace(character, first, second, third);
                if (caps) {
                    replacement = capitalizeFirstLetter(replacement);
                }
            }
            matcher.appendReplacement(b, replacement);
        }
        matcher.appendTail(b);
        return b.toString();
    }

    public static String formatDecimal(double val) {
        return formatter.format(val);
    }

    public static void writeIfCombatUpdateImmediately(Combat c, Character self, String string) {
        writeIfCombat(c, self, string);
        if (c != null) {
            c.updateGUI();
        }
    }

    public static void writeIfCombat(Combat c, Character self, String string) {
	    if (c != null) {
	        c.write(self, string);
	    } else if (self.human()) {
            GUI.gui.message(string);
		}
	}

    public static void writeFormattedIfCombat(Combat c, String string, Character self, Character other, Object ...args) {
		if (c == null) {
            GUI.gui.message(format(string, self, other, args));
		} else {
			c.write(self, format(string, self, other, args));
		}
	}

    public static String prependPrefix(String prefix, String fullDescribe) {
        if (prefix.equals("a ") && "aeiou".contains(fullDescribe.substring(0, 1).toLowerCase())) {
            return "an " + fullDescribe;
        }
        return prefix + fullDescribe;
    }
}
