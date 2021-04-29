#!/bin/bash

dir=$(cd -P -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)

cd "$dir"

MYTMPDIR="$(mktemp -d)"

if [ -z "$DEBUG" ]; then
  trap 'rm -rf -- "$MYTMPDIR"' EXIT
fi

fetch_doc () {
  local repo="$1"
  local branch="$2"
  local remote_dir="$3"
  local local_dir="$4"

  rm -rf "$local_dir"
  mkdir -p "$local_dir"
  rm -rf "$MYTMPDIR/$repo" || true
  git clone "https://github.com/gatling/$repo.git" --depth 1 --branch "$2" "$MYTMPDIR/$repo"
  cp -r "$MYTMPDIR/$repo/$remote_dir"/* "$local_dir"
}

hugo mod get -u
hugo mod npm pack

npm install

rm -Rf ./content/* || true
mkdir ./content || true

#          repo                  branch             remote_dir                  local_dir
fetch_doc "frontline-cloud-doc" "hugo-main"        "content"                   "content/cloud"
fetch_doc "frontline-doc"       "hugo-main"        "content"                   "content/self-hosted"
fetch_doc "frontline-doc"       "hugo-main"        "content/reference/current" "content/self-hosted/reference/1.13"
#fetch_doc "gatling"             "misc-96-doc-hugo" "src/sphinx"                "content/oss"

hugo server \
  --buildDrafts \
  --environment development \
  --baseURL "http://localhost:1313"

sleep infinity
