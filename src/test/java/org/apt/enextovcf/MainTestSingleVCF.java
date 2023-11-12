package org.apt.enextovcf;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTestSingleVCF {

    static Path tmpdir;

    static final boolean openExplorer = false;
    public static final String fileName = "G:\\My Drive\\VSCode\\Evernote\\My Notes.enex";
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
    void testSingle() {
        String name = "testfile.vcf";
        String[] args =  { fileName, tmpdir.toString(), "-s", name };
        Main.main(args);
        assertTrue(Files.exists(tmpdir.resolve(name)));
    }

}
