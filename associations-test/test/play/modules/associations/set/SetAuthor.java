package play.modules.associations.set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.Set;

@Entity
public class SetAuthor {

    @Id
    public String name;

    @ManyToMany
    public Set<SetBook> books;

    @OneToOne(mappedBy = "director")
    public SetLibrary managedLibrary;


    public SetAuthor(String name) {
        this.name = name;
    }

    public String toString() {
        return super.toString() + "(" + name + ")";
    }
}
