package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFsTest {
    
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;
    
    /**
     * 存储文件
     */
    @Test
    public void testStore() throws FileNotFoundException {
        File file = new File("F:/testdata/index_banner.ftl");
        FileInputStream inputStream = new FileInputStream(file);
        // 像GridFs存储文件并返回存储ID
        ObjectId objectId = gridFsTemplate.store(inputStream, "index_banner.ftl");
        System.out.println(objectId);
    }
    
    /**
     * 取文件
     */
    @Test
    public void testQueryFile() throws IOException {
        String fileId = "5d01be1a31c3f0043c464937";
        // 根据ID查询文件
        GridFSFile gridFSFile = gridFsTemplate
            .findOne(Query.query(Criteria.where("_id").is(fileId)));
        // 打开下载流对象
        GridFSDownloadStream downloadStream = gridFSBucket
            .openDownloadStream(gridFSFile.getObjectId());
        // 创建GridFsResource, 用于获取流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, downloadStream);
        // 获取流中的数据
        String s = IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
        System.out.println(s);
    }
    
    /**
     * 删除文件
     */
    @Test
    public void testDelFile() {
        String fileId = "5d01be1a31c3f0043c464937";
        // 根据文件ID删除fs.files和fs.chunks中的记录
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(fileId)));
    }
}
