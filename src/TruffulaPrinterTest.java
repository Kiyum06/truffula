import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TruffulaPrinterTest {

    /**
     * Checks if the current operating system is Windows.
     *
     * This method reads the "os.name" system property and checks whether it
     * contains the substring "win", which indicates a Windows-based OS.
     * 
     * You do not need to modify this method.
     *
     * @return true if the OS is Windows, false otherwise
     */
    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

    /**
     * Creates a hidden file in the specified parent folder.
     * 
     * The filename MUST start with a dot (.).
     *
     * On Unix-like systems, files prefixed with a dot (.) are treated as hidden.
     * On Windows, this method also sets the DOS "hidden" file attribute.
     * 
     * You do not need to modify this method, but you SHOULD use it when creating hidden files
     * for your tests. This will make sure that your tests work on both Windows and UNIX-like systems.
     *
     * @param parentFolder the directory in which to create the hidden file
     * @param filename the name of the hidden file; must start with a dot (.)
     * @return a File object representing the created hidden file
     * @throws IOException if an I/O error occurs during file creation or attribute setting
     * @throws IllegalArgumentException if the filename does not start with a dot (.)
     */
    private static File createHiddenFile(File parentFolder, String filename) throws IOException {
        if(!filename.startsWith(".")) {
            throw new IllegalArgumentException("Hidden files/folders must start with a '.'");
        }
        File hidden = new File(parentFolder, filename);
        hidden.createNewFile();
        if(isWindows()) {
            Path path = Paths.get(hidden.toURI());
            Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
        }
        return hidden;
    }

    @Test
    public void testPrintTree_ExactOutput_WithCustomPrintStream(@TempDir File tempDir) throws IOException {
        // Build the example directory structure:
        // myFolder/
        //    .hidden.txt
        //    Apple.txt
        //    banana.txt
        //    Documents/
        //       images/
        //          Cat.png
        //          cat.png
        //          Dog.png
        //       notes.txt
        //       README.md
        //    zebra.txt

        // Create "myFolder"
        File myFolder = new File(tempDir, "myFolder");
        assertTrue(myFolder.mkdir(), "myFolder should be created");

        // Create visible files in myFolder
        File apple = new File(myFolder, "Apple.txt");
        File banana = new File(myFolder, "banana.txt");
        File zebra = new File(myFolder, "zebra.txt");
        apple.createNewFile();
        banana.createNewFile();
        zebra.createNewFile();

        // Create a hidden file in myFolder
        createHiddenFile(myFolder, ".hidden.txt");

        // Create subdirectory "Documents" in myFolder
        File documents = new File(myFolder, "Documents");
        assertTrue(documents.mkdir(), "Documents directory should be created");

        // Create files in Documents
        File readme = new File(documents, "README.md");
        File notes = new File(documents, "notes.txt");
        readme.createNewFile();
        notes.createNewFile();

        // Create subdirectory "images" in Documents
        File images = new File(documents, "images");
        assertTrue(images.mkdir(), "images directory should be created");

        // Create files in images
        File cat = new File(images, "cat.png");
        File dog = new File(images, "Dog.png");
        cat.createNewFile();
        dog.createNewFile();

        // Set up TruffulaOptions with showHidden = false and useColor = true
        TruffulaOptions options = new TruffulaOptions(myFolder, false, true);

        // Capture output using a custom PrintStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);

        // Instantiate TruffulaPrinter with custom PrintStream
        TruffulaPrinter printer = new TruffulaPrinter(options, printStream);

        // Call printTree (output goes to printStream)
        printer.printTree();

        // Retrieve printed output
        String output = baos.toString();
        String nl = System.lineSeparator();

        // Build expected output with exact colors and indentation
        ConsoleColor reset = ConsoleColor.RESET;
        ConsoleColor white = ConsoleColor.WHITE;
        ConsoleColor purple = ConsoleColor.PURPLE;
        ConsoleColor yellow = ConsoleColor.YELLOW;

        StringBuilder expected = new StringBuilder();
        expected.append(white).append("myFolder/").append(nl).append(reset);
        expected.append(purple).append("   Apple.txt").append(nl).append(reset);
        expected.append(purple).append("   banana.txt").append(nl).append(reset);
        expected.append(purple).append("   Documents/").append(nl).append(reset);
        expected.append(yellow).append("      images/").append(nl).append(reset);
        expected.append(white).append("         cat.png").append(nl).append(reset);
        expected.append(white).append("         Dog.png").append(nl).append(reset);
        expected.append(yellow).append("      notes.txt").append(nl).append(reset);
        expected.append(yellow).append("      README.md").append(nl).append(reset);
        expected.append(purple).append("   zebra.txt").append(nl).append(reset);

        // Assert that the output matches the expected output exactly
        assertEquals(expected.toString(), output);
    }

    @Test
    void testEmptyDirectory(@TempDir File tempDir) {
        TruffulaOptions options =
            new TruffulaOptions(tempDir, false, false);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output);

        TruffulaPrinter printer =
            new TruffulaPrinter(options, printStream);

        printer.printTree();

        String expected = ConsoleColor.WHITE
            + tempDir.getName() + "/"
            + System.lineSeparator()
            + ConsoleColor.RESET;

        assertEquals(expected, output.toString());
    }

    @Test
    void testOneFile(@TempDir File tempDir) throws IOException {
        File file = new File(tempDir, "hello.txt");
        file.createNewFile();

        TruffulaOptions options =
            new TruffulaOptions(tempDir, false, false);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output);

        TruffulaPrinter printer =
            new TruffulaPrinter(options, printStream);

        printer.printTree();

        String expected =
            ConsoleColor.WHITE + tempDir.getName() + "/"
            + System.lineSeparator() + ConsoleColor.RESET
            + ConsoleColor.WHITE + "   hello.txt"
            + System.lineSeparator() + ConsoleColor.RESET;

        assertEquals(expected, output.toString());
    }

    @Test
    void testNestedDirectory(@TempDir File tempDir) throws IOException {
        File folder = new File(tempDir, "documents");
        folder.mkdir();

        File file = new File(folder, "notes.txt");
        file.createNewFile();

        TruffulaOptions options =
            new TruffulaOptions(tempDir, false, false);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output);

        TruffulaPrinter printer =
            new TruffulaPrinter(options, printStream);

        printer.printTree();

        String expected =
            ConsoleColor.WHITE + tempDir.getName() + "/"
            + System.lineSeparator() + ConsoleColor.RESET
            + ConsoleColor.WHITE + "   documents/"
            + System.lineSeparator() + ConsoleColor.RESET
            + ConsoleColor.WHITE + "      notes.txt"
            + System.lineSeparator() + ConsoleColor.RESET;

        assertEquals(expected, output.toString());
    }

    @Test
