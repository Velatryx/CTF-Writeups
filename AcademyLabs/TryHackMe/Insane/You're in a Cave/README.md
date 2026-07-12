## You're in a cave.

Room Description: A room with some ctf elements inspired in text based RPGs

![image](Images/cave.png)

---

## Objectives

1. What was the weird thing carved on the door?
2. What weapon you used to defeat the skeleton?
3. What is the cave flag?
4. What is the outside flag?

---

## Adding target to /etc/hosts

```
sudo echo -e "10.128.186.136 cave.thm" | sudo tee -a /etc/hosts
```

## Enumeration & Recon

> Rustscan results:

```bash
rustscan -a cave.thm --ulimit 5000 --range 1-65535 -- -sCV -O
.----. .-. .-. .----..---.  .----. .---.   .--.  .-. .-.
| {}  }| { } |{ {__ {_   _}{ {__  /  ___} / {} \ |  `| |
| .-. \| {_} |.-._} } | |  .-._} }\     }/  /\  \| |\  |
`-' `-'`-----'`----'  `-'  `----'  `---' `-'  `-'`-' `-'
The Modern Day Port Scanner.
________________________________________
: http://discord.skerritt.blog         :
: https://github.com/RustScan/RustScan :
 --------------------------------------
Open ports, closed hearts.

[~] The config file is expected to be at "/root/.rustscan.toml"
[~] Automatically increasing ulimit value to 5000.
Open 10.128.186.136:80
Open 10.128.186.136:2222
Open 10.128.186.136:3333
[~] Starting Script(s)
PORT     STATE SERVICE    REASON         VERSION
80/tcp   open  http       syn-ack ttl 62 Apache httpd 2.4.41 ((Ubuntu))
|_http-title: Document
|_http-server-header: Apache/2.4.41 (Ubuntu)
| http-methods: 
|_  Supported Methods: GET HEAD POST OPTIONS
2222/tcp open  ssh        syn-ack ttl 62 OpenSSH 8.2p1 Ubuntu 4ubuntu0.1 (Ubuntu Linux; protocol 2.0)
| ssh-hostkey: 
|   3072 79:16:b1:ce:e1:16:79:b4:f1:c7:1f:09:05:b7:75:58 (RSA)
| ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDSaK6ObZFXig5BkU5l9Q93xGbK2DZHPk4lVbU3Bb0nTzNYK13RTmVxgsI99ib9UFNCNFE+z0/Whm1IEfd4zc163VpTBy8XWL+V/9SHALariFmB5oxY/9tYe2y22LLQQjvF5leuNglhawyDa9b/8v85EknYmtgSaw9adqdUFOkX/X9Od5xienC1SFclB+J3BShCTLdObEkhCPOj01EX31BdCfvbdBDpCtBLZy+eMUcnL9BKNHztDjoB6DDCiFvVwchi+B4a9UoXR+jqGyfKmawzVjySgC3EMJ8bhMvhq1Y1odXJ3UOc1UvEt0UbgGOUbsDXP2FeYKzhebLcMw3WPw7/0UH+P6bO3lpCDlT/8cFX3LQ/YPR+jWNXTaxJpSGgtdMQZtjZuxdhtqF4k7dcgnMqg6hlmoMm6L4ttK/BkW8WQPndulkhfijKxAbUjwBKJfzX84ECSakSk92slUH/ANyyceZG2x5GRF+/EMRasYF1+8nQ7UCw66LtkpYmhJGQOO8=
|   256 35:60:6e:3b:a8:ac:4a:6a:76:42:3d:59:13:04:90:19 (ECDSA)
| ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBC5qUgJqhJDqY31rnfF1SEX79P2lCkxWrwIlMwyPbEBimlf8SryTdh0SeJbE1S+yopedohItJgZvnf7inSrqkk4=
|   256 79:a6:05:ca:84:32:dc:59:b4:9b:8b:30:95:34:00:c8 (ED25519)
|_ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIE4DPjU+eVlEZSI6qHQ8/JdPLYigyluwDMOC1+bLo5Op
3333/tcp open  dec-notes? syn-ack ttl 62
| fingerprint-strings: 
|   DNSStatusRequestTCP, DNSVersionBindReqTCP, JavaRMI, NULL, RPCCheck, SMBProgNeg, X11Probe, kumo-server: 
|     You find yourself in a cave, what do you do?
|   FourOhFourRequest, GenericLines, GetRequest, HTTPOptions, Help, Kerberos, LPDString, RTSPRequest, SSLSessionReq, TLSSessionReq, TerminalServerCookie: 
|     You find yourself in a cave, what do you do?
|_    Nothing happens
1 service unrecognized despite returning data. If you know the service/version, please submit the following fingerprint at https://nmap.org/cgi-bin/submit.cgi?new-service :
```

> Feroxbuster results:

```bash
feroxbuster -u http://cave.thm -w /usr/share/wordlists/dirbuster/directory-list-2.3-medium.txt            
                                                                                                                                                                   
 ___  ___  __   __     __      __         __   ___
