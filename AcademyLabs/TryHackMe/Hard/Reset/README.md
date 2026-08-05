
## Reset — TryHackMe Writeup

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/HellsKitchen.png)

**Room Description:** Can you help compromise a civilian machine that we believe is connected to the NSF?

**Room Link:** [Hell's Kitchen](https://tryhackme.com/room/dx2hellskitchen)

> *We need to recover the lost Ambrosia shipment from the NSF (National Secessionist Forces), the only treatment for the plague known as the Grey Death. However, we haven't located their main base of operations.
What we do know is some of the key figures in the organisation, and their associates: Jojo Fine, a punk who runs drugs through Hell's Kitchen, has been identified as a lieutenant in the NSF, and has one Sandra Renton, the daughter of a local hotelier for the 'Ton Hotel on his payroll.
Investigate the websites of the 'Ton Hotel and see if you can find anything that leads us to the NSF.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-05%2014-22-10.png)

---

## Objectives

* What is the Web Flag?
* What is the User Flag?
* What is the Root Flag?

---

## Summary

* **Target IP:** 10.128.136.69 / `reset.thm`
* **OS:** Windows
* **Vulnerabilities Identified:**
   


***Ports Discovered***

| Port | State | Service | Service Version / Info |
| --- | --- | --- | --- |
| **`80/tcp`** | `OPEN` | **HTTP** |  |
| **`4346/tcp`** | `OPEN` | **elanlm?** |  |
  
---

## Adding target to hosts

```bash
sudo echo -e '10.130.191.50 kitchen.thm' | sudo tee -a /etc/hosts
```

---

## Enumeration & Reconnaissance

> Port Scanning & Network Mapping: Rustscan

