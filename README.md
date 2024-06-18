# bankas.skafis.lt API
[https://api.bankas.skafis.lt](https://api.bankas.skafis.lt)

## Tech
NoSQL - Firebase Firestore
Spring Boot - Kotlin
Gradle
Docker
Render - deploy
Jenkins - later...
AWS EC2 - for cron job (using crontab on ubuntu) (to trigger Render deployed service every 14 minutes)

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

## Prod deploy - for me
Just run `basic_deploy.ps1` script on your computer and check `basic_deploy_logs.txt` for errors

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

AND FINALLY WORKS. Here's how: 
- locally (intellij) go to top rght corner (configurations) and click three dots and then edit configuration. THen click "Modify options" and then add environmental variables - add varable FIREBASE_SERVICE_ACCOUNT_PATH=C:\pathpath\firebase.json. ANd then run.
- For deploy - build image, push image to dockerhub, on Render pull that image from dockerhub registry, add env var FIREBASE_SERVICE_ACCOUNT_PATH=/etc/secrets/firebase-admin.json, add secret firebase-admin.json file to Render secrets. And then deploy. And then add custom domain to Render and add CNAME record to your domain provider.

## Jenkins
I need to trigger Render service that is deployed every 15 minutes (cuz if it's inactive, it shuts down).

I use AWS for that. For that, FIRSTLY SETUP BILLING ALERT!!!:
- https://us-east-1.console.aws.amazon.com/billing/home#/preferences >>> Free tier alert.
- https://us-east-1.console.aws.amazon.com/billing/home#/budgets >>> Create budgets (recommendation - create 2 separate: for zero budget and for montly budget threshold (e.g. 10 USD)).

Then, create an EC2 instance and setup jenkins (here tutorial):
- https://www.jenkins.io/doc/tutorials/tutorial-for-installing-jenkins-on-AWS/#launching-an-amazon-ec2-instance

Yeah, it was not enough space for jekins on a free tier EC2 instance, o I just made cron job on ubuntu and that's it. It sends GET problem count every 14 mins.
`*/14 * * * * curl -X GET https://bankas-skafis-api-latest.onrender.com/api/problems/count >> curl_job.log`

## Stuff -2024-06-18
Not using postgresql - this for later - but do all the review stuff and namings immediately. Not later. Also writing postman tests, gonna add collections later. 