|__  |__  |__) |__) | /  `    /  \ \_/ | |  \ |__
|    |___ |  \ |  \ | \__,    \__/ / \ | |__/ |___
by Ben "epi" Risher 🤓                 ver: 2.13.1
───────────────────────────┬──────────────────────
 🎯  Target Url            │ http://cave.thm/
 🚩  In-Scope Url          │ cave.thm
 🚀  Threads               │ 50
 📖  Wordlist              │ /usr/share/wordlists/dirbuster/directory-list-2.3-medium.txt
 👌  Status Codes          │ All Status Codes!
 💥  Timeout (secs)        │ 7
 🦡  User-Agent            │ feroxbuster/2.13.1
 💉  Config File           │ /etc/feroxbuster/ferox-config.toml
 🔎  Extract Links         │ true
 🏁  HTTP methods          │ [GET]
 🔃  Recursion Depth       │ 4
───────────────────────────┴──────────────────────
 🏁  Press [ENTER] to use the Scan Management Menu™
──────────────────────────────────────────────────
404      GET        9l       31w      270c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
403      GET        9l       28w      273c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
200      GET        1l        1w      197c http://cave.thm/search
400      GET        0l        0w        0c http://cave.thm/action.php
200      GET       14l       27w      337c http://cave.thm/
200      GET        1l        1w      181c http://cave.thm/attack
200      GET        1l        1w      261c http://cave.thm/lamp
200      GET        1l        1w      249c http://cave.thm/matches
200      GET        1l        1w      161c http://cave.thm/walk
[####################] - 6m    220547/220547  0s      found:7       errors:2      
[####################] - 6m    220546/220546  643/s   http://cave.thm/ 
```

---

## More Recon:

> I digged in `/matches`, `/search`, `/index.php`, and `/action.php`.

1. Inside /matches, I found: `"rO0ABXNyAAZBY3Rpb275vE3ugB8ZOwIAA0wAB2NvbW1hbmR0ABJMamF2YS9sYW5nL1N0cmluZztMAARuYW1lcQB+AAFMAAZvdXRwdXRxAH4AAXhwdABUWW91IGZpbmQgYSBib3ggb2YgbWF0Y2hlcywgaXQgZ2l2ZXMgZW5vdWdoIGZpcmUgZm9yIHlvdSB0byBzZWUgdGhhdCB5b3UncmUgaW4gYHB3ZGAudAAHbWF0Y2hlc3QAAA=="`
2. Inside `/search`:
`"rO0ABXNyAAZBY3Rpb275vE3ugB8ZOwIAA0wAB2NvbW1hbmR0ABJMamF2YS9sYW5nL1N0cmluZztMAARuYW1lcQB+AAFMAAZvdXRwdXRxAH4AAXhwdAAuWW91IGNhbid0IHNlZSBhbnl0aGluZywgdGhlIGNhdmUgaXMgdmVyeSBkYXJrLnQABnNlYXJjaHQAAA=="`

Using cyberchef, I got some readable strings when converted from Base64, which I already knew by connecting to port 3333 using nc:

> matches

```shell
┌──(root㉿kali)-[~]
└─# nc cave.thm 3333
You find yourself in a cave, what do you do?
> matches
You find a box of matches, it gives enough fire for you to see that you're in /home/cave/src.
```

> search

```shell
┌──(root㉿kali)-[~]
└─# nc cave.thm 3333
You find yourself in a cave, what do you do?
search
You can't see anything, the cave is very dark.
```

At least we get a clue about 'pwd': `/home/cave/src`.

> attack

```shell
┌──(root㉿kali)-[~]
└─# nc cave.thm 3333                                                                                            
You find yourself in a cave, what do you do?
attack
You punch the wall, nothing happens.
```

> lamp

```shell
┌──(root㉿kali)-[~]
└─# nc cave.thm 3333
You find yourself in a cave, what do you do?
lamp
You grab a lamp, and it gives enough light to search around
Action.class
RPG.class
RPG.java
Serialize.class
commons-io-2.7.jar
run.sh
```

> walk

```shell
┌──(root㉿kali)-[~]
└─# nc cave.thm 3333
You find yourself in a cave, what do you do?
walk
There's nowhere to go.
```

---

> But when I carried this interaction out using the browser on port `80`, something strange happens:

![image](Images/index-php.png)

> I was redirected to /action.php, which gave 400 error code, even though my input was just 'matches'. So I opened burp suite and started intercepting the traffic to observe the requests.

