package play.modules.associations;

import play.PlayPlugin;
import play.classloading.ApplicationClasses;

import java.util.Map;

public class AssociationsPlugin extends PlayPlugin {

    private AssociationsEnhancer enhancer = new AssociationsEnhancer();

    public void enhance(ApplicationClasses.ApplicationClass applicationClass) throws Exception {
        enhancer.enhanceThisClass(applicationClass);
    }

    @Override
    public Object bind(String name, Object o, Map<String, String[]> params) {
        return super.bind(name, o, params);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
