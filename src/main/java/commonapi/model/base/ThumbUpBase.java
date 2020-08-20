package commonapi.model.base;

import com.jfinal.plugin.activerecord.IBean;
import io.jboot.db.model.JbootModel;

import java.util.Date;

/**
 * @author zhangxr
 * @Title:
 * @Package
 * @Description:
 * @date 2020/6/21 16:01
 */
public class ThumbUpBase<M extends ThumbUpBase<M>> extends JbootModel<M> implements IBean {
    private static final long serialVersionUID = 1L;

    public void setId(Long id) {
        set("id", id);
    }

    public Long getId() {
        return getLong("id");
    }

    public void setArticleId(Long articleId) {
        set("article_id", articleId);
    }

    public Long getArticleId() {
        return getLong("article_id");
    }

    public void setThumbUpNum(Long thumbUpNum) {
        set("thumb_up_num", thumbUpNum);
    }

    public Long getThumbUpNum() {
        return getLong("thumb_up_num");
    }

    public void setAnonymousDayNum(Long anonymousDayNum) {
        set("anonymous_day_num", anonymousDayNum);
    }

    public Long getAnonymousDayNum() {
        return getLong("anonymous_day_num");
    }

    public void setAnonymousDay(Date anonymousDay) {
        set("anonymous_day", anonymousDay);
    }

    public Date getAnonymousDay() {
        return getDate("anonymous_day");
    }

    public void setCreated(Date created) {
        set("created", created);
    }

    public Date getCreated() {
        return getDate("created");
    }

    public void setModified(Date modified) {
        set("modified", modified);
    }

    public Date getModified() {
        return getDate("modified");
    }

    public void setCreatedBy(Long createdBy) {
        set("created_by", createdBy);
    }

    public Long getCreatedBy() {
        return getLong("created_by");
    }

    public void setModifiedBy(Long modifiedBy) {
        set("modified_by", modifiedBy);
    }

    public Long getModifiedBy() {
        return getLong("modified_by");
    }
}
