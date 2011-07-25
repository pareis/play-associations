package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class Book extends Model {
    
    public String title;

    @ManyToMany(mappedBy = "books")
    public List<Author> authors;

    @ManyToOne
    public Publisher publisher;

    public Book(String title) {
        this.title = title;
    }

    public String toString() {
        return super.toString() + "(" + title + ")";
    }

}
