package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.PublicUserInfoDTO;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL + "tenmo/");

    private TransferService transferService;
    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                consoleService.printMessage("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        consoleService.printMessage("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            consoleService.printMessage("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                consoleService.printMessage("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() { //create account model, each starts with 1,000.00.
        BigDecimal balance = accountService.getBalance(currentUser);
        if (balance == null) {
            consoleService.printErrorMessage();
            System.out.println("Unable to display balance.");
        }
        else {
            consoleService.printAccountBalance(balance);
        }
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        Transfer[] transfers = transferService.getTransferFromUserId(currentUser, Math.toIntExact(currentUser.getUser().getId()));
        System.out.println("-----------------------");
        System.out.println("Transfers");
        System.out.println("ID      From/To        Amount");
        System.out.println("-----------------------");

        int currentUserAccountId = accountService.getAccountByUserId(currentUser, Math.toIntExact(currentUser.getUser().getId())).getAccountId();
        for(Transfer transfer: transfers) {
            printTransferDeetsShort(currentUser, transfer);
        }

        int transferIdChoice = consoleService.promptForInt("\nEnter transfer ID to view details - otherwise press 0 to cancel.");
        Transfer transferChoice = transferIdValidation(transferIdChoice, transfers, currentUser);
        if(transferChoice !=null){
            printTransferDeets(currentUser, transferChoice);
        }

	}

    private void printTransferDeets(AuthenticatedUser currentUser, Transfer transferChoice) {
        int id = transferChoice.getTransferID();
        BigDecimal amount = transferChoice.getTransferAmt();
        int sendingAccount = transferChoice.getFromAccountID();
        int receivingAccount = transferChoice.getToAccountID();
        int transactionTypeId = transferChoice.getTransferTypeID();
        int transactionStatusId = transferChoice.getTransferStatusID();

        int sendingUserId = accountService.getAccountByUserId(currentUser, sendingAccount).getUserId();
        String sendingUserName = UserService.getUserViaUserId(currentUser, sendingUserId).getUsername();
        int receivingUserId = accountService.getAccountByUserId(currentUser, receivingAccount).getUserId();
        String receivingUserName = UserService.getUserViaUserId(currentUser, receivingUserId).getUsername();
        //TODO add types and status for transfer/transaction

        consoleService.printTransferDeets(id, sendingUserName, receivingUserName, transactionType, transactionStatus, amount);
    }

    private void viewPendingRequests() {
		// TODO Auto-generated method stub
        Transfer[] transfers = transferService.getTransferFromUserId(currentUser, Math.toIntExact(currentUser.getUser().getId()));
        System.out.println("-----------------------");
        System.out.println("Pending Transfers");
        System.out.println("ID      From/To        Amount");
        System.out.println("-----------------------");

        for(Transfer transfer: transfers) {
            printTransferDeets(currentUser, transfer);
        }

        int transferIdChoice = consoleService.promptForInt("\nEnter transfer ID to approve or reject - otherwise press 0 to cancel.");
        Transfer transferChoice = transferIdValidation(transferIdChoice, transfers, currentUser);
        if(transferChoice !=null){
            approvalProcess(currentUser, transferChoice);
        }

	}

	private void sendBucks() {
        PublicUserInfoDTO[] users = accountService.getUsers(currentUser);
        if (users == null) {

            consoleService.printErrorMessage();
            System.out.println("Unable to send TE bucks.");
        }
        else {
            List<PublicUserInfoDTO> otherUsers = new ArrayList<>();
            for (PublicUserInfoDTO user : users) {
                if (user.getId() != currentUser.getUser().getId()) {
                    otherUsers.add(user);
                }
            }
            if (otherUsers.isEmpty()) {
                consoleService.printNobodyToSendMoneyToMessage();
            }
            else {
                consoleService.printMoneySendMenu(otherUsers);
                // TODO Implement the rest (prompt for user and send $ to that user)
                int userSelection = consoleService.promptForInt("Please enter ID for recipient - otherwise select 0 to cancel.");
                if(userValidation(userSelection, users, currentUser)){
                    String chooseAmount = consoleService.promptForInt("How much are you sending?");
                    transferService.createTransfer(userSelection, chooseAmount, "Send", "Approved");
                }
            }
        }

	}

    private boolean userValidation(int userSelection, PublicUserInfoDTO[] users, AuthenticatedUser currentUser) {
       //TODO complete method, need to upload now
        return false;
    }

    private void requestBucks() {
		// TODO Auto-generated method stub
        PublicUserInfoDTO[] users = accountService.getUsers(currentUser);

        if (users == null) {
            consoleService.printErrorMessage();
            System.out.println("Unable to request TE bucks.");
        }
        else {
            List<PublicUserInfoDTO> otherUsers = new ArrayList<>();
            for (PublicUserInfoDTO user : users) {
                if (user.getId() != currentUser.getUser().getId()) {
                    otherUsers.add(user);
                }
            }
            if (otherUsers.isEmpty()) {
                consoleService.printNobodyToSendMoneyToMessage();
            }
            else {
                consoleService.printMoneySendMenu(otherUsers);
                // TODO Implement the rest (prompt for user and request $ from that user)
                int userSelection = consoleService.promptForInt("Please enter ID for recipient - otherwise select 0 to cancel.");
                if(userValidation(userSelection, users, currentUser)){
                    String chooseAmount = consoleService.promptForInt("How much are you asking for?");
                    transferService.createTransfer(userSelection, chooseAmount, "Request", "Pending");
            }
        }
	}
}