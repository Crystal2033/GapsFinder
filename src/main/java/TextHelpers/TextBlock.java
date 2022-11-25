package TextHelpers;

import java.util.ArrayList;
import java.util.List;
/**
 * @project SubstrFinder
 * ©Crystal2033
 * @date 21/10/2022
 */
public class TextBlock {
    private List<String> textLines = new ArrayList<>();

    public void setTextLines(List<String> textLines) {
        this.textLines = textLines;
    }

    public boolean isEmpty() {
        return textLines.isEmpty();
    }

    public List<String> getTextLines(){
        return textLines;
    }

    public long getBlockSize(){
        long bytesInText = 0;
        for(String line : textLines){
            bytesInText += line.length();
        }
        return bytesInText;
    }

}
