# api.bankas.skafis.lt
## Tech
NoSQL - Firebase Firestore
Spring Boot - Kotlin
Gradle
Docker
Render - deploy
Jenkins - later...
AWS - later... for deploy

## Dev
1. Somehow get firebase-admin.json file (from firebase console) - have it anywhere on your computer
2. Set environment variable `FIREBASE_SERVICE_ACCOUNT_PATH` to the path of the file (on intellij - edit configuration, or just add args to bootRun command), or if running with docker compose - replace "path/on/your/device/firebase-admin.json" with the path to the file
3. Run the application (in IDE or with `./gradlew build` and `./gradlew bootRun`)
4. Open http://localhost:8080/swagger-ui/index.html in browser

## Prod
1. Build the image `docker build -t naglissul/bankas-skafis-api:latest .`
2. push image to dockerhub registry `docker push naglissul/bankas-skafis-api:latest` (you need to be logged in to dockerhub `docker login`)
3. `docker compose up -d`
4. deploy to Render - upload firebase-admin.json file to render secrets /etc/secrets/firebase-admin.json, set env var FIREBASE_SERVICE_ACCOUNT_PATH to /etc/secrets/firebase-admin.json
5. add custom domain to Render AND for you domain provider add CNAME record to Render domain

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

## deploy stuff - 2024-06-15
Docker, DockerHub, Render (later - AWS)
https://medium.com/spring-boot/free-hosting-bliss-deploying-your-spring-boot-app-on-render-d0ebd9713b9d

## DEploy stuff 2024-06-17
So I deploy using Render. and for that i need to push to dockerhub PUBLIC image. And I dont want to share firebase admin sdk, ofc :DD 

Long story short - env variables and secrets IS PAIN IN THE A** when dealing with free deployment services.

I needed dynamic file loading for firebase sdk file (FirebaseConfig). BUT IT DIDN'T WORK WHEN COPY PASTING FROM CHATGPT!!! So I started slow - first, read env variables, then read file content, the read file content from env var specified path, then config prod/dev env and then DEPLOY yey.