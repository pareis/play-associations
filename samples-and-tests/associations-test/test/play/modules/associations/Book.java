package play.modules.associations;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class Book {
    @Id
    public String title;

    @ManyToMany(mappedBy = "books")
    public List<Author> authors;

    @ManyToOne
    public Library library;

    public Book(String title) {
        this.title = title;
    }

    public String toString() {
        return super.toString() + "(" + title + ")";
    }

}
