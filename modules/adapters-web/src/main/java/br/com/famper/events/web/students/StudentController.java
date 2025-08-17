package br.com.famper.events.web.students;

import br.com.famper.events.application.StudentService;
import br.com.famper.events.application.UpsertStudentCommand;
import br.com.famper.events.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/{tenant}/students")
public class StudentController {
  private final StudentService service;

  public StudentController(StudentService service) { this.service = service; }

  @GetMapping
  @PreAuthorize("hasAuthority('SCOPE_organizer:write') or hasAuthority('SCOPE_admin:full')")
  public Page<StudentDTO> list(@PathVariable String tenant, Pageable pageable, @RequestParam Optional<String> query) {
    Page<Student> page = service.list(tenant, pageable, query);
    return page.map(StudentDTO::from);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAuthority('SCOPE_organizer:write') or hasAuthority('SCOPE_admin:full')")
  public StudentDTO create(@PathVariable String tenant, @RequestBody UpsertStudentDTO dto) {
    var cmd = UpsertStudentCommand.of(dto.getNome(), dto.getEmail(), dto.getCpf());
    Student saved = service.create(tenant, cmd);
    return StudentDTO.from(saved);
  }
}
