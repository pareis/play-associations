package play.modules.associations.list;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
public class ListAuthor {

    @Id
    public String name;

    @ManyToMany
    public List<ListBook> books;

    @OneToOne(mappedBy = "director")
    public ListLibrary managedLibrary;


    public ListAuthor(String name) {
        this.name = name;
    }

    public String toString() {
        return super.toString() + "(" + name + ")";
    }
}
