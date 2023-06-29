package spring.orm.services;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spring.orm.contract.AppointmentDAO;
import spring.orm.contract.DoctorsDAO;
import spring.orm.contract.PatientDAO;
import spring.orm.customexceptions.SlotAlreadyBookedException;
import spring.orm.model.PatientModel;
import spring.orm.model.entity.AppointmentEntity;
import spring.orm.model.input.AppointmentForm;
import spring.orm.model.input.RescheduleAppointmentModel;
import spring.orm.model.output.AppOutFormFamily;
import spring.orm.model.output.MailAppOutputModel;
import spring.orm.model.output.RescheduleAppointmentOutput;
import spring.orm.util.MailSendHelper;

@Component
public class AppointmentService {

	private AppointmentDAO appointmentDAO;

	private DoctorsDAO doctorDAO;

	private PatientDAO patientDAO;
	private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

	@Autowired
	public AppointmentService(AppointmentDAO apdao, DoctorsDAO docdao, PatientDAO patdao) {
		this.appointmentDAO = apdao;
		this.doctorDAO = docdao;
		this.patientDAO = patdao;
	}

	public List<AppointmentEntity> getAllAppointments() {
		// Retrieve all appointments
		List<AppointmentEntity> appointmentlist = appointmentDAO.getAllAppointments();
		for (AppointmentEntity appointment : appointmentlist) {
			// Format date
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			appointment.setDateFormetted(dateFormat.format(appointment.getAppn_sch_date()));

			// Format time
			SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
			appointment.setTimeFormetted(timeFormat.format(appointment.getAppn_sch_date()));
		}
		// Log the number of appointments fetched
		logger.info("Fetched {} appointments", appointmentlist.size());

		return appointmentlist;
	}

	public int bookAppointment(AppointmentForm app) throws SlotAlreadyBookedException {
		// Book an appointment
		logger.info("Booking an appointment");
		DateTimeFormatter sqlformat = DateTimeFormatter.ofPattern("HH:mm:ss");
		logger.info("Appointment time: {}",
				LocalTime.parse(app.getSlots(), DateTimeFormatter.ofPattern("hh:mm a")).format(sqlformat));
		int app_id = appointmentDAO.bookAppointment(patientDAO.getPatientById(app.getExistingPatientid()),
				doctorDAO.getDoctor(app.getDoctor()),
				app.getAppointmentDate().toString() + " "
						+ LocalTime.parse(app.getSlots(), DateTimeFormatter.ofPattern("hh:mm a")).format(sqlformat),
				app.getAppnrefer(), app.getAppnfee());
		logger.info("Appointment booked with ID: {}", app_id);

		return app_id;
	}

	public RescheduleAppointmentOutput getAppointmentByIdOutput(int app_id) {
		// Log method entry
		logger.info("Getting appointment details by ID for rescheduling");

		// Create a new RescheduleAppointmentOutput object
		RescheduleAppointmentOutput rescheduleAppointment = new RescheduleAppointmentOutput();

		// Retrieve appointment details by ID
		AppointmentEntity a = appointmentDAO.getAppointmentById(app_id);

		// Set the details in the RescheduleAppointmentOutput object
		rescheduleAppointment.setApp_id(a.getAppn_id());
		rescheduleAppointment.setSlot(a.getAppn_sch_date().toLocalDateTime().toLocalTime());
		rescheduleAppointment.setApp_sch_date(Date.valueOf(a.getAppn_sch_date().toLocalDateTime().toLocalDate()));
		rescheduleAppointment.setDoctor(a.getDoctor());
		rescheduleAppointment.setPatient(a.getPm());

		// Log the appointment details retrieved
		logger.info("Appointment ID: {}", rescheduleAppointment.getApp_id());
		logger.info("Slot: {}", rescheduleAppointment.getSlot());
		logger.info("Appointment Schedule Date: {}", rescheduleAppointment.getApp_sch_date());
		logger.info("Doctor: {}", rescheduleAppointment.getDoctor());
		logger.info("Patient: {}", rescheduleAppointment.getPatient());

		return rescheduleAppointment;
	}

	public int bookAppointmentWithNewPatient(AppointmentForm appointmentForm) throws SlotAlreadyBookedException {
		// Book an appointment with a new patient
		PatientModel patient = new PatientModel();
		patient.setPatn_name(appointmentForm.getNewPatientName());
		patient.setPatn_age(appointmentForm.getNewPatientAge());
		patient.setPatn_bgroup(appointmentForm.getNewPatientBgroup());
		patient.setPatn_rdate(LocalDate.now());
		patient.setPatn_gender(appointmentForm.getNewPatientGender());

		patientDAO.addNewPatient(patient);
		DateTimeFormatter sqlformat = DateTimeFormatter.ofPattern("HH:mm:ss");
		logger.info("Booking appointment for new patient");
		logger.info("Doctor: {}", appointmentForm.getDoctor());
		logger.info("Appointment Date: {}", appointmentForm.getAppointmentDate().toString());
		logger.info("Slots: {}",
				LocalTime.parse(appointmentForm.getSlots(), DateTimeFormatter.ofPattern("hh:mm a")).format(sqlformat));
		logger.info("Referral: {}", appointmentForm.getAppnrefer());
		logger.info("Fee: {}", appointmentForm.getAppnfee());
		int app_id = appointmentDAO.bookAppointment(patient, doctorDAO.getDoctor(appointmentForm.getDoctor()),
				appointmentForm.getAppointmentDate().toString() + " " + LocalTime
						.parse(appointmentForm.getSlots(), DateTimeFormatter.ofPattern("hh:mm a")).format(sqlformat),
				appointmentForm.getAppnrefer(), appointmentForm.getAppnfee());
		logger.info("Appointment booked with ID: {}", app_id);
		return app_id;
	}

