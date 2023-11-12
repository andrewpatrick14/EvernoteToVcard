package org.apt.enextovcf;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BusinessCardTestScannable {
    static List<EnexNote> notes = new ArrayList<EnexNote>();
    public static final String fileName = "G:\\My Drive\\VSCode\\Evernote\\scannable.enex";
    public static EnexNote note;
    public static BusinessCard bc;


    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        notes = EnexNote.parseEnexFile(fileName);
        note = notes.get(0);
        bc = new BusinessCard(note);
    }

    @Test
    void testGetFields() {

        var fm = bc.getFieldsmap();
        var fd = fm.get("display-as");
        assertTrue(fd.size() == 1);
        boolean pass = false;
        for (String s : fd) {
            if (s.startsWith("Col")) {
                pass = true;
                break;
            }
        }
        assertTrue(pass);
    }

    @Test
    void testPhone() {
        assertTrue(bc.getFieldsmap().get("phone").contains("94  0 112577482"));
    }

    @Test
    void testEmail() {
        Set<String> aset = bc.getFieldsmap().get("email");
        String em = (String) aset.toArray()[0];
        assertTrue(em.contains("ech"));
    }

    @Test
    void testAddress() {

        Set<String> addrset = bc.getFieldsmap().get("address");
        String addr = (String) addrset.toArray()[0];
        assertTrue(addr.contains("Territory"));
    }

    /*
     * @Test void testBusinessCard() { fail("Not yet implemented"); }
     */

}
