package TextHelpers;

import java.util.ArrayList;
import java.util.List;

import static Settings.CONSTANTS.THREAD_READ_LINES_VALUE;
/**
 * @project SubstrFinder
 * ©Crystal2033
 * @date 21/10/2022
 */
public class TextBlock {
    private List<String> textLines = new ArrayList<>();

    public List<String> getThreadPartOfText(int partValue) {
        if(textLines.isEmpty()){
            return new ArrayList<>();
        }
        int leftEdge = partValue * THREAD_READ_LINES_VALUE;
        int rightEdge = (partValue + 1) * THREAD_READ_LINES_VALUE;
        if(leftEdge > textLines.size()){
            return new ArrayList<>();
        }
        if (rightEdge > textLines.size()) {
            return new ArrayList<>(textLines.subList(leftEdge, textLines.size()));
        }
        return new ArrayList<>(textLines.subList(leftEdge, (partValue + 1) * THREAD_READ_LINES_VALUE));
    }

    public void setTextLines(List<String> textLines) {
        this.textLines = textLines;
    }

    public boolean isEmpty() {
        return textLines.isEmpty();
    }

    public long getBlockSize(){
        long bytesInText = 0;
        for(String line : textLines){
            bytesInText += line.length();
        }
        return bytesInText;
    }

}
