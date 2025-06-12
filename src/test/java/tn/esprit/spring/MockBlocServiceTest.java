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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MockBlocServiceTest {

    @Mock
    private BlocRepository blocRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @InjectMocks
    private BlocService blocService;

    @Test
    public void testAddOrUpdate() {
        // Given
        Bloc input = Bloc.builder()
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        Bloc saved = Bloc.builder()
                .idBloc(1L)
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        when(blocRepository.save(any(Bloc.class))).thenReturn(saved);

        // When
        Bloc result = blocService.addOrUpdate(input);

        // Then
        Assertions.assertNotNull(result.getIdBloc());
        Assertions.assertEquals("Bloc A", result.getNomBloc());
        Assertions.assertEquals(100, result.getCapaciteBloc());
        verify(blocRepository).save(any(Bloc.class));
    }

    @Test
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
        when(blocRepository.findAll()).thenReturn(expectedList);

        // When
        List<Bloc> result = blocService.findAll();

        // Then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Bloc A", result.get(0).getNomBloc());
        Assertions.assertEquals("Bloc B", result.get(1).getNomBloc());
        verify(blocRepository).findAll();
    }

    @Test
    public void testFindById() {
        // Given
        Long testId = 1L;
        Bloc expected = Bloc.builder()
                .idBloc(testId)
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        when(blocRepository.findById(testId)).thenReturn(Optional.of(expected));

        // When
        Bloc result = blocService.findById(testId);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Bloc A", result.getNomBloc());
        Assertions.assertEquals(testId, result.getIdBloc());
        verify(blocRepository).findById(testId);
    }

    @Test
    public void testDeleteById() {
        // Given
        Long testId = 1L;
        Bloc existingBloc = Bloc.builder()
                .idBloc(testId)
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        when(blocRepository.findById(testId)).thenReturn(Optional.of(existingBloc));
        doNothing().when(chambreRepository).deleteAll(any());
        doNothing().when(blocRepository).delete(any(Bloc.class));

        // When
        blocService.deleteById(testId);

        // Then
        verify(blocRepository).findById(testId);
        verify(chambreRepository).deleteAll(any());
        verify(blocRepository).delete(any(Bloc.class));
    }
}