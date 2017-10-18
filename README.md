# graphql_code_challenge

## Introduction

This repository contains my solution to the Back-end Code Challenge. This file contains the instructions to run the project locally
and is structured as follows: first, the prerequisites are listed. Next, the steps to setup the project are explained and, finally,
the methods to actually run the project are given.

## Prerequisites

* [Maven](https://maven.apache.org/) - Dependency Management
* [MongoDB](https://www.mongodb.com/) - Database

## Setup

Follow the following steps to setup the project in your local computer:

* Call following command inside directory where project is to be created (Replace 'groupId' and 'artifactId' with the desired
values. Confirm with 'y' when prompted): 

```
mvn archetype:generate -DarchetypeArtifactId=maven-archetype-webapp -DgroupId=<groupId> 
-DartifactId=<artifactId> -Dversion=1.0-SNAPSHOT
```
* Create 'java' directory under 'src/main' of the newly created project folder.
* Copy all contents of 'src/main/java/' of this repository to the folder created in the previous step. This includes all the java classes used by the project
* The host and port of the used database (as well as the database and colection names) are defined in the class GraphQLEndpoint 
in line 16. You can modify them at will.
* Delete all folders and files inside the project's 'src/main/webapp' folder.
* Copy 'src/main/webapp/index.html' from repository to 'src/main/webapp' directory of the project.
* Copy 'src/main/resources/schema.graphqls' from repository to 'src/main/resources' directory of the project.
* Copy 'pdvs.json' file from repository to project's folder (where 'pom.xml' is also found).
* Add the following dependencies to the project's 'pom.xml' file:

```
<dependency>
      <groupId>com.graphql-java</groupId>
      <artifactId>graphql-java</artifactId>
      <version>3.0.0</version>
    </dependency>
    <dependency>
      <groupId>com.graphql-java</groupId>
      <artifactId>graphql-java-tools</artifactId>
      <version>3.2.0</version>
    </dependency>
    <dependency>
      <groupId>com.graphql-java</groupId>
      <artifactId>graphql-java-servlet</artifactId>
      <version>4.0.0</version>
    </dependency>
    <dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>javax.servlet-api</artifactId>
      <version>3.0.1</version>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>org.mongodb</groupId>
      <artifactId>mongodb-driver</artifactId>
      <version>3.4.2</version>
    </dependency>

    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.0.13</version>
      <exclusions>
        <exclusion>
          <!-- Defined below -->
          <artifactId>slf4j-api</artifactId>
          <groupId>org.slf4j</groupId>
        </exclusion>
      </exclusions>
    </dependency>

    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>1.7.25</version>
    </dependency>

    <dependency>
      <groupId>org.json</groupId>
      <artifactId>json</artifactId>
      <version>20090211</version>
</dependency>
```

* Add the following plugins to the project's pom.xml file (Directly under 'finalName'):

```
<plugins>
      <plugin>
        <groupId>org.eclipse.jetty</groupId>
        <artifactId>jetty-maven-plugin</artifactId>
        <version>9.4.6.v20170531</version>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.5.1</version>
        <configuration>
          <source>1.8</source>
          <target>1.8</target>
        </configuration>
      </plugin>

      <plugin>
        <artifactId>maven-war-plugin</artifactId>
        <version>3.1.0</version>
      </plugin>
</plugins>
```

## Running the project

Now that the project is all setup, it can actually be run. To do this, first run the following three steps:

* Run mongod
* Call command below from folder where project was created: 
```
mvn jetty:run
```
* Open any brower and go to "localhost:8080"

The last step opens GraphiQL on your browser. The following subsections show how to use query and mutation functions that
represent the features requested by the challenge. You can use any example below and run them on GraphiQL to test the API.

### Get all PDVs

Gets all the PDVs in the current database: 

```
{
  allPDVs {
    id
    tradingName
    ownerName
    document
    coverageArea {
	    type
	    coordinates
    }
    address {
      type
      coordinates
    }
  }
}

```

### Get PDV by id

Searches for PDV with a specific id in the database. Returns it if found. Example: 
```
{
  getPDV(id: "2"){
    id
    tradingName
    ownerName
    document
    coverageArea {
      type
      coordinates
    }
    address {
      type
      coordinates
    }
  }
}

```

### Create PDV

Adds new PDV to database. Note that if the passed id or document/CNPJ values already belong to a PDV in the database, the new 
PDV is not added to it, even though the new PDV object is returned. Furthermore, all fields have to be filled when creating a 
PDV as shown in the example below:

```
mutation createPDV {
  createPDV(
    id: "100", 
    ownerName: "Ze Bandeira", 
    tradingName: "Hannover Beer Center",
    document: "12311211/2323",
    coverageArea: {
      type: "MultiPolygon"
      coordinates: [[[[1,2], [3,4], [20,5], [-4, 10]]]]
    }
    address: {
      type: "Point"
      coordinates: [35, 33]
    }		
  )
  {
    id
    ownerName
    tradingName
    document
    coverageArea {
      type
      coordinates
    }
    address {
      type
      coordinates
    }
  }
}
```

### Search Closest PDV

Searches the closest PDV that will deliver to a specific location. If one is not found, null is returned. Example:

```
{
  searchClosestPDV(lng: -43.43, lat: -22.747){
    id
    tradingName
    ownerName
    document
    coverageArea {
      type
      coordinates
    }
    address {
      type
      coordinates
    }
  }
}

```
