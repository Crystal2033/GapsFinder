package Exceptions;

/**
 * @project SubstrFinder
 * ©Crystal2033
 * @date 24/10/2022
 */

public class BadArgsException extends Exception{
    private final String errorMsg;
    public BadArgsException(String errorMsg){
        this.errorMsg = errorMsg;
    }

    @Override
    public String getMessage(){
        return errorMsg;
    }

}
