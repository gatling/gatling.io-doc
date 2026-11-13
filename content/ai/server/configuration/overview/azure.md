| **Setting** | **Where you set it** |
| --- | --- |
| `GATLING_SERVER_PUBLIC_BASE_URL` | Set by the module, to the container app's address |
| `GATLING_MODEL_ENDPOINT_BASE_URL` | `server.model.base_url` |
| `GATLING_MODEL_ENDPOINT_MODEL` | `server.model.name` |
| `GATLING_MODEL_ENDPOINT_API_KEY` | `server.model.api_key_secret_id`, the ID of a Key Vault secret |
| `GATLING_MCP_REQUEST_STATE_KEY` | `server.request_state_key_secret_id`, the ID of a Key Vault secret |
| `GATLING_SERVER_SYNC_TIMEOUT` | Set by the module, to `PT3M` |
| `GATLING_MCP_CONTINUE_ON_RETRY` | Set by the module, to `true` |
| Any other setting | `container.environment` |

- Azure Container Apps ends every HTTP request at 240 seconds. That's why the module sets
  `GATLING_SERVER_SYNC_TIMEOUT` and `GATLING_MCP_CONTINUE_ON_RETRY`: a tool call waits 3 minutes for its conversion,
  then hands a conversion still running back to a client on a stateless MCP session, which follows it by retrying.
  This needs the request state key. For a client that can't retry, a conversion that runs past 3 minutes is canceled.
- The module refuses a `container.environment` that sets one of the seven variables above, in any form, including as a
  `-D` system property. Leave them out of the map, `GATLING_SERVER_SYNC_TIMEOUT` included.
- `container.environment` is stored in plain text in the Terraform state: put no secret there.
- With a secret ID without a version, Container Apps reads a new version of the secret within 30 minutes and restarts
  the server.

The [Azure limits]({{< ref "/ai/server/installation/terraform#azure-limits" >}}) give the details.
