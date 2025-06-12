package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class MockBlocServiceTest {

    @Mock
    private BlocRepository blocRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @InjectMocks
    private BlocService blocService;

    private static final Long TEST_BLOC_ID = 1L;

    @Test
    @Order(1)
    public void testAddOrUpdate() {
        // Given
        Bloc input = Bloc.builder()
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        Bloc saved = Bloc.builder()
                .idBloc(TEST_BLOC_ID)
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        // When
        when(blocRepository.save(any(Bloc.class))).thenReturn(saved);

        // Then
        Bloc result = blocService.addOrUpdate(input);

        Assertions.assertNotNull(result.getIdBloc());
        Assertions.assertEquals("Bloc A", result.getNomBloc());
        Assertions.assertEquals(100, result.getCapaciteBloc());

        verify(blocRepository).save(any(Bloc.class));
    }

    @Test
    @Order(2)
    public void testFindAll() {
        // Given
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

        // When
        when(blocRepository.findAll()).thenReturn(expectedList);

        // Then
        List<Bloc> result = blocService.findAll();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Bloc A", result.get(0).getNomBloc());
        Assertions.assertEquals("Bloc B", result.get(1).getNomBloc());

        verify(blocRepository).findAll();
    }

    @Test
    @Order(3)
    public void testFindById() {
        // Given
        Bloc expected = Bloc.builder()
                .idBloc(TEST_BLOC_ID)
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        // When
        when(blocRepository.findById(TEST_BLOC_ID)).thenReturn(Optional.of(expected));

        // Then
        Bloc result = blocService.findById(TEST_BLOC_ID);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Bloc A", result.getNomBloc());
        Assertions.assertEquals(TEST_BLOC_ID, result.getIdBloc());

        verify(blocRepository).findById(TEST_BLOC_ID);
    }

    @Test
    @Order(4)
    public void testDeleteById() {
        // Given
        Bloc existingBloc = Bloc.builder()
                .idBloc(TEST_BLOC_ID)
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        // When
        when(blocRepository.findById(TEST_BLOC_ID)).thenReturn(Optional.of(existingBloc));
        doNothing().when(chambreRepository).deleteAll(any());
        doNothing().when(blocRepository).deleteById(TEST_BLOC_ID);

        // Then
        blocService.deleteById(TEST_BLOC_ID);

        verify(blocRepository).findById(TEST_BLOC_ID);
        verify(chambreRepository).deleteAll(any());
        verify(blocRepository).deleteById(TEST_BLOC_ID);
    }

    @Test
    @Order(5)
    public void testAfterAllCleanup() {
        // This test just verifies that the test suite runs successfully
        Assertions.assertTrue(true);
    }
}