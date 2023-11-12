package org.apt.enextovcf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jdt.annotation.NonNullByDefault;

@NonNullByDefault
public class CardImage extends ProfileImage {

    public CardImage() {
        // TODO Auto-generated constructor stub
    }

    public CardImage(EnexNote note) {
        super();
        var xml = note.getXML();
        String result = this.findImageInXML(xml, "en-media", "--en-resourceType:", "cardImage;", "--en-contactBlockSection:cardImages;");
        if (result.isEmpty())
            result = findImageInXML(xml, "en-media", "", "", "x-evernote:card-image");
        if (!result.isEmpty() && !hashesToSkipSet.contains(result)) {
            var res = note.getResource(result);
            if (!res.isEmpty()) {
                imageData = res.data();
                mimeType = res.mime();
                hashAttribute = result;
            }
        }
        if (!this.mimeType.isEmpty() && mimeType.contains("/")) {
            imageType = mimeType.split("/")[1];
        }
    }

    public void write(File dir, String fullName) {
        String ext = imageType.equalsIgnoreCase("jpeg") ? "jpg" : imageType;
        String name = Utils.makeUniqueFileName(dir, fullName + "." + ext);
        File outFile = new File(dir, name);
        try {
            Files.write(outFile.toPath(), imageData);
        }
        catch (IOException e) {
            System.err.println("IO Error writing to file " + outFile);
        }
    }

}
