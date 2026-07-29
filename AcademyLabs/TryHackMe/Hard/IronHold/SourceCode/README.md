### I will put only the useful source codes that expose security risks.

---

1. DataSeeder.java: 
   
We know that computers are deterministic, so if you know the seed value, you can predict the values that a function will generate. We have the whole source code and the seed value, so it will be useful for predicting secrets, passwords, or values depending on where it is used.

> Other than that, I noticed a hardcoded password "IronholdStaff2026!", set for officers and Staff members.

```java
 private List<Staff> seedStaff() {
        Staff kiosk = new Staff();
        kiosk.setUsername("kiosk");
        kiosk.setPassword(passwordEncoder.encode(kioskPassword));
        kiosk.setFullName("Shift Kiosk Account");
        kiosk.setEmail("kiosk@ironhold.example");
        kiosk.setBadgeNumber("K-000");
        kiosk.setRole("OFFICER");

        Staff warden = new Staff();
        warden.setUsername("warden");
        warden.setPassword(passwordEncoder.encode(wardenPassword));
        warden.setFullName("Warden E. Castellan");
        warden.setEmail("e.castellan@ironhold.example");
        warden.setBadgeNumber("W-001");
        warden.setRole("WARDEN");

        String fillerHash = passwordEncoder.encode("IronholdStaff2026!");    \\Here
        String[][] officers = {
                {"j.reyes", "Officer J. Reyes", "O-104"},
                {"m.chen", "Officer M. Chen", "O-118"},
                {"a.osei", "Officer A. Osei", "O-129"},
                {"l.bianchi", "Officer L. Bianchi", "O-142"},
        };
```

---

2. pom.xml:

> I was expecting to find another useful endpoint here, however, what I found was far more valuable - a severely vulnerable dependency. The version 3.2 is vulnerable to Java Deserialization RCE.


Description:
Primary Vulnerability: Java Deserialization RCECVEs: CVE-2015-7501, CVE-2015-4852, CVE-2015-6420  CVSS Score: Critical / High (9.8 / 10)

Mechanics: The library includes serializable functor classes such as org.apache.commons.collections.functors.InvokerTransformer. 
When an application accepts serialized Java objects from untrusted sources (e.g., via RMI, JMX, HTTP headers, or custom sockets) and has this library on its classpath, attackers can chain these classes into a "gadget chain" to achieve arbitrary command execution on the server.  


```
<dependency>
<groupId>commons-collections</groupId>
<artifactId>commons-collections</artifactId>
<version>[3.2,3.2.2)</version>
</dependency>
```
