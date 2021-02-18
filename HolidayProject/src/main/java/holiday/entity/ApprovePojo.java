package holiday.entity;

import java.util.Calendar;
import java.util.List;

public class ApprovePojo {
	
	private Long eventId;
	private Long userId;
	private String userName;
	private String userEmail;
	private Calendar startDate;
	private Calendar endDate;
	private Integer workDayLong;
	private Byte approved; // -1: denied, 0: pending approval, 1: approved

	public ApprovePojo() {
		
	}

	public ApprovePojo(Long eventId, Long userId, String userName, String userEmail, Calendar startDate,
			Calendar endDate, Integer workDayLong, byte approved) {
		
		this.eventId = eventId;
		this.userId = userId;
		this.userName = userName;
		this.userEmail = userEmail;
		this.startDate = startDate;
		this.endDate = endDate;
		this.workDayLong = workDayLong;
		this.approved = approved;
	}

	
	
	

	public ApprovePojo(String userName,  Calendar startDate, Calendar endDate, Integer workDayLong,
			Byte approved) {
		this.userName = userName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.workDayLong = workDayLong;
		this.approved = approved;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
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

	public Byte getApproved() {
		return approved;
	}

	public void setApproved(Byte approved) {
		this.approved = approved;
	}

	@Override
	public String toString() {
		return "ApprovePojo [eventId=" + eventId + ", userId=" + userId + ", userName=" + userName + ", userEmail="
				+ userEmail + ", startDate=" + startDate + ", endDate=" + endDate + ", workDayLong=" + workDayLong
				+ ", approved=" + approved + "]";
	}

	
	

}
