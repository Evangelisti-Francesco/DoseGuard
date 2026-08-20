package it.ispwproject.doseguard.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import it.ispwproject.doseguard.bean.AppointmentResponseBean;
import it.ispwproject.doseguard.exception.NotificationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class NotificationService {

    private static final String PROPERTIES_FILE = "src/main/resources/db.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Impossibile caricare db.properties");
        }
    }

    private static final String API_KEY    = properties.getProperty("SENDGRID_API_KEY");
    private static final String FROM_EMAIL = properties.getProperty("SENDGRID_FROM_EMAIL");

    private static final String TEMPLATE_CONFIRMATION_PATIENT = "d-4d49f14c5f734b3cb51e504d56823d0e";
    private static final String TEMPLATE_CANCELLATION_PATIENT = "d-9535f2c985ad4dc5ab7c51980e6069b9";
    private static final String TEMPLATE_CONFIRMATION_DOCTOR  = "d-a56071917983440a8473eb642cac5d88";
    private static final String TEMPLATE_CANCELLATION_DOCTOR  = "d-5187d5081a2a4baeb955d16656542960";
    private static final String TEMPLATE_NEW_ACTIVITY        = "d-5fc4b82a3df44ae4ac065f94932f1962";

    private static final String KEY_PATIENT_NAME        = "patientName";
    private static final String KEY_DOCTOR_NAME         = "doctorName";
    private static final String KEY_SPECIALIZATION_NAME = "specializationName";
    private static final String KEY_DATE              = "date";
    private static final String KEY_START_TIME        = "startTime";
    private static final String KEY_DESCRIPTION       = "description";

    private NotificationService() {}

    public static void sendBookingConfirmationToPatient(String toEmail,
                                                        AppointmentResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalizationForPatient(toEmail, booking);
        sendTemplateEmail(TEMPLATE_CONFIRMATION_PATIENT, p);
    }

    public static void sendBookingCancellationToPatient(String toEmail,
                                                        AppointmentResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalizationForPatient(toEmail, booking);
        sendTemplateEmail(TEMPLATE_CANCELLATION_PATIENT, p);
    }

    public static void sendBookingConfirmationToDoctor(String toEmail,
                                                       AppointmentResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalizationForDoctor(toEmail, booking);
        sendTemplateEmail(TEMPLATE_CONFIRMATION_DOCTOR, p);
    }

    public static void sendBookingCancellationToDoctor(String toEmail,
                                                       AppointmentResponseBean booking) throws NotificationException {
        Personalization p = buildPersonalizationForDoctor(toEmail, booking);
        sendTemplateEmail(TEMPLATE_CANCELLATION_DOCTOR, p);
    }

    public static void sendNewActivity(String toEmail, String patientName,
                                       String doctorName, String description) throws NotificationException {
        Personalization p = new Personalization();
        p.addTo(new Email(toEmail));
        p.addDynamicTemplateData(KEY_PATIENT_NAME, patientName);
        p.addDynamicTemplateData(KEY_DOCTOR_NAME,  doctorName);
        p.addDynamicTemplateData(KEY_DESCRIPTION,  description);
        sendTemplateEmail(TEMPLATE_NEW_ACTIVITY, p);
    }

    private static Personalization buildPersonalizationForPatient(String toEmail,
                                                                  AppointmentResponseBean booking) {
        Personalization p = new Personalization();
        p.addTo(new Email(toEmail));
        p.addDynamicTemplateData(KEY_PATIENT_NAME,        booking.getPatient().getFullName());
        p.addDynamicTemplateData(KEY_SPECIALIZATION_NAME, booking.getSpecialization().getName());
        p.addDynamicTemplateData(KEY_DOCTOR_NAME,         booking.getDoctor().getFullName());
        p.addDynamicTemplateData(KEY_DATE,                booking.getSlot().getDate().toString());
        p.addDynamicTemplateData(KEY_START_TIME,          booking.getSlot().getStartTime().toString());
        return p;
    }

    private static Personalization buildPersonalizationForDoctor(String toEmail,
                                                                 AppointmentResponseBean booking) {
        Personalization p = new Personalization();
        p.addTo(new Email(toEmail));
        p.addDynamicTemplateData(KEY_DOCTOR_NAME,         booking.getDoctor().getFullName());
        p.addDynamicTemplateData(KEY_PATIENT_NAME,        booking.getPatient() != null
                ? booking.getPatient().getFullName() : "");
        p.addDynamicTemplateData(KEY_SPECIALIZATION_NAME, booking.getSpecialization().getName());
        p.addDynamicTemplateData(KEY_DATE,                booking.getSlot().getDate().toString());
        p.addDynamicTemplateData(KEY_START_TIME,          booking.getSlot().getStartTime().toString());
        return p;
    }

    private static void sendTemplateEmail(String templateId,
                                          Personalization personalization) throws NotificationException {
        Mail mail = new Mail();
        mail.setFrom(new Email(FROM_EMAIL, "DoseGuard"));
        mail.setTemplateId(templateId);
        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 400) {
                throw new NotificationException(
                        "Errore invio email (status " + response.getStatusCode() +
                                "): " + response.getBody());
            }

        } catch (IOException e) {
            throw new NotificationException("Errore durante l'invio email: " + e.getMessage(), e);
        }
    }
}
