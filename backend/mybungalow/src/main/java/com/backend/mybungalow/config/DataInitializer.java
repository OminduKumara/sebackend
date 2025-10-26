package com.backend.mybungalow.config;

import com.backend.mybungalow.domain.Attendance;
import com.backend.mybungalow.domain.AttendanceStatus;
import com.backend.mybungalow.model.Employee;
import com.backend.mybungalow.model.EmployeeStatus;
import com.backend.mybungalow.model.User;
import com.backend.mybungalow.repository.AttendanceRepository;
import com.backend.mybungalow.repository.EmployeeRepository;
import com.backend.mybungalow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(EmployeeRepository employeeRepository,
                           AttendanceRepository attendanceRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize default admin user if no users exist
        if (userRepository.count() == 0) {
            initializeDefaultAdmin();
        }
        
        // Only initialize if no employees exist
        if (employeeRepository.count() == 0) {
            initializeSampleData();
        }
    }

    private void initializeDefaultAdmin() {
        User admin = User.builder()
                .username("admin")
                .email("admin@mybungalow.com")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .isActive(true)
                .build();
        
        userRepository.save(admin);
        System.out.println("Default admin user created:");
        System.out.println("Username: admin");
        System.out.println("Password: admin123");
        System.out.println("Email: admin@mybungalow.com");
    }

    private void initializeSampleData() {
        // Create sample employees
        Employee emp1 = new Employee();
        emp1.setFirstName("John");
        emp1.setLastName("Doe");
        emp1.setNic("200331511489");
        emp1.setAddress("123 Main Street");
        emp1.setDepartment("IT");
        emp1.setPosition("Software Developer");
        emp1.setHireDate(LocalDate.of(2023, 1, 15));
        emp1.setStatus(EmployeeStatus.ACTIVE);
        Employee savedEmp1 = employeeRepository.save(emp1);

        Employee emp2 = new Employee();
        emp2.setFirstName("Jane");
        emp2.setLastName("Smith");
        emp2.setNic("200331511480");
        emp2.setAddress("123 Main Street");
        emp2.setDepartment("HR");
        emp2.setPosition("HR Manager");
        emp2.setHireDate(LocalDate.of(2022, 6, 10));
        emp2.setStatus(EmployeeStatus.ACTIVE);
        Employee savedEmp2 = employeeRepository.save(emp2);

        // Create sample attendance records for the current month
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        
        // John's attendance (present most days)
        for (int i = 1; i <= today.getDayOfMonth(); i++) {
            LocalDate workDate = startOfMonth.withDayOfMonth(i);
            if (workDate.isBefore(today) || workDate.isEqual(today)) {
                Attendance attendance = new Attendance();
                attendance.setEmployee(savedEmp1);
                attendance.setWorkDate(workDate);
                // Present on weekdays, absent on weekends
                if (workDate.getDayOfWeek().getValue() <= 5) {
                    attendance.setStatus(AttendanceStatus.PRESENT);
                } else {
                    attendance.setStatus(AttendanceStatus.ABSENT);
                }
                attendanceRepository.save(attendance);
            }
        }

        // Jane's attendance (present most days, one leave day)
        for (int i = 1; i <= today.getDayOfMonth(); i++) {
            LocalDate workDate = startOfMonth.withDayOfMonth(i);
            if (workDate.isBefore(today) || workDate.isEqual(today)) {
                Attendance attendance = new Attendance();
                attendance.setEmployee(savedEmp2);
                attendance.setWorkDate(workDate);
                
                if (workDate.getDayOfWeek().getValue() <= 5) {
                    // Present on weekdays, except one leave day
                    if (workDate.getDayOfMonth() == 15) {
                        attendance.setStatus(AttendanceStatus.LEAVE);
                    } else {
                        attendance.setStatus(AttendanceStatus.PRESENT);
                    }
                } else {
                    attendance.setStatus(AttendanceStatus.ABSENT);
                }
                attendanceRepository.save(attendance);
            }
        }

        System.out.println("Sample data initialized successfully!");
        System.out.println("Created 2 employees and attendance records for current month.");
    }
}
