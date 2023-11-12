package org.apt.enextovcf;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainMultiTest {


    static final boolean openExplorer = false;

    static Path tmpdir;
    public static final String fileName = "G:\\My Drive\\VSCode\\Evernote\\My Notes.enex";
    static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        tmpdir = Files.createTempDirectory("enextovcard");
//        String classpath = System.getProperty("java.class.path");
//        for (String item : classpath.split(";")) {
//            System.out.println(item);
//        }
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
        String[] args = {fileName, tmpdir.toString()};
        Main.main(args);
        var fileList = Arrays.asList(tmpdir.toFile().list());
        assertTrue(tmpdir.toFile().list().length == 21);
    }


}
