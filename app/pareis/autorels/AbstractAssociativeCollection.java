package pareis.autorels;


public abstract class AbstractAssociativeCollection<T> implements AssociativeCollection<T> {


    private Reference reference;
    public Reference ref() {
        return reference;
    }

    private Object owner;
    public Object owner() {
        return owner;
    }



    public AbstractAssociativeCollection(Reference reference, Object owner) {
        this.reference = reference;
        this.owner = owner;
    }
}
