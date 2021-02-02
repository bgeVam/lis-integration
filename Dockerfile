FROM maven:3.6.3-adoptopenjdk-8
MAINTAINER ABBR IT Healthcare Applications <healthcare-applications@Example.com>

ENV TZ=Europe/Vienna
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /home

RUN apt update -y
RUN apt install -y gradle git nano sudo

COPY . /home

RUN chmod 755 build.sh
