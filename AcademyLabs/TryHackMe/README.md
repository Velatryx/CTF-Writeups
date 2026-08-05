# TryHackMe Writeups

Personal writeups from TryHackMe's AcademyLabs. Each one documents my methodology, thought process, and takeaways - not just the commands/tools.

---

## Labs

### 🟢 Easy

| Lab | Key Techniques |
|-----|---------------|
| [Fools Mate](Easy/Fools%20Mate/README.md) | Client-side bypass, Burp Suite |
| [MountainSteel](Easy/MountainSteel/README.md) | Metasploit, Unquoted service path, Windows privesc |
| [Cheese CTF](Easy/CheeseCTF/README.md) | Time-based blind SQLi, LFI, PHP filter chain RCE, SUID abuse |

### 🟡 Medium

| Lab | Key Techniques |
|-----|---------------|
| [battery](Medium/battery/README.md) | Null byte injection, XXE, PHP filter exfiltration, sudo abuse |
| [Fools Mate, Revenge](Medium/Fools%20Mate%2C%20Revenge/README.md) | Prototype pollution, `__proto__` filter bypass |
| [Hammer](Medium/Hammer/README.md) | OTP rate-limit bypass (custom script), JWT `kid` header manipulation |
| [Sequence](Medium/Sequence/README.md) | Stored XSS, SSRF, file upload RCE, Docker escape via docker.sock |
| [Voyage](Medium/Voyage/README.md) | CVE-2023-23752, Pickle Deserialization RCE, CAP_SYS_MODULE Docker Escape, Kernel Module Loading |

### 🔴 Hard

| Lab | Key Techniques |
|-----|---------------|
| [IronHold](Hard/IronHold/README.md) | Source review, Spring Actuator leak, SQLi, Mass assignment, Java deserialization RCE |
| [Ledger](Hard/Ledger/README.md) | LDAP null auth, AD CS ESC1, Certipy, Pass-the-Ticket |
| [Plant Photographer](Hard/Plant%20Photographer/README.md) | SSRF, LFI chaining, Werkzeug debug PIN calculation |
| [Second](Hard/Second/README.md) | Second-order SQLi, Second-order SSTI (WAF bypass), /etc/hosts phishing |
| [TryHack3M: Burg3r Bytes](Hard/TryHack3M%3A%20Burg3r%20Bytes/README.md) | Race condition, SSTI, TFTP abuse, Docker escape |
| [DX2: Hell's Kitchen](Hard/DX2:%20Hell's%20Kitchen/README.md) | SQL Injection, Command Injection, Sudo Misconfiguration |

### 🔥 Insane

| Lab | Key Techniques |
|-----|---------------|
| [You're in a Cave](Insane/You're%20in%20a%20Cave/README.md) | XXE, Java deserialization, GPG, Symlink attack, Docker cgroup escape |

---

## Skills Covered

`SQLi` `LFI` `XXE` `SSRF` `XSS` `SSTI` `Prototype Pollution` `JWT Forgery`  
`Java Deserialization` `AD CS (ESC1)` `Race Conditions` `PHP Filter Chains`  
`Docker Escape` `Windows PrivEsc` `Custom Python Scripting` `JS Source Recon` `Manual UNION SQLi (SQLite)` `WebSocket Command Injection` `Steganography` `NFS no_root_squash PrivEsc`

---

> Writeups are added as I work through rooms. More on my [TryHackMe profile](https://tryhackme.com/p/hoaX).
