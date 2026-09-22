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

