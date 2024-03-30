package demo.project_crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import demo.project_crud.entities.Book.Book;

@Repository
public interface BookRepo extends JpaRepository<Book, Integer> {

}
