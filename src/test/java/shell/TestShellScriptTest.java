package shell;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestShellScriptTest {
    public static final String TESTAPP_1 = "testapp1";
    @Mock
    TestShell mockTestShell;

    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        lenient().doNothing().when(mockTestShell).run(anyString(), any());

        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @ParameterizedTest
    @MethodSource("getInputCommandAndExpectedList")
    void getCommands(String input, String[] expected) {
        String inputCommand = input;
        InputStream originStdin = System.in;
        System.setIn(new ByteArrayInputStream(inputCommand.getBytes()));

        String[] actual = TestShellScript.getUserInput();

        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }

        System.setIn(originStdin);
    }

    @ParameterizedTest
    @MethodSource("getCommandAndArgumentsList")
    void getCommandAndArgumentsFromUserInput(String command, String[] arguments) {
        String[] userInput = new String[arguments.length + 1];
        userInput[0] = command;
        for (int i = 0; i < arguments.length; i++) {
            userInput[i + 1] = arguments[i];
        }

        String actualCommand = TestShellScript.getCommandFromUserInput(userInput);
        String[] actualArguments = TestShellScript.getArgumentsFromUserInput(userInput);

        assertEquals(command, actualCommand);
        assertEquals(arguments.length, actualArguments.length);
        for (int i = 0; i < arguments.length; i++) {
            assertEquals(arguments[i], actualArguments[i]);
        }
    }

    static Stream<Arguments> getInputCommandAndExpectedList() {
        return Stream.of(
                Arguments.arguments("fullread", new String[]{"fullread"}),
                Arguments.arguments("read 3", new String[]{"read", "3"}),
                Arguments.arguments("fullwrite 0xFFFFFFFF", new String[]{"fullwrite", "0xFFFFFFFF"}),
                Arguments.arguments("write 45 0xFF290372", new String[]{"write", "45", "0xFF290372"}),
                Arguments.arguments("help", new String[]{"help"}),
                Arguments.arguments("exit", new String[]{"exit"})
        );
    }

    static Stream<Arguments> getCommandAndArgumentsList () {
        return Stream.of(
                Arguments.arguments("write", new String[]{"3", "0x38273123"}),
                Arguments.arguments("write", new String[]{"95", "0x92937739"}),
                Arguments.arguments("fullwrite", new String[]{"0x38273123"}),
                Arguments.arguments("read", new String[]{"75"}),
                Arguments.arguments("fullread", new String[]{}),
                Arguments.arguments("help", new String[]{}),
                Arguments.arguments("exit", new String[]{})
        );
    }
}
