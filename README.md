# 项目简介
样板代码

# 环境部署

## redis-stack
```shell
docker run -d --name redis-stack --restart=always -v redis-data:/data -p 6379:6379 -p 8001:8001 -e REDIS_ARGS="--requirepass 123456" redis/redis-stack:7.2.0-v18
```

## postgres
```shell
docker run --name postgres --restart=always -e POSTGRES_USER=test -e POSTGRES_PASSWORD=123456 -e POSTGRES_DB=postgres -p 5432:5432 -d postgres:17-alpine
```

## 敏感信息
配置文件中账号相关信息通过 account.yml 文件声明，不通过git管理，运行前需要在application.yml 同级目录下声明一个 account.yml 文件或替换为实际的参数

```yml
GITHUB_CLIENT_ID: Ov2******************94
GITHUB_CLIENT_SECRET: 78*******************ff7
GOOGLE_CLIENT_ID: 97****************ntent.com
GOOGLE_CLIENT_SECRET: GOC***********************Fvd
MAIL_USERNAME: us************me@email.com
MAIL_PASSWORD: LU***************Y2
```