package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

@Service
public class PageService {
    
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsConfigRepository cmsConfigRepository;
    @Autowired
    CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;
    
    /**
     * 页面查询方法
     *
     * @param page 页码
     * @param size 查询数量
     * @param queryPageRequest 查询条件
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        // 自定义条件查询
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }
        // 定义条件匹配器(指定模糊查询条件)
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
            .withMatcher("pageAliase", GenericPropertyMatchers.contains());
        // 条件值对象
        CmsPage cmsPage = new CmsPage();
        // 设置站点ID查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        // 设置模板ID查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())) {
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        // 设置页面别名查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        // 定义条件对象Example
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        
        page = page <= 0 ? 0 : page - 1; // 对外接口从1开始查询 实际从0开始
        size = size < 0 ? 0 : size;
        
        Pageable pageable = PageRequest.of(page, size);
        // 实现自定义条件查询并分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        queryResult.setList(all.getContent()); // 数据列表
        queryResult.setTotal(all.getTotalElements()); // 数据总记录数
        
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,
            queryResult);
        
        return queryResponseResult;
    }
    
    /**
     * 新增页面
     *
     * @param cmsPage 页面实体
     */
    public CmsPageResult add(CmsPage cmsPage) {
        // 校验页面名称, 站点ID, 页面webpath的唯一性
        // 根据页面名称, 站点ID, 页面webpath去cms_page集合查询, 如果查到说明此页面已经存在
        // 如果查询不到才会继续添加
        // if (cmsPage == null) {
        //     ExceptionCast.cast(CommonCode.FAIL);
        // }
        cmsPage = null;
        CmsPage findCmsPage = cmsPageRepository
            .findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(),
                cmsPage.getPageWebPath());
        if (findCmsPage != null) {
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        cmsPage.setPageId(null); // 保证主键唯一
        CmsPage save = cmsPageRepository.save(cmsPage);
        // cmsPage 和 save 是同一个对象
        return new CmsPageResult(CommonCode.SUCCESS, save);
        // 添加失败
        // return new CmsPageResult(CommonCode.FAIL, null);
    }
    
    /**
     * 根据页面ID查询页面
     *
     * @param pageId UID
     */
    public CmsPage getById(String pageId) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        return optional.isPresent() ? optional.get() : null;
    }
    
    /**
     * 修改页面
     *
     * @param pageId UID
     * @param cmsPage 页面实体
     */
    public CmsPageResult update(String pageId, CmsPage cmsPage) {
        // 根据ID从数据库查询页面信息
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (optional.isPresent()) {
            // 设置要修改的数据(修改要针对单个属性分别进行设置, 有些属性是不允许修改的)
            CmsPage target = optional.get();
            target.setPageName(cmsPage.getPageName());
            cmsPageRepository.save(target);
            return new CmsPageResult(CommonCode.SUCCESS, target);
        }
        // 修改失败
        return new CmsPageResult(CommonCode.FAIL, null);
    }
    
    /**
     * 删除页面
     *
     * @param pageId UID
     */
    public ResponseResult delete(String pageId) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (optional.isPresent()) {
            cmsPageRepository.deleteById(pageId);
            return ResponseResult.SUCCESS();
        }
        return ResponseResult.FAIL();
    }
    
    /**
     * 根据ID查询CmsConfig
     *
     * @param configId UID
     */
    public CmsConfig getConfigById(String configId) {
        Optional<CmsConfig> optional = cmsConfigRepository.findById(configId);
        return optional.isPresent() ? optional.get() : null;
    }
    
    /**
     * 页面静态化
     *
     * @param pageId UID
     */
    public String getPageHtml(String pageId) {
        // 远程请求DataUrl获取数据模型
        Map model = getModelByPageId(pageId);
        if (model == null) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL); // 数据模型为空
        }
        // 获取页面的模板信息
        String templateContent = getTemplateByPageId(pageId);
        if (templateContent == null) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL); // 模板信息为空
        }
        // 执行页面静态化
        return generateHtml(templateContent, model);
    }
    
    /**
     * 执行页面静态化
     *
     * @param templateContent 模板内容
     * @param model 数据
     */
    private String generateHtml(String templateContent, Map model) {
        // 创建配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 创建模板加载器
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        templateLoader.putTemplate("template", templateContent);
        // 在配置中设置模板加载器
        configuration.setTemplateLoader(templateLoader);
        // 获取模板内容
        try {
            Template template = configuration.getTemplate("template", "UTF-8");
            // 静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取页面模板信息
     *
     * @param pageId UID
     */
    private String getTemplateByPageId(String pageId) {
        // 查询页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOT_EXISTS); // 页面不存在
        }
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL); // 页面的templateId为空
        }
        // 查询模板信息
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()) {
            CmsTemplate cmsTemplate = optional.get();
            String templateFileId = cmsTemplate.getTemplateFileId();
            // 从GridFs中取模板文件内容
            // 根据ID查询文件
            GridFSFile gridFSFile = gridFsTemplate
                .findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            // 打开下载流对象
            GridFSDownloadStream downloadStream = gridFSBucket
                .openDownloadStream(gridFSFile.getObjectId());
            // 创建GridFsResource, 用于获取流对象
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, downloadStream);
            // 获取流中的数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * 获取数据模型
     *
     * @param pageId UID
     */
    private Map getModelByPageId(String pageId) {
        // 查询页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOT_EXISTS); // 页面不存在
        }
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL); // 页面的dataUrl为空
        }
        // 通过RestTemplate请求dataUrl获取数据
        ResponseEntity<Map> entity = restTemplate.getForEntity(dataUrl, Map.class);
        return entity.getBody();
    }
    
}
