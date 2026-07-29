## IronHold 

Room Description: The source leaked. Read it like an attacker, chain the flaws, and shell the door-control server.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/IronHold/Images/Ironhold.png)

> IronHold is retiring its inmate-management platform. Somewhere in the handover, a developer pushed the complete repository to a public mirror and then left the company. Facility security wants a straight answer before the system goes dark for good: if that repository is out there, how far could someone actually get?
We start with nothing but what leaked: the full, unredacted source, and a live copy of the application still running on the network. No credentials, no map, no walkthrough. The code tells us what the developers got wrong; the running instance tells us if we're right.
Get all four and Ironhold's last system goes down the same way it went up: on its own mistakes.
Download the source archive attached to this task and start reading. The lab machine is reachable at http://MACHINE_IP:8080.


---

## Objectives

1. What is the flag on the officer dashboard once you're inside the system?
2. What is the flag in the staff record that no page on the site will show you?
3. What is the flag on the warden's door-control panel?
4. What is the flag waiting on the facility server once you're through the gate?

---
