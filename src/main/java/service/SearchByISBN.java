package service;

import model.Book;
import java.util.ArrayList;
import java.util.List;

public class SearchByISBN implements SearchStrategy {
    @Override
    public List<Book> search(List<Book> books, String query) {
        List<Book> result = new ArrayList<>();
        String q = query.toLowerCase();
        for (Book b : books) {
            if (b.getIsbn().toLowerCase().contains(q)) {
                result.add(b);
            }
        }
        return result;
    }
}
