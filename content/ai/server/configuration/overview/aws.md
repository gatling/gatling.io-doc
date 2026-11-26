| **Setting** | **Where you set it** |
| --- | --- |
| `GATLING_SERVER_PUBLIC_BASE_URL` | `server.public_base_url` |
| `GATLING_MODEL_ENDPOINT_BASE_URL` | `server.model.base_url` |
| `GATLING_MODEL_ENDPOINT_MODEL` | `server.model.name` |
| `GATLING_MODEL_ENDPOINT_API_KEY` | `server.model.api_key_secret_arn`, the ARN of a Secrets Manager secret |
| `GATLING_MCP_REQUEST_STATE_KEY` | `server.request_state_key_secret_arn`, the ARN of a Secrets Manager secret |
| Any other setting | `task.environment` |

- The module refuses a `task.environment` that sets one of the first five settings: set them through `server`.
- `task.environment` is readable in the task definition and stored in plain text in the Terraform state: put no secret
  there.
- ECS reads the secrets when a task starts: after you change a secret's value, start a new task.
- The load balancer closes a connection after 630 seconds of silence: keep `GATLING_SERVER_SYNC_TIMEOUT` at or below
  its default, `PT9M30S`.

The [AWS limits]({{< ref "/ai/server/installation/terraform#aws-limits" >}}) give the details.
