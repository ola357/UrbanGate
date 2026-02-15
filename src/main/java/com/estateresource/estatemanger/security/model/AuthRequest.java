package com.estateresource.estatemanger.security.model;

import lombok.Data;


public record AuthRequest(String phoneNumber, String password) {

}
