package com.gestionSchool.controller;

import com.gestionSchool.model.*;
import com.gestionSchool.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.lowagie.text.*;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/inscriptions")
@AllArgsConstructor
public class InscriptionController {

    private final InscriptionRepo inscriptionRepo;
    private final EtudiantRepo etudiantRepo;
    private final ClasseRepo classeRepo;
    private final AnneeAcademiqueRepo anneeAcademiqueRepo;
    private final TarifRepo tarifRepo;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("inscriptions", inscriptionRepo.findAll());
        return "inscription/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("inscription", new Inscription());
        model.addAttribute("etudiants", etudiantRepo.findAll());
        model.addAttribute("classes", classeRepo.findAll());
        model.addAttribute("annees", anneeAcademiqueRepo.findAll());
        model.addAttribute("etudiantMode", "ancien");
        return "inscription/form";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id) {
        return "redirect:/inscriptions/" + id + "/fiche";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Inscription inscription = inscriptionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inscription introuvable."));
        model.addAttribute("inscription", inscription);
        model.addAttribute("etudiants", etudiantRepo.findAll());
        model.addAttribute("classes", classeRepo.findAll());
        model.addAttribute("annees", anneeAcademiqueRepo.findAll());
        model.addAttribute("etudiantMode", "ancien");
        return "inscription/form";
    }

    @PostMapping
    public String save(@RequestParam(required = false) Long id,
                     @RequestParam String etudiantMode,
                     @RequestParam(required = false) Long etudiantId,
                     @RequestParam(required = false) String newNom,
                     @RequestParam(required = false) String newPrenom,
                     @RequestParam(required = false) String newDate_naissance,
                     @RequestParam(required = false) String newAdresse,
                     @RequestParam(required = false) MultipartFile photoFile,
                     @RequestParam Long classeId,
                     @RequestParam Long anneeId,
                     RedirectAttributes redirectAttributes) {
        try {
            Long actualEtudiantId;
            String mode = etudiantMode != null ? etudiantMode : "ancien";
            
            if ("nouveau".equals(mode)) {
                if (newNom == null || newNom.isBlank() || newPrenom == null || newPrenom.isBlank() || newDate_naissance == null || newDate_naissance.isBlank()) {
                    redirectAttributes.addFlashAttribute("error", "Nom, Prénom et Date de naissance obligatoires.");
                    return "redirect:/inscriptions/new";
                }
                Etudiant e = new Etudiant();
                e.setNom(newNom.trim());
                e.setPrenom(newPrenom.trim());
                e.setDate_naissance(LocalDate.parse(newDate_naissance));
                e.setAdresse(newAdresse != null && !newAdresse.isBlank() ? newAdresse.trim() : null);
                e.setMatricule("ETU" + System.currentTimeMillis());

                updatePhoto(e, photoFile);

                e = etudiantRepo.save(e);
                actualEtudiantId = e.getId();
            } else {
                if (etudiantId == null || etudiantId.toString().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Veuillez sélectionner un étudiant.");
                    return "redirect:/inscriptions/new";
                }
                actualEtudiantId = etudiantId;
            }

            Etudiant etudiant = etudiantRepo.findById(actualEtudiantId).orElseThrow();
            Classe classe = classeRepo.findById(classeId).orElseThrow();
            AnneeAcademique annee = anneeAcademiqueRepo.findById(anneeId).orElseThrow();

            Tarif tarif = tarifRepo.findByClasse(classe).orElse(null);
            if (tarif == null) {
                tarif = new Tarif();
                tarif.setClasse(classe);
                tarif.setMensualite(BigDecimal.ZERO);
                tarif.setAutresFrais(BigDecimal.ZERO);
                tarif = tarifRepo.save(tarif);
            }

            Inscription inscription = id != null
                    ? inscriptionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Inscription introuvable."))
                    : new Inscription();
            inscription.setEtudiant(etudiant);
            inscription.setClasse(classe);
            inscription.setAnneeAcademique(annee);
            inscription.setTarif(tarif);
            if (inscription.getDateInscription() == null) {
                inscription.setDateInscription(LocalDate.now());
            }

            Inscription saved = inscriptionRepo.save(inscription);
            redirectAttributes.addFlashAttribute("message", id == null ? "Inscription créée avec succès!" : "Inscription modifiée avec succès!");
            return "redirect:/inscriptions/" + saved.getId() + "/fiche";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return id != null ? "redirect:/inscriptions/" + id + "/edit" : "redirect:/inscriptions/new";
        }
    }

    @GetMapping("/{id}/fiche")
    public String fiche(@PathVariable Long id, Model model) {
        Inscription inscription = inscriptionRepo.findById(id).orElse(null);
        model.addAttribute("inscription", inscription);
        model.addAttribute("photoDataUri", buildPhotoDataUri(inscription));
        return "inscription/fiche";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        inscriptionRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Inscription supprimée avec succès!");
        return "redirect:/inscriptions";
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<?> generatePdf(@PathVariable Long id) {
        Inscription inscription = inscriptionRepo.findById(id).orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 28, 28, 24, 24);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 34, new Color(194, 120, 0));
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 15, new Color(194, 120, 0));
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.DARK_GRAY);

            Paragraph title = new Paragraph("FICHE D'INSCRIPTION", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(12);
            document.add(title);

            PdfPTable topLine = new PdfPTable(new float[]{1f, 1f});
            topLine.setWidthPercentage(100);
            topLine.setSpacingAfter(10);
            topLine.addCell(createLineCell("Catégorie : .............................................", labelFont, Element.ALIGN_LEFT));
            topLine.addCell(createLineCell("GestionSchool - Saison " + safe(inscription.getAnneeAcademique() != null ? inscription.getAnneeAcademique().getCode() : null), subTitleFont, Element.ALIGN_RIGHT));
            document.add(topLine);

            document.add(createIdentitySection(inscription, sectionFont, labelFont, valueFont, smallFont));
            document.add(createParentsSection(sectionFont, labelFont, valueFont));
            document.add(createBottomNotice(labelFont));

            document.close();

            byte[] pdfBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=fiche-inscription-" + id + ".pdf");
            headers.setContentType(MediaType.APPLICATION_PDF);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur génération PDF: " + e.getMessage());
        }
    }

    private void updatePhoto(Etudiant etudiant, MultipartFile photoFile) throws IOException {
        if (photoFile == null || photoFile.isEmpty()) {
            return;
        }

        String contentType = photoFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier photo doit etre une image.");
        }

        etudiant.setPhotoData(photoFile.getBytes());
        etudiant.setPhotoContentType(contentType);
    }

    private String buildPhotoDataUri(Inscription inscription) {
        if (inscription == null || inscription.getEtudiant() == null || inscription.getEtudiant().getPhotoData() == null || inscription.getEtudiant().getPhotoData().length == 0) {
            return null;
        }
        String contentType = inscription.getEtudiant().getPhotoContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.IMAGE_JPEG_VALUE;
        }
        return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(inscription.getEtudiant().getPhotoData());
    }

    private PdfPTable createIdentitySection(Inscription inscription, Font sectionFont, Font labelFont, Font valueFont, Font smallFont) throws Exception {
        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(100);
        wrapper.setSpacingAfter(16);

        PdfPCell cell = new PdfPCell();
        cell.setPadding(14);
        cell.setBorderWidth(1.4f);
        cell.setBorderColor(Color.BLACK);

        Paragraph sectionTitle = new Paragraph("Identification joueur", sectionFont);
        sectionTitle.setAlignment(Element.ALIGN_CENTER);
        sectionTitle.setSpacingAfter(12);
        cell.addElement(sectionTitle);

        PdfPTable content = new PdfPTable(new float[]{3.2f, 1.1f});
        content.setWidthPercentage(100);

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        left.setPaddingRight(18);
        addField(left, "NOM", inscription.getEtudiant().getNom(), labelFont, valueFont);
        addField(left, "PRENOM", inscription.getEtudiant().getPrenom(), labelFont, valueFont);
        addField(left, "Date de naissance", formatDate(inscription.getEtudiant().getDate_naissance()), labelFont, valueFont);
        addField(left, "Adresse", inscription.getEtudiant().getAdresse(), labelFont, valueFont);
        addField(left, "Lieu de naissance", null, labelFont, valueFont);
        addDualField(left, "Tel domicile", null, "Portable", null, labelFont, valueFont);
        addField(left, "Adresse électronique (mail)", null, labelFont, valueFont);
        addField(left, "Assurance", "Oui [ ]   Non [ ]   -   Nom assureur : ....................................................", labelFont, labelFont);

        Paragraph info = new Paragraph("Si pas d'assurance, voir fiche des modalités d'inscription sur les dommages corporels", smallFont);
        info.setSpacingBefore(6);
        info.setSpacingAfter(8);
        left.addElement(info);

        addField(left, "N° contrat assurance", null, labelFont, valueFont);
        addDualField(left, "Fait le", formatDate(inscription.getDateInscription()), "Signature parent ou joueur majeur", null, labelFont, valueFont);

        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_CENTER);
        right.setVerticalAlignment(Element.ALIGN_TOP);
        right.setPaddingTop(28);
        right.addElement(createPhotoBlock(inscription));

        content.addCell(left);
        content.addCell(right);
        cell.addElement(content);
        wrapper.addCell(cell);
        return wrapper;
    }

    private PdfPTable createParentsSection(Font sectionFont, Font labelFont, Font valueFont) {
        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(100);
        wrapper.setSpacingAfter(10);

        PdfPCell cell = new PdfPCell();
        cell.setPadding(14);
        cell.setBorderWidth(1.4f);
        cell.setBorderColor(Color.BLACK);

        Paragraph sectionTitle = new Paragraph("Renseignements sur les parents responsables du joueur mineur", sectionFont);
        sectionTitle.setAlignment(Element.ALIGN_CENTER);
        sectionTitle.setSpacingAfter(14);
        cell.addElement(sectionTitle);

        addDualField(cell, "Responsable 1 - NOM", null, "PRENOM", null, labelFont, valueFont);
        addDualField(cell, "Tel domicile", null, "Portable", null, labelFont, valueFont);
        addField(cell, "Adresse électronique (mail)", null, labelFont, valueFont);
        addDualField(cell, "Responsable 2 - NOM", null, "PRENOM", null, labelFont, valueFont);
        addDualField(cell, "Tel domicile", null, "Portable", null, labelFont, valueFont);
        addField(cell, "Adresse électronique (mail)", null, labelFont, valueFont);
        addField(cell, "Je soussigné M/Mme", ".................................................... autorise l'enfant ....................................................", labelFont, labelFont);
        addField(cell, "", "à s'adonner à la pratique du football au sein de GestionSchool durant la saison en cours.", labelFont, labelFont);
        addField(cell, "", "J'autorise les responsables ou bénévoles du club à transporter mon enfant si nécessaire.", labelFont, labelFont);
        addDualField(cell, "Fait le", null, "Signature parent", null, labelFont, valueFont);

        wrapper.addCell(cell);
        return wrapper;
    }

    private PdfPTable createBottomNotice(Font labelFont) {
        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(4);

        Paragraph p1 = new Paragraph("Afin de valider mon inscription, je joins le règlement de la cotisation.", labelFont);
        p1.setSpacingAfter(4);
        cell.addElement(p1);
        Paragraph p2 = new Paragraph("[ ] Espèces      [ ] Chèque n° : ..............................................      Banque : ..............................................", labelFont);
        cell.addElement(p2);

        wrapper.addCell(cell);
        return wrapper;
    }

    private void addField(PdfPCell parent, String label, String value, Font labelFont, Font valueFont) {
        String prefix = (label == null || label.isBlank()) ? "" : label + " : ";
        String text = prefix + safeOrLine(value);
        Paragraph paragraph = new Paragraph(text, label != null && !label.isBlank() && value != null && !value.contains("...") ? valueFont : labelFont);
        paragraph.setSpacingAfter(10);
        parent.addElement(paragraph);
    }

    private void addDualField(PdfPCell parent, String label1, String value1, String label2, String value2, Font labelFont, Font valueFont) {
        PdfPTable table = new PdfPTable(new float[]{1f, 1f});
        table.setWidthPercentage(100);
        table.setSpacingAfter(8);
        table.addCell(createLineCell(label1 + " : " + safeOrLine(value1), value1 == null ? labelFont : valueFont, Element.ALIGN_LEFT));
        table.addCell(createLineCell(label2 + " : " + safeOrLine(value2), value2 == null ? labelFont : valueFont, Element.ALIGN_LEFT));
        parent.addElement(table);
    }

    private PdfPCell createLineCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(0);
        return cell;
    }

    private PdfPTable createPhotoBlock(Inscription inscription) throws Exception {
        PdfPTable photoTable = new PdfPTable(1);
        photoTable.setTotalWidth(120);
        photoTable.setLockedWidth(true);

        PdfPCell photoCell = new PdfPCell();
        photoCell.setFixedHeight(160);
        photoCell.setBorderWidth(1f);
        photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        byte[] photoData = inscription.getEtudiant() != null ? inscription.getEtudiant().getPhotoData() : null;
        if (photoData != null && photoData.length > 0) {
            Image image = Image.getInstance(photoData);
            image.scaleToFit(104, 144);
            image.setAlignment(Element.ALIGN_CENTER);
            photoCell.addElement(image);
        } else {
            Paragraph placeholder = new Paragraph("PHOTO", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.BLACK));
            placeholder.setAlignment(Element.ALIGN_CENTER);
            photoCell.addElement(placeholder);
        }

        photoTable.addCell(photoCell);
        return photoTable;
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null;
    }

    private String safe(String value) {
        return value != null && !value.isBlank() ? value : "........................";
    }

    private String safeOrLine(String value) {
        return value != null && !value.isBlank() ? value : "........................................................";
    }

    private PdfPCell createCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        return cell;
    }
}
