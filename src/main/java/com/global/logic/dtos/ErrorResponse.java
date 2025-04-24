// src/main/java/com/tuorganizacion/usermanagementservice/dto/ErrorResponse.java
package com.global.logic.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private List<ErrorDetail> error;
}
