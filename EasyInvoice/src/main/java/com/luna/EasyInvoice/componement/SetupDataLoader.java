package com.luna.EasyInvoice.componement;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.luna.EasyInvoice.entities.Privilege;
import com.luna.EasyInvoice.entities.Role;
import com.luna.EasyInvoice.entities.User;
import com.luna.EasyInvoice.repository.PrivilegeRepository;
import com.luna.EasyInvoice.repository.RoleRepository;
import com.luna.EasyInvoice.repository.UserRepository;

@Component
public class SetupDataLoader implements  ApplicationListener<ContextRefreshedEvent> {
 
    boolean alreadySetup = false;
 
    @Autowired
    private UserRepository userRepository;
 
    @Autowired
    private RoleRepository roleRepository;
 
    @Autowired
    private PrivilegeRepository privilegeRepository;
 
    @Autowired
    private PasswordEncoder passwordEncoder;
 
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
 
        if (alreadySetup)
            return;
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        Privilege updatPrivilege = createPrivilegeIfNotFound("UPDATE_PRIVILEGE") ;
        Privilege deletePrivilege = createPrivilegeIfNotFound("DELETE_PRIVILEGE") ;
        Privilege insertPrivilege = createPrivilegeIfNotFound("INSERT_PRIVILEGE") ;
        
        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege,updatPrivilege,deletePrivilege);        
        
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
       // createRoleIfNotFound("ROLE_MANAGER", Arrays.asList(insertPrivilege, readPrivilege));
        createRoleIfNotFound("ROLE_COM_MANAGER", adminPrivileges);
        
        createRoleIfNotFound("ROLE_COM_HEAD_ADMIN",Arrays.asList(readPrivilege,insertPrivilege));
        createRoleIfNotFound("ROLE_COM_LOG_OFF",Arrays.asList(readPrivilege,writePrivilege,deletePrivilege,insertPrivilege));
        createRoleIfNotFound("ROLE_COM_HR_OFF",Arrays.asList(readPrivilege,insertPrivilege,updatPrivilege));
        createRoleIfNotFound("ROLE_COM_HEAD_FINA",Arrays.asList(readPrivilege,writePrivilege));
        createRoleIfNotFound("ROLE_COM_CASH",Arrays.asList(readPrivilege,writePrivilege));
        
        createRoleIfNotFound("ROLE_COM_ACOUNTANT", Arrays.asList(readPrivilege));
        
        
//        createRoleIfNotFound("ROLE_CLIENT", Arrays.asList(readPrivilege,writePrivilege,updatPrivilege));
//        createRoleIfNotFound("ROLE_STOCK_MANAGER",Arrays.asList(readPrivilege,writePrivilege,updatPrivilege));
 
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        
        if(!createUserIfNotFound("admin")) {
        	User user = new User();
            user.setFirstName("Default admin");
            user.setLastName("Default admin");
            user.setEmail("ndayegide2003@gmail.com");
            user.setOrganisationName("Luna MicroSystem");
            user.setTokenExpired(false);
            user.setTelephone("79983653");
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRoles(Arrays.asList(adminRole));
            user.setEnabled(true);
            user.setStatus(1);
            userRepository.save(user);
        }
        alreadySetup = true;
    }
 
    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {
 
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }
    
    
    @Transactional
    boolean createUserIfNotFound(String username) {
 
        User us = userRepository.findByUsername(username);
        if (us == null) {
        	return false;
        }else {
        	return true;
        }
    }
    
 
    @Transactional
    Role createRoleIfNotFound(
      String name, Collection<Privilege> privileges) {
 
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
}
