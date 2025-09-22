package org.viniciusvirgilli.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.viniciusvirgilli.model.Cliente;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class ClienteDao {

    EntityManager em;

    public void persist(Cliente entity) { em.persist(entity); }

    public List<Cliente> findAll() {
        return em.createQuery("SELECT c FROM Cliente c ORDEM BY p.id",
                Cliente.class).getResultList();
    }

    public Optional<Cliente> findByCpfCnpj(String cpfCnpj) {
        List<Cliente> resultado = em.createQuery(
                "SELECT c FROM Cliente c WHERE c.cpfCnpj = :cpfCnpj",
                Cliente.class
        ).setParameter("cpfCnpj", cpfCnpj).getResultList();

        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.getFirst());
    }

    public void delete(Cliente entity) { em.remove(entity); }

    public void update(Cliente entity) { em.merge(entity); }
}
