package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.PublicUserInfoDTO;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import com.techelevator.tenmo.model.Account;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AccountService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url) {
        this.baseUrl = url;
    }

    public BigDecimal getBalance(AuthenticatedUser user) {
        BigDecimal balance = null;
        try {
            balance = restTemplate.exchange(baseUrl + "getbalance", HttpMethod.GET, makeAuthEntity(user),
                    BigDecimal.class).getBody();
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public Map<Long,PublicUserInfoDTO> getUsers(AuthenticatedUser user) {
        Map<Long,PublicUserInfoDTO> userInfos = null;
        try {
            PublicUserInfoDTO[] userInfoArray = restTemplate.exchange(baseUrl + "getusers", HttpMethod.GET, makeAuthEntity(user),
                    PublicUserInfoDTO[].class).getBody();
            userInfos = new HashMap<>();
            for (PublicUserInfoDTO userInfo : userInfoArray) {
                userInfos.put(userInfo.getId(), userInfo);
            }
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return userInfos;
    }

    private HttpEntity<Void> makeAuthEntity(AuthenticatedUser user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        return new HttpEntity<>(headers);
    }

    public Account getAccountByUserId(AuthenticatedUser authenticatedUser, int userId) {
        Account account = null;
        try{
            account = restTemplate.exchange(baseUrl + "account/user/" + userId,
                    HttpMethod.GET,
                    makeAuthEntity(authenticatedUser),
                    Account.class).getBody();
        }catch(RestClientResponseException e){
            System.out.println("Could not complete request. Code:" + e.getRawStatusCode());
        }catch(ResourceAccessException e) {
            System.out.println("Request has failed. Please try again later.");
        }
        return account;
    }

//    public Account findUsernameByAccountID(AuthenticatedUser authenticatedUser, int accountID){
//        Account account = null;
//
//        try{
//            account= restTemplate.exchange(baseUrl + "account/" + accountID,
//                    HttpMethod.GET,
//                    makeAuthEntity(authenticatedUser),
//                    Account.class).getBody();
//        }catch(RestClientResponseException e){
//            System.out.println("Could not complete request. Code:" + e.getRawStatusCode());
//        }catch(ResourceAccessException e) {
//            System.out.println("Request has failed. Please try again later.");
//        }
//        return account;
//    }

}