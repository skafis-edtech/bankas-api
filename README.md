# api.bankas.skafis.lt
## Dev
1. Add firebase-admin.json file with firebase admin SDK keys to src/main/resources directory
2. Run the application (in IDE or with `./gradlew build` and `./gradlew bootRun`)
3. Open http://localhost:8080/swagger-ui/index.html in browser

## Testing
There are postman tests in TESTS_AND_DOCUMENTATION directory

## Init
start.spring.io

Project: Gradle - Kotlin
Language: Kotlin
Spring Boot: 3.3.0

Project Metadata:
- Group: lt.skafis
- Artifact: bankas
- Name: bankas
- Description: Skafis School Problem Bank
- Package name: lt.skafis.bankas
- Packaging: Jar
- Java: 22

Dependencies:
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Boot DevTools
- Spring Boot Actuator
- Validation
- Lombok

## Auth stuff
1. Firebase to check JWT and retrieve user data
https://medium.com/comsystoreply/authentication-with-firebase-auth-and-spring-security-fcb2c1dc96d
2. Other stuff
https://github.com/naglissul/korys-backend/blob/main/src/main/kotlin/lt/koriodienynas/service/AuthService.kt
https://github.com/Esc-Key-5/back/blob/master/src/main/kotlin/com/example/demo/configuration/WebSecurityConfiguration.kt

## Dir layout
https://malshani-wijekoon.medium.com/spring-boot-folder-structure-best-practices-18ef78a81819

## stuff
Logging - services
exceptions and HTTP codes - controllers (handleded by exception handler bean)