package Exceptions;

/**
 * @project DataBaseGAPChecking
 * ©Crystal2033
 * @date 10/11/2022
 */
public class LogFileException extends Exception{
    private final String errorMsg;
    public LogFileException(String errorMsg){
        this.errorMsg = errorMsg;
    }

    @Override
    public String getMessage(){
        return errorMsg;
    }
}
