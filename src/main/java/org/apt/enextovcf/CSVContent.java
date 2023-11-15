package org.apt.enextovcf;

import static java.util.Map.entry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apt.enextovcf.BusinessCard.FieldContent;
import org.eclipse.jdt.annotation.NonNullByDefault;

@NonNullByDefault
public class CSVContent {

    private final static boolean encodeNewLine = false;

    private final static String fullNameString = "fullName";

    private final static Map<String, String> convertFieldNames = Map.ofEntries(
            entry("display-as", fullNameString),
            entry("contact-title", "title"),
            entry("contact-org", "organization")
    );

    private String header = ""; //column names as a cvs line
    private final List<BusinessCard> cards;
    private List<String> headerList = new ArrayList<>(); //the column header entries
    private List<Map<String, FieldContent>> fieldListMap = new ArrayList<>();

    public CSVContent(List<BusinessCard> cardList) {
        cards = cardList;
        makeListMap();
        makeHeaderString();
    }

    /**Generate a single line of CVS values using the given map of entries
     * if the map doesn't have an entry for the given column "" is used
     * @param fieldMap map of fields
     * @return a line of CVS
     */
    private String getCSVLine(Map<String, FieldContent> fieldMap) {
        StringBuilder sb = new StringBuilder(headerList.size() * 10);
        for (String hd : headerList) {
            String value = "";
            if (fieldMap.containsKey(hd)) value = fieldMap.get(hd).fieldValue();
            if (encodeNewLine) value = value.replace("\n", "<br/>");
            sb.append("\"").append(value).append("\",");
        }
        sb.deleteCharAt(sb.length() - 1); //final ,
        return sb.toString();
    }

    public String getHeader() {
        return header;
    }

    public List<String> makeAllCVSLines() {
        List<String> result = new LinkedList<>();
        for (var fieldMap : fieldListMap) {
            result.add(getCSVLine(fieldMap));
        }
        return result;
    }


    private void makeHeaderString() {
        if (header.isEmpty()) {
            StringBuilder sb = new StringBuilder(headerList.size() * 10);
            for (String hd : headerList) {
                sb.append("\"").append(hd).append("\",");
            }
            sb.deleteCharAt(sb.length() - 1); //final ,
            header = sb.toString();
        }
    }

    private void makeListMap() {
        Set<String> headerSet = new HashSet<>();
        List<Map<String, FieldContent>> resultListMap = new ArrayList<>();
        for (BusinessCard card : cards) {
            List<FieldContent> fields = card.getFields();
            Map<String, FieldContent> fieldMap = new HashMap<>();
            for (BusinessCard.FieldContent field : fields) {
                String fieldName = convertFieldNames.getOrDefault(field.fieldName(), field.fieldName());
                String fieldType = field.fieldType();
                String column = fieldType.isEmpty() || fieldType.equalsIgnoreCase(fieldName) ? fieldName : fieldName + ":" + fieldType;
                headerSet.add(column);
                fieldMap.put(column, field);
            }
            resultListMap.add(fieldMap);
        }
        List<String> headerList = new LinkedList<>();
        headerSet.remove(fullNameString); //make sure fullName is first
        headerList.add(fullNameString);
        headerList.addAll(headerSet);

        this.headerList = headerList;
        this.fieldListMap = resultListMap;

    }

    public void writeCVS(File file) {
        List<String> lines = new LinkedList<>();
        lines.add(header);
        lines.addAll(makeAllCVSLines());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed Writing to File " + file);
        }

    }

}
