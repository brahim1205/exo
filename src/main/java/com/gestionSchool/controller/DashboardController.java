package com.gestionSchool.controller;

import com.gestionSchool.repository.AnneeAcademiqueRepo;
import com.gestionSchool.repository.ClasseRepo;
import com.gestionSchool.repository.EtudiantRepo;
import com.gestionSchool.repository.FiliereRepo;
import com.gestionSchool.repository.InscriptionRepo;
import com.gestionSchool.repository.TarifRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class DashboardController {

    private final EtudiantRepo etudiantRepo;
    private final ClasseRepo classeRepo;
    private final FiliereRepo filiereRepo;
    private final InscriptionRepo inscriptionRepo;
    private final AnneeAcademiqueRepo anneeAcademiqueRepo;
    private final TarifRepo tarifRepo;

    @GetMapping("/dashboard")
    public String dashboardShell() {
        return "dashboard/admin";
    }

    @GetMapping("/dashboard-view")
    public String dashboard(Model model) {
        model.addAttribute("countEtudiants", etudiantRepo.count());
        model.addAttribute("countClasses", classeRepo.count());
        model.addAttribute("countFilieres", filiereRepo.count());
        model.addAttribute("countInscriptions", inscriptionRepo.count());
        model.addAttribute("countAnnees", anneeAcademiqueRepo.count());
        model.addAttribute("countTarifs", tarifRepo.count());
        return "dashboard/view";
    }
}
