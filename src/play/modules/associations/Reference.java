package play.modules.associations;


import play.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Reference {

    private Class clazz;
    private String fieldName;
    private Class oppClazz;
    private String oppFieldName;

    public Reference(Class clazz, String fieldName, Class oppClass, String oppFieldName) {
        this.clazz = clazz;
        this.fieldName = fieldName;
        this.oppClazz = oppClass;
        this.oppFieldName = oppFieldName;
    }

    private Field field;
    public Field field() {
        if(field==null) {
            try {
                this.field = clazz.getField(fieldName);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return field;
    }

    private Field delegate;
    public Field delegate() {
        if(delegate==null) {
            try {
                this.delegate = clazz.getField("_delegate_" + fieldName);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("error resolving _delegate_" + fieldName + " in class " + clazz.getName(), e);
            }
        }
        return delegate;
    }

    private Reference opposite;
    public Reference opposite() {
        if(opposite==null) {
            try {
                Field oppRefField = oppClazz.getField("_ref_" + oppFieldName);
                this.opposite = (Reference) oppRefField.get(null);
                Logger.info("%s.%s opposite is %s", clazz.getName(), fieldName, oppRefField);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return opposite;
    }

    private Boolean collection;
    public boolean isCollection() {
        if(collection==null) {
            collection = Collection.class.isAssignableFrom(field().getType()) ? Boolean.TRUE : Boolean.FALSE;
        }
        return collection.booleanValue();
    }


    /**
     * makes the raw change, without considering the other side
     * @param owner The object that should be referencing the target
     * @param target The target
     */
    public void link(Object owner, Object target) {
        
        if(isCollection()) {
            Logger.info(" link (coll) %s: %s --> %s", owner, fieldName, target);
            try {
                Collection<?> raw = (Collection)field().get(owner);
                if(raw==null) {
                    if(List.class.isAssignableFrom(field().getType())) {
                        raw = new ArrayList<Object>();
                    } else if(Set.class.isAssignableFrom(field().getType())) {
                        raw = new HashSet<Object>();
                    }
                }

                AssociativeCollection del = (AssociativeCollection)delegate().get(owner);
                if(del==null) {
                    if(List.class.isAssignableFrom(field().getType())) {
                        del = new AssociativeList(this, owner);
                    } else if(Set.class.isAssignableFrom(field().getType())) {
                        //del = new AssociativeSet<Object>(this, owner); // TODO complete all collections
                    }
                }

                del.link(target);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            Logger.info(" link (single) %s: %s --> %s", owner, fieldName, target);
            try {
                field().set(owner, target);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * makes the raw change, without considering the other side
     * @param owner The object that is currently referencing the target
     * @param target The target that is to be dereferenced
     */
    public void unlink(Object owner, Object target) {
        if(isCollection()) {
            Logger.info(" unlink (coll) %s: %s -x- %s", owner, fieldName, target);
            try {
                AssociativeCollection del = (AssociativeCollection)delegate().get(owner);
                if(del!=null) {
                    del.unlink(target);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            Logger.info(" unlink (single) %s: %s -x- %s", owner, fieldName, target);
            try {
                field().set(owner, null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

    }


    /**
     * Helper for set on to-one references
     * @param owner
     * @param target
     */
    public void set(Object owner, Object target) {
            Object current = current(owner);
            Logger.info("%s set: '%s': %s => %s)", owner, fieldName, current, target);
            if(current != target) {
                if(current!=null) {
                    if(target!=null && !opposite().isCollection()) {
                        // break the association of the target with its current counterpart
                        opposite().set(target, null);
                    }
                    opposite().unlink(current, owner);
                    current = null;
                }
                if(target != null) {
                    opposite().link(target, owner);
                }
                link(owner, target);
            }

    }


    public Object current(Object owner) {
        try {
            return (isCollection() ? delegate() : field()).get(owner);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
}
