package spring.orm.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import spring.orm.model.DoctorSchedule;
import spring.orm.model.Specialization;

@Entity
@Table(name = "ma_doctors")
public class DoctorTemp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "doct_id")
	private int doctId;

	@Column(name = "doct_name")
	private String doctName;

	@Column(name = "doct_qual")
	private String doctQual;

	@ManyToOne
	@JoinColumn(name = "doct_spec_id")
	private Specialization specialization;

	@Column(name = "doct_exp")
	private int doctExp;

	@Column(name = "doct_photo")
	private byte[] doctPhoto;

	@Column(name = "doct_cfee")
	private Double doctCfee;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@OneToOne(mappedBy = "doctor")
	private DoctorSchedule schedule;

	public Integer getDoctId() {
		return doctId;
	}

	public void setDoctId(Integer doctId) {
		this.doctId = doctId;
	}

	public String getDoctName() {
		return doctName;
	}

	public void setDoctName(String doctName) {
		this.doctName = doctName;
	}

	public String getDoctQual() {
		return doctQual;
	}

	public void setDoctQual(String doctQual) {
		this.doctQual = doctQual;
	}

	public Specialization getSpecialization() {
		return specialization;
	}

	public void setSpecialization(Specialization specialization) {
		this.specialization = specialization;
	}

	public int getDoctExp() {
		return doctExp;
	}

	public void setDoctExp(int docexp) {
		this.doctExp = docexp;
	}

	public byte[] getDoctPhoto() {
		return doctPhoto;
	}

	public void setDoctPhoto(byte[] doctPhoto) {
		this.doctPhoto = doctPhoto;

	}

	public Double getDoctCfee() {
		return doctCfee;
	}

	public void setDoctCfee(Double doctCfee) {
		this.doctCfee = doctCfee;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public DoctorTemp(Integer doctId, String doctName, String doctQual, Specialization specialization, int doctExp,
			byte[] doctPhoto, Double doctCfee, boolean isDeleted) {
		super();
		this.doctId = doctId;
		this.doctName = doctName;
		this.doctQual = doctQual;
		this.specialization = specialization;
		this.doctExp = doctExp;
		this.doctPhoto = doctPhoto;
		this.doctCfee = doctCfee;
		this.isDeleted = isDeleted;

	}

	@Override
	public String toString() {
		return "DoctorTemp [doctId=" + doctId + ", doctName=" + doctName + ", doctQual=" + doctQual
				+ ", specialization=" + specialization + ", doctExp=" + doctExp + ", doctPhoto=" + ", doctCfee="
				+ doctCfee + ", isDeleted=" + isDeleted + "]";
	}

	public DoctorTemp() {
	}

	public DoctorSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(DoctorSchedule schedule) {
		this.schedule = schedule;
	}

	public void setDoctId(int doctId) {
		this.doctId = doctId;
	}

}
