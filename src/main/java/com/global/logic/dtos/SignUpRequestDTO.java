package com.global.logic.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@NoArgsConstructor
public class SignUpRequestDTO {

    private String name;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "El formato del email es inválido. Debe ser como aaaaaaa@undominio.algo")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Pattern(regexp = "^(?=.*[A-Z])(?=(?:\\D*\\d){2}\\D*$)[A-Za-z\\d]{8,12}$", message = "La contraseña debe tener entre 8 y 12 caracteres, contener exactamente una letra mayúscula y exactamente dos números.")
    private String password;

    @Valid
    private List<PhoneDTO> phones;
}
