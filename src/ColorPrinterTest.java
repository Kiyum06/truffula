import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColorPrinterTest {

  @Test
  void testPrintlnWithRedColorAndReset() {
    // Arrange: Capture the printed output
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream);

    ColorPrinter printer = new ColorPrinter(printStream);
    printer.setCurrentColor(ConsoleColor.RED);

    // Act: Print the message
    String message = "I speak for the trees";
    printer.println(message);


    String expectedOutput = ConsoleColor.RED + "I speak for the trees" + System.lineSeparator() + ConsoleColor.RESET;

    // Assert: Verify the printed output
    assertEquals(expectedOutput, outputStream.toString());
  }


  @Test
  void testPrintWithGreenColor() {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(outputStream);

      ColorPrinter printer = new ColorPrinter(printStream);
      printer.setCurrentColor(ConsoleColor.GREEN);

      printer.print("Hello");

      String expected = ConsoleColor.GREEN + "Hello" + ConsoleColor.RESET;

      assertEquals(expected, outputStream.toString());
  }

  @Test
  void testPrintWithoutReset() {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(outputStream);

      ColorPrinter printer = new ColorPrinter(printStream);
      printer.setCurrentColor(ConsoleColor.BLUE);

      printer.print("Hello", false);

      String expected = ConsoleColor.BLUE + "Hello";

      assertEquals(expected, outputStream.toString());
  }

  @Test
  void testPrintlnWithoutReset() {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(outputStream);

      ColorPrinter printer = new ColorPrinter(printStream);
      printer.setCurrentColor(ConsoleColor.YELLOW);

      printer.println("Hello", false);

      String expected = ConsoleColor.YELLOW + "Hello" + System.lineSeparator();

      assertEquals(expected, outputStream.toString());
  }

  @Test
  void testDefaultColor() {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(outputStream);

      ColorPrinter printer = new ColorPrinter(printStream);

      printer.print("Hello");

      String expected = ConsoleColor.WHITE + "Hello" + ConsoleColor.RESET;

      assertEquals(expected, outputStream.toString());
  }

  @Test
  void testColorChange() {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(outputStream);

      ColorPrinter printer = new ColorPrinter(printStream);

      printer.setCurrentColor(ConsoleColor.RED);
      printer.print("Red");

      printer.setCurrentColor(ConsoleColor.GREEN);
      printer.print("Green");

      String expected =
          ConsoleColor.RED + "Red" + ConsoleColor.RESET +
          ConsoleColor.GREEN + "Green" + ConsoleColor.RESET;

      assertEquals(expected, outputStream.toString());
  }

  @Test
  void testEmptyMessage() {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(outputStream);

      ColorPrinter printer = new ColorPrinter(printStream);
      printer.setCurrentColor(ConsoleColor.CYAN);

      printer.print("");

      String expected = ConsoleColor.CYAN.toString() + ConsoleColor.RESET;

      assertEquals(expected, outputStream.toString());
  }

  @Test
  void testCurrentColor() {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(outputStream);

      ColorPrinter printer = new ColorPrinter(printStream);

      assertEquals(ConsoleColor.WHITE, printer.getCurrentColor());

      printer.setCurrentColor(ConsoleColor.PURPLE);

      assertEquals(ConsoleColor.PURPLE, printer.getCurrentColor());
  }
}
