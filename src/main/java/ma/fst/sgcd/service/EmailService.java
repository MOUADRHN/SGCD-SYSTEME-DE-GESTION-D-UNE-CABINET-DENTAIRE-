package ma.fst.sgcd.service;

import ma.fst.sgcd.model.Facture;
import ma.fst.sgcd.model.Patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Service d'envoi d'email simulé.
 *
 * En production : remplacer le corps de sendFacture() par
 * une intégration JavaMail (SMTP) ou une API externe (SendGrid, Mailgun).
 *
 * Actuellement : simulation avec log dans la console Tomcat.
 */
public class EmailService {

    private static final Logger LOG = Logger.getLogger(EmailService.class.getName());

    /**
     * Envoie (ou simule) l'envoi du reçu de facture par email.
     *
     * @param patient   patient destinataire
     * @param facture   facture à envoyer
     * @return true si l'envoi a réussi (ou a été simulé avec succès)
     */
    public boolean sendFacture(Patient patient, Facture facture) {
        try {
            String destinataire = patient != null && patient.getTelephone() != null
                    ? patient.getNomComplet() : "Patient inconnu";

            String contenu = buildEmailContent(patient, facture);

            // ── Simulation : log dans Tomcat catalina.out ────────────────
            LOG.info("=======================================================");
            LOG.info("[SGCD EMAIL] Envoi simulé à : " + destinataire);
            LOG.info("[SGCD EMAIL] Objet : Votre reçu SGCD - Facture FAC-" + facture.getIdFacture());
            LOG.info("[SGCD EMAIL] Contenu :");
            LOG.info(contenu);
            LOG.info("[SGCD EMAIL] Envoi simulé avec succès - " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            LOG.info("=======================================================");

            /*
             * ── Pour activer l'envoi réel avec JavaMail ──────────────────
             * Ajouter la dépendance Maven :
             *   <dependency>
             *     <groupId>com.sun.mail</groupId>
             *     <artifactId>jakarta.mail</artifactId>
             *     <version>2.0.1</version>
             *   </dependency>
             *
             * Puis remplacer le log ci-dessus par :
             *
             * Properties props = new Properties();
             * props.put("mail.smtp.host", "smtp.gmail.com");
             * props.put("mail.smtp.port", "587");
             * props.put("mail.smtp.auth", "true");
             * props.put("mail.smtp.starttls.enable", "true");
             *
             * Session session = Session.getInstance(props, new Authenticator() {
             *     protected PasswordAuthentication getPasswordAuthentication() {
             *         return new PasswordAuthentication("sgcd@votredomaine.ma", "mot_de_passe");
             *     }
             * });
             *
             * Message message = new MimeMessage(session);
             * message.setFrom(new InternetAddress("sgcd@votredomaine.ma"));
             * message.setRecipients(Message.RecipientType.TO,
             *         InternetAddress.parse(patient.getEmail()));
             * message.setSubject("Votre reçu SGCD - Facture FAC-" + facture.getIdFacture());
             * message.setText(contenu);
             * Transport.send(message);
             */

            return true;

        } catch (Exception e) {
            LOG.severe("[SGCD EMAIL] Erreur lors de l'envoi : " + e.getMessage());
            return false;
        }
    }

    /**
     * Construit le contenu textuel de l'email de reçu.
     */
    private String buildEmailContent(Patient patient, Facture facture) {
        String nomPatient = patient != null ? patient.getNomComplet() : "Patient";
        String ligne      = "─────────────────────────────────────────";

        return "Bonjour " + nomPatient + ",\n\n"
                + "Nous vous confirmons la réception de votre paiement.\n\n"
                + ligne + "\n"
                + "  REÇU DE PAIEMENT — SGCD Cabinet Dentaire\n"
                + ligne + "\n"
                + "  Numéro de facture : FAC-" + facture.getIdFacture() + "\n"
                + "  Date              : " + facture.getDate() + "\n"
                + "  Montant réglé     : " + String.format("%.2f MAD", facture.getMontantTotal()) + "\n"
                + (facture.getPaiement() != null
                ? "  Mode de paiement  : " + facture.getPaiement().getModePaiement().getLibelle() + "\n"
                : "")
                + ligne + "\n\n"
                + "Merci de votre confiance.\n"
                + "L'équipe SGCD — Cabinet Dentaire\n"
                + "Fès, Maroc\n";
    }
}
