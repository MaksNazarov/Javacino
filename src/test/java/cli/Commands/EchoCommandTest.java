package cli.Commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EchoCommandTest {
    EchoCommand echoCommand;

    @BeforeEach
    void setUp() {
        echoCommand = new EchoCommand();
    }

    @Test
    void testCallMethodWithNoArguments() {
        echoCommand = new EchoCommand();
        echoCommand.args = Collections.emptyList();

        String result = echoCommand.call();

        assertEquals("", result, "Expected an empty string when no arguments are provided.");
    }

    @Test
    void testCallMethodWithSingleArgument() {
        echoCommand = new EchoCommand();
        echoCommand.args = List.of("ILovePDF");

        String result = echoCommand.call();

        assertEquals("ILovePDF", result, "Expected the single argument to be echoed.");
    }

    @Test
    void testCallMethodWithMultipleArguments() {
        echoCommand = new EchoCommand();
        echoCommand.args = List.of("Hello", "World", "!");

        String result = echoCommand.call();

        assertEquals("Hello World !", result, "Expected multiple arguments to be joined with spaces.");
    }

}