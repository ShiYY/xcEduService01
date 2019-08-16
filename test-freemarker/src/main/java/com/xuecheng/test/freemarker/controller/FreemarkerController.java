package com.xuecheng.test.freemarker.controller;


import com.xuecheng.test.freemarker.model.Student;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
// @RestController 默认响应的是json数据(这里不能使用)
@RequestMapping("/freemarker")
public class FreemarkerController {
    
    @Autowired
    RestTemplate restTemplate;
    
    @RequestMapping("/banner")
    public String index_banner(Map<String, Object> map) {
        // 使用RestTemplate请求轮播图的模型数据
        String url = "http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f";
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(url, Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);
        return "index_banner";
    }
    
    @RequestMapping("/test1")
    public String freemarker(Map<String, Object> map) {
        // map就是freemarker模板所使用的数据
        map.put("name", "seattle");
        
        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());
        
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
        stu2.setBirthday(new Date());
        
        List<Student> friends = new ArrayList<>();
        friends.add(stu1);
        stu2.setFriends(friends);
        stu2.setBestFriend(stu1);
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);
        //向数据模型放数据
        map.put("stus", stus);
        //准备map数据
        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        //向数据模型放数据
        map.put("stu1", stu1);
        //向数据模型放数据
        map.put("stuMap", stuMap);
        
        map.put("point", 102920122);
        
        // 返回freemarker模板的位置, 基于resources/templates路径
        return "test1";
    }
}
