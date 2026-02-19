package com.ecommerce.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CustomConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final Logger log = LoggerFactory.getLogger(CustomConverter.class);
    @Value("${keycloak.clientId}")
    public String clientId;

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        Map<String, Object> resourceAccess =  jwt.getClaimAsMap("resource_access");

        for(Map.Entry<String, Object> entry : resourceAccess.entrySet()){
            log.info("resource_access: {}", entry.getKey());
            log.info("resource_access: {}", entry.getValue().toString());
        }

        if(resourceAccess == null || !resourceAccess.containsKey(clientId)){
            return List.of();
        }

        Map<String,Object> clientRole = resourceAccess.containsKey(clientId)
                ? (Map<String, Object>) resourceAccess.get(clientId) : null;

        for(Map.Entry<String, Object> entry : clientRole.entrySet()){
            log.info("clientRole: {}", entry.getKey());
            log.info("clientRole: {}", entry.getValue().toString());
        }

        if(clientRole == null){
            return List.of();
        }

        Collection<String> roles = clientRole.containsKey("roles")
                ? (Collection<String>) clientRole.get("roles") : null;


        for(String role : roles){
            log.info("Role found: {}", role);
        }

        if(roles == null){
            return List.of();
        }

        return roles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toList());
    }
}
