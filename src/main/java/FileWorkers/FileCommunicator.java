package FileWorkers;

import TextHelpers.TextBlock;

import java.io.IOException;
import java.util.*;

import static Settings.CONSTANTS.VALUE_OF_LINES_IN_BLOCK;
/**
 * @project FindGAP
 * ©Crystal2033
 * @date 10/11/2022
 */
public class FileCommunicator {
    private final FileDataReader fileDataReader;
    private final FileDataWriter fileDataWriter;
    private final TextBlock currentBlock;
    private long bytesAlreadyChecked;
    private int foundGaps = 0;

    public FileCommunicator(String inputFilename, String outputFileName) throws IOException {
        fileDataReader = new FileDataReader(inputFilename);
        fileDataWriter = new FileDataWriter(outputFileName);
        currentBlock = new TextBlock();
        initTextBlock();
    }

    public void updateBlocks() throws IOException {
        bytesAlreadyChecked += currentBlock.getBlockSize();
        insertTextFromFileInBlock(currentBlock);
    }

    public void insertTextInOutputFile(List<String> lines) throws IOException {
        foundGaps++;
        fileDataWriter.writeLines(lines);
    }

    public void reset() throws IOException {
        reopenFiles();
        bytesAlreadyChecked = 0;
        foundGaps = 0;
        initTextBlock();
    }
    private void reopenFiles() throws IOException {
        fileDataReader.resetBuffer();
    }

    public void closeBuffers() throws IOException {
        fileDataWriter.closeWriter();
        fileDataReader.closeReader();
    }


    public TextBlock getCurrentBlock() {
        return currentBlock;
    }

    public long getBytesAlreadyChecked() {
        return bytesAlreadyChecked;
    }

    public long getFileSize() {
        return fileDataReader.getFileSize();
    }

    public int getFoundGaps() {
        return foundGaps;
    }

    private void initTextBlock() throws IOException {
        insertTextFromFileInBlock(currentBlock);
    }

    private void insertTextFromFileInBlock(TextBlock block) throws IOException {
        List<String> textForBlock = new ArrayList<>();
        for (int i = 0; i < VALUE_OF_LINES_IN_BLOCK; i++) {
            if (!fileDataReader.hasNextLine()) {
                break;
            }
            textForBlock.add(fileDataReader.getNextLine());
        }
        block.setTextLines(textForBlock);
    }
}