void testHiddenFileNotShown(@TempDir File tempDir) throws IOException {
    createHiddenFile(tempDir, ".secret.txt");

    TruffulaOptions options =
        new TruffulaOptions(tempDir, false, false);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(output);

    TruffulaPrinter printer =
        new TruffulaPrinter(options, printStream);

    printer.printTree();

    assertFalse(output.toString().contains(".secret.txt"));
}

@Test
void testHiddenFileShown(@TempDir File tempDir) throws IOException {
    createHiddenFile(tempDir, ".secret.txt");

    TruffulaOptions options =
        new TruffulaOptions(tempDir, true, false);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(output);

    TruffulaPrinter printer =
        new TruffulaPrinter(options, printStream);

    printer.printTree();

    assertTrue(output.toString().contains(".secret.txt"));
}

@Test
void testVisibleFileShown(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "hello.txt");
    file.createNewFile();

    createHiddenFile(tempDir, ".secret.txt");

    TruffulaOptions options =
        new TruffulaOptions(tempDir, false, false);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(output);

    TruffulaPrinter printer =
        new TruffulaPrinter(options, printStream);

    printer.printTree();

    assertTrue(output.toString().contains("hello.txt"));
    assertFalse(output.toString().contains(".secret.txt"));
}

    @Test
    void testHiddenFolderNotShown(@TempDir File tempDir) throws IOException {
        File hiddenFolder = new File(tempDir, ".private");
        assertTrue(hiddenFolder.mkdir());

        File file = new File(hiddenFolder, "secret.txt");
        assertTrue(file.createNewFile());

        TruffulaOptions options =
            new TruffulaOptions(tempDir, false, false);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output);

        TruffulaPrinter printer =
            new TruffulaPrinter(options, printStream);

        printer.printTree();

        assertFalse(output.toString().contains(".private"));
        assertFalse(output.toString().contains("secret.txt"));
    }

    @Test
    void testColorByLevel(@TempDir File tempDir) throws IOException {
        File folder = new File(tempDir, "documents");
        assertTrue(folder.mkdir());

        File file = new File(folder, "notes.txt");
        assertTrue(file.createNewFile());

        TruffulaOptions options =
            new TruffulaOptions(tempDir, false, true);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output);

        TruffulaPrinter printer =
            new TruffulaPrinter(options, printStream);

        printer.printTree();

        String nl = System.lineSeparator();

        String expected =
            ConsoleColor.WHITE + tempDir.getName() + "/" + nl + ConsoleColor.RESET
            + ConsoleColor.PURPLE + "   documents/" + nl + ConsoleColor.RESET
            + ConsoleColor.YELLOW + "      notes.txt" + nl + ConsoleColor.RESET;

        assertEquals(expected, output.toString());
    }

    @Test
    void testColorDisabled(@TempDir File tempDir) throws IOException {
        File file = new File(tempDir, "hello.txt");
        assertTrue(file.createNewFile());

        TruffulaOptions options =
            new TruffulaOptions(tempDir, false, false);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output);

        TruffulaPrinter printer =
            new TruffulaPrinter(options, printStream);

        printer.printTree();

        String nl = System.lineSeparator();

        String expected =
            ConsoleColor.WHITE + tempDir.getName() + "/" + nl + ConsoleColor.RESET
            + ConsoleColor.WHITE + "   hello.txt" + nl + ConsoleColor.RESET;

        assertEquals(expected, output.toString());
    }

    @Test
    void testColorCycle(@TempDir File tempDir) {
        File level1 = new File(tempDir, "folder1");
        File level2 = new File(level1, "folder2");
        File level3 = new File(level2, "folder3");

        assertTrue(level1.mkdir());
        assertTrue(level2.mkdir());
        assertTrue(level3.mkdir());

        TruffulaOptions options =
            new TruffulaOptions(tempDir, false, true);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output);

        TruffulaPrinter printer =
            new TruffulaPrinter(options, printStream);

        printer.printTree();

        String nl = System.lineSeparator();

        String expected =
            ConsoleColor.WHITE + tempDir.getName() + "/" + nl + ConsoleColor.RESET
            + ConsoleColor.PURPLE + "   folder1/" + nl + ConsoleColor.RESET
            + ConsoleColor.YELLOW + "      folder2/" + nl + ConsoleColor.RESET
            + ConsoleColor.WHITE + "         folder3/" + nl + ConsoleColor.RESET;

        assertEquals(expected, output.toString());
    }


    @Test
    void testAlphabeticalOrder(@TempDir File tempDir) throws IOException {
        new File(tempDir, "zebra.txt").createNewFile();
        new File(tempDir, "banana.txt").createNewFile();
        new File(tempDir, "Apple.txt").createNewFile();

        TruffulaOptions options =
            new TruffulaOptions(tempDir, false, false);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output);

        TruffulaPrinter printer =
            new TruffulaPrinter(options, printStream);

        printer.printTree();

        String result = output.toString();

        assertTrue(result.indexOf("Apple.txt") < result.indexOf("banana.txt"));
        assertTrue(result.indexOf("banana.txt") < result.indexOf("zebra.txt"));
    }


    @Test
    void testNestedAlphabeticalOrder(@TempDir File tempDir) throws IOException {
        File folder = new File(tempDir, "documents");
        assertTrue(folder.mkdir());

        new File(folder, "zebra.txt").createNewFile();
        new File(folder, "apple.txt").createNewFile();
        new File(folder, "banana.txt").createNewFile();

        TruffulaOptions options =
            new TruffulaOptions(tempDir, false, false);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output);

        TruffulaPrinter printer =
            new TruffulaPrinter(options, printStream);

        printer.printTree();

        String result = output.toString();

        assertTrue(result.indexOf("apple.txt") < result.indexOf("banana.txt"));
        assertTrue(result.indexOf("banana.txt") < result.indexOf("zebra.txt"));
    }

    
}
