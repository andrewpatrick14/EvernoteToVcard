package org.apt.enextovcf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProfileImageTest {
    static List<EnexNote> notes = new ArrayList<EnexNote>();
    public static final String fileName = "G:\\My Drive\\VSCode\\Evernote\\Test Card Can be Deleted.enex";
    public static EnexNote note;
    public static BusinessCard bc;
    public static String hash = "09c3a0e40c0618dd0fed9e630537562a";
    public static String skipHash = "dd1ebfba7d74872f3eaf1ab971bd87c3";

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        notes = EnexNote.parseEnexFile(fileName);
        note = notes.get(0);

    }

    @Test
    void testValid() {
        ProfileImage pi = new ProfileImage(note);
        assertEquals(pi.getMimeType(), "image/jpeg");
        assertTrue(pi.getImageData().length > 0);
    }

    @Test
    void testSkip() {
        String content = note.getContent();
        var content2 = content.replace(hash, skipHash);
        Document document = Jsoup.parse(content2);
        note.setXml(document);
        ProfileImage pi = new ProfileImage(note);
        assertTrue(pi.isEmpty());
        note.setXml(Jsoup.parse(content));
    }

    @Test
    void testInvalidHash() {
        String content = note.getContent();
        var content2 = content.replace(hash, "No Hash");
        note.setXml(Jsoup.parse(content2));
        ProfileImage pi = new ProfileImage(note);
        assertTrue(pi.isEmpty());
        note.setXml(Jsoup.parse(content));
    }

}
