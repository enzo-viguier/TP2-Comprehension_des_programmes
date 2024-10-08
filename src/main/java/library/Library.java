package library;

import java.util.ArrayList;
import java.util.List;

class Library {
    private List<Book> books;

    public Library() {
        this.books = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public Book findBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                return book;
            }
        }
        return null;
    }

    public boolean checkOutBook(String title) {
        Book book = findBook(title);
        if (book != null && !book.isCheckedOut()) {
            book.checkOut();
            return true;
        }
        return false;
    }

    public boolean returnBook(String title) {
        Book book = findBook(title);
        if (book != null && book.isCheckedOut()) {
            book.returnBook();
            return true;
        }
        return false;
    }

    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : books) {
            if (!book.isCheckedOut()) {
                availableBooks.add(book);
            }
        }
        return availableBooks;
    }

    public void printLibraryStatus() {
        System.out.println("Library Status:");
        for (Book book : books) {
            System.out.println(book);
        }
    }
}