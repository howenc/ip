package command;

import java.time.LocalDateTime;

import dukeExceptions.DukeException;
import task.ListOfTask;
import ui.Ui;

/**
 * This class uses its state to execute commands.
 * The Commands factory methods should only be used by Parser class.
 */
public class Commands {

    /**
     * List of both primary and secondary commands.
     */
    public enum CommandEnum { BYE, LIST, TODO, DEADLINE, EVENT, MARK,
            UNMARK, DELETE, BY, FROM, TO, SORT, FIND, UNKNOWN }

    private CommandEnum primaryCommand;
    private String taskDescription;
    private int index;
    private LocalDateTime dateTime;

    private Commands(CommandEnum command) {
        this.primaryCommand = command;
    }

    private Commands(CommandEnum command, String task) {
        this.primaryCommand = command;
        this.taskDescription = task;
    }

    private Commands(CommandEnum command, int index) {
        this.primaryCommand = command;
        this.index = index;
    }


    private Commands(CommandEnum command, LocalDateTime dateTime) {
        this.primaryCommand = command;
        this.dateTime = dateTime;
    }

    /**
     * Constructs a Commands object with only the primary command.
     *
     * @param command The command or action given by the Parse object.
     */
    public static Commands of(CommandEnum command) {
        return new Commands(command);
    }

    /**
     * Constructs a Commands object with both a primary command and task description.
     *
     * @param command The command or action given by the Parse object.
     * @param task The task name or description.
     */
    public static Commands of(CommandEnum command, String task) {
        return new Commands(command, task);
    }

    /**
     * Constructs a Commands object with both a primary command and the index of the task.
     *
     * @param command The command or action given by the Parse object.
     * @param index The index of the task that the command should act on.
     */
    public static Commands of(CommandEnum command, int index) {
        return new Commands(command, index);
    }

    /**
     * Constructs a Commands object with both a primary command and the index of the task.
     *
     * @param command The command or action given by the Parse object.
     * @param dateTime The date and time of a command giving in the format of 'dd-MM-yyyy HHmm'.
     */
    public static Commands of(CommandEnum command, LocalDateTime dateTime) {
        return new Commands(command, dateTime);
    }

    /**
     * Constructs a Commands object with both a primary command and the index of the task.
     *
     * @param command The command or action given by the Parse object.
     * @param task The task name or description.
     * @param secondaryCommand A sub-command that supplements the main command.
     */
    public static Commands of(CommandEnum command, String task, Commands secondaryCommand) {
        return new Commands.TwoCommands(command, task, secondaryCommand);
    }

    /**
     * Constructs a Commands object with both a primary command and the index of the task.
     *
     * @param command The command or action given by the Parse object.
     * @param task The task name or description.
     * @param secondaryCommand A sub-command that supplements the main command.
     * @param tertiaryCommand A sub-command that supplements the main command and comes after the secondaryCommand.
     */
    public static Commands of(CommandEnum command, String task, Commands secondaryCommand, Commands tertiaryCommand) {
        return new Commands.ThreeCommands(command, task, secondaryCommand, tertiaryCommand);
    }

    /**
     * Checks if this object's COMMANDS is the same as command.
     *
     * @param command The COMMANDS enum that you want to compare
     * @return Returns true same, false otherwise.
     */
    public boolean checkCommand(CommandEnum command) {
        if (this.primaryCommand == command) {
            return true;
        }
        return false;
    }
    /**
     * Executes the action or throws a DukeException.
     *
     * @param taskList The list of tasks that the action will be executed in.
     * @return Returns a string representing the task done, or an error or null.
     * @throws DukeException The exception thrown when encountering any problems in executing.
     */
    public String execute(ListOfTask taskList) throws DukeException {
        return execute(taskList, true);
    }

    /**
     * Executes the action or throws a DukeException.
     *
     * @param taskList The list of tasks that the action will be executed in.
     * @return Returns a string representing the task done, or an error or null.
     * @throws DukeException The exception thrown when encountering any problems in executing.
     */
    public String execute(ListOfTask taskList, boolean print) throws DukeException {
        switch (this.primaryCommand) {

        case BYE:
            return Ui.exit();

        case LIST:
            return taskList.listTasks();

        case TODO:
            if (print) {
                return taskList.addToDo(this.taskDescription, true);
            } else {
                return taskList.addToDo(this.taskDescription, false);
            }

        case FIND:
            return taskList.find(this.taskDescription);

        //case SORT:
        //    break;

        case MARK:
            if (print) {
                return taskList.markOrUnmark(this.index, true, true);
            } else {
                return taskList.markOrUnmark(this.index, true, false);
            }

        case UNMARK:
            if (print) {
                return taskList.markOrUnmark(this.index, false, true);
            } else {
                return taskList.markOrUnmark(this.index, false, false);
            }

        case DELETE:
            if (print) {
                return taskList.delete(this.index, true);
            } else {
                return taskList.delete(this.index, false);
            }

        default:

        }
        return null;
    }

