package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
        App app = new App();
        app.run();
    }

    private void run() throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
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

    private void handleRegister() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        consoleService.printMessage("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            consoleService.printMessage("Registration successful. You can now login.");
            Sounds.playSound("happynoise.wav");
        } else {
            consoleService.printErrorMessage();
            Sounds.playSound("Narf.wav");
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
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
                System.out.println("Thanks for using the TEnmo Application!");
                Sounds.playSound("Shutdown.wav");
                TimeUnit.SECONDS.sleep(2); //allows shutdown sound to play fully
                continue;
            } else {
                consoleService.printMessage("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() throws UnsupportedAudioFileException, LineUnavailableException, IOException { //each starts with 1,000.00.
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
//        Transfer transferChoice = transferIdValidation(transferIdChoice, transfers, currentUser);
        Transfer transferChoice = transferService.getTransferFromTransferId(currentUser,transferIdChoice);
        if (transferChoice != null) {
            printTransferDeets(currentUser, transferChoice);
        }
    }


    private void printTransferDeets(AuthenticatedUser authenticatedUser, Transfer transferChoice) {
       // Transfer currentTransfer = transferService.getTransferFromTransferId(currentUser, transferChoice.getTransferID());
        int id = transferChoice.getTransferID();
        BigDecimal amount = transferChoice.getTransferAmt();
        int sendingAccount = transferChoice.getFromAccountID();
        int receivingAccount = transferChoice.getToAccountID();
        int transactionTypeId = transferChoice.getTransferTypeID();
        int transactionStatusId = transferChoice.getTransferStatusID();
//        int id = currentTransfer.getTransferID();
//        BigDecimal amount = currentTransfer.getTransferAmt();
//        int sendingAccount = currentTransfer.getFromAccountID();
//        int receivingAccount = currentTransfer.getToAccountID();
//        int transactionTypeId = currentTransfer.getTransferTypeID();
//        int transactionStatusId = currentTransfer.getTransferStatusID();

//        int sendingUserId = accountService.getAccountByUserId(currentUser, sendingAccount).getUserId();
//        String sendingUserName = UserService.getUserViaUserId(currentUser, sendingUserId).getUsername();
        User sendingUser= userService.findUsernameByAccountID(currentUser,transferChoice.getFromAccountID());
            String sendingUserName = sendingUser.getUsername();
//        int receivingUserId = accountService.getAccountByUserId(currentUser, receivingAccount).getUserId();
//        String receivingUserName = UserService.getUserViaUserId(currentUser, receivingUserId).getUsername();
        User recievingUser = userService.findUsernameByAccountID(currentUser,transferChoice.getToAccountID());
        String receivingUserName = recievingUser.getUsername();
        //TODO add types and status for transfer/transaction
        String transactionType = transferService.determineTransferType(transactionTypeId);
        String transactionStatus = transferService.determineTransferStatus(transactionStatusId);

        consoleService.printTransferDeets(id, sendingUserName, receivingUserName, transactionType, transactionStatus, amount);
    }

    private void viewPendingRequests() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        // TODO Auto-generated method stub
        int currentUserAccountId = accountService.getAccountByUserId(currentUser, Math.toIntExact(currentUser.getUser().getId())).getAccountId();

        Transfer[] transfers = transferService.getAllTransfers(currentUser);
        List<Transfer> pendingRequests = new ArrayList<>();
        consoleService.pendingRequestHeader();
        if (transfers == null) {
            System.out.println("No transfers to display.");
        } else {

            for (Transfer transfer : transfers) {
                if (transfer.getTransferStatusID() == 1) {
                    User user = userService.findUsernameByAccountID(currentUser, transfer.getFromAccountID());

                    consoleService.printMessage(consoleService.padRight(String.valueOf(transfer.getTransferID()), 12) + consoleService.padRight("From:", 6) +
                            consoleService.padRight(user.getUsername(), 17) + "$" + consoleService.padLeft(String.valueOf(transfer.getTransferAmt()), 7));                }
            }
            int transferIdChoice = consoleService.promptForInt("\nEnter transfer ID to approve or reject - otherwise press 0 to cancel.");
            Transfer transferChoice = transferService.getTransferFromTransferId(currentUser,transferIdChoice);
//                pendingRequests.add(transferChoice);
//            Transfer transferChoice = transferIdValidation(transferIdChoice, transfers, currentUser);

            if (transferChoice != null) {
                approvalProcess(currentUser, transferChoice);
            }
        }
    }

    private void sendBucks() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
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
                Sounds.playSound("TPIRloss.wav");
            } else {
                consoleService.printOtherUserSelectionMenu(otherUsers.values());
                long userSelection = consoleService.promptForInt("Please enter ID for recipient - otherwise select 0 to cancel.");
                if (userValidation(userSelection, users, currentUser)) {
                    BigDecimal chooseAmount = consoleService.promptForBigDecimal("How much are you sending? $");
                    createTransfer((int) userSelection, chooseAmount, "Send", "Approved");
                    System.out.println("Successfully Sent!");
                    Sounds.playSound("LTTP_World_Warp.wav");
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

    private void requestBucks() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
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
                Sounds.playSound("TPIRloss.wav");
            } else {
                consoleService.printOtherUserSelectionMenu(otherUsers.values());
                long userSelection = consoleService.promptForInt("Please enter ID to request money from - or enter '0' to cancel.");
                if (userValidation(userSelection, users, currentUser)) {
                    BigDecimal chooseAmount = consoleService.promptForBigDecimal("How much are you asking for? $");
                    createTransfer((int) userSelection, chooseAmount, "Request", "Pending");
                    Sounds.playSound("LTTP_Warp.wav");
                    System.out.println("Request sent. Check back later to see if it was approved.");

                }
            }
        }
    }

    public void approvalProcess(AuthenticatedUser authenticatedUser, Transfer pendingTransfer) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        consoleService.printApprovalOption();
        int choice = consoleService.promptForInt("Please choose an option: ");
        if (choice == 1) {
            //int transferStatusID = transferService.getTransferStatus(currentUser, "Approved").getTransferStatusID();
            pendingTransfer.setTransferStatusID(TransferStatus.STATUS_ID_APPROVED);
            Sounds.playSound("CoinPayout.wav");
//            transferService.updateTransfer(currentUser, pendingTransfer);
           // System.out.println(transferService.determineTransferStatus(choice));
        } else if (choice == 2) {
            // int transferStatusID = transferService.getTransferStatus(currentUser, "Rejected").getTransferStatusID();
            pendingTransfer.setTransferStatusID(TransferStatus.STATUS_ID_REJECTED);
            Sounds.playSound("sadnoise.wav");
//            transferService.updateTransfer(currentUser, pendingTransfer);

            //System.out.println(transferService.determineTransferStatus(choice));
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

    private Transfer createTransfer(int accountChoiceUserId, BigDecimal amount, /*String amountString,*/ String transferType, String status) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
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
                consoleService.printMessage("Couldn't find account information for recipient.");
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
            Sounds.playSound("TPIRloss.wav");
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