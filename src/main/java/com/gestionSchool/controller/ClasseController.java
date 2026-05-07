package com.gestionSchool.controller;

import com.gestionSchool.model.Classe;
import com.gestionSchool.model.Filiere;
import com.gestionSchool.repository.ClasseRepo;
import com.gestionSchool.repository.FiliereRepo;
import com.gestionSchool.service.CodeGenerationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/classes")
@AllArgsConstructor
public class ClasseController {

    private final ClasseRepo classeRepo;
    private final FiliereRepo filiereRepo;
    private final CodeGenerationService codeGenerationService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("classes", classeRepo.findAll());
        return "classe/list";
    }

    @GetMapping("/count")
    public long count() {
        return classeRepo.count();
    }

    @GetMapping("/search")
    @ResponseBody
    public List<LookupItem> searchByNom(@RequestParam(value = "q", required = false) String q,
                                        @RequestParam(value = "code", required = false) String code,
                                        @RequestParam(value = "nom", required = false) String nom) {
        String query = q;
        if (query == null || query.isBlank()) {
            query = code != null && !code.isBlank() ? code : nom;
        }
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String trimmed = query.trim();
        var resultsByCode = classeRepo.findByCodeContainingIgnoreCaseOrderByCodeAsc(trimmed);
        var resultsByNom = classeRepo.findByNomContainingIgnoreCaseOrderByNomAsc(trimmed);
        return java.util.stream.Stream.concat(resultsByCode.stream(), resultsByNom.stream())
                .distinct()
                .limit(20)
                .map(c -> new LookupItem(c.getId(), c.getCode() + " - " + c.getNom()))
                .collect(Collectors.toList());
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("classe", new Classe());
        model.addAttribute("filieres", filiereRepo.findAll());
        return "classe/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Classe classe = classeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Classe introuvable."));
        List<Filiere> filieres = filiereRepo.findAll();
        model.addAttribute("classe", classe);
        model.addAttribute("filieres", filieres);
        return "classe/form";
    }

    @PostMapping
    public String save(@ModelAttribute("classe") Classe classe, RedirectAttributes redirectAttributes) {
        if (classe.getFiliere() == null || classe.getFiliere().getId() == null) {
            throw new IllegalArgumentException("Filiere obligatoire.");
        }
        Filiere filiere = filiereRepo.findById(classe.getFiliere().getId())
                .orElseThrow(() -> new IllegalArgumentException("Filiere introuvable."));
        classe.setFiliere(filiere);
        if (classe.getId() == null) {
            classe.setCode(codeGenerationService.nextClasseCode());
        } else {
            Classe existing = classeRepo.findById(classe.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Classe introuvable."));
            classe.setCode(existing.getCode());
        }
        classeRepo.save(classe);
        redirectAttributes.addFlashAttribute("message", "Classe enregistree.");
        return "redirect:/classes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        classeRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Classe supprimee.");
        return "redirect:/classes";
    }

    public record LookupItem(Long id, String label) {}
}
