package lk.ijse.dep9.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO implements Serializable {
//    fields we like to use in data transfer
    @NotBlank(message = "ISBN can't be empty")
    @Pattern(regexp = "([0-9][0-9\\\\-]*[0-9])", message = "Invalid JSON")
    private String isbn;

    @NotBlank(message = "Title can't be empty")
    @Pattern(regexp = ".+", message = "Invalid Title")
    private String title;

    @NotBlank(message = "Author can't be empty")
    @Pattern(regexp = "[A-Za-z ]+", message = "Iinvalid Author")
    private String author;

    @NotNull(message = "Copies can't be empty")
    @Min(message = "Copies can't be zero or negative", value = 1)
    private Integer copies;

}
