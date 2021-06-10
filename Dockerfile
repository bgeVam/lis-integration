# This is a multistage build Dockerfile
# Part 1: Build the project and produce an rpm file as output
FROM maven:3.6.3-adoptopenjdk-8
MAINTAINER ABBR IT Healthcare Applications <healthcare-applications@Example.com>

ENV TZ=Europe/Vienna
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /home

RUN apt update -y
RUN apt install -y gradle git nano sudo

COPY . /home

RUN chmod 755 build.sh
RUN ./build.sh

# Part 2: Build the image for the deployment
FROM it-projects-market-Example-his-configured
MAINTAINER ABBR IT Healthcare Applications <healthcare-applications@Example.com>

COPY --from=0 /home/bahmni-package/bahmni-lis/build/distributions/lis-integration-0.93-1.noarch.rpm .
COPY install_lis_integration_rpm.sh .
COPY update_lis_integration_db.sh .

ENTRYPOINT ["/usr/sbin/init"]