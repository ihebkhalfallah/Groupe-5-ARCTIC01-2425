package tn.esprit.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tn.esprit.spring.DAO.Entities.Universite;
import tn.esprit.spring.Services.Universite.IUniversiteService;

@SpringBootApplication
//@EnableScheduling
public class FoyerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoyerApplication.class, args);
    }

//    @Autowired
//    IUniversiteService universiteService;
//
//    Universite u = Universite.builder()
//            .nomUniversite("ESPRIT")
//            .adresse("Ariana")
//            .build();
//
//    Universite saved = universiteService.addOrUpdate(u);
//    System.out.print()
}
