package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
import java.util.*;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private static final String API_TENMO_BASE_URL = API_BASE_URL + "tenmo/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_TENMO_BASE_URL);

    private TransferService transferService = new TransferService(API_TENMO_BASE_URL);
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
        } else {
            consoleService.printAccountBalance(balance);
        }
    }

    private void viewTransferHistory() {
// WONT_COMPILE
//		// TODO Auto-generated method stub
//        Transfer[] transfers = transferService.getTransferFromUserId(currentUser, Math.toIntExact(currentUser.getUser().getId()));
//        consoleService.printTransferDeetsShortHeader();
//
//        int currentUserAccountId = accountService.getAccountByUserId(currentUser, Math.toIntExact(currentUser.getUser().getId())).getAccountId();
//        for(Transfer transfer: transfers) {
//            consoleService.printMessage(transfer.getTransferID()+"      "+consoleService.typeFormattingRequestDisplay(transfer.getTransferTypeID())+ /*<--spacing done in formatting just need the user that isnt current user*/"         "+transfer.getTransferAmt());
//
//
//        }
//
//        int transferIdChoice = consoleService.promptForInt("\nEnter transfer ID to view details - otherwise press 0 to cancel.");
//        Transfer transferChoice = transferIdValidation(transferIdChoice, transfers, currentUser);
//        if(transferChoice !=null){
//            printTransferDeets(currentUser, transferChoice);
//        }
// WONT_COMPILE_END

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
        String transactionType = transferService.determineTransferType(transactionTypeId);
        String transactionStatus = transferService.determineTransferStatus(transactionStatusId);

        consoleService.printTransferDeets(id, sendingUserName, receivingUserName, transactionType, transactionStatus, amount);
    }

    private void viewPendingRequests() {
// WONT_COMPILE_BEGIN
//
//		// TODO Auto-generated method stub
//        Transfer[] transfers = transferService.getTransferFromUserId(currentUser, Math.toIntExact(currentUser.getUser().getId()));
//       consoleService.pendingRequestHeader();
//
//        for(Transfer transfer: transfers) {
//            if (transfer.getTransferStatusID() == 1) {
//                printTransferDeets(currentUser, transfer);
//            }
//        }
//        int transferIdChoice = consoleService.promptForInt("\nEnter transfer ID to approve or reject - otherwise press 0 to cancel.");
//        Transfer transferChoice = transferIdValidation(transferIdChoice, transfers, currentUser);
//        if(transferChoice !=null){
//            approvalProcess(currentUser, transferChoice);
//        }
// WONT_COMPILE_END
  }

	private void sendBucks() {
        Map<Long,PublicUserInfoDTO> users = accountService.getUsers(currentUser);
        if (users == null) {

            consoleService.printErrorMessage();
            System.out.println("Unable to send TE bucks.");
        }
        else {
            Map<Long,PublicUserInfoDTO> otherUsers = new HashMap<>();
            for (PublicUserInfoDTO user : users.values()) {
                if (user.getId() != currentUser.getUser().getId()) {
                    otherUsers.put(user.getId(), user);
                }
            }
            if (otherUsers.isEmpty()) {
                consoleService.printNobodyToTransferMoneyWithMessage();
            }
            else {
                consoleService.printOtherUserSelectionMenu(otherUsers.values());

                long userSelection = consoleService.promptForInt("Please enter ID for recipient - otherwise select 0 to cancel.");
                if(userValidation(userSelection, users)) {
                    BigDecimal chooseAmount = consoleService.promptForBigDecimal("How much are you sending?");

                    Account fromAccount = accountService.getAccountByUserId(currentUser,
                            currentUser.getUser().getId());
                    if (fromAccount == null) {
                        // TODO Handle this situation better
                        consoleService.printMessage("Couldn't retrieve your account information.");
                        return;
                    }

                    if (chooseAmount.compareTo(fromAccount.getBalance()) > 0) {
                        // TODO Handle this situation better
                        consoleService.printMessage("Insufficient funds.");
                        return;
                    }

                    Account toAccount = accountService.getAccountByUserId(currentUser, userSelection);
                    if (toAccount == null) {
                        // TODO Handle this situation better
                        consoleService.printMessage("Couldn't account information for recipient.");
                        return;
                    }

                   Transfer t1 = new Transfer();
                  t1.setFromAccountID(fromAccount.getAccountId());//getting current user account ID
                  t1.setTransferAmt(chooseAmount); //setting our prompted bigdecimal to transfer object
                  t1.setToAccountID(toAccount.getAccountId());  //setting recipient account ID
                  t1.setTransferTypeID(2);    //sending money is transfer type 2
                  t1.setTransferStatusID(2);  //all sent funds are default approved, status type 2

                // TODO Have server respond to the transfer creation by actually performing the transfer because the transfer status is already approved
                  transferService.createTransfer(currentUser, t1);
                }
            }
        }

	}


    private boolean userValidation(long userSelection, Map<Long,PublicUserInfoDTO> users) {
        if (!users.containsKey(userSelection)) {
            consoleService.printMessage("Invalid Selection");
            return false;
        }
        return true;
    }

    private void requestBucks() {
        Map<Long,PublicUserInfoDTO> users = accountService.getUsers(currentUser);

        if (users == null) {
            consoleService.printErrorMessage();
            System.out.println("Unable to request TE bucks.");
        }
        else {
            Map<Long,PublicUserInfoDTO> otherUsers = new HashMap<>();
            for (PublicUserInfoDTO user : users.values()) {
                if (user.getId() != currentUser.getUser().getId()) {
                    otherUsers.put(user.getId(), user);
                }
            }
            if (otherUsers.isEmpty()) {
                consoleService.printNobodyToTransferMoneyWithMessage();
            } else {
                consoleService.printOtherUserSelectionMenu(otherUsers.values());
                // TODO Implement the rest (prompt for user and request $ from that user)
                long userSelection = consoleService.promptForInt("Please enter ID for recipient - otherwise select 0 to cancel.");
                if (userValidation(userSelection, users)) {
                    BigDecimal chooseAmount = consoleService.promptForBigDecimal("How much are you asking for?");

                    Account fromAccount = accountService.getAccountByUserId(currentUser, userSelection);
                    if (fromAccount == null) {
                        // TODO Handle this situation better
                        consoleService.printMessage("Couldn't account information for recipient.");
                        return;
                    }

                    Account toAccount = accountService.getAccountByUserId(currentUser,
                            currentUser.getUser().getId());
                    if (toAccount == null) {
                        // TODO Handle this situation better
                        consoleService.printMessage("Couldn't retrieve your account information.");
                        return;
                    }

                    Transfer t1 = new Transfer();
                    t1.setFromAccountID(fromAccount.getAccountId());  //setting user ID from the account we want to take from
                    t1.setTransferAmt(chooseAmount); //setting our prompted bigdecimal to transfer object
                    t1.setToAccountID(toAccount.getAccountId());//getting current user account ID
                    t1.setTransferTypeID(1);    //requesting money is type 1
                    t1.setTransferStatusID(1);  //status type is 1 for pending
                    transferService.createTransfer(currentUser,t1);
                }
            }
        }
    }

    public void approvalProcess(AuthenticatedUser authenticatedUser, Transfer pendingTransfer ){
            consoleService.printApprovalOption();
            int choice= consoleService.promptForInt("Please choose an option: ");
                if (choice==1){
                    //int transferStatusID = transferService.getTransferStatus(currentUser, "Approved").getTransferStatusID();
                    pendingTransfer.setTransferStatusID(choice);
                    System.out.println(transferService.determineTransferStatus(choice));
                }
                else if (choice==2){
                   // int transferStatusID = transferService.getTransferStatus(currentUser, "Rejected").getTransferStatusID();
                    pendingTransfer.setTransferStatusID(choice);
                    System.out.println(transferService.determineTransferStatus(choice));
                }
                else if(choice == 3) {
                    consoleService.printMessage("No changes will be made to this transfer");
                    consoleService.printMainMenu();
                }
                try{
                    transferService.updateTransfer(authenticatedUser, pendingTransfer);
                }catch (Exception e){
                    System.out.println("Error attempting to respond to transfer request "+ e.getMessage());

                }


        }
}