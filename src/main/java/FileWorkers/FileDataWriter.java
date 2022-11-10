package FileWorkers;

import java.io.*;
import java.util.List;
/**
 * @project SubstrFinder
 * ©Crystal2033
 * @date 21/10/2022
 */
public class FileDataWriter {
    private final BufferedWriter bufferedWriter;

    public FileDataWriter(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();

        FileWriter fileWriter = new FileWriter(file);
        bufferedWriter = new BufferedWriter(fileWriter);

    }

    synchronized public void writeLines(List<String> lines) throws IOException {
        for (String line : lines) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
    }

    public void closeWriter() throws IOException {
        bufferedWriter.close();
    }
}
