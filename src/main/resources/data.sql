-- Donnees pour la gestion scolaire (seed data)

-- 1. Filieres
INSERT INTO filiere (code, nom) VALUES 
('FIL-0001', 'Informatique'),
('FIL-0002', 'Gestion'),
('FIL-0003', 'Comptabilite');

-- 2. Classes
INSERT INTO classe (code, nom, filiere_id) VALUES 
('CL-0001', 'Licence 1 Info', 1),
('CL-0002', 'Licence 2 Info', 1),
('CL-0003', 'Licence 3 Info', 1),
('CL-0004', 'Licence 1 Gestion', 2),
('CL-0005', 'Licence 2 Gestion', 2),
('CL-0006', 'Licence 1 Comptabilite', 3);

-- 3. Annees academiques
INSERT INTO annee_academique (code, date_ouverture, date_debut_inscription, date_fin_inscription, date_fermeture, statut) VALUES 
('2026-2027', '2025-09-01', '2025-09-01', '2027-03-31', '2027-07-31', 'INSCRIPTIONS_OUVERTES'),
('2025-2026', '2025-09-01', '2025-09-01', '2026-03-31', '2026-07-31', 'ANNEE_CLOTUREE');

-- 4. Etudiants (sans ID explicite pour éviter le conflit avec auto-génération)
INSERT INTO etudiant (matricule, nom, prenom, date_naissance, adresse) VALUES 
('ETU-000001', 'Diop', 'Moussa', '2004-05-15', 'Dakar, Senegal'),
('ETU-000002', 'Sarr', 'Fatou', '2005-02-20', 'Thies, Senegal'),
('ETU-000003', 'Ndiaye', 'Omar', '2004-11-08', 'Saint-Louis, Senegal'),
('ETU-000004', 'Sow', 'Aminata', '2005-07-22', 'Kaolack, Senegal'),
('ETU-000005', 'Cisse', 'Mamadou', '2004-03-30', 'Ziguinchor, Senegal');

-- 5. Tarifs
INSERT INTO tarif (mensualite, autres_frais, classe_id) VALUES 
(50000, 25000, 1),
(55000, 30000, 2),
(60000, 35000, 3),
(45000, 20000, 4),
(50000, 25000, 5),
(40000, 15000, 6);

-- 6. Inscriptions
INSERT INTO inscription (date_inscription, etudiant_id, classe_id, annee_academique_id, tarif_id) VALUES 
('2025-10-01', 1, 1, 1, 1),
('2025-10-02', 2, 1, 1, 1),
('2025-10-03', 3, 2, 1, 2),
('2025-10-05', 4, 4, 1, 4),
('2025-10-06', 5, 6, 1, 6);
