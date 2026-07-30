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


> Feroxbuster:

```
feroxbuster -u http://ledger.thm/ -w /usr/share/wordlists/seclists/Discovery/Web-Content/big.txt
                                                                                                                                                                   
 ___  ___  __   __     __      __         __   ___
|__  |__  |__) |__) | /  `    /  \ \_/ | |  \ |__
|    |___ |  \ |  \ | \__,    \__/ / \ | |__/ |___
by Ben "epi" Risher 🤓                 ver: 2.13.1
───────────────────────────┬──────────────────────
 🎯  Target Url            │ http://ledger.thm/
 🚩  In-Scope Url          │ ledger.thm
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
404      GET       29l       95w     1245c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
200      GET      334l     2089w   180418c http://ledger.thm/iisstart.png
200      GET       32l       55w      703c http://ledger.thm/
301      GET        2l       10w      155c http://ledger.thm/aspnet_client => http://ledger.thm/aspnet_client/
404      GET       42l      157w     1914c http://ledger.thm/aux
404      GET       42l      157w     1915c http://ledger.thm/com3
404      GET       42l      157w     1915c http://ledger.thm/com2
404      GET       42l      157w     1915c http://ledger.thm/com4
404      GET       42l      157w     1915c http://ledger.thm/com1
404      GET       42l      157w     1914c http://ledger.thm/con
404      GET       42l      157w     1928c http://ledger.thm/aspnet_client/aux
404      GET       42l      157w     1929c http://ledger.thm/aspnet_client/com1
404      GET       42l      157w     1929c http://ledger.thm/aspnet_client/com2
404      GET       42l      157w     1929c http://ledger.thm/aspnet_client/com4
404      GET       42l      157w     1929c http://ledger.thm/aspnet_client/com3
404      GET       42l      157w     1928c http://ledger.thm/aspnet_client/con
404      GET       42l      157w     1915c http://ledger.thm/lpt1
404      GET       42l      157w     1915c http://ledger.thm/lpt2
404      GET       42l      157w     1914c http://ledger.thm/nul
404      GET       42l      157w     1929c http://ledger.thm/aspnet_client/lpt1
404      GET       42l      157w     1929c http://ledger.thm/aspnet_client/lpt2
404      GET       42l      157w     1914c http://ledger.thm/prn
404      GET       42l      157w     1928c http://ledger.thm/aspnet_client/nul
404      GET       42l      157w     1928c http://ledger.thm/aspnet_client/prn
301      GET        2l       10w      166c http://ledger.thm/aspnet_client/system_web => http://ledger.thm/aspnet_client/system_web/
404      GET       42l      157w     1939c http://ledger.thm/aspnet_client/system_web/aux
404      GET       42l      157w     1940c http://ledger.thm/aspnet_client/system_web/com3
404      GET       42l      157w     1940c http://ledger.thm/aspnet_client/system_web/com4
404      GET       42l      157w     1940c http://ledger.thm/aspnet_client/system_web/com1
404      GET       42l      157w     1940c http://ledger.thm/aspnet_client/system_web/com2
404      GET       42l      157w     1939c http://ledger.thm/aspnet_client/system_web/con
404      GET       42l      157w     1940c http://ledger.thm/aspnet_client/system_web/lpt1
404      GET       42l      157w     1940c http://ledger.thm/aspnet_client/system_web/lpt2
404      GET       42l      157w     1939c http://ledger.thm/aspnet_client/system_web/nul
404      GET       42l      157w     1939c http://ledger.thm/aspnet_client/system_web/prn
[####################] - 80s    61455/61455   0s      found:34      errors:0      
[####################] - 38s    20482/20482   542/s   http://ledger.thm/ 
[####################] - 37s    20482/20482   547/s   http://ledger.thm/aspnet_client/ 
[####################] - 42s    20482/20482   490/s   http://ledger.thm/aspnet_client/system_web/   
```

> Feroxbuster /netasp_client/web_service/
> If the found directory gives 403 error, it exists. The found folder will tell us the exact version this service uses.


```
feroxbuster -u http://ledger.thm/aspnet_client/system_web/ -w iis.txt                                
                                                                                                                                                                   
 ___  ___  __   __     __      __         __   ___
|__  |__  |__) |__) | /  `    /  \ \_/ | |  \ |__
|    |___ |  \ |  \ | \__,    \__/ / \ | |__/ |___
by Ben "epi" Risher 🤓                 ver: 2.13.1
───────────────────────────┬──────────────────────
 🎯  Target Url            │ http://ledger.thm/aspnet_client/system_web
 🚩  In-Scope Url          │ ledger.thm
 🚀  Threads               │ 50
 📖  Wordlist              │ iis.txt
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
404      GET       29l       95w     1245c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
301      GET        2l       10w      166c http://ledger.thm/aspnet_client/system_web => http://ledger.thm/aspnet_client/system_web/
301      GET        2l       10w      176c http://ledger.thm/aspnet_client/system_web/4_0_30319 => http://ledger.thm/aspnet_client/system_web/4_0_30319/
[####################] - 2s        24/24      0s      found:2       errors:0      
[####################] - 1s         8/8       12/s    http://ledger.thm/aspnet_client/system_web/ 
[####################] - 0s         8/8       23/s    http://ledger.thm/aspnet_client/system_web/4_0_30319/    
```

> iis.txt

```text
1_0_3705
1_1_4322
2_0_50727
4_0_30319
4_5_22
crystalreportviewers13
crystalreportviewers14
```

## AI Answer:

```
It Narrows Down the .NET Framework VersionWhat it means: The application is running on .NET Framework 4.0, 4.5, 4.6, 4.7, or 4.8. All of these versions utilize the exact same CLR 4.0 core engine and share the v4.0.30319 folder naming convention.
It Highlights Exploitation and Vulnerability Testing VectorsBecause you know the app relies on the .NET 4.x ecosystem, you can prioritize specific penetration testing techniques:
Sub-Directory Fuzzing: The existence of this folder means you should immediately fuzz for standard sub-assets. A common example is testing for Crystal Reports integration by fuzzing /aspnet_client/system_web/4_0_30319/crystalreportviewers13/, which often leaks javascript files or application layouts.
Insecure Deserialization: This environment is susceptible to classic modern .NET deserialization flaws. 
```

---

## Information Gathering

> Domain Name (FQDN): `labyrinth.thm.local`
> IP Address: `10.130.181.194`
> Certificate Authority: `thm-LABYRINTH-CA`

## SMB Share enumeration

```shell
nxc smb ledger.thm -u 'guest' -p '' --shares
SMB         10.130.181.194  445    LABYRINTH        [*] Windows 10 / Server 2019 Build 17763 x64 (name:LABYRINTH) (domain:thm.local) (signing:True) (SMBv1:None) (Null Auth:True)                                                                                                                                                     
SMB         10.130.181.194  445    LABYRINTH        [+] thm.local\guest: 
SMB         10.130.181.194  445    LABYRINTH        [*] Enumerated shares
SMB         10.130.181.194  445    LABYRINTH        Share           Permissions     Remark
SMB         10.130.181.194  445    LABYRINTH        -----           -----------     ------
SMB         10.130.181.194  445    LABYRINTH        ADMIN$                          Remote Admin
SMB         10.130.181.194  445    LABYRINTH        C$                              Default share
SMB         10.130.181.194  445    LABYRINTH        IPC$            READ            Remote IPC
SMB         10.130.181.194  445    LABYRINTH        NETLOGON                        Logon server share 
SMB         10.130.181.194  445    LABYRINTH        SYSVOL                          Logon server share
```

> Looks like we can only read one share's contents as a guest - IPC$.

```shell
nxc smb ledger.thm -u 'guest' -p '' --share IPC$ --dir
SMB         10.130.181.194  445    LABYRINTH        [*] Windows 10 / Server 2019 Build 17763 x64 (name:LABYRINTH) (domain:thm.local) (signing:True) (SMBv1:None) (Null Auth:True)                                                                                                                                                     
SMB         10.130.181.194  445    LABYRINTH        [+] thm.local\guest: 
SMB         10.130.181.194  445    LABYRINTH        Perms    File Size      Date                          File Path                                    
SMB         10.130.181.194  445    LABYRINTH        -----    ---------      ----                          ---------                                    
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      InitShutdown                                 
SMB         10.130.181.194  445    LABYRINTH        fr--     5              Sun Dec 31 19:03:58 1600      lsass                                        
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      ntsvcs                                       
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      scerpc                                       
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      Winsock2\CatalogChangeListener-37c-0         
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      epmapper                                     
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      Winsock2\CatalogChangeListener-214-0         
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      LSM_API_service                              
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      eventlog                                     
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      Winsock2\CatalogChangeListener-404-0         
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      atsvc                                        
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      Winsock2\CatalogChangeListener-6c8-0         
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      TermSrv_API_service                          
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      Ctx_WinStation_API_service                   
SMB         10.130.181.194  445    LABYRINTH        fr--     4              Sun Dec 31 19:03:58 1600      wkssvc                                       
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      SessEnvPublicRpc                             
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      Winsock2\CatalogChangeListener-880-0         
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      Winsock2\CatalogChangeListener-280-0         
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      Winsock2\CatalogChangeListener-280-1         
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      RpcProxy\49670                               
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      47c63ef24d01e88e                             
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      RpcProxy\593                                 
SMB         10.130.181.194  445    LABYRINTH        fr--     4              Sun Dec 31 19:03:58 1600      srvsvc                                       
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      Winsock2\CatalogChangeListener-968-0         
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      spoolss                                      
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      Winsock2\CatalogChangeListener-810-0         
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      netdfs                                       
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      W32TIME_ALT                                  
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      Amazon\SSM\InstanceData\health               
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      Amazon\SSM\InstanceData\termination          
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      Winsock2\CatalogChangeListener-26c-0         
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      Winsock2\CatalogChangeListener-ce4-0         
SMB         10.130.181.194  445    LABYRINTH        fr--     3              Sun Dec 31 19:03:58 1600      cert                                         
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      Winsock2\CatalogChangeListener-878-0         
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      PIPE_EVENTROOT\CIMV2SCM EVENT PROVIDER       
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      Winsock2\CatalogChangeListener-d08-0         
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      iisipm52436baa-da1f-4de1-a25e-e9a8cd18914a   
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      iislogpipe239a6c86-b96e-4128-a83f-96bd8e97dc16
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      6vkFux0s7nG2b80pxbmSm4Yl1e5MPyj86fAzzW1q71khy7S7gUBoKLtlCcPWgg1w0DCu7Qb6B1euaEwuJ9W1XZrfi5k8xLUd5YqQ3C9iUTzpQ6pf9mhO2X                                                                                                      
SMB         10.130.181.194  445    LABYRINTH        fr--     1              Sun Dec 31 19:03:58 1600      CPFATP_1856_v4.0.30319
```


> User enumeration with NetExec:
> Looks like we got two users with leaked credentials in description section. Two users: `SUSANNA_MCKNIGHT` and `IVY_WILLIS` with the password of `CHANGEME2023!`.

```shell
nxc ldap ledger.thm -u '' -p '' --base-dn "DC=thm,DC=local" --users
        10.130.181.194  389    LABYRINTH        [*] Windows 10 / Server 2019 Build 17763 (name:LABYRINTH) (domain:thm.local) (signing:None) (channel binding:Never)                                                                                                                                                               
LDAP        10.130.181.194  389    LABYRINTH        [+] thm.local\: 
LDAP        10.130.181.194  389    LABYRINTH        [*] Enumerated 487 domain users: thm.local
LDAP        10.130.181.194  389    LABYRINTH        -Username-                    -Last PW Set-       -BadPW-  -Description-                                                                                                                                                                                                          
LDAP        10.130.181.194  389    LABYRINTH        Guest                         <never>             0        Tier 1 User                                                                                                                                                                                                            
LDAP        10.130.181.194  389    LABYRINTH        greg                          2023-05-15 10:49:03 0        Tier 1 User                                                                                                                                                                                                            
LDAP        10.130.181.194  389    LABYRINTH        SHANA_FITZGERALD              2023-05-30 05:45:58 0                                                                                                                                                                                                                               
LDAP        10.130.181.194  389    LABYRINTH        CAREY_FIELDS                  2023-05-30 05:45:58 0                                                                                                                                                                                                                               
LDAP        10.130.181.194  389    LABYRINTH        DWAYNE_NGUYEN                 2023-05-30 05:45:58 0        Tier 1 User                                                                                                                                                                                                            
LDAP        10.130.181.194  389    LABYRINTH        BRANDON_PITTMAN               2023-05-30 05:45:59 0        Tier 1 User                                                                                                                                                                                                            
LDAP        10.130.181.194  389    LABYRINTH        BRET_DONALDSON                2023-05-30 05:45:59 0                                                                                                                                                                                                                               
LDAP        10.130.181.194  389    LABYRINTH        VAUGHN_MARTIN                 2023-05-30 05:45:59 0        Tier 1 User
LDAP        10.130.181.194  389    LABYRINTH        NATALIE_BRADFORD              2023-05-30 05:46:49 0                                                                                                                                                                                                                               
LDAP        10.130.181.194  389    LABYRINTH        FRED_DOTSON                   2023-05-30 05:46:49 0        Tier 1 User                                                                                                                                                                                                            
LDAP        10.130.181.194  389    LABYRINTH        MORTON_BURNS                  2023-05-30 05:46:49 0                                                                                                                                                                                                                               
LDAP        10.130.181.194  389    LABYRINTH        IVY_WILLIS                    2023-05-30 08:30:55 0        Please change it: CHANGEME2023!                                                                                                                                                                                        
LDAP        10.130.181.194  389    LABYRINTH        SOFIA_PATTERSON               2023-05-30 05:46:49 0                                                                                                                                                                                                                               
LDAP        10.130.181.194  389    LABYRINTH        JANE_FOLEY                    2023-05-30 05:46:49 0                                                                                                                                                                                                                               
LDAP        10.130.181.194  389    LABYRINTH        PEARL_FULLER                  2023-05-30 05:46:49 0                                                                                                                                                                                                                               
LDAP        10.130.181.194  389    LABYRINTH        GUADALUPE_TURNER              2023-05-30 05:46:50 0        Tier 1 User                                                                                                                                                                                                            
LDAP        10.130.181.194  389    LABYRINTH        VIVIAN_HARPER                 2023-05-30 05:46:50 0                                                                                                                                                                                                                               
LDAP        10.130.181.194  389    LABYRINTH        VICENTE_BURT                  2023-05-30 05:46:50 0                                                                                                                                                                                                                               
LDAP        10.130.181.194  389    LABYRINTH        DIXIE_BERGER                  2023-05-30 05:46:50 0                                                                                                                                                                                                                               
LDAP        10.130.181.194  389    LABYRINTH        LIZ_WALTER                    2023-05-30 05:46:50 0        Tier 1 User                                                                                                                                                                                                            
LDAP        10.130.181.194  389    LABYRINTH        SUSANNA_MCKNIGHT              2023-07-05 11:11:32 0        Please change it: CHANGEME2023!                                                                                                                                                                                        
LDAP        10.130.181.194  389    LABYRINTH        LILY_LYONS                    2023-05-30 05:46:50 0        Tier 1 User                                                                                                                                                                                                            
LDAP        10.130.181.194  389    LABYRINTH        WALDO_BOYER                   2023-05-30 05:46:51 0                                                                                                                                                                                                                                 
```

> Enumeration with low-privileged users:

```shell
┌──(root㉿kali)-[~]
└─# nxc smb ledger.thm -u 'IVY_WILLIS' -p 'CHANGEME2023!'  --shares
SMB         10.130.181.194  445    LABYRINTH        [*] Windows 10 / Server 2019 Build 17763 x64 (name:LABYRINTH) (domain:thm.local) (signing:True) (SMBv1:None) (Null Auth:True)                                                                                                                                                     
SMB         10.130.181.194  445    LABYRINTH        [+] thm.local\IVY_WILLIS:CHANGEME2023! 
SMB         10.130.181.194  445    LABYRINTH        [*] Enumerated shares
SMB         10.130.181.194  445    LABYRINTH        Share           Permissions     Remark
SMB         10.130.181.194  445    LABYRINTH        -----           -----------     ------
SMB         10.130.181.194  445    LABYRINTH        ADMIN$                          Remote Admin
SMB         10.130.181.194  445    LABYRINTH        C$                              Default share
SMB         10.130.181.194  445    LABYRINTH        IPC$            READ            Remote IPC
SMB         10.130.181.194  445    LABYRINTH        NETLOGON        READ            Logon server share 
SMB         10.130.181.194  445    LABYRINTH        SYSVOL          READ            Logon server share 
                                                                                                                                                                   
┌──(root㉿kali)-[~]
└─# nxc smb ledger.thm -u 'SUSANNA_MCKNIGHT' -p 'CHANGEME2023!'  --shares
SMB         10.130.181.194  445    LABYRINTH        [*] Windows 10 / Server 2019 Build 17763 x64 (name:LABYRINTH) (domain:thm.local) (signing:True) (SMBv1:None) (Null Auth:True)                                                                                                                                                     
SMB         10.130.181.194  445    LABYRINTH        [+] thm.local\SUSANNA_MCKNIGHT:CHANGEME2023! 
SMB         10.130.181.194  445    LABYRINTH        [*] Enumerated shares
SMB         10.130.181.194  445    LABYRINTH        Share           Permissions     Remark
SMB         10.130.181.194  445    LABYRINTH        -----           -----------     ------
SMB         10.130.181.194  445    LABYRINTH        ADMIN$                          Remote Admin
SMB         10.130.181.194  445    LABYRINTH        C$                              Default share
SMB         10.130.181.194  445    LABYRINTH        IPC$            READ            Remote IPC
SMB         10.130.181.194  445    LABYRINTH        NETLOGON        READ            Logon server share 
SMB         10.130.181.194  445    LABYRINTH        SYSVOL          READ            Logon server share 
```

> Looks like we have read permission over SYSVOL share, which is a huge security risk. Let's read its contents. Unfortunately, I could not find something VERY useful for pwnage.

> RDP Port is open, let's test:

```shell
┌──(root㉿kali)-[~/thm.local]
└─# nxc rdp ledger.thm -u 'SUSANNA_MCKNIGHT' -p 'CHANGEME2023!' -x 'whoami'
[!] Executing remote command via RDP will disconnect the Windows session (not log off) if the targeted user is connected via RDP, do you want to continue ? [Y/n] y
RDP         10.130.181.194  3389   LABYRINTH        [*] Windows 10 or Windows Server 2016 Build 17763 (name:LABYRINTH) (domain:thm.local) (nla:True)
RDP         10.130.181.194  3389   LABYRINTH        [+] thm.local\SUSANNA_MCKNIGHT:CHANGEME2023! (Pwn3d!)
RDP         10.130.181.194  3389   LABYRINTH        [+] Executing command: whoami with delay 5 seconds
RDP         10.130.181.194  3389   LABYRINTH        [+] Waiting for clipboard to be ready...
RDP         10.130.181.194  3389   LABYRINTH        thm\susanna_mcknight
```

> Okay, looks like we can actually use this.

> RDP: First flag.

```shell
xfreerdp /v:ledger.thm /u:SUSANNA_MCKNIGHT /p:'CHANGEME2023!' /dynamic-resolution
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Ledger/Images/Screenshot%20From%202026-07-30%2022-47-30.png)

---

## Privilege Escalation

> With a hint, I found out that we should be looking for a certificate misconfiguration. I am not an Active Directory nerd, so I asked AI for help, and explanation for this privilege escalation vector.

```shell
certipy-ad find -u SUSANNA_MCKNIGHT -p 'CHANGEME2023!' -dc-ip 10.130.181.194 -target thm.local -vulnerable -enabled
Certipy v5.0.4 - by Oliver Lyak (ly4k)

[*] Finding certificate templates
[*] Found 37 certificate templates
[*] Finding certificate authorities
[*] Found 1 certificate authority
[*] Found 14 enabled certificate templates
[*] Finding issuance policies
[*] Found 21 issuance policies
[*] Found 0 OIDs linked to templates
[*] Retrieving CA configuration for 'thm-LABYRINTH-CA' via RRP
[!] Failed to connect to remote registry. Service should be starting now. Trying again...
[*] Successfully retrieved CA configuration for 'thm-LABYRINTH-CA'
[*] Checking web enrollment for CA 'thm-LABYRINTH-CA' @ 'labyrinth.thm.local'
[*] Saving text output to '20260730145459_Certipy.txt'
[*] Wrote text output to '20260730145459_Certipy.txt'
[*] Saving JSON output to '20260730145459_Certipy.json'
[*] Wrote JSON output to '20260730145459_Certipy.json'
```

> Simple Explanation; From what I understand, AD CS (Active Directory Certificate Services) is like the Passport Office, where users can login to Active Directory by showing Digital Certificates instead of entering password every single time. Since we can request a certificate in behalf of someone else (Like a more privileged user: Administrator) as a low-privileged user, this can be turned into a privilege escalation vector.

> AI Explanation:

Breaking down the command in plain English:

    -u SUSANNA_MCKNIGHT -p 'CHANGEME2023!': You provided normal, low-level user credentials to log in.

    find: You asked Certipy to scan the domain’s Certificate Authority (the passport office) and list all the rules it uses.

    -enabled: "Only show me rules (templates) that are currently active."

    -vulnerable: "Filter out the safe stuff—only highlight configurations that have known security flaws."
    
Why Did We Do It?

In Windows networks, administrators create Certificate Templates (think of them like pre-printed application forms for different types of badges—e.g., "Web Server Badge", "Standard User Badge", "VPN Access Badge").

Because AD CS is complex, system administrators frequently make mistakes when setting up these application forms. For example, they might accidentally leave a box checked that says:

    "Allow the applicant to type whatever name they want on the badge, and issue it without approval."

If Certipy finds a vulnerable template (such as a flaw known as ESC1):

    Ask for an ID Badge: As a normal user (SUSANNA_MCKNIGHT), we fill out the vulnerable application form.

    Impersonate the Boss: Because of the misconfiguration, we write Administrator in the "Name on Badge" field.

    Get Approved: The Certificate Authority signs it and hands us a valid digital certificate that says we are the Domain Administrator.

    Log In as Administrator: We present this certificate to the domain to authenticate. The network trusts the certificate completely, handing us full Domain Admin (Root) control over every computer in the domain.

Certipy created a text file (20260730145459_Certipy.txt) containing the scan results. The next step is to open that file to see which specific template is vulnerable so we can request our administrator certificate.
