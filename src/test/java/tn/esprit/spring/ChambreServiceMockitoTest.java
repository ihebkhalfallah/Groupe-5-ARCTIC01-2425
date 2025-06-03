package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.Services.Chambre.ChambreService;
import tn.esprit.spring.Services.Chambre.IChambreService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChambreServiceMockitoTest {

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private BlocRepository blocRepository;

    @InjectMocks
    private ChambreService chambreService;

    private Chambre chambre;
    private Bloc bloc;

    @BeforeEach
    void setUp() {
        bloc = Bloc.builder()
                .idBloc(1L)
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .build();

        chambre = Chambre.builder()
                .idChambre(1L)
                .numeroChambre(101L)
                .typeC(TypeChambre.DOUBLE)
                .bloc(bloc)
                .build();
    }
    @Order(1)
    @Test
    void whenAddChambre_thenReturnSavedChambre() {
        // Arrange
        when(chambreRepository.save(any(Chambre.class))).thenReturn(chambre);

        // Act
        Chambre saved = chambreService.addOrUpdate(chambre);

        // Assert
        assertNotNull(saved);
        assertEquals(101L, saved.getNumeroChambre());
        verify(chambreRepository).save(chambre);
    }

    @Order(2)
    @Test
    void whenFindById_thenReturnChambre() {
        // Arrange
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));

        // Act
        Chambre found = chambreService.findById(1L);

        // Assert
        assertNotNull(found);
        assertEquals(101L, found.getNumeroChambre());
    }

    @Order(3)
    @Test
    void whenFindByIdNotFound_thenThrowException() {
        // Arrange
        when(chambreRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            chambreService.findById(999L);
        });
    }

    @Order(4)
    @Test
    void whenGetChambresByBlocName_thenReturnFilteredList() {
        // Arrange
        List<Chambre> mockChambres = Arrays.asList(
                chambre,
                Chambre.builder().numeroChambre(102L).typeC(TypeChambre.SIMPLE).bloc(bloc).build()
        );
        when(chambreRepository.findByBlocNomBloc("Bloc A")).thenReturn(mockChambres);

        // Act
        List<Chambre> result = chambreService.getChambresParNomBloc("Bloc A");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getBloc().getNomBloc().equals("Bloc A")));
    }

}