package br.com.famper.events.web.students;

import br.com.famper.events.domain.Student;

public class StudentDTO {
  private String id;
  private String nome;
  private String email;
  private String cpf;

  public static StudentDTO from(Student s) {
    var dto = new StudentDTO();
    dto.id = s.getId().toString();
    dto.nome = s.getNome();
    dto.email = s.getEmail();
    dto.cpf = s.getCpf();
    return dto;
  }
  // getters/setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getCpf() { return cpf; }
  public void setCpf(String cpf) { this.cpf = cpf; }
}