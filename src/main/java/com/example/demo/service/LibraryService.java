package com.example.demo.service;

import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.Issue;
import com.example.demo.entity.Student;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LibraryService {
    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;
    private final StudentRepository studentRepo;
    private final IssueRepository issueRepo;

    public LibraryService (BookRepository bookRepo, AuthorRepository authorRepo, StudentRepository studentRepo, IssueRepository issueRepo) {
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
        this.studentRepo = studentRepo;
        this.issueRepo = issueRepo;
    }

    public void addBook(String isbn, String title, String category, String authorName, String email, int quantity) {
        Book book = new Book(isbn, title, category, quantity);
        Author author = new Author(book, authorName, email);

        authorRepo.save(author);
        bookRepo.save(book);
    }

    public List<Book> searchByTitle(String title) {
        return bookRepo.findByTitle(title);
    }

    public List<Book> searchByCategory(String category) {
        return bookRepo.findByCategory(category);
    }

    public List<Book> searchByAuthor(String name) {
        List<Author> authors = authorRepo.findByName(name);
        return authors.stream()
                .map(Author::getAuthorBook)
                .toList();
    }

    public List<Book> listAllBooks() {
        return bookRepo.findAll();
    }

    public String issueBook(String usn, String name, String isbn) {
        Book book = bookRepo.findById(isbn).orElse(null);

        if (book == null || book.getQuantity() <= 0) {
            return "Book not available";
        }

        Student student = new Student(usn, name);
        studentRepo.save(student);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime returnDate = now.plusDays(7);

        Issue issue = new Issue(
                now.toString(),
                returnDate.toString(),
                student,
                book
        );

        issueRepo.save(issue);

        book.setQuantity(book.getQuantity() - 1);
        bookRepo.save(book);

        return "Book issued. Return date: " + returnDate;
    }
    public List<Book> listBooksByUsn(String usn) {
        List<Issue> issues = issueRepo.findByIssueStudent_Usn(usn);

        return issues.stream()
                .map(Issue::getIssueBook)
                .toList();
    }
}
