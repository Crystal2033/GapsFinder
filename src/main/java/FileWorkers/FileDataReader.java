package FileWorkers;

import java.io.*;
/**
 * @project SubstrFinder
 * ©Crystal2033
 * @date 21/10/2022
 */
public class FileDataReader {
    private final BufferedReader bufferedReader;
    private final File file;

    public FileDataReader(String fileName) throws IOException {
        file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("File " + file.getName() + " was not found.");
        }
        FileReader fileReader = new FileReader(file);
        bufferedReader = new BufferedReader(fileReader);
    }

    public String getNextLine() throws IOException {
        String fileLine = "";
        if (!hasNextLine()) {
            bufferedReader.close();
            return fileLine;
        }
        return bufferedReader.readLine();
    }

    public void closeReader() throws IOException {
        bufferedReader.close();
    }

    public boolean hasNextLine() throws IOException {
        return bufferedReader.ready();
    }

    public long getFileSize(){
        return file.length();
    }
}
