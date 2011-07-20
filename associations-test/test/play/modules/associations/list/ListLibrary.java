package play.modules.associations.list;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
public class ListLibrary {
    @Id
    public String name;

    @OneToMany
    public List<ListAuthor> authors;

    @OneToMany(mappedBy = "library")
    public List<ListBook> books;


    @OneToOne
    public ListAuthor director;

    public ListLibrary(String name) {
        this.name = name;
    }

    public String toString() {
        return super.toString() + "(" + name + ")";
    }

}
