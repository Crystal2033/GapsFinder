package Mechanic;

import Colors.ConsoleColors;
import Exceptions.LogFileException;
import FileWorkers.FileCommunicator;
import HelpCollections.Pair;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @project DataBaseGAPChecking
 * ©Crystal2033
 * @date 25/11/2022
 */
public class DataBaseGapChecker {
    private final FileCommunicator fileCommunicator;
    private final GapsFinder gapsFinder;

    public DataBaseGapChecker(FileCommunicator fileCommunicator, GapsFinder gapsFinder){
        this.gapsFinder = gapsFinder;
        this.fileCommunicator = fileCommunicator;
    }

    public void checkForGaps() {
        try {
            while (!fileCommunicator.getCurrentBlock().isEmpty()) {
                workWithPartOfText();
            }
            System.out.println(ConsoleColors.YELLOW_BOLD + "--------------------------------------------" +
                    "------------------------------" + ConsoleColors.RESET);
        } catch (IOException | LogFileException | ParseException e) {
            System.out.println(e.getMessage());
        }
    }


    private void workWithPartOfText() throws IOException, LogFileException, ParseException {
        List<String> textLines = fileCommunicator.getCurrentBlock().getTextLines();
        for (String textLine : textLines) {
            boolean isGap = gapsFinder.isLogAGap(textLine);
            if(isGap){
                Pair<LocalDate, LocalTime> requestDateAndTime = gapsFinder.getTimeOfRequestForResult(textLine);
                outPutGapInFile(textLine, requestDateAndTime);
            }
        }
        fileCommunicator.updateBlocks();
        getProgress();
    }

    private void outPutGapInFile(String threadLine, Pair<LocalDate, LocalTime> requestDateAndTime) throws IOException {
        List<String> outPutText = new ArrayList<>();
        outPutText.add("--------------------------------------------------");
        outPutText.add(requestDateAndTime.first + " " + requestDateAndTime.second + " request date and time.");
        outPutText.add(threadLine);
        outPutText.add("--------------------------------------------------");
        fileCommunicator.insertTextInOutputFile(outPutText);

    }

    private void getProgress(){
        System.out.printf("Already checked: %.2f%% of file.",
                (float)fileCommunicator.getBytesAlreadyChecked() / (float)fileCommunicator.getFileSize() * 100);
        System.out.println("Was found "  + ConsoleColors.CYAN_BRIGHT + fileCommunicator.getFoundGaps()
                + ConsoleColors.RESET + " matches.");
    }
}
