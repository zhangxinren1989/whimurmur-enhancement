package commonapi.model.base;

import com.jfinal.plugin.activerecord.IBean;
import io.jboot.db.model.JbootModel;

/**
 * @author zhangxr
 * @Title:
 * @Package
 * @Description:
 * @date 2020/6/21 16:21
 */
public class ThumbUpUserBase<M extends ThumbUpUserBase<M>> extends JbootModel<M> implements IBean {
    public void setThumbUpId(Long thumbUpId) {
        set("thumb_up_id", thumbUpId);
    }

    public Long getThumbUpId() {
        return getLong("thumb_up_id");
    }

    public void setUserId(Long userId) {
        set("user_id", userId);
    }

    public Long getUserId() {
        return getLong("user_id");
    }

    public void setArticleId(Long articleId) {
        set("article_id", articleId);
    }

    public Long getArticleId() {
        return getLong("article_id");
    }
}
