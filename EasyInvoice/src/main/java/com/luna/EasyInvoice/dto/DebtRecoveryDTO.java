package com.luna.EasyInvoice.dto;

import java.time.LocalDate;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DebtRecoveryDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String code;

    private LocalDate creationDate;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String invoiceRef;

    private Date recoveryDate;

    private Integer status;
    
    private double amount;
    
    private double totalamount;
    
    private Date nextAppointment;

    private int type;

    private Long invoice;

}
