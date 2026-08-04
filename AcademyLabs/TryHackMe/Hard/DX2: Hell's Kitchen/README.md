## DX2: Hell's Kitchen — TryHackMe Writeup

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/HellsKitchen.png)

**Room Description:** Can you help compromise a civilian machine that we believe is connected to the NSF?

**Room Link:** [Hell's Kitchen](https://tryhackme.com/room/dx2hellskitchen)

> *We need to recover the lost Ambrosia shipment from the NSF (National Secessionist Forces), the only treatment for the plague known as the Grey Death. However, we haven't located their main base of operations.
What we do know is some of the key figures in the organisation, and their associates: Jojo Fine, a punk who runs drugs through Hell's Kitchen, has been identified as a lieutenant in the NSF, and has one Sandra Renton, the daughter of a local hotelier for the 'Ton Hotel on his payroll.
Investigate the websites of the 'Ton Hotel and see if you can find anything that leads us to the NSF.

![image]()

---

## Objectives

* What is the Web Flag?
* What is the User Flag?
* What is the Root Flag?

---

## Summary
- **Target IP:** 10.130.191.50
- **OS:** Linux (Ubuntu)
- **Vulnerabilities:**


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

```
PORT     STATE SERVICE REASON         VERSION
80/tcp   open  http    syn-ack ttl 62
| http-methods: 
|_  Supported Methods: GET
|_http-title: Welcome to the 'Ton!
| fingerprint-strings: 
|   GetRequest: 
|     HTTP/1.0 200 OK
|     content-length: 859
|     date: Tue, 04 Aug 2026 11:56:51 GMT
4346/tcp open  elanlm? syn-ack ttl 62
| fingerprint-strings: 
|   GenericLines: 
|     HTTP/1.1 408 Request Timeout
|     content-length: 0
|     connection: close
|     date: Tue, 04 Aug 2026 11:56:56 GMT
|   GetRequest: 
|     HTTP/1.0 200 OK
|     content-length: 10909
|     date: Tue, 04 Aug 2026 11:56:56 GMT
```

**About `4346` port**: 

[!] > ELANLM is an IANA-assigned service name tied to port 4346 on TCP, intended for secure communication and data exchange between devices on a local network. In practice, this is the kind of listener you are more likely to keep inside a trusted segment than expose to the internet.

[!] > That matters because anything handling device-to-device coordination or internal data exchange can become an unnecessary attack surface when it is reachable beyond the LAN. On business networks, the right question is usually whether the host and the service still belong in the workflow, not whether outside users need direct access. (SOURCE: [Port Lookup 4346](https://portlookup.com/port-4346/))

