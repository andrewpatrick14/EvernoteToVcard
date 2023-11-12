package org.apt.enextovcf;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MainTestCSV {

    static Path tmpdir;

    static final boolean openExplorer = false;
    public static final String fileName = "G:\\My Drive\\VSCode\\Evernote\\Test Card Can be Deleted.enex";
    static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        tmpdir = Files.createTempDirectory("enextovcard");
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        System.out.println(tmpdir.toString());
        Process pc = null;
        if (isWindows && openExplorer) {
            var pb = new ProcessBuilder();
            pb.command("explorer", tmpdir.toString());
            pc = pb.start();
        }

    }

    @Test
    void testCVS() {
        String name = "testfile.csv";
        String[] args =  { fileName, tmpdir.toString(), "-c", name };
        Main.main(args);
        assertTrue(Files.exists(tmpdir.resolve(name)));
    }

}
