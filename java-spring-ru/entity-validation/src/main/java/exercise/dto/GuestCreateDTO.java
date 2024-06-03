package exercise.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

// BEGIN
@Getter
@Setter
public class GuestCreateDTO {
    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 11, max = 13)
    @Pattern(regexp = "^\\+\\d+$")
    private String phoneNumber;

    @NotNull
    @Size(min = 4, max = 4)
    private String clubCard;

    @NotNull
    @Future
    private LocalDate cardValidUntil;
}
// END
