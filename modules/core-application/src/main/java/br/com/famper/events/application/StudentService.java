package br.com.famper.events.application;

import br.com.famper.events.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface StudentService {
  Page<Student> list(String tenant, Pageable pageable, Optional<String> query);
  Student create(String tenant, UpsertStudentCommand command);
}
