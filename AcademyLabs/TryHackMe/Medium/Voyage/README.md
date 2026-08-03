## Voyage Writeup

Room Description: Chain multiple vulnerabilities to gain control of a system.

**Room Link**: [Voyage Room](https://tryhackme.com/room/voyage)

> Sometimes in a pentest, you get root access very quickly. But is it the real root or just a container? The voyage might still be going on.


---

## Objectives

1. What is the value of user-level flag?
2. What is the value of root-level flag?

---

## Adding target to hosts

```shell
echo -e '10.128.162.162 voyage.thm' | sudo tee -a /etc/hosts
```

##
