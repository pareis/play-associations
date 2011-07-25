package play.modules.associations;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.FieldInfo;
import play.Logger;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.classloading.enhancers.Enhancer;
import play.exceptions.UnexpectedException;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enhance @Entity classes with bidirectional aware getters and setters
 *
 * TODO: report Guillaime that getter does not have play enhancement annotation (only setter)
 */
public class AssociationsEnhancer extends Enhancer {

    private static final String JAVAX_PERSISTENCE_ENTITY = "javax.persistence.Entity";
    private static final String ENHANCER_NAME = AssociationsEnhancer.class.getName();
    private static final Pattern INFO_REGEX = Pattern.compile("Ljava/util/(List|Set)<L([^;]+);>;");
    private static final Pattern COLLECTION_REGEX = Pattern.compile("^java\\.util\\.(List|Set)$");


    private static class AssociationProperty {
        CtField field;
        CtClass type;
        CtField oppField;
        boolean many;
        boolean list;
        AssociationProperty opposite;

        boolean valid() {
            return field!=null && type!=null && oppField !=null && opposite!=null;
        }
        public String toString() {
            return field.getDeclaringClass().getName() + "." + field.getName() + ":" + type.getName() + "[" + (many ? "*" + (list ? "(List)" : "(Set)") : "1") + "]";
        }

    }

    public void enhanceThisClass(ApplicationClass applicationClass) throws Exception {

        CtClass ctClass = makeClass(applicationClass);

        // Enhance only JPA entities
        if (!hasAnnotation(ctClass, JAVAX_PERSISTENCE_ENTITY)) {
            return;
        }

        String entityName = ctClass.getName();


        CtField[] declaredFields = ctClass.getDeclaredFields();
        List<AssociationProperty> declared = new ArrayList<AssociationProperty>(declaredFields.length);

        for (CtField ctField : declaredFields) {


            try {
                if (isProperty(ctField)) {

                    // eligible properties are:
                    // - annotation (One|Many)To(One|Many) must be present
                    // - OneToOne with mappedBy here or on the other side
                    // - OneToMany with mappedBy
                    // - ManyToOne with mappedBy on other side
                    // - ManyToMany with mappedBy here or on the other side

                    AssociationProperty ap = analyze(ctField);
                    if(ap==null) {
                        continue;
                    }
                    if(Logger.isTraceEnabled()) Logger.trace(ENHANCER_NAME + " found bi-directional association %s <-> %s", ap, ap.opposite);


                    String propertyName = ctField.getName().substring(0, 1).toUpperCase() + (ctField.getName().length()>1 ?ctField.getName().substring(1) : "");
                    String getter = "get" + propertyName;
                    String setter = "set" + propertyName;


                    try {
                        CtMethod ctMethodSet = ctClass.getDeclaredMethod(setter);
                        if(!ap.many) {
                            ctClass.removeMethod(ctMethodSet);
                            if(Logger.isTraceEnabled()) Logger.trace("removed current " + ctMethodSet);
                        }
                    } catch (NotFoundException noSetter) {
                    }

                    try {
                        CtMethod ctMethodGet = ctClass.getDeclaredMethod(getter);
                        if(ap.many) {
                            ctClass.removeMethod(ctMethodGet);
                            Logger.debug("removed current " + ctMethodGet);
                        }
                    } catch (NotFoundException noGetter) {
                    }

                    CtField reference = CtField.make("public static " + Reference.class.getName() + " _ref_" + ctField.getName() + " = new " + Reference.class.getName() + "("
                            + ctClass.getName() + ".class, "
                            + qq(ctField.getName()) + ", "
                            + ap.oppField.getDeclaringClass().getName() + ".class, "
                            + qq(ap.oppField.getName()) + ");", ctClass);
                    ctClass.addField(reference);
                    if(Logger.isTraceEnabled()) Logger.trace("%s added field %s", ctClass.getName(), reference);

                    if(ap.many) {

                        Class<? extends AbstractAssociativeCollection> collectionClass = ap.list ? AssociativeList.class : AssociativeSet.class;
                        CtField delegate = CtField.make("public transient " + collectionClass.getName() + " _delegate_" + ctField.getName()
                                + " = new " + collectionClass.getName() + "(_ref_" + ctField.getName() + ", " + "this);", ctClass);
                        ctClass.addField(delegate);
                        if(Logger.isTraceEnabled()) Logger.trace("%s added field %s", ctClass.getName(), delegate);

                        CtMethod ctMethodGet = CtMethod.make("public " + ctField.getType().getName() + " " + getter + "() { return this." + " _delegate_" + ctField.getName() + "; }", ctClass);
                        ctClass.addMethod(ctMethodGet);

                    } else {

                        CtMethod ctMethodSet = CtMethod.make("public void " + setter + "(" + ctField.getType().getName() + " value) { _ref_" + ctField.getName() + ".set(this, value); }", ctClass);
                        ctClass.addMethod(ctMethodSet);
                    }

                }


            } catch (Exception e) {
                Logger.error(e, "Error in " + ENHANCER_NAME);
                throw new UnexpectedException("Error in " + ENHANCER_NAME, e);
            }

        }

        // Done.
        applicationClass.enhancedByteCode = ctClass.toBytecode();
        ctClass.defrost();
    }


