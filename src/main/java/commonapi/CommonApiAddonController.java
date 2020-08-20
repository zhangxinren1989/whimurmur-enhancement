package commonapi;

import com.alibaba.fastjson.JSONObject;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import commonapi.model.ThumbUp;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


@RequestMapping(value = "/admin/addon/commonapi",viewPath = "/")
public class CommonApiAddonController extends JbootController {

    @ActionKey("/admin/addon/commonapi/index")
    @AdminMenu(groupId = JPressConsts.SYSTEM_MENU_ADDON, text = "功能增强")
    public void index() {
        renderJson("还没有设置页");
    }

    public void incThumbUp() {
        Long articleId = getLong("articleId");
        Long userId = getLong("userId");
        if(null != userId){
            int existUser = Db.queryInt("select count(*) from ex_thumb_up_user where user_id = ? and article_id = ?", userId, articleId);
            if(existUser == 1){
                // 已存在该用户的点赞
                renderJson("code", 1);
                return;
            }
        }

        ThumbUp thumbUp = new ThumbUp().findFirst("select * from ex_thumb_up where article_id = ?", articleId);
        Long thumbUpId = null;
        if(thumbUp != null){
            System.out.println(thumbUp.toString());
            thumbUpId = thumbUp.getId();
            Long anonymousDayNum = thumbUp.getAnonymousDayNum();
            // 从数据库取出的日期是java.sql.Date
            Date anonymousDay = new Date(thumbUp.getAnonymousDay().getTime());
            if(userId == null){
                LocalDate now = LocalDate.now();
                LocalDate anonymousLocalDate = anonymousDay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if(now.equals(anonymousLocalDate)){
                    // 每天最多匿名点赞50次
                    if(anonymousDayNum >= 50){
                        renderJson("code", 2);
                        return;
                    }else {
                        anonymousDayNum++;
                    }
                }else {
                    anonymousDayNum = 1l;
                    anonymousDay = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
                }
            }

            thumbUp.setThumbUpNum(thumbUp.getThumbUpNum() + 1);
            thumbUp.setAnonymousDay(anonymousDay);
            thumbUp.setAnonymousDayNum(anonymousDayNum);
            thumbUp.setModified(new Date());
            thumbUp.setModifiedBy(userId == null ? 0 : userId);
            thumbUp.update();

        }else{
            Record record = new Record().set("article_id", articleId)
                    .set("thumb_up_num", 1)
                    .set("anonymous_day_num", userId == null ? 1 : 0)
                    .set("anonymous_day", new Date())
                    .set("created", new Date())
                    .set("created_by", userId == null ? 0 : userId);
            Db.save("ex_thumb_up", record);
            thumbUpId = record.get("id");
        }

        if(userId != null) {
            Record userThumbUpUser = new Record().set("article_id", articleId)
                    .set("user_id", userId)
                    .set("thumb_up_id", thumbUpId);
            Db.save("ex_thumb_up_user", userThumbUpUser);
        }

        renderJson("code", 0);
    }

    public void queryThumbUp(){
        Long articleId = getLong("articleId");
        Long thumbUpNum = Db.queryLong("select thumb_up_num from ex_thumb_up where article_id = ? ", articleId);
        renderJson("thumbUpNum", thumbUpNum);
    }

    /**
     * @Description: 文章档案：月度文章数，年度文章数；月度转载数，年度转载数
     * @author almond
     * @date 2020/6/30 22:32
     */
    public void queryArticleArchive(){
        List<Object[]> articleStatistics = Db.query("select date_format(ifnull(created, modified), '%Y%m') statisticDimension, count(*) amount " +
                "from article " +
                "where status = 'normal' and (style <> 'reprint' or style is null) " +
                "group by date_format(ifnull(created, modified), '%Y%m') " +
                "order by date_format(ifnull(created, modified), '%Y%m') desc");

        List<Object[]> reprintArticleStatistics = Db.query("select date_format(ifnull(created, modified), '%Y%m') statisticDimension, count(*) amount " +
                "from article " +
                "where status = 'normal' and style = 'reprint' " +
                "group by date_format(ifnull(created, modified), '%Y%m') " +
                "order by date_format(ifnull(created, modified), '%Y%m') desc");

        JSONObject result = new JSONObject();
        String month, year;
        int amount, yearAmount;

        if(CollectionUtil.isNotEmpty(articleStatistics)){
            JSONObject monthStat = new JSONObject();
            JSONObject yearStat = new JSONObject();
            Map<String, Integer> yearStatMap = new TreeMap<>((k1, k2) -> Integer.parseInt(k2) - Integer.parseInt(k1));
            for(Object[] fields: articleStatistics){
                month = fields[0].toString();
                year = month.substring(0, 4);
                amount = Integer.parseInt(fields[1].toString());
                monthStat.put(month, amount);
                yearAmount = yearStatMap.getOrDefault(year, 0);
                yearAmount += amount;
                yearStatMap.put(year, yearAmount);
            }
            yearStat.putAll(yearStatMap);
            result.put("selfArticleMonthly", monthStat);
            result.put("selfArticleYearly", yearStat);
        }

        if(CollectionUtil.isNotEmpty(reprintArticleStatistics)){
            JSONObject monthStat = new JSONObject();
            JSONObject yearStat = new JSONObject();
            Map<String, Integer> yearStatMap = new TreeMap<>((k1, k2) -> Integer.parseInt(k2) - Integer.parseInt(k1));
            for(Object[] fields: reprintArticleStatistics){
                month = fields[0].toString();
                year = month.substring(0, 4);
                amount = Integer.parseInt(fields[1].toString());
                monthStat.put(month, amount);
                yearAmount = yearStatMap.getOrDefault(year, 0);
                yearAmount += amount;
                yearStatMap.put(year, yearAmount);
            }
            yearStat.putAll(yearStatMap);
            result.put("reprintArticleMonthly", monthStat);
            result.put("reprintArticleYearly", yearStat);
        }

        renderJson(result.toJSONString());
    }
}
