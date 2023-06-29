package spring.orm.controller;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import spring.orm.contract.DocScheduleDAO;
import spring.orm.contract.DoctorsDAO;
import spring.orm.contract.SpecializationDAO;
import spring.orm.customexceptions.InvalidWeekdayException;
import spring.orm.model.entity.DoctorTemp;
import spring.orm.model.input.DoctorInput;
import spring.orm.model.input.DoctorUpdateModel;
import spring.orm.services.DoctorOutputService;

@Controller

public class DoctorController {

	// Injects the respective class objects

	private DoctorsDAO doctorDAO;

	private SpecializationDAO specializationDAO;

	private DoctorOutputService doctorOutputService;

	private DocScheduleDAO doctorScheduleDAO;

	private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

	@Autowired
	public DoctorController(DoctorsDAO doctorDAO, SpecializationDAO specializationDAO,
			DoctorOutputService doctorOutputService, DocScheduleDAO doctorScheduleDAO) {
		super();
		this.doctorDAO = doctorDAO;
		this.specializationDAO = specializationDAO;
		this.doctorOutputService = doctorOutputService;
		this.doctorScheduleDAO = doctorScheduleDAO;
	}

	@RequestMapping(value = "admin/savedoc", method = RequestMethod.POST)
	public String saveDoctor(@ModelAttribute DoctorInput doctorInputModel, @RequestParam CommonsMultipartFile docphoto,
			Model model) throws InvalidWeekdayException, FileNotFoundException {
		/*
		 * This method maps the "/admin/savedoc" URL to handle a POST request for saving doctor information.
		 */

		try {
			int docid = doctorOutputService.addDoc(doctorInputModel, docphoto);
			doctorScheduleDAO.addDoctorSchedule(doctorInputModel, docid);// Add the doctor's schedule information to the
																			// database
			logger.info("Doctor saved successfully. Doctor ID: {}", docid);
			return "admin/redirect";// Return the name of the view template for the redirection page
		} catch (Exception e) {
			logger.error("Invalid weekday provided: {}. Expected values are MON, TUE, WED, THU, FRI, SAT, SUN.",
					e.getMessage());
			logger.error("Error occurred while saving doctor information.", e);

			model.addAttribute("error",
					"Invalid weekday provided: " + e + ". Expected values are MON, TUE, WED, THU, FRI, SAT, SUN.");
			return "admin/error"; // Return the name of the error view template
		}

	}

	@RequestMapping(value = "admin/doctors", method = RequestMethod.GET)
	public String getDoctorpage(Model m) {
		/*
		 * This method maps the "/admin/doctors" URL to handle a GET request for displaying the doctor page.
		 */
		try {
			m.addAttribute("docsche", doctorDAO.getallDocSchedule());
			m.addAttribute("speclist", specializationDAO.getAllSpecializations());
			logger.info("Doctor page loaded successfully");
			return "admin/doctor"; // Return the name of the view template for the doctor page
		} catch (Exception e) {
			logger.error("Error occurred while loading doctor page", e);
			m.addAttribute("error", "An error occurred while loading doctor page.");
			return "admin/error"; // Return the name of the error view template
		}
	}

	@RequestMapping(value = "admin/updatedoc", method = RequestMethod.POST)
	public String updateDoctor(@ModelAttribute DoctorUpdateModel doctorUpdate,
			@RequestParam CommonsMultipartFile docphoto, Model model)
			throws IOException, InvalidWeekdayException, FileNotFoundException {
		/*
		 * This method maps the "/admin/updatedoc" URL to handle a POST request for updating a doctor's information.
		 */

		int id = doctorOutputService.updateDoctor(doctorUpdate, docphoto);
		logger.info("Doctor updated successfully. Doctor ID: {}", id);
		return "redirect:/admin/doctors";

	}

	@RequestMapping(value = "admin/getdoc", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	public @ResponseBody DoctorTemp getDoctor(@RequestParam("id") int id) throws NullPointerException {
		try {
			DoctorTemp docList = doctorDAO.getDoctor(id);
			logger.info("Doctor retrieved successfully. Doctor ID: {}", id);
			return docList;
		} catch (NullPointerException e) {
			logger.error("Failed to retrieve doctor information. Doctor ID: {}", id, e);
			throw e; // Rethrow the exception to be handled by the global exception handler
		}
	}

	@ExceptionHandler(InvalidWeekdayException.class)
	public ResponseEntity<?> handleInvalidWeekdayException(InvalidWeekdayException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}
}