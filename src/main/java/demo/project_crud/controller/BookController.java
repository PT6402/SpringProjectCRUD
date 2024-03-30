package demo.project_crud.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.project_crud.DTO.book.BookRequest;
import demo.project_crud.services.BookService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<?> books() {
        var list = bookService.gets();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> book(@PathVariable int id) {
        var book = bookService.get(id);

        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return new ResponseEntity<>("not found", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BookRequest request) {
        var book = bookService.create(request);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(@RequestBody BookRequest request, @PathVariable int id) {
        boolean check = bookService.update(request, id);
        if (check) {
            return ResponseEntity.ok("update success");
        } else {
            return new ResponseEntity<>("update fail", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        boolean check = bookService.delete(id);
        if (check) {
            return ResponseEntity.ok("delete success");
        } else {
            return new ResponseEntity<>("delete fail", HttpStatus.BAD_REQUEST);
        }

    }
}
