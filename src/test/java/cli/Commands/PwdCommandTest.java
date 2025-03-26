package cli.Commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PwdCommandTest {
    PwdCommand pwdCommand;

    @BeforeEach
    void setUp() {
        pwdCommand = new PwdCommand();
    }

    @Test
    void testCallMethodReturnsCurrentDirectory() {
        String result = pwdCommand.call();

        Path expectedPath = Paths.get("").toAbsolutePath();
        assertEquals(expectedPath.toString(), result, "Expected the current working directory path.");
    }

    // FIXME: issues with Mockito setup
//    @Test
//    void testCallMethodWithMockedWorkingDirectory() {
//        // Arrange
//        try (MockedStatic<Paths> mockedPaths = Mockito.mockStatic(Paths.class)) {
//            Path mockPath = Mockito.mock(Path.class);
//            mockedPaths.when(() -> Paths.get("")).thenReturn(mockPath);
//
//            Path expectedPath = Paths.get("/mocked/directory").toAbsolutePath();
//            Mockito.when(mockPath.toAbsolutePath()).thenReturn(expectedPath);
//
//            PwdCommand pwdCommand = new PwdCommand();
//
//            String result = pwdCommand.call();
//
//            assertEquals(expectedPath.toString(), result, "Expected the mocked working directory path.");
//        }
//    }
}