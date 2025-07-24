package com.practice.smartcontactmanager.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class sessionHelper {

    public String removeMessageFromSession() {
        try {
            System.out.println("Removing message from session...");
            HttpSession session = ((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes()).getRequest().getSession();
            session.removeAttribute("message");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ""; 
    }
}