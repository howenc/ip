package parser;

import command.Commands;
import duke.Duke;
import dukeExceptions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * This class takes in the user input and parses returns a Commands object.
 */
public class Parser {

    private String command;
    private String[] initialParse;
    private String[] phaseParse;

    /**
     * Construct the Parser object.
     * @param command The string that needs to be parsed.
     */
    public Parser(String command) {
        this.command = command;
    }

    /**
     * Parses the string into actionable commands.
     * @return Returns a Command object.
     * @throws DukeException If the input is not a command
     */
    public Commands parse() throws DukeException {
        Commands.COMMANDS cmd = this.mainCommand();
        switch (cmd) {
        case BYE: case LIST:
            return Commands.of(cmd);

        case TODO: case FIND:
            if (this.secondWord() == null) {
                throw new DukeException("Please add the task name");
            } else {
                return Commands.of(cmd, this.secondWord());
            }

        case BY: case FROM: case TO:
            try {
                String restOfCommand = this.secondWord().trim();
                LocalDateTime dateTime = LocalDateTime.parse(restOfCommand, Duke.FORMAT);
                return Commands.of(cmd, dateTime);
            } catch (DateTimeParseException | NullPointerException e) {
                throw new DukeDateTimeParseException(cmd
                        + ": The format for dates&time is 'dd-MM-yyyy hhmm'");
            }

        case MARK: case UNMARK: case DELETE:
            try {
                int index = Integer.parseInt(this.secondWord());
                return Commands.of(cmd, index);
            } catch (NumberFormatException | NullPointerException e) {
                throw new DukeNumberFormatException("Place a number after the command");
            }

        case DEADLINE:
            try {
                String task = this.phaseParse();
                String command2 = this.phaseTwo();
                if (task != null) {
                    Parser phaseTwo = new Parser(command2);
                    Commands c = phaseTwo.parse();
                    if (c.checkCommand(Commands.COMMANDS.BY)) {
                        return Commands.of(Commands.COMMANDS.DEADLINE, task, c);
                    }
                } else {
                    throw new DukeException("Please add the task name");
                }
            } catch (DukeUnknownCommandException e) {
                throw new DukeNullPointerException("The format for the command is: deadline task /by date&time");
            } catch (NullPointerException e) {
                throw new DukeNullPointerException("The format for the command is: deadline task /by date&time");
            }

        case EVENT:
            try {
                String task = this.phaseParse();
                String command2 = this.phaseTwo();
                String command3 = this.phaseThree();
                if (task != null) {
                    Parser phaseTwo = new Parser(command2);
                    Commands c1 = phaseTwo.parse();
                    Parser phaseThree = new Parser(command3);
                    Commands c2 = phaseThree.parse();
                    if (!c1.compareTime(c2)) {
                        throw new DukeFromEarlierThanToException("From must be earlier than To");
                    }
                    if (c1.checkCommand(Commands.COMMANDS.FROM) && c2.checkCommand(Commands.COMMANDS.TO)) {
                        return Commands.of(Commands.COMMANDS.EVENT, task, c1, c2);
                    } else {
                        throw new DukeNullPointerException("The format for the command is: "
                                + "event task /from startDayDateTime /to endDayDateTime");
                    }
                } else {
                    throw new DukeException("Please add the task name");
                }
            } catch (DukeUnknownCommandException e) {
                throw new DukeNullPointerException("The format for the command is: "
                        + "event task /from startDayDateTime /to endDayDateTime");
            } catch (NullPointerException e) {
                throw new DukeNullPointerException("The format for the command is: "
                        + "event task /from startDayDateTime /to endDayDateTime");
            }

        }
        throw new DukeUnknownCommandException("Unknown command");
    }

    private Commands.COMMANDS mainCommand() {
        this.initialParse = command.split(" ",2);
        switch (initialParse[0]) {
            case ("bye"):
                return Commands.COMMANDS.BYE;
            case ("list"):
                return Commands.COMMANDS.LIST;
            case ("todo"):
                return Commands.COMMANDS.TODO;
            case ("deadline"):
                return Commands.COMMANDS.DEADLINE;
            case ("event"):
                return Commands.COMMANDS.EVENT;
            case ("mark"):
                return Commands.COMMANDS.MARK;
            case ("unmark"):
                return Commands.COMMANDS.UNMARK;
            case ("delete"):
                return Commands.COMMANDS.DELETE;
            case ("by"):
                return Commands.COMMANDS.BY;
            case ("from"):
                return Commands.COMMANDS.FROM;
            case ("to"):
                return Commands.COMMANDS.TO;
            case ("sort"):
                return Commands.COMMANDS.SORT;
            case ("find"):
                return Commands.COMMANDS.FIND;
            default:
                return Commands.COMMANDS.UNKNOWN;
        }
    }

    private String secondWord() {
        try {
            if (this.initialParse[1].equals("")) {
                return null;
            } else {
                return this.initialParse[1];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private String phaseParse() {
        try {
            this.phaseParse = this.initialParse[1].split("/");
            return phaseParse[0];
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private String phaseTwo() {
        try {
            return this.phaseParse[1];
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private String phaseThree() {
        try {
            return this.phaseParse[2];
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
