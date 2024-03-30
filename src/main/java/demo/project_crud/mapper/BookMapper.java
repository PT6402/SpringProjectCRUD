package demo.project_crud.mapper;

import org.springframework.stereotype.Component;

import demo.project_crud.DTO.book.BookResponse;
import demo.project_crud.entities.Book.Book;

@Component
public class BookMapper {

    public BookResponse toBookResponse(Book model) {
        var bookResponse = BookResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .price(model.getPrice())
                .build();
        return bookResponse;
    }
}
