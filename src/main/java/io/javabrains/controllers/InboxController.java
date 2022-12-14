package io.javabrains.controllers;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.javabrains.emaillist.EmailListItem;
import io.javabrains.emaillist.EmailListItemRepository;
import io.javabrains.folders.Folder;
import io.javabrains.folders.FolderRepository;
import io.javabrains.folders.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Controller
public class InboxController {
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private EmailListItemRepository emailListItemRepository;
    @Autowired
    private FolderService folderService;
    @GetMapping(value = "/")
    public String homePage(
            @RequestParam(required = false) String folder,
            @AuthenticationPrincipal OAuth2User principal,
            Model model) {

        if ((principal == null) || !StringUtils.hasText(principal.getAttribute("login"))){
            return "index";
        }
        //fetch folder
        String userId = principal.getAttribute("login");
        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);
        List<Folder> defaultFolders = folderService.fetchDefaultFolder(userId);
        model.addAttribute("defaultFolders", defaultFolders);

        // fetch messages
        if(!StringUtils.hasText(folder)) {
            folder = "Inbox";
        }
        List<EmailListItem> emailList = emailListItemRepository.findAllByKey_IdAndKey_Label(userId,folder);
        emailList.stream().forEach(emailItem -> {
            UUID timeUuid = emailItem.getKey().getTimeUUID();
            Date emailDateTime = new Date(Uuids.unixTimestamp(timeUuid));
//            emailItem.setUnread();
        } );
        model.addAttribute("emailList", emailList);
        model.addAttribute("folderName", folder);

        return "inbox-page";
    }
}
