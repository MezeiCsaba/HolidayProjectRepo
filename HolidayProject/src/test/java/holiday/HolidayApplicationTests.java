package holiday;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import holiday.services.EventService;

@SpringBootTest
class HolidayApplicationTests {

	EventService eventService;
	
	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}
	
	
	@Test
	void WorkDayLongTest() {
		Calendar date1 = new GregorianCalendar(2021, 02, 12);
		Calendar date2 = new GregorianCalendar(2021, 02, 21);
		Integer expected=5; // 2021-ben 03.12 -21. közt 5 munkanap van)
		Integer result = eventService.howWorkDayLong(date1,date2);
		assertEquals(expected, result);
	}

}
