package org.apt.enextovcf;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NonNullByDefault
public class BusinessCard {

    private List<FieldContent> fields = new ArrayList<>();
    private Map<String, Set<String>> fieldsmap = new HashMap<>(); // created on demand
    // skip these when parsing the enml
    private static final String[] fieldsToIgnore = { "label", "context", "contact", "value" };
    private static final Set<String> fieldsToIgnoreSet;
    private ProfileImage profileImage = new ProfileImage();
    private CardImage cardImage = new CardImage();

    public ProfileImage getProfileImage() {
        return profileImage;
    }

    static {
        fieldsToIgnoreSet = new HashSet<>();
        fieldsToIgnoreSet.addAll(Arrays.asList(fieldsToIgnore));
    }

    public Map<String, Set<String>> getFieldsmap() {
        if (fieldsmap.isEmpty()) {
            fieldsmap = createMap(fields);
        }
        return fieldsmap;
    }

    public boolean isEmpty() {
        return fields.isEmpty();
    }

    public List<FieldContent> getFields() {
        return fields;
    }

    public BusinessCard(EnexNote note) {
        String xhtml = note.getContent();
        if (!xhtml.isEmpty()) {
            Document document = note.getXML();
            fields = new ArrayList<>();
            var fields_en = processXHTML(document, "--en-field:", "--en-type:", "--en-value:true");
            var fields_x = processXHTML(document, "x-evernote:", "--nothing", "x-evernote:value");
            // this form is found in cards created by scanable
            fields.addAll(fields_en);
            fields.addAll(fields_x);
            profileImage = new ProfileImage(note);
            cardImage = new CardImage(note);
        }
    }

    protected static Map<String, Set<String>> createMap(List<FieldContent> records) {
        Map<String, Set<String>> resultMap = new HashMap<>();
        for (FieldContent record : records) {
            String fieldName = record.fieldName();
            String fieldType = record.fieldType();
            String fieldValue = record.fieldValue();
            if (fieldValue.isEmpty()) {
                continue; // Skip empty field values
            }
            String key = fieldType.isEmpty() || fieldType.equals(fieldName) ? fieldName : fieldName + ":" + fieldType;
            resultMap.computeIfAbsent(key, k -> new HashSet<>()).add(fieldValue);
        }
        return resultMap;
    }

    public static List<FieldContent> processXHTML(Document document, String fieldMarker, String typeMarker,
                                                  String valueMarker) {
        Elements elements = document.select("div[style*='" + fieldMarker + "'], span[style*='" + fieldMarker + "']");
        var fieldList = getFieldInfo(fieldMarker, typeMarker, valueMarker, elements);
        return new ArrayList<>(fieldList);
    }

    private static List<FieldContent> getFieldInfo(String fieldMarker, String typeMarker, String valueMarker,
                                                   Elements nodes) {
        var resultList = new ArrayList<FieldContent>();
        Pattern fieldPattern = Pattern.compile(fieldMarker + "(.*?);");
        for (Element element : nodes) {
            String fieldName = "";
            String fieldType = "";
            String fieldValue = "";
            try {
                String style = element.attr("style");
                Matcher fieldMatcher = fieldPattern.matcher(style); // find the fieldName
                while (fieldMatcher.find()) {
                    fieldName = Optional.ofNullable(fieldMatcher.group(1)).orElse("");
                }
                if (!fieldName.isEmpty() && !fieldsToIgnoreSet.contains(fieldName)) {
                    fieldValue = element.wholeOwnText();
                    if (fieldValue.isEmpty() || !element.children().isEmpty()) {
                        for (Element childElement : element.children()) {
                            String childStyle = childElement.attr("style");
                            if (childStyle.contains(valueMarker)) {
                                fieldValue = childElement.wholeText();
                                // to get the </br> preserved in multi-line strings
                            }

                        }
                    }
                    if (style.contains(typeMarker)) {
                        Pattern typePattern = Pattern.compile(typeMarker + "(\\w+);");
                        Matcher typeMatcher = typePattern.matcher(style);
                        while (typeMatcher.find()) {
                            fieldType = Optional.ofNullable(typeMatcher.group(1)).orElse("");
                        }
                    }
                    if (!fieldValue.isEmpty())
                        resultList.add(new FieldContent(fieldName, fieldType, fieldValue));
                }
            } catch (Exception e) {
                System.err.println("Ignoring Parse Error in " + fieldName);
            }
        }
        return resultList;
    }

    public CardImage getCardImage() {
        return cardImage;
    }

    public record FieldContent(String fieldName, String fieldType, String fieldValue) {
    }
}
