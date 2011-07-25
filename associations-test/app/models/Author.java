package models;



import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
public class Author extends Person {



    @ManyToMany
    public List<Book> books;




    public Author(String name) {
        super(name);
    }

    public String toString() {
        return super.toString() + "(" + name + ")";
    }
}
