## 插件信息
>对jpress进行功能增强
>2020-06-21 增加了点赞功能
>2020-06-30 增加了文章档案功能
----
### 文章档案：
* url: /admin/addon/commonapi/queryArticleArchive
* 参数：无
* 例子：
```javascript
$.post('/admin/addon/commonapi/queryArticleArchive',
    null,
    function(data){
        //console.log("articleArchive: " + JSON.stringify(data));
    },
    'json'
);
```
----
### 点赞：
**获取文章点赞数：**
* url：/admin/addon/commonapi/queryThumbUp
* 参数: 文章ID: articleId
* 例子：
```javascript
$.post('/admin/addon/commonapi/queryThumbUp',
        {articleId: $("#articleId").val()},
        function(data){
            if(data && data.thumbUpNum){
                $("#thumbUpNum").text(data.thumbUpNum);
            }
        },
        'json'
    );
```

**点赞数+1**
* url:/admin/addon/commonapi/incThumbUp
* 参数：文章ID：articleId，用户Id（可以没有用户Id)：userId
* 例子：
```javascript
$.post('/admin/addon/commonapi/incThumbUp',
    {
        articleId: $("#articleId").val(),
        userId: $("#userId").val()
    },
    function(data){
        var code = data.code;
        if(code == 0){
            $("#thumbUpNum").text(parseInt($("#thumbUpNum").text()) + 1);
        }else if(code == 1){
            toastr.error("你已经点过赞了");
        }else if(code == 2){
            toastr.error("今天匿名点赞次数超过上限了");
        }
    },
    'json'
);
```