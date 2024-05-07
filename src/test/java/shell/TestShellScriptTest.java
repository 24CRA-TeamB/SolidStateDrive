package shell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestShellScriptTest {
    @Mock
    TestShell mockTestShell;

    @BeforeEach
    void setUp() {
        lenient().doNothing().when(mockTestShell).read(anyString());
        lenient().doNothing().when(mockTestShell).fullRead();
        lenient().doNothing().when(mockTestShell).write(anyString(), anyString());
        lenient().doNothing().when(mockTestShell).fullwrite(anyString());
        lenient().doNothing().when(mockTestShell).help();
        lenient().doNothing().when(mockTestShell).exit();
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

    @Test
    void runTestShellWithCommand_read() {
        String command = "read";
        String[] arguments = new String[] {"3"};

        TestShellScript.runTestShellWithCommand(mockTestShell, command, arguments);

        verify(mockTestShell, times(1)).read(matches("3"));
    }

    @Test
    void runTestShellWithCommand_fullread() {
        String command = "fullread";
        String[] arguments = new String[] {};

        TestShellScript.runTestShellWithCommand(mockTestShell, command, arguments);

        verify(mockTestShell, times(1)).fullRead();
    }


    @Test
    void runTestShellWithCommand_write() {
        String command = "write";
        String[] arguments = new String[] {"37", "0xC38293FF"};

        TestShellScript.runTestShellWithCommand(mockTestShell, command, arguments);

        verify(mockTestShell, times(1)).write(matches("37"), matches("0xC38293FF"));
    }

    @Test
    void runTestShellWithCommand_fullwrite() {
        String command = "fullwrite";
        String[] arguments = new String[] {"0xC38293FF"};

        TestShellScript.runTestShellWithCommand(mockTestShell, command, arguments);

        verify(mockTestShell, times(1)).fullwrite(matches("0xC38293FF"));
    }

    @Test
    void runTestShellWithCommand_help() {
        String command = "help";
        String[] arguments = new String[] {};

        TestShellScript.runTestShellWithCommand(mockTestShell, command, arguments);

        verify(mockTestShell, times(1)).help();
    }

    @Test
    void runTestShellWithCommand_exit() {
        String command = "exit";
        String[] arguments = new String[] {};

        TestShellScript.runTestShellWithCommand(mockTestShell, command, arguments);

        verify(mockTestShell, times(1)).exit();
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
