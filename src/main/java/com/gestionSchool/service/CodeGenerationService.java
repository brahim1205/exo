package com.gestionSchool.service;

import com.gestionSchool.model.Classe;
import com.gestionSchool.model.Etudiant;
import com.gestionSchool.model.Filiere;
import com.gestionSchool.repository.ClasseRepo;
import com.gestionSchool.repository.EtudiantRepo;
import com.gestionSchool.repository.FiliereRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CodeGenerationService {

    private static final String ETUDIANT_PREFIX = "ETU-";
    private static final String FILIERE_PREFIX = "FIL-";
    private static final String CLASSE_PREFIX = "CL-";

    private final EtudiantRepo etudiantRepo;
    private final FiliereRepo filiereRepo;
    private final ClasseRepo classeRepo;

    public String nextMatricule() {
        String last = etudiantRepo.findTopByOrderByMatriculeDesc()
                .map(Etudiant::getMatricule)
                .orElse(null);
        int next = nextNumber(last, ETUDIANT_PREFIX);
        return ETUDIANT_PREFIX + String.format("%06d", next);
    }

    public String nextFiliereCode() {
        String last = filiereRepo.findTopByOrderByCodeDesc()
                .map(Filiere::getCode)
                .orElse(null);
        int next = nextNumber(last, FILIERE_PREFIX);
        return FILIERE_PREFIX + String.format("%04d", next);
    }

    public String nextClasseCode() {
        String last = classeRepo.findTopByOrderByCodeDesc()
                .map(Classe::getCode)
                .orElse(null);
        int next = nextNumber(last, CLASSE_PREFIX);
        return CLASSE_PREFIX + String.format("%04d", next);
    }

    private int nextNumber(String lastCode, String prefix) {
        if (lastCode == null || !lastCode.startsWith(prefix)) {
            return 1;
        }
        String suffix = lastCode.substring(prefix.length());
        try {
            return Integer.parseInt(suffix) + 1;
        } catch (NumberFormatException ex) {
            return 1;
        }
    }
}
