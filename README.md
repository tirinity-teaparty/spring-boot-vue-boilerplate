# spring-boot-vue-boilerplate
This boilerplate project demonstrates how to render Vue components in a Spring Boot web application using Thymeleaf.

## Overview
This project demonstrates how to integrate Vue with a Spring Boot web application using Thymeleaf. The core concept is to copy the static build output from the Vue project into the static resources folder of the Spring Boot application. Vue components are rendered by attaching multiple root elements to corresponding HTML DOM nodes, each identified by an `section-*` prefix.

## Technologies
* Backend
  * Spring Boot
  * Thymeleaf Template Engine
* Frontend
* Vue
  * TypeScript

## Requirements
* JDK 17+
* Node 20+

## Project Structure
* vue-common
  * A Vue project. Its build output is copied into java backend modules.
* spring-boot-java-vue
  * A Java-based Spring Boot project that serves the bundled Vue frontend.

## Run Instruction
### Overview
You can run either the Java project, depending on your preference.

### Frontend (Vue)
* Default server port: 3000

```bash
cd vue-common
yarn install
yarn build    # or `yarn serve` for dev mode (when use-vue-bundle = false)
```
### Backend (Java)
* Default server port: 8080

```bash
cd spring-boot-java-vue
./gradlew bootRun
```

## Related Projects
* https://github.com/rheech/spring-boot-react-boilerplate
* https://github.com/AkiaCode/python-react-boilerplate
* https://github.com/trinity-teaparty/python-vue-boilerplate
