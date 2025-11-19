```yaml
workflow:
  rules:
    # Execute the pipeline only on pushes to the main branch
    - if: $CI_COMMIT_BRANCH == "main"

stages:
  - build
  - load-test

variables:
  SIMULATION_ID: 'test_00000000000000000000000000'

# Build and deploy your Gatling project
build-gatling-simulation:
  stage: build
  # JDK 17 from Azul; see https://hub.docker.com/r/azul/zulu-openjdk for other tags available, or use another image configured with a JDK
  # See also https://gitlab.com/gitlab-org/gitlab/-/blob/master/lib/gitlab/ci/templates/Maven.gitlab-ci.yml for other useful options for Maven builds.
  # See https://docs.gatling.io/reference/integrations/build-tools/maven-plugin/#deploying-on-gatling-enterprise for options.
  image: azul/zulu-openjdk:17-latest
  script:
    - ./mvnw gatling:enterpriseDeploy -Dgatling.enterprise.validateSimulationId=$SIMULATION_ID

# Run the simulation on Gatling Enterprise Edition
run-gatling-enterprise:
  stage: load-test
  image:
    name: gatlingcorp/enterprise-runner:1
    entrypoint: ['']
  script:
    - gatlingEnterpriseStart
```
