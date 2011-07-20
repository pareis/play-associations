package play.modules.associations.set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.Set;

@Entity
public class SetBook {
    @Id
    public String title;

    @ManyToMany(mappedBy = "books")
    public Set<SetAuthor> authors;

    @ManyToOne
    public SetLibrary library;

    public SetBook(String title) {
        this.title = title;
    }

    public String toString() {
        return super.toString() + "(" + title + ")";
    }

}
