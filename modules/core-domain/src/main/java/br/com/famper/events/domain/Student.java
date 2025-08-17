package br.com.famper.events.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "aluno")
public class Student {
  @Id
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(name = "cliente_id", nullable = false, columnDefinition = "uuid")
  private UUID clientId;

  @Column(nullable = false, length = 200)
  private String nome;

  @Column(length = 200)
  private String email;

  @Column(length = 20)
  private String cpf;

  @Column(name = "criado_em")
  private OffsetDateTime createdAt;

  @PrePersist
  public void prePersist() {
    if (id == null) id = UUID.randomUUID();
    if (createdAt == null) createdAt = OffsetDateTime.now();
  }

  // getters/setters
  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getClientId() { return clientId; }
  public void setClientId(UUID clientId) { this.clientId = clientId; }
  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getCpf() { return cpf; }
  public void setCpf(String cpf) { this.cpf = cpf; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}