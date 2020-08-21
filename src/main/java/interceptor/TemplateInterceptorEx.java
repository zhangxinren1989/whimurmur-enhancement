package interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jpress.JPressOptions;
import io.jpress.core.addon.annotation.GlobalInterceptor;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;

/**
 * @author zhangxr
 * @Title:
 * @Package
 * @Description:
 * @date 2020年8月20日 0020 17:15
 */
@GlobalInterceptor
public class TemplateInterceptorEx implements Interceptor, JPressOptions.OptionChangeListener {

    // 额外使页面可以获取一些常见的setting，原来只能获取网站相关，这里使页面可以获取一些当前模板相关的设置
//    private static String webTitle = null;
//    private static String webSubTitle = null;
//    private static String webName = null;
//    private static String webDomain = null;
//    private static String webCopyright = null;
//    private static String webIpcNo = null;
//    private static String seoTitle = null;
//    private static String seoKeyword = null;
//    private static String seoDescription = null;

    private static String tTitle = null;
    private static String tVersion = null;
    private static String tPath = null;
    private static String jpressVersion = null;
    private static String jpressVersionCode = null;

    public TemplateInterceptorEx(){
//        System.out.println("TemplateInterceptorEx: enter constructor");
        init();
        JPressOptions.addListener(this);
    }

    public void init() {
        Template currentTemplate = TemplateManager.me().getCurrentTemplate();
        tTitle = currentTemplate.getTitle();
        tVersion = currentTemplate.getVersion();
        tPath = currentTemplate.getRelativePath();
        jpressVersion = JPressOptions.get("jpress_version");
        jpressVersionCode = JPressOptions.get("jpress_version_code");
    }

    @Override
    public void intercept(Invocation invocation) {
//        System.out.println("TemplateInterceptorEx: enter intercept");
        Controller controller = invocation.getController();
        controller.setAttr("T_TITLE", tTitle);
        controller.setAttr("T_VERSION", tVersion);
        controller.setAttr("T_PATH", tPath);
        controller.setAttr("JPRESS_VERSION", jpressVersion);
        controller.setAttr("JPRESS_VERSION_CODE", jpressVersionCode);

        invocation.invoke();
    }

    @Override
    public void onChanged(String key, String newValue, String oldValue) {
        switch (key) {
            case "web_template":
//                System.out.println("TemplateInterceptorEx: change web_template");
                Template currentTemplate = TemplateManager.me().getTemplateById(newValue);
                tTitle = currentTemplate.getTitle();
                tVersion = currentTemplate.getVersion();
                tPath = currentTemplate.getRelativePath();
                break;

        }
    }
}
