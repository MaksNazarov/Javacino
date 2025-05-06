package cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {
    Parser parser;
    String command;
    List<List<String>> expected, got;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    @Test
    void testParseEmpty() {
        command = "";
        expected = List.of();
        got = parser.parse(command);
        assertEquals(expected, got, "Empty input should return an empty list");
    }

    @Test
    void testParseSingleCommandNoArgs() {
        command = "cmd";
        expected = List.of(List.of("cmd"));
        got = parser.parse(command);
        assertEquals(expected, got, "Single command with no arguments should return a list with one command");
    }

    @Test
    void testParseSingleCommandWithArgs() {
        command = "echo hello world";
        expected = List.of(List.of("echo", "hello", "world"));
        got = parser.parse(command);
        assertEquals(expected, got, "Command with arguments should split into command and args");
    }

    @Test
    void testParsePipes() {
        command = "grep foo | wc -l";
        expected = List.of(
                List.of("grep", "foo"),
                List.of("wc", "-l")
        );
        got = parser.parse(command);
        assertEquals(expected, got, "Commands separated by pipes should split into separate lists");
    }

    @Test
    void testParseQuotesDouble() {
        command = "echo \"dog echo \"";
        expected = List.of(List.of("echo", "dog echo "));
        got = parser.parse(command);
        assertEquals(expected, got, "Quoted arguments should be treated as a single argument without quotes");
    }

    @Test
    void testParseQuotesSingle() {
        command = "echo 'dog echo '";
        expected = List.of(List.of("echo", "dog echo "));
        got = parser.parse(command);
        assertEquals(expected, got, "Quoted arguments should be treated as a single argument without quotes");
    }

    @Test
    void testParseQuotesCombination() {
        command = "echo \" 'dog echo ' \"";
        expected = List.of(List.of("echo", " 'dog echo ' "));
        got = parser.parse(command);
        assertEquals(expected, got, "Quoted arguments should be treated as a single argument without quotes");
    }

    @Test
    void testParsePipesInQuotes() {
        command = "echo \"a | b\" | wc -l";
        expected = List.of(
                List.of("echo", "a | b"),
                List.of("wc", "-l")
        );
        got = parser.parse(command);
        assertEquals(expected, got, "Pipes inside quotes should not split commands");
    }
}