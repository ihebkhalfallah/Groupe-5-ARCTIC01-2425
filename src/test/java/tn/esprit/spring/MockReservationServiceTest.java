package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Reservation.ReservationService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class MockReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private static String savedReservationId = "1";

    @Test
    @Order(1)
    public void testAddOrUpdate() {
        Reservation input = Reservation.builder()
                .idReservation("Amine")
                .anneeUniversitaire(LocalDate.of(2025,5,10))
                .build();

        Reservation saved = Reservation.builder()
                .idReservation(savedReservationId)
                .idReservation("Amine")
                .anneeUniversitaire(LocalDate.of(2025,5,10))
                .build();

        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        Reservation result = reservationService.addOrUpdate(input);

        Assertions.assertNotNull(result.getIdReservation());
        Assertions.assertEquals("Amine", result.getIdReservation());

        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @Order(2)
    public void testFindAll() {
        Reservation u1 = Reservation.builder()
                .idReservation("1")
                .anneeUniversitaire(LocalDate.of(2025,5,10))
                .estValide(true)
                .build();

        Reservation u2 = Reservation.builder()
                .idReservation("2")
                .anneeUniversitaire(LocalDate.of(2025,4,2))
                .estValide(false)
                .build();

        List<Reservation> expectedList = Arrays.asList(u1, u2);

        when(reservationRepository.findAll()).thenReturn(expectedList);

        List<Reservation> result = reservationService.findAll();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("1", result.get(0).getIdReservation());
        Assertions.assertEquals("2", result.get(1).getIdReservation());

        verify(reservationRepository).findAll();
    }

    @Test
    @Order(3)
    public void testFindById() {
        Reservation expected = Reservation.builder()
                .idReservation(savedReservationId)
                .anneeUniversitaire(LocalDate.of(2025,5,10))
                .estValide(true)
                .build();

        when(reservationRepository.findById(anyString())).thenReturn(Optional.of(expected));

        Reservation result = reservationService.findById(savedReservationId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("1", result.getIdReservation());
        Assertions.assertEquals(savedReservationId, result.getIdReservation());

        verify(reservationRepository).findById(anyString());
    }

    @Test
    @Order(4)
    public void testDeleteById() {

        doNothing().when(reservationRepository).deleteById(anyString());

        reservationService.deleteById(savedReservationId);
        verify(reservationRepository).deleteById(anyString());
    }

    @Test
    @Order(5)
    public void testAfterAllCleanup() {
        Assertions.assertTrue(true);
    }
}