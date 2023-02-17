package com.gmail.dimabah.javaproeighthlesson;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Controller

public class MyController {
    static final int DEFAULT_GROUP_ID = -1;
    static final int ITEMS_PER_PAGE = 6;
    private final DBService dbService;

    public MyController(DBService dbService) {
        this.dbService = dbService;
    }

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(required = false, defaultValue = "0") Integer page) {
        if (page < 0) {
            page = 0;
        }
        List<Contact> contacts = dbService.
                findAll(PageRequest.of(page, ITEMS_PER_PAGE, Sort.Direction.DESC, "id"));
        model.addAttribute("groups", dbService.findGroups());
        model.addAttribute("contacts", contacts);
        model.addAttribute("allPages", new Long[getPageCount()]);

        return "index";
    }

    @GetMapping("/reset")
    public String reset(){
        dbService.reset();
        return "redirect:/";
    }

    @PostMapping(value = "/search")
    public String search(@RequestParam String pattern, Model model) {
        model.addAttribute("groups", dbService.findGroups());
        model.addAttribute("contacts", dbService.findByPattern(pattern, null));

        return "index";
    }
    @GetMapping("/group/{id}")
    public String listGroup(
            @PathVariable(value = "id") long groupId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            Model model)
    {
        Group group = (groupId != DEFAULT_GROUP_ID) ? dbService.findGroup(groupId) : null;
        if (page < 0) page = 0;

        List<Contact> contacts = dbService
                .findByGroup(group, PageRequest.of(page, ITEMS_PER_PAGE, Sort.Direction.DESC, "id"));

        model.addAttribute("groups", dbService.findGroups());
        model.addAttribute("contacts", contacts);
        model.addAttribute("byGroupPages", new Long[getPageCount(group)]);
        model.addAttribute("groupId", groupId);

        return "index";
    }

    @PostMapping(value = "/contact/delete")
    public ResponseEntity<Void> delete(@RequestParam(value = "toDelete[]", required = false) long[] toDelete) {
        if (toDelete != null && toDelete.length > 0)
            dbService.deleteContacts(toDelete);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping(value = "/group/delete")
    public String deleteGroup(@RequestParam Long groupId) {
        if (groupId!=null) {
            dbService.deleteGroup(groupId);
        }
        return "redirect:/";
    }
    @GetMapping("/contact_add_page")
    public String contactAddPage(Model model) {
        model.addAttribute("groups", dbService.findGroups());
        return "contact_add_page";
    }
    @GetMapping("/group_add_page")
    public String groupAddPage() {
        return "group_add_page";
    }
    @PostMapping(value="/contact/add")
        public String contactAdd(@RequestParam(value = "group") long groupId,
                             @RequestParam String name,
                             @RequestParam String surname,
                             @RequestParam String phone,
                             @RequestParam String email)
    {
        Group group = (groupId != DEFAULT_GROUP_ID) ? dbService.findGroup(groupId) : null;
        Contact contact = new Contact(name, surname, phone, email);

        if (group!=null){
            group.addContact(contact);
        }
        dbService.addContact(contact);
        return "redirect:/";
    }
    @PostMapping(value="/group/add")
    public String groupAdd(@RequestParam String name) {
        dbService.addGroup(new Group(name));
        return "redirect:/";
    }
    @GetMapping("/load_contacts_from_json_page")
    public String importContactsPage(Model model) {
        model.addAttribute("groups", dbService.findGroups());
        return "load_contacts_from_json_page";
    }
    @PostMapping ("/contact/import")
    public String importContacts( @RequestParam(value = "group") long groupId,
                                  @RequestParam ("file") MultipartFile file) {
        try {
            Gson gson = new Gson();
            JsonReader jsonReader = new JsonReader(new InputStreamReader(file.getInputStream()));
            Contact[] contacts = gson.fromJson(jsonReader,Contact[].class);
            Group group = (groupId != DEFAULT_GROUP_ID) ? dbService.findGroup(groupId) : null;
            for (var i: contacts  ) {
                if (group!=null){
                    group.addContact(i);
                }
                dbService.addContact(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }

    private int getPageCount() {
        long totalCount = dbService.count();
        return (int) (totalCount / ITEMS_PER_PAGE) + ((totalCount % ITEMS_PER_PAGE > 0) ? 1 : 0);
    }
    private int getPageCount(Group group) {
        long totalCount = dbService.countByGroup(group);
        return (int)(totalCount / ITEMS_PER_PAGE) + ((totalCount % ITEMS_PER_PAGE > 0) ? 1 : 0);
    }




}
