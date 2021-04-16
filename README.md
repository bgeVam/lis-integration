# LIS-Integration

LIS Integration is Bahmni's Laboratory System that can send laboratory orders to other system that is HL7 ORM compatible, and in return can receive laboratory results than can be viewed in Bahmni. When an order is completed in LIS, an ORU message is received, which transmits results from the producing system (LIS), in the ordering system (HIS). ORUs message can also contain PDFs. The generated PDF showing the results of patient will be received in HL7 ORU message.

## Features

* Capable of making Laboratory Orders for different samples that define different type of analysis.
* Manage patient information from making order to receiving results.
* Capable of transmitting healthcare standard format, HL7.
* Possible of making order in HL7 ORM message for a single test, panel, both of them.
* Possible of receiving results in HL7 ORU message for a single test, panel, both of them.

## Build

To run this project **java jdk version8**, **maven** and **gradle** are required.

If you need to install those use commands:
```
sudo apt install -y openjdk-8-jdk
sudo apt install -y maven
sudo apt install -y gradle
```

Step 1: Go inside lis-integration project directory
```
cd lis-integration
```

Step 2: Compile the project to generate lis-integration.war file.
```
mvn clean install 
```

Step 3: Move lis-integration.war file from lis-integration project to bahmni-package project
```
mv lis-integration-webapp/target/lis-integration.war /bahmni-package/bahmni-lis/resources/lis-integration/
```
**Note:**  In order for this to function, you must have already cloned Bahmni Package project:
```
git clone git@gitlab.Example.com:it-projects-market/bahmni-package.git
```

Step 4: Genarete new rpm file for lis-integration
```
cd bahmni_package && ./gradlew :bahmni-lis:clean :bahmni-lis:dist
```

Now we should have a generated rpm file in this directory ```bahmni-package/bahmni-lis/build/distributions``` with name **lis-integration-0.93-1.noarch.rpm**.

## Installation

### Pre-requisite: Bahmni installed

Lis-Integration is not a standalone software, so as a pre-requisite for its installation is that Bahmni should be installed as described [here](https://gitlab.Example.com/it-projects-market/bahmni).

### Installation of RPM

Follow these steps to install lis-integration service.

1. Set the value of the port `lis.port` in file application.properties.
2. Move RPM file `lis-integration-0.93-1.noarch.rpm` that should be generated in build step, to the bahmni home directory server.
3. Access bahmni server.
4. Install the RPM file in the bahmni server:  
```
yum install -y /home/lis-integration-0.93-1.noarch.rpm
```
5. Restart the module lis-integration, inside of bahmni server: 
```
systemctl restart lis-integration
```
6. Execute the next command to insert records in lis-intgration database:
a) LIS endpoint in the lis table 
b) Type of the orders in order_type table 
```
psql postgres -d bahmni_lis -c "
INSERT into lis VALUES (1, 'LIS', 'Example LIS', 'XXXX.XXXX.XXXX.XXXX', 12345, 3000);
INSERT into order_type VALUES (1, 'Lab Order', 1);"
```
7. Execute the command to stop iptables service:
```
systemctl stop iptables
```

## Contributing

### Set up LIS-Integration locally

Follow the steps to set up the lis integration project locally, without pushing:

1. Install Postgres locally
2. Start Postgres: ```sudo service postgresql start```
3. Now access postgres: ```sudo su - postgres```
4. Create a new ddatabase: ```create database bahmni_lis;```
5. Create a new user: ```create user lis with password 'password'```
6. Grand: ```grant all privileges on database bahmni_lis to lis```
7. Log out from postgres user: ```exit```
8. Create a folder for logs: ```sudo mkdir /var/log/lis-integration```
9. Change the mode of the folder: ```sudo chmod 777 /var/log/lis-integration```
10. Check if you have the right Java Runtime, for more information check the fifth [step](https://gitlab.Example.com/sgf-it/it-wiki/-/wikis/how-to-beginners/Install-Java-In-VSCode). 
11. Execute the main function in LisIntegration.java class

### Set up GitLab project

To start contributing first fork this repository. Clone the forked repository and follow all steps from the build instructions:
```bash
git clone git@gitlab.Example.com:$USER/lis-integration.git
```

Add this repository as base for all commits:
```bash
git remote add upstream git@gitlab.Example.com:it-projects-market/lis-integration.git
```

Create a new branch from the master every time you prepare a new merge request. 
Best practise is to call the branch as the issue you are working on:
```bash
git fetch upstream master:some-random-issue
```

Apply your changes to the respective files and test them locally. 

Now push to your fork:
```bash
git push origin some-random-issue
```
Now visit https://gitlab.Example.com/YOUR_USER/lis-integration/merge_requests/new?merge_request%5Bsource_branch%5D=some-random-issue

**Note:** If you want to configure other values, like for example bahmni's uuid, please check file: **lis-integration-webapp/src/main/resources/application.properties** 

## Continuous Deployment

As we want to perform continuous deployment a gitlab runner needs to be configured. The gitlab docker executor seems to be the best fit, [unfortunatley it is not possibly to publish ports this way](https://gitlab.com/gitlab-org/gitlab-runner/issues/3500). The [shell executor](https://docs.gitlab.com/runner/executors/#shell-executor) is a workaround for now:
```bash
sudo gitlab-runner register \
--non-interactive \
--url "https://gitlab.Example.com/" \
--registration-token "XXXXXXXXXXXXX" \
--description "lis-integration-shell" \
--executor "shell"
```

### Set Environment Variables

Environment variables are part of the environment in which a process runs. They are useful for customizing your jobs in GitLab CI/CD's pipelines.
GitLab reads the ```.gitlab-ci.yml``` file, sends the information to the Runner (which runs the scripts commands), under which the variable are exposed.

To set the variales, go to the CI/CD settings page:

https://gitlab.Example.com/YOUR_USER/lis-integration/-/settings/ci_cd

Under "Variables" add the following entries:

```
LIS_INCOMING_HL7_PORT=xxxx
SSL_PORT=xxxx
```

### IMPORTANT

For being able to contribute in this project you have to keep in mind that this repository is depended on **it-projects-market-Example-his-configured** image which is created automatically in the runner via running https://gitlab.Example.com/it-projects-market/Example-his

Atomfeed set the markers to first page if you don't set it so: 

 - Set the markers manually after provisioning and before deployment. Especially openmrs encounter feed as we are reading encounter feed to figure out the orders.

 - Use the following sql query to set the markers manually according to the events in your machine (change the last_read_entry_id and feed_uri_for_last_read_entry ).
	```
	insert into markers (feed_uri, last_read_entry_id, feed_uri_for_last_read_entry) values ('http://loalhost:8080/openmrs/ws/atomfeed/encounter/recent', '?', '?');
	```