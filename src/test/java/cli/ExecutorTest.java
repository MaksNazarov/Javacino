package cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExecutorTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private Executor executor;
    private Parser parser;

    @BeforeEach
    void setUp() {
        ShellContext shellContext = new ShellContext();
        executor = new Executor(shellContext);
        parser = new Parser(shellContext);
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

    @Test
    void testGlobalVars() {
        executor.executeQuery(parser.parse("AUTHOR = MaksNazarov"));
        executor.executeQuery(parser.parse("grep AUTHOR LICENSE"));
        executor.executeQuery(parser.parse("grep $AUTHOR LICENSE"));
        String expected = "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER" + System.lineSeparator() +
                "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM," + System.lineSeparator() +
                "Copyright (c) 2025 MaksNazarov" + System.lineSeparator() + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }

    @Test
    void testGlobalVarAssignmentAndSubstitution() {
        executor.executeQuery(parser.parse("NAME = World"));
        executor.executeQuery(parser.parse("echo Hello $NAME"));
        String expected = "Hello World" + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }

    @Test
    void testEnvironmentVariableFallback() {
        String homeEnvVar = System.getenv("HOME") != null ? "HOME" : "USERPROFILE";

        executor.executeQuery(parser.parse("echo $" + homeEnvVar));
        String expected = System.getenv(homeEnvVar) + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }

    @Test
    void testDoubleQuoteSubstitution() {
        executor.executeQuery(parser.parse("GREETING = Hi"));
        executor.executeQuery(parser.parse("NAME = John"));
        executor.executeQuery(parser.parse("echo \"$GREETING $NAME\""));
        String expected = "Hi John" + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }

    @Test
    void testSingleQuoteNoSubstitution() {
        executor.executeQuery(parser.parse("COLOR=blue"));
        executor.executeQuery(parser.parse("echo 'My $COLOR is #COLOR'"));
        String expected = "My $COLOR is #COLOR" + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }

    @Test
    void testNestedVariableSubstitution() {
        executor.executeQuery(parser.parse("VAR1 = value"));
        executor.executeQuery(parser.parse("VAR2 = VAR1"));
        executor.executeQuery(parser.parse("echo $VAR2 is $$VAR2"));
        String expected = "VAR1 is value" + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }

    @Test
    void testCommandNameSubstitution() {
        executor.executeQuery(parser.parse("CMD = echo"));
        executor.executeQuery(parser.parse("$CMD Dynamic command name"));
        String expected = "Dynamic command name" + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }

    @Test
    void testMultipleSubstitutionsInOneCommand() {
        executor.executeQuery(parser.parse("A = quick"));
        executor.executeQuery(parser.parse("B = brown"));
        executor.executeQuery(parser.parse("C = fox"));
        executor.executeQuery(parser.parse("echo The $A $B $C jumps"));
        String expected = "The quick brown fox jumps" + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }

    @Test
    void testVariableWithSpaces() {
        executor.executeQuery(parser.parse("MESSAGE = \"Hello World\""));
        executor.executeQuery(parser.parse("echo \"$MESSAGE\""));
        String expected = "Hello World" + System.lineSeparator();
        assertEquals(expected, outContent.toString());
    }

}