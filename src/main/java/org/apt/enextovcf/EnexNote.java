package org.apt.enextovcf;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.System.exit;

@NonNullByDefault
public class EnexNote {

    private static final Logger logger = LoggerFactory.getLogger(EnexNote.class);

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
        } catch (IOException e) {
            logger.error("Error reading file " + filename);
            exit(1);
        } catch (SAXException e) {
            logger.error("XML errors in file " + filename, e);
            exit(1);
        }
        catch (ParserConfigurationException e) {
            logger.error("Parser Configuration ", e);
            exit(1);
        }

        return enexNotes;
    }

    @NonNull
    private String getResourceElement(Element e, String name) {
        var elements = e.getElementsByTagName(name);
        String result = "";
        if (elements.getLength() > 0 && elements.item(0) != null)
            result = elements.item(0).getTextContent();
        return result == null ? "" : result;
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
                String mime = getResourceElement(resourceElement, "mime");
                String data = getResourceElement(resourceElement, "data");
                String filename = getResourceElement(resourceElement, "file-name");

                // Calculate the hashcode using the extract-hash function (provide your implementation)

                // Create a ResourceRecord and add it to the map
                if (!data.isEmpty() && !mime.isEmpty()) {
                    var datast = data.trim();
                    var bytedata = Base64.getMimeDecoder().decode(datast);
                    String charMD5 = DigestUtils.md5Hex(bytedata);
                    ResourceRecord resourceRecord = new ResourceRecord(mime, bytedata, filename);
                    resourceMap.put(charMD5, resourceRecord);
                }
            }
        } catch (DOMException e) {
            logger.error("XML Format Error in Enex", e);
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


    public record ResourceRecord(String mime, byte[] data, String fileName) {

        public boolean isEmpty() {
            return data.length == 0;
        }
        // Record automatically generates constructor, accessor methods, and equals/hashCode methods
    }



    public void setXml(org.jsoup.nodes.Document doc) {
        this.xmlContent = doc;
        this.parsed = true;

    }
}
