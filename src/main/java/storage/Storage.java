package storage;

import command.Commands;
import dukeExceptions.DukeException;
import parser.Parser;
import task.ListOfTask;
import task.Task;
import ui.Ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * This class handles all the reads and writes of the program.
 * It will save the list and load the list.
 */
public class Storage {

    /**
     * Saves the list to a specific file.
     *
     * @param listOfTask The task list to save.
     */
    public static void save(ArrayList<Task> listOfTask) {
        File writeData = new File("./src/data/duke.txt");
        try {
            writeData.createNewFile();
            FileWriter writer = new FileWriter(writeData);
            listOfTask.forEach(x-> {
                try {
                    writer.write(x.write());
                    if (x.getIsDone()) {
                        writer.write("mark " + (listOfTask.indexOf(x) + 1) + "\n");
                    }
                } catch (IOException e) {
                    System.out.println("You do not have access to write to your save file");
                }
            });
            writer.close();
        } catch (IOException e) {
            System.out.println("You do not have access to save your file");
        }
    }

    /**
     * Loads the task list from a specific line in the save file onwards.
     *
     * @param taskList The task list that tasks are loaded into.
     * @param startLine The line number to start loading from.
     * @return Returns false if it is unable to load and the user does not want to
     * be able to save the task list. True if otherwise.
     */
    public static boolean load(ListOfTask taskList, int startLine) {
        File saveData = new File("./src/data/duke.txt");
        String error = null;
        try {
            saveData.createNewFile();
            Scanner readData = new Scanner(saveData);
            for (int i = 0; i < startLine - 1; i++) {
                if (readData.hasNextLine()) {
                    readData.nextLine();
                }
            }
            while (readData.hasNextLine()) {
                String command = readData.nextLine();
                error = command;
                if (command.equals("\n")) {
                    break;
                }
                Parser cmd = new Parser(command);
                Commands action = cmd.parse();
                action.execute(taskList, startLine, error);
                startLine++;
            }
            readData.close();
        } catch (DukeException e) {
            System.out.println("line " + startLine + " corrupted: " + error);
            startLine++;
            load(taskList, startLine);
        } catch (IOException f) {
            if (clarify() == 0) {
                return false;
            }
        }
        return true;
    }

    private static int clarify() {
        System.out.println("You do not have access to create a save file");
        System.out.println("Do you wish to continue? (yes/no) None of your data will be saved.");
        Scanner scan = new Scanner(System.in);
        String respond = scan.nextLine();
        if (respond.equals("yes")) {
            return 1;
        } else if (respond.equals("no")) {
            return 0;
        } else {
            return clarify();
        }
    }
}
