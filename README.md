## Recent Changes
[Upgrading old dependencies to newest versions.](https://github.com/nighthawkcoders/spring_portfolio/issues/12)

# [Spring Portfolio Starter](https://nighthawkcodingsociety.com/projectsearch/details/Spring%20Portfolio%20Starter)

- Runtime link: https://spring.nighthawkcodingsociety.com/
- JWT Login: https://nighthawkcoders.github.io/APCSA/data/login
- Jokes endpoint: https://spring.nighthawkcodingsociety.com/api/jokes/



## Visual thoughts

- Starter code should be fun and practical
- Organize with Bootstrap menu 
- Add some color and fun through VANTA Visuals (birds, halo, solar, net)
- Show some practical and fun links (hrefs) like Twitter, Git, Youtube
- Show student project specific links (hrefs) per page
- Show student About me pages

## Getting started

- Clone project and open in VSCode
- Verify Project Structure to use a good Java JDK (adoptopenjdk:17)
- Play or entry point is Main.java, look for Run option in code.  This eanbles Spring to load
- Java source (src/main/java/...) has Java files.  Find "controllers" path, these files enable HTTP route and HTML file relationship.
- HTML source (src/main/resources/...) had templates and supporting files.  Find index.html as this file is launched by defaul in Spring.  Other HTML files are loaded by building an "@Controller"
- Use java version 17:
    - to install, run: sudo apt-get update -y;sudo apt-get upgrade -y;sudo apt install openjdk-17-jdk openjdk-17-jre
        - sudo apt-get update -y: updates package index files on your system
        - sudo apt-get upgrade -y: downloads and installs most recent packages
        - sudo apt install openjdk-17-jdk openjdk-17-jre: installs java 17 onto your system

## IDE management

- A ".gitignore" can teach a Developer a lot about Java runtime.  A target directory is created when you press play button, byte code is generated and files are moved into this location.
- "pom.xml" file can teach you a lot about Java dependencies.  This is similar to "requirements.txt" file in Python.  It manages packages and dependencies.
