package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {
    
    @Autowired
    CmsPageRepository cmsPageRepository;
    
    /**
     * 查询全部
     */
    @Test
    public void testFindAll() {
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }
    
    /**
     * 分页查询
     */
    @Test
    public void testFindPage() {
        // 分页查询参数
        int page = 0; // 从0开始
        int size = 10; // 分页条数
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all.getSize()); // size数值
    }
    
    /**
     * 添加
     */
    @Test
    public void testInsert() {
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        List<CmsPageParam> cmsPageParams = new ArrayList<>();
        cmsPageParams.add(cmsPageParam);
        
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("s01");
        cmsPage.setTemplateId("t01");
        cmsPage.setPageName("测试页面");
        cmsPage.setPageCreateTime(new Date());
        cmsPage.setPageParams(cmsPageParams);
        
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage);
    }
    
    /**
     * 删除
     */
    @Test
    public void testDelete() {
        cmsPageRepository.deleteById("5cefc1c731c3f011580608cf");
    }
    
    /**
     * 修改
     */
    @Test
    public void testUpdate() {
        // 查询对象
        Optional<CmsPage> optional = cmsPageRepository.findById("5abefd525b05aa293098fca6");
        if (optional.isPresent()) {
            // 设置修改的值
            CmsPage cmsPage = optional.get();
            cmsPage.setPageAliase("aaa");
            // 修改提交
            CmsPage save = cmsPageRepository.save(cmsPage);
            System.out.println(save);
        }
    }
    
    // ==============自定义Dao查询条件===============
    
    /**
     * 根据页面名称查询
     */
    @Test
    public void testFindByPageName() {
        // TODO 多个满足条件会怎样?
        CmsPage cmsPage = cmsPageRepository.findByPageName("index2.html");
        System.err.println(cmsPage);
    }
    
    /**
     * 自定义条件查询
     */
    @Test
    public void testFindAllByExample() {
        // 分页查询参数
        int page = 0; // 从0开始
        int size = 10; // 分页条数
        Pageable pageable = PageRequest.of(page, size);
        
        // 条件值对象
        CmsPage cmsPage = new CmsPage();
        // 1.不设置任何条件查全部 2.往条件值对象中设置查询条件时进行精确匹配
        // 设置站点ID查询条件
        // cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        // 设置模板ID查询条件
        // cmsPage.setTemplateId("5a962b52b00ffc514038faf7");
        // 设置页面别名查询条件
        cmsPage.setPageAliase("轮");
        // 条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        // 设置模糊匹配
        exampleMatcher = exampleMatcher
            .withMatcher("pageAliase", GenericPropertyMatchers.contains());
        // 定义Example
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content.size());
    }
    
    
}
