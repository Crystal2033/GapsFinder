package Mechanic;

import COLORS.ConsoleColors;
import Converters.DateConverter;
import Converters.TimeConverter;
import Exceptions.LogFileException;
import HelpCollections.Pair;
import Settings.CONSTANTS;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private final Pattern pattern;
    private final Map<Integer, Pair<LocalDate, LocalTime>> requestsWithTime;

    private long avgTimeInLong;
    private boolean isUserAvgTime;
    private int valueOfCheckedGaps = 0;
    int valueOfGaps = 0;

    public GapsFinder(LocalTime avgTime){
        if(avgTime == null){
            isUserAvgTime = false;
            avgTimeInLong = 0L;
        }
        else{
            avgTimeInLong = TimeConverter.fromTimeToSeconds(avgTime);
            isUserAvgTime = true;
        }
        requestsWithTime = new ConcurrentHashMap<>();
        String regex = "(\\d{4}-\\d{2}-\\d{2}) *\\s* (\\d{2}:\\d{2}:\\d{2}) - INFO -\\s*(RESULT)? QUERY FOR ID = (\\d*)";
        pattern = Pattern.compile(regex);
    }


    public boolean isLogAGap(String logStr) throws LogFileException, ParseException {
        Matcher matcher = pattern.matcher(logStr);
        if(!matcher.find()){
            throw new LogFileException("Log file consists unpredictable values. Check it out.");
        }
        LocalDate date = getDateFromRegexMatcher(matcher);
        LocalTime time = getTimeFromRegexMatcher(matcher);
        RequestStatus requestStatus = getReqStatusFromRegexMatcher(matcher);
        int requestId = getReqIDFromRegexMatcher(matcher);
//        System.out.println(RegexPart.DATE + " = " + ConsoleColors.YELLOW_BRIGHT +  date + ConsoleColors.RESET);
//        System.out.println(RegexPart.TIME + " = " + ConsoleColors.YELLOW_BRIGHT + time+ ConsoleColors.RESET);
//        System.out.println(RegexPart.REQUEST_STATUS + " = " + ConsoleColors.YELLOW_BRIGHT + requestStatus+ ConsoleColors.RESET);
//        System.out.println(RegexPart.REQ_ID + " = " + ConsoleColors.YELLOW_BRIGHT + requestId+ ConsoleColors.RESET);

        if(requestStatus == RequestStatus.REQUEST){
            insertDataInMap(date, time, requestId);
        }
        else if(requestStatus == RequestStatus.RESULT){
            return isGap(date, time, requestId);
        }
        return false;
    }

    public long getAverageTime(){
        return avgTimeInLong;
    }

    public void setAverageTime(long avgTime){
        avgTimeInLong = avgTime;
        isUserAvgTime = true;
    }
    public Pair<LocalDate, LocalTime> getTimeOfRequestForResult(String resultReq) throws LogFileException {
        Matcher matcher = pattern.matcher(resultReq);
        if(!matcher.find()){
            throw new LogFileException("Unpredictable mistake");
        }
        return requestsWithTime.get(Integer.parseInt(matcher.group(RegexPart.REQ_ID.getValue())));
    }

    public int getValueOfCheckedGaps(){
        return valueOfCheckedGaps;
    }

    private void insertDataInMap(LocalDate localDate, LocalTime time, int requestID){
        Pair<LocalDate, LocalTime> dateTimePair = Pair.create(localDate, time);
        requestsWithTime.put(requestID, dateTimePair);
    }

    private boolean isGap(LocalDate resultDate, LocalTime resultTime, int requestID){
        Pair<LocalDate, LocalTime> requestDateTimePair = requestsWithTime.get(requestID);
        long deltaTime = getDeltaDateTime(requestDateTimePair.first, requestDateTimePair.second, resultDate, resultTime);
        if(deltaTime > CONSTANTS.SECONDS_IN_DAY){
            return true;
        }

        if(isUserAvgTime){
            if(avgTimeInLong < deltaTime){
                valueOfGaps++;
                return true;
            }
        }
        else{
            int prevValOfGaps = valueOfCheckedGaps;
            valueOfCheckedGaps++;
            avgTimeInLong = (avgTimeInLong * prevValOfGaps + deltaTime) / valueOfCheckedGaps;
//            System.out.println(ConsoleColors.YELLOW_BRIGHT + "Delta time is: " + ConsoleColors.CYAN_BRIGHT + deltaTime + ConsoleColors.RESET);
//            System.out.println(ConsoleColors.YELLOW_BRIGHT + "Average time is: " + ConsoleColors.CYAN_BRIGHT + avgTimeInLong + ConsoleColors.RESET);
//            System.out.println("--------------------------------------------------------------------------------------");
        }
        return false;
    }

    private long getDeltaDateTime(LocalDate requestDate, LocalTime requestTime, LocalDate resultDate, LocalTime resultTime) {
        int deltaYear = resultDate.getYear() - requestDate.getYear();
        int deltaMonth = resultDate.getMonthValue() - requestDate.getMonthValue();
        int deltaDay = resultDate.getDayOfMonth() - requestDate.getDayOfMonth();
        if(deltaYear > 0 || deltaMonth > 0 || deltaDay > 1){
            return CONSTANTS.SECONDS_IN_DAY;
        }

        if(deltaDay == 1){
            long maxDayTime = CONSTANTS.SECONDS_IN_DAY;
            long timeRequest = TimeConverter.fromTimeToSeconds(requestTime);
            long timeOfPrevDayToItsEnd = maxDayTime - timeRequest;
            long timeResult = TimeConverter.fromTimeToSeconds(resultTime);
            return timeResult + timeOfPrevDayToItsEnd;
        }

        long dateTime = 0;
        int hour = resultTime.getHour() - requestTime.getHour();
        int min = resultTime.getMinute() - requestTime.getMinute();
        int sec = resultTime.getSecond() - requestTime.getSecond();
        dateTime += TimeConverter.fromTimeToSeconds(LocalTime.of(hour, min, sec));
        return dateTime;
    }

    private LocalDate getDateFromRegexMatcher(Matcher matcher) {
        return DateConverter.getDate(matcher.group(RegexPart.DATE.getValue()));
    }

    private LocalTime getTimeFromRegexMatcher(Matcher matcher) throws ParseException {
        return TimeConverter.getTime(matcher.group(RegexPart.TIME.getValue()));
    }

    private RequestStatus getReqStatusFromRegexMatcher(Matcher matcher) {
        return (matcher.group(RegexPart.REQUEST_STATUS.getValue()) == null) ? RequestStatus.REQUEST : RequestStatus.RESULT;
    }

    private int getReqIDFromRegexMatcher(Matcher matcher) {
        return Integer.parseInt(matcher.group(RegexPart.REQ_ID.getValue()));
    }

}
