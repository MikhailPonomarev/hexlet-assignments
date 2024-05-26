package exercise.model;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

import lombok.*;

// BEGIN
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"title", "price"})
@Entity
@Table
public class Product {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String title;

    private long price;
}
// END
