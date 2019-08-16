package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {
    
    @Autowired
    PageService pageService;
    
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page,
        @PathVariable("size") int size, QueryPageRequest queryPageRequest) {
        
        // 测试 - 静态数据
        /*CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("测试页面");
        List<CmsPage> list = new ArrayList<>();1
        list.add(cmsPage);
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        queryResult.setList(list);
        queryResult.setTotal(1);
    
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,
            queryResult);
        return queryResponseResult;*/
        
        // 数据库数据
        return pageService.findList(page, size, queryPageRequest);
    }
    
    @Override
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        // @RequestBody 将请求的json数据转换为对象
        return pageService.add(cmsPage);
    }
    
    @Override
    @GetMapping("/get/{id}")
    public CmsPage getById(@PathVariable("id") String id) {
        return pageService.getById(id);
    }
    
    @Override
    @PutMapping("/update/{id}") // 这里使用put方法, http方法中put表示更新
    public CmsPageResult update(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        return pageService.update(id, cmsPage);
    }
    
    @Override
    @DeleteMapping("delete/{id}")
    public ResponseResult delete(@PathVariable String id) {
        return pageService.delete(id);
    }
}
