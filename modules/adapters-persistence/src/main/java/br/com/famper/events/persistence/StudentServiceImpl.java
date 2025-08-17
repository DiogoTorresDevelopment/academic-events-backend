package br.com.famper.events.persistence;

import br.com.famper.events.application.StudentService;
import br.com.famper.events.application.UpsertStudentCommand;
import br.com.famper.events.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {
  private final StudentRepository repo;

  public StudentServiceImpl(StudentRepository repo) { this.repo = repo; }

  @Override
  public Page<Student> list(String tenant, Pageable pageable, Optional<String> query) {
    UUID tenantId = resolveTenant(tenant);
    return repo.findByClientAndQuery(tenantId, query.orElse(null), pageable);
  }

  @Override
  public Student create(String tenant, UpsertStudentCommand cmd) {
    UUID tenantId = resolveTenant(tenant);
    Student s = new Student();
    s.setClientId(tenantId);
    s.setNome(cmd.nome());
    s.setEmail(cmd.email());
    s.setCpf(cmd.cpf());
    return repo.save(s);
  }

  private UUID resolveTenant(String tenantSlugOrId) {
    try { return UUID.fromString(tenantSlugOrId); }
    catch (Exception e) { throw new IllegalArgumentException("Invalid tenant id/slug for skeleton"); }
  }
}
