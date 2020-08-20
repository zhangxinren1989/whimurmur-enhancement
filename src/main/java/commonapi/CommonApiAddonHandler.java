package commonapi;


import com.jfinal.handler.Handler;
import org.apache.log4j.helpers.ThreadLocalMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CommonApiAddonHandler extends Handler {
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
//        System.out.println("CommonApiAddonHandler invoked for target : " + target);

        /*String servletPath = request.getServletPath();
        String params = servletPath.substring(servletPath.lastIndexOf("/") + 1);
        String[] paramsArr = params.split("-");
        if(paramsArr != null){
            if(paramsArr.length > 0){
                if("historyArticle".equals(paramsArr[0])){
                    if(paramsArr.length > 1){
                        String  page = paramsArr[1];
                        try {
                            response.sendRedirect("/historyArticle?page=" + page);
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }*/

        next.handle(target, request, response, isHandled);
    }
}
