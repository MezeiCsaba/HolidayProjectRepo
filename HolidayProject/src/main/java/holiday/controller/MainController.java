package holiday.controller;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import holiday.entity.User;
import holiday.services.EmailService;
import holiday.services.EventService;
import holiday.services.UserService;

@Controller
public class MainController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private EventService eventService;
	private UserService userService;

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping("/")
	public String index(Authentication authentication, Model model) {

//		szabadság fánk :) 

		User authUser = userService.findByEmail(authentication.getName());
		Integer userLeaves[] = new Integer[2]; // kivett és összes szabadság napokban
		Arrays.fill(userLeaves, 0);

		userLeaves[0] = eventService.sumWorkDayLong(authUser.getId());
		userLeaves[1] = eventService.sumLeaveDay(authUser);

		String approverName = " nincs";
		if (authUser.getApproverId() != null) {
			User approver = userService.findById(authUser.getApproverId());
			approverName = approver.getName() + " (" + approver.getEmail() + ")"; // jóváhagyó személye és emil címe
		}

		model.addAttribute("approverName", approverName);
		model.addAttribute("user", authUser);
		model.addAttribute("userLeave", userLeaves);
		model.addAttribute("holidayEvents", eventService.googleEventTable(authUser.getId()));

		return "index";
	}

}
