package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.Services.Chambre.ChambreService;
import tn.esprit.spring.Services.Chambre.IChambreService;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChambreServiceTest {

    @Autowired
    private IChambreService chambreService;

    private static Chambre testChambre;

    @BeforeAll
    static void setUp() {
        testChambre = Chambre.builder()
                .numeroChambre(123L)
                .typeC(TypeChambre.DOUBLE)
                .build();
    }

    @Test
    @Order(1)
    void testAddOrUpdate() {
        // Act
        Chambre savedChambre = chambreService.addOrUpdate(testChambre);

        // Assert
        Assertions.assertNotNull(savedChambre, "Saved chambre should not be null");
        Assertions.assertNotNull(savedChambre.getIdChambre(), "Chambre ID should be generated");
        Assertions.assertEquals(testChambre.getNumeroChambre(), savedChambre.getNumeroChambre(), "NumeroChambre should match");
        Assertions.assertEquals(testChambre.getTypeC(), savedChambre.getTypeC(), "TypeChambre should match");

        // Update testChambre with the saved instance
        testChambre = savedChambre;
    }

    @Test
    @Order(2)
    void testFindById() {
        // Act
        Chambre foundChambre = chambreService.findById(testChambre.getIdChambre());

        // Assert
        Assertions.assertNotNull(foundChambre, "Chambre should be found");
        Assertions.assertEquals(testChambre.getIdChambre(), foundChambre.getIdChambre(), "Chambre IDs should match");
    }

    @Test
    @Order(3)
    void testDelete() {
        // Act
        chambreService.delete(testChambre);

        // Verify
        Assertions.assertThrows(Exception.class, () -> {
            chambreService.findById(testChambre.getIdChambre());
        }, "Chambre should be deleted");
    }
}