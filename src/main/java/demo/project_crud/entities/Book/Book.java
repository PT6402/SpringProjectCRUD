package demo.project_crud.entities.Book;

import java.util.List;

import demo.project_crud.entities.BaseEntity;
import demo.project_crud.entities.User.Profile;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Table(name = "tbBook")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)

public class Book extends BaseEntity {

    private String name;
    private double price;
    private String fileBook;

    @OneToMany(mappedBy = "book")
    private List<Images> images;
    @ManyToMany
    @JoinTable(
            name = "profiles_books",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    private List<Profile> profiles;

}
