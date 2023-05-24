package dat3.p2backend.config;

import dat3.p2backend.entity.ImageLink;
import dat3.p2backend.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import dat3.p2backend.entity.SleepingBag;
import dat3.p2backend.entity.SleepingBagExternal;
import dat3.p2backend.repository.ImageLinkRepository;
import dat3.p2backend.repository.SleepingBagExternalRepository;
import dat3.p2backend.repository.SleepingBagRepository;
import dat3.security.dto.UserWithRolesResponse;
import dat3.security.entity.Role;
import dat3.security.entity.UserWithRoles;
import dat3.security.repository.UserWithRolesRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.io.*;


@Component
public class DeveloperData implements CommandLineRunner {

    SleepingBagRepository sleepingBagRepository;

    SleepingBagExternalRepository sleepingBagExternalRepository;
    ImageLinkRepository imageLinkRepository;

    PasswordEncoder passwordEncoder;

    @Autowired
    UserWithRolesRepository userWithRolesRepository;

    public DeveloperData(SleepingBagRepository sleepingBagRepository, SleepingBagExternalRepository sleepingBagExternalRepository,
                         ImageLinkRepository imageLinkRepository, PasswordEncoder passwordEncoder) {
        this.sleepingBagRepository = sleepingBagRepository;
        this.sleepingBagExternalRepository = sleepingBagExternalRepository;
        this.imageLinkRepository = imageLinkRepository;
        this.passwordEncoder = passwordEncoder;
    }




    @Override
    public void run(String... args) throws Exception {
        setupUserWithRoleUsers();
        readImagesFromFile();
        importData();
    }

    public void readImagesFromFile() {

           String pathLinks = "pictures_links.csv";

        try {
            //BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pathLinks),"ISO-8859-1"));
            BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(pathLinks), "ISO-8859-1"));

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setDelimiter(',')
                .build();

            Iterable<CSVRecord> records = csvFormat.parse(in);

            boolean skippedFirstRow = false;
            for (CSVRecord record : records) {
                if (!skippedFirstRow) {
                    skippedFirstRow = true;
                    continue;
                }
                ImageLink imageLink  = new ImageLink(record.get(0), record.get(1));
                imageLinkRepository.save(imageLink);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void importData() {
        String path = "sovepose-data.csv";

        try {
            //BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "ISO-8859-1"));
            BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(path), "ISO-8859-1"));

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .build();

            Iterable<CSVRecord> records = csvFormat.parse(in);

            boolean skippedFirstRow = false;
            for (CSVRecord record : records) {
                if (skippedFirstRow == false) {
                    skippedFirstRow = true;
                    continue;
                }

                SleepingBagExternal sleepingBagExternal = new SleepingBagExternal(
                    record.get(0),
                    record.get(1),
                    record.get(2),
                    record.get(3),
                    record.get(4),
                    record.get(5),
                    record.get(6),
                    record.get(7),
                    record.get(8),
                    record.get(9),
                    record.get(10),
                    record.get(11),
                    record.get(12),
                    record.get(13)
                );

                SleepingBag sleepingBag = new SleepingBag(sleepingBagExternal);

                sleepingBagRepository.save(sleepingBag);

                sleepingBagExternalRepository.save(sleepingBagExternal);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void setupUserWithRoleUsers() {

        String passwordUsedByAll = "test12";


        String pw = passwordEncoder.encode(passwordUsedByAll);
        UserWithRoles userWithRoles1 = new UserWithRoles("user1", pw, "user1@a.dk");
            userWithRoles1.addRole(Role.USER);
            userWithRolesRepository.save(userWithRoles1);


    }

}


