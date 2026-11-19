# Dev image for docker-compose: Hugo dropped LibSass in 0.170, and the base image does not ship Dart Sass
FROM floryn90/hugo:0.170.1-ext-alpine

ARG DART_SASS_VERSION=1.108.2

# Hugo's Node.js ESM resolver hook fails on relative NODE_PATH entries, like the base image's "."
ENV NODE_PATH=/usr/local/lib/node_modules

USER root

RUN arch="$(uname -m | sed -e 's/x86_64/x64/' -e 's/aarch64/arm64/')" \
  && wget -qO- "https://github.com/sass/dart-sass/releases/download/${DART_SASS_VERSION}/dart-sass-${DART_SASS_VERSION}-linux-${arch}-musl.tar.gz" \
    | tar -xz -C /opt \
  && ln -s /opt/dart-sass/sass /usr/local/bin/sass

USER hugo
