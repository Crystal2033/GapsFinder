package Converters;

import java.time.LocalDate;

/**
 * @project DataBaseGAPChecking
 * ©Crystal2033
 * @date 11/11/2022
 */
public class DateConverter {
    public static LocalDate getDate(String date){
        return LocalDate.parse(date);
    }
}
