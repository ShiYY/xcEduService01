package com.xuecheng.framework.domain.cms.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 页面查询条件的封装
 */
@Getter
@Setter
public class QueryPageRequest {

    /**
     * 站点ID
     */
    @ApiModelProperty("站点ID")
    private String siteId;
    /**
     * 页面ID
     */
    @ApiModelProperty("页面ID")
    private String pageId;
    /**
     * 页面名称
     */
    @ApiModelProperty("页面名称")
    private String pageName;
    /**
     * 别名
     */
    @ApiModelProperty("别名")
    private String pageAliase;
    /**
     * 模板ID
     */
    @ApiModelProperty("模板ID")
    private String templateId;
}
