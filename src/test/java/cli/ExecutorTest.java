package cli;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ExecutorTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private Executor executor;
    private Parser parser;

    @BeforeEach
    void setUp() {
        executor = new Executor();
        parser = new Parser();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testEchoCommandWithQuotes() {
        String input = "echo \"Hello World\"";
        executor.executeQuery(parser.parse(input));
        assertEquals("Hello World" + System.lineSeparator(), outContent.toString());
    }

    @Test
    void testPipeline_EchoToWc() {
        String input = "echo \"Hello\n World\nI\nLove\nYou\" | wc";
        executor.executeQuery(parser.parse(input));
        assertEquals("5 lines, 6 words, 24 chars" + System.lineSeparator(), outContent.toString());
    }

    @Test
    void testGrepCommand(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("data.txt");
        Files.write(tempFile, "i\nlove\npdf".getBytes());

        String input = "grep -A 2 \"i\" " + tempFile;
        executor.executeQuery(parser.parse(input));
        assertEquals("i" + System.lineSeparator() +
                "love" + System.lineSeparator() + "pdf" + System.lineSeparator(), outContent.toString());
    }

    @Test
    void testPwdCommand() {
        String input = "pwd";
        executor.executeQuery(parser.parse(input));
        String expected = System.getProperty("user.dir") + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }

}