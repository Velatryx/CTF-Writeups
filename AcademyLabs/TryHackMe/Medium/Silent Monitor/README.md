## Silent Monitor — TryHackMe Writeup

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Silent%20Monitor/Images/silent.png)

**Room Description**: Enumerate a running internal service, exploit a vulnerable web application, pivot through the system, and crack your way to root.

**Room Link**: [Silent Monitor](https://tryhackme.com/room/silent-monitor)

> **Green Lights, Dark Corners**

> CorpNet's internal network operations centre has been running quietly for years. Monitoring hosts, logging events, and keeping the infrastructure alive. Or so it seems. A tip from a disgruntled contractor suggests that someone on the NOC team has been cutting corners, leaving doors open, and hiding things in places no one thinks to look.
The portal is up. The services show green. The audit log looks clean.
But clean logs can be written by anyone.
Your job is to get in, move through the system, and find out what is really running behind the secret dashboard.


---

### Objectives
 
 - What is the content of user.txt?
 - What is the content of root.txt?

---

> Add the target to hosts

```zsh
sudo echo -e '10.129.154.142 silent.thm' | sudo tee -a /etc/hosts
```
---

## Enum & Recon

```zsh
nmap silent.thm --min-rate=1000 -p-
Starting Nmap 7.99 ( https://nmap.org ) at 2026-09-20 12:18 -0400
Nmap scan report for silent.thm (10.129.154.142)
Host is up (0.077s latency).
Not shown: 65533 closed tcp ports (reset)
PORT     STATE SERVICE
22/tcp   open  ssh
5050/tcp open  mmcc
```

From the feroxbuster, I got no output, and I still do not know why. I discovered from another place that `/internal` endpoint was actually open, though feroxbuster gave no output of it. Anyway, I discovered a login portal in this endpoint, and there was no other clue, so it had to be a SQL injection. I tried some manual payloads, and got a hit with 

```SQL
admin' OR '1'='1' -- -
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Silent%20Monitor/Images/Screenshot%20From%202026-09-20%2020-38-53.png)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Silent%20Monitor/Images/Screenshot%20From%202026-09-20%2020-39-46.png)


> While navigating through the tabs, I instantly noticed the ping function, command injection here XD.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Silent%20Monitor/Images/Screenshot%20From%202026-09-20%2020-41-20.png)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Silent%20Monitor/Images/neuron.jpg)


> Well I tried some injection attempts, like `|`, `&`, and `;`, even tried some blind injection but `$` also caused errors. Looks like there was not any command injection.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Silent%20Monitor/Images/Screenshot%20From%202026-09-20%2021-16-01.png)

> .. Or was there?

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Silent%20Monitor/Images/vsauce.jpg)

> See, I noticed a command injection attempt in the logs of the dashboard: `127.0.0.1%0awhoami` a bypass technique where you inject a null byte, and pass a second command. But when I tried using it, it did not work. So I opened the burpsuite, and noticed we can actually pass the second command using a new line, which you can't do inside a web-browser. It is called a **CRLF Injection**, where you pass `\r\n`, \r representing the end of the line, and \n being beginning of a new line.


![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Silent%20Monitor/Images/Screenshot%20From%202026-09-20%2021-16-31.png)
