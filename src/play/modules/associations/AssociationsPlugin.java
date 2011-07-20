package play.modules.associations;

import play.PlayPlugin;
import play.classloading.ApplicationClasses;

public class AssociationsPlugin extends PlayPlugin {

    private AssociationsEnhancer enhancer = new AssociationsEnhancer();

    public void enhance(ApplicationClasses.ApplicationClass applicationClass) throws Exception {
        enhancer.enhanceThisClass(applicationClass);
    }
}
