package cli.Commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class LsCommandTest {
    private LsCommand lsCommand;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        lsCommand = new LsCommand();
    }

    @Test
    void testLsCurrentDirectory() throws IOException {
        File file1 = tempDir.resolve("file1.txt").toFile();
        File file2 = tempDir.resolve("file2.txt").toFile();
        File dir1 = tempDir.resolve("dir1").toFile();

        assertTrue(file1.createNewFile());
        assertTrue(file2.createNewFile());
        assertTrue(dir1.mkdir());

        System.setProperty("user.dir", tempDir.toString());
        lsCommand.directory = null;
        lsCommand.call();

        String[] output = lsCommand.getExecutionResult().split("\n");
        Set<String> expectedFiles = new HashSet<>(Arrays.asList("file1.txt", "file2.txt", "dir1"));
        Set<String> actualFiles = new HashSet<>(Arrays.asList(output));

        assertEquals(expectedFiles, actualFiles);
    }

    @Test
    void testLsSpecificDirectory() throws IOException {
        File testDir = tempDir.resolve("testDir").toFile();
        assertTrue(testDir.mkdir());

        File file1 = new File(testDir, "file1.txt");
        File file2 = new File(testDir, "file2.txt");
        assertTrue(file1.createNewFile());
        assertTrue(file2.createNewFile());

        lsCommand.directory = testDir.getAbsolutePath();
        lsCommand.call();

        String[] output = lsCommand.getExecutionResult().split("\n");
        Set<String> expectedFiles = new HashSet<>(Arrays.asList("file1.txt", "file2.txt"));
        Set<String> actualFiles = new HashSet<>(Arrays.asList(output));

        assertEquals(expectedFiles, actualFiles);
    }

    @Test
    void testLsNonExistentDirectory() {
        lsCommand.directory = "/nonexistent/directory";
        lsCommand.call();

        assertTrue(lsCommand.getExecutionResult().startsWith("ls: cannot access"));
    }

    @Test
    void testLsOnFile() throws IOException {
        File testFile = tempDir.resolve("testFile.txt").toFile();
        assertTrue(testFile.createNewFile());

        lsCommand.directory = testFile.getAbsolutePath();
        lsCommand.call();

        assertTrue(lsCommand.getExecutionResult().startsWith("ls: cannot access"));
    }
}