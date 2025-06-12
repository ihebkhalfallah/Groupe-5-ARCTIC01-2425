package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.Services.Bloc.IBlocService;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BlocServiceTest {

    @Autowired
    IBlocService blocService;

    private static Long savedBlocId;

    @AfterAll
    public void afterAll(){
        if (savedBlocId != null) {
            blocService.deleteById(savedBlocId);

            // Vérifier que l'entité a été supprimée
            try {
                Bloc b = blocService.findById(savedBlocId);
                // Si on arrive ici, l'entité n'a pas été supprimée
                Assertions.fail("Le bloc devrait être supprimé");
            } catch (Exception e) {
                // Exception attendue car l'entité n'existe plus
                System.out.println("Bloc supprimé avec succès");
            }
        }
    }

    @Test
    @Order(1)
    public void testAddOrUpdate() {
        Bloc b = Bloc.builder()
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .build();

        System.out.println("object: " + b);
        Bloc saved = blocService.addOrUpdate(b);
        System.out.println("saved: " + saved);
        savedBlocId = saved.getIdBloc();

        Assertions.assertNotNull(saved.getIdBloc());
        Assertions.assertEquals("Bloc A", saved.getNomBloc());
    }

    @Test
    @Order(2)
    public void testFindAll() {
        List<Bloc> list = blocService.findAll();
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    @Order(3)
    public void testFindById() {
        Bloc b = blocService.findById(savedBlocId);
        Assertions.assertEquals("Bloc A", b.getNomBloc());
    }
}