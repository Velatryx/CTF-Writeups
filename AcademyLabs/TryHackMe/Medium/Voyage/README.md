## Voyage Writeup

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/voyage.png)

Room Description: Chain multiple vulnerabilities to gain control of a system.

**Room Link**: [Voyage Room](https://tryhackme.com/room/voyage)

> Sometimes in a pentest, you get root access very quickly. But is it the real root or just a container? The voyage might still be going on.

---

## Objectives

1. What is the value of user-level flag?
2. What is the value of root-level flag?

---

## Summary
- **Target IP:** 10.128.162.162
- **OS:** Linux (Ubuntu)
- **Vulnerabilities:**

| Port | State | Service | Service Version / Info |
| --- | --- | --- | --- |
| **`22/tcp`** | `OPEN` | **SSH** | OpenSSH 9.6p1 Ubuntu 3ubuntu13.11 |
| **`80/tcp`** | `OPEN` | **HTTP** | Apache httpd 2.4.58 ((Ubuntu)) |
| **`2222/tcp`** | `OPEN` | **SSH** | OpenSSH 8.2p1 Ubuntu 4ubuntu0.13 |

---

## Adding target to hosts

```shell
echo -e '10.128.162.162 voyage.thm' | sudo tee -a /etc/hosts
```

## Enumeration & Reconnaissance

> Rustscan

```
rustscan -a voyage.thm --ulimit 5000 -- -sCV -O
------------------------------------------------
22/tcp   open  ssh     syn-ack ttl 62 OpenSSH 9.6p1 Ubuntu 3ubuntu13.11 (Ubuntu Linux; protocol 2.0)
80/tcp   open  http    syn-ack ttl 62 Apache httpd 2.4.58 ((Ubuntu))
|_http-favicon: Unknown favicon MD5: 1B6942E22443109DAEA739524AB74123
| http-robots.txt: 16 disallowed entries 
| /joomla/administrator/ /administrator/ /api/ /bin/ 
| /cache/ /cli/ /components/ /includes/ /installation/ 
|_/language/ /layouts/ /libraries/ /logs/ /modules/ /plugins/ /tmp/
|_http-generator: Joomla! - Open Source Content Management
|_http-server-header: Apache/2.4.58 (Ubuntu)
| http-methods: 
|_  Supported Methods: GET HEAD POST OPTIONS
2222/tcp open  ssh     syn-ack ttl 61 OpenSSH 8.2p1 Ubuntu 4ubuntu0.13 (Ubuntu Linux; protocol 2.0)
```

