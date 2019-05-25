## 更新说明
1. 使用Java Californium 框架，当前版本`2.0.0-M14`
2. 使用IDEA+Maven重写，2019年之前并没有系统学习和使用Java

## 运行
使用pm2工具保持后台运行,新建一个名为`server.json`的文件，文件内容如下
```
{
    "name": "coap-plugtest-server",
    "script": "/usr/bin/java",
    "args": [
        "-jar",
        "plugtest-server.jar"
    ],
    "exec_interpreter": "",
    "exec_mode": "fork"
}
```
在控制台中通过pm2启动进行,启动前应使用`pm2 stop [id]`停止原应用
```
pm2 start server.json
```