	public void cancelAppointment(int app_id) {
		// Cancel an appointment
		logger.info("Canceling appointment with ID: {}", app_id);

		// Cancel the appointment with the specified ID
		appointmentDAO.cancelAppointment(app_id);

		// Log cancellation confirmation
		logger.info("Appointment with ID {} canceled", app_id);
	}

	public MailAppOutputModel getAppointmentByID(int app_id) {
		// Get appointment details by ID
		AppointmentEntity appointment = appointmentDAO.getAppointmentById(app_id);
		MailAppOutputModel appointmentMailOutput = new MailAppOutputModel();
		appointmentMailOutput.setAppn_id(appointment.getAppn_id());
		appointmentMailOutput.setDoc_name(appointment.getDoctor().getDoctName());
		appointmentMailOutput.setAppn_booked_Date(appointment.getAppn_booked_Date());
		appointmentMailOutput.setAppn_sch_date(appointment.getAppn_sch_date());
		appointmentMailOutput.setAppn_payamount(appointment.getAppn_payamount());
		appointmentMailOutput.setAppn_paymode(appointment.getAppn_paymode());
		appointmentMailOutput.setDoc_Photo(appointment.getDoctor().getDoctPhoto());
		appointmentMailOutput.setAppn_payreference(appointment.getAppn_payreference());
		appointmentMailOutput.setPat_name(appointment.getPm().getPatn_name());
		String mail = "";
		if (appointment.getPm().getAccessPatientId() != null) {
			logger.info("Using AccessPatient for email retrieval");
			// if patient have accesspatient id then it fetch mail from the patient user
			mail = appointment.getPm().getAccessPatient().getUserPass().getMail();
		}
		if (appointment.getPm().getUserPass() != null) {
			// if the booked apppointment is for user patient then it fetch directly from
			// the userpass model
			logger.info("Using UserPass for email retrieval");
			mail = appointment.getPm().getUserPass().getMail();
		}
		logger.info("Appointment ID: {}", appointmentMailOutput.getAppn_id());
		logger.info("Doctor Name: {}", appointmentMailOutput.getDoc_name());
		logger.info("Appointment Booked Date: {}", appointmentMailOutput.getAppn_booked_Date());
		logger.info("Appointment Schedule Date: {}", appointmentMailOutput.getAppn_sch_date());
		logger.info("Payment Amount: {}", appointmentMailOutput.getAppn_payamount());
		logger.info("Payment Mode: {}", appointmentMailOutput.getAppn_paymode());
		logger.info("Doctor Photo: {}", appointmentMailOutput.getDoc_Photo());
		logger.info("Payment Reference: {}", appointmentMailOutput.getAppn_payreference());
		logger.info("Patient Name: {}", appointmentMailOutput.getPat_name());
		logger.info("Email: {}", appointmentMailOutput.getMail());

		appointmentMailOutput.setMail(mail);
		return appointmentMailOutput;

	}

	@Transactional
	public void reschduleAppointment(RescheduleAppointmentModel rm) {
		logger.info("Rescheduling appointment");

		// Reschedule the appointment using the provided RescheduleAppointmentModel
		appointmentDAO.reschduleAppointment(rm);

		// Log rescheduling confirmation
		logger.info("Appointment rescheduled");
	}

	public int bookAppointment(AppointmentForm appointment, int patientId) throws SlotAlreadyBookedException {
		// Book an appointment with a specified patient ID
		DateTimeFormatter sqlFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
		logger.info("Booking an appointment with a specified patient ID");

		System.out.println(
				LocalTime.parse(appointment.getSlots(), DateTimeFormatter.ofPattern("hh:mm a")).format(sqlFormat));

		int appointmentId = appointmentDAO.bookAppointment(patientDAO.getPatientById(patientId),
				doctorDAO.getDoctor(appointment.getDoctor()),
				appointment.getAppointmentDate().toString() + " " + LocalTime
						.parse(appointment.getSlots(), DateTimeFormatter.ofPattern("hh:mm a")).format(sqlFormat),
				appointment.getAppnrefer(), appointment.getAppnfee());
		logger.info("Booked appointment ID: {}", appointmentId);

		return appointmentId;
	}

	public List<AppointmentEntity> getPatientAppointmentByID(int patn_id) {
		// Get all appointments of a patient by their ID
		List<AppointmentEntity> alist = appointmentDAO.getAppointmentsByPatientId(patn_id);
		return alist;
	}

	public List<AppOutFormFamily> getFormFamily(int id) {
		// Get family members for form filling
		List<AppOutFormFamily> r = new ArrayList<>();
		for (PatientModel patient : patientDAO.getFamilyDetailsById(id)) {
			AppOutFormFamily form = new AppOutFormFamily();
			form.setFam_patn_name(patient.getPatn_name());
			form.setFam_patni_id(patient.getPatn_id());
			r.add(form);

		}
		return r;
	}

	public void sendBookingMail(HttpServletRequest request, HttpServletResponse response, int app_id) {
		logger.info("Sending booking email");

		// Get the user's email address
		String userMail = getAppointmentByID(app_id).getMail();

		if (!userMail.equals("")) {
			try {
				// Send the booking email
				// MailSend.sendBookingEmail(request, response, getAppointmentByID(app_id), userMail);
				MailSendHelper.sendBookingEmail(request, response, getAppointmentByID(app_id), userMail);

				// Log successful email sending
				logger.info("Booking email sent to: {}", userMail);
			} catch (Exception e) {
				// Log the exception
				logger.error("Error sending booking email", e);
			}
		} else {
			// Log that the user's email is empty
			logger.warn("User's email address is empty");
		}

	}
}