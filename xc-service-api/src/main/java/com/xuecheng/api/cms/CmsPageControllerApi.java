package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "CMS页面管理接口", tags = {"提供页面的CRUD"})
public interface CmsPageControllerApi {
    
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
        @ApiImplicitParam(name = "size", value = "查询数量", required = true, paramType = "path", dataType = "int")
    })
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);
    
    @ApiOperation("新增页面")
    public CmsPageResult add(CmsPage cmsPage);
    
    @ApiOperation("根据页面ID查询页面")
    public CmsPage getById(String id);
    
    @ApiOperation("修改页面")
    public CmsPageResult update(String id, CmsPage cmsPage);
    
    @ApiOperation("删除页面")
    public ResponseResult delete(String id);
}
