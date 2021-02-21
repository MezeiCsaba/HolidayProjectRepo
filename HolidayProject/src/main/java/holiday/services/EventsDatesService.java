package holiday.services;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import holiday.entity.EventDates;
import holiday.repository.EventsDateRepository;

@Service
public class EventsDatesService {
	
	private EventsDateRepository eventsDayRepo;

	@Autowired
	public void setEventsDayRepo(EventsDateRepository eventsDayRepo) {
		this.eventsDayRepo = eventsDayRepo;
	}
	
	public Page<EventDates> getAllEvents(Pageable pageable){
		return eventsDayRepo.findAllByOrderByDate(pageable);
	}
	
	public EventDates findById(Long id) {
		return eventsDayRepo.findAllById(id);
	}
	
	public void deleteEventById(Long id) {
		eventsDayRepo.deleteById(id);
	}
	
	public void addNewExceptionEvent(EventDates event) {
		eventsDayRepo.save(event);
	}
	

	public Long isExceptionEventAlreadyExist(EventDates event) {
		
		Long ids = -1L;
		List<EventDates> events = getAllEvents();
		for (EventDates anevent: events) {
			anevent.getDate().set(Calendar.HOUR_OF_DAY, 0);
			event.getDate().set(Calendar.HOUR_OF_DAY, 0);
			if(event.getDate().getTime().compareTo(anevent.getDate().getTime())==0) {
				ids=anevent.getId();
				return ids;
			}
		}
		return ids;
	}

	
	public List<EventDates> getAllEvents() {
		return eventsDayRepo.findAllByOrderByDate();
	}


	
	
	

//	@PostConstruct
	public void init() throws ParseException {
		
		
		Calendar  date = new GregorianCalendar(2021, 00, 01);
		EventDates eventd=new EventDates(date, "Újév", false);
		eventsDayRepo.save(eventd);
		date = new GregorianCalendar(2021, 02, 15);
		eventd=new EventDates(date, "nemzeti ünnep", false);
		eventsDayRepo.save(eventd);
		date = new GregorianCalendar(2021, 03, 02); 
		eventd=new EventDates(date, "Nagypéntek", false);
		eventsDayRepo.save(eventd);
		date = new GregorianCalendar(2021, 03, 05); 
		eventd=new EventDates(date, "húsvét hétfő", false);
		eventsDayRepo.save(eventd);
		date = new GregorianCalendar(2021, 04, 01); 
		eventd=new EventDates(date, "munka ünnepe", false);
		eventsDayRepo.save(eventd);
		date = new GregorianCalendar(2021, 04, 24); 
		eventd=new EventDates(date, "Pünkösd hétfő", false);
		eventsDayRepo.save(eventd);
		date = new GregorianCalendar(2021, 07, 20); 
		eventd=new EventDates(date, "Államalapítás ünnepe", false);
		eventsDayRepo.save(eventd);
		date = new GregorianCalendar(2021, 9, 23); 
		eventd=new EventDates(date, "56-os forradalom", false);
		eventsDayRepo.save(eventd);
		date = new GregorianCalendar(2021,10, 01); 
		eventd=new EventDates(date, "Mindenszentek", false);
		eventsDayRepo.save(eventd);
		date = new GregorianCalendar(2021, 11, 11); 
		eventd=new EventDates(date, "Munakanap áthelyezés(12.24)", true);
		eventsDayRepo.save(eventd);
		date = new GregorianCalendar(2021,11, 24); 
		eventd=new EventDates(date, "Szeneste", false);
		eventsDayRepo.save(eventd);
		date = new GregorianCalendar(2021, 11, 25);
		eventd=new EventDates(date, "Karácson első napja", false);
		eventsDayRepo.save(eventd);
		date = new GregorianCalendar(2021, 11, 26);
		eventd=new EventDates(date, "Karácson második napja", false);
		eventsDayRepo.save(eventd);
		
		
	}



}
