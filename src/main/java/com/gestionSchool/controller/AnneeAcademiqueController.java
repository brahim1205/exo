package com.gestionSchool.controller;

import com.gestionSchool.model.AnneeAcademique;
import com.gestionSchool.service.AnneeAcademiqueCreationService;
import com.gestionSchool.service.AnneeAcademiqueManagementService;
import com.gestionSchool.repository.AnneeAcademiqueRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/annees")
@AllArgsConstructor
public class AnneeAcademiqueController {

    private final AnneeAcademiqueRepo anneeAcademiqueRepo;
    private final AnneeAcademiqueCreationService creationService;
    private final AnneeAcademiqueManagementService managementService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("annees", anneeAcademiqueRepo.findAll());
        return "anneeacademique/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("annee", new AnneeAcademique());
        return "anneeacademique/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        AnneeAcademique annee = anneeAcademiqueRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Annee academique introuvable."));
        model.addAttribute("annee", annee);
        return "anneeacademique/form";
    }

    @PostMapping
    public String save(@ModelAttribute("annee") AnneeAcademique annee, RedirectAttributes redirectAttributes) {
        if (annee.getId() == null) {
            creationService.creer(annee);
            redirectAttributes.addFlashAttribute("message", "Annee academique creee.");
        } else {
            if (annee.getDateDebutInscription() != null && annee.getDateFinInscription() != null) {
                annee.setCode(annee.getDateDebutInscription().getYear() + "-" + annee.getDateFinInscription().getYear());
            }
            anneeAcademiqueRepo.save(annee);
            redirectAttributes.addFlashAttribute("message", "Annee academique mise a jour.");
        }
        return "redirect:/annees";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        anneeAcademiqueRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Annee academique supprimee.");
        return "redirect:/annees";
    }

    @PostMapping("/{id}/publier")
    public String publier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        managementService.publier(id);
        redirectAttributes.addFlashAttribute("message", "Annee academique publiee.");
        return "redirect:/annees";
    }

    @PostMapping("/{id}/ouvrir")
    public String ouvrirInscriptions(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        managementService.ouvrirInscriptions(id);
        redirectAttributes.addFlashAttribute("message", "Inscriptions ouvertes.");
        return "redirect:/annees";
    }

    @PostMapping("/{id}/suspendre")
    public String suspendreInscriptions(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        managementService.suspendreInscriptions(id);
        redirectAttributes.addFlashAttribute("message", "Inscriptions suspendues.");
        return "redirect:/annees";
    }

    @PostMapping("/{id}/reouvrir")
    public String reouvrirInscriptions(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        managementService.reouvrirInscriptions(id);
        redirectAttributes.addFlashAttribute("message", "Inscriptions reouvertes.");
        return "redirect:/annees";
    }

    @PostMapping("/{id}/cloturer")
    public String cloturer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        managementService.cloturer(id);
        redirectAttributes.addFlashAttribute("message", "Annee academique cloturee.");
        return "redirect:/annees";
    }

}
