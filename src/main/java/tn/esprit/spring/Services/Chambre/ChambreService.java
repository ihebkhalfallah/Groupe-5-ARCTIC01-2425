package tn.esprit.spring.Services.Chambre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ChambreService implements IChambreService {
    private final ChambreRepository chambreRepository;
    ChambreRepository repo;
    BlocRepository blocRepository;

    @Override
    public Chambre addOrUpdate(Chambre c) {
        return repo.save(c);
    }

    @Override
    public List<Chambre> findAll() {
        return repo.findAll();
    }

    @Override
    public Chambre findById(long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Chambre c) {
        repo.delete(c);
    }

    @Override
    public List<Chambre> getChambresParNomBloc(String nomBloc) {
        return repo.findByBlocNomBloc(nomBloc);
    }

    @Override
    public long nbChambreParTypeEtBloc(TypeChambre type, long idBloc) {
        long compteur = 0;
        List<Chambre> list = chambreRepository.findAll();
        for (Chambre chambre : list) {
            if (chambre.getBloc().getIdBloc() == idBloc
                    && chambre.getTypeC().equals(type)) {
                compteur++;
            }
        }
        return compteur;
    }

    @Override
    public List<Chambre> getChambresNonReserveParNomFoyerEtTypeChambre(String nomFoyer, TypeChambre type) {
        LocalDate[] auDates = getCurrentAcademicYearRange();
        LocalDate dateDebutAU = auDates[0];
        LocalDate dateFinAU = auDates[1];

        return repo.findAll().stream()
                .filter(c -> c.getTypeC().equals(type)
                        && c.getBloc().getFoyer().getNomFoyer().equals(nomFoyer))
                .filter(c -> isChambreDisponible(c, dateDebutAU, dateFinAU))
                .collect(Collectors.toList());
    }

    private LocalDate[] getCurrentAcademicYearRange() {
        int year = LocalDate.now().getYear() % 100;
        LocalDate dateDebutAU;
        LocalDate dateFinAU;

        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(2000 + (year - 1), 9, 15);
            dateFinAU = LocalDate.of(2000 + year, 6, 30);
        } else {
            dateDebutAU = LocalDate.of(2000 + year, 9, 15);
            dateFinAU = LocalDate.of(2000 + (year + 1), 6, 30);
        }

        return new LocalDate[]{dateDebutAU, dateFinAU};
    }

    private boolean isChambreDisponible(Chambre c, LocalDate dateDebutAU, LocalDate dateFinAU) {
        long count = c.getReservations().stream()
                .filter(r -> r.getAnneeUniversitaire().isAfter(dateDebutAU)
                        && r.getAnneeUniversitaire().isBefore(dateFinAU))
                .count();

        int maxAllowed = switch (c.getTypeC()) {
            case SIMPLE -> 1;
            case DOUBLE -> 2;
            case TRIPLE -> 3;
        };

        return count < maxAllowed;
    }

    @Scheduled(cron = "0 * * * * *")
    public void listeChambresParBloc() {
        for (Bloc b : blocRepository.findAll()) {
            log.info("Bloc => " + b.getNomBloc() +
                    " ayant une capacité " + b.getCapaciteBloc());
            if (!b.getChambres().isEmpty()) {
                log.info("La liste des chambres pour ce bloc: ");
                for (Chambre c : b.getChambres()) {
                    log.info("NumChambre: " + c.getNumeroChambre() +
                            " type: " + c.getTypeC());
                }
            } else {
                log.info("Pas de chambre disponible dans ce bloc");
            }
            log.info("********************");
        }
    }

    @Override
    public void pourcentageChambreParTypeChambre() {
        long totalChambre = repo.count();
        double pSimple = (double) (repo.countChambreByTypeC(TypeChambre.SIMPLE) * 100) / totalChambre;
        double pDouble = (double) (repo.countChambreByTypeC(TypeChambre.DOUBLE) * 100) / totalChambre;
        double pTriple = (double) (repo.countChambreByTypeC(TypeChambre.TRIPLE) * 100) / totalChambre;
        log.info("Nombre total des chambre: " + totalChambre);
        log.info("Le pourcentage des chambres pour le type SIMPLE est égale à " + pSimple);
        log.info("Le pourcentage des chambres pour le type DOUBLE est égale à " + pDouble);
        log.info("Le pourcentage des chambres pour le type TRIPLE est égale à " + pTriple);

    }

    @Override
    public void nbPlacesDisponibleParChambreAnneeEnCours() {
        final String messagePlaceDispo = "Le nombre de place disponible pour la chambre ";
        final String messageChambre = "La chambre ";
        final String messageEstComplete =" est complete";

        // Début "récuperer l'année universitaire actuelle"
        LocalDate dateDebutAU;
        LocalDate dateFinAU;
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        } else {
            dateDebutAU = LocalDate.of(Integer.parseInt("20" + year), 9, 15);
            dateFinAU = LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
        }
        // Fin "récuperer l'année universitaire actuelle"
        for (Chambre c : repo.findAll()) {
            long nbReservation = repo.countReservationsByIdChambreAndReservationsEstValideAndReservationsAnneeUniversitaireBetween(c.getIdChambre()
                    , true, dateDebutAU, dateFinAU);
            switch (c.getTypeC()) {
                case SIMPLE:
                    if (nbReservation == 0) {
                        log.info(messagePlaceDispo + c.getTypeC() + " " + c.getNumeroChambre() + " est 1 ");
                    } else {
                        log.info(messageChambre + c.getTypeC() + " " + c.getNumeroChambre() + messageEstComplete);
                    }
                    break;
                case DOUBLE:
                    if (nbReservation < 2) {
                        log.info(messagePlaceDispo + c.getTypeC() + " " + c.getNumeroChambre() + " est " + (2 - nbReservation));
                    } else {
                        log.info(messageChambre + c.getTypeC() + " " + c.getNumeroChambre() + messageEstComplete);
                    }
                    break;
                case TRIPLE:
                    if (nbReservation < 3) {
                        log.info(messagePlaceDispo + c.getTypeC() + " " + c.getNumeroChambre() + " est " + (3 - nbReservation));
                    } else {
                        log.info(messageChambre + c.getTypeC() + " " + c.getNumeroChambre() + messageEstComplete);
                    }
            }
        }
    }

    @Override
    public List<Chambre> getChambresParNomBlocJava(String nomBloc) {
        Bloc b = blocRepository.findByNomBloc(nomBloc);
        return b.getChambres();
    }

    @Override
    public List<Chambre> getChambresParNomBlocKeyWord(String nomBloc) {
        return repo.findByBlocNomBloc(nomBloc);
    }

    @Override
    public List<Chambre> getChambresParNomBlocJPQL(String nomBloc) {
        return repo.getChambresParNomBlocJPQL(nomBloc);
    }

    @Override
    public List<Chambre> getChambresParNomBlocSQL(String nomBloc) {
        return repo.getChambresParNomBlocSQL(nomBloc);
    }
}
