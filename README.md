### 项目简介
样板代码

### 环境部署

#### redis-stack
```shell
docker run -d --name redis-stack --restart=always -v redis-data:/data -p 6379:6379 -p 8001:8001 -e REDIS_ARGS="--requirepass 123456" redis/redis-stack:7.2.0-v18
```

#### postgres
```shell
docker run --name postgres --restart=always -e POSTGRES_USER=test -e POSTGRES_PASSWORD=123456 -e POSTGRES_DB=postgres -p 5432:5432 -d postgres:17-alpine
```

#### .env
配置文件中账号相关信息通过.env文件声明，不通过git管理，运行前需要替换为实际的参数或同样声明一个.env文件

```dotenv
GITHUB_CLIENT_ID=12312
GITHUB_CLIENT_SECRET=1231231
GOOGLE_CLIENT_ID=123123
GOOGLE_CLIENT_SECRET=1232134
MAIL_USERNAME=1232131312
MAIL_PASSWORD=12313123123
```