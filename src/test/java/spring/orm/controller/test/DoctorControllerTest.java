package test.java.spring.orm.controller.test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import spring.orm.contract.DocScheduleDAO;
import spring.orm.contract.DoctorsDAO;
import spring.orm.contract.SpecializationDAO;
import spring.orm.controller.DoctorController;
import spring.orm.customexceptions.InvalidWeekdayException;
import spring.orm.model.Specialization;
import spring.orm.model.entity.DoctorTemp;
import spring.orm.model.input.DoctorInput;
import spring.orm.model.input.DoctorUpdateModel;
import spring.orm.model.output.DoctorOutPutModel;
import spring.orm.services.DoctorOutputService;

public class DoctorControllerTest {

	@Mock
	private DoctorsDAO doctorDAO;

	@Mock
	private SpecializationDAO specializationDAO;

	@Mock
	private DoctorOutputService doctorOutputService;

	@Mock
	private DocScheduleDAO doctorScheduleDAO;

	@InjectMocks
	private DoctorController doctorController;

	@BeforeMethod
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		// Create a mock instance of the DoctorDAO
		doctorDAO = Mockito.mock(DoctorsDAO.class);

		// Inject the mock DoctorDAO into the DoctorController
		doctorController = new DoctorController(doctorDAO, specializationDAO, doctorOutputService, doctorScheduleDAO);
	}

	@Test
	public void testSaveDoctor() throws InvalidWeekdayException, FileNotFoundException {
		// Mock input data
		DoctorInput doctorInputModel = new DoctorInput();
		CommonsMultipartFile docPhoto = mock(CommonsMultipartFile.class);
		Model model = mock(Model.class);

		// Mock dependencies
		int docId = 17;
		when(doctorOutputService.addDoc(eq(doctorInputModel), eq(docPhoto))).thenReturn(docId);

		// Call the controller method
		String result = doctorController.saveDoctor(doctorInputModel, docPhoto, model);

		// Verify the interactions and assertions
		verify(doctorScheduleDAO).addDoctorSchedule(eq(doctorInputModel), eq(docId));
		verify(model, never()).addAttribute(eq("error"), anyString());
		assertEquals(result, "admin/redirect");
	}

	@Test
	public void testGetDoctorpage() {
		// Mock dependencies
		List<DoctorOutPutModel> mockList = Collections.singletonList(mock(DoctorOutPutModel.class));
		when(doctorDAO.getallDocSchedule()).thenReturn(mockList);
		when(specializationDAO.getAllSpecializations())
				.thenReturn(Collections.singletonList(mock(Specialization.class)));

		Model model = mock(Model.class);

		// Call the controller method
		String result = doctorController.getDoctorpage(model);

		// Verify the interactions and assertions
		verify(model, never()).addAttribute(eq("error"), anyString());
		assertEquals(result, "admin/doctor");
	}

	@Test
	public void testUpdateDoctor() throws IOException, InvalidWeekdayException, FileNotFoundException {
		// Mock input data
		DoctorUpdateModel doctorUpdate = new DoctorUpdateModel();
		CommonsMultipartFile docPhoto = mock(CommonsMultipartFile.class);
		Model model = mock(Model.class);

		// Mock dependencies
		int doctorId = 1;
		when(doctorOutputService.updateDoctor(eq(doctorUpdate), eq(docPhoto))).thenReturn(doctorId);

		// Call the controller method
		String result = doctorController.updateDoctor(doctorUpdate, docPhoto, model);

		// Verify the interactions and assertions
		verify(model, never()).addAttribute(eq("error"), anyString());
		assertEquals(result, "redirect:/admin/doctors");
	}

	@Test
	public void testGetDoctor() throws NullPointerException {
		// Mock input data
		int doctorId = 1;

		// Mock dependencies
		DoctorTemp doctor = new DoctorTemp();
		when(doctorDAO.getDoctor(eq(doctorId))).thenReturn(doctor);

		// Call the controller method
		DoctorTemp result = doctorController.getDoctor(doctorId);

		// Verify the interactions and assertions
		assertEquals(result, doctor);
	}

	@Test
	public void testHandleInvalidWeekdayException() {
		// Mock exception
		InvalidWeekdayException exception = mock(InvalidWeekdayException.class);

		// Call the exception handler method
		ResponseEntity<?> response = doctorController.handleInvalidWeekdayException(exception);

		// Verify the interactions and assertions
		assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
		assertEquals(response.getBody(), exception.getMessage());
	}
}
