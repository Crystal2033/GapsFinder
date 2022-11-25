import Colors.ConsoleColors;
import Converters.TimeConverter;
import Exceptions.BadArgsException;
import FileWorkers.FileCommunicator;
import Mechanic.GapsFinder;
import TextHelpers.ArgsConverter;
import ThreadClasses.TextBlockChanger;
import ThreadClasses.ThreadSeeker;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static Settings.CONSTANTS.VALUE_OF_THREADS;

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
            CyclicBarrier cyclicBarrier = new CyclicBarrier(VALUE_OF_THREADS, new TextBlockChanger(fileCommunicator));
            GapsFinder gapsFinder;
            if (avgTimeStr == null) {
                gapsFinder = new GapsFinder(null);
            } else {
                gapsFinder = new GapsFinder(TimeConverter.getTime(avgTimeStr));
            }

            foundAndOutputGaps(fileCommunicator, cyclicBarrier, gapsFinder);

            if (avgTimeStr == null) {
                fileCommunicator.reset();
                gapsFinder.setAverageTime(gapsFinder.getAverageTime());
                foundAndOutputGaps(fileCommunicator, cyclicBarrier, gapsFinder);
            }

            System.out.println(Colors.ConsoleColors.GREEN_BRIGHT + "Work is done." + Colors.ConsoleColors.RESET + " Check " + outFileName +
                    " file. Was found " + Colors.ConsoleColors.CYAN_BRIGHT + gapsFinder.getValueOfGaps() + " GAP`s.");
            fileCommunicator.closeBuffers();
        } catch (BadArgsException | IOException | InterruptedException | DateTimeParseException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void foundAndOutputGaps(FileCommunicator fileCommunicator, CyclicBarrier cyclicBarrier, GapsFinder gapsFinder) throws InterruptedException {
        ExecutorService threadingFixedPool = Executors.newFixedThreadPool(VALUE_OF_THREADS);
        List<ThreadSeeker> listOfThreads = new ArrayList<>();
        for (int i = 0; i < VALUE_OF_THREADS; i++) {
            ThreadSeeker threadSeeker = new ThreadSeeker(fileCommunicator, cyclicBarrier, gapsFinder);
            listOfThreads.add(threadSeeker);
        }

        List<Future<?>> listOfFuture = new ArrayList<>();
        for (int i = 0; i < VALUE_OF_THREADS; i++) {
            Future<?> future = threadingFixedPool.submit(listOfThreads.get(i));
            listOfFuture.add(future);
        }

        boolean allIsDone = false;
        while (!allIsDone) {
            allIsDone = listOfFuture.stream().allMatch(Future::isDone);
        }
        threadingFixedPool.shutdown();

    }
}
