# Betting Stakes Service

简单的HTTP服务，用来管理投注的stake。

## 快速开始

**如果遇到Java环境问题，请先看 [INSTALL.md](INSTALL.md)**

### 方式1：命令行（需要Java JDK）

```bash
# 编译
build.bat

# 打包成JAR
package.bat

# 运行JAR
java -jar betting-stakes.jar

# 或者直接运行class文件
run.bat
```

### 方式2：使用IDE（推荐）

直接用IDE打开项目，运行 `BettingStakesApplication.java`

## 怎么运行

需要Java 11或更高版本。

### 编译
```bash
javac -d target/classes src/main/java/com/betting/session/SessionManager.java \
    src/main/java/com/betting/stake/StakeManager.java \
    src/main/java/com/betting/handler/*.java \
    src/main/java/com/betting/BettingStakesApplication.java
```

或者用Maven:
```bash
mvn package
```

### 运行
```bash
java -cp target/classes com.betting.BettingStakesApplication
```

或者用jar:
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

## 实现思路

### Session管理
- 用HashMap存储，key是customerId和sessionKey
- 10分钟过期
- 后台线程每5分钟清理过期session

### Stake存储
- 用ConcurrentHashMap，支持并发
- 每个betOfferId对应一个Map<customerId, stake>
- 只保存每个customer最高的stake
- 查询时排序取前20

### 并发处理
- 用ConcurrentHashMap保证线程安全
- HttpServer用cached thread pool处理请求
- 没有用锁，性能比较好

## 测试

```bash
mvn test
```

或者手动测试:
```bash
# 获取session
curl http://localhost:8001/1234/session

# 提交stake (用上面返回的sessionkey)
curl -X POST -d "4500" "http://localhost:8001/888/stake?sessionkey=YOUR_KEY"

# 查询
curl http://localhost:8001/888/highstakes
```

## 注意事项

- 数据存在内存里，重启会丢失
- session过期后stake不会删除
- 没有用任何外部框架，只用JDK自带的HttpServer
