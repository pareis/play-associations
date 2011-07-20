package play.modules.associations.list;

import org.junit.*;
import play.test.*;


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

}
