
## CRLF Injection 

> Inject false data in the journalisation log.

---

> First, let's understand what exactly is CRLF Injection attack. CRLF stands for Carriage Return (\r) and Line Feed (\n), the two characters traditionally used to represent a line break. `\r` ending the line, `\n` starting a new line.

> Now assume you have a web page, and whatever you do is logged normally. For instance, if you send a POST login request, that includes your username/email, password, or any additional required data, they are stored inside the server's logs. Each log entry gets a new line, so they are not confused when analyzed. A basic sample would be from the challenge itself:

```
admin failed to authenticate.
admin authenticated.
guest failed to authenticate.
```

> A SOC Analyst would conclude that admin user failed authentication first time, and succeeded in the second attempt. But what if an attacker could inject a new line, making it seem like there has been a successful attack to a high-privileged user? This itself is not a high severity exposure, but confusing logs can halt SOC Analysts' jobs, or used as a distraction, drawing the attention to a decoy attack. 
