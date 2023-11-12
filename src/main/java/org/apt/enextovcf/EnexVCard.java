package org.apt.enextovcf;

import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.VCard;
import net.fortuna.ical4j.vcard.VCardOutputter;
import net.fortuna.ical4j.vcard.parameter.Type;
import net.fortuna.ical4j.vcard.property.*;
import org.apt.enextovcf.BusinessCard.FieldContent;
import org.eclipse.jdt.annotation.NonNullByDefault;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.*;

import static java.util.Map.entry;

@NonNullByDefault
public class EnexVCard {

    private static final Map<String, String> fieldNameMap = Map.ofEntries(entry("display-as", "FN"), entry("fullname", "FN"),
            entry("phone", "TEL"), entry("address", "ADR"), entry("contactnotes", "NOTE"), entry("note-body", "NOTE"),
            entry("website", "URL"),
            entry("contact-title", "TITLE"), entry("social", "X-SOCIALPROFILE"), entry("contact-org", "ORG"),
            entry("organization", "ORG"));
    private static final Map<String, String> socialMediaMap = Map.ofEntries(
            entry("linkedipn", "http://www.linkedin.com/in/{0}"), entry("flickr", "http://www.flickr.com/photos/{0}"),
            entry("facebook", "http://www.facebook.com/{0}"), entry("twitter", "http://twitter.com/{0}"));


    private final VCard vcard;
    private final VCardOutputter outputter;

    private String fullName = "";
    private CardImage cardImage = new CardImage();

    public String getFullName() {
        return fullName;
    }

    public void writeCardImage(File dir) {
        if (!cardImage.isEmpty()) cardImage.write(dir, fullName);
    }

    public EnexVCard(BusinessCard bcard)  {
        var props = createProperties(bcard);
        var image = bcard.getProfileImage();
        if (!image.isEmpty()) {
            new Photo(image.getImageData(), new Type(image.getImageType()));
        }
        vcard = new VCard(props);
        outputter = new VCardOutputter(false);
        cardImage = bcard.getCardImage();
    }

    /**Write this card to the given stream
     * @throws ValidationException if the vcard format is invalid
     * @throws IOException
     */
    public String writeString() throws IOException{
        var sw = new StringWriter();
        try (sw) {
            outputter.output(vcard, sw);
        }
        catch (ValidationException e) {
            System.err.println(e.getMessage());
            System.err.println("Skipping invalid vCard");
        }
        return sw.toString();
    }




    /**Write the vcard to a file based on the fullname property of the card
     * the file name is adjusted to make sure it is unique
     * @param directory the directory to write to
     */
    public void write(File directory) {
        String uniqueName = Utils.makeUniqueFileName(directory, fullName + ".vcf");
        var outFile = new File(directory, uniqueName);
        try (var wr = Files.newBufferedWriter(outFile.toPath(), StandardCharsets.UTF_8)) {
            outputter.output(vcard,
                    wr);
        } catch (IOException e) {
            System.err.println("IO Error Writing to " + outFile);
        }
    }



    private List<Property> createProperties(BusinessCard card) {
        List<Property> props = new ArrayList<>();
        props.add(new Version("4.0"));
        if (!card.getProfileImage().isEmpty()) {
            var photo = new Photo(card.getProfileImage().getImageData());
            props.add(photo);
        }
        for (FieldContent field : card.getFields()) {
            assert(!field.fieldValue().isEmpty());
            String vCardFieldName = fieldNameMap.getOrDefault(field.fieldName().toLowerCase(),
                    field.fieldName().toUpperCase(Locale.UK));
            String value = field.fieldValue();
            String type = field.fieldType();
            if (type.equalsIgnoreCase(value))
                type = "";
            Type t = type.isEmpty() ? null : new Type(type);
            List<Parameter> tlist = new ArrayList<>();
            if (t != null)
                tlist.add(t);
            Property p = null;
            switch (vCardFieldName) {
                case "FN": {
                    fullName = value;
                    p = new Fn(value);
                    break;
                }
                case "TEL": {
                    p = t == null ? new Telephone(value) : new Telephone(value);
                    break;
                }
                case "ADR": {
                    List<String> lines = new LinkedList<>(Arrays.asList(value.split("\\n")));
                    while (lines.size() < 6)
                        lines.add("");
                    value = ";;" + String.join(";", lines);
                    try {
                        p = new Address(tlist, value);
                    } catch (ParseException e) {
                        System.err.println("Skipping incorrect address format" + value);
                        p = null;
                    }
                    break;
                }
                case "NOTE": {
                    p = new Note(tlist, value);
                    break;
                }
                case "URL": {
                    try {
                        p = new Url(tlist, value);
                    } catch (URISyntaxException e) {
                        System.err.println("Skipping incorrect URL format " + value);
                        p = null;
                    }
                    break;
                }
                case "TITLE": {
                    p = new Title(value);
                    break;
                }
                case "ORG": {
                    p = new Org(value);
                    break;
                }
                case "X-SOCIALPROFILE": {
                    if (type.startsWith("@"))
                        type = type.substring(1);
                    if (!type.isEmpty()) {
                        String socialURL = socialMediaMap.getOrDefault(type, "http://" + type + ".com/{0}");
                        socialURL = socialURL.replace("{0}", value);
                        p = new XProperty("SOCIALPROFILE", tlist, socialURL);
                    } else
                        p = null;
                    break;
                }
                case "EMAIL": {
                    p = new Email(tlist, value);
                    break;
                }
                default: {
                    System.err.println("Unknown field " + vCardFieldName);
                    p = null;
                }

            }
            if (p != null) {
                try {
                    p.validate();
                }
                catch (ValidationException e) {
                    if (!p.getId().getPropertyName().equals("URL")) System.err.println(e.getMessage() + vCardFieldName);
                }
                props.add(p);
            }

        }
        return props;
    }

}
