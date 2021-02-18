package holiday.entity;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.format.annotation.DateTimeFormat;


@Entity
public class Event {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@DateTimeFormat (iso = DateTimeFormat.ISO.DATE)
	private Calendar startDate;
	@DateTimeFormat (iso = DateTimeFormat.ISO.DATE)
	private Calendar endDate;
	private Integer workDayLong; // munkanapokban mérve (év közben vátozhat (új ünnepnap miatt pl: 2019 Nagypéntek bevezetése év közben)!!)
	private Integer dayLong;	// naptári napokban mérve
	@ManyToOne
	private User user;
	private Byte approved; // -1: denied, 0: pending approval, 1: approved
	
	
	
	public Event() {}
		

	public Event(Calendar startDate, Calendar endDate, Integer workDayLong, Integer dayLong, User user) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.workDayLong = workDayLong;
		this.dayLong = dayLong;
		this.user = user;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public Integer getWorkDayLong() {
		return workDayLong;
	}

	public void setWorkDayLong(Integer workDayLong) {
		this.workDayLong = workDayLong;
	}

	public Integer getDayLong() {
		return dayLong;
	}

	public void setDayLong(Integer dayLong) {
		this.dayLong = dayLong;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Byte getApproved() {
		return approved;
	}


	public void setApproved(Byte approved) {
		this.approved = approved;
	}


	@Override
	public String toString() {
		return "Event [id=" + id + ", startDate="  + ", endDate="  + ", workDayLong=" + workDayLong
				+ ", dayLong=" + dayLong + ", user=" + user + ", approved=" + approved + "]";
	}




	
	
	
	

}
