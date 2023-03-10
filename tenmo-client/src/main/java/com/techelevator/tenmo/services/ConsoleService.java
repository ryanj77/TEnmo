package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.PublicUserInfoDTO;
import com.techelevator.tenmo.model.Sounds;
import com.techelevator.tenmo.model.UserCredentials;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Scanner;

public class ConsoleService {

    private static final String LIST_HEADER_SEPARATOR_LINE = "-------------------------------------------";
    private static final String USER_LIST_TITLE = "Users";
    private static final String USER_LIST_USER_ID_COLUMN_NAME = "ID";
    private static final int USER_LIST_USER_ID_COLUMN_WIDTH = 12;
    private static final String USER_LIST_USERNAME_COLUMN_NAME = "Name";
    private static final String LIST_FOOTER = "---------";

    // TODO Improve this message
    private static final String NOBODY_TO_TRANSFER_MONEY_WITH_MESSAGE = "There are no other users in the system. There's nobody else to transfer money with! You should make more friends!";

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

    public void printLoginMenu() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
        Sounds.playSound("coin.wav");
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

    public void printAccountBalance(BigDecimal balance) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        System.out.println("Your current account balance is: " + NumberFormat.getCurrencyInstance().format(balance));
        Sounds.playSound("coin.wav");
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }

    public void printOtherUserSelectionMenu(Iterable<PublicUserInfoDTO> otherUsers) {
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

    public void printNobodyToTransferMoneyWithMessage() {
        System.out.println(NOBODY_TO_TRANSFER_MONEY_WITH_MESSAGE);
    }

    public void printTransferDeets(int id, String from, String to, String type, String status, BigDecimal amount) {
        System.out.println("------------------");
        System.out.println("Transfer Details");
        System.out.println("------------------");
        System.out.println("Id: " + id);
        System.out.println("From: " + from);
        System.out.println("To: " + to);
        System.out.println("Type: " + type);
        System.out.println("Status: " + status);
        System.out.println("Amount: " + amount);
    }

    public void printTransferDeetsShortHeader() {

        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.println("ID          From/To                 Amount");
        System.out.println("-------------------------------------------");

    }

    public void printApprovalOption(){
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject");
        System.out.println(" ---------");
    }

    public void pendingRequestHeader(){
        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.println("ID          To                 Amount");
        System.out.println("-------------------------------------------");
    }

    public void typeFormattingRequestDisplay(int type){
        String message;
        if(type == 1){
            message = "To:      ";
        }
        else if(type == 2){
            message = "From: ";
        }
        else{
            System.out.println("Error in type formatting");
        }
    }

}