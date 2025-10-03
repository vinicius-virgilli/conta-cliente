package org.viniciusvirgilli.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.viniciusvirgilli.enums.TipoContaEnum;
import org.viniciusvirgilli.model.Cliente;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class ClienteDao {

    @Inject
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

    public boolean jaExisteConta(String cpfCnpj, TipoContaEnum tipoConta) {
        Optional<Cliente> contaExistente = em.createQuery(
                "SELECT c FROM Cliente c WHERE c.cpfCnpj = :cpfCnpj AND c.tipoConta = :tipoConta",
                Cliente.class
        ).setParameter("cpfCnpj", cpfCnpj)
                .setParameter("tipoConta", tipoConta)
                .getResultList()
                .stream()
                .findFirst();

        return contaExistente.isPresent();
    }

    public Optional<Cliente> findByIdOptional(Long id) {
        Cliente cliente = em.find(Cliente.class, id);
        return Optional.ofNullable(cliente);
    }

    public Optional<Cliente> findByCpfCnpjAndTipoConta(String cpjCnpj, TipoContaEnum tipoContaEnum) {
        Optional<Cliente> cliente = em.createQuery(
                "SELECT c FROM Cliente c WHERE c.cpfCnpj = :cpfCnpj AND c.tipoConta = :tipoConta",
                Cliente.class
        ).setParameter("cpfCnpj", cpjCnpj)
                .setParameter("tipoConta", tipoContaEnum)
                .getResultList()
                .stream()
                .findFirst();

        return cliente;
    }
}
