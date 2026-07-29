### I will put only the useful source codes that expose security risks.

---

## 1. DataSeeder.java: 
   
We know that computers are deterministic, so if you know the seed value, you can predict the values that a function will generate. We have the whole source code and the seed value, so it will be useful for predicting secrets, passwords, or values depending on where it is used.

> Other than that, I noticed a hardcoded password "IronholdStaff2026!", set for officers and Staff members.


> Seed value  exposed:

```java
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final Random RNG = new Random(42);

    private final StaffRepository staffRepository;
    private final InmateRepository inmateRepository;
    private final MovementRepository movementRepository;

...
    @Value("${app.kiosk.pw}")
    private String kioskPassword;

    @Value("${app.warden.password}")
    private String wardenPassword;

    @Value("${app.flag1.secret}")
    private String flag1;

    @Value("${app.flag2.secret}")
    private String flag2;

    @Value("${app.flag3.secret}")
    private String flag3;

    public DataSeeder(StaffRepository staffRepository,
                       InmateRepository inmateRepository,
                       MovementRepository movementRepository,
...
}
```

> Exposed plaintext credentials

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

## 2. pom.xml:

> I was expecting to find another useful endpoint here, however, what I found was far more valuable - a severely vulnerable dependency. The version 3.2 is vulnerable to Java Deserialization RCE.


Description:
Primary Vulnerability: Java Deserialization RCECVEs: CVE-2015-7501, CVE-2015-4852, CVE-2015-6420  CVSS Score: Critical / High (9.8 / 10)

Mechanics: The library includes serializable functor classes such as org.apache.commons.collections.functors.InvokerTransformer. 
When an application accepts serialized Java objects from untrusted sources (e.g., via RMI, JMX, HTTP headers, or custom sockets) and has this library on its classpath, attackers can chain these classes into a "gadget chain" to achieve arbitrary command execution on the server.  


```xml
<dependency>
<groupId>commons-collections</groupId>
<artifactId>commons-collections</artifactId>
<version>[3.2,3.2.2)</version>
</dependency>
```

---

## 3. DataAccessConfig.java:

> In this file, there are also exposed credentials, which looks like a special user created just for looking up things like inmates, and case files. The credentials are assigned to constants declared in a public class.

```java
@Configuration
public class DataAccessConfig {

    public static final String LOOKUP_USER = "ironhold_lookup";
    public static final String LOOKUP_PASSWORD = "Lk_r0_2091!";

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
```

> Found in another file.

```java
    private void provisionLookupAccount() {
        // The inmate lookup connects under a reduced-privilege account rather than
        // the application account. It is granted read access only to the record
        // tables that feature serves, so it cannot reach staff credentials,
        // internal notices, or host files even if a query is malformed.
        jdbcTemplate.execute("CREATE USER IF NOT EXISTS " + DataAccessConfig.LOOKUP_USER
                + " PASSWORD '" + DataAccessConfig.LOOKUP_PASSWORD + "'");
        jdbcTemplate.execute("GRANT SELECT ON inmates TO " + DataAccessConfig.LOOKUP_USER);
        jdbcTemplate.execute("GRANT SELECT ON case_files TO " + DataAccessConfig.LOOKUP_USER);
```

---

## 4. InmateController.java: SQL Injection

> The injection point is obvious, since there is no sanitization for query.

```java
    @GetMapping("/inmates/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        List<Map<String, Object>> results;
        if (q == null || q.isBlank()) {
            results = jdbcTemplate.queryForList("SELECT id, name, block FROM inmates");
        } else {
            String sql = "SELECT id, name, block FROM inmates WHERE name = '" + q + "'";
            results = jdbcTemplate.queryForList(sql);
        }
        model.addAttribute("results", results);
        model.addAttribute("query", q == null ? "" : q);
        return "inmate-search";
    }

```

> Two table names inside /seed/DataSeeder.java: `case_files` and `inmates`.

```
    private void provisionLookupAccount() {
        // The inmate lookup connects under a reduced-privilege account rather than
        // the application account. It is granted read access only to the record
        // tables that feature serves, so it cannot reach staff credentials,
        // internal notices, or host files even if a query is malformed.
        jdbcTemplate.execute("CREATE USER IF NOT EXISTS " + DataAccessConfig.LOOKUP_USER
                + " PASSWORD '" + DataAccessConfig.LOOKUP_PASSWORD + "'");
        jdbcTemplate.execute("GRANT SELECT ON inmates TO " + DataAccessConfig.LOOKUP_USER);
        jdbcTemplate.execute("GRANT SELECT ON case_files TO " + DataAccessConfig.LOOKUP_USER);
    }
```
