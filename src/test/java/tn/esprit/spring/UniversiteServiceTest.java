package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.Services.Universite.IUniversiteService;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UniversiteServiceTest {

    @Autowired
    IUniversiteService universiteService;

    private static Long savedUniversiteId;


    @AfterAll
    public void afterAll(){
        universiteService.deleteById(savedUniversiteId);
        Universite u = universiteService.findById(savedUniversiteId);
        System.out.println(u.toString());
        Assertions.assertTrue( u.getNomUniversite().equals("mouch maoujoud"));
    }

    @Test
    @Order(1)
    public void testAddOrUpdate() {
        Universite u = Universite.builder()
                .nomUniversite("ESPRIT")
                .adresse("Ariana")
                .build();
        System.out.println("object   "+u);
        Universite saved = universiteService.addOrUpdate(u);
        System.out.println("saaaaaaved   "+saved);
        savedUniversiteId = saved.getIdUniversite();

        Assertions.assertNotNull(saved.getIdUniversite());
        Assertions.assertEquals("ESPRIT", saved.getNomUniversite());
    }

    @Test
    @Order(2)
    public void testFindAll() {
        List<Universite> list = universiteService.findAll();
        Assertions.assertFalse(list.isEmpty());
    }

    @Test
    @Order(3)
    public void testFindById() {
        Universite u = universiteService.findById(savedUniversiteId);
        Assertions.assertEquals("ESPRIT", u.getNomUniversite());
    }

}
