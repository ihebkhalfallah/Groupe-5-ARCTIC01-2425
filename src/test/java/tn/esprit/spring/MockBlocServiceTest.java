package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.Services.Bloc.BlocService;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class MockBlocServiceTest {

    @Mock
    private BlocRepository blocRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @InjectMocks
    private BlocService blocService;

    private static Long savedBlocId = 1L;

    @Test
    @Order(1)
    public void testAddOrUpdate() {
        Bloc input = Bloc.builder()
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        Bloc saved = Bloc.builder()
                .idBloc(savedBlocId)
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        when(blocRepository.save(any(Bloc.class))).thenReturn(saved);

        Bloc result = blocService.addOrUpdate(input);

        Assertions.assertNotNull(result.getIdBloc());
        Assertions.assertEquals("Bloc A", result.getNomBloc());

        verify(blocRepository).save(any(Bloc.class));
    }

    @Test
    @Order(2)
    public void testFindAll() {
        Bloc b1 = Bloc.builder()
                .idBloc(1L)
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        Bloc b2 = Bloc.builder()
                .idBloc(2L)
                .nomBloc("Bloc B")
                .capaciteBloc(150)
                .chambres(new ArrayList<>())
                .build();

        List<Bloc> expectedList = Arrays.asList(b1, b2);

        when(blocRepository.findAll()).thenReturn(expectedList);

        List<Bloc> result = blocService.findAll();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Bloc A", result.get(0).getNomBloc());
        Assertions.assertEquals("Bloc B", result.get(1).getNomBloc());

        verify(blocRepository).findAll();
    }

    @Test
    @Order(3)
    public void testFindById() {
        Bloc expected = Bloc.builder()
                .idBloc(savedBlocId)
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        when(blocRepository.findById(anyLong())).thenReturn(Optional.of(expected));

        Bloc result = blocService.findById(savedBlocId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Bloc A", result.getNomBloc());
        Assertions.assertEquals(savedBlocId, result.getIdBloc());

        verify(blocRepository).findById(anyLong());
    }

    @Test
    @Order(4)
    public void testDeleteById() {
        // Mock de la méthode findById pour vérifier l'existence avant suppression
        Bloc existingBloc = Bloc.builder()
                .idBloc(savedBlocId)
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        when(blocRepository.findById(anyLong())).thenReturn(Optional.of(existingBloc));
        doNothing().when(chambreRepository).deleteAll(any()); // Mock pour ChambreRepository
        doNothing().when(blocRepository).deleteById(anyLong());

        blocService.deleteById(savedBlocId);

        verify(blocRepository).findById(anyLong());
        verify(chambreRepository).deleteAll(any());
        verify(blocRepository).deleteById(anyLong());
    }

    @Test
    @Order(5)
    public void testAfterAllCleanup() {
        Assertions.assertTrue(true);
    }
}