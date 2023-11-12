package org.apt.enextovcf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CSVContentTest {
    static List<EnexNote> notes = new ArrayList<EnexNote>();
    public static final String fileName = "G:\\My Drive\\VSCode\\Evernote\\Test Card Can be Deleted.enex";
    public static EnexNote note;
    public static BusinessCard bc;
    public static CSVContent csv;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        notes = EnexNote.parseEnexFile(fileName);
        note = notes.get(0);
        bc = new BusinessCard(note);
        List<BusinessCard> bcl = new ArrayList<>();
        bcl.add(bc);
        csv = new CSVContent(bcl);
    }

    @Test
    void testCVSContent() {

    }

    @Test
    void testGetHeader() {
        assertTrue(csv.getHeader().contains("\"website:homepage\""));
    }

    @Test
    void testMakeAllCVSLines() {
        var lines = csv.makeAllCVSLines();
        assertTrue( lines.size() == 1);
        assertTrue(lines.get(0).startsWith("\"Sand"));

    }

}
