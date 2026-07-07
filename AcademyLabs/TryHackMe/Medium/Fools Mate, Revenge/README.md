
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


---

So I went back to website after some exploration, and found an extra thing, options to change the style of the board etc.

<img width="3456" height="1726" alt="Screenshot From 2026-07-07 18-26-11" src="https://github.com/user-attachments/assets/8d990ff1-54fd-4d8e-87b7-41719d00e05e" />

> Opened it up in burpsuite and tried some payloads that could hit the prototype pollution vulnerability:

<img width="3151" height="1809" alt="Screenshot From 2026-07-07 18-27-12" src="https://github.com/user-attachments/assets/55ca1904-103f-4b42-8d6f-5eae58e2082d" />

First, I tried using something like:

```json
{"theme":"midnight","pieceSet":"outline","animationMs":100,
"__proto__": {
    "session.config.unlocked": true
  }

}
```

then 

```json
{"theme":"midnight","pieceSet":"outline","animationMs":100,

  "__proto__": {
    "session": {
      "__proto__": {
        "config": {
          "__proto__": {
            "unlocked": true
          }
        }
      }
    }
  }
}
```

and then

```json
{
  "theme": "midnight",
  "pieceSet": "outline",
  "animationMs": 100,
  "__proto__": {
    "config": {
      "unlocked": true
    }
  }
}
```

> But none of it worked. So I gave up on "__proto__" and switched to "constructor":

```json
{
  "theme": "midnight",
  "pieceSet": "outline",
  "animationMs": 100,
  "constructor":{"prototype":{"unlocked":true}}
}
```

<img width="3177" height="1787" alt="Screenshot From 2026-07-07 18-26-56" src="https://github.com/user-attachments/assets/6c446a30-a31d-4a4b-8e22-9f56dc5b2b70" />


and tested checkmating one last time:

<img width="3173" height="1800" alt="Screenshot From 2026-07-07 18-26-36" src="https://github.com/user-attachments/assets/583d1904-404f-48b7-8604-0189254965ac" />


So... How? (AI written!)

> The backend server evaluates this specific condition to release the flag:

```JavaScript
if (session.config.unlocked) { ... }
```

When this execution occurs, the JavaScript runtime searches for the property unlocked inside the config object. 
If config does not explicitly contain that property, JavaScript traverses upward through the prototype chain to Object.prototype to see if the property exists globally.

> Failure of the First Payload ("session.config.unlocked": true)

```JSON

"__proto__": { "session.config.unlocked": true }
```

JSON keys are treated as literal strings. This payload did not create a nested structure (session -> config -> unlocked).

The Result: It created a single literal property named exactly "session.config.unlocked" on the prototype. When the server evaluated session.config.unlocked, the lookup failed because the nested path did not exist.

> Failure of the Standard Payload ("__proto__")

```JSON
"__proto__": { "unlocked": true }
```

Why it failed: The application or an underlying dependency utilized an input filter (a blocklist) that explicitly checked for and stripped out the key name "__proto__".

The Result: The input was sanitized before reaching the merge function, meaning Object.prototype was never modified.

---

> Success: ("constructor")

```JSON
"constructor": { "prototype": { "unlocked": true } }
```

This payload succeeded due to two precise technical facts:

> Keyword Filter Bypass: The server's validation logic only blocked the string "__proto__". It did not account for alternative accessor properties.

> Prototype Resolution: In JavaScript, every standard object has a constructor property that references the function that created it (the Object constructor). The Object constructor has a prototype property that points to the exact same root memory location as __proto__.

---

When the vulnerable deep-merge function processed this valid JSON structure, it followed the constructor.prototype path and successfully injected the property unlocked: true directly into the base global Object.prototype.

When the server subsequently checked session.config.unlocked, the runtime fell back to the modified Object.prototype, evaluated the condition as true, and returned the flag.
