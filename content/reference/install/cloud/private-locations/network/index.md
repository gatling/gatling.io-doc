---
title: Network Requirements for Gatling Control Plane and Private Locations
menutitle: Network
description: Learn how to configure network configuration your Control Plane and Private Locations
lead: Network Configuration
date: 2021-11-07T14:29:04+00:00
---

This guide provides clear instructions for configuring network access for both the Control Plane and Private Locations.

## Network Requirements

{{< alert info >}}
Make sure to first check the architecture diagram section in the [Private Locations Introduction section]({{< ref "reference/install/cloud/private-locations/introduction/#introduction" >}})
{{< /alert >}}

You must permit outbound access to the Gatling Cloud API served from `https://api.gatling.io` from the following components:
* your control plane
* your private locations, as configured by the control plane

`api.gatling.io` uses the following static IPv4 addresses that you can allow in your network configuration:
* 15.236.15.177
* 51.44.121.66
* 52.47.87.192

## Configuring a Forward Proxy

This section describes how to have all the outbound requests from Gatling Enterprise components go through a forward proxy or an API gateway.

### Forward Proxy for the Control Plane

The Control Plane configuration supports the setup of a forward proxy. 

{{< alert info >}}
When configuring the proxy, you must ensure it rewrites the `Host` header to `api.gatling.io` and forwards all other headers.
{{< /alert >}}

Here’s how to configure it in the control-plane section of your configuration:
```bash
control-plane {
  # Authentication token
  token = "cpt_example_c7oze5djp3u14a5xqjanh..." 
  # Enterprise Cloud Network Configuration
  enterprise-cloud {
    proxy {
      # Forward Proxy URL
      forward {
        url = "http://private-control-plane-forward-proxy/gatling"
      }
      # Uncomment if you need to trust custom certificates
      # truststore {
      #   # absolute path to a file containing the certificates in PEM format (can contains multiple concatenated PEM certificates) 
      #   path = "/path/to/truststore.pem" 
      # }
      
      # Uncomment for mutual authentication
      # keystore {
      #   # absolute path to a file containing the key and its certificate in PKCS12 format
      #   path = "/path/to/keystore.p12"
      #   # optional password if needed to open the keystore
      #   # password = "p@ssw0rd"
      # }
    }
  }
}
```

### Forward Proxy for Private Locations

Private Locations can also be configured to use a forward proxy. You have two options:

#### 1. Use the Same Forward Proxy as the Control Plane

You can reuse the Control Plane's forward proxy configuration by leveraging [HOCON substitutions](https://github.com/lightbend/config/blob/main/HOCON.md#substitutions).
```bash
control-plane {
  # Authentication token
  token = "cpt_example_c7oze5djp3u14a5xqjanh..." 
  # Control Plane Enterprise Cloud Network Configuration
  enterprise-cloud {
    proxy {
      # Forward Proxy URL
      forward {
        url = "http://private-control-plane-forward-proxy/gatling"
      }
      # Uncomment if you need to trust custom certificates
      # truststore {
      #   # absolute path to a file containing the certificates in PEM format (can contains multiple concatenated PEM certificates) 
      #   path = "/path/to/truststore.pem" 
      # }
      
      # Uncomment for mutual authentication
      # keystore {
      #   # absolute path to a file containing the key and its certificate in PKCS12 format
      #   path = "/path/to/keystore.p12"
      #   # optional password if needed to open the keystore
      #   # password = "p@ssw0rd"
      # }
    }
  }
  # Private Locations
  locations = [
    {
      # Private Location ID
      id = "prl_example"
      # Private Location using Control Plane Enterprise Cloud Network Configuration
      enterprise-cloud = ${control-plane.enterprise-cloud}
    }
  ]
}
```

#### 2. Use a Dedicated Forward Proxy for Private Locations

If you prefer separate proxies, define a substitution for the private locations and reference it in their configuration.
```bash
location-enterprise-cloud = {
  proxy {
    # Forward Proxy URL
    forward {
      url = "http://location-forward-proxy/gatling"
    }
    # Uncomment if you need to trust custom certificates
    # truststore {
    #   # absolute path to a file containing the certificates in PEM format (can contains multiple concatenated PEM certificates) 
    #   path = "/path/to/truststore.pem" 
    # }
    
    # Uncomment for mutual authentication
    # keystore {
    #   # absolute path to a file containing the key and its certificate in PKCS12 format
    #   path = "/path/to/keystore.p12"
    #   # optional password if needed to open the keystore
    #   # password = "p@ssw0rd"
    # }
  }
}

control-plane {
  # Authentication token
  token = "cpt_example_c7oze5djp3u14a5xqjanh..." 
  # Control Plane Enterprise Cloud Network Configuration
  enterprise-cloud {
    proxy {
      # Forward Proxy URL
      forward {
        url = "http://private-control-plane-forward-proxy/gatling"
      }
      # Uncomment if you need to trust custom certificates
      # truststore {
      #   # absolute path to a file containing the certificates in PEM format (can contains multiple concatenated PEM certificates) 
      #   path = "/path/to/truststore.pem" 
      # }
      
      # Uncomment for mutual authentication
      # keystore {
      #   # absolute path to a file containing the key and its certificate in PKCS12 format
      #   path = "/path/to/keystore.p12"
      #   # optional password if needed to open the keystore
      #   # password = "p@ssw0rd"
      # }
    }
  }
  # Private Locations
  locations = [
    {
      # Private Location ID
      id = "prl_example_a"
      # Private Location Enterprise Cloud Network Configuration
      enterprise-cloud = ${location-enterprise-cloud}
    },
    {
      # Private Location ID
      id = "prl_example_b"
      # Private Location Enterprise Cloud Network Configuration
      enterprise-cloud = ${location-enterprise-cloud}
    }
  ]
}
```

### Key Notes

* **Host Header Rewriting**: Ensure all configured forward proxies rewrite the host header to `api.gatling.io`. This is a
    mandatory requirement for proper communication.
* **Configuration Simplification**: Take advantage of HOCON substitutions to reuse and simplify configuration settings,
    minimizing redundancy and reducing maintenance effort.
* **Proxy Separation**: Decide whether to use a single proxy for both the Control Plane and Private Locations or
    separate proxies for each, based on your infrastructure needs.
* **File Access**: Ensure that truststore and keystore are accessible when using them. Mind that they may be located at
    different places for **Control plane** and **Load generators** depending on the way you mount volumes and/or generated the images.
* **Full access** to the api.gateway.io domain: Don't expect to be able to cherry-pick allowed routes, headers, payloads,
    etc. This is an internal API, subject to change any time without prior notice.
* Ensure enough **payload size**:
  * upload: **5MB** from your network to Gatling API.
  * download: **5GB** from Gatling API to your network.
