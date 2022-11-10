package Mechanic;

import COLORS.ConsoleColors;
import Exceptions.LogFileException;
import HelpCollections.Pair;

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
    private final Map<Integer, Date> requestsWithTime;
    private Time averageTime;

    public GapsFinder() {
        requestsWithTime = new ConcurrentHashMap<Integer, Date>();
        pattern = Pattern.compile(regex);
    }

    public void checkLogForGap(String logStr) throws LogFileException, ParseException {
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
