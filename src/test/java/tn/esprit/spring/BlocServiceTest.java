package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.Services.Bloc.IBlocService;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BlocServiceTest {

    @Autowired
    IBlocService blocService;

    private Long savedBlocId;

    @AfterAll
    public void afterAll(){
        if (savedBlocId != null) {
            try {
                blocService.deleteById(savedBlocId);
                System.out.println("Bloc supprimé avec succès");
            } catch (Exception e) {
                System.out.println("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    @Test
    @Order(1)
    public void testAddOrUpdate() {
        Bloc b = Bloc.builder()
                .nomBloc("Bloc A")
                .capaciteBloc(100)
                .chambres(new ArrayList<>())
                .build();

        System.out.println("object: " + b);
        Bloc saved = blocService.addOrUpdate(b);
        System.out.println("saved: " + saved);
        savedBlocId = saved.getIdBloc();

        Assertions.assertNotNull(saved.getIdBloc());
        Assertions.assertEquals("Bloc A", saved.getNomBloc());
        Assertions.assertEquals(100, saved.getCapaciteBloc());
    }

    @Test
    @Order(2)
    public void testFindAll() {
        List<Bloc> list = blocService.findAll();
        Assertions.assertFalse(list.isEmpty());
        System.out.println("Found " + list.size() + " blocs");
    }

    @Test
    @Order(3)
    public void testFindById() {
        Assertions.assertNotNull(savedBlocId, "savedBlocId should not be null - testAddOrUpdate might have failed");

        Bloc b = blocService.findById(savedBlocId);
        Assertions.assertNotNull(b, "Bloc should be found");
        Assertions.assertEquals("Bloc A", b.getNomBloc());
        Assertions.assertEquals(savedBlocId, b.getIdBloc());
    }
}