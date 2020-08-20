/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package directive;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.dialect.JbootMysqlDialect;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jboot.web.directive.base.PaginateDirectiveBase;
import io.jpress.JPressOptions;
import io.jpress.commons.directive.DirectveKit;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.article.service.provider.ArticleServiceProvider;
import io.jpress.service.UserService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@JFinalDirective("articlePageEx")
public class ArticlePageExDirective extends JbootDirectiveBase {

    private static final String DEFAULT_ORDER_BY = "order_number desc,id desc";

    @Inject
    private ArticleServiceProvider service;

    @Inject
    private UserService userService;

    private String addition;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Controller controller = JbootControllerContext.get();

        int page = getParaToInt("page", scope, 0);

        if(page == 0){
            page = controller.getParaToInt(1, 1);
        }

        int pageSize = getParaToInt("pageSize", scope, 10);
        // 当flag设置为hidden，文章列表页不可见，通过hidden参数触发
        Boolean hiddenFlag = getParaToBool("hiddenFlag", scope, false);
        String orderBy = getPara("orderBy", scope, "id desc");
        
        // 可以指定当前的分类ID
        Long categoryId = getParaToLong("categoryId", scope, 0L);
        ArticleCategory category = controller.getAttr("category");

        if (categoryId == 0 && category != null) {
            categoryId = category.getId();
        }

        // tofix 可以优化，参数从url中获取，如year为year2020这种形式,addition为
        // additionArtstyleReprint这种
        // add by zhangxr
        String year = getPara("year", scope);
        String month = getPara("month", scope);
        addition = getPara("addition", scope);

        Page<Article> articlePageEx = null;
        if(year != null){
            Columns columns = Columns.create().sqlPart("date_format(ifnull(created, modified), '%Y') = " + year);
            page = controller.getParaToInt(3, 1);
            articlePageEx = exQuery(columns, hiddenFlag, page, pageSize, orderBy);
        }else if(month != null){
            Columns columns = Columns.create().sqlPart("date_format(ifnull(created, modified), '%Y%m') = " + month);
            page = controller.getParaToInt(3, 1);
            articlePageEx = exQuery(columns, hiddenFlag, page, pageSize, orderBy);
        }else{
            articlePageEx = categoryId == 0
                    ? (hiddenFlag ? paginateInNormalNotHidden(page, pageSize, orderBy) : service.paginateInNormal(page, pageSize, orderBy))
                    : (hiddenFlag ? paginateByCategoryIdInNormalNotHidden(page, pageSize, categoryId, orderBy) : service.paginateByCategoryIdInNormal(page, pageSize, categoryId, orderBy));
        }



        scope.setGlobal("articlePageEx", articlePageEx);
        renderBody(env, scope, writer);
    }

    private Page<Article> exQuery(Columns columns, boolean hiddenFlag, int page, int pagesize, String orderBy){
        orderBy = StrUtil.obtainDefaultIfBlank(orderBy, DEFAULT_ORDER_BY);
        columns.eq("status", Article.STATUS_NORMAL);
        if(hiddenFlag){
            columns.sqlPart("(flag != 'hidden' or flag is null)");
        }

        if(addition != null && addition.startsWith("artstyle")){
            String style = addition.substring(8);
            style = style.substring(0, 1).toLowerCase() + style.substring(1);
            if("reprint".equals(style)){
                columns.eq("style", style);
            }else if("default".equals(style)){
                columns.sqlPart("style is null");
            }
        }

        Page<Article> dataPage = service.paginateByColumns(page, pagesize, columns, orderBy);
        userService.join(dataPage, "user_id");
        return dataPage;
    }

    @Cacheable(name = "articles")
    public Page<Article> paginateInNormalNotHidden(int page, int pagesize, String orderBy) {
        orderBy = StrUtil.obtainDefaultIfBlank(orderBy, DEFAULT_ORDER_BY);
        Columns columns = new Columns();
        columns.eq("status", Article.STATUS_NORMAL);
        columns.sqlPart("(flag != 'hidden' or flag is null)");
        Page<Article> dataPage = service.paginateByColumns(page, pagesize, columns, orderBy);
        userService.join(dataPage, "user_id");
        return dataPage;
    }

    @Cacheable(name = "articles")
    public Page<Article> paginateByCategoryIdInNormalNotHidden(int page, int pagesize, long categoryId, String orderBy) {

        Columns columns = new Columns();
        columns.eq("m.category_id", categoryId);
        columns.eq("article.status", Article.STATUS_NORMAL);
        columns.sqlPart("(article.flag != 'hidden' or article.flag is null)");

        Page<Article> dataPage = service.getDao().leftJoin("article_category_mapping")
                .as("m").on("article.id=m.`article_id`")
                .paginateByColumns(page, pagesize, columns, StrUtil.obtainDefaultIfBlank(orderBy, DEFAULT_ORDER_BY));
        userService.join(dataPage, "user_id");
        return dataPage;
    }

    @Override
    public boolean hasEnd() {
        return true;
    }


    @JFinalDirective("articlePaginateEx")
    public static class TemplatePaginateDirective extends PaginateDirectiveBase {

        @Override
        protected String getUrl(int pageNumber, Env env, Scope scope, Writer writer) {
            HttpServletRequest request = JbootControllerContext.get().getRequest();
            String url = request.getRequestURI();
            String contextPath = JFinal.me().getContextPath();

            boolean firstGotoIndex = getPara("firstGotoIndex", scope, false);

            if (pageNumber == 1 && firstGotoIndex) {
                return contextPath + "/";
            }

            // 如果当前页面是首页的话
            // 需要改变url的值，因为 上一页或下一页是通过当前的url解析出来的
            if (url.equals(contextPath + "/")) {
                url = contextPath + "/article/category/index"
                        + JPressOptions.getAppUrlSuffix();
            }
            return DirectveKit.replacePageNumber(url, pageNumber);
        }

        @Override
        protected Page<?> getPage(Env env, Scope scope, Writer writer) {
            return (Page<?>) scope.get("articlePageEx");
        }

    }
}
