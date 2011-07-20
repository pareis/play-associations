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

    public void link(T anElement, int position) {
        if(position<0) {
            delegate().add(anElement);
        } else {
            delegate().add(position, anElement);
        }
    }

    private void linkOpposite(T anElement) {
        ref().opposite().link(anElement, owner());
    }


    public void unlink(T t) {
//        Logger.info(" AssociativeList.unlink %s %s -x- %s", owner(), ref().field().getName(), t);
        unlink(t, -1);
    }

    public void unlink(T anElement, int position) {
        if(position<0) {
            delegate().remove(anElement);
        } else {
            if(position< delegate().size() && delegate().get(position)==anElement) {
                delegate().remove(position);
            } else {
                delegate().remove(anElement);
            }
        }
    }

    private void unlinkOpposite(T current) {
        ref().opposite().unlink(current, owner());
    }




    public T set(int anIndex, T anElement) {
        if(anElement==null) {
            return remove(anIndex);
        } else {
            T current = get(anIndex);
            if(current!=anElement) {
                unlink(current, anIndex);
                unlinkOpposite(current);
                if(!ref().opposite().isCollection()) {
                    ref().opposite().set(anElement, null);
                }
                link(anElement, anIndex);
                linkOpposite(anElement);
            }
            return current;
        }
    }



    public T remove(int anIndex) {
        T o = get(anIndex);
        unlink(o, anIndex);
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





    public boolean addAll(Collection<? extends T> aCollection) {
        for (java.util.Iterator<? extends T> i = aCollection.iterator(); i.hasNext();) {
            add(i.next());
        }
        return aCollection.size() > 0;
    }

    public boolean addAll(int anIndex, Collection<? extends T> aColl) {
        int index = anIndex;
        for (java.util.Iterator<? extends T> i = aColl.iterator(); i.hasNext();) {
            add(index++, i.next());
        }
        return aColl.size() > 0;
    }

    public void clear() {
        List<T> del = delegate();
        for (int i = del.size() - 1; i >= 0; i--) {
            remove(i);
        }
    }


    public boolean contains(Object anObject) {
        return indexOf(anObject) >= 0;
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
        if(anObject instanceof AssociativeList<?>) {
            AssociativeList<?> other = (AssociativeList<?>) anObject;
            if(size()!=other.size() || owner()!=other.owner() || ref()!=other.ref()) return false;
            List<T> del = delegate();
            for (int i = 0, size = del.size(); i < size; i++) {
                if(!del.get(i).equals(other.delegate().get(i))) return false;
            }
            return true;
        } else {
            return this == anObject;
        }
    }



    public T get(int anIndex) {
        return delegate().get(anIndex);
    }


    public int hashCode() {
        return delegate().hashCode();
    }

    public int indexOf(Object anObject) {
        return delegate().indexOf(anObject);
    }

    public boolean isEmpty() {
        return delegate().isEmpty();
    }

    public Iterator<T> iterator() {
        return new Iter();
    }

    public int lastIndexOf(Object anObject) {
        return delegate().lastIndexOf(anObject);
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

        /**
         * Index of element to be returned by subsequent call to next.
         */
        int cursor = 0;

        /**
         * Index of element returned by most recent call to next or previous.
         * Reset to -1 if this element is deleted by a call to remove.
         */
        int lastRet = -1;

        public boolean hasNext() {
            return cursor != size();
        }

        public T next() {
            try {
                T next = get(cursor);
                lastRet = cursor++;
                return next;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (lastRet == -1) {
                throw new IllegalStateException();
            }

            try {
                AssociativeList.this.remove(lastRet);
                if (lastRet < cursor) {
                    cursor--;
                }
                lastRet = -1;
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
            lastRet = i;
            cursor = i;
            return previous;
        }
        public int nextIndex() {
            return cursor;
        }
        public int previousIndex() {
            return cursor - 1;
        }
        public void set(T anE) {
            if (lastRet == -1) {
                throw new IllegalStateException();
            }

            try {
                AssociativeList.this.set(lastRet, anE);
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
        public void add(T anE) {

            try {
                AssociativeList.this.add(cursor++, anE);
                lastRet = -1;
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
    public boolean remove(Object anObject) {
        List<T> del = delegate();
        int size = del.size();
        int index = indexOf(anObject);
        if(index>=0) {
            unlink((T)anObject, index);
            unlinkOpposite((T)anObject);
            return del.size()!=size;
        }

        return false;
    }

    public boolean removeAll(Collection<?> aCollection) {
        boolean changed = false;
        for (java.util.Iterator<?> i = aCollection.iterator(); i.hasNext();) {
            changed = changed || remove(i.next());
        }
        return changed;
    }
    public boolean retainAll(Collection<?> aCollection) {
        boolean changed = false;
        List<T> del = delegate();
        for (int i = del.size() - 1; i >= 0; i--) {
            if (!aCollection.contains(del.get(i))) {
                remove(i);
                changed = true;
            }
        }
        return changed;
    }
    public int size() {
        return delegate().size();
    }
    public List<T> subList(int aFromIndex, int aToIndex) {
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
