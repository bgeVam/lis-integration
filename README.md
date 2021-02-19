# LIS-Integration

## Features

## Set up 

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

Step 4: Genarete new rpm file for lis-integration
```
cd bahmni_package && ./gradlew :bahmni-lis:clean :bahmni-lis:dist
```

Now we should have a generated rpm file in this directory ```bahmni-package/bahmni-lis/build/distributions``` with name **lis-integration-0.93-1.noarch.rpm**.

### Contributing

To start contributing first fork this repository. Clone the forked repository and follow all steps from the build instructions:
```bash
git clone git@gitlab.Example.com:$USER/lis-integration.git
```

Add the this repository as base for all commits:
```bash
git remote add upstream git@gitlab.Example.com:it-projects-market/lis-integration.git
```

Create a new branch from the master every time you prepare a new merge request. Best practise is to call the branch as the issue you are working on:
```bash
git fetch upstream master:some-random-issue
```

Apply your changes to the respective files and test them locally. 

Now push to your fork:
```bash
git push origin some-random-issue
```
Now visit https://gitlab.Example.com/YOUR USER/lis-integration/merge_requests/new?merge_request%5Bsource_branch%5D=some-random-issue

### IMPORTANT

For being able to contribute in this project you have to keep in mind that this repository is depended on **it-projects-market-Example-his-configured** image which is created automatically in the runner via running https://gitlab.Example.com/it-projects-market/Example-his

Atomfeed set the markers to first page if you don't set it so: 

 - Set the markers manually after provisioning and before deployment. Especially openmrs encounter feed as we are reading encounter feed to figure out the orders.

 - Use the following sql query to set the markers manually according to the events in your machine (change the last_read_entry_id and feed_uri_for_last_read_entry ).
	```
	insert into markers (feed_uri, last_read_entry_id, feed_uri_for_last_read_entry) values ('http://loalhost:8080/openmrs/ws/atomfeed/encounter/recent', '?', '?');
	```