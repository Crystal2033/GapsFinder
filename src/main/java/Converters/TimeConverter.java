package Converters;

import java.text.ParseException;
import java.time.LocalTime;

/**
 * @project DataBaseGAPChecking
 * ©Crystal2033
 * @date 11/11/2022
 */
public class TimeConverter {
    public static LocalTime getTime(String timeStr) throws ParseException {
        return LocalTime.parse(timeStr);
    }

    public static long fromTimeToSeconds(LocalTime time){
        long timeValue = 0L;
        timeValue += time.getHour() * 3600;
        timeValue += time.getMinute() * 60;
        timeValue += time.getSecond();
        return timeValue;
    }

    public static LocalTime fromSecondsToTime(long timeInSec){
            int hour = (int) (timeInSec / 3600);
            int min = (int) ((timeInSec % 3600) / 60);
            int sec = (int) ((timeInSec % 3600) % 60);
            return LocalTime.of(hour, min, sec);
    }
}
