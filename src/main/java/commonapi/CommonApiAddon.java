package commonapi;

import com.jfinal.aop.Aop;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Db;
import io.jpress.JPressOptions;
import io.jpress.core.addon.Addon;
import io.jpress.core.addon.AddonInfo;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Option;
import io.jpress.service.OptionService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是一个 JPress 插件的 hello world 项目，没有具体的功能。
 *
 * 其存在的目的是为了帮助开发者，通过 hello world ，了解如何开发一个 JPress 插件
 *
 */
public class CommonApiAddon implements Addon{

    @Override
    public void onInstall(AddonInfo addonInfo) {
        System.out.println("CommonApiAddon update...");
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
        Template currentTemplate = TemplateManager.me().getCurrentTemplate();
        String title = currentTemplate.getTitle();
        int versionCode = currentTemplate.getVersionCode();
        if("whimurmur".equals(title) && versionCode >= 10503){
            System.out.println("option update...");
            try {
                // 先查出同时存在calmlog_ex和whimurmur前缀的相同设置
                List<String> keys = Db.query("select o1.`key` " +
                        "from `option` o1, `option` o2 " +
                        "where o1.`key` like 'calmlog_ex%' and o2.`key` like 'whimurmur%' and SUBSTR(o1.`key`,11) = SUBSTR(o2.`key`, 10)");
                // 删除calmlog_ex前缀的设置，防止下面更新失败
                if(null != keys && keys.size() > 0){
                    for(String key: keys){
                        Db.update("delete from `option` where `key` = ?", key);
                    }

                }

            }catch (Exception e){
                System.out.println("option calmlog_ex delete fail..." + e.getMessage());
            }

            String optionUpdate = "update `option` set `key` = concat('whimurmur', substr(`key`, 11)) "
                    + "where `key` like 'calmlog_ex%' ";
            try {
                // 更新calmlog_ex前缀为whimurmur前缀
                Db.update(optionUpdate);
            }catch (Exception e){
                System.out.println("option calmlog_ex update fail..." + e.getMessage());
            }

            // 额外把-改为_
            try {
                Db.update("update `option` set `key` = 'whimurmur_article_tag_cloud' where `key` = 'whimurmur_article-tag-cloud'");
            }catch (Exception e){
                System.out.println("option whimurmur_article-tag-cloud update fail..." + e.getMessage());
            }

            try {
                Db.update("update `option` set `key` = 'whimurmur_product_tag_cloud' where `key` = 'whimurmur_product-tag-cloud'");
            }catch (Exception e){
                System.out.println("option whimurmur_product-tag-cloud update fail..." + e.getMessage());
            }

            // 额外把whimurmur_index_top_ad改为whimurmur_page_top_ad
            try {
                Db.update("update `option` set `key` = 'whimurmur_page_top_ad' where `key` = 'whimurmur_index_top_ad'");
            }catch (Exception e){
                System.out.println("option whimurmur_index_top_ad update fail..." + e.getMessage());
            }

            // 重新更新内存中的option
            OptionService service = Aop.get(OptionService.class);
            List<Option> options = service.findAll();
            for (Option option : options) {
                //整个网站的后台配置不超过100个，再未来最多也100多个，所以全部放在内存毫无压力
                JPressOptions.set(option.getKey(), option.getValue());
            }
        }

        // ckeditor增强
        File htmls = new File(JFinal.me().getServletContext().getRealPath("") + File.separator + "WEB-INF/views");
        List<File> articleWriteHtmls = new ArrayList<>();
        articleWriteHtml(articleWriteHtmls, htmls);

        System.out.println("ckeditor update...");
        if(articleWriteHtmls.size() > 0){

            for(File html: articleWriteHtmls){
                BufferedWriter writer = null;
                BufferedReader reader = null;
                try{
                    File temp = new File(html.getParent(), "article_write_temp.html");
                    if(temp.exists()){
                        temp.delete();
                    }
                    temp.createNewFile();
                    reader = new BufferedReader(new FileReader(html));
                    writer = new BufferedWriter(new FileWriter(temp));

                    String line;
                    while ((line = reader.readLine()) != null){
                        if(line.indexOf("ckeditor-enhancement.js") > -1
                                || line.indexOf("ckeditor.js") > -1){
                            continue;
                        }

                        if(line.indexOf("#define script()") > -1){
                            writer.write(line);
                            writer.newLine();
                            writer.write("<script src='#(T_PATH)/js/plugin/ckeditor/ckeditor.js'></script>");
                            writer.newLine();
                            writer.write("<script src='#(T_PATH)/js/plugin/ckeditor/ckeditor-enhancement.js'></script>");
                        }else if(line.indexOf("initEditor") > -1){
                            writer.write("initWhimurmurEditor('editor1', 467, editMode);");
                        }else{
                            writer.write(line);
                        }
                        writer.newLine();
                    }

                    writer.flush();
                    writer.close();
                    reader.close();
                    Files.copy(temp.toPath(), html.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    temp.delete();

                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    if(writer != null){
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(writer != null){
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }

    private void articleWriteHtml(List<File> articleWriteHtmls, File dir){
        if(dir.exists() && dir.isDirectory()){
            for(File f: dir.listFiles()){
                if(f.isDirectory()){
                    articleWriteHtml(articleWriteHtmls, f);
                }else if(f.isFile() && f.getName().equals("article_write.html")){
                    articleWriteHtmls.add(f);
                }
            }
        }
    }

    @Override
    public void onUninstall(AddonInfo addonInfo) {

        /**
         *  在 onUninstall 中，我们一般需要去删除自己在 onInstall 中创建的表 或者 其他资源文件
         *  这个方法是用户在 Jpress 后台卸载插件的时候回触发。
         */
        System.out.println("CommonApiAddon onUninstall");
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
