package demo.project_crud.entities.Book;

import demo.project_crud.entities.BaseEntity;
import jakarta.persistence.Entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Table(name = "tbImage")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Images extends BaseEntity {

    private String url;
    private boolean isMain;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
