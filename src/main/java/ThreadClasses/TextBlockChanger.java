package ThreadClasses;

import Colors.ConsoleColors;
import FileWorkers.FileCommunicator;

import java.io.IOException;
/**
 * @project SubstrFinder
 * ©Crystal2033
 * @date 21/10/2022
 */
public record TextBlockChanger(FileCommunicator fileCommunicator) implements Runnable {

    @Override
    public void run() {
        try {
            fileCommunicator.insertTextInOutputFile();
            fileCommunicator.updateBlocks();
            getProgress();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getProgress(){
        System.out.printf("Already checked: %.2f%% of file.",
                (float)fileCommunicator.getBytesAlreadyChecked() / (float)fileCommunicator.getFileSize() * 100);
        System.out.println("Was found "  + ConsoleColors.CYAN_BRIGHT + fileCommunicator.getFoundGaps()
                + ConsoleColors.RESET + " matches.");
    }
}
