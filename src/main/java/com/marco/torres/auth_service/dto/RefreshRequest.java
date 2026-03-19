package com.marco.torres.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefreshRequest {
    private String refreshToken;
}