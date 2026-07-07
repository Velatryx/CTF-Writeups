Room Description: 

Hack into a Mr. Robot themed Windows machine. Use metasploit for initial access, utilise powershell for Windows privilege escalation enumeration and learn a new technique to get Administrator access. 

---

First, I ran a `rustscan`

```bash
rustscan -a 10.129.174.58 --range 1-65535 --ulimit=5000 -- -sV -O 
.----. .-. .-. .----..---.  .----. .---.   .--.  .-. .-.
| {}  }| { } |{ {__ {_   _}{ {__  /  ___} / {} \ |  `| |
| .-. \| {_} |.-._} } | |  .-._} }\     }/  /\  \| |\  |
`-' `-'`-----'`----'  `-'  `----'  `---' `-'  `-'`-' `-'
The Modern Day Port Scanner.
________________________________________
: http://discord.skerritt.blog         :
: https://github.com/RustScan/RustScan :
 --------------------------------------
To scan or not to scan? That is the question.

[~] The config file is expected to be at "/root/.rustscan.toml"
[~] Automatically increasing ulimit value to 5000.
Open 10.129.174.58:80
Open 10.129.174.58:139
Open 10.129.174.58:135
Open 10.129.174.58:445
Open 10.129.174.58:3389
Open 10.129.174.58:5985
Open 10.129.174.58:8080
Open 10.129.174.58:47001
Open 10.129.174.58:49156
Open 10.129.174.58:49152
Open 10.129.174.58:49154
Open 10.129.174.58:49153
Open 10.129.174.58:49155
Open 10.129.174.58:49197
Open 10.129.174.58:49196
[~] Starting Script(s)
[>] Running script "nmap -vvv -p {{port}} -{{ipversion}} {{ip}} -sV -O" on ip 10.129.174.58
Depending on the complexity of the script, results may take some time to appear.
[~] Starting Nmap 7.99 ( https://nmap.org ) at 2026-07-04 12:54 -0400
NSE: Loaded 48 scripts for scanning.
Initiating Ping Scan at 12:54
Scanning 10.129.174.58 [4 ports]
Completed Ping Scan at 12:54, 0.10s elapsed (1 total hosts)
Initiating Parallel DNS resolution of 1 host. at 12:54
Completed Parallel DNS resolution of 1 host. at 12:54, 4.50s elapsed
DNS resolution of 1 IPs took 4.50s. Mode: Async [#: 1, OK: 0, NX: 0, DR: 1, SF: 0, TR: 3, CN: 0]
Initiating SYN Stealth Scan at 12:54
Scanning 10.129.174.58 [15 ports]
Discovered open port 3389/tcp on 10.129.174.58
Discovered open port 49152/tcp on 10.129.174.58
Discovered open port 139/tcp on 10.129.174.58
Discovered open port 80/tcp on 10.129.174.58
Discovered open port 445/tcp on 10.129.174.58
Discovered open port 49197/tcp on 10.129.174.58
Discovered open port 135/tcp on 10.129.174.58
Discovered open port 8080/tcp on 10.129.174.58
Discovered open port 49153/tcp on 10.129.174.58
Discovered open port 5985/tcp on 10.129.174.58
Discovered open port 49156/tcp on 10.129.174.58
Discovered open port 49155/tcp on 10.129.174.58
Discovered open port 49196/tcp on 10.129.174.58
Discovered open port 47001/tcp on 10.129.174.58
Discovered open port 49154/tcp on 10.129.174.58
Completed SYN Stealth Scan at 12:54, 0.18s elapsed (15 total ports)
Initiating Service scan at 12:54
Scanning 15 services on 10.129.174.58
Service scan Timing: About 60.00% done; ETC: 12:56 (0:00:37 remaining)
Completed Service scan at 12:55, 60.18s elapsed (15 services on 1 host)
Initiating OS detection (try #1) against 10.129.174.58
NSE: Script scanning 10.129.174.58.
NSE: Starting runlevel 1 (of 2) scan.
Initiating NSE at 12:55
Completed NSE at 12:55, 0.61s elapsed
NSE: Starting runlevel 2 (of 2) scan.
Initiating NSE at 12:55
Completed NSE at 12:55, 0.37s elapsed
Nmap scan report for 10.129.174.58
Host is up, received timestamp-reply ttl 126 (0.083s latency).
Scanned at 2026-07-04 12:54:52 EDT for 62s

PORT      STATE SERVICE       REASON          VERSION
80/tcp    open  http          syn-ack ttl 126 Microsoft IIS httpd 8.5
135/tcp   open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
139/tcp   open  netbios-ssn   syn-ack ttl 126 Microsoft Windows netbios-ssn
445/tcp   open  microsoft-ds  syn-ack ttl 126 Microsoft Windows Server 2008 R2 - 2012 microsoft-ds
3389/tcp  open  ms-wbt-server syn-ack ttl 126 Microsoft Terminal Services
5985/tcp  open  http          syn-ack ttl 126 Microsoft HTTPAPI httpd 2.0 (SSDP/UPnP)
8080/tcp  open  http          syn-ack ttl 126 HttpFileServer httpd 2.3
47001/tcp open  http          syn-ack ttl 126 Microsoft HTTPAPI httpd 2.0 (SSDP/UPnP)
49152/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49153/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49154/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49155/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49156/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49196/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49197/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
Warning: OSScan results may be unreliable because we could not find at least 1 open and 1 closed port
```

--- 
Task1: Employee of the month:

![](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Easy/MountainSteel/images/Screenshot%20From%202026-07-04%2020-55-20.png)


