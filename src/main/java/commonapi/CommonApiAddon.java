package commonapi;

import com.jfinal.plugin.activerecord.Db;
import io.jpress.core.addon.Addon;
import io.jpress.core.addon.AddonInfo;

/**
 * 这是一个 JPress 插件的 hello world 项目，没有具体的功能。
 *
 * 其存在的目的是为了帮助开发者，通过 hello world ，了解如何开发一个 JPress 插件
 *
 */
public class CommonApiAddon implements Addon {

    @Override
    public void onInstall(AddonInfo addonInfo) {

        /**
         * 在 onInstall ，我们一般需要 创建自己的数据表
         *
         * onInstall 方法只会执行一次，执行完毕之后不会再执行，除非是用户卸载插件再次安装
         */
//        System.out.println("CommonApiAddon onInstall");

        // 只建表不删表
        String createSql = "CREATE TABLE IF NOT EXISTS ex_thumb_up( "
                + "id int(11) unsigned NOT NULL AUTO_INCREMENT,"
                + "article_id int(11) not null,"
                + "thumb_up_num int(11),"
                + "anonymous_day_num int(11),"
                + "anonymous_day date,"
                + "created datetime,"
                + "modified datetime,"
                + "created_by int(11),"
                + "modified_by int(11),"
                + "PRIMARY KEY(id)"
                + ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";

        String createSql2 = "CREATE TABLE IF NOT EXISTS ex_thumb_up_user("
                + "thumb_up_id int(11) NOT NULL,"
                + "user_id int(11) not null,"
                + "article_id int(11) not null,"
                + "PRIMARY KEY(article_id, user_id)"
                + ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";

        Db.update(createSql);
        Db.update(createSql2);

        // whimurmur v1.5.3，将option中calmlog_ex改为whimurmur
        String version = addonInfo.getVersion();
        if("1.5.3".equals(version)){
            String optionUpdate = "update `option` set `key` = concat('whimurmur', substr(`key`, 11))"
                    + "where `key` like '%calmlog_ex%' ";
            Db.update(optionUpdate);
            Db.update("update `option` set `key` = 'whimurmur_article_tag_cloud' where `key` = 'whimurmur_article-tag-cloud'");
            Db.update("update `option` set `key` = 'whimurmur_product_tag_cloud' where `key` = 'whimurmur_product-tag-cloud'");
        }
    }

    @Override
    public void onUninstall(AddonInfo addonInfo) {

        /**
         *  在 onUninstall 中，我们一般需要去删除自己在 onInstall 中创建的表 或者 其他资源文件
         *  这个方法是用户在 Jpress 后台卸载插件的时候回触发。
         */
//        System.out.println("CommonApiAddon onUninstall");
    }

    @Override
    public void onStart(AddonInfo addonInfo) {

        /**
         *  在 onStart 方法中，我们可以做很多事情，例如：创建后台或用户中心的菜单
         *
         *  此方法是每次项目启动，都会执行。
         *
         *  同时用户也可以在后台触发
         */
//        System.out.println("CommonApiAddon onStart");

    }

    @Override
    public void onStop(AddonInfo addonInfo) {

        /**
         *  和 onStart 对应，在 onStart 所处理的事情，在 onStop 应该释放
         *
         *  同时用户也可以在后台触发
         */
//        System.out.println("CommonApiAddon onStop");

    }
}
