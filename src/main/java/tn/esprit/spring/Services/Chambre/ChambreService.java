package tn.esprit.spring.Services.Chambre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        return repo.findById(id).get();
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
        //return repo.countByTypeCAndBlocIdBloc(type, idBloc);
    }

    @Override
    public List<Chambre> getChambresNonReserveParNomFoyerEtTypeChambre(String nomFoyer, TypeChambre type) {
        LocalDate dateDebutAU = calculerDateDebutAnneeUniversitaire();
        LocalDate dateFinAU = calculerDateFinAnneeUniversitaire();

        List<Chambre> listChambreDispo = new ArrayList<>();

        for (Chambre chambre : chambreRepository.findAll()) {
            if (estChambreCorrespondante(chambre, nomFoyer, type)) {
                int nombreReservations = compterReservationsDansAnneeUniversitaire(chambre, dateDebutAU, dateFinAU);
                if (estChambreDisponible(nombreReservations, type)) {
                    listChambreDispo.add(chambre);
                }
            }
        }

        return listChambreDispo;
    }

    private LocalDate calculerDateDebutAnneeUniversitaire() {
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            return LocalDate.of(2000 + (year - 1), 9, 15);
        } else {
            return LocalDate.of(2000 + year, 9, 15);
        }
    }

    private LocalDate calculerDateFinAnneeUniversitaire() {
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            return LocalDate.of(2000 + year, 6, 30);
        } else {
            return LocalDate.of(2000 + (year + 1), 6, 30);
        }
    }

    private boolean estChambreCorrespondante(Chambre chambre, String nomFoyer, TypeChambre type) {
        return chambre.getTypeC().equals(type)
                && chambre.getBloc().getFoyer().getNomFoyer().equals(nomFoyer);
    }

    private int compterReservationsDansAnneeUniversitaire(Chambre chambre, LocalDate debut, LocalDate fin) {
        int count = 0;
        for (Reservation reservation : chambre.getReservations()) {
            if (reservation.getAnneeUniversitaire().isAfter(debut)
                    && reservation.getAnneeUniversitaire().isBefore(fin)) {
                count++;
            }
        }
        return count;
    }

    private boolean estChambreDisponible(int nombreReservations, TypeChambre type) {
        return (type == TypeChambre.SIMPLE && nombreReservations == 0)
                || (type == TypeChambre.DOUBLE && nombreReservations < 2)
                || (type == TypeChambre.TRIPLE && nombreReservations < 3);
    }


    @Scheduled(cron = "0 * * * * *")
    public void listeChambresParBloc() {
        for (Bloc b : blocRepository.findAll()) {
            log.info("Bloc => " + b.getNomBloc() +
                    " ayant une capacité " + b.getCapaciteBloc());
            if (b.getChambres().size() != 0) {
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
        // Début "récuperer l'année universitaire actuelle"
        LocalDate dateDebutAU;
        LocalDate dateFinAU;
        int numReservation;
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
                        log.info("Le nombre de place disponible pour la chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est 1 ");
                    } else {
                        log.info("La chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est complete");
                    }
                    break;
                case DOUBLE:
                    if (nbReservation < 2) {
                        log.info("Le nombre de place disponible pour la chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est " + (2 - nbReservation));
                    } else {
                        log.info("La chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est complete");
                    }
                    break;
                case TRIPLE:
                    if (nbReservation < 3) {
                        log.info("Le nombre de place disponible pour la chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est " + (3 - nbReservation));
                    } else {
                        log.info("La chambre " + c.getTypeC() + " " + c.getNumeroChambre() + " est complete");
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
