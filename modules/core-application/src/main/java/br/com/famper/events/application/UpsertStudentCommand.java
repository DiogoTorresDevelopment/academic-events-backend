package br.com.famper.events.application;

public record UpsertStudentCommand(String nome, String email, String cpf) {
  public static UpsertStudentCommand of(String nome, String email, String cpf) {
    return new UpsertStudentCommand(nome, email, cpf);
  }
}
