import Converters.TimeConverter;
import Exceptions.BadArgsException;
import FileWorkers.FileCommunicator;
import Mechanic.GapsFinder;
import TextHelpers.ArgsConverter;
import ThreadClasses.TextBlockChanger;
import ThreadClasses.ThreadSeeker;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;

import static Settings.CONSTANTS.VALUE_OF_THREADS;

/**
 * @project DataBaseGAPChecking
 * ©Crystal2033
 * @date 10/11/2022
 */
public class Application {
    private static Matcher matcher;

    public static void main(String[] args) throws IOException, InterruptedException, ParseException, BadArgsException {
        final String PATH = "D:\\Paul\\Programming\\Java\\RPKS\\Labs\\DataBaseGAPChecking\\src\\main\\resources\\";
        ArgsConverter argsConverter = new ArgsConverter(args);
        final String inFileName = argsConverter.getInputFileName();
        final String outFileName = argsConverter.getOutputFileName();
        final String avgTimeStr = argsConverter.getAverageTime();

        FileCommunicator fileCommunicator = new FileCommunicator(inFileName, outFileName);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(VALUE_OF_THREADS, new TextBlockChanger(fileCommunicator));
        GapsFinder gapsFinder = null;
        if(avgTimeStr == null){
            gapsFinder = new GapsFinder(null);
        }
        else{
            gapsFinder = new GapsFinder(TimeConverter.getTime(avgTimeStr));
        }


        foundAndOutputGaps(fileCommunicator, cyclicBarrier, gapsFinder);

        if(avgTimeStr == null){
            fileCommunicator.reopenFiles();
            gapsFinder.setAverageTime(gapsFinder.getAverageTime());
            foundAndOutputGaps(fileCommunicator, cyclicBarrier,  gapsFinder);
        }


        System.out.println(COLORS.ConsoleColors.GREEN_BRIGHT + "Work is done." + COLORS.ConsoleColors.RESET + " Check " + outFileName +
                " file. Was found " + COLORS.ConsoleColors.CYAN_BRIGHT + " GAP`s.");
        fileCommunicator.closeBuffers();
    }

    private static void foundAndOutputGaps(FileCommunicator fileCommunicator, CyclicBarrier cyclicBarrier,  GapsFinder gapsFinder) throws InterruptedException {
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
