/* package tn.esprit.spring.Services.Bloc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BlocService implements IBlocService {
    BlocRepository repo;
    ChambreRepository chambreRepository;
    BlocRepository blocRepository;
    FoyerRepository foyerRepository;

    @Override
    public Bloc addOrUpdate2(Bloc b) { //Cascade
        List<Chambre> chambres = b.getChambres();
        for (Chambre c : chambres) {
            c.setBloc(b);
            chambreRepository.save(c);
        }
        return b;
    }

    @Override
    public Bloc addOrUpdate(Bloc b) {
        List<Chambre> chambres = b.getChambres();
        b = repo.save(b);
        for (Chambre chambre : chambres) {
            chambre.setBloc(b);
            chambreRepository.save(chambre);
        }
        return b;
    }

    @Override
    public List<Bloc> findAll() {
        return repo.findAll();
    }

    @Override
    public Bloc findById(long id) {
        return repo.findById(id).get();
    }

    @Override
    public void deleteById(long id) {
        Bloc b =repo.findById(id).get();
        chambreRepository.deleteAll(b.getChambres());
        repo.delete(b);
    }

    @Override
    public void delete(Bloc b) {
        chambreRepository.deleteAll(b.getChambres());
        repo.delete(b);
    }

    @Override
    public Bloc affecterChambresABloc(List<Long> numChambre, String nomBloc) {
        //1
        Bloc b = repo.findByNomBloc(nomBloc);
        List<Chambre> chambres = new ArrayList<>();
        for (Long nu : numChambre) {
            Chambre chambre = chambreRepository.findByNumeroChambre(nu);
            chambres.add(chambre);
        }
        // Keyword (2ème méthode)
        //2 Parent==>Chambre  Child==> Bloc
        for (Chambre cha : chambres) {
            //3 On affecte le child au parent
            cha.setBloc(b);
            //4 save du parent
            chambreRepository.save(cha);
        }
        return b;
    }

    @Override
    public Bloc affecterBlocAFoyer(String nomBloc, String nomFoyer) {
        Bloc b = blocRepository.findByNomBloc(nomBloc); //Parent
        Foyer f = foyerRepository.findByNomFoyer(nomFoyer); //Child
        //On affecte le child au parent
        b.setFoyer(f);
        return blocRepository.save(b);
    }

    @Override
    public Bloc ajouterBlocEtSesChambres(Bloc b) {
        // Activer l'option cascade au niveau parent
        for (Chambre c : b.getChambres()) {
            c.setBloc(b);
            chambreRepository.save(c);
        }
        return b;
    }

    @Override
    public Bloc ajouterBlocEtAffecterAFoyer(Bloc b, String nomFoyer) {
        // Foyer: child , Bloc: Parent
        Foyer f= foyerRepository.findByNomFoyer(nomFoyer);
        b.setFoyer(f);
        return blocRepository.save(b);
    }



}
*/
package tn.esprit.spring.Services.Bloc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.DAO.Entities.Bloc;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Foyer;
import tn.esprit.spring.DAO.Repositories.BlocRepository;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.FoyerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BlocService implements IBlocService {
    private final BlocRepository blocRepository;
    private final ChambreRepository chambreRepository;
    private final FoyerRepository foyerRepository;

    @Override
    public Bloc addOrUpdate2(Bloc b) { //Cascade non complet dans ta version
        List<Chambre> chambres = b.getChambres();
        for (Chambre c : chambres) {
            c.setBloc(b);
            chambreRepository.save(c);
        }
        return blocRepository.save(b);
    }

    @Override
    public Bloc addOrUpdate(Bloc b) {
        List<Chambre> chambres = b.getChambres();
        Bloc savedBloc = blocRepository.save(b);
        for (Chambre chambre : chambres) {
            chambre.setBloc(savedBloc);
            chambreRepository.save(chambre);
        }
        return savedBloc;
    }

    @Override
    public List<Bloc> findAll() {
        return blocRepository.findAll();
    }

    @Override
    public Bloc findById(long id) {
        return blocRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(long id) {
        Bloc b = blocRepository.findById(id).orElse(null);
        if (b != null) {
            chambreRepository.deleteAll(b.getChambres());
            blocRepository.delete(b);
        }
    }

    @Override
    public void delete(Bloc b) {
        chambreRepository.deleteAll(b.getChambres());
        blocRepository.delete(b);
    }

    @Override
    public Bloc affecterChambresABloc(List<Long> numChambre, String nomBloc) {
        Bloc b = blocRepository.findByNomBloc(nomBloc);
        List<Chambre> chambres = new ArrayList<>();
        for (Long nu : numChambre) {
            Chambre chambre = chambreRepository.findByNumeroChambre(nu);
            if (chambre != null) {
                chambres.add(chambre);
            }
        }
        for (Chambre cha : chambres) {
            cha.setBloc(b);
            chambreRepository.save(cha);
        }
        return b;
    }

    @Override
    public Bloc affecterBlocAFoyer(String nomBloc, String nomFoyer) {
        Bloc b = blocRepository.findByNomBloc(nomBloc);
        Foyer f = foyerRepository.findByNomFoyer(nomFoyer);
        if (b != null && f != null) {
            b.setFoyer(f);
            return blocRepository.save(b);
        }
        return null;
    }

    @Override
    public Bloc ajouterBlocEtSesChambres(Bloc b) {
        Bloc savedBloc = blocRepository.save(b);
        for (Chambre c : b.getChambres()) {
            c.setBloc(savedBloc);
            chambreRepository.save(c);
        }
        return savedBloc;
    }

    @Override
    public Bloc ajouterBlocEtAffecterAFoyer(Bloc b, String nomFoyer) {
        Foyer f = foyerRepository.findByNomFoyer(nomFoyer);
        if (f != null) {
            b.setFoyer(f);
            return blocRepository.save(b);
        }
        return null;
    }
}
