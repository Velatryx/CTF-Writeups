## IronHold 


Room Description: The source leaked. Read it like an attacker, chain the flaws, and shell the door-control server.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/IronHold/Images/Ironhold.png)

> IronHold is retiring its inmate-management platform. Somewhere in the handover, a developer pushed the complete repository to a public mirror and then left the company. Facility security wants a straight answer before the system goes dark for good: if that repository is out there, how far could someone actually get?
We start with nothing but what leaked: the full, unredacted source, and a live copy of the application still running on the network. No credentials, no map, no walkthrough. The code tells us what the developers got wrong; the running instance tells us if we're right.
Get all four and Ironhold's last system goes down the same way it went up: on its own mistakes.
Download the source archive attached to this task and start reading. The lab machine is reachable at http://MACHINE_IP:8080.

---

## Objectives

1. What is the flag on the officer dashboard once you're inside the system?
2. What is the flag in the staff record that no page on the site will show you?
3. What is the flag on the warden's door-control panel?
4. What is the flag waiting on the facility server once you're through the gate?

---

## Adding the target to hosts

```shell
sudo echo -e '10.130.134.243 iron.thm' | sudo tee -a /etc/hosts
```

---

## Enumeration & Recon

> Rustscan results:

```
rustscan -a iron.thm --ulimit 5000 -- -sCV -O                              
.----. .-. .-. .----..---.  .----. .---.   .--.  .-. .-.
| {}  }| { } |{ {__ {_   _}{ {__  /  ___} / {} \ |  `| |
| .-. \| {_} |.-._} } | |  .-._} }\     }/  /\  \| |\  |
`-' `-'`-----'`----'  `-'  `----'  `---' `-'  `-'`-' `-'
The Modern Day Port Scanner.
________________________________________
: http://discord.skerritt.blog         :
: https://github.com/RustScan/RustScan :
 --------------------------------------
Open ports, closed hearts.

[~] The config file is expected to be at "/root/.rustscan.toml"
[~] Automatically increasing ulimit value to 5000.
Open 10.130.134.243:22
Open 10.130.134.243:8080
[~] Starting Script(s)
[>] Running script "nmap -vvv -p {{port}} -{{ipversion}} {{ip}} -sCV -O" on ip 10.130.134.243

PORT     STATE SERVICE REASON         VERSION
22/tcp   open  ssh     syn-ack ttl 62 OpenSSH 9.6p1 Ubuntu 3ubuntu13.5 (Ubuntu Linux; protocol 2.0)
| ssh-hostkey: 
|   256 45:10:c1:59:78:66:21:6f:36:24:57:90:b9:e7:01:cc (ECDSA)
| ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBCn5H47SNdmyPDCwOEM5coxwTDyfqFlh8xzbCKCwwb6hJJoCdYAnFLY/7nBJ3Gcyfqoljylq32ZVkulOcAagrvU=
|   256 b1:18:85:c9:79:a2:e3:c3:89:e2:7b:f8:a2:66:ee:9a (ED25519)
|_ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAICurGxxSZvTHMlG2rbJZVZ0Bm8xC8JSLynaztRn4o+Vr
8080/tcp open  http    syn-ack ttl 61 Apache Tomcat (language: en)
| http-methods: 
|_  Supported Methods: GET HEAD OPTIONS
|_http-title: Ironhold Correctional | Staff Login

```

> Feroxbuster results

```
feroxbuster -u http://iron.thm:8080/ -w /usr/share/wordlists/dirbuster/directory-list-lowercase-2.3-medium.txt
                                                                                                                                                                   
 ___  ___  __   __     __      __         __   ___
|__  |__  |__) |__) | /  `    /  \ \_/ | |  \ |__
|    |___ |  \ |  \ | \__,    \__/ / \ | |__/ |___
by Ben "epi" Risher 🤓                 ver: 2.13.1
───────────────────────────┬──────────────────────
 🎯  Target Url            │ http://iron.thm:8080/
 🚩  In-Scope Url          │ iron.thm
 🚀  Threads               │ 50
 📖  Wordlist              │ /usr/share/wordlists/dirbuster/directory-list-lowercase-2.3-medium.txt
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
302      GET        0l        0w        0c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
405      GET        1l        3w      103c http://iron.thm:8080/login
200      GET       35l       77w     1193c http://iron.thm:8080/status;jsessionid=4927F475950030CA4B65284C6E1DF038
200      GET      226l      405w     3747c http://iron.thm:8080/css/style.css;jsessionid=4927F475950030CA4B65284C6E1DF038
404      GET        1l        2w       93c http://iron.thm:8080/css/
200      GET       38l      132w     1397c http://iron.thm:8080/about;jsessionid=4927F475950030CA4B65284C6E1DF038
200      GET       32l       78w     1201c http://iron.thm:8080/
200      GET      226l      405w     3747c http://iron.thm:8080/css/style.css;jsessionid=09FC773600122C0763EA4A1CD07EF990
200      GET       38l      132w     1397c http://iron.thm:8080/about
404      GET        1l        2w       92c http://iron.thm:8080/css
200      GET      226l      405w     3747c http://iron.thm:8080/css/style.css;jsessionid=713919F811A29A177F4B9B91E4D0F1DE
200      GET       35l       77w     1193c http://iron.thm:8080/status
405      GET        1l        3w      104c http://iron.thm:8080/logout
500      GET        1l        1w       73c http://iron.thm:8080/error
400      GET        1l       32w      435c http://iron.thm:8080/http%3a%2f%2fwww
400      GET        1l       32w      435c http://iron.thm:8080/http%3a%2f%2fyoutube
400      GET        1l       32w      435c http://iron.thm:8080/http%3a%2f%2fblogs
400      GET        1l       32w      435c http://iron.thm:8080/http%3a%2f%2fblog
400      GET        1l       32w      435c http://iron.thm:8080/**http%3a%2f%2fwww
400      GET        1l       32w      435c http://iron.thm:8080/external%5cx-news
404      GET        1l        2w       96c http://iron.thm:8080/web-inf
400      GET        1l       32w      435c http://iron.thm:8080/http%3a%2f%2fcommunity
400      GET        1l       32w      435c http://iron.thm:8080/http%3a%2f%2fradar
400      GET        1l       32w      435c http://iron.thm:8080/http%3a%2f%2fjeremiahgrossman
400      GET        1l       32w      435c http://iron.thm:8080/http%3a%2f%2fweblog
400      GET        1l       32w      435c http://iron.thm:8080/http%3a%2f%2fswik
[####################] - 6m    207641/207641  0s      found:25      errors:0      
[####################] - 6m    207629/207629  587/s   http://iron.thm:8080/     
```

---

## Exposed Endpoints in the source code

```
/
/login
/logout
/about
/status
/css/**
/error
/actuator/**
/admin
/admin/**
/admin/export
/admin/staff
/ironhold-internal:8080/admin/import
/staff
/staff/{id}
/control
/roster
/settings
/settings/diagnostics
/profile
/profile/update
/incidents
/commissary
/comissary/orders
/visitation
/visitation/new
/inmates
/inmates/search
/inmates/{inmate_id}/movements        # Exposed in inmate-detail.html
/notices
```

---

## More Recon and Information Gathering

> During the assessment of the vulnerable website, I came accross this /actuator endpoint, where the directory contents were fully exposed. I found /actuator/env, and started reading the contents. Then I found another exposed credential `kiosk:Sh1ftK10sk#2091` the username and password fully in plaintext. Let's note that.
