package exercise.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Table(name = "guests")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Guest {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    // BEGIN
    @NotNull
    @NotEmpty
    private String name;

    @Column(unique = true)
    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 11, max = 13)
    @Pattern(regexp = "^\\+\\d+$")
    private String phoneNumber;

    @Column(unique = true)
    @NotNull
    @Size(min = 4, max = 4)
    private String clubCard;

    @NotNull
    @Future
    private LocalDate cardValidUntil;
    // END

    @CreatedDate
    private LocalDate createdAt;
}
