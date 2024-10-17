# bankas.skafis.lt API
[https://api.bankas.skafis.lt](https://api.bankas.skafis.lt)

## Tech
- Spring Boot - Kotlin
- Gradle
- ...and everything that is described in the bankas-skafis frontend repo...
- SENTRY for error tracking

## Dev
1. Somehow get firebase-admin.json file (from firebase console) - have it anywhere on your computer
2. Set environment variables `FIREBASE_SERVICE_ACCOUNT_PATH` to the path of the file (on intellij - edit configuration, or just add args to bootRun command), or if running with docker compose - replace "path/on/your/device/firebase-admin.json" with the path to the file, and `SENTRY_AUTH_TOKEN` with your sentry auth token.
3. Run the application (in IDE or with `./gradlew build` and `./gradlew bootRun`)
4. Open http://localhost:9000/swagger-ui/index.html in browser

OR you can setup env vars ENVIRONMENT and PORT to prod and 80.

## Prod
Merge to main branch on github and gh actions will take over, push image to dockerhub and trigger deploy on render.com.

For tagged releases to push to dockerhub:
1. Build the image `docker build -t naglissul/bankas-skafis-api:TAG .`
2. push image to dockerhub registry `docker push naglissul/bankas-skafis-api:TAG` (you need to be logged in to dockerhub `docker login`)

Having image pushed you can sun container with this:
`docker compose up -d`

## Sentry (CURRENTLY NOT WORKING)
For now it's self hosted, used on cloud. So, firstly, you need Sentry account. THen add `SENTRY_AUTH_TOKEN` and `SENTRY_DSN` env var.

Since this shit happens: `Could not start '/home/.../build/tmp/sentry-cli-2.37.0.exe'` with this line `id("io.sentry.jvm.gradle") version "4.12.0"`

I do it by hand (on linux)
```
curl -sL https://github.com/getsentry/sentry-cli/releases/download/2.37.0/sentry-cli-Linux-x86_64 -o sentry-cli
chmod +x sentry-cli
sudo mv sentry-cli /usr/local/bin/sentry-cli

```