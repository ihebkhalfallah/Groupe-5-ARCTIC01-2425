package tn.esprit.spring;

import org.junit.After;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;
import tn.esprit.spring.Services.Bloc.BlocService;

@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.class)
@SpringBootTest
public class BlocServiceTest {
    @Autowired
    BlocService blocService;

    @Autowired
    BlocRepository blocRepository;

    @Autowired
    ChambreRepository chambreRepository;

    @Autowired
    FoyerRepository foyerRepository;

    private Bloc testBloc;
    private Foyer testFoyer;

    @BeforeEach
    void beforeEach() {
        // Créer un foyer
        testFoyer = Foyer.builder()
                .nomFoyer("Foyer Central")
                .capaciteFoyer(100L)
                .build();
        foyerRepository.save(testFoyer);

        // Créer un bloc
        testBloc = Bloc.builder()
                .nomBloc("Bloc A")
                .capaciteBloc(30L)
                .foyer(testFoyer)
                .build();
        blocRepository.save(testBloc);

        // Ajouter des chambres
        Chambre ch1 = Chambre.builder()
                .numeroChambre(101)
                .bloc(testBloc)
                .build();

        Chambre ch2 = Chambre.builder()
                .numeroChambre(102)
                .bloc(testBloc)
                .build();

        chambreRepository.save(ch1);
        chambreRepository.save(ch2);
    }

    @AfterEach
    void afterEach() {
        chambreRepository.deleteAll();
        blocRepository.deleteAll();
        foyerRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testAddBloc() {
        Bloc bloc = Bloc.builder()
                .nomBloc("Bloc B")
                .capaciteBloc(40L)
                .foyer(testFoyer)
                .build();

        Bloc savedBloc = blocService.ajouterBlocEtSesChambres(bloc);
        Assertions.assertNotNull(savedBloc.getIdBloc());
        Assertions.assertEquals("Bloc B", savedBloc.getNomBloc());
    }

    @Test
    @Order(2)
    void testFindBlocById() {
        Bloc bloc = blocRepository.findById(testBloc.getIdBloc()).orElse(null);
        Assertions.assertNotNull(bloc);
        Assertions.assertEquals("Bloc A", bloc.getNomBloc());
    }

    @Test
    @Order(3)
    void testUpdateBloc() {
        testBloc.setNomBloc("Bloc A+");
        Bloc updated = blocService.addOrUpdate(testBloc);
        Assertions.assertEquals("Bloc A+", updated.getNomBloc());
    }

    @Test
    @Order(4)
    void testDeleteBloc() {
        blocService.deleteById(testBloc.getIdBloc());
        Assertions.assertFalse(blocRepository.findById(testBloc.getIdBloc()).isPresent());
    }


}