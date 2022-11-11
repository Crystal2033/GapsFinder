package ThreadClasses;

import Exceptions.LogFileException;
import FileWorkers.FileCommunicator;
import HelpCollections.Pair;
import Mechanic.GapsFinder;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static Settings.CONSTANTS.*;
/**
 * @project FindGAP
 * ©Crystal2033
 * @date 10/11/2022
 */

public class ThreadSeeker implements Runnable {

    private static int threadID = 0;
    private final FileCommunicator fileCommunicator;
    private final int id;
    private final CyclicBarrier cyclicBarrier;
    private int alreadyCheckedBlocks = 0;
    private int currentStartPosInBlock = 0;
    private final GapsFinder gapsFinder;

    public ThreadSeeker(FileCommunicator fileCommunicator, CyclicBarrier cyclicBarrier, GapsFinder gapsFinder){
        this.gapsFinder = gapsFinder;
        this.fileCommunicator = fileCommunicator;
        id = threadID++;
        new Thread(this, "Hobbit" + id);
        recountCurrentStartPosInBlock();
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public void run() {
        try {
            while (!fileCommunicator.getCurrentBlock().isEmpty()) {
                while (currentStartPosInBlock < VALUE_OF_LINES_IN_BLOCK) {
                    workWithPartOfText();
                    updateThreadReadingInfo(alreadyCheckedBlocks + 1);
                }
                updateThreadReadingInfo(0);
                cyclicBarrier.await();
            }
        } catch (IOException | BrokenBarrierException | InterruptedException | LogFileException | ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    private void recountCurrentStartPosInBlock() {
        currentStartPosInBlock = (id + VALUE_OF_THREADS * alreadyCheckedBlocks) * THREAD_READ_LINES_VALUE;
    }

    private void updateThreadReadingInfo(int newCheckedBlocksValue) {
        alreadyCheckedBlocks = newCheckedBlocksValue;
        recountCurrentStartPosInBlock();
    }


    private void workWithPartOfText() throws IOException, LogFileException, ParseException {
        List<String> threadLines = fileCommunicator.getCurrentBlock().getThreadPartOfText(id + VALUE_OF_THREADS * alreadyCheckedBlocks);
        for (String threadLine : threadLines) {
            boolean isGap = gapsFinder.isLogAGap(threadLine);
            if(isGap){
                Pair<LocalDate, LocalTime> requestDateAndTime = gapsFinder.getTimeOfRequestForResult(threadLine);
                outPutGapInFile(threadLine, requestDateAndTime);
            }
        }
    }

    private void outPutGapInFile(String threadLine, Pair<LocalDate, LocalTime> requestDateAndTime) {
        List<String> outPutText = new ArrayList<>();
        outPutText.add("--------------------------------------------------");
        outPutText.add(requestDateAndTime.first + " " + requestDateAndTime.second + " request date and time.");
        outPutText.add(threadLine);
        outPutText.add("--------------------------------------------------");
        fileCommunicator.insertGapTextInQueueForOutput(outPutText);
    }
}
