package com.techelevator.tenmo.services;
import com.techelevator.tenmo.model.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class UserService {
    private static RestTemplate restTemplate;
    private static final String BASEURL = "http://localhost:8080/tenmo/";

    public UserService(){
        this.restTemplate = new RestTemplate();
    }

    private static HttpEntity genEntity(AuthenticatedUser authenticatedUser){
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }

    public User[] getAllUsers(AuthenticatedUser authenticatedUser) {
        User[] users = null;
        try{
            users = restTemplate.exchange(BASEURL + "users",
                    HttpMethod.GET,
                    genEntity(authenticatedUser),
                    User[].class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Request failed. Code:" + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("System is unavailable at this time. Please try again later.");
        }
        return users;
    }

    public static User getUserViaUserId(AuthenticatedUser authenticatedUser, int id) {
        User user = null;
        try{
            user = restTemplate.exchange(BASEURL + "user/"+id,
                    HttpMethod.GET,
                    genEntity(authenticatedUser),
                    User.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Request failed. Code:" + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("System is unavailable at this time. Please try again later.");
        }
        return user;
    }

    public User findUsernameByAccountID(AuthenticatedUser authenticatedUser, int accountID) {
        User user = null;
        try{
            user = restTemplate.exchange(BASEURL + "userbyaccountid/" +accountID,
                    HttpMethod.GET,
                    genEntity(authenticatedUser),
                    User.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Request failed. Code:" + e.getRawStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("System is unavailable at this time. Please try again later.");
        }
        return user;
    }


}
