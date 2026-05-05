# Betting Stakes Service

简单的HTTP服务，用来管理投注的stake。

## 快速开始



## 怎么运行

需要Java 11或更高版本。


### 运行

```bash
java -jar betting-stakes-1.0.0.jar
```

服务会在8001端口启动。

## API

### 1. 获取session
```
GET /<customerid>/session
```

例子:
```bash
curl http://localhost:8001/1234/session
# 返回: QWER12A
```

session有效期10分钟，同一个customer在10分钟内会返回相同的session。

### 2. 提交stake
```
POST /<betofferid>/stake?sessionkey=<sessionkey>
body: <stake>
```

例子:
```bash
curl -X POST -d "4500" "http://localhost:8001/888/stake?sessionkey=QWER12A"
```

需要有效的session key，否则返回401。

### 3. 获取最高stake
```
GET /<betofferid>/highstakes
```

例子:
```bash
curl http://localhost:8001/888/highstakes
# 返回: 1234=4500,5678=1337
```

返回前20个最高的stake，按降序排列，每个customer只显示最高的那个。

## 代码结构

```
src/main/java/com/betting/
├── BettingStakesApplication.java  # 主程序
├── handler/
│   ├── SessionHandler.java        # 处理session请求
│   ├── StakeHandler.java          # 处理stake提交
│   └── HighStakesHandler.java     # 处理highstakes查询
├── session/
│   └── SessionManager.java        # session管理
└── stake/
    └── StakeManager.java          # stake存储
```



- 数据存在内存里，重启会丢失
- session过期后stake不会删除
- 没有用任何外部框架，只用JDK自带的HttpServer
