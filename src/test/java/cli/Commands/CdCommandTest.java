package cli.Commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class CdCommandTest {
    private CdCommand cdCommand;
    private String originalWorkingDir;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        cdCommand = new CdCommand();
        originalWorkingDir = System.getProperty("user.dir");
    }

    @Test
    void testCdToExistingDirectory() {
        File testDir = tempDir.resolve("testDir").toFile();
        assertTrue(testDir.mkdir());

        cdCommand.directory = testDir.getAbsolutePath();
        cdCommand.run();

        assertEquals(testDir.getAbsolutePath(), System.getProperty("user.dir"));
        assertEquals("", cdCommand.getExecutionResult());
    }

    @Test
    void testCdToNonExistentDirectory() {
        cdCommand.directory = "/nonexistent/directory";
        cdCommand.run();

        assertEquals(originalWorkingDir, System.getProperty("user.dir"));
        assertTrue(cdCommand.getExecutionResult().startsWith("cd: no such directory:"));
    }

    @Test
    void testCdToFile() {
        File testFile = tempDir.resolve("testFile.txt").toFile();
        try {
            assertTrue(testFile.createNewFile());
        } catch (Exception e) {
            fail("Failed to create test file", e);
        }

        cdCommand.directory = testFile.getAbsolutePath();
        cdCommand.run();

        assertEquals(originalWorkingDir, System.getProperty("user.dir"));
        assertTrue(cdCommand.getExecutionResult().startsWith("cd: not a directory:"));
    }

    @Test
    void testCdNoArguments() {
        String homeDir = System.getProperty("user.home");
        cdCommand.directory = null;
        cdCommand.run();

        assertEquals(homeDir, System.getProperty("user.dir"));
        assertEquals("", cdCommand.getExecutionResult());
    }
} 