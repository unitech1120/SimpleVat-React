package com.simplevat.entity;

import com.simplevat.entity.Company;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by mohsinh on 2/26/2017.
 */
@Entity
@Table(name = "COMPANY_TYPE")
@Data
public class CompanyType {
    @Id
    @Column(name = "COMPANY_TYPE_CODE")
    private int companyTypeCode;
    @Basic
    @Column(name = "COMPANY_TYPE_NAME")
    private String companyTypeName;
    @Basic
    @Column(name = "COMPANY_TYPE_DESCRIPTION")
    private String companyTypeDescription;
}