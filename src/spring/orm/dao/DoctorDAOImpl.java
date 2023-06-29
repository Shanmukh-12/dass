package spring.orm.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import spring.orm.contract.DocScheduleDAO;
import spring.orm.contract.DoctorsDAO;
import spring.orm.model.DoctorSchedule;
import spring.orm.model.entity.DoctorTemp;
import spring.orm.model.output.DoctorList;
import spring.orm.model.output.DoctorOutPutModel;

@Repository
public class DoctorDAOImpl implements DoctorsDAO {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private DocScheduleDAO docschedule;

	private static final Logger logger = LoggerFactory.getLogger(DoctorDAOImpl.class);

	@Override
	@Transactional
	public Long findCount() {
		// This method retrieves the count of non-deleted doctors from the database.
		try {
			logger.debug("Retrieving count of non-deleted doctors");
			Long count = em.createQuery("select count(d) from DoctorTemp d where d.isDeleted = :status", Long.class)
					.setParameter("status", false).getSingleResult();
			logger.debug("Retrieved count of non-deleted doctors: {}", count);
			return count;
		} catch (Exception e) {
			logger.error("Error occurred while retrieving count of non-deleted doctors", e);
			throw new RuntimeException("Failed to retrieve count of non-deleted doctors", e);
		}
	}

	@Transactional
	@Override
	public List<DoctorTemp> getAllDoc() {
		// This method retrieves all doctors from the database.
		try {
			logger.debug("Retrieving all doctors");
			List<DoctorTemp> doctors = em.createQuery("select d from DoctorTemp d", DoctorTemp.class).getResultList();
			logger.debug("Retrieved {} doctors", doctors.size());
			return doctors;
		} catch (Exception e) {
			logger.error("Error occurred while retrieving all doctors", e);
			throw new RuntimeException("Failed to retrieve all doctors", e);
		}
	}

	@Transactional
	public List<DoctorTemp> getAllDocSpec(String Spec) {
		/*
		 * This method retrieves all doctors with a specific specialization from the database.
		 */
		try {
			List<DoctorTemp> doctors = em
					.createQuery("select d from Doctor d where d.specialization.id=:spec", DoctorTemp.class)
					.setParameter("spec", Spec).getResultList();
			logger.info("Retrieved {} doctors with specialization: {}", doctors.size(), Spec);
			return doctors;
		} catch (Exception e) {
			logger.error("Failed to retrieve doctors with specialization: {}", Spec, e);
			throw e; // Rethrow the exception to be handled by the global exception handler
		}
	}

	@Transactional
	public DoctorTemp getDoctor(int Id) {
		/*
		 * This method retrieves a specific doctor based on the provided ID from the database.
		 */
		try {
			DoctorTemp doctor = em.createQuery("select d from DoctorTemp d where d.doctId=:doct", DoctorTemp.class)
					.setParameter("doct", Id).getSingleResult();
			logger.info("Retrieved doctor with ID: {}", Id);
			return doctor;
		} catch (Exception e) {
			logger.error("Failed to retrieve doctor with ID: {}", Id, e);
			throw e; // Rethrow the exception to be handled by the global exception handler
		}
	}

	@Transactional
	@Override
	public void saveDoc(DoctorTemp s) {
		try {
			em.persist(s);
			logger.info("Saved doctor: {}", s);
		} catch (Exception e) {
			logger.error("Failed to save doctor: {}", s, e);
			throw e; // Rethrow the exception to be handled by the global exception handler
		}
	}

	@Transactional
	@Override
	public void updatedoc(DoctorTemp s) {
		try {
			em.merge(s);
			logger.info("Updated doctor: {}", s.getDoctId());
		} catch (Exception e) {
			logger.error("Failed to update doctor: {}", s.getDoctId(), e);
			throw e; // Rethrow the exception to be handled by the global exception handler
		}
	}

	@Override
	public List<DoctorList> getallDocScheduleBySpec(String spec, String like) {
		/*
		 * This method retrieves doctors' schedules based on a specific specialization and weekday pattern from the
		 * database.
		 */
		try {
			List<DoctorList> dout = em.createQuery(
					"select new spring.orm.model.output.DoctorList( d.doctId,d.doctName) from DoctorTemp d where (d.schedule.weekday ='ALL' or d.schedule.weekday like CONCAT('%', :like, '%')) and d.specialization.id=:spec and d.isDeleted = false ",
					DoctorList.class).setParameter("like", like).setParameter("spec", spec).getResultList();

			logger.info("Retrieved {} doctors' schedules with specialization: {} and weekday pattern: {}", dout.size(),
					spec, like);
			return dout;
		} catch (Exception e) {
			logger.error("Failed to retrieve doctors' schedules with specialization: {} and weekday pattern: {}", spec,
					like, e);
			throw e; // Rethrow the exception to be handled by the global exception handler
		}
	}

	@Override
	public DoctorOutPutModel getDocById(int id) {
		try {
			DoctorOutPutModel d = new DoctorOutPutModel();
			d.setD(em.find(DoctorTemp.class, id));
			d.setDocsche(docschedule.getSchedulebyId(d.getDoctId()));
			logger.info("Retrieved doctor with id: {}", id);
			return d;
		} catch (Exception e) {
			logger.error("Failed to retrieve doctor with id: {}", id, e);
			throw e; // Rethrow the exception to be handled by the global exception handler
		}
	}

	public List<DoctorOutPutModel> getallDocSchedule() {
		try {
			logger.debug("Retrieving all doctors with schedules");
			List<DoctorOutPutModel> doctorSchedules = new ArrayList<>();
			for (DoctorTemp d : em.createQuery("select d from DoctorTemp d where isDeleted = false", DoctorTemp.class)
					.getResultList()) {
				DoctorSchedule schedule = docschedule.getSchedulebyId(d.getDoctId());
				doctorSchedules.add(new DoctorOutPutModel(d, schedule));
			}
			logger.debug("Retrieved {} doctors with schedules", doctorSchedules.size());
			return doctorSchedules;
		} catch (Exception e) {
			logger.error("Error occurred while retrieving all doctors with schedules", e);
			throw new RuntimeException("Failed to retrieve all doctors with schedules", e);
		}
	}

	@Override
	public void deletedoc(int id) {
		try {
			logger.debug("Deleting doctor with ID: {}", id);
			DoctorTemp doctor = em.find(DoctorTemp.class, id);
			doctor.setDeleted(true);
			em.merge(doctor);
			logger.debug("Doctor with ID {} deleted successfully", id);
		} catch (Exception e) {
			logger.error("Error occurred while deleting doctor with ID: {}", id, e);
			throw new RuntimeException("Failed to delete doctor with ID: " + id, e);
		}
	}

}