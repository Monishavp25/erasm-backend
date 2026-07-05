package com.erasm.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JwtResponse {
    private String token;
    private String tokenType = "Bearer";
    private String email;
    private String role;
}
