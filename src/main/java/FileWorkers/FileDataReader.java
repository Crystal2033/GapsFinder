package FileWorkers;

import java.io.*;
/**
 * @project SubstrFinder
 * ©Crystal2033
 * @date 21/10/2022
 */
public class FileDataReader {
    private BufferedReader bufferedReader;
    private final File file;
    private final FileInputStream fileInputStream;

    public FileDataReader(String fileName) throws IOException {
        file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("File " + file.getName() + " was not found.");
        }
        fileInputStream = new FileInputStream(file);
        FileReader fileReader = new FileReader(file);
        bufferedReader = new BufferedReader(fileReader);
    }

    public void resetBuffer() throws IOException {
        fileInputStream.getChannel().position(0);
        bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
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
