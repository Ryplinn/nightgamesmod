package nightgames.characters.trait;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.requirements.Requirement;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static nightgames.requirements.RequirementShortcuts.*;

public class TraitTree {

    public static class RequirementXmlHandler extends DefaultHandler {
        private HashMap<Trait, List<Requirement>> requirements;
        private String text;
        private String trait;
        private List<Requirement> reqs;
        private Attribute att;

        public HashMap<Trait, List<Requirement>> getRequirements() {
            return requirements;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            requirements = new HashMap<>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                        throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (qName.equals("Requirements")) {
                reqs = new ArrayList<>();
            } else if (qName.equals("AttributeReq")) {
                att = Attribute.fromLegacyName(attributes.getValue("type").trim());
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            try {
                super.characters(ch, start, length);
            } catch (SAXException e) {
                e.printStackTrace();
            }
            text = new String(Arrays.copyOfRange(ch, start, start + length));
        }

        /**
         * where the real stuff happens
         */
        @Override
        public void endElement(String uri, String localName, String qName) {
            final String val = text;
            switch (qName) {
                case "Name":
                    trait = text;
                    break;
                case "Trait":
                    requirements.put(Trait.valueOf(trait), reqs);
                    break;
                case "LevelReq":
                    reqs.add(level(Integer.parseInt(val.trim())));
                    break;
                case "TraitReq":
                    reqs.add(trait(Trait.valueOf(val.trim())));
                    break;
                case "NoTraitReq":
                    reqs.add(noTrait(Trait.valueOf(val.trim())));
                    break;
                case "BreastsReq":
                    reqs.add((c, self, other) -> self.body.getLargestBreasts().getSize() >= Integer.parseInt(val.trim()));
                    break;
                case "AttributeReq":
                    reqs.add(attribute(att, Integer.parseInt(val.trim())));
                    break;
                case "BodypartReq":
                    reqs.add(bodypart(val.trim()));
                    break;
            }
        }
    }

    private Map<Trait, List<Requirement>> requirements;

    public TraitTree(InputStream xml) {
        try {
            RequirementXmlHandler handler = new RequirementXmlHandler();
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(handler);
            parser.parse(new InputSource(xml));
            requirements = handler.getRequirements();
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private boolean meetsRequirements(Character self, Trait t) {
        List<Requirement> reqs = t.baseTrait != null ? t.baseTrait.getRequirements() : requirements.get(t);
        return reqs.parallelStream().allMatch(req -> req.meets(null, self, null));
    }

    List<Trait> availTraits(Character self) {
        return requirements.keySet().stream().filter(trait -> meetsRequirements(self, trait)).collect(Collectors.toList());
    }
}
