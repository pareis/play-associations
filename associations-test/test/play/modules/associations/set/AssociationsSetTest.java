package play.modules.associations.set;

import org.junit.Test;
import play.test.UnitTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;


public class AssociationsSetTest extends UnitTest {

    @Test
    public void oneToOne() {
        SetLibrary l1 = new SetLibrary("l1");
        SetLibrary l2 = new SetLibrary("l2");
        SetAuthor a1 = new SetAuthor("a1");
        SetAuthor a2 = new SetAuthor("a2");

        l1.director = a1;
        assertEquals(a1, l1.director);
        assertEquals(l1, a1.managedLibrary);

        a1.managedLibrary = null;
        assertNull(a1.managedLibrary);
        assertNull(l1.director);

        a2.managedLibrary = l2;
        a1.managedLibrary = l1;
        assertEquals(a1, l1.director);
        assertEquals(l1, a1.managedLibrary);
        assertEquals(a2, l2.director);
        assertEquals(l2, a2.managedLibrary);

        l2.director = a1;
        assertEquals(l2, a1.managedLibrary);
        assertEquals(a1, l2.director);
        assertNull(l1.director);
        assertNull(a2.managedLibrary);
    }

    @Test
    public void oneToMany() {
        SetLibrary l1 = new SetLibrary("l1");
        SetLibrary l2 = new SetLibrary("l2");
        SetBook b1 = new SetBook("b1");
        SetBook b2 = new SetBook("b2");
        SetBook b3 = new SetBook("b3");


        l1.books.add(b1);
        l1.books.add(b2);
        l1.books.add(b3);
        assertEquals(3, l1.books.size());
        assertEquals(l1, b1.library);
        assertEquals(l1, b2.library);
        assertEquals(l1, b3.library);

        l2.books.add(b2);
        assertFalse(l1.books.contains(b2));
        assertTrue(l2.books.contains(b2));
        assertEquals(2, l1.books.size());
        assertEquals(1, l2.books.size());
        assertEquals(l1, b1.library);
        assertEquals(l2, b2.library);
        assertEquals(l1, b3.library);

        b3.library = l2;
        assertEquals(1, l1.books.size());
        assertEquals(2, l2.books.size());
        assertEquals(l1, b1.library);
        assertEquals(l2, b2.library);
        assertEquals(l2, b3.library);


        l2.books.remove(b3);
        assertNull(b3.library);

        b1.library = null;
        assertEquals(0, l1.books.size());
        assertEquals(1, l2.books.size());
        assertNull(b1.library);

        l2.books.add(b1);
        assertEquals(0, l1.books.size());
        assertEquals(2, l2.books.size());
        assertEquals(l2, b1.library);

    }

    @Test
    public void manyToMany() {
        SetBook b1 = new SetBook("b1");
        SetBook b2 = new SetBook("b2");
        SetBook b3 = new SetBook("b3");

        SetAuthor a1 = new SetAuthor("a1");

        // link one book with one author
        b1.authors.add(a1);
        assertTrue(a1.books.contains(b1));
        assertTrue(b1.authors.contains(a1));
        assertEquals(1, a1.books.size());
        assertEquals(1, b1.authors.size());

        // break the last association
        a1.books.remove(b1);
        assertFalse(a1.books.contains(b1));
        assertFalse(b1.authors.contains(a1));
        assertEquals(0, a1.books.size());
        assertEquals(0, b1.authors.size());

        b1.authors.add(a1);
        assertTrue(a1.books.contains(b1) && b1.authors.contains(a1));
        SetAuthor a2 = new SetAuthor("a2");
        a2.books.add(b1);
        assertEquals(2, b1.authors.size());
        assertEquals(1, a1.books.size());
        assertEquals(1, a2.books.size());

        b1.authors.clear();
        assertEquals(0, b1.authors.size());
        assertEquals(0, a1.books.size());
        assertEquals(0, a2.books.size());
    }



    @Test
    public void iteration() {
        SetBook b1 = new SetBook("b1");
        SetBook b2 = new SetBook("b2");
        SetBook b3 = new SetBook("b3");

        SetAuthor a1 = new SetAuthor("a1");
        SetAuthor a2 = new SetAuthor("a2");

        b1.authors.addAll(Arrays.asList(a1, a2));
        b2.authors.addAll(Arrays.asList(a1, a2));
        b3.authors.addAll(Arrays.asList(a1, a2));
        assertEquals(2, b1.authors.size());
        assertEquals(2, b2.authors.size());
        assertEquals(2, b3.authors.size());
        assertEquals(3, a1.books.size());
        assertEquals(3, a2.books.size());


        Iterator<SetBook> ibooks = a1.books.iterator();
        SetBook tmp = ibooks.next();
        ibooks.remove();
        assertEquals(2, a1.books.size());
        assertEquals(3, a2.books.size());
        assertEquals(1, tmp.authors.size());
        ibooks.next();
        ibooks.remove();
        assertEquals(1, a1.books.size());
        assertEquals(3, a2.books.size());

    }

    @Test
    public void sanity() {
        SetLibrary l1 = new SetLibrary("l1");
        SetBook b1 = new SetBook("b1");
        b1.library = l1;
        assertTrue(l1.books.contains(b1));
        b1.library = null;
        assertFalse(l1.books.contains(b1));

        SetAuthor a1 = new SetAuthor("a1");
        try {
            a1.books.add(null);
            fail();
        } catch(IllegalArgumentException e) {
        }

        a1.books.add(b1);
        assertEquals(1, a1.books.size());
        assertEquals(1, b1.authors.size());

        a1.books.add(b1);
        assertEquals(1, a1.books.size());
        assertEquals(1, b1.authors.size());

        b1.authors.add(a1);
        assertEquals(1, a1.books.size());
        assertEquals(1, b1.authors.size());

    }




        @Test
    public void collections() {

        SetLibrary l1 = new SetLibrary("l1");
        SetLibrary l2 = new SetLibrary("l2");
        SetBook b1 = new SetBook("b1");
        SetBook b2 = new SetBook("b2");
        SetBook b3 = new SetBook("b3");
        SetBook b4 = new SetBook("b4");

        l1.books.addAll(Arrays.asList(b1, null, null, b4));
        assertEquals(2, l1.books.size());
        assertEquals(l1, b1.library);
        assertEquals(l1, b4.library);


        l2.books.addAll(Arrays.asList(b2, b3));
        assertEquals(2, l2.books.size());

        l1.books.addAll(l2.books);
        assertEquals(4, l1.books.size());
        assertEquals(0, l2.books.size());
        assertEquals(new HashSet(Arrays.asList(b1, b2, b3, b4)), l1.books);

        l1.books.removeAll(l1.books);
        assertEquals(0, l1.books.size());
        assertNull(b1.library);

        l1.books.remove(null);
    }
}
