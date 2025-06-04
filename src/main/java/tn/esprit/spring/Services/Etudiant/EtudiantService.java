package tn.esprit.spring.Services.Etudiant;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EtudiantService implements IEtudiantService {
    EtudiantRepository repo;
    ReservationRepository reservationRepository;

    @Override
    public Etudiant addOrUpdate(Etudiant e) {
        return repo.save(e);
    }

    @Override
    public List<Etudiant> findAll() {
        return repo.findAll();
    }

    @Override
    public Etudiant findById(long id) {
        return repo.findById(id).get();
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
    }

    @Override
    public void delete(Etudiant e) {
        repo.delete(e);
    }

    @Override
    public List<Etudiant> selectJPQL(String nom) {
        return repo.selectJPQL(nom);
    }

    @Override
    public void affecterReservationAEtudiant
            (String idR, String nomE, String prenomE) {
        // ManyToMany: Reservation(Child) -- Etudiant(Parent)
        // 1- Récupérer les objets
        Reservation res= reservationRepository.findById(idR).get();
        Etudiant et= repo.getByNomEtAndPrenomEt(nomE,prenomE);
        // 2- Affectation: On affecte le child au parent
        et.getReservations().add(res);
        // 3- Save du parent
        repo.save(et);
    }



    @Override
    public void desaffecterReservationAEtudiant(String idR, String nomE, String prenomE) {
        // ManyToMany: Reservation(Child) -- Etudiant(Parent)

        // 1- Récupérer les objets de façon sécurisée
        Optional<Reservation> optionalRes = reservationRepository.findById(idR);
        Etudiant et = repo.getByNomEtAndPrenomEt(nomE, prenomE);

        if (optionalRes.isPresent() && et != null) {
            Reservation res = optionalRes.get();

            // 2- Désaffectation : On enlève la réservation de la liste de l'étudiant
            et.getReservations().remove(res);

            // 3- Sauvegarde du parent
            repo.save(et);
        } else {
            System.out.println("Réservation ou étudiant introuvable");
        }
    }


}
