package org.apt.enextovcf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EnexNoteTest {

    static List<EnexNote> notes = new ArrayList<EnexNote>();
    public static final String fileName = "G:\\My Drive\\VSCode\\Evernote\\Test Card Can be Deleted.enex";
    public static EnexNote note;
    private static String hash = "09c3a0e40c0618dd0fed9e630537562a";

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        notes = EnexNote.parseEnexFile(fileName);
        note = notes.get(0);
    }

    @Test
    void testParseXmlFile() {
        assertEquals(notes.size(), 1);
    }

    @Test
    void testGetContent() {

        assertTrue(note.getContent().trim().startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone"));
    }

    @Test
    void testGetResourceMap() {
        assertTrue(note.getResourceMap().keySet().contains(hash));
    }

    @Test
    void testGetResource() {
        assertTrue(note.getResource(hash) != null);
    }

}
