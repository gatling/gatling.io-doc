| **Setting** | **Where you set it** |
| --- | --- |
| `GATLING_SERVER_PUBLIC_BASE_URL` | Set by the module, to the service's `run.app` URL |
| `GATLING_MODEL_ENDPOINT_BASE_URL` | `server.model.base_url` |
| `GATLING_MODEL_ENDPOINT_MODEL` | `server.model.name` |
| `GATLING_MODEL_ENDPOINT_API_KEY` | `server.model.api_key_secret`, the name of a Secret Manager secret |
| `GATLING_MCP_REQUEST_STATE_KEY` | `server.request_state_key_secret`, the name of a Secret Manager secret |
| Any other setting | `container.environment` |

- The module refuses a `container.environment` that sets one of the first five settings.
- `container.environment` is stored in plain text in the Terraform state: put no secret there.
- The server reads the `latest` version of each secret when it starts: a new version takes effect at the next
  restart.
- Cloud Run ends a request after 630 seconds: keep `GATLING_SERVER_SYNC_TIMEOUT` at or under its default, `PT9M30S`.
- Cloud Run caps an HTTP/1 request at 32 MiB: keep `GATLING_UPLOAD_MAX_BYTES` under it.

The [Google Cloud limits]({{< ref "/ai/server/installation/terraform#google-cloud-limits" >}}) give the details.
