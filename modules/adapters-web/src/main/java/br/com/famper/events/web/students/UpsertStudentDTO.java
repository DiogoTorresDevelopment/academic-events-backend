package br.com.famper.events.web.students;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpsertStudentDTO {
  @NotBlank
  private String nome;
  @Email
  private String email;
  private String cpf;

  // getters/setters
  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getCpf() { return cpf; }
  public void setCpf(String cpf) { this.cpf = cpf; }
}