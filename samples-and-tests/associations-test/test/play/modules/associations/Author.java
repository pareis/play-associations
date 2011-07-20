package play.modules.associations;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
public class Author {

    @Id
    public String name;

    @ManyToMany
    public List<Book> books;

    @OneToOne(mappedBy = "director")
    public Library managedLibrary;


    public Author(String name) {
        this.name = name;
    }

    public String toString() {
        return super.toString() + "(" + name + ")";
    }
}
