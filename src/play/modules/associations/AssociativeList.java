package play.modules.associations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class AssociativeList<T> extends AbstractAssociativeCollection<T> implements List<T> {



    public AssociativeList(Reference reference, Object owner) {
        super(reference, owner);
    }

    public void link(T t) {
//        Logger.info(" AssociativeList.link %s %s --> %s", owner(), ref().field().getName(), t);
        link(t, -1);
    }

    public void link(T t, int position) {
        if(position<0) {
            delegate().add(t);
        } else {
            delegate().add(position, t);
        }
    }

    private void linkOpposite(T t) {
        ref().opposite().link(t, owner());
    }


    public void unlink(T t) {
//        Logger.info(" AssociativeList.unlink %s %s -x- %s", owner(), ref().field().getName(), t);
        unlink(t, -1);
    }

    public void unlink(T t, int position) {
        if(position<0) {
            delegate().remove(t);
        } else {
            if(position< delegate().size() && delegate().get(position)==t) {
                delegate().remove(position);
            } else {
                delegate().remove(t);
            }
        }
    }

    private void unlinkOpposite(T current) {
        ref().opposite().unlink(current, owner());
    }




    public T set(int index, T t) {
        if(t==null) {
            return remove(index);
        } else {
            T current = get(index);
            if(current!=t) {
                unlink(current, index);
                unlinkOpposite(current);
                if(!ref().opposite().isCollection()) {
                    ref().opposite().set(t, null);
                }
                link(t, index);
                linkOpposite(t);
            }
            return current;
        }
    }



    public T remove(int index) {
        T o = get(index);
        unlink(o, index);
        unlinkOpposite(o);
        return o;
    }

    public void add(int index, T t) {
        assert(indexOf(t) < 0);
        assert(t!=null);
        if(!ref().opposite().isCollection()) {
            ref().opposite().set(t, null);
        }
        link(t, index);
        linkOpposite(t);
    }


    public boolean add(T t) {
        int size = delegate().size();
        assert(t!=null);
        if(!ref().opposite().isCollection()) {
            ref().opposite().set(t, null);
        }
        link(t, -1);
        linkOpposite(t);
        return size!= delegate().size();
    }





    public boolean addAll(Collection<? extends T> collection) {
        for (java.util.Iterator<? extends T> i = collection.iterator(); i.hasNext();) {
            add(i.next());
        }
        return collection.size() > 0;
    }

    public boolean addAll(int index, Collection<? extends T> collection) {
        for (java.util.Iterator<? extends T> i = collection.iterator(); i.hasNext();) {
            add(index++, i.next());
        }
        return collection.size() > 0;
    }

    public void clear() {
        List<T> del = delegate();
        for (int i = del.size() - 1; i >= 0; i--) {
            remove(i);
        }
    }


    public boolean contains(Object object) {
        return indexOf(object) >= 0;
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
        if(object instanceof AssociativeList<?>) {
            AssociativeList<?> other = (AssociativeList<?>) object;
            if(size()!=other.size() || owner()!=other.owner() || ref()!=other.ref()) return false;
            List<T> del = delegate();
            for (int i = 0, size = del.size(); i < size; i++) {
                if(!del.get(i).equals(other.delegate().get(i))) return false;
            }
            return true;
        } else {
            return this == object;
        }
    }



    public T get(int index) {
        return delegate().get(index);
    }


    public int hashCode() {
        return delegate().hashCode();
    }

    public int indexOf(Object object) {
        return delegate().indexOf(object);
    }

    public boolean isEmpty() {
        return delegate().isEmpty();
    }

    public Iterator<T> iterator() {
        return new Iter();
    }

    public int lastIndexOf(Object object) {
        return delegate().lastIndexOf(object);
    }

    public List<T> delegate() {
        try {
            List<T> delegate = (List<T>) ref().field().get(owner());
            if(delegate==null) {
                delegate = new ArrayList();
                ref().field().set(owner(), delegate);
            }
            return delegate;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private class Iter implements Iterator<T> {

        int cursor = 0;
        int last = -1;

        public boolean hasNext() {
            return cursor != size();
        }

        public T next() {
            try {
                T next = get(cursor);
                last = cursor++;
                return next;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (last == -1) {
                throw new IllegalStateException();
            }

            try {
                AssociativeList.this.remove(last);
                if (last < cursor) {
                    cursor--;
                }
                last = -1;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private class ListIter extends Iter implements ListIterator<T> {
        ListIter(int anIndex) {
            cursor = anIndex;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }
        public T previous() {
            int i = cursor - 1;
            T previous = get(i);
            last = i;
            cursor = i;
            return previous;
        }
        public int nextIndex() {
            return cursor;
        }
        public int previousIndex() {
            return cursor - 1;
        }
        public void set(T t) {
            if (last == -1) {
                throw new IllegalStateException();
            }

            try {
                AssociativeList.this.set(last, t);
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
        public void add(T t) {

            try {
                AssociativeList.this.add(cursor++, t);
                last = -1;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
    }

    public ListIterator<T> listIterator() {
        return new ListIter(0);
    }
    public ListIterator<T> listIterator(int anIndex) {
        return new ListIter(anIndex);
    }

    @SuppressWarnings("unchecked")
    public boolean remove(Object object) {
        List<T> del = delegate();
        int size = del.size();
        int index = indexOf(object);
        if(index>=0) {
            unlink((T)object, index);
            unlinkOpposite((T)object);
            return del.size()!=size;
        }

        return false;
    }

    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        for (java.util.Iterator<?> i = collection.iterator(); i.hasNext();) {
            changed = changed || remove(i.next());
        }
        return changed;
    }
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        List<T> del = delegate();
        for (int i = del.size() - 1; i >= 0; i--) {
            if (!collection.contains(del.get(i))) {
                remove(i);
                changed = true;
            }
        }
        return changed;
    }
    public int size() {
        return delegate().size();
    }
    public List<T> subList(int from, int to) {
        // TODO: may be this can be supported?
        throw new UnsupportedOperationException();
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
