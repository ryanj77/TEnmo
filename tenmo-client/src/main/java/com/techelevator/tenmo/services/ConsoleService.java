package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.PublicUserInfoDTO;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    private static final String LIST_HEADER_SEPARATOR_LINE = "-------------------------------------------";
    private static final String USER_LIST_TITLE = "Users";
    private static final String USER_LIST_USER_ID_COLUMN_NAME = "ID";
    private static final int USER_LIST_USER_ID_COLUMN_WIDTH = 12;
    private static final String USER_LIST_USERNAME_COLUMN_NAME = "Name";
    private static final String LIST_FOOTER = "---------";

    // TODO Improve this message
    public static final String NOBODY_TO_SEND_MONEY_TO_MESSAGE = "There are no other users in the system. There's nobody else to transfer money with!";

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printMessage(String message) {
        System.out.println(message);
    }
	
    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

    public void printAccountBalance(BigDecimal balance) {
        System.out.println("Your current account balance is: " + NumberFormat.getCurrencyInstance().format(balance));
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public void printMoneySendMenu(List<PublicUserInfoDTO> otherUsers) {
        System.out.println(LIST_HEADER_SEPARATOR_LINE);
        System.out.println(USER_LIST_TITLE);
        System.out.println(padRight(USER_LIST_USER_ID_COLUMN_NAME, USER_LIST_USER_ID_COLUMN_WIDTH) +
                USER_LIST_USERNAME_COLUMN_NAME);
        System.out.println(LIST_HEADER_SEPARATOR_LINE);
        for (PublicUserInfoDTO user : otherUsers) {
            System.out.println(padRight(String.valueOf(user.getId()), USER_LIST_USER_ID_COLUMN_WIDTH) + user.getUsername());
        }
        System.out.println(LIST_FOOTER);
        System.out.println();
    }

    public void printNobodyToSendMoneyToMessage() {
        System.out.println(NOBODY_TO_SEND_MONEY_TO_MESSAGE);
    }
}