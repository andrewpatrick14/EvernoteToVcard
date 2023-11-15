
package org.apt.enextovcf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**Profile image contains any profile image embedded in the business card
 * this is not the image of the business card itself but typically the face of
 * the contact.
 */
public class ProfileImage {

    protected static String[] hashesToSkip = { "3eaff6cec30aabd4655a8ea26fd9a9a1", "dd1ebfba7d74872f3eaf1ab971bd87c3" };

    protected static Set<String> hashesToSkipSet = new HashSet<>();

    static {
        hashesToSkipSet.addAll(Arrays.asList(hashesToSkip));
    }
    protected String hashAttribute = "";

    protected byte[] imageData = {};

    protected String mimeType = "";
    protected String imageType = "";

    protected ProfileImage() {

    }

    /**Create a profile image from the given xml enex note. If no image is found then
     * the isEmpty method will return true
     * @param note the note class, which contains resource entries to retrieve the image itself
     */
    public ProfileImage(EnexNote note) {
        var xml = note.getXML();
        String result = findImageInXML(xml, "en-media", "--en-resourceType:", "profileImage;", "--en-contactBlockSection:profileImage;");
        if (result.isEmpty())
            result = findImageInXML(xml, "en-media", "", "", "-evernote-editable:profile-image;");
        if (!result.isEmpty() && !hashesToSkipSet.contains(result)) {
            var res = note.getResource(result);
            imageData = res.data();
            mimeType = res.mime();
            hashAttribute = result;
        }
        if (mimeType != null && !mimeType.isEmpty() && mimeType.contains("/")) {
            imageType = mimeType.split("/")[1];
        }
    }
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProfileImage other = (ProfileImage) obj;
        return Objects.equals(hashAttribute, other.hashAttribute) && Objects.equals(mimeType, other.mimeType);
    }

    /**Look for profile image tags in the given xml
     * @param xml a jsoup xml doc
     * @param imagetag the tag to look for
     * @param resourceStyleMarker the marker for resource types
     * @param resourceStyleType the marker for profile images
     * @param divMarker the marker for the div or span containing the profile image
     * @return returns the hash string for the given image
     */
    @SuppressWarnings("SameParameterValue")
    protected String findImageInXML(Document xml, String imagetag, String resourceStyleMarker, String resourceStyleType,
                                    String divMarker) {
        String hashAttribute = "";
        String regex = Pattern.quote(resourceStyleMarker) + "\\s*" + Pattern.quote(resourceStyleType);
        var compiledSearch = Pattern.compile(regex);
        var divsAndSpans = xml.select("div[style*='" + divMarker + "'], span[style*='" + divMarker + "']");
        for (Element element : divsAndSpans) {
            Elements children = element.children();
            for (Element child : children) {
                String style = child.attr("style");
                if (compiledSearch.matcher(style).find() && child.nodeName().equals(imagetag)) {
                    hashAttribute = child.attr("hash");
                    break; // Stop processing after the first valid match
                }
            }
        }
        return hashAttribute;
    }

    public byte[] getImageData() {
        return imageData;
    }

    /**Get the image type e.g jpeg
     * @return the image type
     */
    public String getImageType() {
        return imageType;
    }

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashAttribute, mimeType);
    }

    /**Check if this contains a valid profile image
     * @return true if this is not a valid image
     */
    public boolean isEmpty() {
        return mimeType.isEmpty();
    }



}


