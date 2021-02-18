package holiday.entity;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class EventDates {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@DateTimeFormat (iso = DateTimeFormat.ISO.DATE)
	@Column(unique = true, nullable = false)
	private Calendar date;
	private String note;
	private Boolean isWorkDay;
	
	public EventDates() {}

	public EventDates(Calendar date, String note, Boolean isWorkDay) {
		this.date = date;
		this.note = note;
		this.isWorkDay = isWorkDay;
	}

	public Long getId() {
		return id;
	}

	public Calendar getDate() {
		return date;
	}


	public void setDate(Calendar Date) {
		this.date = Date;
	}


	public String getNote() {
		return note;
	}


	public void setNote(String note) {
		this.note = note;
	}


	public Boolean getIsWorkDay() {
		return isWorkDay;
	}


	public void setIsWorkDay(Boolean isWorkDay) {
		this.isWorkDay = isWorkDay;
	}



	@Override
	public String toString() {
		return "EventDates [id=" + id + ", Date=" + date.getTime() + ", note=" + note + ", isWorkDay=" + isWorkDay + "]";
	}
	
	

}
