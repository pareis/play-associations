package play.modules.associations;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
public class Library {
    @Id
    public String name;

    @OneToMany
    public List<Author> authors;

    @OneToMany(mappedBy = "library")
    public List<Book> books;


    @OneToOne
    public Author director;

    public Library(String name) {
        this.name = name;
    }

    public String toString() {
        return super.toString() + "(" + name + ")";
    }

}
