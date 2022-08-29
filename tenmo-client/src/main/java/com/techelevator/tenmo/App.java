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
    private final UserService userService = new UserService();

    private TransferService transferService = new TransferService(API_TENMO_BASE_URL);

    private AuthenticatedUser currentUser;
    private AuthenticatedUser otherUser;
    private Account otherAccount;

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
        // TODO Auto-generated method stub
        Transfer[] transfers = transferService.getAllTransfers(currentUser);
        if (transfers == null) {
            System.out.println("No transfers to display.");
        } else {
            consoleService.printTransferDeetsShortHeader();
            for (Transfer list : transfers) {
                //User[] userList = userService.getAllUsers(currentUser);
                int currentUserAccountId = accountService.getAccountByUserId(currentUser, Math.toIntExact(currentUser.getUser().getId())).getAccountId();
                // int otherUserAccountId = accountService.getAccountByUserId(currentUser, Math.toIntExact(otherUser.getUser().getId())).getAccountId();
                if (list.getToAccountID() == currentUserAccountId) {
                    //if to you id =2 print From: account
                    User user = userService.findUsernameByAccountID(currentUser, list.getFromAccountID());
                    //Account otherAccount = accountService.findUsernameByAccountID(currentUser, otherAccountID);
//                    for(Account nameFind: otherAccount)
//                        if (otherAccountID == nameFind.getAccountId())
                    // otherAccount = accountService
//                          int tempID = otherAccount.getUserId();
//                          accountService.getAccountByUserId(currentUser, tempID);
                    consoleService.printMessage(consoleService.padRight(String.valueOf(list.getTransferID()), 12) + consoleService.padRight("From:", 6) +
                            consoleService.padRight(user.getUsername(), 17) + "$" + consoleService.padLeft(String.valueOf(list.getTransferAmt()), 7));
                } else if (list.getFromAccountID() == currentUserAccountId) {
                    User user = userService.findUsernameByAccountID(currentUser, list.getToAccountID());

                    consoleService.printMessage(consoleService.padRight(String.valueOf(list.getTransferID()), 12) + consoleService.padRight("To:", 6) +
                            consoleService.padRight(user.getUsername(), 17) + "$" + consoleService.padLeft(String.valueOf(list.getTransferAmt()), 7));
                } else {
                    consoleService.printMessage("Error populating transfer history");
                }
            }
        }
        // for(Transfer transfer: transfers) {
        //   consoleService.printMessage(transfer.getTransferID()+"      "+consoleService.typeFormattingRequestDisplay(transfer.getTransferTypeID())+ /*<--spacing done in formatting just need the user that isnt current user*/"         "+transfer.getTransferAmt());

        int transferIdChoice = consoleService.promptForInt("\nEnter transfer ID to view details - otherwise press 0 to cancel.");
        Transfer transferChoice = transferIdValidation(transferIdChoice, transfers, currentUser);
        if (transferChoice != null) {
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
        String transactionType = transferService.determineTransferType(transactionTypeId);
        String transactionStatus = transferService.determineTransferStatus(transactionStatusId);

        consoleService.printTransferDeets(id, sendingUserName, receivingUserName, transactionType, transactionStatus, amount);
    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub
        Transfer[] transfers = transferService.getUnresolvedTransfersViaUserId(currentUser);
        consoleService.pendingRequestHeader();

        for (Transfer transfer : transfers) {
            if (transfer.getTransferStatusID() == 1) {
                printTransferDeets(currentUser, transfer);
            }
        }
        int transferIdChoice = consoleService.promptForInt("\nEnter transfer ID to approve or reject - otherwise press 0 to cancel.");
        Transfer transferChoice = transferIdValidation(transferIdChoice, transfers, currentUser);
        if (transferChoice != null) {
            approvalProcess(currentUser, transferChoice);
        }

    }

    private void sendBucks() {
        Map<Long, PublicUserInfoDTO> users = accountService.getUsers(currentUser);
        if (users == null) {
            consoleService.printErrorMessage();
            System.out.println("Unable to send TE bucks.");
        } else {
            Map<Long, PublicUserInfoDTO> otherUsers = new HashMap<>();
            for (PublicUserInfoDTO user : users.values()) {
                if (user.getId() != currentUser.getUser().getId()) {
                    otherUsers.put(user.getId(), user);
                }
            }
            if (otherUsers.isEmpty()) {
                consoleService.printNobodyToTransferMoneyWithMessage();
            } else {
                consoleService.printOtherUserSelectionMenu(otherUsers.values());
                long userSelection = consoleService.promptForInt("Please enter ID for recipient - otherwise select 0 to cancel.");
                if (userValidation(userSelection, users, currentUser)) {
                    BigDecimal chooseAmount = consoleService.promptForBigDecimal("How much are you sending?");
                    createTransfer((int) userSelection, chooseAmount, "Send", "Approved");
                }
            }
        }
    }

    private boolean userValidation(long userSelection, Map<Long, PublicUserInfoDTO> users, AuthenticatedUser currentUser) {
        //commenting out undefined exceptions, temporarily setting userValidation to always return "true" (Don T)

//        if(userSelection !=0){
//            try{
//                boolean validUserId = false;
//                if(userSelection == currentUser.getUser().getId()){
//                    throw new InvalidUserSelectionException();
//                }
//                else if (users.containsKey(userSelection)){
//                    validUserId = true;
//                }
//                if(validUserId == false){
//                    throw new AccountNotFoundException();
//                }
//                return true;
//            } catch (InvalidUserSelectionException e) {
//                throw new RuntimeException(e);
//            } catch (AccountNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }
        return true;
    }

    private Transfer transferIdValidation(int transferIdChoice, Transfer[] transfers, AuthenticatedUser currentUser) {
        Transfer userChoice = null;
        if (transferIdChoice != 0) {
            try {
                boolean validID = false;
                for (Transfer transfer : transfers) {
                    if (transfer.getTransferID() == transferIdChoice) {
                        validID = true;
                        userChoice = transfer;
                    } else if (!validID) {
                        throw new Exception();  //InvalidTransferIdValidation();
                    }
                }

            } catch (Exception e) { //InvalidTransferIdValidation e){
                System.out.println(e.getMessage());
            }
        }
        return userChoice;
    }

    private void requestBucks() {
        Map<Long, PublicUserInfoDTO> users = accountService.getUsers(currentUser);
        if (users == null) {
            consoleService.printErrorMessage();
            System.out.println("Unable to request TE bucks.");
        } else {
            Map<Long, PublicUserInfoDTO> otherUsers = new HashMap<>();
            for (PublicUserInfoDTO user : users.values()) {
                if (user.getId() != currentUser.getUser().getId()) {
                    otherUsers.put(user.getId(), user);
                }
            }
            if (otherUsers.isEmpty()) {
                consoleService.printNobodyToTransferMoneyWithMessage();
            } else {
                consoleService.printOtherUserSelectionMenu(otherUsers.values());
                long userSelection = consoleService.promptForInt("Please enter ID for recipient - otherwise select 0 to cancel.");
                if (userValidation(userSelection, users, currentUser)) {
                    BigDecimal chooseAmount = consoleService.promptForBigDecimal("How much are you asking for?");
                    createTransfer((int) userSelection, chooseAmount, "Request", "Pending");
                }
            }
        }
    }

    public void approvalProcess(AuthenticatedUser authenticatedUser, Transfer pendingTransfer) {
        consoleService.printApprovalOption();
        int choice = consoleService.promptForInt("Please choose an option: ");
        if (choice == 1) {
            //int transferStatusID = transferService.getTransferStatus(currentUser, "Approved").getTransferStatusID();
            pendingTransfer.setTransferStatusID(choice);
            System.out.println(transferService.determineTransferStatus(choice));
        } else if (choice == 2) {
            // int transferStatusID = transferService.getTransferStatus(currentUser, "Rejected").getTransferStatusID();
            pendingTransfer.setTransferStatusID(choice);
            System.out.println(transferService.determineTransferStatus(choice));
        } else if (choice == 3) {
            consoleService.printMessage("No changes will be made to this transfer");
            consoleService.printMainMenu();
        }
        try {
            transferService.updateTransfer(authenticatedUser, pendingTransfer);
        } catch (Exception e) {
            System.out.println("Error attempting to respond to transfer request " + e.getMessage());

        }


    }

    private Transfer createTransfer(int accountChoiceUserId, BigDecimal amount, /*String amountString,*/ String transferType, String status) {
        int transferTypeId = transferService.getTransferType(currentUser, transferType).getTransferTypeId();
        int transferStatusId = transferService.getTransferStatus(currentUser, status).getTransferStatusId();
        int accountToId;
        int accountFromId;
        Account toAccount;
        Account fromAccount;
        if (transferType.equals("Send")) {
//            accountToId = accountService.getAccountByUserId(currentUser, accountChoiceUserId).getAccountId();
//            accountFromId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
            toAccount = accountService.getAccountByUserId(currentUser, accountChoiceUserId);
            if (toAccount == null) {
                // TODO Handle this situation better
                consoleService.printMessage("Couldn't account information for recipient.");
                return null;
            }
            fromAccount = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId());
            if (fromAccount == null) {
                // TODO Handle this situation better
                consoleService.printMessage("Couldn't retrieve your account information.");
                return null;
            }
        } else {
//            accountToId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
//            accountFromId = accountService.getAccountByUserId(currentUser, accountChoiceUserId).getAccountId();
            toAccount = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId());
            if (toAccount == null) {
                // TODO Handle this situation better
                consoleService.printMessage("Couldn't retrieve your account information.");
                return null;
            }
            fromAccount = accountService.getAccountByUserId(currentUser, accountChoiceUserId);
            if (fromAccount == null) {
                // TODO Handle this situation better
                consoleService.printMessage("Couldn't retrieve account information for sender.");
                return null;
            }
        }
        accountToId = toAccount.getAccountId();
        accountFromId = fromAccount.getAccountId();
//        BigDecimal amount = new BigDecimal(amountString);
        if (amount.compareTo(fromAccount.getBalance()) > 0) {
            // TODO Handle this situation better
            consoleService.printMessage("Insufficient funds.");
            return null;
        }
        Transfer transfer = new Transfer();
        transfer.setFromAccountID(accountFromId);
        transfer.setToAccountID(accountToId);
        transfer.setTransferAmt(amount);
        transfer.setTransferStatusID(transferStatusId);
        transfer.setTransferTypeID(transferTypeId);
        transferService.createTransfer(currentUser, transfer);
        return transfer;
    }
}