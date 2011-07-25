package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class Person extends Model {

    public String name;

    @OneToOne(mappedBy = "manager")
    public Publisher managedLibrary;

    public Person(String name) {
        this.name = name;
    }
}
