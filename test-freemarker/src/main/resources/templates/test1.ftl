<!DOCTYPE html>
<html>
<head>
  <meta charset="utf‐8">
  <title>Hello World!</title>
</head>
<body>
Hello ${name}!
<table>
  <tr>
    <td>序号</td>
    <td>姓名</td>
    <td>年龄</td>
    <td>金额</td>
    <td>出生日期</td>
  </tr>
  <#-- 遍历List -->
  <#if stus??> <#-- 空值处理 ??不为空 -->
  <#list stus as stu>
    <tr>
      <td>${stu_index + 1}</td>
      <#-- <#if>标签要与前面有空格间隔 -->
      <td <#if stu.name=='小明'>style="background:cornflowerblue";</#if> >${stu.name}</td>
      <td>${stu.age}</td>
      <#-- stu.money gt 300 或 (stu.money>300) -->
      <td <#if stu.money gt 300>style="background:yellow";</#if> >${stu.money}</td>
      <td>${stu.birthday?date}</td><#-- 内建函数 -->
      <td>${stu.birthday?string("yyyy年MM月dd日")}</td><#-- 内建函数 -->
    </tr>
  </#list>
    <br/>
    学生的个数: ${stus?size} <#-- 内建函数 -->
  </#if>
</table>

<br/>

<#-- 遍历Map -->
<#-- 方法一:在中括号中填写map的key -->
姓名:${(stuMap["stu1"].name)!""}<br/> <#-- 设置缺省值用的最多 格式: (表达式)!"缺省值" -->
年龄:${(stuMap["stu1"].age)!""}<br/>
金额:${(stuMap["stu1"].money)!""}<br/>

<#-- 方法二:直接map.key -->
姓名:${stuMap.stu2.name}<br/>
年龄:${stuMap.stu2.age}<br/>
金额:${stuMap.stu2.money}<br/>

<#-- 方法三:遍历map中的key stuMap?keys就是key的列表 -->
<#list stuMap?keys as key>
  姓名:${stuMap[key].name}<br/> <#-- 只能这种格式 stuMap.key.name会报错 -->
</#list>

<br/>
${point?c} <#-- 默认显示格式:102,920,122 / point?c转换成字符串 -->

<br/>
<#-- 将json字符串转换成对象 <#assign>定义一个页面变量 -->
<#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
<#assign data=text?eval />
<#-- 内建函数 data=text?eval 将json字符串(text)转换成对象赋给data 很少这么用了解一下-->
开户行：${data.bank} 账号：${data.account}
</body>
</html>
