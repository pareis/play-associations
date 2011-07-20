package play.modules.associations.set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Set;

@Entity
public class SetLibrary {
    @Id
    public String name;

    @OneToMany
    public Set<SetAuthor> authors;

    @OneToMany(mappedBy = "library")
    public Set<SetBook> books;


    @OneToOne
    public SetAuthor director;

    public SetLibrary(String name) {
        this.name = name;
    }

    public String toString() {
        return super.toString() + "(" + name + ")";
    }

}
