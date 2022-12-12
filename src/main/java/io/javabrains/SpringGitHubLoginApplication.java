package io.javabrains;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.javabrains.emaillist.EmailListItem;
import io.javabrains.emaillist.EmailListItemKey;
import io.javabrains.emaillist.EmailListItemRepository;
import io.javabrains.folders.FolderRepository;
import io.javabrains.folders.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.Arrays;

@SpringBootApplication
@RestController
public class SpringGitHubLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGitHubLoginApplication.class, args);
	}

	@Autowired
	private FolderRepository folderRepository;
	@Autowired
	private EmailListItemRepository emailListItemRepository;
	/**
	 *
	 * This is necessary to have the Spring Boot app use the Astra secure bundle
	 * to connect to the database
	 */

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

	@RequestMapping("/user")
	public String user(@AuthenticationPrincipal OAuth2User principal) {
		System.out.println(principal);
		return principal.getAttribute("name");
	}

	@PostConstruct
	public void init() {
		folderRepository.save(new Folder("ThanhTC266","Inbox", "blue"));
		folderRepository.save(new Folder("ThanhTC266","Sent", "green"));
		folderRepository.save(new Folder("ThanhTC266","Important", "yellow"));

		for(int i=0; i< 10; i++){
			EmailListItemKey key = new EmailListItemKey();
			key.setId("ThanhTC266");
			key.setLabel("Inbox");
			key.setTimeUUID(Uuids.timeBased());

			EmailListItem item = new EmailListItem();
			item.setKey(key);
			item.setTo(Arrays.asList("ThanhTC266"));
			item.setSubject("Subject "+ i);
			item.setUnread(true);

			emailListItemRepository.save(item);
		}
	}
}
