### I will put only the useful source codes that expose security risks.

---

1. DataSeeder.java: Why is it vulnerable?
   
We know that computers are deterministic, so if you know the seed value, you can predict the values that a function will generate. We have the whole source code and the seed value, so it will be useful for predicting secrets, passwords, or values depending on where it is used.

> So we see a hardcoded password "IronholdStaff2026!", set for officers and Staff members.

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

        String fillerHash = passwordEncoder.encode("IronholdStaff2026!");
        String[][] officers = {
                {"j.reyes", "Officer J. Reyes", "O-104"},
                {"m.chen", "Officer M. Chen", "O-118"},
                {"a.osei", "Officer A. Osei", "O-129"},
                {"l.bianchi", "Officer L. Bianchi", "O-142"},
        };

        List<Staff> all = new java.util.ArrayList<>();
        all.add(staffRepository.save(kiosk));
        all.add(staffRepository.save(warden));
        for (String[] o : officers) {
            Staff officer = new Staff();
            officer.setUsername(o[0]);
            officer.setPassword(fillerHash);
            officer.setFullName(o[1]);
            officer.setEmail(o[0] + "@ironhold.example");
            officer.setBadgeNumber(o[2]);
            officer.setRole("OFFICER");
            all.add(staffRepository.save(officer));
        }
        return all;
    }
```
