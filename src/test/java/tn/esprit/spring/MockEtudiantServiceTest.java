package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;
import tn.esprit.spring.Services.Etudiant.EtudiantService;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class MockEtudiantServiceTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private EtudiantService etudiantService;

    private static Long savedEtudiantId = 1L;

    @Test
    @Order(1)
    public void testAddOrUpdate() {
        Etudiant input = Etudiant.builder()
                .nomEt("John")
                .prenomEt("Doe")
                .cin(12345678L)
                .ecole("ESPRIT")
                .dateNaissance(LocalDate.of(2000, 1, 1))
                .build();

        Etudiant saved = Etudiant.builder()
                .idEtudiant(savedEtudiantId)
                .nomEt("John")
                .prenomEt("Doe")
                .cin(12345678L)
                .ecole("ESPRIT")
                .dateNaissance(LocalDate.of(2000, 1, 1))
                .build();

        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(saved);

        Etudiant result = etudiantService.addOrUpdate(input);

        Assertions.assertNotNull(result.getIdEtudiant());
        Assertions.assertEquals("John", result.getNomEt());

        verify(etudiantRepository).save(any(Etudiant.class));
    }

    @Test
    @Order(2)
    public void testFindAll() {
        Etudiant e1 = Etudiant.builder()
                .idEtudiant(1L)
                .nomEt("John")
                .prenomEt("Doe")
                .cin(12345678L)
                .ecole("ESPRIT")
                .dateNaissance(LocalDate.of(2000, 1, 1))
                .build();

        Etudiant e2 = Etudiant.builder()
                .idEtudiant(2L)
                .nomEt("Jane")
                .prenomEt("Smith")
                .cin(87654321L)
                .ecole("ENIT")
                .dateNaissance(LocalDate.of(1999, 6, 15))
                .build();

        List<Etudiant> expectedList = Arrays.asList(e1, e2);

        when(etudiantRepository.findAll()).thenReturn(expectedList);

        List<Etudiant> result = etudiantService.findAll();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("John", result.get(0).getNomEt());
        Assertions.assertEquals("Jane", result.get(1).getNomEt());

        verify(etudiantRepository).findAll();
    }

    @Test
    @Order(3)
    public void testFindById() {
        Etudiant expected = Etudiant.builder()
                .idEtudiant(savedEtudiantId)
                .nomEt("John")
                .prenomEt("Doe")
                .cin(12345678L)
                .ecole("ESPRIT")
                .dateNaissance(LocalDate.of(2000, 1, 1))
                .build();

        when(etudiantRepository.findById(anyLong())).thenReturn(Optional.of(expected));

        Etudiant result = etudiantService.findById(savedEtudiantId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("John", result.getNomEt());
        Assertions.assertEquals(savedEtudiantId, result.getIdEtudiant());

        verify(etudiantRepository).findById(anyLong());
    }

    @Test
    @Order(4)
    public void testDeleteById() {
        doNothing().when(etudiantRepository).deleteById(anyLong());

        etudiantService.deleteById(savedEtudiantId);
        verify(etudiantRepository).deleteById(anyLong());
    }
}
