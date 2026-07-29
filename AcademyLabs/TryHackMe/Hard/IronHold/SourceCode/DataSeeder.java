package com.ironhold.seed;

import com.ironhold.config.DataAccessConfig;
import com.ironhold.model.AdminNotice;
import com.ironhold.model.CommissaryOrder;
import com.ironhold.model.IncidentReport;
import com.ironhold.model.Inmate;
import com.ironhold.model.Message;
import com.ironhold.model.Movement;
import com.ironhold.model.Notice;
import com.ironhold.model.Staff;
import com.ironhold.model.Visitation;
import com.ironhold.repository.AdminNoticeRepository;
import com.ironhold.repository.CommissaryOrderRepository;
import com.ironhold.repository.IncidentReportRepository;
import com.ironhold.repository.InmateRepository;
import com.ironhold.repository.MessageRepository;
import com.ironhold.repository.MovementRepository;
import com.ironhold.repository.NoticeRepository;
import com.ironhold.repository.StaffRepository;
import com.ironhold.repository.VisitationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final Random RNG = new Random(42);

    private final StaffRepository staffRepository;
    private final InmateRepository inmateRepository;
    private final MovementRepository movementRepository;
    private final NoticeRepository noticeRepository;
    private final VisitationRepository visitationRepository;
    private final CommissaryOrderRepository commissaryOrderRepository;
    private final MessageRepository messageRepository;
    private final IncidentReportRepository incidentReportRepository;
    private final AdminNoticeRepository adminNoticeRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

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
                       NoticeRepository noticeRepository,
                       VisitationRepository visitationRepository,
                       CommissaryOrderRepository commissaryOrderRepository,
                       MessageRepository messageRepository,
                       IncidentReportRepository incidentReportRepository,
                       AdminNoticeRepository adminNoticeRepository,
                       JdbcTemplate jdbcTemplate,
                       PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.inmateRepository = inmateRepository;
        this.movementRepository = movementRepository;
        this.noticeRepository = noticeRepository;
        this.visitationRepository = visitationRepository;
        this.commissaryOrderRepository = commissaryOrderRepository;
        this.messageRepository = messageRepository;
        this.incidentReportRepository = incidentReportRepository;
        this.adminNoticeRepository = adminNoticeRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        List<Staff> staff = seedStaff();
        List<Inmate> inmates = seedInmates();
        seedMovements(inmates, staff);
        seedNotices();
        seedVisitations(inmates);
        seedCommissary(inmates);
        seedMessages(staff);
        seedIncidents(inmates, staff);
        seedAdminNotices();
        seedCaseFile();
        provisionLookupAccount();
        log.info("Ironhold seed data loaded: {} staff, {} inmates.", staff.size(), inmates.size());
    }

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

    private List<Inmate> seedInmates() {
        String[] firstNames = {"James", "Robert", "Michael", "David", "Marcus", "Elena", "Sofia",
                "Grace", "Daniel", "Victor", "Nadia", "Omar", "Isabel", "Lucas", "Theo",
                "Priya", "Hassan", "Ines", "Kenji", "Ruth"};
        String[] lastNames = {"Doyle", "Marsh", "Okafor", "Petrov", "Alvarez", "Winslow", "Kowalski",
                "Nakamura", "Fitzgerald", "Novak", "Brennan", "Delgado", "Hartley", "Solano",
                "Abara", "Vance", "Castillo", "Whitfield", "Duarte", "Reilly"};
        String[] blocks = {"A-Wing", "B-Wing", "C-Wing", "D-Wing"};
        String[] offenses = {"Burglary", "Fraud", "Grand Theft Auto", "Racketeering",
                "Forgery", "Extortion", "Arson", "Assault"};
        String[] statuses = {"ACTIVE", "ACTIVE", "ACTIVE", "SEGREGATION", "TRANSFERRED"};

        List<Inmate> inmates = new java.util.ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Inmate inmate = new Inmate();
            inmate.setName(firstNames[i % firstNames.length] + " " + lastNames[(i * 3 + 1) % lastNames.length]);
            inmate.setBlock(blocks[i % blocks.length]);
            inmate.setCellNumber((100 + i * 3) + "");
            inmate.setOffense(offenses[i % offenses.length]);
            inmate.setAdmissionDate(LocalDate.of(2022 + (i % 4), 1 + (i % 12), 1 + (i % 27)));
            inmate.setStatus(statuses[i % statuses.length]);
            inmates.add(inmateRepository.save(inmate));
        }
        return inmates;
    }

    private void seedMovements(List<Inmate> inmates, List<Staff> staff) {
        String[] reasons = {"Routine block transfer", "Medical appointment", "Disciplinary segregation",
                "Court appearance", "Work detail reassignment"};
        for (int i = 0; i < 15; i++) {
            Inmate inmate = inmates.get(RNG.nextInt(inmates.size()));
            Movement movement = new Movement();
            movement.setInmateId(inmate.getId());
            movement.setFromBlock(inmate.getBlock());
            movement.setToBlock(inmates.get(RNG.nextInt(inmates.size())).getBlock());
            movement.setReason(reasons[RNG.nextInt(reasons.length)]);
            movement.setAuthorizedBy(staff.get(2 + RNG.nextInt(staff.size() - 2)).getUsername());
            movement.setOccurredAt(LocalDateTime.now().minusDays(RNG.nextInt(180)));
            movementRepository.save(movement);
        }
    }

    private void seedNotices() {
        Notice older = new Notice();
        older.setTitle("Commissary price adjustment");
        older.setBody("Commissary pricing for stamps and hygiene items changes on the "
                + "1st of next month. See the commissary board for the updated list.");
        older.setPostedBy("warden");
        older.setPostedAt(LocalDateTime.now().minusDays(9));
        noticeRepository.save(older);

        Notice kioskCredentialNotice = new Notice();
        kioskCredentialNotice.setTitle("Shift handover: kiosk account reminder");
        kioskCredentialNotice.setBody("Reminder for all shifts: the kiosk terminal login is "
                + "shared across the floor and rotates only when IT reissues it. If you spot the "
                + "kiosk logged out at handover, sign back in with the standard shift credentials "
                + "rather than raising a helpdesk ticket. Flag: " + flag1);
        kioskCredentialNotice.setPostedBy("warden");
        kioskCredentialNotice.setPostedAt(LocalDateTime.now().minusHours(6));
        noticeRepository.save(kioskCredentialNotice);
    }

    private void seedVisitations(List<Inmate> inmates) {
        String[] relationships = {"Spouse", "Sibling", "Parent", "Legal counsel", "Child"};
        String[] statuses = {"APPROVED", "PENDING", "APPROVED", "APPROVED", "PENDING"};
        String[] visitorNames = {"Maria Doyle", "Tom Reilly", "Fatima Hassan", "Chen Wei",
                "Laura Novak", "Ben Vance", "Ana Castillo", "Peter Marsh"};
        for (int i = 0; i < 10; i++) {
            Visitation visitation = new Visitation();
            visitation.setInmateId(inmates.get(RNG.nextInt(inmates.size())).getId());
            visitation.setVisitorName(visitorNames[i % visitorNames.length]);
            visitation.setRelationship(relationships[i % relationships.length]);
            visitation.setScheduledAt(LocalDateTime.now().plusDays(RNG.nextInt(21)));
            visitation.setStatus(statuses[i % statuses.length]);
            visitation.setApprovedBy("warden");
            visitationRepository.save(visitation);
        }
    }

    private void seedCommissary(List<Inmate> inmates) {
        String[] itemSets = {"Stamps, notepad", "Hygiene kit", "Instant coffee, snacks",
                "Reading glasses", "Radio batteries", "Playing cards, notepad"};
        String[] statuses = {"FULFILLED", "PENDING", "FULFILLED"};
        for (int i = 0; i < 12; i++) {
            CommissaryOrder order = new CommissaryOrder();
            order.setInmateId(inmates.get(RNG.nextInt(inmates.size())).getId());
            order.setItems(itemSets[i % itemSets.length]);
            order.setTotalCredits(5 + RNG.nextInt(40));
            order.setStatus(statuses[i % statuses.length]);
            order.setOrderedAt(LocalDateTime.now().minusDays(RNG.nextInt(30)));
            commissaryOrderRepository.save(order);
        }
    }

    private void seedMessages(List<Staff> staff) {
        String[][] messages = {
                {"Shift handover notes", "Block C quiet, no incidents. Commissary delivery expected 09:00."},
                {"Visitor list update", "Two visitor requests for tomorrow still need approval."},
                {"Maintenance ticket", "D-Wing shower drain still backing up, logged with facilities."},
                {"Roster swap request", "Can we swap Thursday's shift? Family appointment came up."},
                {"Training reminder", "Annual use-of-force refresher is due for all floor staff this month."},
        };
        for (int i = 0; i < messages.length; i++) {
            Message message = new Message();
            message.setSubject(messages[i][0]);
            message.setBody(messages[i][1]);
            message.setFromStaff(staff.get(2 + (i % (staff.size() - 2))).getUsername());
            message.setToStaff(staff.get(2 + ((i + 1) % (staff.size() - 2))).getUsername());
            message.setSentAt(LocalDateTime.now().minusHours(i * 5));
            messageRepository.save(message);
        }
    }

    private void seedIncidents(List<Inmate> inmates, List<Staff> staff) {
        String[][] incidents = {
                {"Altercation in yard", "MEDIUM", "Brief altercation broken up without injury.", "altercation-yard-notes.txt"},
                {"Contraband found in cell search", "HIGH", "Routine search turned up a modified razor.", "contraband-search-notes.txt"},
                {"Fire alarm false trigger", "LOW", "Kitchen smoke tripped the alarm, no fire.", null},
                {"Medical emergency", "HIGH", "Inmate collapsed during yard time, treated on site.", null},
                {"Verbal threat against staff", "MEDIUM", "Reported during evening headcount.", null},
                {"Lockdown drill", "LOW", "Scheduled quarterly drill, ran to plan.", null},
        };
        String[] blocks = {"A-Wing", "B-Wing", "C-Wing", "D-Wing"};
        for (int i = 0; i < incidents.length; i++) {
            IncidentReport report = new IncidentReport();
            report.setTitle(incidents[i][0]);
            report.setSeverity(incidents[i][1]);
            report.setDescription(incidents[i][2]);
            report.setEvidenceFile(incidents[i][3]);
            report.setBlock(blocks[i % blocks.length]);
            report.setReportedBy(staff.get(2 + (i % (staff.size() - 2))).getUsername());
            report.setReportedAt(LocalDateTime.now().minusDays(i * 2));
            incidentReportRepository.save(report);
        }
    }

    private void seedAdminNotices() {
        AdminNotice notice = new AdminNotice();
        notice.setTitle("Facility Master Override Code");
        notice.setBody(flag3);
        notice.setPostedBy("warden");
        notice.setPostedAt(LocalDateTime.now().minusDays(2));
        adminNoticeRepository.save(notice);
    }

    private void seedCaseFile() {
        jdbcTemplate.update(
                "INSERT INTO case_files (case_number, title, summary, status, opened_at) VALUES (?, ?, ?, ?, ?)",
                "IA-2024-007", "Internal Affairs Review", flag2, "OPEN",
                LocalDateTime.now().minusMonths(3));
    }
}
