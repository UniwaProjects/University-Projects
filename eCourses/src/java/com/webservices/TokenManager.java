package com.webservices;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TokenManager {

    private static List<String> tokens = new ArrayList<>();

    public TokenManager() {
    }

    public String issueToken() {
        String token = UUID.randomUUID().toString();
        tokens.add(token);
        printTokens();
        return token;
    }

    public void removeToken(String token) {
        printTokens();
        tokens.remove(token);
    }
    
    public void clearTokens(){
        for (String t : tokens) {
            tokens.remove(t);
        }
    }

    public void printTokens() {
        System.out.println("*****TOKEN  LIST*****");
        for (String t : tokens) {
            System.out.println(t);
        }
    }

}
