package commonapi.model;

import commonapi.model.base.ThumbUpBase;
import io.jboot.db.annotation.Table;

/**
 * @author zhangxr
 * @Title:
 * @Package
 * @Description:
 * @date 2020/6/21 16:01
 */
@Table(tableName = "ex_thumb_up", primaryKey = "id")
public class ThumbUp extends ThumbUpBase<ThumbUp> {
    private static final long serialVersionUID = 1L;
}
