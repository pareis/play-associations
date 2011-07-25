package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;
import java.util.Set;

@Entity
public class Publisher extends Model {

    
    public String name;

    @OneToMany
    public List<Author> authors;

    @OneToMany(mappedBy = "publisher")
    public Set<Book> publishedBooks;

    @OneToOne
    public Person manager;

    public Publisher(String name) {
        this.name = name;
    }

    public String toString() {
        return super.toString() + "(" + name + ")";
    }

}
