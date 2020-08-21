package commonapi;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jpress.core.addon.annotation.GlobalInterceptor;

@GlobalInterceptor
public class CommonApiAddonInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {

//        System.out.println("CommonApiAddonInterceptor invoke");

        inv.invoke();
    }
}
