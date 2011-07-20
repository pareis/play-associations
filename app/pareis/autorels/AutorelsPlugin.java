package pareis.autorels;

import play.PlayPlugin;
import play.classloading.ApplicationClasses;

public class AutorelsPlugin extends PlayPlugin {

    private AutorelsEnhancer enhancer = new AutorelsEnhancer();

    public void enhance(ApplicationClasses.ApplicationClass applicationClass) throws Exception {
        enhancer.enhanceThisClass(applicationClass);
    }
}
