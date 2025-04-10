package com.luna.EasyInvoice.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@Table(name = "user_tbl",
uniqueConstraints = @UniqueConstraint(columnNames = { "username"}))
public class User implements Serializable{
	private static final long serialVersionUID = -3776952859432394996L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String username;
	private int status;
	private String password;
	private String firstName;
    private String lastName;
	private String organisation;
	private String organisationName;
	private String telephone;
	private String email;
	private boolean enabled;
    private boolean tokenExpired;
    @CreatedBy
    private String createdBy;
    @CreatedDate
    private Date createdOn;

    @ManyToMany(fetch = FetchType.EAGER) 
    @JoinTable( 
        name = "users_roles", joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")) 
    private Collection<Role> roles;

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", status=" + status + ", password=" + password
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", organisation=" + organisation
				+ ", telephone=" + telephone + ", email=" + email + ", enabled=" + enabled + ", tokenExpired="
				+ tokenExpired + ", createdBy=" + createdBy + ", createdOn=" + createdOn + "]";
	}
}
