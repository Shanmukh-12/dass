package spring.orm.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import spring.orm.contract.AdminDAO;
import spring.orm.model.output.OutputDoctorProfit;
import spring.orm.model.output.OutputSpecializationProfit;

@Component
public class AdminImplDAO implements AdminDAO {
	@PersistenceContext
	EntityManager entityManager;
	private static final Logger logger = LoggerFactory.getLogger(AdminImplDAO.class);

	/**
	 * Fetches the profits generated by doctors. return A list of OutputDoctorProfit containing the doctor name and the
	 * corresponding profit.
	 */
	@Override
	public List<OutputDoctorProfit> fetchDoctorProfit() {
		logger.info("Inside Fetch Doctor Profit Method");
		// Constructing the HQL query to fetch doctor wise profits
		logger.info("Query to get Doctor Wise Profit");
		String hql = "SELECT new spring.orm.model.output.OutputDoctorProfit(d.doctName, SUM(a.appn_payamount) AS Double) FROM DoctorTemp d ,AppointmentEntity a where a.doctor.doctId=d.doctId GROUP BY d.doctName";

		// Executing the query and retrieving the results
		List<OutputDoctorProfit> outputDoctorProfitList = entityManager.createQuery(hql, OutputDoctorProfit.class)
				.getResultList();

		// Returning the fetched doctor profit data
		return outputDoctorProfitList;
	}

	/**
	 * Fetches the profits generated by specializations. returns a list of OutputSpecializationProfit containing the
	 * specialization ID, title, and the corresponding profit.
	 */
	@Override
	public List<OutputSpecializationProfit> fetchSpecializationProfit() {
		logger.info("Inside Fetch Specialization wise  Profit Method");
		// Constructing the HQL query to fetch specialization profits
		logger.info("Query to get Specialization Wise Profit");
		String hql = "SELECT new spring.orm.model.output.OutputSpecializationProfit(s.id, s.title, SUM(a.appn_payamount)) FROM DoctorTemp d, Specialization s, AppointmentEntity a where s.id=d.specialization.id and a.doctor.doctId=d.doctId group by s.id, s.title";

		// Executing the query and retrieving the results
		List<OutputSpecializationProfit> outputSpecializationProfitList = entityManager
				.createQuery(hql, OutputSpecializationProfit.class).getResultList();

		// Returning the fetched specialization profit data
		return outputSpecializationProfitList;
	}

	/**
	 * Fetches the profits generated by doctors within a specified date range. 'from' The starting date of the range
	 * (format: yyyy-MM-dd). 'to' The ending date of the range (format: yyyy-MM-dd). returns A list of
	 * OutputDoctorProfit containing the doctor name and the corresponding profit.
	 */
	@Override
	public List<OutputDoctorProfit> fetchDoctorProfit(String from, String to) {
		logger.info("Inside Fetch Doctor Profit between dates Method");
		// Constructing the HQL query to fetch doctor profits within a specified date range
		logger.info("Query to get Doctor Wise Profit between dates ");
		String hql = "SELECT new spring.orm.model.output.OutputDoctorProfit(d.doctName, SUM(a.appn_payamount) AS Double) FROM DoctorTemp d, AppointmentEntity a where a.doctor.doctId=d.doctId and a.appn_sch_date >= :fromDate AND a.appn_sch_date <= :toDate GROUP BY d.doctName";

		// Parsing the date strings into Date objects
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date fromDate = null;
		Date toDate = null;

		try {
			fromDate = dateFormat.parse(from);
			toDate = dateFormat.parse(to);
			logger.info("from Date" + " " + fromDate);
			logger.info("To Date" + " " + toDate);
		} catch (ParseException e) {
			// Handle the exception if the date strings are not in the correct format
			// For example, log an error message or notify the user about the incorrect date format
			System.err.println("Invalid date format. Please provide dates in the format yyyy-MM-dd.");
			e.printStackTrace();
			// Perform any additional error handling steps as needed
		}

		// Executing the query with the specified date parameters and retrieving the results
		List<OutputDoctorProfit> outputDoctorProfitList = entityManager.createQuery(hql, OutputDoctorProfit.class)
				.setParameter("fromDate", fromDate).setParameter("toDate", toDate).getResultList();

		// Returning the fetched doctor profit data within the specified date range
		return outputDoctorProfitList;
	}

	/**
	 * Fetches the profits generated by specializations within a specified date range. from The starting date of the
	 * range (format: yyyy-MM-dd). to The ending date of the range (format: yyyy-MM-dd). returns a list of
	 * OutputSpecializationProfit containing the specialization ID, title, and the corresponding profit.
	 */
	@Override
	public List<OutputSpecializationProfit> fetchSpecializationProfit(String from, String to) {
		logger.info("Inside Fetch Specialization wise  Profit Method between dates");
		// Constructing the HQL query to fetch specialization profits within a specified date range
		logger.info("Query to get Specialization Wise Profit between dates");

		String hql = "SELECT new spring.orm.model.output.OutputSpecializationProfit(s.id, s.title, SUM(a.appn_payamount)) FROM DoctorTemp d, Specialization s, AppointmentEntity a where s.id=d.specialization.id and a.doctor.doctId=d.doctId and a.appn_sch_date >= :fromDate AND a.appn_sch_date <= :toDate group by s.id, s.title";

		// Parsing the date strings into Date objects
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date fromDate = null;
		Date toDate = null;

		try {
			fromDate = dateFormat.parse(from);
			toDate = dateFormat.parse(to);
			logger.info("from Date" + " " + fromDate);
			logger.info("To Date" + " " + toDate);
		} catch (ParseException e) {
			// Handle the exception if the date strings are not in the correct format
			// For example, log an error message or notify the user about the incorrect date format
		}

		// Executing the query with the specified date parameters and retrieving the results
		List<OutputSpecializationProfit> outputSpecializationProfitList = entityManager
				.createQuery(hql, OutputSpecializationProfit.class).setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate).getResultList();

		// Returning the fetched specialization profit data within the specified date range
		return outputSpecializationProfitList;
	}

}