package play.modules.associations;

import org.junit.*;
import play.test.*;


public class AssociationsTest extends UnitTest {

    @Test
    public void oneToOne() {
        Library l1 = new Library("l1");
        Library l2 = new Library("l2");
        Author a1 = new Author("a1");
        Author a2 = new Author("a2");

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
        Library l1 = new Library("l1");
        Library l2 = new Library("l2");
        Book b1 = new Book("b1");
        Book b2 = new Book("b2");
        Book b3 = new Book("b3");


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
        Book b1 = new Book("b1");
        Book b2 = new Book("b2");
        Book b3 = new Book("b3");

        Author a1 = new Author("a1");

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
        Author a2 = new Author("a2");
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