    private AssociationProperty analyze(CtField ctField) {
        try {
            AssociationProperty ap = scan(ctField);
            if(ap!=null) {
//            Logger.trace("1 %s", ap);
                if(ap.oppField==null) {
//                Logger.trace("2.1 %s", ap);
                    for(CtField ofield : ap.type.getDeclaredFields()) {
                        AssociationProperty oppp = scan(ofield);
//                    Logger.trace("2.1.1 %s %s", ofield, oppp);
                        if(oppp!=null && oppp.oppField == ctField) {
                            ap.opposite = oppp;
                            ap.oppField = oppp.field;
                            oppp.opposite = ap;
                            break;
                        }
                    }
                } else {
//                Logger.trace("2.2 %s", ap);
                    AssociationProperty oppp = scan(ap.oppField);
//                Logger.trace("2.2.1 %s %s", ap.oppField, oppp);
                    if(oppp!=null && oppp.type == ctField.getDeclaringClass()) {
                        ap.opposite = oppp;
                        oppp.opposite = ap;
                        oppp.oppField = ap.field;
                    }
                }
                return ap.valid() && ap.opposite.valid() ? ap : null;
            }
        } catch(NotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private AssociationProperty scan(CtField ctField) throws NotFoundException, ClassNotFoundException {
        AssociationProperty ap = new AssociationProperty();
        for (javassist.bytecode.annotation.Annotation a : getAnnotations(ctField).getAnnotations()) {
            if(a.getTypeName().matches("^javax\\.persistence\\.(One|Many)To(One|Many)$")) {

                ap.field = ctField;
                Matcher m = COLLECTION_REGEX.matcher(ctField.getType().getName());

                ap.many = m.matches();

                if(ap.many) {
                    ap.list = "List".equals(m.group(1));
//                    Logger.trace("plain sig" + ctField.getSignature());
                    FieldInfo fi = ctField.getFieldInfo();
                    AttributeInfo signature = fi.getAttribute("Signature");
                    if(signature!=null) {
                        int index = new BigInteger(signature.get()).intValue();
                        String info = signature.getConstPool().getUtf8Info(index);
//                        Logger.trace("deep sig @%s = %s", index, info);
                        Matcher m2 = INFO_REGEX.matcher(info);
                        if(m2.matches()) {
                            String type = m2.group(2).replaceAll("/", ".");
                            CtClass targetClass = ctField.getDeclaringClass().getClassPool().get(type);
//                            Logger.trace("type is %s of %s", m.group(1), targetClass);
                            ap.type = targetClass;
                        }
                    }
                } else {
                    ap.type = ctField.getType();
                }

                if(ap.type!=null && hasAnnotation(ap.type, JAVAX_PERSISTENCE_ENTITY)) {

                    final Set memberNames = a.getMemberNames();
                    if(memberNames!=null && memberNames.contains("mappedBy")) {
                        // find oppField field from mappedBy spec here
                        ap.oppField = ap.type.getField(a.getMemberValue("mappedBy").toString().replaceAll("^\"|\"$", ""));
                    } else {
                        // not done here: scan target type for field with mappedBy = this field
                        // will be done from caller (analyze) by calling scan on all of target types fields
                    }
                }
            }
        }
        return ap.type!=null ? ap : null;
    }


    /**
     * Test if a method has the provided annotation
     * @param ctMethod the javassist method representation
     * @param annotation fully qualified name of the annotation class eg."javax.persistence.Entity"
     * @return true if method has the annotation
     * @throws java.lang.ClassNotFoundException
     */
    protected boolean hasAnnotation(CtMethod ctMethod, String annotation) throws ClassNotFoundException {
        for (Object object : ctMethod.getAvailableAnnotations()) {
            Annotation ann = (Annotation) object;
            if (ann.annotationType().getName().equals(annotation)) {
                return true;
            }
        }
        return false;
    }


    private boolean isProperty(CtField ctField) {
        return !ctField.getName().matches("^[A-Z]") && Modifier.isPublic(ctField.getModifiers())
                && !Modifier.isFinal(ctField.getModifiers())
                && !Modifier.isStatic(ctField.getModifiers());
    }

    public static String qq(String s) {
        return "\"" + s + "\"";
    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface AssociationsPropertyAccessor {
    }
}
