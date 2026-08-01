## Second — TryHackMe Writeup

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Second/Images/second.png)

**Room Description:** You Shall Fear The Second Order.

> *"Being second isn't such a bad thing, but not in this case."*

---

## Objectives

* [*] **User Flag:** `user.txt`
* [*] **Root Flag:** `root.txt`

---

## Enumeration & Reconnaissance

### 1. Port Scanning

We begin initial enumeration using `rustscan` coupled with standard `nmap` version scanning scripts:

```bash
rustscan -a second.thm --ulimit 5000 -- -sCV -O

```

#### Open Ports

| Port | State | Service | Service Version / Info |
| --- | --- | --- | --- |
| **`22/tcp`** | `OPEN` | **SSH** | OpenSSH 8.2p1 Ubuntu 4ubuntu0.13 |
| **`8000/tcp`** | `OPEN` | **HTTP** | Werkzeug httpd 2.0.3 (Python 3.8.10) |


> Output

```nmap
PORT     STATE SERVICE REASON         VERSION
22/tcp   open  ssh     syn-ack ttl 62 OpenSSH 8.2p1 Ubuntu 4ubuntu0.13 (Ubuntu Linux; protocol 2.0)
| ssh-hostkey: 
|   3072 48:fd:b6:d0:ea:82:f3:75:85:2d:09:73:13:cb:94:ea (RSA)
|   256 8d:51:a6:07:22:62:68:82:a3:ec:7a:a6:e6:5b:d9:5b (ECDSA)
|_  256 0e:fd:07:8b:3f:ff:b5:c0:d5:4d:f6:53:4a:d8:ec:87 (ED25519)

8000/tcp open  http    syn-ack ttl 62 Werkzeug httpd 2.0.3 (Python 3.8.10)
| http-methods: 
|_  Supported Methods: GET HEAD OPTIONS
|_http-title: Login
```

> 
