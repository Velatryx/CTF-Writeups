## Second - Tryhackme Writeup

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Second/Images/second.png)

Room Description: You Shall Fear The Second Order.

> Being second isn't such a bad thing, but not in this case.

---

## Objectives

1. What is the user flag?
2. What is the root flag?

---

## Enumeration & Recon

```shell
rustscan -a second.thm --ulimit 5000 -- -sCV -O
```

-- - Ports Discovered: `22` (ssh), `8000` (http)

