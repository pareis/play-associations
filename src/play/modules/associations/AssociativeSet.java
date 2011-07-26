package play.modules.associations;

import org.apache.commons.collections.iterators.ArrayListIterator;
import play.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AssociativeSet<T> extends AbstractAssociativeCollection<T> implements Set<T> {



    public AssociativeSet(Reference reference, Object owner) {
        super(reference, owner);
    }

    public void link(T t) {
        if(Logger.isTraceEnabled()) Logger.trace(" AssociativeSet.link %s %s --> %s", owner(), ref().field().getName(), t);
        delegate().add(t);
    }

    private void linkOpposite(T t) {
        ref().opposite().link(t, owner());
    }


    public void unlink(T t) {
        if(Logger.isTraceEnabled()) Logger.trace(" AssociativeSet.unlink %s %s -x- %s", owner(), ref().field().getName(), t);
        delegate().remove(t);
    }

    private void unlinkOpposite(T t) {
        ref().opposite().unlink(t, owner());
    }








    public boolean add(T t) {
        if(t==null) throw new IllegalArgumentException("null is not allowed in associations");
        int size = delegate().size();
        if(!ref().opposite().isCollection()) {
            ref().opposite().set(t, null);
        }
        link(t);
        linkOpposite(t);
        return size!=size();
    }





    public boolean addAll(Collection<? extends T> collection) {
        for (Iterator<? extends T> i = new ArrayList(collection).iterator(); i.hasNext();) {
            T t = i.next();
            if(t!=null) {
                add(t);
            }
        }
        return collection.size() > 0;
    }


    public void clear() {
        Set<T> del = delegate();
        while(del.size()>0) {
            remove(del.iterator().next());
        }
    }


    public boolean contains(Object object) {
        return delegate().contains(object);
    }

    public boolean containsAll(Collection<?> collection) {
        for(Object o : collection) {
            if(!contains(o)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object object) {
        if(object instanceof AssociativeSet<?>) {
            return delegate().equals(((AssociativeSet)object).delegate());
        } else {
            return this == object;
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
    public boolean remove(Object object) {
        Set<T> del = delegate();
        int size = del.size();
        if(del.contains(object)) {
            unlink((T)object);
            unlinkOpposite((T)object);
            return del.size()!=size;
        }

        return false;
    }

    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        for (Iterator<?> i = new ArrayList(collection).iterator(); i.hasNext();) {
            changed = remove(i.next()) || changed;
        }
        return changed;
    }

    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        Set<T> del = delegate();
        Set<T> copy = new HashSet<T>(del);
        for (T t : copy) {
            if (!collection.contains(t)) {
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
