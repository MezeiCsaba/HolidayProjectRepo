package holiday.services;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import holiday.entity.AppPojoList;
import holiday.entity.ApprovePojo;
import holiday.entity.Event;
import holiday.entity.EventDates;
import holiday.entity.User;
import holiday.repository.EventRepository;
import holiday.repository.EventsDateRepository;
import holiday.repository.UserRepository;

@Service
public class EventService {

	private final static String USER_ROLE = "${roles.USER_ROLE}";
	private final static String ADMIN_ROLE = "${roles.ADMIN_ROLE}";
	private final static String HR_ROLE = "${roles.HR_ROLE}";
	private final static String APPROVER_ROLE = "${roles.APPROVER_ROLE}";

//	private final static String USER_ROLE = "USER";
//	private final static String ADMIN_ROLE = "ADMIN";
//	private final static String HR_ROLE = "HR";
//	private final static String APPROVER_ROLE = "APPROVER";

	private EventsDatesService eventsDatesService;
	private UserRepository userRepo;
	private EventRepository eventRepo;
	private EventsDateRepository eventDateRepo;
	private ExecutorService emailExecutor = Executors.newFixedThreadPool(10);
	private JavaMailSender javaMailSender;
	
	
	

	@Autowired
	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	@Autowired
	public void setEventsDatesService(EventsDatesService eventsDatesService) {
		this.eventsDatesService = eventsDatesService;
	}

