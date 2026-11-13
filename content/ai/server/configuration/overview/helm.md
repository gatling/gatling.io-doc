| **Setting** | **Where you set it** |
| --- | --- |
| `GATLING_SERVER_PUBLIC_BASE_URL` | `server.publicBaseUrl` |
| `GATLING_MODEL_ENDPOINT_BASE_URL` | `server.model.baseUrl` |
| `GATLING_MODEL_ENDPOINT_MODEL` | `server.model.name` |
| `GATLING_MODEL_ENDPOINT_API_KEY` | The key `model-api-key` of the Secret that `secret.name` names |
| `GATLING_MCP_REQUEST_STATE_KEY` | The key `request-state-key` of the same Secret |
| Any other setting | `env`, as `name` and `value` entries, or `envFrom`, from a whole ConfigMap or Secret |

- To give the Secret's keys other names, set `secret.modelApiKeyKey` and `secret.requestStateKeyKey`.
- The server reads the Secret at startup: after you change one of its keys, restart the server.
- If you raise `GATLING_SERVER_SYNC_TIMEOUT`, keep your ingress timeout at least 60 seconds above it.

[Install with Helm]({{< ref "/ai/server/installation/helm#change-a-setting" >}}) shows how to apply a change.
