package cli.Commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WcCommandTest {
    private WcCommand wcCommand;

    @TempDir
    File tempDir; // temporary dir for test files

    ByteArrayOutputStream outputStream; // used to capture System.out
    PrintStream originalOut; // used to restore System.out after tests

    @BeforeEach
    void setUp() {
        wcCommand = new WcCommand();
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testValidFile() throws IOException {
        // single file with multiline content is processed correctly
        File tempFile = new File(tempDir, "test.txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Hello, world!\nThis is a test file.");
        }

        wcCommand.file = tempFile;

        wcCommand.run();

        String expectedOutput = "2 lines, 7 words, 35 chars" + System.lineSeparator();
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void testEmptyFile() throws IOException {
        // empty file yields empty output
        File tempFile = new File(tempDir, "empty.txt");
        assertTrue(tempFile.createNewFile());

        wcCommand.file = tempFile;

        wcCommand.run();

        String expectedOutput = "0 lines, 0 words, 0 chars" + System.lineSeparator();
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void testFileWithSpecialCharacters() throws IOException {
        // special characters in a file, including multichar ones, are processed correctly
        File tempFile = new File(tempDir, "special.txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("!@#$%^&*()\n\tTabbed Line\nNewline\n");
        }

        wcCommand.file = tempFile;

        wcCommand.run();

        String expectedOutput = "3 lines, 5 words, 32 chars" + System.lineSeparator();
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void testStandardInput() {
        // string as input, not a file
        String input = "Hello, World!\nThis is a test.";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        wcCommand.file = null;
        wcCommand.run();

        String expectedOutput = "2 lines, 6 words, 30 chars" + System.lineSeparator();
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void testMultipleFiles() throws IOException {
        // multiple files with multiline content are processed correctly
        File file1 = new File(tempDir, "file1.txt");
        try (FileWriter writer = new FileWriter(file1)) {
            writer.write("Line 1\nLine 2");
        }

        File file2 = new File(tempDir, "file2.txt");
        try (FileWriter writer = new FileWriter(file2)) {
            writer.write("Another line\n");
        }

        wcCommand.file = file1;
        wcCommand.run();

        wcCommand.file = file2;
        wcCommand.run();

        System.setOut(originalOut);

        String expectedOutput = "2 lines, 4 words, 14 chars" + System.lineSeparator() +
                "1 lines, 2 words, 13 chars" + System.lineSeparator();
        assertEquals(expectedOutput, outputStream.toString());
    }
}