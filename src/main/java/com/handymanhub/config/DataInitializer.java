package com.handymanhub.config;

import com.handymanhub.model.Skill;
import com.handymanhub.repository.SkillRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.handymanhub.model.User;
import com.handymanhub.model.Customer;
import com.handymanhub.model.Worker;
import com.handymanhub.repository.UserRepository;
import com.handymanhub.repository.CustomerRepository;
import com.handymanhub.repository.WorkerRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedData(
            UserRepository userRepo,
            CustomerRepository customerRepo,
            WorkerRepository workerRepo,
            SkillRepository skillRepo,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepo.count() > 0) return;

            // Admin
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@handyman.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepo.save(admin);

            // Customer user
            User customerUser = new User();
            customerUser.setName("Shresth");
            customerUser.setEmail("shresthb@gmail.com");
            customerUser.setPassword(passwordEncoder.encode("shresth123"));
            customerUser.setRole("CUSTOMER");
            userRepo.save(customerUser);

            // Customer profile
            Customer customer = new Customer();
            customer.setName("Shresth");
            customer.setEmail("shresthb@gmail.com");
            customer.setPhone("9876501234");
            customer.setPincode("462001");
            customer.setAddress("123 Main Street, Bhopal");
            customerRepo.save(customer);

            // Skills
            String[][] skills = {
                {"Plumbing", "Plumbing", "Pipe fitting, leakage repair, bathroom installations"},
                {"Electrician", "Electrical", "Wiring, switchboard repair, appliance installation"},
                {"Carpentry", "Civil", "Furniture repair, door/window fitting, woodwork"},
                {"Painting", "Domestic", "Wall painting, waterproofing, interior design"}
            };
            for (String[] s : skills) {
                Skill skill = new Skill();
                skill.setName(s[0]);
                skill.setCategory(s[1]);
                skill.setDescription(s[2]);
                skillRepo.save(skill);
            }

            // Workers
            String[][] workers = {
                {"Ramu Kumar", "9876501234", "462001", "500"},
                {"Suresh Yadav", "9876501235", "462001", "600"},
                {"Amit Sharma", "9876501236", "462002", "450"}
            };
            for (String[] w : workers) {
                Worker worker = new Worker();
                worker.setName(w[0]);
                worker.setPhone(w[1]);
                worker.setPincode(w[2]);
                worker.setDailyRate(Integer.parseInt(w[3]));
                worker.setAvailable(true);
                workerRepo.save(worker);
            }
        };
    }
}
