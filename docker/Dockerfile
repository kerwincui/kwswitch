FROM java:8

# 作者
MAINTAINER kerwincui

# 添加jar包到容器
ADD ruoyi.jar /kwswitch/app.jar

# 更新jar包的时间戳
RUN bash -c 'touch /app.jar'

# 运行jar包
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/kwswitch/app.jar"]

# 设置时区
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
