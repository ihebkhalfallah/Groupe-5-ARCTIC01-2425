package tn.esprit.spring;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.Services.Foyer.FoyerService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoyerServiceMockTest {

    @Mock
    private FoyerRepository foyerRepository;

    @InjectMocks
    private FoyerService foyerService;

    public FoyerServiceMockTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOrUpdate() {
        Foyer foyer = new Foyer();
        foyer.setNomFoyer("Test Foyer");
        when(foyerRepository.save(foyer)).thenReturn(foyer);

        Foyer saved = foyerService.addOrUpdate(foyer);
        assertNotNull(saved);
        assertEquals("Test Foyer", saved.getNomFoyer());
    }

    @Test
    void testFindById() {
        Foyer foyer = new Foyer();
        foyer.setIdFoyer(1L);
        when(foyerRepository.findById(1L)).thenReturn(Optional.of(foyer));

        Foyer result = foyerService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getIdFoyer());
    }

    @Test
    void testFindAll() {
        when(foyerRepository.findAll()).thenReturn(Arrays.asList(new Foyer(), new Foyer()));
        List<Foyer> list = foyerService.findAll();
        assertEquals(2, list.size());
    }

    @Test
    void testDelete() {
        Foyer foyer = new Foyer();
        foyer.setIdFoyer(5L);
        foyerService.delete(foyer);
        verify(foyerRepository, times(1)).delete(foyer);
    }

    @Test
    void testDeleteById() {
        foyerService.deleteById(7L);
        verify(foyerRepository, times(1)).deleteById(7L);
    }
}
