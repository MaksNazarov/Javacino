package cli.Commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GrepCommandTest {

    @TempDir
    Path tempDir;

    @Test
    void testBasicGrepOneMatch() throws Exception {
        // basic grep scenario without options works correctly
        Path file = createFile("test.txt", "zer0", "apple", "banana", "apple pie", "glottal stop", "Dublin");
        String[] args = {"apple", file.toString()};
        GrepCommand command = parseCommand(args);
        String result = command.call();
        assertEquals(String.join(System.lineSeparator(), "apple", "banana", "apple pie", "glottal stop"), result);
    }

    @Test
    void testBasicGrepMultipleMatches() throws Exception {
        // basic grep scenario without options works correctly
        Path file = createFile("test.txt", "zer0", "apple", "banana", "apple pie", "glottal stop", "Dublin");

        // when matches are in different lines
        String[] args = {"apple", file.toString()};
        GrepCommand command = parseCommand(args);
        String result = command.call();
        assertEquals(String.join(System.lineSeparator(), "apple", "banana", "apple pie", "glottal stop"), result);

        // when matches are on the same line
        args = new String[]{"an", file.toString()};
        command = parseCommand(args);
        result = command.call();
        assertEquals(String.join(System.lineSeparator(), "banana", "apple pie"), result);
    }

    @Test
    void testOnlyWords() throws Exception {
        // onlyWords option works correctly, ignoring words where pattern is but a part
        Path file = createFile("test.txt", "apple", "boba", "apples", "pineapple");
        String[] args = {"-w", "apple", file.toString()};
        GrepCommand command = parseCommand(args);
        String result = command.call();
        assertEquals(String.join(System.lineSeparator(), "apple", "boba"), result);
        // note that boba is a part of default context window
    }

    @Test
    void testCaseInsensitive() throws Exception {
        // caseInsensitive argument works correctly, ignoring letter case while matching
        Path file = createFile("test.txt", "Apple", "apple");
        String[] args = {"-i", "APPLE", file.toString()};
        GrepCommand command = parseCommand(args);
        String result = command.call();
        assertEquals(String.join(System.lineSeparator(), "Apple", "apple"), result);
    }

    @Test
    void testLineCount() throws Exception {
        // line count param value matches the amount of lines after matched pattern
        Path file = createFile("test.txt", "a", "b", "c", "d");

        // 1. normally
        String[] args = {"-A", "2", "a", file.toString()};
        GrepCommand command = parseCommand(args);
        String result = command.call();
        assertEquals(String.join(System.lineSeparator(), "a", "b", "c"), result);

        // 2. when context window overlaps with file end
        args = new String[]{"-A", "2", "c", file.toString()};
        command = parseCommand(args);
        result = command.call();
        assertEquals(String.join(System.lineSeparator(), "c", "d"), result);
    }

    @Test
    void testLineCountOverlappingContexts() throws Exception {
        // outputted context window correctly extends if another pattern match happens inside it
        Path file = createFile("test.txt", "a", "a", "a");
        String[] args = {"-A", "2", "a", file.toString()};
        GrepCommand command = parseCommand(args);
        String result = command.call();
        assertEquals(String.join(System.lineSeparator(), "a", "a", "a"), result);
    }

    @Test
    void testMultipleFiles() throws Exception {
        // patterns are matched and found in all provided files
        Path file1 = createFile("file1.txt", "apple");
        Path file2 = createFile("file2.txt", "banana");
        String[] args = {"a", file1.toString(), file2.toString()};
        GrepCommand command = parseCommand(args);
        String result = command.call();
        assertEquals(String.join(System.lineSeparator(), "apple", "banana"), result);
    }

    @Test
    void testNoMatches() throws Exception {
        // in case of no matches, GrepCommand output is an empty string
        Path file = createFile("test.txt", "banana", "cherry");
        String[] args = {"apple", file.toString()};
        GrepCommand command = parseCommand(args);
        String result = command.call();
        assertEquals("", result);
    }

    @Test
    void testNoArgs() {
        String[] args = {};
        assertThrows(IllegalArgumentException.class, () -> parseCommand(args).call());
    }

    @Test
    void testNonExistentFile() {
        String[] args = {"pattern", "nonexistent.txt"};
        GrepCommand command = parseCommand(args);
        assertThrows(IOException.class, command::call);
    }

    /// Create file with provided lines in temporary dir.
    private Path createFile(String filename, String... lines) throws IOException {
        Path file = tempDir.resolve(filename);
        Files.write(file, Arrays.asList(lines));
        return file;
    }

    /// Get GrepCommand instance with following args.
    private GrepCommand parseCommand(String... args) {
        GrepCommand command = new GrepCommand();
        new CommandLine(command).parseArgs(args);
        return command;
    }
}