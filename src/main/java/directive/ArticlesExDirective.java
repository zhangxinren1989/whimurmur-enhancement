package directive;

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.provider.ArticleServiceProvider;
import io.jpress.service.UserService;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @extend zhangxr 增加查询条件，扩展使用场景
 * @version V1.0
 */
@JFinalDirective("articlesEx")
public class ArticlesExDirective extends JbootDirectiveBase {

    @Inject
    private ArticleServiceProvider service;

    @Inject
    private UserService userService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String flag = getPara("flag", scope);
        String style = getPara("style", scope);
        Boolean hasThumbnail = getParaToBool("hasThumbnail", scope);
        String orderBy = getPara("orderBy", scope, "article.id desc");

        // 兼容以前不带表别名的orderBy写法
        if(orderBy != null && orderBy.indexOf(".") == -1){
            orderBy = "article." + orderBy;
        }

        // 隐藏部分不想展示的文章
        Boolean hiddenFlag = getParaToBool("hiddenFlag", scope, false);
        int count = getParaToInt("count", scope, 10);


        Columns columns = Columns.create();

        columns.eq("article.flag", flag);
        columns.eq("article.style", style);
        columns.eq("article.status", Article.STATUS_NORMAL);
        
        if(hiddenFlag) {
        	columns.sqlPart("(article.flag != 'hidden' or article.flag is null)");
        }

        if (hasThumbnail != null) {
            if (hasThumbnail) {
                columns.isNotNull("article.thumbnail");
            } else {
                columns.isNull("article.thumbnail");
            }
        }

        // add by zhangxr 增加查询条件
        String year = getPara("year", scope);
        String month = getPara("month", scope);
        if(null != year){
            columns.sqlPart("date_format(ifnull(article.created, article.modified), '%Y') = " + year);
        }else if(month != null){
            columns.sqlPart("date_format(ifnull(article.created, article.modified), '%Y%m') = " + month);
        }

        // 关联点赞文章
        List<Article> articles = service.getDao().leftJoin("ex_thumb_up").as("thumbUp")
                .on("article.id = thumbUp.article_id")
                .findListByColumns(columns, orderBy, count, "article.*");

        if (articles == null || articles.isEmpty()) {
            return;
        }

        userService.join(articles, "user_id");

        scope.setLocal("articles", articles);
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
