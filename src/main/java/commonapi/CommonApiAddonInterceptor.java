package commonapi;


import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;


public class CommonApiAddonInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {

//        System.out.println("HelloWorldAddonInterceptor invoke");

        inv.invoke();
    }
}
