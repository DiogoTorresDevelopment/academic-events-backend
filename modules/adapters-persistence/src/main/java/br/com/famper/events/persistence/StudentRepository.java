package br.com.famper.events.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.famper.events.domain.Student;
@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
  @Query("""
         select s from Student s
         where s.clientId = :clientId
           and (:q is null
                or lower(s.nome) like lower(concat('%',:q,'%'))
                or lower(s.email) like lower(concat('%',:q,'%')))
         """)
  Page<Student> findByClientAndQuery(@Param("clientId") UUID clientId,
                                     @Param("q") String q,
                                     Pageable pageable);
}
