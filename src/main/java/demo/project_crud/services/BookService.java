package demo.project_crud.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import demo.project_crud.DTO.book.BookRequest;
import demo.project_crud.DTO.book.BookResponse;
import demo.project_crud.entities.Book.Book;
import demo.project_crud.mapper.BookMapper;
import demo.project_crud.repository.BookRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class BookService {

    private final BookRepo bookRepo;
    private final BookMapper bookMapper;
    private final ModelMapper modelMapper;

    public List<BookResponse> gets() {
        return bookRepo.findAll()
                .stream()
                .map(bookMapper::toBookResponse)
                .collect(Collectors.toList());
    }

    public BookResponse get(int id) {
        var book = bookRepo.findById(id).orElse(null);
        return modelMapper.map(book, BookResponse.class);
    }

    @SuppressWarnings("null")
    public Book create(BookRequest model) {

        try {
            var book = modelMapper.map(model, Book.class);
            return bookRepo.save(book);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean delete(int id) {
        try {
            bookRepo.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean update(BookRequest model, int id) {
        try {
            Book book = (modelMapper.map(model, Book.class));
            book.setId(id);
            bookRepo.save(book);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
