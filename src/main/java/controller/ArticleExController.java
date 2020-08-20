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
package controller;

import com.jfinal.aop.Inject;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.web.base.TemplateControllerBase;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @extend zhangxr 返回文章列表页，增加查询条件，增强功能
 * @version V1.0
 * @Title: 文章前台页面Controller
 * @Package io.jpress.module.article
 */
@RequestMapping("/article/ex")
public class ArticleExController extends TemplateControllerBase {


    public void index() {

        if (StrUtil.isBlank(getPara())) {
            redirect("/");
            return;
        }

        String key = getPara(0);
        String condition = getPara(1);
        String addition = getPara(2);

        setAttr(key, condition);
        setAttr("addition", addition);

        render(getRenderView(addition));
    }

    private String getRenderView(String style) {
        String customPage = null;
        if(style != null && style.startsWith("page")){
            customPage = style.substring(4);
            customPage = customPage.substring(0, 1).toLowerCase() + customPage.substring(1);
        }
        return customPage == null
                ? "artlist.html"
                : "artlist_" + customPage + ".html";
    }


}
