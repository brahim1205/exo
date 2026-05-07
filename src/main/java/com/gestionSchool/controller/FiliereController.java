package com.gestionSchool.controller;

import com.gestionSchool.model.Filiere;
import com.gestionSchool.repository.FiliereRepo;
import com.gestionSchool.service.CodeGenerationService;
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
@RequestMapping("/filieres")
@AllArgsConstructor
public class FiliereController {

    private final FiliereRepo filiereRepo;
    private final CodeGenerationService codeGenerationService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("filieres", filiereRepo.findAll());
        return "filiere/list";
    }

    @GetMapping("/count")
    public long count() {
        return filiereRepo.count();
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("filiere", new Filiere());
        return "filiere/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Filiere filiere = filiereRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Filiere introuvable."));
        model.addAttribute("filiere", filiere);
        return "filiere/form";
    }

    @PostMapping
    public String save(@ModelAttribute("filiere") Filiere filiere, RedirectAttributes redirectAttributes) {
        if (filiere.getId() == null) {
            filiere.setCode(codeGenerationService.nextFiliereCode());
        } else {
            Filiere existing = filiereRepo.findById(filiere.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Filiere introuvable."));
            filiere.setCode(existing.getCode());
        }
        filiereRepo.save(filiere);
        redirectAttributes.addFlashAttribute("message", "Filiere enregistree.");
        return "redirect:/filieres";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        filiereRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Filiere supprimee.");
        return "redirect:/filieres";
    }
}
