# Prerequisites #

The following software is required in order to build source code:
  1. [Maven 2.2.1 or later](http://maven.apache.org/)
  1. [Java 1.7 or later](http://java.com/)

# Building with Maven #

If everything is OK with your environment then the following will build a binary distribution in target directory:
```
cd neerc-soft
mvn install
```

# Editing, compiling and running in IntelliJ IDEA #

The following instructions were tested with [IntelliJ IDEA](http://www.jetbrains.com/idea/) IC-90.96 and Gentoo Linux. However, they should be valid for many other versions of IDEA and OSes.
  * Start IntelliJ IDEA
  * Import Maven project from Chat/pom.xml