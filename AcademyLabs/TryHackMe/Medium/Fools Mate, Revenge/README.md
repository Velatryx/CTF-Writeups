
## Room Description: Do you have what it takes to defeat me and claim your prize?

`I see my client-side defences were no match for you, well done, my apprentice! Let's see if you have what it takes to claim your prize.`

resources (USEFUL!): https://learn.snyk.io/lesson/prototype-pollution/?ecosystem=javascript

---

First, I tried checkmating the king to see if it prevents us with any tricks:

<img width="3456" height="1717" alt="Screenshot From 2026-07-07 17-27-07" src="https://github.com/user-attachments/assets/6bca1bcd-8d77-4e9e-b25d-ab723b6ec2ca" />

This time, it accepted it, however, we did not receive our reward (flag). So I fired up burp suite to see what's really going on:

<img width="3177" height="1791" alt="Screenshot From 2026-07-07 18-26-29" src="https://github.com/user-attachments/assets/35ef3281-56db-43ba-84aa-6b6865994153" />

It gave an unusual error with a reason: session.config.unlocked not set. I thought it was another parameter to be included in json body, however, it was a bit different. It was pointing to a prototype pollution. 

> What is prototype pollution and how it occurs? You can learn interactively [here](https://learn.snyk.io/lesson/prototype-pollution/?ecosystem=javascript) or [this one on tryhackme](https://tryhackme.com/room/prototypepollution)
