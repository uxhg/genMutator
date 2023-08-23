@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public boolean isEmployeeExist(Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        return employee.isPresent();
    }
}
