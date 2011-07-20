package play.modules.associations.list;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class ListBook {
    @Id
    public String title;

    @ManyToMany(mappedBy = "books")
    public List<ListAuthor> authors;

    @ManyToOne
    public ListLibrary library;

    public ListBook(String title) {
        this.title = title;
    }

    public String toString() {
        return super.toString() + "(" + title + ")";
    }

}
