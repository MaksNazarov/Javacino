package cli.Commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CatCommandTest {

    private CatCommand catCommand;

    @TempDir
    File tempDir; // temporary dir for test files

    @BeforeEach
    void setUp() {
        catCommand = new CatCommand();
    }

    @Test
    void testValidFile() throws IOException {
        // simple file with several lines of content is outputted correctly
        String data = "JSON is a best file format ever!"
                + System.lineSeparator()
                + "I love it with passion!"
                + System.lineSeparator();

        File tempFile = new File(tempDir, "test.txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(data);
        }

        catCommand.file = tempFile;

        String result = catCommand.call();
        assertEquals(data, result);
    }

    @Test
    void testEmptyFile() throws IOException {
        // empty file as input yields empty output
        File tempFile = new File(tempDir, "empty.txt");
        assertTrue(tempFile.createNewFile(), "if this fails, there's an issue with test code");

        catCommand.file = tempFile;

        String result = catCommand.call();
        assertEquals("", result);
    }

    @Test
    void testNonExistentFile() {
        // non-existent file as input yields empty output
        catCommand.file = new File(tempDir, "nonexistent.txt");

        String result = catCommand.call();
        assertEquals("", result);
    }

    @Test
    void testLargeFile() throws IOException {
        // a large file as input is processed correctly
        File tempFile = new File(tempDir, "large.txt");
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 1e6; i++) {
            largeContent.append("I ate another apple, now I ate ")
                    .append(i)
                    .append(" apples total")
                    .append(System.lineSeparator());
        }
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(largeContent.toString());
        }

        catCommand.file = tempFile;

        String result = catCommand.call();
        assertEquals(largeContent.toString(), result);
    }
}