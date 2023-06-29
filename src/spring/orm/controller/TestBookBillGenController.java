package spring.orm.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import spring.orm.contract.DiagnosticBillDAO;
import spring.orm.contract.PatientDAO;
import spring.orm.contract.TestDAO;
import spring.orm.model.TestModel;
import spring.orm.model.input.BillInputModel;
import spring.orm.model.output.patientsoutputmodel;
import spring.orm.model.output.testsCategoriesModel;
import spring.orm.services.TestServices;
import spring.orm.util.MailSendHelper;

@Controller
public class TestBookBillGenController {

	private TestServices testService;

	private DiagnosticBillDAO daignosticBillDAO;

	private TestDAO testDAO;

	private HttpSession httpSession;

	private PatientDAO patientDAO;

	@Autowired
	public TestBookBillGenController(TestServices testService, DiagnosticBillDAO daignosticBillDAO, TestDAO testDAO,
			HttpSession httpSession, PatientDAO patientDAO) {
		super();
		this.testService = testService;
		this.daignosticBillDAO = daignosticBillDAO;
		this.testDAO = testDAO;
		this.httpSession = httpSession;
		this.patientDAO = patientDAO;
	}

	@RequestMapping("/dcadmin/booktest")
	public String GetCat(Model model) {
		return "dcadmin/booktest";
	}

	@GetMapping("/dcadmin/gettestcat")
	public @ResponseBody ResponseEntity<String> GetCategories(Model model) {

		List<testsCategoriesModel> lc = testDAO.getCategories();

		System.out.println("*********************" + lc);

		return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(lc));

	}

	@GetMapping("/dcadmin/getpatients")
	public @ResponseBody ResponseEntity<String> getPatients(Model model) {

		List<patientsoutputmodel> lc = testDAO.getPatients();

		System.out.println("*********************" + lc);

		return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(lc));

	}
	// Get list of test Categories and patients from the respective DAOs - TestDAO and PatientDAO

	@RequestMapping(value = "/dcadmin/gettestbycat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getTestByCategory(@RequestParam String cat, Model model) {

		List<TestModel> test = testDAO.getTestByCategory(cat);

		return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(test));

	}

	// This method is responsible for booking a test and storing the information provided in the BillInputModel object
	// to database.
	@RequestMapping(value = "/dcadmin/bookdctest", method = RequestMethod.POST)
	public void BookTest(Model model, @ModelAttribute BillInputModel bi) {

		daignosticBillDAO.bookDcTest(bi);

	}

	@RequestMapping(value = "/dcadmin/gettestprice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getTestPrice(@RequestParam int test, Model model) {
		System.out.println("inside  price testcat");
		Object price = testDAO.getSelectedTestPrice(test);

		return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(price));

	}

	// This method retrieves the price of a test based on the provided test ID from TestDAO gettestprice method.
	// The method receives the test ID as a request parameter and the Model object for rendering the view.

	@RequestMapping(value = "/dcadmin/storedb", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> storeToDatabase(Model model, @RequestParam int patient) {
		System.out.println("inside  price testcat");
		int billid = daignosticBillDAO.storeToDatabase(patient);

		return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(billid));
	}

	// Invokes the gettotalbills() method from the DiagnosticBillDAO dbs object to retrieve the total bills for the
	// specified patient.

	@RequestMapping(value = "/dcadmin/totalbills", method = RequestMethod.GET)
	public ResponseEntity<String> totalBills(@RequestParam int patient, Model model) {
		System.out.println("in book");
		List<Object> lb = daignosticBillDAO.getTotalBills(patient);

		return ResponseEntity.status(HttpStatus.OK).body(new Gson().toJson(lb));

	}

	// Calls the sendEmail1() method from the MailSend class to send the email with the provided parameters.
	@RequestMapping(value = "/dcadmin/mailsend2", method = RequestMethod.POST)
	public @ResponseBody void mailSend(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String email, @RequestParam String content) {

		try {
			// MailSend.sendEmailTestBooking(request, response, email, content);
			MailSendHelper.sendEmailTestBooking(request, response, email, content);

		} catch (Exception e) {
			// Catches any exception that occurs during the email sending process and prints the stack trace.
			// MessagingException
			// AuthenticationFailedException
			// SendFailedException
			// SMTPException
			e.printStackTrace();
		}

	}

}