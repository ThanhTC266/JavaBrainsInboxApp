package io.javabrains.controllers;

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

import java.util.List;


@Controller
public class InboxController {
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private EmailListItemRepository emailListItemRepository;
    @Autowired
    private FolderService folderService;
    @GetMapping(value = "/")
    public String homePage(@AuthenticationPrincipal OAuth2User principal
    , Model model) {

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
        String folderlabel = "Inbox";
        List<EmailListItem> emailList = emailListItemRepository.findAllByKey_IdAndKey_Label(userId,folderlabel);
        model.addAttribute("emailList", emailList);
        return "inbox-page";
    }
}
