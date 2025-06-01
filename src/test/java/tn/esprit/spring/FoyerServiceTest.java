package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.DAO.Repositories.UniversiteRepository;
import tn.esprit.spring.Services.Foyer.IFoyerService;
import tn.esprit.spring.Services.Universite.IUniversiteService;
import org.junit.jupiter.api.TestMethodOrder;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FoyerServiceTest {
    @Autowired
    UniversiteRepository universiteRepository;

    @Autowired
    FoyerRepository foyerRepository;
    @Autowired
    IFoyerService foyerService;

    @Autowired
    IUniversiteService universiteService;

    private Long foyerId;
    private Long universiteId;

    @BeforeAll
    void init() {
        // Créer une université pour le test
        Universite u = Universite.builder()
                .nomUniversite("JUnit Université")
                .adresse("Tunis")
                .build();

        Universite saved = universiteService.addOrUpdate(u);
        universiteId = saved.getIdUniversite();
    }

    @Test
    @Order(1)
    void testAjouterFoyerEtAffecterAUniversite() {
        // Création de blocs
        Bloc b1 = Bloc.builder().nomBloc("Bloc A").capaciteBloc(10).build();
        Bloc b2 = Bloc.builder().nomBloc ("Bloc B").capaciteBloc(20).build();
        List<Bloc> blocs = new ArrayList<>();
        blocs.add(b1);
        blocs.add(b2);

        // Création du foyer
        Foyer f = Foyer.builder()
                .nomFoyer("Foyer JUnit")
                .capaciteFoyer(100L)
                .blocs(blocs)
                .build();

        // Appel de la méthode à tester
        Foyer result = foyerService.ajouterFoyerEtAffecterAUniversite(f, universiteId);
        foyerId = result.getIdFoyer();

        // Vérifications
        assertNotNull(result);
        assertEquals("Foyer JUnit", result.getNomFoyer());
        assertEquals(2, result.getBlocs().size());

        Universite updatedU = universiteService.findById(universiteId);
        assertNotNull(updatedU.getFoyer());
        assertEquals(result.getIdFoyer(), updatedU.getFoyer().getIdFoyer());
    }

    @AfterAll
    void cleanup() {
        if (foyerId != null) {
            foyerService.deleteById(foyerId); // supprime aussi les blocs associés
        }
        if (universiteId != null) {
            universiteService.deleteById(universiteId);
        }
    }

    @Test
    void testInjection() {
        assertNotNull(universiteService, "universiteService is not injected!");
    }

    @Test
    void testAddUniversiteSimple() {
        Universite u = Universite.builder().nomUniversite("X").adresse("Y").build();
        Universite result = universiteService.addOrUpdate(u);
        System.out.println("Résultat: " + result);
    }
}
