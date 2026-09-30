## Anonymous Playground — TryHackMe Writeup

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Anonymous%20Playground/Images/anonymous.png)

**Room Description**: Want to become part of Anonymous? They have a challenge for you. Can you get the flags and become an operative?

**Room Link**: [Anonymous Playground](https://tryhackme.com/room/anonymousplayground)

> So, you've decided to sign up with Anonymous?  Well, it won't be that easy.  They've constructed a vulnerable CTF machine for
you to hack your way into and prove you have what it takes to become a member of Anonymous.  Can you do it?  Do you have
what it takes?



---

#### Objectives: 
  - User 1 Flag
  - User 2 Flag
  - User 3 Flag

---

> Add the target to hosts:

```zsh
sudo echo -e '10.128.186.164 anon.thm' | sudo tee -a /etc/hosts
```

---

## Enum & Recon

