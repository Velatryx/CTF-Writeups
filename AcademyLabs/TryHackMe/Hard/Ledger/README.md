## Ledger

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Ledger/Images/ledger.png)

Room Description: This challenge simulates a real cyber-attack scenario where you must exploit an Active Directory.

> Can you find all the flags?

---

## Objectives:

1. What is the user flag?
2. What is the root flag?

---

## Adding target to hosts

```bash
echo -e '10.130.181.194 ledger.thm' | sudo tee -a /etc/hosts
```

---

## Enumeration & Recon

> Rustscan results:

```shell
rustscan -a ledger.thm --ulimit 5000 -- -sCV -O  
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
Open 10.130.181.194:53
Open 10.130.181.194:80
Open 10.130.181.194:88
Open 10.130.181.194:135
Open 10.130.181.194:139
Open 10.130.181.194:389
Open 10.130.181.194:445
Open 10.130.181.194:443
Open 10.130.181.194:464
Open 10.130.181.194:593
Open 10.130.181.194:636
Open 10.130.181.194:3269
Open 10.130.181.194:3268
Open 10.130.181.194:3389
Open 10.130.181.194:9389
Open 10.130.181.194:47001
Open 10.130.181.194:49666
Open 10.130.181.194:49665
Open 10.130.181.194:49667
Open 10.130.181.194:49670
Open 10.130.181.194:49669
Open 10.130.181.194:49676
Open 10.130.181.194:49671
Open 10.130.181.194:49675
Open 10.130.181.194:49664
Open 10.130.181.194:49681
Open 10.130.181.194:49712
Open 10.130.181.194:49722
Open 10.130.181.194:49727
Open 10.130.181.194:49798
[~] Starting Script(s)

PORT      STATE SERVICE       REASON          VERSION
53/tcp    open  domain        syn-ack ttl 126 Simple DNS Plus
80/tcp    open  http          syn-ack ttl 126 Microsoft IIS httpd 10.0
| http-methods: 
|   Supported Methods: OPTIONS TRACE GET HEAD POST
|_  Potentially risky methods: TRACE
|_http-title: IIS Windows Server
|_http-server-header: Microsoft-IIS/10.0
88/tcp    open  kerberos-sec  syn-ack ttl 126 Microsoft Windows Kerberos (server time: 2026-07-30 15:17:20Z)
135/tcp   open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
139/tcp   open  netbios-ssn   syn-ack ttl 126 Microsoft Windows netbios-ssn
389/tcp   open  ldap          syn-ack ttl 126 Microsoft Windows Active Directory LDAP (Domain: thm.local, Site: Default-First-Site-Name)
| ssl-cert: Subject: commonName=labyrinth.thm.local
| Subject Alternative Name: othername: 1.3.6.1.4.1.311.25.1:<unsupported>, DNS:labyrinth.thm.local
| Issuer: commonName=thm-LABYRINTH-CA/domainComponent=thm
| Public Key type: rsa
| Public Key bits: 2048
| Signature Algorithm: sha256WithRSAEncryption
| Not valid before: 2026-07-30T14:51:11

443/tcp   open  ssl/https?    syn-ack ttl 126
| tls-alpn: 
|   h2
|_  http/1.1
|_ssl-date: 2026-07-30T15:19:26+00:00; -3s from scanner time.
| ssl-cert: Subject: commonName=thm-LABYRINTH-CA/domainComponent=thm
| Issuer: commonName=thm-LABYRINTH-CA/domainComponent=thm
| Public Key type: rsa
| Public Key bits: 2048
| Signature Algorithm: sha256WithRSAEncryption
| Not valid before: 2023-05-12T07:26:00
| Not valid after:  2028-05-12T07:35:59

445/tcp   open  microsoft-ds? syn-ack ttl 126
464/tcp   open  kpasswd5?     syn-ack ttl 126
593/tcp   open  ncacn_http    syn-ack ttl 126 Microsoft Windows RPC over HTTP 1.0
636/tcp   open  ssl/ldap      syn-ack ttl 126 Microsoft Windows Active Directory LDAP (Domain: thm.local, Site: Default-First-Site-Name)
| ssl-cert: Subject: commonName=labyrinth.thm.local
| Subject Alternative Name: othername: 1.3.6.1.4.1.311.25.1:<unsupported>, DNS:labyrinth.thm.local
| Issuer: commonName=thm-LABYRINTH-CA/domainComponent=thm
| Public Key type: rsa
| Public Key bits: 2048
| Signature Algorithm: sha256WithRSAEncryption
| Not valid before: 2026-07-30T14:51:11
| Not valid after:  2027-07-30T14:51:11

3268/tcp  open  ldap          syn-ack ttl 126 Microsoft Windows Active Directory LDAP (Domain: thm.local, Site: Default-First-Site-Name)
| ssl-cert: Subject: commonName=labyrinth.thm.local
| Subject Alternative Name: othername: 1.3.6.1.4.1.311.25.1:<unsupported>, DNS:labyrinth.thm.local
| Issuer: commonName=thm-LABYRINTH-CA/domainComponent=thm
| Public Key type: rsa
| Public Key bits: 2048
| Signature Algorithm: sha256WithRSAEncryption

3269/tcp  open  ssl/ldap      syn-ack ttl 126 Microsoft Windows Active Directory LDAP (Domain: thm.local, Site: Default-First-Site-Name)
|_ssl-date: 2026-07-30T15:19:26+00:00; -3s from scanner time.
| ssl-cert: Subject: commonName=labyrinth.thm.local
| Subject Alternative Name: othername: 1.3.6.1.4.1.311.25.1:<unsupported>, DNS:labyrinth.thm.local
| Issuer: commonName=thm-LABYRINTH-CA/domainComponent=thm
| Public Key type: rsa
| Public Key bits: 2048

3389/tcp  open  ms-wbt-server syn-ack ttl 126 Microsoft Terminal Services
|_ssl-date: 2026-07-30T15:19:26+00:00; -3s from scanner time.
| rdp-ntlm-info: 
|   Target_Name: THM
|   NetBIOS_Domain_Name: THM
|   NetBIOS_Computer_Name: LABYRINTH
|   DNS_Domain_Name: thm.local
|   DNS_Computer_Name: labyrinth.thm.local
|   Product_Version: 10.0.17763
|_  System_Time: 2026-07-30T15:18:18+00:00
| ssl-cert: Subject: commonName=labyrinth.thm.local
| Issuer: commonName=labyrinth.thm.local
| Public Key type: rsa
| Public Key bits: 2048
| Signature Algorithm: sha256WithRSAEncryption
| Not valid before: 2026-07-29T15:00:08
| Not valid after:  2027-01-28T15:00:08
9389/tcp  open  mc-nmf        syn-ack ttl 126 .NET Message Framing
47001/tcp open  http          syn-ack ttl 126 Microsoft HTTPAPI httpd 2.0 (SSDP/UPnP)
|_http-server-header: Microsoft-HTTPAPI/2.0
|_http-title: Not Found
49664/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49665/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49666/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49667/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49669/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49670/tcp open  ncacn_http    syn-ack ttl 126 Microsoft Windows RPC over HTTP 1.0
49671/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49675/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49676/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49681/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49712/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49722/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49727/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
49798/tcp open  msrpc         syn-ack ttl 126 Microsoft Windows RPC
Warning: OSScan results may be unreliable because we could not find at least 1 open and 1 closed port
Host script results:
|_clock-skew: mean: -2s, deviation: 0s, median: -3s
| smb2-security-mode: 
|   3.1.1: 
|_    Message signing enabled and required
| p2p-conficker: 
|   Checking for Conficker.C or higher...
|   Check 1 (port 6289/tcp): CLEAN (Couldn't connect)
|   Check 2 (port 13641/tcp): CLEAN (Couldn't connect)
|   Check 3 (port 54512/udp): CLEAN (Failed to receive data)
|   Check 4 (port 23583/udp): CLEAN (Timeout)
|_  0/4 checks are positive: Host is CLEAN or ports are blocked
| smb2-time: 
|   date: 2026-07-30T15:18:20
|_  start_date: N/A
```

---

## Information Gathering

> Domain Name (FQDN): `labyrinth.thm.local`
> IP Address: `10.130.181.194`
> Certificate Authority: `thm-LABYRINTH-CA`