	@Autowired
	public void setEventDateRepo(EventsDateRepository eventDateRepo) {
		this.eventDateRepo = eventDateRepo;
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	public void setUserRepo(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Autowired
	public void setEventRepo(EventRepository eventRepo) {
		this.eventRepo = eventRepo;
	}

	public List<Event> getEvents() {

		return eventRepo.findAll();
	}

	public Event findByUserIdByEventId(Long userId, Long eventId) {

		return eventRepo.findByUserIdAndId(userId, eventId);
	}

	public Page<Event> findAllByUserIdOrderByStartDate(Long authUserId, Pageable pageable) {

		return eventRepo.findAllByUserIdOrderByStartDate(authUserId, pageable);
	}

	public Integer getUserSumLeave(Long userId) {

		Integer sumLeave = 0;
		List<Event> events = getUserEvents(userId);
		for (Event event : events) {
			sumLeave = sumLeave + event.getWorkDayLong();
		}
		return sumLeave;
	}

	public List<Event> getUserEvents(long id) {

		return eventRepo.findAllByUserIdOrderByStartDate(id);
	}

	public void deleteEvent(Event event) {
		eventRepo.delete(event);
	}

	public void addNewEvent(User user, Event event) {

		event.setUser(user);

		event.getStartDate().set(Calendar.HOUR_OF_DAY, 0);
		event.getStartDate().set(Calendar.MINUTE, 0);
		event.getStartDate().set(Calendar.SECOND, 0);
		event.getStartDate().set(Calendar.MILLISECOND, 0);

		// a szabadságra (event) eső munkanapok számának meghatározása

		Integer diffDay = (differentDate(event.getStartDate(), event.getEndDate())); // a szabadság tartomány hossza
																						// napokban
		Integer workDayLong = howWorkDayLong(event.getStartDate(), event.getEndDate()); // a szabadság tartományban lévő
																						// (EventDates)
		event.setDayLong(diffDay);
		event.setWorkDayLong(workDayLong);

		// ha nincs a userhez "jóváhagyó" rendelve, a szabadság kérelem jóváhagyásra
		// kerül automatikusan, egyébként 0(jóváhagyásra vár) és emailt küldünk a
		// jóváhagyónak

		User approverUser = userRepo.findFirstById(user.getApproverId());

		if (user.getApproverId() != null && user.getApproverId() >= 0) {
			event.setApproved((byte) 0);
			String messageText = "Kérelmező: " + user.getName() + " (" + user.getEmail() + ") \n Időtartam: "
					+ event.getStartDate().getTime() + " - " + event.getEndDate().getTime() + ", összesen "
					+ workDayLong + " munkanap";
			String messageSubject = "Értesítés új szabadság jóváhagyási kérésről";

			emailExecutor.execute(new EmailService(approverUser, messageSubject, messageText, javaMailSender));

		} else
			event.setApproved((byte) 1);

		saveEvent(event);

	}

	public void saveEvent(Event event) {

		Event e = eventRepo.save(event);

	}

	// összeszámoljuk az összes, már kivett szabadságok számát munkanapokban
	public Integer sumWorkDayLong(Long id) {
		List<Event> leaveEventList = getUserEvents(id);
		Integer sum = 0;
		for (Event anevent : leaveEventList) {
			Calendar sd = anevent.getStartDate();
			Calendar ed = anevent.getEndDate();
			sum += howWorkDayLong(sd, ed);
		}
		return sum;
	}

	public Integer sumLeaveDay(User authUser) {

		Integer bl = 0; // alapszabi
		if (authUser.getBaseLeave() == null)
			bl = 0;
		else
			bl = authUser.getBaseLeave();
		Integer cl = 0; // tavalyról áthozott szabi
		if (authUser.getCarriedLeave() == null)
			cl = 0;
		else
			cl = authUser.getCarriedLeave();
		Integer pl = 0; // szülői szabi
		if (authUser.getParentalLeave() == null)
			pl = 0;
		else
			pl = authUser.getParentalLeave();
		Integer ol = 0; // egyéb szabi
		if (authUser.getOtherLeave() == null)
			ol = 0;
		else
			ol = authUser.getOtherLeave();

		return bl + pl + cl + ol;

	}

	// a dátumtartományba eső munkanapok számának meghatározása
	public Integer howWorkDayLong(Calendar sDate, Calendar eDate) {

		Integer workDayLong = 0;
		List<EventDates> exDays = eventDateRepo.findAllByOrderByDate();
		Calendar startDate = Calendar.getInstance();

		startDate.setTime(sDate.getTime());
		startDate.set(Calendar.HOUR_OF_DAY, 0);	
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);

		Integer diffDay = (differentDate(startDate, eDate));

		for (int i = 0; i < diffDay; i++) {

			if (isThisWorkDay(startDate, exDays))
				workDayLong++;
			startDate.add(Calendar.DATE, 1);
		}
		return workDayLong;
	}

	public Boolean isThisWorkDay(Calendar date, List<EventDates> exDays) {
		
		Boolean isWorkDay = true;
		if ((date.get(Calendar.DAY_OF_WEEK) == 7) || (date.get(Calendar.DAY_OF_WEEK) == 1))
			isWorkDay = false; // ha szombat vagy vasárnap, akkor alapesetben nem munkanap
		for (EventDates exDay : exDays) {
			exDay.getDate().set(Calendar.HOUR_OF_DAY,0);
			date.set(Calendar.HOUR_OF_DAY,0);
			if (exDay.getDate().getTime().compareTo(date.getTime()) == 0)
				isWorkDay = exDay.getIsWorkDay(); // ha szerepel a kivételnapok közt, akkor a kivételnap típusa lesz
		}
		return isWorkDay;
	}

	public List<Long> isEventAlreadyExist(Long uid, Event event) {
		// vizsgálat: az új szabadság időszaka nem esik-e egybe egy másik már a DB-ben
		// lévővel
		List<Event> events = getUserEvents(uid);
		List<Long> ids = new ArrayList<>();
		Calendar s = event.getStartDate();
		Calendar e = event.getEndDate();

		for (Event mevent : events) {
			Calendar ms = mevent.getStartDate();
			Calendar me = mevent.getEndDate();
			Long mid = mevent.getId();
			int mss = s.compareTo(ms); // ms > s :1
			int mee = e.compareTo(me); // me > e :1
			int mes = e.compareTo(ms); // me > s :1
			int mse = s.compareTo(me); // ms > e :1
			if (((mss >= 0) && (mse <= 0)) || ((mes >= 0) && (mee <= 0)) || ((mss < 0) && (mee > 0))) {
				ids.add(mid); // dátum már létezik, átfedésben van korábbiakkal az új tartomány
			}
		}
		return ids;
	}

	public Integer differentDate(Calendar date1, Calendar date2) {
		date1.set(Calendar.HOUR_OF_DAY,0);
		date2.set(Calendar.HOUR_OF_DAY,0);
		return (int) ((date2.getTimeInMillis() - date1.getTimeInMillis()) / 1000 / 60 / 60 / 24) + 1;
	}

	public Map<Date, Integer> googleEventTable(Long authUserId) {

		Map<Date, Integer> eventMap = new HashMap<>();
		Calendar startDate = Calendar.getInstance();
		List<EventDates> exDates = eventsDatesService.getAllEvents();

		// hétvégék és kivételnapok a táblába
		LocalDate now = LocalDate.now();
		startDate.set(Calendar.YEAR, now.getYear());
		startDate.set(Calendar.MONTH, 0);
		startDate.set(Calendar.DAY_OF_MONTH, 0);
		startDate.set(Calendar.HOUR_OF_DAY,0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		do {
			if (!isThisWorkDay(startDate, exDates)) {
				eventMap.put(startDate.getTime(), 3);
			}
			startDate.add(Calendar.DATE, 1);
		} while (now.getYear() == startDate.get(Calendar.YEAR));

		// szabadságok táblába
		List<Event> leaveEventList = getUserEvents(authUserId);
		for (Event anevent : leaveEventList) {
			startDate.setTime(anevent.getStartDate().getTime());
			startDate.set(Calendar.HOUR_OF_DAY, 0);
			startDate.set(Calendar.MINUTE, 0);
			startDate.set(Calendar.SECOND, 0);
			startDate.set(Calendar.MILLISECOND, 0);
			Integer diffDay = (differentDate(anevent.getStartDate(), anevent.getEndDate()));
			for (Integer i = 0; i < diffDay; i++) {
				if (isThisWorkDay(startDate, exDates)) // ha az adott szabadságnap munkanap, akkor táblába
					eventMap.put(startDate.getTime(), -7); // az adott dátumra eső szabadság (1-egész nap, 0.5 -fél nap)
				startDate.add(Calendar.DATE, 1);
			}
		}

		return eventMap;
	}

	public AppPojoList findMyApprovList(Long authUserId) {

		AppPojoList approveList = new AppPojoList();
		// kikeressük azokat a Usereket, akinek az authUser a jóváhagyója
		List<User> userList = userRepo.findAllByApproverId(authUserId);

		userList.forEach(user -> {
			List<Event> events = user.getEvents(); // lekérjük a szabadságok listáját
			// List<Event> events= eventRepo.findByUserIdAndApprovedEquals(authUserId,0); //
			// szűrt Lista is lehet, de egyelőre lekérem mindet

			events.forEach(event -> {
				ApprovePojo aPojo = new ApprovePojo();
				if (event.getApproved() == null)
					event.setApproved((byte) 0);
				if (event.getApproved() == 0) { // ha jóváhagyásra vár, akkor listába vele
					aPojo.setUserId(user.getId());
					aPojo.setEventId(event.getId());
					aPojo.setUserName(user.getName());
					aPojo.setUserEmail(user.getEmail());
					aPojo.setStartDate(event.getStartDate());
					aPojo.setEndDate(event.getEndDate());
					aPojo.setWorkDayLong(event.getWorkDayLong());
					aPojo.setApproved(event.getApproved());
					approveList.add(aPojo);
				}
			});
		});
		return approveList;
	}

//	@PostConstruct
	public void init() throws ParseException {

		Event event;
		// -----------------------------------------------
		User user = new User("Mezei Csaba", "mezeicsaba72@gmail.com", passwordEncoder.encode("pass"), ADMIN_ROLE, true,
				30);
		user.setStatus(true);
		userRepo.save(user);

		Calendar date1 = new GregorianCalendar(2021, 00, 01);
		Calendar date2 = new GregorianCalendar(2021, 00, 10);
		Integer workd = howWorkDayLong(date1, date2);

		event = new Event(date1, date2, workd, differentDate(date1, date2), user);
		eventRepo.save(event);

		date1 = new GregorianCalendar(2021, 00, 14);
		date2 = new GregorianCalendar(2021, 00, 14);
		workd = howWorkDayLong(date1, date2);
		event = new Event(date1, date2, workd, differentDate(date1, date2), user);
		eventRepo.save(event);

		// -----------------------------------------------

		user = new User("Nagy Endre", "mezeicsaba72+nagyendre@gmail.com", passwordEncoder.encode("pass"), USER_ROLE,
				true, 22);
		user.setStatus(true);
		userRepo.save(user);

		date1 = new GregorianCalendar(2021, 00, 06);
		date2 = new GregorianCalendar(2021, 00, 10);
		workd = howWorkDayLong(date1, date2);

		event = new Event(date1, date2, workd, differentDate(date1, date2), user);
		eventRepo.save(event);

		date1 = new GregorianCalendar(2021, 00, 25);
		date2 = new GregorianCalendar(2021, 00, 29);
		workd = howWorkDayLong(date1, date2);
		event = new Event(date1, date2, workd, differentDate(date1, date2), user);
		eventRepo.save(event);

		// -----------------------------------------------

		user = new User("Kiss Irén", "mezeicsaba72+kissiren@gmail.com", passwordEncoder.encode("pass"), HR_ROLE, true,
				24);
		user.setStatus(true);
		userRepo.save(user);

		date1 = new GregorianCalendar(2021, 00, 11);
		date2 = new GregorianCalendar(2021, 00, 20);
		workd = howWorkDayLong(date1, date2);

		event = new Event(date1, date2, workd, differentDate(date1, date2), user);
		eventRepo.save(event);

		date1 = new GregorianCalendar(2021, 00, 22);
		date2 = new GregorianCalendar(2021, 00, 24);
		workd = howWorkDayLong(date1, date2);
		event = new Event(date1, date2, workd, differentDate(date1, date2), user);
		eventRepo.save(event);

		// -----------------------------------------------

		user = new User("Nagy Dávid", "mezeicsaba72+nagydavid@gmail.com", passwordEncoder.encode("pass"), APPROVER_ROLE,
				true, 26);
		user.setStatus(true);
		userRepo.save(user);

		date1 = new GregorianCalendar(2021, 00, 11);
		date2 = new GregorianCalendar(2021, 00, 20);
		workd = howWorkDayLong(date1, date2);

		event = new Event(date1, date2, workd, differentDate(date1, date2), user);
		eventRepo.save(event);

		date1 = new GregorianCalendar(2021, 00, 22);
		date2 = new GregorianCalendar(2021, 00, 24);
		workd = howWorkDayLong(date1, date2);
		event = new Event(date1, date2, workd, differentDate(date1, date2), user);
		eventRepo.save(event);

		// -----------------------------------------------

	}

}
