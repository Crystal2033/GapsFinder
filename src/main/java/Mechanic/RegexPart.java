package Mechanic;

/**
 * @project DataBaseGAPChecking
 * ©Crystal2033
 * @date 10/11/2022
 */
public enum RegexPart {
    DATE, TIME, REQUEST_STATUS, REQ_ID;

    public int getValue(){
        return this.ordinal() + 1;
    }
}
