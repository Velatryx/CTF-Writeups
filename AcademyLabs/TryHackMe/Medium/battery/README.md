## Battery

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

