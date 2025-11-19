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
  # Maven 3 and JDK 17; see https://hub.docker.com/_/maven for other tags available
  # See also https://gitlab.com/gitlab-org/gitlab/-/blob/master/lib/gitlab/ci/templates/Maven.gitlab-ci.yml for other useful options for Maven builds.
  # See https://docs.gatling.io/reference/integrations/build-tools/maven-plugin/#deploying-on-gatling-enterprise for options.
  image: maven:3-openjdk-17-slim
  script:
    - mvn gatling:enterpriseDeploy -Dgatling.enterprise.validateSimulationId=$SIMULATION_ID
```
