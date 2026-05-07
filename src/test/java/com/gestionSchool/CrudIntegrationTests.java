package com.gestionSchool;

import com.gestionSchool.model.AnneeAcademique;
import com.gestionSchool.model.Classe;
import com.gestionSchool.model.Etudiant;
import com.gestionSchool.model.Filiere;
import com.gestionSchool.model.Inscription;
import com.gestionSchool.model.StatutAnneeAcademique;
import com.gestionSchool.model.Tarif;
import com.gestionSchool.repository.AnneeAcademiqueRepo;
import com.gestionSchool.repository.ClasseRepo;
import com.gestionSchool.repository.EtudiantRepo;
import com.gestionSchool.repository.FiliereRepo;
import com.gestionSchool.repository.InscriptionRepo;
import com.gestionSchool.repository.TarifRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:crudtests;DB_CLOSE_DELAY=0;DB_CLOSE_ON_EXIT=FALSE",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class CrudIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FiliereRepo filiereRepo;

    @Autowired
    private ClasseRepo classeRepo;

    @Autowired
    private EtudiantRepo etudiantRepo;

    @Autowired
    private TarifRepo tarifRepo;

    @Autowired
    private AnneeAcademiqueRepo anneeAcademiqueRepo;

    @Autowired
    private InscriptionRepo inscriptionRepo;

    @Test
    void filiereCrudWorks() throws Exception {
        mockMvc.perform(get("/filieres"))
                .andExpect(status().isOk());

        long initialCount = filiereRepo.count();

                mockMvc.perform(post("/filieres")
                        .param("nom", "Filiere Test CRUD"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/filieres"));

        Filiere filiere = filiereRepo.findAll().stream()
                .filter(item -> "Filiere Test CRUD".equals(item.getNom()))
                .findFirst()
                .orElseThrow();
        assertThat(filiereRepo.count()).isEqualTo(initialCount + 1);
        assertThat(filiere.getCode()).startsWith("FIL-");

                mockMvc.perform(post("/filieres")
                        .param("id", filiere.getId().toString())
                        .param("nom", "Filiere Test CRUD MAJ"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/filieres"));

        assertThat(filiereRepo.findById(filiere.getId())).get()
                .extracting(Filiere::getNom, Filiere::getCode)
                .containsExactly("Filiere Test CRUD MAJ", filiere.getCode());

        mockMvc.perform(post("/filieres/" + filiere.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/filieres"));

        assertThat(filiereRepo.existsById(filiere.getId())).isFalse();
    }

    @Test
    void classeCrudWorks() throws Exception {
        Filiere filiere = filiereRepo.save(new Filiere(null, "FIL-9001", "Filiere Classe CRUD", null));

        mockMvc.perform(get("/classes"))
                .andExpect(status().isOk());

                mockMvc.perform(post("/classes")
                        .param("nom", "Classe CRUD")
                        .param("filiere.id", filiere.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classes"));

        Classe classe = classeRepo.findAll().stream()
                .filter(item -> "Classe CRUD".equals(item.getNom()))
                .findFirst()
                .orElseThrow();
        assertThat(classe.getCode()).startsWith("CL-");
        assertThat(classe.getFiliere().getId()).isEqualTo(filiere.getId());

                mockMvc.perform(post("/classes")
                        .param("id", classe.getId().toString())
                        .param("nom", "Classe CRUD MAJ")
                        .param("filiere.id", filiere.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classes"));

        assertThat(classeRepo.findById(classe.getId())).get()
                .extracting(Classe::getNom, item -> item.getFiliere().getId())
                .containsExactly("Classe CRUD MAJ", filiere.getId());

        mockMvc.perform(post("/classes/" + classe.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classes"));

        assertThat(classeRepo.existsById(classe.getId())).isFalse();
    }

    @Test
    void etudiantCrudWorks() throws Exception {
        mockMvc.perform(get("/etudiants"))
                .andExpect(status().isOk());

        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "photo.jpg",
                "image/jpeg",
                "fake-image".getBytes(StandardCharsets.UTF_8)
        );

                mockMvc.perform(multipart("/etudiants")
                        .file(photo)
                        .param("nom", "Diallo")
                        .param("prenom", "Aminata")
                        .param("date_naissance", "2010-04-12")
                        .param("adresse", "Dakar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/etudiants"));

        Etudiant etudiant = etudiantRepo.findAll().stream()
                .filter(item -> "Diallo".equals(item.getNom()) && "Aminata".equals(item.getPrenom()))
                .findFirst()
                .orElseThrow();
        assertThat(etudiant.getMatricule()).startsWith("ETU-");
        assertThat(etudiant.getPhotoData()).isNotEmpty();

                mockMvc.perform(multipart("/etudiants")
                        .file(new MockMultipartFile("photo", new byte[0]))
                        .param("id", etudiant.getId().toString())
                        .param("nom", "Diallo")
                        .param("prenom", "Awa")
                        .param("date_naissance", "2010-04-12")
                        .param("adresse", "Thiès"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/etudiants"));

        assertThat(etudiantRepo.findById(etudiant.getId())).get()
                .extracting(Etudiant::getPrenom, Etudiant::getAdresse)
                .containsExactly("Awa", "Thiès");

        mockMvc.perform(post("/etudiants/" + etudiant.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/etudiants"));

        assertThat(etudiantRepo.existsById(etudiant.getId())).isFalse();
    }

    @Test
    void tarifCrudWorks() throws Exception {
        Filiere filiere = filiereRepo.save(new Filiere(null, "FIL-9002", "Filiere Tarif CRUD", null));
        Classe classe = classeRepo.save(new Classe(null, "CL-9002", "Classe Tarif CRUD", filiere, null, null));

        mockMvc.perform(get("/tarifs"))
                .andExpect(status().isOk());

                mockMvc.perform(post("/tarifs")
                        .param("classe.id", classe.getId().toString())
                        .param("mensualite", "25000")
                        .param("autresFrais", "5000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tarifs"));

        Tarif tarif = tarifRepo.findByClasse(classe).orElseThrow();
        assertThat(tarif.getMensualite()).isEqualByComparingTo("25000");

                mockMvc.perform(post("/tarifs")
                        .param("id", tarif.getId().toString())
                        .param("classe.id", classe.getId().toString())
                        .param("mensualite", "30000")
                        .param("autresFrais", "7000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tarifs"));

        assertThat(tarifRepo.findById(tarif.getId())).get()
                .extracting(Tarif::getMensualite, Tarif::getAutresFrais)
                .containsExactly(new BigDecimal("30000"), new BigDecimal("7000"));

        mockMvc.perform(post("/tarifs/" + tarif.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tarifs"));

        assertThat(tarifRepo.existsById(tarif.getId())).isFalse();
    }

    @Test
    void anneeAcademiqueCrudWorks() throws Exception {
        mockMvc.perform(get("/annees"))
                .andExpect(status().isOk());

                mockMvc.perform(post("/annees")
                        .param("dateOuverture", "2030-09-01")
                        .param("dateFermeture", "2031-07-31")
                        .param("dateDebutInscription", "2030-09-01")
                        .param("dateFinInscription", "2031-06-30")
                        .param("statut", "BROUILLON"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/annees"));

        AnneeAcademique annee = anneeAcademiqueRepo.findAll().stream()
                .filter(item -> "2030-2031".equals(item.getCode()))
                .findFirst()
                .orElseThrow();
        assertThat(annee.getStatut()).isEqualTo(StatutAnneeAcademique.BROUILLON);

                mockMvc.perform(post("/annees")
                        .param("id", annee.getId().toString())
                        .param("dateOuverture", "2030-09-01")
                        .param("dateFermeture", "2031-08-15")
                        .param("dateDebutInscription", "2030-09-05")
                        .param("dateFinInscription", "2031-07-15")
                        .param("statut", "PUBLIEE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/annees"));

        assertThat(anneeAcademiqueRepo.findById(annee.getId())).get()
                .extracting(AnneeAcademique::getCode, AnneeAcademique::getStatut, AnneeAcademique::getDateFermeture)
                .containsExactly("2030-2031", StatutAnneeAcademique.PUBLIEE, LocalDate.parse("2031-08-15"));

        mockMvc.perform(post("/annees/" + annee.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/annees"));

        assertThat(anneeAcademiqueRepo.existsById(annee.getId())).isFalse();
    }

    @Test
    void inscriptionCrudWorksIncludingFicheAndPdf() throws Exception {
        Filiere filiere = filiereRepo.save(new Filiere(null, "FIL-9003", "Filiere Inscription CRUD", null));
        Classe classe1 = classeRepo.save(new Classe(null, "CL-9101", "Classe 1", filiere, null, null));
        Classe classe2 = classeRepo.save(new Classe(null, "CL-9102", "Classe 2", filiere, null, null));
        tarifRepo.save(new Tarif(null, classe1, new BigDecimal("20000"), new BigDecimal("5000")));
        tarifRepo.save(new Tarif(null, classe2, new BigDecimal("25000"), new BigDecimal("6000")));
        AnneeAcademique annee1 = anneeAcademiqueRepo.save(buildYear("2032-09-01", "2033-06-30"));
        AnneeAcademique annee2 = anneeAcademiqueRepo.save(buildYear("2033-09-01", "2034-06-30"));
        Etudiant etudiant = etudiantRepo.save(buildStudent("ETU-900001", "Sow", "Moussa"));

        mockMvc.perform(get("/inscriptions"))
                .andExpect(status().isOk());

                mockMvc.perform(post("/inscriptions")
                        .param("etudiantMode", "ancien")
                        .param("etudiantId", etudiant.getId().toString())
                        .param("classeId", classe1.getId().toString())
                        .param("anneeId", annee1.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/inscriptions/*/fiche"));

        Inscription inscription = inscriptionRepo.findByEtudiantAndAnneeAcademique(etudiant, annee1).orElseThrow();
        assertThat(inscription.getClasse().getId()).isEqualTo(classe1.getId());

        mockMvc.perform(get("/inscriptions/" + inscription.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inscriptions/" + inscription.getId() + "/fiche"));

        mockMvc.perform(get("/inscriptions/" + inscription.getId() + "/fiche"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/inscriptions/" + inscription.getId() + "/edit"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/inscriptions/" + inscription.getId() + "/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));

                mockMvc.perform(post("/inscriptions")
                        .param("id", inscription.getId().toString())
                        .param("etudiantMode", "ancien")
                        .param("etudiantId", etudiant.getId().toString())
                        .param("classeId", classe2.getId().toString())
                        .param("anneeId", annee2.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inscriptions/" + inscription.getId() + "/fiche"));

        Inscription updated = inscriptionRepo.findById(inscription.getId()).orElseThrow();
        assertThat(updated.getClasse().getId()).isEqualTo(classe2.getId());
        assertThat(updated.getAnneeAcademique().getId()).isEqualTo(annee2.getId());

        mockMvc.perform(post("/inscriptions/" + inscription.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inscriptions"));

        assertThat(inscriptionRepo.existsById(inscription.getId())).isFalse();
    }

    private AnneeAcademique buildYear(String start, String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        AnneeAcademique annee = new AnneeAcademique();
        annee.setDateOuverture(startDate);
        annee.setDateDebutInscription(startDate);
        annee.setDateFinInscription(endDate);
        annee.setDateFermeture(endDate.plusMonths(1));
        annee.setStatut(StatutAnneeAcademique.BROUILLON);
        return annee;
    }

    private Etudiant buildStudent(String matricule, String nom, String prenom) {
        Etudiant etudiant = new Etudiant();
        etudiant.setMatricule(matricule);
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setDate_naissance(LocalDate.of(2011, 1, 15));
        etudiant.setAdresse("Adresse test");
        etudiant.setPhotoData(Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+Xkz8AAAAASUVORK5CYII="));
        etudiant.setPhotoContentType("image/png");
        return etudiant;
    }
}
