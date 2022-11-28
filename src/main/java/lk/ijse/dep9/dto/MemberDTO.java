package lk.ijse.dep9.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO implements Serializable {

    private String id;

    @NotBlank(message = "Name can't be empty")
    @Pattern(regexp = "[A-Za-z ]+", message = "Invalid name")
    private String name;

    @NotBlank(message = "Address can't be empty")
    @Pattern(regexp = "^[A-Za-z0-9| ,.:;#\\/\\\\-]+$", message = "Invalid address")
    private String address;

    @NotBlank(message = "Contact number can't be empty")
    @Pattern(regexp = "\\d{3}-\\d{7}", message = "Invalid contact number")
    private String contact;
}
