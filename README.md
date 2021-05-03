# gatling.io - documentation section

This repository aims to aggregate the documentation from our different products.

No modification should be needed from our dear customers in this repository.

For documentation modification, please refer to the documentation product repository

| Product | Repo | Path in repo | Documentation HTML |
| ------- | ---- | ------------ | ------------------ |
| gatling | [gatling/gatling](https://github.com/gatling/gatling) | [`src/sphinx/`](https://github.com/gatling/gatling/tree/master/src/sphinx) | [:link:](https://gatling.io/docs/oss/) |
| FrontLine | [gatling/frontline-doc](https://github.com/gatling/frontline-doc) | [`content/`](https://github.com/gatling/frontline-doc/tree/main/content) | [:link:](https://gatling.io/docs/self-hosted/) |
| FrontLine SaaS (beta) | [gatling/frontline-cloud-doc](https://github.com/gatling/frontline-cloud-doc) | [`content/`](https://github.com/gatling/frontline-cloud-doc/tree/main/content) | [:link:](https://gatling.io/docs/cloud/) |


## Development

Either way, once launched, you can visit at [http://localhost:1313/docs](http://localhost:1313/docs)

### Docker-compose

This project provide a `docker-compose.yml` configuration

```console
docker-compose up
```

### Local

```console
./bin/entrypoint.sh
```
