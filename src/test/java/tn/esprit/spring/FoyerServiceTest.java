package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.DAO.Repositories.UniversiteRepository;
import tn.esprit.spring.Services.Foyer.IFoyerService;
import tn.esprit.spring.Services.Universite.IUniversiteService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FoyerServiceTest {

    @Autowired
    UniversiteRepository universiteRepository;

    @Autowired
    FoyerRepository foyerRepository;

    @Autowired
    BlocRepository blocRepository;

    @Autowired
    IFoyerService foyerService;

    @Autowired
    IUniversiteService universiteService;

    private Long foyerId;
    private Long universiteId;

    @BeforeAll
    void init() {
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
        Bloc b1 = Bloc.builder().nomBloc("Bloc A").capaciteBloc(10).build();
        Bloc b2 = Bloc.builder().nomBloc("Bloc B").capaciteBloc(20).build();
        List<Bloc> blocs = new ArrayList<>();
        blocs.add(b1);
        blocs.add(b2);

        Foyer f = Foyer.builder()
                .nomFoyer("Foyer JUnit")
                .capaciteFoyer(100L)
                .blocs(blocs)
                .build();

        Foyer result = foyerService.ajouterFoyerEtAffecterAUniversite(f, universiteId);
        foyerId = result.getIdFoyer();

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
            foyerRepository.findById(foyerId).ifPresent(foyer -> {
                List<Bloc> blocs = foyer.getBlocs();
                if (blocs != null && !blocs.isEmpty()) {
                    blocRepository.deleteAll(blocs);
                }
            });
            foyerRepository.deleteById(foyerId);
        }

        if (universiteId != null) {
            universiteService.deleteById(universiteId);
        }
    }

    @Test
    void testInjection() {
        assertNotNull(universiteService);
    }

    @Test
    void testAddUniversiteSimple() {

        Etudiant etudiant = new Etudiant();
        Universite u = Universite.builder()
                .nomUniversite("X")
                .adresse("Y")
                .build();

        Universite result = universiteService.addOrUpdate(u);

        assertNotNull(result);
        assertEquals(0L, etudiant.getIdEtudiant());
//        assertNotNull(result.getIdUniversite());
        assertEquals("X", result.getNomUniversite());
        assertEquals("Y", result.getAdresse());
    }


    @Test
    void testAssignNewFoyerToAnotherUniversity() {
        Universite u2 = Universite.builder()
                .nomUniversite("Université Test Unique")
                .adresse("Sousse")
                .build();
        Universite savedU2 = universiteService.addOrUpdate(u2);

        Bloc b = Bloc.builder().nomBloc("Bloc Test").capaciteBloc(30).build();
        List<Bloc> blocs = new ArrayList<>();
        blocs.add(b);

        Foyer f = Foyer.builder()
                .nomFoyer("Foyer Test Unique")
                .capaciteFoyer(120L)
                .blocs(blocs)
                .build();

        Foyer newFoyer = foyerService.ajouterFoyerEtAffecterAUniversite(f, savedU2.getIdUniversite());

        assertNotNull(newFoyer);
        assertEquals("Foyer Test Unique", newFoyer.getNomFoyer());

        Universite updated = universiteService.findById(savedU2.getIdUniversite());
        assertNotNull(updated.getFoyer());
        assertEquals(newFoyer.getIdFoyer(), updated.getFoyer().getIdFoyer());

        // cleanup
        foyerRepository.findById(newFoyer.getIdFoyer()).ifPresent(foyer -> {
            List<Bloc> blocsToDelete = foyer.getBlocs();
            if (blocsToDelete != null && !blocsToDelete.isEmpty()) {
                blocRepository.deleteAll(blocsToDelete);
            }
        });
        foyerRepository.deleteById(newFoyer.getIdFoyer());
        universiteService.deleteById(savedU2.getIdUniversite());
    }



    @Test
    void testAddFoyerWithoutBlocs() {
        Foyer f = Foyer.builder()
                .nomFoyer("Empty Blocs Foyer")
                .capaciteFoyer(50L)
                .blocs(new ArrayList<>()) // ← Ajoute cette ligne
                .build();

        Foyer saved = foyerService.addOrUpdate(f);
        assertNotNull(saved);

        List<Bloc> blocs = saved.getBlocs();
        assertTrue(blocs == null || blocs.isEmpty(), "Blocs should be null or empty");

        foyerRepository.deleteById(saved.getIdFoyer());
    }




}