    /**
     * Compares LocalDateTime between this object and c.
     *
     * @param c Commands object to be compared to.
     * @return Returns true if this object's time is before c's time, false for all other cases.
     */
    public boolean compareTime(Commands c) {
        try {
            if (this.dateTime.isBefore(c.dateTime)) {
                return true;
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    /**
     * Compares this object to another object.
     *
     * @param obj An object.
     * @return Returns true if both objects are equivalent, false if otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Commands) {
            Commands b = (Commands) obj;
            if (b.primaryCommand == this.primaryCommand
                    && b.dateTime == this.dateTime
                    && b.index == this.index
                    && b.taskDescription == this.taskDescription) {
                return true;
            }
        }
        return false;
    }

    private static class TwoCommands extends Commands {
        private CommandEnum backUpSecondaryCommand;
        private String backUpSecondaryString;
        private Commands secondaryCommand;
        private TwoCommands(CommandEnum command, String str, CommandEnum backUpSecondaryCommand,
                            String backUpSecondaryString) {
            super(command, str);
            this.backUpSecondaryCommand = backUpSecondaryCommand;
            this.backUpSecondaryString = backUpSecondaryString;
        }
        private TwoCommands(CommandEnum command, String str, Commands secondaryCommand) {
            super(command, str);
            this.secondaryCommand = secondaryCommand;
        }

        /**
         * @inheritDoc
         */
        @Override
        public String execute(ListOfTask taskList, boolean print) throws DukeException {
            switch (super.primaryCommand) {

            case DEADLINE:
                if (print) {
                    return taskList.addDeadline(super.taskDescription, this.secondaryCommand.dateTime, true);
                } else {
                    return taskList.addDeadline(super.taskDescription, this.secondaryCommand.dateTime, false);
                }

            default:

            }
            return null;
        }

        /**
         * @inheritDoc
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Commands.TwoCommands) {
                Commands.TwoCommands b = (Commands.TwoCommands) obj;
                if (super.equals(b)
                        && b.backUpSecondaryCommand == this.backUpSecondaryCommand
                        && this.backUpSecondaryString.equals(b.backUpSecondaryString)
                        && this.secondaryCommand.equals(b.secondaryCommand)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class ThreeCommands extends Commands {
        private CommandEnum backUpSecondaryCommand;
        private String backUpSecondaryDescription;
        private CommandEnum backUpTertiaryCommand;
        private String backUpTertiaryDescription;
        private Commands phaseTwo;
        private Commands phaseThree;
        private ThreeCommands(CommandEnum command, String str, CommandEnum command2,
                              String str2, CommandEnum command3, String str3) {
            super(command, str);
            this.backUpSecondaryCommand = command2;
            this.backUpSecondaryDescription = str2;
            this.backUpTertiaryCommand = command3;
            this.backUpTertiaryDescription = str3;
        }

        private ThreeCommands(CommandEnum command, String str, Commands phaseTwo, Commands phaseThree) {
            super(command, str);
            this.phaseTwo = phaseTwo;
            this.phaseThree = phaseThree;
        }

        /**
         * @inheritDoc
         */
        @Override
        public String execute(ListOfTask taskList, boolean print) throws DukeException {
            switch (super.primaryCommand) {

            case EVENT:
                if (print) {
                    return taskList.addEvent(super.taskDescription, this.phaseTwo.dateTime,
                            this.phaseThree.dateTime, true);
                } else {
                    return taskList.addEvent(super.taskDescription, this.phaseTwo.dateTime,
                            this.phaseThree.dateTime, false);
                }

            default:

            }
            return null;
        }

        /**
         * @inheritDoc
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Commands.ThreeCommands) {
                Commands.ThreeCommands b = (Commands.ThreeCommands) obj;
                if (super.equals(b) && b.backUpSecondaryCommand == this.backUpSecondaryCommand
                        && this.backUpSecondaryDescription.equals(b.backUpSecondaryDescription)
                        && b.backUpTertiaryCommand == this.backUpTertiaryCommand
                        && this.backUpTertiaryDescription.equals(b.backUpTertiaryDescription)
                        && this.phaseTwo.equals(b.phaseTwo) && this.phaseThree.equals(b.phaseThree)) {
                    return true;
                }
            }
            return false;
        }
    }
}
