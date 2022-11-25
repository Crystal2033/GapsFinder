import Colors.ConsoleColors;
import Converters.TimeConverter;
import Exceptions.BadArgsException;
import FileWorkers.FileCommunicator;
import Mechanic.DataBaseGapChecker;
import Mechanic.GapsFinder;
import TextHelpers.ArgsConverter;

import java.io.IOException;
import java.time.format.DateTimeParseException;

/**
 * @project DataBaseGAPChecking
 * ©Crystal2033
 * @date 10/11/2022
 */
public class Application {

    public static void main(String[] args) {

        System.out.println(ConsoleColors.CYAN_BOLD + "Developer: " + ConsoleColors.BLUE_BOLD
                + "Kulikov Pavel, M8O-311");
        System.out.println(ConsoleColors.PURPLE_BOLD + "This program is able to find gaps in your log file " +
                "with setting average time with" + ConsoleColors.BLUE_BOLD + " hh:mm:ss" + ConsoleColors.PURPLE_BOLD + " style. Or program will choose" +
                " own average time if user does not set it." + ConsoleColors.RESET);

        System.out.println();
        try {
            ArgsConverter argsConverter = new ArgsConverter(args);
            final String inFileName = argsConverter.getInputFileName();
            final String outFileName = argsConverter.getOutputFileName();
            final String avgTimeStr = argsConverter.getAverageTime();

            FileCommunicator fileCommunicator = new FileCommunicator(inFileName, outFileName);
            GapsFinder gapsFinder;
            if (avgTimeStr == null) {
                gapsFinder = new GapsFinder(null);
            } else {
                gapsFinder = new GapsFinder(TimeConverter.getTime(avgTimeStr));
            }
            DataBaseGapChecker dataBaseGapChecker = new DataBaseGapChecker(fileCommunicator, gapsFinder);
            dataBaseGapChecker.checkForGaps();

            if (avgTimeStr == null) {
                fileCommunicator.reset();
                gapsFinder.setAverageTime(gapsFinder.getAverageTime());
                dataBaseGapChecker.checkForGaps();
            }

            System.out.println(Colors.ConsoleColors.GREEN_BRIGHT + "Work is done." + Colors.ConsoleColors.RESET + " Check " + outFileName +
                    " file. Was found " + Colors.ConsoleColors.CYAN_BRIGHT + gapsFinder.getValueOfGaps() + " GAP`s.");
            fileCommunicator.closeBuffers();
        } catch (BadArgsException | IOException | DateTimeParseException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
