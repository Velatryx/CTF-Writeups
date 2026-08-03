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
| **`80/tcp`** | `OPEN` | **HTTP** | Apache httpd 2.4.58 ((Ubuntu)) - Joomla 4.2.7 |
| **`2222/tcp`** | `OPEN` | **SSH** | OpenSSH 8.2p1 Ubuntu 4ubuntu0.13 |

---

## Adding target to hosts

```shell
echo -e '10.128.162.162 voyage.thm' | sudo tee -a /etc/hosts
```

---

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

### Web Technology Enumeration

> Whatweb

```shell
 ~# whatweb voyage.thm:80

http://voyage.thm:80 [200 OK] Apache[2.4.58], Cookies[310c29008fc04f792e0bccb4682e5b78], Country[RESERVED][ZZ], HTML5, HTTPServer[Ubuntu Linux][Apache/2.4.58 (Ubuntu)], HttpOnly[310c29008fc04f792e0bccb4682e5b78], IP[10.128.162.162], MetaGenerator[Joomla! - Open Source Content Management], PasswordField[password], Script[application/json,application/ld+json,module], Title[Home], UncommonHeaders[referrer-policy,cross-origin-opener-policy], X-Frame-Options[SAMEORIGIN]
```

> cmseek

```shell
[✔] Target: http://voyage.thm
[✔] Detected CMS: Joomla
[✔] CMS URL: https://joomla.org
[✔] Joomla Version: 4.2.7
[✔] Readme file: http://voyage.thm/README.txt
[✔] Admin URL: http://voyage.thm/administrator


[✔] Open directories: 4
[*] Open directory url: 
   [>] http://voyage.thmadministrator/templates
   [>] http://voyage.thmadministrator/modules
   [>] http://voyage.thmadministrator/components
   [>] http://voyage.thmimages/banners
```

### Directory Enumeration

```shell
~# feroxbuster -u http://voyage.thm -w /usr/share/wordlists/dirbuster/directory-list-2.3-medium.txt

http://voyage.thm/
/robots.txt
/images/
/component/
/templates/
/media/
/index.php
/index.php/component/users/
/index.php/component/users/remind
/index.php/component/users/reset
/plugins/
/includes/
/components/
/api/
/cache/
/tmp/
/layouts/
/administrator/
/cli/
```

### Joomscan - joomla scanner

> So let's scan the target using joomscan, and look if anything is outdated, or we missed anything

```shell
~# joomscan --url http://voyage.thm/ --enumerate-components

[+] FireWall Detector
[++] Firewall not detected

[+] Detecting Joomla Version
[++] Joomla 4.2.7

[+] Core Joomla Vulnerability
[++] Target Joomla core is not vulnerable

[++] robots.txt is found
path : http://voyage.thm/robots.txt 

Interesting path found from robots.txt
http://voyage.thm/joomla/administrator/
http://voyage.thm/administrator/
http://voyage.thm/api/
http://voyage.thm/bin/
http://voyage.thm/cache/
http://voyage.thm/cli/                       
http://voyage.thm/components/                         
http://voyage.thm/includes/                     
http://voyage.thm/installation/                         
http://voyage.thm/language/                          
http://voyage.thm/layouts/                        
http://voyage.thm/libraries/                             
http://voyage.thm/logs/                          
http://voyage.thm/modules/                           
http://voyage.thm/plugins/         
http://voyage.thm/tmp/ 
```

> I searched for a vulnerable component, but the endpoint did not exist or I did not have access to it.

```shell
└─# searchsploit joomla com_fields 
----------------------------------------------------------------------------------------------------------------------------------------------------------------
 Exploit Title                                                                                                                                                                    |  Path
---------------------------------------------------------------------------------------------------------------------------------------------------------------- 
Joomla! 3.7.0 - 'com_fields' SQL Injection                                                                                                                                        | php/webapps/42033.txt
----------------------------------------------------------------------------------------------------------------------------------------------------------------
```
