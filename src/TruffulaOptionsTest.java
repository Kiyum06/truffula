import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TruffulaOptionsTest {

  @Test
  void testValidDirectoryIsSet(@TempDir File tempDir) throws FileNotFoundException {
    // Arrange: Prepare the arguments with the temp directory
    File directory = new File(tempDir, "subfolder");
    directory.mkdir();
    String directoryPath = directory.getAbsolutePath();
    String[] args = {"-nc", "-h", directoryPath};

    // Act: Create TruffulaOptions instance
    TruffulaOptions options = new TruffulaOptions(args);

    // Assert: Check that the root directory is set correctly
    assertEquals(directory.getAbsolutePath(), options.getRoot().getAbsolutePath());
    assertTrue(options.isShowHidden());
    assertFalse(options.isUseColor());
  }


  @Test
    void testDefaultOptions(@TempDir File tempDir) throws FileNotFoundException {
      String[] args = {tempDir.getAbsolutePath()};

      TruffulaOptions options = new TruffulaOptions(args);

      assertFalse(options.isShowHidden());
      assertTrue(options.isUseColor());
      assertEquals(tempDir.getAbsolutePath(), options.getRoot().getAbsolutePath());
    }

    @Test
    void testShowHiddenOnly(@TempDir File tempDir) throws FileNotFoundException {
      String[] args = {"-h", tempDir.getAbsolutePath()};

      TruffulaOptions options = new TruffulaOptions(args);

      assertTrue(options.isShowHidden());
      assertTrue(options.isUseColor());
    }

    @Test
    void testNoColorOnly(@TempDir File tempDir) throws FileNotFoundException {
      String[] args = {"-nc", tempDir.getAbsolutePath()};

      TruffulaOptions options = new TruffulaOptions(args);

      assertFalse(options.isShowHidden());
      assertFalse(options.isUseColor());
    }

    @Test
    void testFlagsInDifferentOrder(@TempDir File tempDir) throws FileNotFoundException {
      String[] args = {"-h", "-nc", tempDir.getAbsolutePath()};

      TruffulaOptions options = new TruffulaOptions(args);

      assertTrue(options.isShowHidden());
      assertFalse(options.isUseColor());
    }

    @Test
    void testMissingPath() {
      String[] args = {};

      assertThrows(IllegalArgumentException.class, () -> {
          new TruffulaOptions(args);
      });
    }

    @Test
    void testUnknownFlag(@TempDir File tempDir) {
      String[] args = {"-abc", tempDir.getAbsolutePath()};

      assertThrows(IllegalArgumentException.class, () -> {
          new TruffulaOptions(args);
      });
    }

    @Test
    void testNonExistingDirectory(@TempDir File tempDir) {
      File directory = new File(tempDir, "notExisting");
      String[] args = {directory.getAbsolutePath()};

      assertThrows(FileNotFoundException.class, () -> {
          new TruffulaOptions(args);
      });
    }

    @Test
    void testFileInsteadOfDirectory(@TempDir File tempDir) throws Exception {
      File file = new File(tempDir, "test.txt");
      file.createNewFile();

      String[] args = {file.getAbsolutePath()};

      assertThrows(FileNotFoundException.class, () -> {
          new TruffulaOptions(args);
      });
    }

    @Test
    void testFlagWithoutPath() {
      String[] args = {"-h"};

      assertThrows(IllegalArgumentException.class, () -> {
          new TruffulaOptions(args);
      });
    }
}
