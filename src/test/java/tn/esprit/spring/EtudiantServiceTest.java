package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.Services.Etudiant.IEtudiantService;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.util.List;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EtudiantServiceTest {

    @Autowired
    IEtudiantService etudiantService;

    private static Long savedEtudiantId;

    @AfterAll
    public void cleanup() {
        etudiantService.deleteById(savedEtudiantId);
        Etudiant e = etudiantService.findById(savedEtudiantId);
        Assertions.assertNull(e, "Etudiant should be deleted");
    }

    @Test
    @Order(1)
    public void testAddOrUpdate() {
        Etudiant e = Etudiant.builder()
                .nomEt("John")
                .prenomEt("Doe")
                .cin(12345678L)
                .ecole("ESPRIT")
                .dateNaissance(LocalDate.of(2000, 1, 1))
                .build();

        Etudiant saved = etudiantService.addOrUpdate(e);
        savedEtudiantId = saved.getIdEtudiant();

        Assertions.assertNotNull(saved.getIdEtudiant());
        Assertions.assertEquals("John", saved.getNomEt());
    }

    @Test
    @Order(2)
    public void testFindAll() {
        List<Etudiant> list = etudiantService.findAll();
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    @Order(3)
    public void testFindById() {
        Etudiant e = etudiantService.findById(savedEtudiantId);
        Assertions.assertEquals("John", e.getNomEt());
    }
}
