package com.gestionSchool.controller;

import com.gestionSchool.model.Classe;
import com.gestionSchool.model.Tarif;
import com.gestionSchool.repository.ClasseRepo;
import com.gestionSchool.repository.TarifRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tarifs")
@AllArgsConstructor
public class TarifController {

    private final TarifRepo tarifRepo;
    private final ClasseRepo classeRepo;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tarifs", tarifRepo.findAll());
        return "tarif/list";
    }

    @GetMapping("/count")
    public long count() {
        return tarifRepo.count();
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("tarif", new Tarif());
        model.addAttribute("classes", classeRepo.findAll());
        return "tarif/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Tarif tarif = tarifRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarif introuvable."));
        List<Classe> classes = classeRepo.findAll();
        model.addAttribute("tarif", tarif);
        model.addAttribute("classes", classes);
        return "tarif/form";
    }

    @PostMapping
    public String save(@ModelAttribute("tarif") Tarif tarif, RedirectAttributes redirectAttributes) {
        if (tarif.getClasse() == null || tarif.getClasse().getId() == null) {
            throw new IllegalArgumentException("Classe obligatoire.");
        }
        Classe classe = classeRepo.findById(tarif.getClasse().getId())
                .orElseThrow(() -> new IllegalArgumentException("Classe introuvable."));
        tarif.setClasse(classe);
        tarifRepo.save(tarif);
        redirectAttributes.addFlashAttribute("message", "Tarif enregistre.");
        return "redirect:/tarifs";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        tarifRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Tarif supprime.");
        return "redirect:/tarifs";
    }
}
