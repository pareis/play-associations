package play.modules.associations;

import java.util.Collection;

public interface AssociativeCollection<T> extends Collection<T> {
    Object owner();
    Reference ref();
    void link(T t);
    void unlink(T t);
}
