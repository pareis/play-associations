package play.modules.associations.list;

import org.junit.*;
import play.Logger;
import play.test.*;

import java.util.Arrays;


public class AssociationsListTest extends UnitTest {

    @Test
    public void oneToOne() {
        ListLibrary l1 = new ListLibrary("l1");
        ListLibrary l2 = new ListLibrary("l2");
        ListAuthor a1 = new ListAuthor("a1");
        ListAuthor a2 = new ListAuthor("a2");

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
        ListLibrary l1 = new ListLibrary("l1");
        ListLibrary l2 = new ListLibrary("l2");
        ListBook b1 = new ListBook("b1");
        ListBook b2 = new ListBook("b2");
        ListBook b3 = new ListBook("b3");


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
        ListBook b1 = new ListBook("b1");
        ListBook b2 = new ListBook("b2");
        ListBook b3 = new ListBook("b3");

        ListAuthor a1 = new ListAuthor("a1");

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
        ListAuthor a2 = new ListAuthor("a2");
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
    public void sanity() {
        ListLibrary l1 = new ListLibrary("l1");
        ListBook b1 = new ListBook("b1");
        b1.library = l1;
        assertTrue(l1.books.contains(b1));
        b1.library = null;
        assertFalse(l1.books.contains(b1));

        ListAuthor a1 = new ListAuthor("a1");
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

        ListLibrary l1 = new ListLibrary("l1");
        ListLibrary l2 = new ListLibrary("l2");
        ListBook b1 = new ListBook("b1");
        ListBook b2 = new ListBook("b2");
        ListBook b3 = new ListBook("b3");
        ListBook b4 = new ListBook("b4");

        l1.books.addAll(Arrays.asList(b1, null, null, b4));
        assertEquals(2, l1.books.size());
        assertEquals(l1, b1.library);
        assertEquals(l1, b4.library);


        l2.books.addAll(Arrays.asList(b2, b3));
        assertEquals(2, l2.books.size());

        l1.books.addAll(1, l2.books);
        assertEquals(4, l1.books.size());
        assertEquals(0, l2.books.size());
        assertEquals(Arrays.asList(b1, b2, b3, b4), l1.books);

        l1.books.removeAll(l1.books);
        assertEquals(0, l1.books.size());
        assertNull(b1.library);

        l1.books.remove(null);
    }


}
