package org.apt.enextovcf;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@NonNullByDefault
public class EnexNote {

    private String content = "";
    private org.jsoup.nodes.Document xmlContent = new org.jsoup.nodes.Document("");
    private boolean parsed = false;




    private final Map<String, ResourceRecord> resourceMap = new HashMap<>();

    @SuppressWarnings("null")
    public org.jsoup.nodes.Document getXML() {
        if (!parsed) xmlContent = Jsoup.parse(content);
        parsed = true;
        return xmlContent;
    }

    public static List<EnexNote> parseEnexFile(String filename) {
        List<EnexNote> enexNotes = new ArrayList<>();

        try {
            File file = new File(filename);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            Element root = document.getDocumentElement();

            // Find all 'note' sections
            NodeList noteNodes = root.getElementsByTagName("note");

            for (int i = 0; i < noteNodes.getLength(); i++) {
                Element noteElement = (Element) noteNodes.item(i);
                // Create an EnexNote instance for each 'note' section
                if (noteElement != null) {
                    EnexNote enexNote = new EnexNote(noteElement);
                    enexNotes.add(enexNote);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return enexNotes;
    }

    @SuppressWarnings("null")
    public EnexNote(Element doc) {
        try {
            // Extract content
            Node contentNode = doc.getElementsByTagName("content").item(0);
            this.content = contentNode.getTextContent();

            // Extract resources
            NodeList resourceNodes = doc.getElementsByTagName("resource");

            for (int i = 0; i < resourceNodes.getLength(); i++) {
                Element resourceElement = (Element) resourceNodes.item(i);
                Optional<String> mime = Optional.ofNullable(resourceElement.
                        getElementsByTagName("mime").item(0).getTextContent());
                Optional<String> data = Optional.ofNullable(resourceElement
                        .getElementsByTagName("data").item(0).getTextContent());
                Optional<String> filename;
                try {
                    filename = Optional.ofNullable(resourceElement.
                            getElementsByTagName("file-name").item(0).getTextContent());

                }
                catch (Exception e) {
                    filename = Optional.empty();
                }

                // Calculate the hashcode using the extract-hash function (provide your implementation)

                // Create a ResourceRecord and add it to the map
                if (data.isPresent() && mime.isPresent()) {
                    var datast = data.get().trim();
                    var bytedata = Base64.getMimeDecoder().decode(datast);
                    String charMD5 = DigestUtils.md5Hex(bytedata);
                    ResourceRecord resourceRecord = new ResourceRecord(mime.orElse(""), bytedata, filename.orElse(""));
                    resourceMap.put(charMD5, resourceRecord);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getContent() {
        return content;
    }

    public Map<String, ResourceRecord> getResourceMap() {
        return resourceMap;
    }

    @SuppressWarnings("null")
    public ResourceRecord getResource(String hash) {
        return resourceMap.getOrDefault(hash, new ResourceRecord("", new byte[0], ""));
    }



    record ResourceRecord(String mime, byte[] data, String fileName) {

        public boolean isEmpty() {
            return data.length == 0;
        }
        // Record automatically generates constructor, accessor methods, and equals/hashCode methods
    }



    public void setXml(org.jsoup.nodes.Document document) {
        if (document != null) {
            this.xmlContent = document;
            this.parsed = true;
        }

    }
}
