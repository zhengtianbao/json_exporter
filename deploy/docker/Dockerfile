FROM centos:7
WORKDIR /opt
ENV LANG=en_US.utf8
RUN set -ex \
    && echo "LANG=en_US.utf8" > /etc/locale.conf \
    && echo "net.core.somaxconn = 1024" >> /etc/sysctl.conf \
    && echo "vm.overcommit_memory = 1" >> /etc/sysctl.conf \
    && yum -y install yum -y install *epel* \
    && yum -y localinstall http://mirrors.ustc.edu.cn/mysql-repo/mysql57-community-release-el7.rpm \
    && yum -y install mysql-community-server java-11-openjdk git nginx npm --nogpgcheck \
    && echo "start" > /dev/null
COPY deploy/docker/entrypoint.sh .
COPY web/nginx.conf /etc/nginx/conf.d/nginx.conf
COPY build/ /usr/share/nginx/html/
RUN chmod +x ./entrypoint.sh
EXPOSE 80
ENTRYPOINT ["./entrypoint.sh"]
