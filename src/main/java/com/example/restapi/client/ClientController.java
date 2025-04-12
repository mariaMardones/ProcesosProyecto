package es.deusto.sd.auctions.client.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.deusto.sd.auctions.client.proxies.IAuctionsServiceProxy;
import es.deusto.sd.auctions.client.data.Article;
import es.deusto.sd.auctions.client.data.Category;
import es.deusto.sd.auctions.client.data.Credentials;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ClientController {

	@Autowired
	private IAuctionsServiceProxy auctionsServiceProxy;

	private String token;

	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		String currentUrl = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();
		model.addAttribute("currentUrl", currentUrl);
		model.addAttribute("token", token);
	}

	@GetMapping("/")
	public String home(Model model) {
		List<Category> categories;

		try {
			categories = auctionsServiceProxy.getAllCategories();
			model.addAttribute("categories", categories);
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Failed to load categories: " + e.getMessage());
		}

		return "index";
	}

	@GetMapping("/login")
	public String showLoginPage(@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		model.addAttribute("redirectUrl", redirectUrl);

		return "login";
	}

	@PostMapping("/login")
	public String performLogin(@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl, Model model) {
		Credentials credentials = new Credentials(email, password);

		try {
			token = auctionsServiceProxy.login(credentials);

			return "redirect:" + (redirectUrl != null && !redirectUrl.isEmpty() ? redirectUrl : "/");
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Login failed: " + e.getMessage());
			return "login";
		}
	}

	@GetMapping("/logout")
	public String performLogout(@RequestParam(value = "redirectUrl", defaultValue = "/") String redirectUrl,
			Model model) {
		try {
			auctionsServiceProxy.logout(token);
			token = null;
			model.addAttribute("successMessage", "Logout successful.");
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Logout failed: " + e.getMessage());
		}

		return "redirect:" + redirectUrl;
	}

	@GetMapping("/category/{name}")
	public String getCategoryArticles(@PathVariable("name") String name,
			@RequestParam(value = "currency", defaultValue = "EUR") String currency, Model model) {
		List<Article> articles;

		try {
			articles = auctionsServiceProxy.getArticlesByCategory(name, currency);
			model.addAttribute("articles", articles);
			model.addAttribute("categoryName", name);
			model.addAttribute("selectedCurrency", currency);
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Failed to load articles for category: " + e.getMessage());
			model.addAttribute("articles", null);
			model.addAttribute("categoryName", name);
			model.addAttribute("selectedCurrency", "EUR");
		}

		return "category";
	}

	@GetMapping("/article/{id}")
	public String getArticleDetails(@PathVariable("id") Long id,
			@RequestParam(value = "currency", defaultValue = "EUR") String currency, Model model) {
		Article article;

		try {
			article = auctionsServiceProxy.getArticleDetails(id, currency);
			model.addAttribute("article", article);
			model.addAttribute("selectedCurrency", currency);
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Failed to load article details: " + e.getMessage());
			model.addAttribute("article", null);
			model.addAttribute("selectedCurrency", "EUR");
		}

		return "article";
	}

	@PostMapping("/bid")
	public String makeBid(@RequestParam("id") Long id, @RequestParam("amount") Float amount,
			@RequestParam(value = "currency", defaultValue = "EUR") String currency, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			auctionsServiceProxy.makeBid(id, amount, currency, token);
			redirectAttributes.addFlashAttribute("successMessage", "Bid placed successfully!");
		} catch (RuntimeException e) {
			// Add an error message to be displayed in the article view
			redirectAttributes.addFlashAttribute("errorMessage", "Failed to place bid: " + e.getMessage());
		}

		return "redirect:/article/" + id + "?currency=" + currency;
	}
}