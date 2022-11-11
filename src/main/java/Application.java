import FileWorkers.FileCommunicator;
import Mechanic.GapsFinder;
import ThreadClasses.TextBlockChanger;
import ThreadClasses.ThreadSeeker;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static void main(String[] args) throws IOException, InterruptedException {
        final String PATH = "D:\\Paul\\Programming\\Java\\RPKS\\Labs\\DataBaseGAPChecking\\src\\main\\resources\\";
        final String inFileName = PATH + "in.txt";
        final String outFileName = PATH + "out.txt";

        String avgTimeStr = "00:00:01";
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Time avgTime = null;
        try {
            avgTime = new java.sql.Time(formatter.parse(avgTimeStr).getTime());
        }
        catch (ParseException exception){
            System.out.println(exception.getMessage());
        }


        FileCommunicator fileCommunicator = new FileCommunicator(inFileName, outFileName);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(VALUE_OF_THREADS, new TextBlockChanger(fileCommunicator));
        ExecutorService threadingFixedPool = Executors.newFixedThreadPool(VALUE_OF_THREADS);
        GapsFinder gapsFinder = new GapsFinder(null);

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
        System.out.println(COLORS.ConsoleColors.GREEN_BRIGHT + "Work is done." + COLORS.ConsoleColors.RESET + " Check " + outFileName +
                " file. Was found " + COLORS.ConsoleColors.CYAN_BRIGHT + " GAP`s.");
        fileCommunicator.closeBuffers();
    }
}
