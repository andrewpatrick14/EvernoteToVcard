package org.apt.enextovcf;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UtilsTest {

    private static File testFile;
    private static File dir;


    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        dir = new File(System.getProperty("java.io.tmpdir")).getAbsoluteFile();
        testFile = createTempFile("test", ".txt", dir);
        testFile.deleteOnExit();

    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {

    }

    @Test
    void test() {
        var fn = Utils.makeUniqueFileName(dir, "/\\:" + testFile.getName());
        var fl = new File(fn);
        assertTrue(fl.getName().startsWith("___test"));
        assertNotEquals(fl.getName(), testFile.getName());

    }

    @Test
    void testUnique() {
        var fn = Utils.makeUniqueFileName(dir, testFile.getName());
        var fl = new File(fn);
        assertTrue(fl.getName().startsWith("test"));
        assertNotEquals(fl.getName(), testFile.getName());
        assertTrue(!fl.exists());
    }

}
