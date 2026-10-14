# sysc4806Lab: Simple Spring MVC AddressBook App

[![Java CI with Maven](https://github.com/Bxssel05/sysc4806Lab/actions/workflows/maven.yml/badge.svg)](https://github.com/Bxssel05/sysc4806Lab/actions/workflows/maven.yml)

Uses Spring Boot, Spring MVC, Spring Data JPA, H2 and Thymeleaf.

## Run
Open pom.xml as a Maven project in IntelliJ and reload Maven. Set the project
SDK and Maven runner JRE to Java 17 or a compatible newer JDK (the installed
Corretto 26 is used for verification). Run org.mycompany.app.Main.
The terminal default Java 11 is too old; set JAVA_HOME to the selected JDK
before running Maven. Use `mvn clean test`, `mvn package`, and
`java -jar target/sysc4806Lab-1.0-SNAPSHOT.jar` or `mvn spring-boot:run`.

## HTTP API
See HTTP_Requests.txt for an ordered example with generated IDs.

| Method | Path | Body / result |
| --- | --- | --- |
| POST | /api/addressbooks | Send {}; returns 201 and a new book |
| POST | /api/buddies | Send name and phoneNumber JSON; returns 201 and buddy |
| POST | /api/addressbooks/{bookId}/buddies/{buddyId} | No body; returns book with associated buddies |
| GET | /api/addressbooks/{bookId} | Returns book with nested buddies |
| GET | /addressbooks/{bookId}/view | HTML table, empty message, or helpful 404 page |

REST responses are JSON. Creation requires Content-Type: application/json.
Missing IDs return 404. Repeating the same association does not duplicate it.
The unidirectional one-to-many relationship has CascadeType.ALL; a buddy
belongs to at most one book under the existing JPA mapping. Use separate
buddies for separate books. No reverse JSON reference is present.

H2 stores data in memory for the lifetime of the application; restart clears
it, as in the original configuration. Normal startup has no seed data.
The original repository demonstration is available with
`--spring.profiles.active=demo` when running the jar.

Zip the sysc4806Lab directory with pom.xml and HTTP_Requests.txt at its root.
Generated target/ files and personal IDE workspace files may be excluded.

## Verified results
`mvn clean test` and `mvn package` completed successfully using Corretto
26.0.2.1 and Maven 3.9.16. All 16 tests passed with no failures, errors, or
skipped tests. The updated packaged app started on temporary verification port 18084 without errors; normal startup uses port 8080.
Live HTTP requests created a book and two buddies, associated them, and
retrieved both in a later request. The HTML showed both names and phone
numbers. Empty-book output and missing-ID HTTP 404 responses were checked.
No outdated project names remain in editable project files.

## Dependency security update
Spring Boot parent is 4.1.1; starters use its dependency management.
Boot 4 requires the webmvc starter and separate MVC/JPA test starters.
Tests now use the relocated Boot test annotations and Jackson 3 JsonMapper.
Entities, repositories, API URLs and Thymeleaf behavior remain unchanged.

Resolved dependency versions checked with Maven dependency:tree:
- Spring Framework 7.0.9
- Spring Data JPA 4.1.1
- Jackson core/databind 3.1.7 (Jackson 2 core/databind are absent)
- Log4j API 2.25.5
- Tomcat 11.0.26
- Logback core/classic 1.6.5

Tomcat, Logback and Jackson BOM properties intentionally override Boot's older defaults
for vendor-published security fixes. No starter versions are pinned.
Vendor references:
https://spring.io/security/cve-2026-59282/
https://spring.io/security/cve-2026-59283/
https://spring.io/security/cve-2026-47834/
https://tomcat.apache.org/security-11.html
https://logback.qos.ch/news.html
https://logging.apache.org/security.html

After this update, clean test and package passed with all 16 tests. Live HTTP
verification also passed. IntelliJ/Mend inspection cannot be run from this
terminal; reload Maven and refresh its vulnerability inspection to confirm
its current findings. No claim of a clean vulnerability scan is made.

Jackson BOM was updated to 3.1.7 after the remaining Jackson inspection
warnings. Maven confirmed both core and databind resolve to 3.1.7.
Clean test (16 tests), package, and packaged-app live HTTP checks passed.
Patch release reference:
https://github.com/FasterXML/jackson/wiki/Jackson-Release-3.1.7
