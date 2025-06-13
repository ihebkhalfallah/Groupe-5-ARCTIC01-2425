package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.Services.Reservation.IReservationService;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationServiceTest {

    @Autowired
    IReservationService reservationService;

    private static String savedUniversiteId;


    @AfterAll
    public void afterAll(){
        reservationService.deleteById(savedUniversiteId);
     //   Reservation r = reservationService.findById(savedUniversiteId);
    }

    @Test
    @Order(1)
    public void testAddOrUpdate() {
        Reservation u = Reservation.builder()
                .idReservation("Amine")
                .anneeUniversitaire(LocalDate.of(2025,5,10))
                .build();
        Reservation saved = reservationService.addOrUpdate(u);
        System.out.println("saaaaaaved   "+saved);
        savedUniversiteId = saved.getIdReservation();

        Assertions.assertNotNull(saved.getIdReservation());
        Assertions.assertEquals("Amine", saved.getIdReservation());
    }

    @Test
    @Order(2)
    public void testFindAll() {
        List<Reservation> list = reservationService.findAll();
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    @Order(3)
    public void testFindById() {
        Reservation u = reservationService.findById(savedUniversiteId);
        Assertions.assertEquals("Amine", u.getIdReservation());
    }

}