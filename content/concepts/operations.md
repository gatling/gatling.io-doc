---
title: Operations
seotitle: Gatling operations reference
description: How to tweak the TCP stack of your computer for more performance.
lead: Learn how to tune the OS for more performance, configure the open files limit, the kernel and the network
date: 2021-04-20T18:30:56+02:00
aliases:
  - /reference/script/core/operations/
---

## IPv4 vs IPv6

IPv6 (enabled by default on Java) was found to sometimes cause some performance issues, so the launch scripts disable it with the following options

```shell
-Djava.net.preferIPv4Stack=true
-Djava.net.preferIPv6Addresses=false
```

If you really need to prefer IPv6, please edit the launch scripts.

## OS tuning

{{< alert tip >}}
Those instructions below are excerpts from the great Riak documentation.
Please refer to the [Open Files Limit](https://github.com/basho/basho_docs/blob/master/content/riak/kv/2.2.3/using/performance/open-files-limit.md/)
and [Kernel and Network Tuning](https://github.com/basho/basho_docs/blob/master/content/riak/kv/2.2.3/using/performance.md#kernel-and-network-tuning)
sections for more details or for instructions for macOS.
{{< /alert >}}

Gatling can consume a very large number of open file handles during normal operation.
Typically, operating systems limit this number, so you may have to tweak a few options in your chosen OS so that you can open *many* new sockets and achieve heavy load.

### Open files limit

Most operating systems can change the open-files limit using the `ulimit -n` command. Example:

```console
ulimit -n 65536
```

However, this only changes the limit for the current shell session. Changing the limit on a system-wide, permanent basis varies more between systems.

To permanently set the soft and hard values *for all users of the system* to allow for up to 65536 open files ; edit `/etc/security/limits.conf` and append the following two lines:

```
*       soft    nofile  65535
*       hard    nofile  65535
```

Save the file. Start a new session so that the limits take effect. You can now verify with `ulimit -a` that the limits are correctly set.

For Debian and Ubuntu, you should enable PAM user limits. To do so, add `session required pam_limits.so` in:

* `/etc/pam.d/common-session`
* `/etc/pam.d/common-session-noninteractive` if the file exists
* `/etc/pam.d/sshd` if you access the machine via SSH

Also, if accessing the machine via SSH, be sure to have `UseLogin yes` in `/etc/ssh/sshd_config`

For more tuning, you may want to do the following:

```console
# more ports for testing
sudo sysctl -w net.ipv4.ip_local_port_range="1025 65535"

# increase the maximum number of possible open file descriptors:
echo 300000 | sudo tee /proc/sys/fs/nr_open
echo 300000 | sudo tee /proc/sys/fs/file-max
```

### Kernel and network tuning

Consider tuning kernel and network and add settings such as the following in `/etc/sysctl.conf`.

```ini
net.ipv4.tcp_max_syn_backlog = 40000
net.core.somaxconn = 40000
net.core.wmem_default = 8388608
net.core.rmem_default = 8388608
net.ipv4.tcp_sack = 1
net.ipv4.tcp_window_scaling = 1
net.ipv4.tcp_fin_timeout = 15
net.ipv4.tcp_keepalive_intvl = 30
net.ipv4.tcp_tw_reuse = 1
net.ipv4.tcp_moderate_rcvbuf = 1
net.core.rmem_max = 134217728
net.core.wmem_max = 134217728
net.ipv4.tcp_mem  = 134217728 134217728 134217728
net.ipv4.tcp_rmem = 4096 277750 134217728
net.ipv4.tcp_wmem = 4096 277750 134217728
net.core.netdev_max_backlog = 300000
```
