package org.apt.enextovcf;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EnexVCardTest {

    static List<EnexNote> notes = new ArrayList<EnexNote>();
    public static final String fileName = "G:\\My Drive\\VSCode\\Evernote\\Test Card Can be Deleted.enex";
    public static EnexNote note;
    public static BusinessCard bc;
    public static EnexVCard c;


    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        notes = EnexNote.parseEnexFile(fileName);
        note = notes.get(0);
        if (note != null) {
            bc = new BusinessCard(note);
            c = new EnexVCard(bc);
        }
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @Test
    void testGetFullName() {
        assertTrue(c.getFullName().contains("Sand"));
    }

    @Test
    void testWriteWriter() throws IOException {
        StringWriter sw = new StringWriter();
        c.write(sw);
        assertTrue(sw.getBuffer().toString().contains("Sand"));
    }



}
