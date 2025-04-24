package com.global.logic.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private UUID id;

    @JsonFormat(pattern = "MMM dd, yyyy hh:mm:ss a")
    private LocalDateTime created;

    @JsonFormat(pattern = "MMM dd, yyyy hh:mm:ss a")
    private LocalDateTime lastLogin;

    private String token;
    private boolean isActive;
    private String name;
    private String email;
    private List<PhoneDTO> phones;
}
