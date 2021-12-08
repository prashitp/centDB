package com.example.models;

import com.example.models.enums.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String username;

    private String password;

    private Permission permission;

    private Map<String, String> answers;
}
