package com.luna.EasyInvoice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class DebtRecovery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column
    private LocalDate creationDate;

    @Column
    private String createdBy;

    @Column
    private String invoiceRef;

    @Column
    private Date recoveryDate;

    @Column
    private Integer status;
    
    @Column
    private double amount;
    
    @Column
    private double totalamount;
    
    private Date nextAppointment;
    
    @Column
    private int type;
    
    @OneToMany(mappedBy = "debt")
    private List<Payment> payments;
    
    @OneToMany(mappedBy = "debt")
    private List<Action> actions;

    @OneToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @CreatedDate
    @Column(nullable = true, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = true)
    private OffsetDateTime lastUpdated;

}
