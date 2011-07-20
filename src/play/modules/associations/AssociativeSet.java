package play.modules.associations;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AssociativeSet<T> extends AbstractAssociativeCollection<T> implements Set<T> {



    public AssociativeSet(Reference reference, Object owner) {
        super(reference, owner);
    }

    public void link(T t) {
//        Logger.info(" AssociativeList.link %s %s --> %s", owner(), ref().field().getName(), t);
        delegate().add(t);
    }

    private void linkOpposite(T anElement) {
        ref().opposite().link(anElement, owner());
    }


    public void unlink(T t) {
//        Logger.info(" AssociativeList.unlink %s %s -x- %s", owner(), ref().field().getName(), t);
        delegate().remove(t);
    }

    private void unlinkOpposite(T current) {
        ref().opposite().unlink(current, owner());
    }








    public boolean add(T t) {
        int size = delegate().size();
        assert(t!=null);
        if(!ref().opposite().isCollection()) {
            ref().opposite().set(t, null);
        }
        link(t);
        linkOpposite(t);
        return size!= delegate().size();
    }





    public boolean addAll(Collection<? extends T> aCollection) {
        for (Iterator<? extends T> i = aCollection.iterator(); i.hasNext();) {
            add(i.next());
        }
        return aCollection.size() > 0;
    }


    public void clear() {
        Set<T> del = delegate();
        while(del.size()>0) {
            remove(del.iterator().next());
        }
    }


    public boolean contains(Object anObject) {
        return delegate().contains(anObject);
    }

    public boolean containsAll(Collection<?> aCollection) {
        for(Object o : aCollection) {
            if(!contains(o)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object anObject) {
        if(anObject instanceof AssociativeSet<?>) {
            return delegate().equals(((AssociativeSet)anObject).delegate());
        } else {
            return this == anObject;
        }
    }





    public int hashCode() {
        return delegate().hashCode();
    }


    public boolean isEmpty() {
        return delegate().isEmpty();
    }

    public Iterator<T> iterator() {
        return new Iter();
    }


    public Set<T> delegate() {
        try {
            Set<T> delegate = (Set<T>) ref().field().get(owner());
            if(delegate==null) {
                delegate = new HashSet<T>();
                ref().field().set(owner(), delegate);
            }
            return delegate;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private class Iter implements Iterator<T> {

        Iterator<T> i = new HashSet(delegate()).iterator();
        T current;

        public boolean hasNext() {
            return i.hasNext();
        }

        public T next() {
            return (current = i.next());
        }

        public void remove() {
            if(current == null) {
                throw new IllegalStateException();
            }
            AssociativeSet.this.remove(current);
            current = null;
        }
    }


    @SuppressWarnings("unchecked")
    public boolean remove(Object anObject) {
        Set<T> del = delegate();
        int size = del.size();
        if(del.contains(anObject)) {
            unlink((T)anObject);
            unlinkOpposite((T)anObject);
            return del.size()!=size;
        }

        return false;
    }

    public boolean removeAll(Collection<?> aCollection) {
        boolean changed = false;
        for (Iterator<?> i = aCollection.iterator(); i.hasNext();) {
            changed = changed || remove(i.next());
        }
        return changed;
    }

    public boolean retainAll(Collection<?> aCollection) {
        boolean changed = false;
        Set<T> del = delegate();
        Set<T> copy = new HashSet<T>(del);
        for (T t : copy) {
            if (!aCollection.contains(t)) {
                remove(t);
                changed = true;
            }
        }
        return changed;
    }

    public int size() {
        return delegate().size();
    }

    public Object[] toArray() {
        return delegate().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return delegate().toArray(a);
    }
    @Override
    public String toString() {
        return delegate().toString();
    }

}
