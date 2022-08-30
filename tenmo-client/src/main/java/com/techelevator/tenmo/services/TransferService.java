package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;


public class TransferService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public TransferService(String url) {
        this.baseUrl = url;
    }

    private HttpEntity genEntity(AuthenticatedUser authenticatedUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }

    public void createTransfer(AuthenticatedUser authenticatedUser, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        String url = baseUrl + "transfer";
        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Transfer.class);
        } catch (RestClientResponseException e) {
            if (e.getMessage().contains(
                    // This message matches up with message received in the HTTP response.
                    // See line 6 of InsufficientFundsException.java on server side.
                    "Funds unavailable"
                    // The following message does not make it into the HTTP response.
//                    "Your needs exceed your resources. Please adjust your request."*/
            )) {
                System.out.println("Transaction cannot be completed - insufficient funds available.");
            } else {
                System.out.println("Request failed. Code:" + e.getRawStatusCode());
            }
        } catch (ResourceAccessException e) {
            System.out.println("System is unavailable at this time. Please try again later.");
        }
    }


    public Transfer[] getTransferFromUserId(AuthenticatedUser authenticatedUser, int userId) {
        Transfer[] transfer = null;
        try {
            transfer = restTemplate.exchange(baseUrl + "transfers/user/" + userId,
                    HttpMethod.GET,
                    genEntity(authenticatedUser),
                    Transfer[].class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Request failed. Code:" + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("System is unavailable at this time. Please try again later.");
        }
        return transfer;
    }

    public Transfer getTransferFromTransferId(AuthenticatedUser authenticatedUser, int id) {
        Transfer transfer = null;
        try {
            transfer = restTemplate.exchange(baseUrl + "transfersviaid/" + id,
                    HttpMethod.GET,
                    genEntity(authenticatedUser),
                    Transfer.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Request failed. Code:" + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("System is unavailable at this time. Please try again later.");
        }
        return transfer;
    }

    public Transfer[] getAllTransfers(AuthenticatedUser authenticatedUser) {
        Transfer[] transfers = new Transfer[0];
        try {
            transfers = restTemplate.exchange(baseUrl + "gettransfers",
                    HttpMethod.GET,
                    genEntity(authenticatedUser),
                    Transfer[].class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Request failed. Code:" + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("System is unavailable at this time. Please try again later.");
        }
        return transfers;

    }

    public Transfer[] getUnresolvedTransfersViaUserId(AuthenticatedUser authenticatedUser, int userID) {
        Transfer[] transfers = new Transfer[0];
        try {

            transfers = restTemplate.exchange(baseUrl + "transfers/user/" + userID + "/pending",
                    HttpMethod.GET,
                    genEntity(authenticatedUser),
                    Transfer[].class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Request failed. Code:" + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("System is unavailable at this time. Please try again later.");
        }
        return transfers;
    }

    public void updateTransfer(AuthenticatedUser authenticatedUser, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);

        String url = baseUrl + "transfers/" + transfer.getTransferID();

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Transfer.class);
        } catch (RestClientResponseException e) {
            if (e.getMessage().contains("Your needs exceed your resources. Please adjust your request.")) {
                System.out.println("Transaction cannot be completed - insufficient funds available.");
            } else {
                System.out.println("Request failed. Code:" + e.getRawStatusCode());
            }
        } catch (ResourceAccessException e) {
            System.out.println("System is unavailable at this time. Please try again later.");
        }
    }

    public String determineTransferType(int typeID) {
        String message = "";
        if (typeID == 1) {
            message = "Request";
        } else if (typeID == 2) {
            message = "Send";
        } else {
            message = "Error determining request type";
        }
        return message;
    }

    public String determineTransferStatus(int typeID) {
        String message = "";
        if (typeID == 1) {
            message = "Approval Pending";
        } else if (typeID == 2) {
            message = "Transfer Approved";
        } else if (typeID == 3) {
            message = "Transfer Rejected";
        } else {
            message = "Error in determining transfer status.";
        }
        return message;
    }

    public TransferStatus getTransferStatusById(AuthenticatedUser authenticatedUser, int transferStatusId) {
        TransferStatus transferStatus = null;
        try {
            String url = baseUrl + "transferstatus/" + transferStatusId;
            transferStatus = restTemplate.exchange(url, HttpMethod.GET, genEntity(authenticatedUser), TransferStatus.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("Could not complete request due to server network issue. Please try again.");
        }

        return transferStatus;
    }

    public TransferType getTransferTypeFromId(AuthenticatedUser authenticatedUser, int transferTypeId) {
        TransferType transferType = null;

        try {
            String url = baseUrl + "transfertype/" + transferTypeId;
            transferType = restTemplate.exchange(url, HttpMethod.GET, genEntity(authenticatedUser), TransferType.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("Could not complete request due to server network issue. Please try again.");
        }

        return transferType;
    }

    public TransferStatus getTransferStatus(AuthenticatedUser currentUser, String status) {
        TransferStatus transferStatus = null;
        try {
            String url = baseUrl + "gettransferstatusid/" + status;
            transferStatus = restTemplate.exchange(url, HttpMethod.GET, genEntity(currentUser), TransferStatus.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("Could not complete request due to server network issue. Please try again.");
        }

        return transferStatus;
    }

    public TransferType getTransferType(AuthenticatedUser currentUser, String type) {
        TransferType transferType = null;

        try {
            String url = baseUrl + "gettransfertypeid/" + type;
            transferType = restTemplate.exchange(url, HttpMethod.GET, genEntity(currentUser), TransferType.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Could not complete request. Code: " + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("Could not complete request due to server network issue. Please try again.");
        }

        return transferType;
    }
}