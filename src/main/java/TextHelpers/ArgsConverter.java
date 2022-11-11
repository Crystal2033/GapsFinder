package TextHelpers;

import Exceptions.BadArgsException;

import static Settings.CONSTANTS.VALUE_OF_LINES_IN_BLOCK;

/**
 * @project SubstrFinder
 * ©Crystal2033
 * @date 24/10/2022
 */

public class ArgsConverter {
    private final String[] args;

    public ArgsConverter(String[] args) throws BadArgsException {
        if(args.length != 2 && args.length != 3){
            throw new BadArgsException("""
                    There should be 3 or 4 arguments:
                     1. input file with logs
                     2. output file fot gaps
                     3. Time (in hh:mm:ss style) as average time.
                     """);
        }
        this.args = args;
    }

    public String getInputFileName(){
        return args[0];
    }
    public String getOutputFileName(){
        return args[1];
    }
    public String getAverageTime(){
        if (args.length == 2){
            return null;
        }
        return args[2];
    }

}
