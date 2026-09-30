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

```zsh
nmap -p- -T4 -n -Pn anon.thm
```

```zsh
nmap -p80 -sV anon.thm --script http*
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Anonymous%20Playground/Images/Screenshot%20From%202026-09-30%2018-40-18.png)

> Directory Bruteforcing gave me the secret directory (which is already exposed inside /robots.txt): `/zYdHuAKjP`. If we visit it, we are denied access. But how does the server know who we are, and determine if we have the authorization to access this resource or not? After intercepting the request with burpsuite, I noticed we were assigned a cookie, which had the value `access=denied`. So a simple action like changing the `denied` value to `granted` will grant us the access to see what's in this directory.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Anonymous%20Playground/Images/Screenshot%20From%202026-09-30%2018-50-28.png)

> Before

```
GET /zYdHuAKjP/ HTTP/1.1
Host: anon.thm
Cache-Control: max-age=0
Accept-Language: en-US,en;q=0.9
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
Accept-Encoding: gzip, deflate, br
Cookie: access=denied
Connection: keep-alive

```

> After

```
GET /zYdHuAKjP/ HTTP/1.1
Host: anon.thm
Cache-Control: max-age=0
Accept-Language: en-US,en;q=0.9
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
Accept-Encoding: gzip, deflate, br
Cookie: access=granted
Connection: keep-alive

```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Anonymous%20Playground/Images/Screenshot%20From%202026-09-30%2019-38-47.png)


> It looks like we have to decode this. From the structure of it, it's really easy to tell that `::` is the separator for something like `username:password`. Secondly, there was a pattern - one lowercase and one uppercase character, and finally another clue is that there is a partial repetition in password section which has the same string from username in its first part. Which we can conclude it's something like `user:user123@!` for SSH. Now, with the help of the hint, we see `zA is 'a'`. Firstly, I converted `z` to its alphabetical position - `26`, and `A` to `27` as it was uppercase, and tried to solve it like `27-26=1` which made sense, as 'a' would be 1. But as it did not work, I used `A` as 1 as well, and solved it like `26+1=1`, as the next letter after `z` would be `a` if we think of it as a loop. Finally, the credentials we get is:

```
MAGNA::MAGNAISANELEPHANT
```

---

## Initial Foothold

> Let's ssh into user `magna`

```zsh
ssh magna@anon.thm
```

