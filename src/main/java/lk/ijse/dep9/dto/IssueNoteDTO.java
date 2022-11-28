package lk.ijse.dep9.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class IssueNoteDTO implements Serializable {

    @Null(message = "Issue note id can't have a value")
    private Integer id;

    @NotNull(message = "Date can't be empty")
    private LocalDate date;

    @NotNull(message = "Member id can't be empty")
    @Pattern(regexp = "^([A-Fa-f0-9]{8}(-[A-Fa-f0-9]{4}){3}-[A-Fa-f0-9]{12})$")
    private String memberId;

    @NotEmpty(message = "Books can't be empty")
    private ArrayList<
            @NotBlank(message = "ISBN can't be a null value",
            @Pattern(regexp = "^(\\d[\\d\\\\-]*\\d)$", message = "Invalid ISBN"))String> books = new ArrayList<>();

}
