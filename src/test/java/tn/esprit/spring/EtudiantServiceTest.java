package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.Services.Etudiant.IEtudiantService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EtudiantServiceTest {

    @Mock
    IEtudiantService etudiantService;

    @Test
    @Order(1)
    public void testAddOrUpdate() {
        Etudiant e = getEtudiant();
        when(etudiantService.addOrUpdate(any()))
                .thenReturn(e);

        Etudiant saved = etudiantService.addOrUpdate(e);

        Assertions.assertEquals(getEtudiant(), saved);
    }

    @Test
    @Order(2)
    public void testFindAll() {
        when(etudiantService.findAll())
                .thenReturn(List.of(getEtudiant()));

        List<Etudiant> list = etudiantService.findAll();

        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(list.get(0), getEtudiant());
    }

    @Test
    @Order(3)
    public void testFindById() {
        when(etudiantService.findById(1L))
                .thenReturn(getEtudiant());

        Etudiant e = etudiantService.findById(1L);

        Assertions.assertEquals(getEtudiant(), e);
    }

    private Etudiant getEtudiant() {
        return Etudiant.builder()
                .idEtudiant(1L)
                .nomEt("John")
                .prenomEt("Doe")
                .cin(12345678L)
                .ecole("ESPRIT")
                .dateNaissance(LocalDate.of(2000, 1, 1))
                .build();
    }
}
