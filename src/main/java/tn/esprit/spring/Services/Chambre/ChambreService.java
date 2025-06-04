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
    private final BlocRepository blocRepository;

    @Override
    public Chambre addOrUpdate(Chambre c) {
        return chambreRepository.save(c);
    }

    @Override
    public List<Chambre> findAll() {
        return chambreRepository.findAll();
    }

    @Override
    public Chambre findById(long id) {
        return chambreRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(long id) {
        chambreRepository.deleteById(id);
    }

    @Override
    public void delete(Chambre c) {
        chambreRepository.delete(c);
    }

    @Override
    public List<Chambre> getChambresParNomBloc(String nomBloc) {
        return chambreRepository.findByBlocNomBloc(nomBloc);
    }

    @Override
    public long nbChambreParTypeEtBloc(TypeChambre type, long idBloc) {
        long compteur = 0;
        List<Chambre> list = chambreRepository.findAll();
        for (Chambre chambre : list) {
            if (chambre.getBloc().getIdBloc() == idBloc && chambre.getTypeC().equals(type)) {
                compteur++;
            }
        }
        return compteur;
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
            log.info("Bloc => " + b.getNomBloc() + " ayant une capacité " + b.getCapaciteBloc());
            if (!b.getChambres().isEmpty()) {
                log.info("La liste des chambres pour ce bloc:");
                for (Chambre c : b.getChambres()) {
                    log.info("NumChambre: " + c.getNumeroChambre() + " type: " + c.getTypeC());
                }
            } else {
                log.info("Pas de chambre disponible dans ce bloc");
            }
            log.info("********************");
        }
    }

    @Override
    public void pourcentageChambreParTypeChambre() {
        long totalChambre = chambreRepository.count();
        if (totalChambre == 0) {
            log.warn("Aucune chambre enregistrée.");
            return;
        }
        double pSimple = (double) chambreRepository.countChambreByTypeC(TypeChambre.SIMPLE) * 100 / totalChambre;
        double pDouble = (double) chambreRepository.countChambreByTypeC(TypeChambre.DOUBLE) * 100 / totalChambre;
        double pTriple = (double) chambreRepository.countChambreByTypeC(TypeChambre.TRIPLE) * 100 / totalChambre;

        log.info("Nombre total des chambres: " + totalChambre);
        log.info("Pourcentage SIMPLE: " + pSimple + "%");
        log.info("Pourcentage DOUBLE: " + pDouble + "%");
        log.info("Pourcentage TRIPLE: " + pTriple + "%");
    }

    @Override
    public void nbPlacesDisponibleParChambreAnneeEnCours() {
        LocalDate dateDebutAU;
        LocalDate dateFinAU;
        int year = LocalDate.now().getYear() % 100;

        if (LocalDate.now().getMonthValue() <= 7) {
            dateDebutAU = LocalDate.of(2000 + (year - 1), 9, 15);
            dateFinAU = LocalDate.of(2000 + year, 6, 30);
        } else {
            dateDebutAU = LocalDate.of(2000 + year, 9, 15);
            dateFinAU = LocalDate.of(2000 + (year + 1), 6, 30);
        }


    @Override
    public List<Chambre> getChambresParNomBlocJava(String nomBloc) {
        Bloc bloc = blocRepository.findByNomBloc(nomBloc);
        return bloc != null ? bloc.getChambres() : new ArrayList<>();
    }

    @Override
    public List<Chambre> getChambresParNomBlocKeyWord(String nomBloc) {
        return chambreRepository.findByBlocNomBloc(nomBloc);
    }

    @Override
    public List<Chambre> getChambresParNomBlocJPQL(String nomBloc) {
        return chambreRepository.getChambresParNomBlocJPQL(nomBloc);
    }

    @Override
    public List<Chambre> getChambresParNomBlocSQL(String nomBloc) {
        return chambreRepository.getChambresParNomBlocSQL(nomBloc);
    }
}
