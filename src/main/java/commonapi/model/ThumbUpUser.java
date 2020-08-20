package commonapi.model;

import commonapi.model.base.ThumbUpBase;
import commonapi.model.base.ThumbUpUserBase;
import io.jboot.db.annotation.Table;

/**
 * @author zhangxr
 * @Title:
 * @Package
 * @Description:
 * @date 2020/6/21 16:21
 */
@Table(tableName = "ex_thumb_up_user", primaryKey = "article_id,user_id")
public class ThumbUpUser extends ThumbUpUserBase<ThumbUpUser> {
}
