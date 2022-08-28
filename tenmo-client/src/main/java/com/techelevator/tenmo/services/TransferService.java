package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.PublicUserInfoDTO;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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

    private HttpEntity genEntity(AuthenticatedUser authenticatedUser){
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }

    public void createTransfer(AuthenticatedUser authenticatedUser, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);

        String url = baseUrl + "/transfer";

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

    public Transfer[] getTransferFromUserId(AuthenticatedUser authenticatedUser, int userId) {
        Transfer[] transfer = null;
        try {
            transfer = restTemplate.exchange(baseUrl + "/transfers/user/" + userId,
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
        try{
            transfer = restTemplate.exchange(baseUrl + "/transfers/" + id,
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

    public Transfer [] getAllTransfers(AuthenticatedUser authenticatedUser) {
        Transfer[] transfers = new Transfer [0];
        try{
            transfers = restTemplate.exchange(baseUrl + "/transfers",
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

    Transfer [] getUnresolvedTransfersViaUserId(AuthenticatedUser authenticatedUser) {
        Transfer [] transfers = null;
        try{
            transfers = restTemplate.exchange(baseUrl + "/transfers/user/" + authenticatedUser.getUser().getId() + "/pending",
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
    public void updateTransfer(AuthenticatedUser authenticatedUser, Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);

        String url = baseUrl + "/transfers/" + transfer.getTransferID();

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

    public String determineTransferType(int typeID){
        String message = "";
        if(typeID == 1){
            message = "Funds Sent";
        }
        else if(typeID == 2){
            message = "Funds Requested";
        }
        else {
            message = "Error determining request type";
        }
        return message;
    }

    public String determineTransferStatus(int typeID){
        String message = "";
        if(typeID == 1){
            message = "Approval Pending";
        }
        else if(typeID == 2){
            message = "Transfer Approved";
        }
        else if(typeID == 3){
            message = "Transfer Rejected";
        }
        else {
            message = "Error in determining transfer status.";
        }
        return message;
    }

}
