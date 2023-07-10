package com.example.employee.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.employee.dto.AdminDTO;

@Service
public interface AdminService {

	String addAdmin(AdminDTO adminDto);

	List<AdminDTO> getAllAdmins();

	AdminDTO getAdminById(long adminId);

	String updateAdminDetails(long adminId, AdminDTO adminDto);

	String deleteAdminById(long adminId);

}
