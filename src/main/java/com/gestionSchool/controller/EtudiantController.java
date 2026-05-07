package com.gestionSchool.controller;

import com.gestionSchool.model.Etudiant;
import com.gestionSchool.repository.EtudiantRepo;
import com.gestionSchool.service.CodeGenerationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;

@Controller
@RequestMapping("/etudiants")
@AllArgsConstructor
public class EtudiantController {

    private final EtudiantRepo etudiantRepo;
    private final CodeGenerationService codeGenerationService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("etudiants", etudiantRepo.findAll());
        return "etudiant/list";
    }

    @GetMapping("/count")
    public long count() {
        return etudiantRepo.count();
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<java.util.List<LookupItem>> searchByMatricule(@RequestParam("matricule") String matricule) {
        if (matricule == null || matricule.isBlank()) {
            return ResponseEntity.ok(java.util.List.of());
        }
        var results = etudiantRepo.findByMatriculeContainingIgnoreCaseOrderByMatriculeAsc(matricule.trim())
                .stream()
                .limit(20)
                .map(e -> new LookupItem(e.getId(), e.getMatricule() + " - " + e.getNom()))
                .toList();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("etudiant", new Etudiant());
        return "etudiant/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Etudiant etudiant = etudiantRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Etudiant introuvable."));
        model.addAttribute("etudiant", etudiant);
        return "etudiant/form";
    }

    @PostMapping
    public String save(@RequestParam(required = false) Long id,
                      @RequestParam String nom,
                      @RequestParam String prenom,
                      @RequestParam String date_naissance,
                      @RequestParam(required = false) String adresse,
                      @RequestParam(name = "photo", required = false) MultipartFile photo,
                      RedirectAttributes redirectAttributes) {

        Etudiant etudiant;
        boolean isNew = (id == null);
        
        if (isNew) {
            etudiant = new Etudiant();
            etudiant.setMatricule(codeGenerationService.nextMatricule());
        } else {
            etudiant = etudiantRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Etudiant introuvable."));
        }
        
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setDate_naissance(LocalDate.parse(date_naissance));
        etudiant.setAdresse(adresse);
        updatePhoto(etudiant, photo);
        
        etudiantRepo.save(etudiant);
        redirectAttributes.addFlashAttribute("message", "Etudiant enregistre.");
        return "redirect:/etudiants";
    }

    private void updatePhoto(Etudiant etudiant, MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            return;
        }

        String contentType = photo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier photo doit etre une image.");
        }

        try {
            etudiant.setPhotoData(photo.getBytes());
            etudiant.setPhotoContentType(contentType);
        } catch (IOException ex) {
            throw new IllegalStateException("Impossible de lire la photo importee.", ex);
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        etudiantRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Etudiant supprime.");
        return "redirect:/etudiants";
    }

    public record LookupItem(Long id, String label) {}
}
