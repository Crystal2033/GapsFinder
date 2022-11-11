package Mechanic;

import COLORS.ConsoleColors;
import Exceptions.LogFileException;
import HelpCollections.Pair;

import java.io.Console;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @project DataBaseGAPChecking
 * ©Crystal2033
 * @date 10/11/2022
 */
public class GapsFinder {
    private final String regex = "(\\d{4}-\\d{2}-\\d{2}) *\\s* (\\d{2}:\\d{2}:\\d{2}) - INFO -\\s*(RESULT)? QUERY FOR ID = (\\d*)";
    private final Pattern pattern;
    private final Map<Integer, Pair<LocalDate, Time>> requestsWithTime;
    private Time averageTime;
    private final boolean isUserAvgTime;
    int valueOfCheckedGaps = 0;

    public GapsFinder(Time avgTime) {
        if(avgTime == null){
            isUserAvgTime = false;
        }
        else{
            averageTime = avgTime;
            isUserAvgTime = true;
        }
        requestsWithTime = new ConcurrentHashMap<>();
        pattern = Pattern.compile(regex);
    }

    public boolean isLogAGap(String logStr) throws LogFileException, ParseException {
        Matcher matcher = pattern.matcher(logStr);
        if(!matcher.find()){
            throw new LogFileException("Log file consists unpredictable values. Check it out.");
        }
        LocalDate date = getDateFromRegexMatcher(matcher);
        Time time = getTimeFromRegexMatcher(matcher);
        RequestStatus requestStatus = getReqStatusFromRegexMatcher(matcher);
        int requestId = getReqIDFromRegexMatcher(matcher);
        System.out.println(RegexPart.DATE + " = " + ConsoleColors.YELLOW_BRIGHT +  date + ConsoleColors.RESET);
        System.out.println(RegexPart.TIME + " = " + ConsoleColors.YELLOW_BRIGHT + time+ ConsoleColors.RESET);
        System.out.println(RegexPart.REQUEST_STATUS + " = " + ConsoleColors.YELLOW_BRIGHT + requestStatus+ ConsoleColors.RESET);
        System.out.println(RegexPart.REQ_ID + " = " + ConsoleColors.YELLOW_BRIGHT + requestId+ ConsoleColors.RESET);
        System.out.println("--------------------------------------------------------------------------------------");
        if(requestStatus == RequestStatus.REQUEST){
            insertDataInMap(date, time, requestId);
        }
        else if(requestStatus == RequestStatus.RESULT){
            return isGap(date, time, requestId);
        }
        return false;
    }

    public Time getAverageTime(){
        return averageTime;
    }

    private void insertDataInMap(LocalDate localDate, Time time, int requestID){
        Pair<LocalDate, Time> dateTimePair = Pair.create(localDate, time);
        requestsWithTime.put(requestID, dateTimePair);
    }

    private boolean isGap(LocalDate localDate, Time time, int requestID){
        Pair<LocalDate, Time> dateTimePair = requestsWithTime.get(requestID);
        long deltaTime = dateTimePair.second.getTime() - time.getTime();

        if(isUserAvgTime){
            return averageTime.getTime() < deltaTime;
        }
        else{
            long newAvgTime = (averageTime.getTime()*valueOfCheckedGaps + deltaTime) / ++valueOfCheckedGaps;
            averageTime.setTime(newAvgTime);
            System.out.println(ConsoleColors.YELLOW_BRIGHT + "Average time is: " + ConsoleColors.CYAN_BRIGHT + averageTime + ConsoleColors.RESET);
            return false;
        }
    }

    private Pair<Integer, Date> parseLogAndGet(String logStr) {
        return new Pair<Integer, Date>(1, null);
    }

    private LocalDate getDateFromRegexMatcher(Matcher matcher) {
        return LocalDate.parse(matcher.group(RegexPart.DATE.getValue()));
    }

    private Time getTimeFromRegexMatcher(Matcher matcher) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return new java.sql.Time(formatter.parse(matcher.group(RegexPart.TIME.getValue())).getTime());
    }

    private RequestStatus getReqStatusFromRegexMatcher(Matcher matcher) {
        return (matcher.group(RegexPart.REQUEST_STATUS.getValue()) == null) ? RequestStatus.REQUEST : RequestStatus.RESULT;
    }

    private int getReqIDFromRegexMatcher(Matcher matcher) {
        return Integer.parseInt(matcher.group(RegexPart.REQ_ID.getValue()));
    }

}
