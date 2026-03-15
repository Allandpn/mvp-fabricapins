package com.finalphase.fabricapins.config.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public String getLoggedUsername(){
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    public boolean isAdmin() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public void validateSelfOrAdmin(String username){
        if(!username.equals(getLoggedUsername())
                && !isAdmin()){
            throw new AccessDeniedException("Acesso negado");
        }
    }

}
