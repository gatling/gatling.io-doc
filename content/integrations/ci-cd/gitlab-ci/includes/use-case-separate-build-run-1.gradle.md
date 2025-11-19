```yaml
workflow:
  rules:
    # Execute the pipeline only on pushes to the main branch
    - if: $CI_COMMIT_BRANCH == "main"

stages:
  - build

variables:
  SIMULATION_ID: 'test_00000000000000000000000000'

# Build and deploy your Gatling project
build-gatling-simulation:
  stage: build
  # Gradle 8 and JDK 17; see https://hub.docker.com/_/gradle for other tags available
  # See also https://gitlab.com/gitlab-org/gitlab/-/blob/master/lib/gitlab/ci/templates/Gradle.gitlab-ci.yml for other useful options for Gradle builds.
  # See https://docs.gatling.io/reference/integrations/build-tools/maven-plugin/#deploying-on-gatling-enterprise for options.
  image: gradle:8-jdk17
  script:
    - gradle gatlingEnterpriseDeploy -Dgatling.enterprise.validateSimulationId=$SIMULATION_ID
```