![image](Images/Screenshot%20From%202026-07-12%2020-25-16.png)

> POST Request in BP looked like this:

![image](Images/POST-before.png)

---

## XXE Injection & Exfiltration

> Then I changed the `Content-Type` header to `application/xml`, because the server also accepted xml type. And it finally gives us something to work with in the error message.

![image](Images/POST-afterXML.png)

> Then I pasted a basic XML text to see if it worked. If we see "Doe" printed on response, it is vulnerable to XXE injection. And... It worked!!!

![image](Images/Screenshot%20From%202026-07-12%2020-48-37.png)

> I already knew the server used php technology, obviously from .php extensions. After this, I exfiltrated `/etc/passwd`, using 'php://' wrapper to encode the output in base64, because as we know, this file includes special characters that are not suitable for XML structure, and easily breaks it, that's why we encode it to avoid those issues. And bull's eye!

![images](Images/Screenshot%20From%202026-07-12%2020-48-47.png)


> Decoded output:

```text
root:x:0:0:root:/root:/bin/bash
daemon:x:1:1:daemon:/usr/sbin:/usr/sbin/nologin
bin:x:2:2:bin:/bin:/usr/sbin/nologin
sys:x:3:3:sys:/dev:/usr/sbin/nologin
sync:x:4:65534:sync:/bin:/bin/sync
games:x:5:60:games:/usr/games:/usr/sbin/nologin
man:x:6:12:man:/var/cache/man:/usr/sbin/nologin
lp:x:7:7:lp:/var/spool/lpd:/usr/sbin/nologin
mail:x:8:8:mail:/var/mail:/usr/sbin/nologin
news:x:9:9:news:/var/spool/news:/usr/sbin/nologin
uucp:x:10:10:uucp:/var/spool/uucp:/usr/sbin/nologin
proxy:x:13:13:proxy:/bin:/usr/sbin/nologin
www-data:x:33:33:www-data:/var/www:/usr/sbin/nologin
backup:x:34:34:backup:/var/backups:/usr/sbin/nologin
list:x:38:38:Mailing List Manager:/var/list:/usr/sbin/nologin
irc:x:39:39:ircd:/var/run/ircd:/usr/sbin/nologin
gnats:x:41:41:Gnats Bug-Reporting System (admin):/var/lib/gnats:/usr/sbin/nologin
nobody:x:65534:65534:nobody:/nonexistent:/usr/sbin/nologin
_apt:x:100:65534::/nonexistent:/usr/sbin/nologin
systemd-timesync:x:101:101:systemd Time Synchronization,,,:/run/systemd:/usr/sbin/nologin
systemd-network:x:102:103:systemd Network Management,,,:/run/systemd:/usr/sbin/nologin
systemd-resolve:x:103:104:systemd Resolver,,,:/run/systemd:/usr/sbin/nologin
messagebus:x:104:105::/nonexistent:/usr/sbin/nologin
sshd:x:105:65534::/run/sshd:/usr/sbin/nologin
cave:x:1000:1000:,,,:/home/cave:/bin/bash
door:x:1001:1001:,,,:/home/door:/bin/bash
skeleton:x:1002:1002:,,,:/home/skeleton:/bin/bash
```

> Oh, we look what we have: `cave`, `door`, and `skeleton`. Looking at the output of /etc/hostname, the current user is `cave`.

## Action.php Contents & Leakage:

![image](Images/Screenshot%20From%202026-07-12%2021-00-49.png)


> Decoded Output:

```text
<?php
     
    libxml_disable_entity_loader (false);
    libxml_use_internal_errors(true);

    $data = trim(file_get_contents('php://input'));
    if($data == ""){
        $data = urldecode(trim(parse_url($_SERVER['REQUEST_URI'], PHP_URL_QUERY)));
    }
    $dom = new DOMDocument();
    $dom->loadXML($data, LIBXML_NOENT | LIBXML_DTDLOAD);
    $xml = simplexml_import_dom($dom);
     
    if(!isset($xml)) {
        header($_SERVER["SERVER_PROTOCOL"]." 400 Bad Request", true, 400);

        if($_SERVER["CONTENT_TYPE"] == "application/xml" || $_SERVER["CONTENT_TYPE"] == "text/xml"){
            foreach(libxml_get_errors() as $xmlError) {
                echo $xmlError->message . "\t";
            }
        }

        exit;
    }

    echo $xml;
?>

```

> Reading RPG.java from `/home/cave/src/RPG.java`:

![image](Images/Screenshot%20From%202026-07-12%2021-57-07.png)

> Decoded Output: [here](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Insane/You're%20in%20a%20Cave/RPG.java)

