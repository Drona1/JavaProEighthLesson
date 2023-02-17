package com.gmail.dimabah.javaproeighthlesson;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DBService {
    private final ContactRepository contactRepository;
    private final GroupRepository groupRepository;

    public DBService(ContactRepository contactRepository, GroupRepository groupRepository) {
        this.contactRepository = contactRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public void addContact(Contact contact) {
        contactRepository.save(contact);
    }

    @Transactional
    public void addGroup(Group group) {
        groupRepository.save(group);
    }
    @Transactional
    public void deleteContacts(long[] idList) {
        for (var i : idList) {
            contactRepository.deleteById(i);
        }
    }
    @Transactional
    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }
    @Transactional (readOnly = true)
    public List<Group> findGroups(){
        return groupRepository.findAll();
    }

    @Transactional (readOnly = true)
    public List<Contact> findAll(Pageable pageable){
        return contactRepository.findAll(pageable).getContent();
    }
    @Transactional(readOnly = true)
    public List<Contact> findByPattern (String pattern, Pageable pageable){
        return  contactRepository.findByPattern(pattern,pageable);
    }
    @Transactional(readOnly = true)
    public List<Contact> findByGroup (Group group, Pageable pageable){
        return contactRepository.findByGroup(group,pageable);
    }
    @Transactional(readOnly=true)
    public Group findGroup(long id) {
        return groupRepository.findById(id).get();
    }
    @Transactional(readOnly = true)
    public long countByGroup (Group group){
        return contactRepository.countByGroup(group);
    }
    @Transactional(readOnly = true)
    public  long count (){
        return contactRepository.count();
    }
    @Transactional
    public void reset (){
        groupRepository.deleteAll();
        contactRepository.deleteAll();

        Group group = new Group("Test");

        Contact contact;
        addGroup(group);

        for (int i = 0; i < 13; i++) {
            contact = new Contact("Name" + i, "Surname" + i, "1234567" + i, "user" + i + "@default.com");
            addContact(contact);
        }
        for (int i = 0; i < 10; i++) {
            contact = new Contact("Other" + i, "OtherSurname" + i, "7654321" + i, "user" + i + "@other.com");
            group.addContact(contact);
        }
    }


}
