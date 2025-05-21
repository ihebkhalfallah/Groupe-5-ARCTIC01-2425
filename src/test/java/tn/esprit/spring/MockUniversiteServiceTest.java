package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.DAO.Repositories.UniversiteRepository;
import tn.esprit.spring.Services.Universite.UniversiteService;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class MockUniversiteServiceTest {

    @Mock
    private UniversiteRepository universiteRepository;

    @InjectMocks
    private UniversiteService universiteService;

    private static Long savedUniversiteId = 1L;

    @Test
    @Order(1)
    public void testAddOrUpdate() {
        Universite input = Universite.builder()
                .nomUniversite("ESPRIT")
                .adresse("Ariana")
                .build();

        Universite saved = Universite.builder()
                .idUniversite(savedUniversiteId)
                .nomUniversite("ESPRIT")
                .adresse("Ariana")
                .build();

        when(universiteRepository.save(any(Universite.class))).thenReturn(saved);

        Universite result = universiteService.addOrUpdate(input);

        Assertions.assertNotNull(result.getIdUniversite());
        Assertions.assertEquals("ESPRIT", result.getNomUniversite());

        verify(universiteRepository).save(any(Universite.class));
    }

    @Test
    @Order(2)
    public void testFindAll() {
        Universite u1 = Universite.builder()
                .idUniversite(1L)
                .nomUniversite("ESPRIT")
                .adresse("Ariana")
                .build();

        Universite u2 = Universite.builder()
                .idUniversite(2L)
                .nomUniversite("ENIT")
                .adresse("Tunis")
                .build();

        List<Universite> expectedList = Arrays.asList(u1, u2);

        when(universiteRepository.findAll()).thenReturn(expectedList);

        List<Universite> result = universiteService.findAll();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("ESPRIT", result.get(0).getNomUniversite());
        Assertions.assertEquals("ENIT", result.get(1).getNomUniversite());

        verify(universiteRepository).findAll();
    }

    @Test
    @Order(3)
    public void testFindById() {
        Universite expected = Universite.builder()
                .idUniversite(savedUniversiteId)
                .nomUniversite("ESPRIT")
                .adresse("Ariana")
                .build();

        when(universiteRepository.findById(anyLong())).thenReturn(Optional.of(expected));

        Universite result = universiteService.findById(savedUniversiteId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ESPRIT", result.getNomUniversite());
        Assertions.assertEquals(savedUniversiteId, result.getIdUniversite());

        verify(universiteRepository).findById(anyLong());
    }

    @Test
    @Order(4)
    public void testDeleteById() {

        doNothing().when(universiteRepository).deleteById(anyLong());

        universiteService.deleteById(savedUniversiteId);
        verify(universiteRepository).deleteById(anyLong());
    }

    @Test
    @Order(5)
    public void testAfterAllCleanup() {
        Assertions.assertTrue(true);
    }
}