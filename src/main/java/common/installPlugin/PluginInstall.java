package common.installPlugin;

import com.jfinal.log.Log;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * @author zhangxr
 * @Title:
 * @Package
 * @Description:
 * @date 2020年8月11日 0011 15:48
 */
public class PluginInstall {

    private PluginInstall(){}

    private static Log log = Log.getLog(PluginInstall.class);

    // 根据文件名检测插件文件有没有被安装
    public static boolean checkPlugin(String checkFile) {
        Template curTemplate = TemplateManager.me().getCurrentTemplate();
        File templateDir = curTemplate.getAbsolutePathFile();

        File[] files = templateDir.listFiles();
        boolean applied = false;
        for(File file: files) {
            if(file.getName().equals(checkFile)) {
                applied = true;
                break;
            }
        }

        return applied;
    }

    // 包括插件目录和资源目录
    public static int applyPlugin(String resourcePath) {
        Template curTemplate = TemplateManager.me().getCurrentTemplate();
        File templateDir = curTemplate.getAbsolutePathFile();
        File parentFile = templateDir.getParentFile();
        while(!parentFile.getName().equals("templates")) {
            parentFile = parentFile.getParentFile();
        }
        // 网站根目录
        parentFile = parentFile.getParentFile();

        File pluginResource = new File(parentFile.getAbsolutePath()
                + File.separator + "addons" + File.separator + resourcePath);

        int failCount = 0;
        for(File f: pluginResource.listFiles()){
            failCount = applyPlugin(f, "/");
        }

        curTemplate.refresh();

        return failCount;
    }

    public static void removePlugin(List<String> files) {
        Template curTemplate = TemplateManager.me().getCurrentTemplate();
        File templateDir = curTemplate.getAbsolutePathFile();

        deleteFiles(templateDir, files);
        curTemplate.refresh();
    }

    private static int applyPlugin(File file, String parentPath){
        int res = 0;
        Template curTemplate = TemplateManager.me().getCurrentTemplate();
        File templateDir = curTemplate.getAbsolutePathFile();

        if(file.isDirectory()) {
            for(File resource: file.listFiles()) {
                applyPlugin(resource, parentPath + File.separator + file.getName());
            }
        }else{
            File temp = new File(templateDir + parentPath, file.getName());
            try {
                //判断父目录是否存在，如果不存在，则创建
                if (temp.getParentFile() != null && !temp.getParentFile().exists()) {
                    temp.getParentFile().mkdirs();
                }
                if(!temp.exists()){
                    temp.createNewFile();
                }
                Files.copy(file.toPath(), temp.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                res--;
                log.error("安装插件文件失败，文件全路径名：" + file.getAbsolutePath(), e);
            }
        }

        return res;
    }

    // 从模板中找插件文件并删除
    private static void deleteFiles(File file, List<String> deletes) {
        boolean deleted = false;
        for(String del: deletes) {
            if(file.getName().equals(del)) {
                if(file.isDirectory()){
                    deletePlugin(file);
                }else{
                    file.delete();
                }
                deleted = true;
            }
        }

        if(!deleted && file.isDirectory()) {
            for(File f: file.listFiles()) {
                deleteFiles(f, deletes);
            }
        }
    }

    // 删除插件文件夹
    private static void deletePlugin(File dir) {
        if(dir.isDirectory()) {
            for(File f: dir.listFiles()) {
                if(f.isFile()) {
                    f.delete();
                }else {
                    deletePlugin(f);
                }
            }
        }

        dir.delete();
    }
}
