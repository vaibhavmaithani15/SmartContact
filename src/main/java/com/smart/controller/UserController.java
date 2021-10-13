package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.MyOrder;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private MyOrderRepository myOrderRepository;
	
	//method for adding common data to handler
	@ModelAttribute
	public void addCommonData(Model m,Principal principal) {
		
		String userName=principal.getName();
		User user=userRepository.getUserByUserName(userName);
		m.addAttribute("user",user);
	}
	
	
	//dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		
	
		// get the user from username
		
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact( @ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file, Principal principal,HttpSession session) {
		try {
		String name=principal.getName();
		
		
		
		//processing and uploading file
		if(file.isEmpty()) {
			//if the file is empty		
			System.out.println("File is empty");
			contact.setImage("contact.png");
		}else {
				
				
			contact.setImage(file.getOriginalFilename());
			File saveFile =new ClassPathResource("static/img").getFile();
			Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Image is uploaded");
		}
		
		
		User user=this.userRepository.getUserByUserName(name);
		contact.setUser(user);
		user.getContacts().add(contact);
		
		this.userRepository.save(user);
		System.out.println("Added to data base");
		//message sucessful
		session.setAttribute("message", new Message("Contact Added Successfully", "success"));
	
		
		System.out.println(contact);
		}catch (Exception e) {
			
			//error message
			session.setAttribute("message", new Message("Something Went Wrong Try Again", "danger"));
			
				System.out.println("Erro "+e.getMessage());
		}
		
		
		return "normal/add_contact_form";
	}
	
	
	//show contacts handler
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
		
		
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		m.addAttribute("title", "Show User Contacts");
		Pageable pageable = PageRequest.of(page, 10);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
		
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	} 
	
	// Showing particular contact detail
	@GetMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		System.out.println("CID " + cId);
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("title", contact.getName());
			model.addAttribute("contact", contact);
		}

		return "normal/contact_detail";
	}
	
	// delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session,
			Principal principal) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {
			contact.setUser(null);
			this.contactRepository.delete(contact);
			session.setAttribute("message", new Message("Contact deleted successfully", "success"));
			String file = contact.getImage();
			if (!file.equals("contact.png")) {
				// delete old photo
				File deleteFile = null;
				try {
					deleteFile = new ClassPathResource("static/img").getFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				File file1 = new File(deleteFile, contact.getImage());
				file1.delete();
			}
			// return "redirect:/user/show-contacts/0";
		}

		return "redirect:/user/show-contacts/0";
	}

	// open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model m) {
		m.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepository.findById(cid).get();
		m.addAttribute("contact", contact);
		return "normal/update_form";
	}

	// update contact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model m, HttpSession session, Principal principal) {
		try {
//			old contact detail
			Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();

			// image
			if (!file.isEmpty()) {
				// delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldcontactDetail.getImage());
				file1.delete();

				// new file update to save db
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				System.out.println("Image is uploaded");
			} else {
				contact.setImage(oldcontactDetail.getImage());
			}
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			session.setAttribute("message", new Message("Your contact is updated...", "success"));
			this.contactRepository.save(contact);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		System.out.println("CONTACT " + contact.getName());
		return "redirect:/user/" + contact.getcId() + "/contact";
	}

	// Your Profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}

	// open setting handler
	@GetMapping("/settings")
	public String openSettings(Model model) {
		model.addAttribute("title", "Settings");
		return "normal/settings";
	}

	// change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
		String userName = principal.getName();
		User currentUser = userRepository.getUserByUserName(userName);
		System.out.println("ENCRYPT PASSWORD " + currentUser.getPassword());
		System.out.println("OLD PASSWORD " + oldPassword);
		System.out.println("NEW PASSWORD " + newPassword);
		if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			// Change password
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("Password change successfully", "success"));
		} else {
			session.setAttribute("message", new Message("Old Password Wrong", "danger"));
			return "redirect:/user/settings";
		}

		return "redirect:/user/settings";
	}

	// creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data, Principal principal) throws Exception {
		// System.out.println("Hey order done successfully");
		System.out.println(data);
		int amt = Integer.parseInt(data.get("amount").toString());
		System.out.println("AMOUNT PRINTING " + amt);

		RazorpayClient razorpayClient = new RazorpayClient("rzp_test_4j9iMus0GGWqYS", "VsKionvpMRsNE0cdX1hHTH7t");
		JSONObject ob = new JSONObject();
		ob.put("amount", amt * 100);
		ob.put("currency", "INR");
		ob.put("receipt", "txn_235432");

		// creating new order
		Order order = razorpayClient.Orders.create(ob);

		// save the order into the database
		MyOrder myOrder = new MyOrder();
		int a = Integer.parseInt(data.get("amount").toString());

		myOrder.setAmount(a);
		myOrder.setOrderId(order.get("id"));
		myOrder.setPaymentId(null);
		myOrder.setStatus("created");
		myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
		myOrder.setReceipt(order.get("receipt"));

		this.myOrderRepository.save(myOrder);
		// if you want to save order details to database
		System.out.println(order);
		return order.toString();
	}

	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data){
		MyOrder myOrder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());
		myOrder.setPaymentId(data.get("payment_id").toString());
		myOrder.setStatus(data.get("status").toString());
		this.myOrderRepository.save(myOrder);
		System.out.println(data);
		return ResponseEntity.ok(Map.of("msg", "Updated.."));
	}

}
