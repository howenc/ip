package ui;

import java.util.Scanner;

public class UiStud {
    public UiStud() {

    }
    public String nextInput() {
        Scanner scanObj = new Scanner(System.in);
        return scanObj.nextLine();
    }

    public void greet() {
        System.out.println("Hello. I am Luxion. \n" +
                "What can I do for you?");
    }


    public void exit() {
        System.out.println("Bye. See you soon.");
    }
}
