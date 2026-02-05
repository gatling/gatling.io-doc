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

IPv6 (enabled by default on Java) was found to sometimes cause some performance issues, so the launch scripts disable it with the following options:

```shell
-Djava.net.preferIPv4Stack=true
-Djava.net.preferIPv6Addresses=false
```

If you really need to prefer IPv6, please edit the launch scripts.

## OS tuning

Gatling can consume a very large number of open file handles during normal operation.
Typically, operating systems limit this number, so you may have to tweak a few options in your chosen OS so that you can open *many* new sockets and achieve heavy load.

### Kernel and network tuning

Consider tuning kernel and network by adding settings such as the following in `/etc/sysctl.d/99-gatling.conf`:

```ini
fs.nr_open = 1073741816

net.ipv4.ip_local_port_range = 1024 65535
net.ipv4.tcp_fin_timeout = 15
net.ipv4.tcp_max_tw_buckets = 8192
net.ipv4.tcp_tw_reuse = 1

net.ipv4.tcp_slow_start_after_idle = 0

net.core.optmem_max = 131072
net.ipv4.tcp_rmem = 4096 131072 33554432
net.ipv4.tcp_wmem = 4096 16384 4194304
```

In newer kernel (6.1+), as `fs.file-max` defaults to the largest signed 64 bits integer, it doesn't need to be changed.
If it is not the case, you can add it to the previous configuration.
This can be checked with:

```console
$ cat /proc/sys/fs/file-max
9223372036854775807
```

Apply the configuration by using:

```console
sudo sysctl -p /etc/sysctl.d/99-gatling.conf
```


### Open files limit

{{< alert warning >}}
Make sure to increase these limits after applying the previous sysctl parameters.
{{< /alert >}}

Most operating systems can change the open-files limit using the `ulimit -n` command. Example:

```console
ulimit -n 1048576
```

However, this only changes the limit for the current shell session. Changing the limit on a system-wide, permanent basis varies more between systems.

To permanently set the soft and hard values *for all users of the system* to allow for up to 1048576 open files; create or edit `/etc/security/limits.d/99-gatling.conf` and add the following two lines:

```
*         soft   nofile 1048576
*         hard   nofile 1048576
```

Save the file. Start a new session so that the limits take effect. You can now verify with `ulimit -n` that the limits are correctly set.

Also, if accessing the machine via SSH, be sure to have `UsePAM yes` in `/etc/ssh/sshd_config`.

### Systemd limits

The SSH daemon can also be limited by Systemd, which can be checked with:

```console
$ systemctl show sshd -p LimitNOFILE
LimitNOFILE=65535
```

If you see this number is below our settings, you can either edit the `sshd` service file and add
`LimitNOFILE=1048576` in the `Service` section or create a file called `/etc/systemd/system.conf.d/99-gatling.conf` with
the following content:

```
[Manager]
DefaultLimitNOFILE=1048576:1048576
```

The `/etc/systemd/system.conf.d` may not exist yet and can be created with:

```
sudo mkdir /etc/systemd/system.conf.d
```

In Amazon Linux 2023, there is an existing file with default limits you can edit: `/etc/systemd/system.conf.d/50-limits.conf`.

Don't forget to reload the Systemd daemon after any modification, then check the limit again:

```console
$ sudo systemctl daemon-reload
$ systemctl show sshd -p LimitNOFILE
LimitNOFILE=1048576
```
